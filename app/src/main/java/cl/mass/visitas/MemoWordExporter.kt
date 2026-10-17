package cl.mass.visitas

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import cl.mass.visitas.data.ChecklistQuestion
import cl.mass.visitas.data.StoreVisit
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object MemoWordExporter {
    private fun esc(s: String) = s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
    private fun para(s: String, style: String = "") = "<w:p><w:pPr><w:pStyle w:val=\"$style\"/></w:pPr><w:r><w:t xml:space=\"preserve\">${esc(s)}</w:t></w:r></w:p>"
    private fun initials(name: String): String {
        val words = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        return if (words.size >= 2) "${words.first().first().uppercaseChar()}.${words.last().first().uppercaseChar()}."
        else words.firstOrNull()?.take(1)?.uppercase()?.plus(".") ?: ""
    }
    private fun table(rows: List<List<String>>): String = "<w:tbl><w:tblPr><w:tblBorders><w:top w:val=\"single\"/><w:left w:val=\"single\"/><w:bottom w:val=\"single\"/><w:right w:val=\"single\"/><w:insideH w:val=\"single\"/><w:insideV w:val=\"single\"/></w:tblBorders></w:tblPr>" +
        rows.joinToString("") { row -> "<w:tr>" + row.joinToString("") { cell -> "<w:tc><w:p><w:r><w:t>${esc(cell)}</w:t></w:r></w:p></w:tc>" } + "</w:tr>" } + "</w:tbl>"
    fun create(context: Context, visit: StoreVisit, questions: List<ChecklistQuestion>): File {
        val parts = mutableListOf<String>()
        val images = mutableListOf<Pair<String, ByteArray>>()
        parts += para("MEMORÁNDUM DE VISITA A TIENDA", "Title")
        listOf("Jefe de Ventas" to visit.salesLead, "Supervisor Zonal" to visit.zonalSupervisor,
            "Tienda" to visit.store, "Administrador" to visit.administrator, "Fecha" to visit.date).forEach { (k,v) -> parts += para("$k: $v") }
        parts += para("INDICADORES DEL PERIODO", "Heading1")
        parts += para("Venta del periodo (S/): ${visit.salesActual} | Merma del periodo (%): ${visit.shrinkPercent} | Antigüedad (meses): ${visit.ageMonths}")
        val active = questions.filter { it.active }
        val standardOrder = listOf(
            "Fachada e ingreso", "Atención al cliente y protocolo de cajas", "Zona de cajas",
            "Reposición y exhibición de mercadería", "Equipos de frío", "Almacén, oficina y baños",
            "Limpieza de tienda", "Cartelería", "Seguimiento y comunicación interna",
            "Personas y presentación personal"
        )
        active.groupBy { it.section }.toList().sortedBy { (section, _) ->
            standardOrder.indexOfFirst { it.equals(section.trim(), ignoreCase = true) }
                .let { if (it < 0) Int.MAX_VALUE else it }
        }.forEachIndexed { index, (section, qs) ->
            parts += para("${index + 1}. ${section.replace(Regex("""^\\d+\\.\\s*"""), "")}", "Heading1")
            qs.forEach { q -> visit.answers[q.id]?.let { a ->
                if (a.status != "N/A") {
                    // La descripción del criterio se muestra en la app, no en el memorándum.
                    parts += para("Estado: ${a.status}")
                    if (a.observation.isNotBlank()) parts += para(a.observation)
                    if (a.correctiveAction.isNotBlank()) parts += para("Acción correctiva: ${a.correctiveAction}")
                    if (a.responsible.isNotBlank()) parts += para("Responsable: ${a.responsible} | Compromiso: ${a.dueDate}")
                }
            } }
        }
        parts += para("11. Quiebres de mercadería", "Heading1")
        parts += table(listOf(listOf("Código", "Descripción", "Estado")) + visit.stockBreaks.map { listOf(it.code, it.description, it.status) })
        parts += para("12. Cuaderno de visitas", "Heading1")
        parts += para("Registrado: ${visit.visitBookStatus}. ${visit.visitBookObservation}")
        parts += para("13. Resumen / compromisos", "Heading1")
        parts += para(visit.summaryCommitments)
        val photos = active.flatMap { q -> visit.answers[q.id]?.let { a ->
            a.photoUris.ifEmpty { listOfNotNull(a.photoUri.takeIf { it.isNotBlank() }) }.map { q.section to it }
        } ?: emptyList() }
        if (photos.isNotEmpty()) {
            parts += "<w:p><w:r><w:br w:type=\"page\"/></w:r></w:p>"
            parts += para("Anexos Fotográficos", "Heading1")
            photos.forEachIndexed { i, (section, uri) ->
                val bytes = runCatching {
                    if (uri.startsWith("content://")) context.contentResolver.openInputStream(Uri.parse(uri))?.use { it.readBytes() }
                    else File(uri).readBytes()
                }.getOrNull()
                if (bytes != null) {
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        val name = "image${images.size+1}.jpg"
                        val out = java.io.ByteArrayOutputStream()
                        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, out)
                        val imageBytes = out.toByteArray()
                        bitmap.recycle()
                        images += name to imageBytes
                        val rid = "rId${images.size}"
                        // El pie de foto se coloca después de la imagen, como en la plantilla.
                        val maxWidth = 4572000L
                        val maxHeight = 5486400L
                        val scale = minOf(maxWidth.toDouble() / bitmap.width.coerceAtLeast(1), maxHeight.toDouble() / bitmap.height.coerceAtLeast(1))
                        val photoWidth = (bitmap.width * scale).toLong()
                        val photoHeight = (bitmap.height * scale).toLong()
                        parts += """<w:tbl><w:tblPr><w:tblW w:w="0" w:type="auto"/><w:tblBorders><w:top w:val="single" w:sz="6"/><w:bottom w:val="single" w:sz="6"/><w:left w:val="single" w:sz="6"/><w:right w:val="single" w:sz="6"/></w:tblBorders></w:tblPr><w:tr><w:tc><w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:drawing><wp:inline xmlns:wp="http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"><wp:extent cx="$photoWidth" cy="$photoHeight"/><wp:docPr id="${i+1}" name="Foto ${i+1}"/><a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"><a:graphicData uri="http://schemas.openxmlformats.org/drawingml/2006/picture"><pic:pic xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture"><pic:nvPicPr><pic:cNvPr id="0" name="$name"/><pic:cNvPicPr/></pic:nvPicPr><pic:blipFill><a:blip xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" r:embed="$rid"/><a:stretch><a:fillRect/></a:stretch></pic:blipFill><pic:spPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="$photoWidth" cy="$photoHeight"/></a:xfrm><a:prstGeom prst="rect"><a:avLst/></a:prstGeom></pic:spPr></pic:pic></a:graphicData></a:graphic></wp:inline></w:drawing></w:r></w:p></w:tc></w:tr></w:tbl>"""
                        parts += para("Anexo ${i+1}. $section", "Caption")
                    }
                }
            }
        }
        val dir = File(context.cacheDir, "memos").apply { mkdirs() }
        val file = File(dir, "Memo_Visita_${visit.store.replace(Regex("[^A-Za-z0-9_-]"),"_").take(40)}_${visit.date}.docx")
        val footer = initials(visit.salesLead)
        ZipOutputStream(file.outputStream()).use { zip ->
            fun add(name: String, content: ByteArray) { zip.putNextEntry(ZipEntry(name)); zip.write(content); zip.closeEntry() }
            fun addText(name: String, content: String) = add(name, content.toByteArray(Charsets.UTF_8))
            addText("[Content_Types].xml", """<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Default Extension="jpg" ContentType="image/jpeg"/><Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/><Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/><Override PartName="/word/footer1.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml"/></Types>""")
            addText("_rels/.rels", """<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>""")
            addText("word/document.xml", """<?xml version="1.0" encoding="UTF-8"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body>${parts.joinToString("")}<w:sectPr><w:footerReference w:type="default" r:id="rIdFooter" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"/><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440"/></w:sectPr></w:body></w:document>""")
            addText("word/styles.xml", """<?xml version="1.0" encoding="UTF-8"?><w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:docDefaults><w:rPrDefault><w:rPr><w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman"/><w:sz w:val="24"/></w:rPr></w:rPrDefault></w:docDefaults><w:style w:type="paragraph" w:styleId="Title"><w:name w:val="Title"/> <w:pPr><w:jc w:val="center"/><w:spacing w:after="280"/></w:pPr><w:rPr><w:b/><w:sz w:val="28"/><w:color w:val="1F3864"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="Heading1"><w:name w:val="Heading1"/><w:pPr><w:spacing w:before="200" w:after="100"/><w:keepNext/></w:pPr><w:rPr><w:b/><w:color w:val="1F3864"/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="Heading2"><w:name w:val="Heading2"/><w:pPr><w:spacing w:before="100" w:after="60"/></w:pPr><w:rPr><w:b/></w:rPr></w:style><w:style w:type="paragraph" w:styleId="Caption"><w:name w:val="Caption"/><w:pPr><w:jc w:val="center"/><w:spacing w:before="180" w:after="80"/></w:pPr><w:rPr><w:i/></w:rPr></w:style></w:styles>""")
            addText("word/footer1.xml", """<?xml version="1.0" encoding="UTF-8"?><w:ftr xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:p><w:pPr><w:jc w:val="right"/></w:pPr><w:r><w:t>${footer}</w:t></w:r></w:p></w:ftr>""")
            addText("word/_rels/document.xml.rels", """<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rIdFooter" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/footer" Target="footer1.xml"/><Relationship Id="rIdStyles" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>${images.mapIndexed { i,(name,_) -> "<Relationship Id=\"rId${i+1}\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/image\" Target=\"media/$name\"/>" }.joinToString("")}</Relationships>""")
            images.forEach { (name,bytes) -> add("word/media/$name", bytes) }
        }
        return file
    }
}
