package cl.mass.visitas
import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import cl.mass.visitas.data.ChecklistQuestion
import cl.mass.visitas.data.StoreVisit
import java.io.File
import java.io.FileOutputStream

object MemoPdfExporter {
 private const val W=595; private const val H=842; private const val M=54f
 fun create(context:Context, visit:StoreVisit, questions:List<ChecklistQuestion>):File {
  val doc=PdfDocument(); var pageNo=0; var page:PdfDocument.Page?=null; var c:Canvas?=null; var y=0f
  val body=Paint(1).apply{color=Color.rgb(25,25,25);textSize=10.5f;typeface=Typeface.create(Typeface.SANS_SERIF,Typeface.NORMAL)}
  val bold=Paint(body).apply{typeface=Typeface.create(Typeface.SANS_SERIF,Typeface.BOLD)}
  val blue=Color.rgb(18,59,115); val yellow=Color.rgb(243,196,0)
  fun newPage(title:Boolean=false){page?.let{doc.finishPage(it)};pageNo++;page=doc.startPage(PdfDocument.PageInfo.Builder(W,H,pageNo).create());c=page!!.canvas;y=M
   if(title){val p=Paint(bold).apply{color=blue;textSize=20f};c!!.drawText("MEMORÁNDUM DE VISITA A TIENDA",M,y,p);y+=24;c!!.drawLine(M,y,W-M,y,Paint().apply{color=yellow;strokeWidth=4f});y+=20}}
  fun ensure(space:Float){if(y+space>H-M)newPage()}
  fun wrapped(text:String,paint:Paint=body,indent:Float=0f,gap:Float=14f){val max=W-2*M-indent;var line=""
   text.ifBlank{"—"}.replace("\n"," \n ").split(" ").forEach{word->
    if(word=="\n"){if(line.isNotBlank()){ensure(gap);c!!.drawText(line,M+indent,y,paint);y+=gap;line=""}}
    else{val test=if(line.isEmpty())word else "$line $word";if(paint.measureText(test)>max&&line.isNotEmpty()){ensure(gap);c!!.drawText(line,M+indent,y,paint);y+=gap;line=word}else line=test}}
   if(line.isNotEmpty()){ensure(gap);c!!.drawText(line,M+indent,y,paint);y+=gap}}
  fun heading(t:String){ensure(30f);y+=8;wrapped(t,Paint(bold).apply{color=blue;textSize=12f},gap=16f);c!!.drawLine(M,y-8,W-M,y-8,Paint().apply{color=Color.LTGRAY})}
  fun field(l:String,v:String){ensure(18f);c!!.drawText("$l:",M,y,bold);wrapped(v,body,110f)}
  newPage(true)
  field("Jefe de Ventas",visit.salesLead);field("Supervisor Zonal",visit.zonalSupervisor);field("Tienda",visit.store);field("Administrador",visit.administrator);field("Fecha",visit.date)
  heading("Indicadores del periodo");wrapped("Venta del periodo (S/): ${visit.salesActual}    |    Merma del periodo (%): ${visit.shrinkPercent}    |    Antigüedad (meses): ${visit.ageMonths}")
  val active=questions.filter{it.active}
  active.groupBy{it.section}.forEach{(section,qs)->heading(section);qs.forEach{q->visit.answers[q.id]?.let{a->if(a.status!="N/A"){wrapped("Estado: ${a.status}",bold);wrapped(q.text);if(a.observation.isNotBlank())wrapped(a.observation);if(a.correctiveAction.isNotBlank())wrapped("Acción / oportunidad de mejora: ${a.correctiveAction}");if(a.responsible.isNotBlank())wrapped("Responsable: ${a.responsible}    Compromiso: ${a.dueDate}");y+=4}}}}
  if(visit.stockBreaks.isNotEmpty()){heading("Quiebres de mercadería");visit.stockBreaks.forEachIndexed{i,b->wrapped("${i+1}. ${b.description}  |  Código: ${b.code.ifBlank{"—"}}  |  Estado: ${b.status}")}}
  heading("Cuaderno de visitas");wrapped("Supervisor registrado: ${visit.visitBookStatus.ifBlank{"—"}}");if(visit.visitBookObservation.isNotBlank())wrapped(visit.visitBookObservation)
  heading("Resumen / compromisos");wrapped(visit.summaryCommitments)
  val photos=active.mapNotNull{q->visit.answers[q.id]?.let{a->if(a.photoUri.isNotBlank())Triple(q,a,a.photoUri)else null}}
  if(photos.isNotEmpty()){newPage();c!!.drawText("ANEXOS FOTOGRÁFICOS",M,y,Paint(bold).apply{color=blue;textSize=18f});y+=28
   photos.forEachIndexed{index,(q,a,u)->loadBitmap(context,u)?.let{bmp->val maxW=W-2*M;val scale=minOf(maxW/bmp.width.toFloat(),430f/bmp.height.toFloat(),1f);val dw=bmp.width*scale;val dh=bmp.height*scale;ensure(dh+70);c!!.drawBitmap(bmp,null,RectF(M,y,M+dw,y+dh),Paint(1));y+=dh+14;wrapped("Figura ${index+1}. ${q.section} — ${a.observation.ifBlank{q.text}}",Paint(body).apply{typeface=Typeface.create(Typeface.SANS_SERIF,Typeface.ITALIC)});y+=16;bmp.recycle()}}}
  page?.let{doc.finishPage(it)};val dir=File(context.cacheDir,"memos").apply{mkdirs()};val safe=visit.store.replace(Regex("[^A-Za-z0-9_-]"),"_").take(40);val f=File(dir,"Memo_${safe}_${visit.date}.pdf");FileOutputStream(f).use{doc.writeTo(it)};doc.close();return f
 }
 private fun loadBitmap(context:Context,value:String):Bitmap?=runCatching{if(value.startsWith("content://"))context.contentResolver.openInputStream(Uri.parse(value))?.use{BitmapFactory.decodeStream(it)}else BitmapFactory.decodeFile(value)}.getOrNull()
}