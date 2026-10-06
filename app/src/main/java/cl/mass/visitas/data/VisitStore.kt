package cl.mass.visitas.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

data class ChecklistQuestion(
    val id: String = UUID.randomUUID().toString(),
    val section: String,
    val text: String,
    val active: Boolean = true,
)

data class ChecklistAnswer(
    val status: String = "N/A",
    val observation: String = "",
    val correctiveAction: String = "",
    val responsible: String = "",
    val dueDate: String = "",
    val photoUri: String = "",
    val photoUris: List<String> = emptyList(),
    val completed: Boolean = false,
)

data class StockBreak(
    val code: String = "",
    val description: String = "",
    val status: String = "Activo",
)

data class StoreVisit(
    val id: String = UUID.randomUUID().toString(),
    val salesLead: String = "",
    val zonalSupervisor: String = "",
    val store: String = "",
    val administrator: String = "",
    val date: String = LocalDate.now().toString(),
    val salesTarget: String = "",
    val salesActual: String = "",
    val customerCount: String = "",
    val shrinkPercent: String = "",
    val ageMonths: String = "",
    val visitBookStatus: String = "",
    val visitBookObservation: String = "",
    val summaryCommitments: String = "",
    val stockBreaks: List<StockBreak> = emptyList(),
    val answers: Map<String, ChecklistAnswer> = emptyMap(),
)

object VisitStore {
    val sections = listOf(
        "1. Fachada e ingreso",
        "2. Atención al cliente y protocolo de cajas",
        "3. Zona de cajas",
        "4. Reposición y exhibición de mercadería",
        "5. Equipos de frío",
        "6. Almacén, oficina y baños",
        "7. Limpieza de tienda",
        "8. Cartelería",
        "9. Seguimiento y comunicación interna",
        "10. Personas y presentación personal",
    )

    private val starterQuestions = listOf(
        "Limpieza y presentación de fachada.",
        "Validar saludo al cliente, timbrado/reconocimiento mediante DNI e incentivo del cajero para el pago con SIP.",
        "Orden, limpieza, coches y bolsas.",
        "Reposición, stock, precios y espacios vacíos.",
        "Temperatura, reposición y exhibición.",
        "Orden y limpieza del almacén, control de sobrestock, orden de oficina y estado/orden de baños.",
        "Validar limpieza general de sala de ventas, pisos, góndolas, equipos, cajas y zonas visibles al cliente.",
        "Carteles promocionales vigentes.",
        "Memo semanal, conocimiento del equipo y visita del Supervisor Zonal.",
        "Validar uso correcto del uniforme: polo, pantalón, calzado, mandil, presentación personal y cumplimiento de lineamientos.",
    )

    fun load(context: Context): Pair<List<StoreVisit>, List<ChecklistQuestion>> {
        val prefs = context.getSharedPreferences("visitas_mass", Context.MODE_PRIVATE)
        val visits = runCatching { decodeVisits(prefs.getString("visits", "[]").orEmpty()) }.getOrDefault(emptyList())
        var questions = runCatching { decodeQuestions(prefs.getString("questions", null)) }
            .getOrElse { defaultQuestions() }
        // Migración: incorpora la pregunta de uniforme también a instalaciones que ya tenían un checklist guardado.
        if (questions.none { it.text.contains("uso correcto del uniforme", ignoreCase = true) }) {
            questions = questions + ChecklistQuestion(
                section = "10. Personas y presentación personal",
                text = "Validar uso correcto del uniforme: polo, pantalón, calzado, mandil, presentación personal y cumplimiento de lineamientos."
            )
            saveQuestions(context, questions)
        }
        return visits to questions
    }

    fun saveVisits(context: Context, visits: List<StoreVisit>) {
        context.getSharedPreferences("visitas_mass", Context.MODE_PRIVATE).edit()
            .putString("visits", JSONArray().apply { visits.forEach { put(encodeVisit(it)) } }.toString()).apply()
    }

    fun saveQuestions(context: Context, questions: List<ChecklistQuestion>) {
        context.getSharedPreferences("visitas_mass", Context.MODE_PRIVATE).edit()
            .putString("questions", JSONArray().apply { questions.forEach { put(encodeQuestion(it)) } }.toString()).apply()
    }

    fun activeQuestions(questions: List<ChecklistQuestion>) = questions.filter { it.active }

    fun findings(visit: StoreVisit, questions: List<ChecklistQuestion>): List<Pair<ChecklistQuestion, ChecklistAnswer>> =
        questions.mapNotNull { question ->
            visit.answers[question.id]?.takeIf { it.status == "No Conforme" }?.let { question to it }
        }

    private fun defaultQuestions() = starterQuestions.mapIndexed { index, text ->
        ChecklistQuestion(section = sections[index.coerceAtMost(sections.lastIndex)], text = text)
    }

    private fun encodeVisit(visit: StoreVisit) = JSONObject().apply {
        put("id", visit.id); put("salesLead", visit.salesLead); put("zonalSupervisor", visit.zonalSupervisor)
        put("store", visit.store); put("administrator", visit.administrator); put("date", visit.date)
        put("salesTarget", visit.salesTarget); put("salesActual", visit.salesActual); put("customerCount", visit.customerCount)
        put("shrinkPercent", visit.shrinkPercent); put("ageMonths", visit.ageMonths)
        put("visitBookStatus", visit.visitBookStatus); put("visitBookObservation", visit.visitBookObservation)
        put("summaryCommitments", visit.summaryCommitments)
        put("stockBreaks", JSONArray().apply { visit.stockBreaks.forEach { b -> put(JSONObject().apply {
            put("code", b.code); put("description", b.description); put("status", b.status)
        }) } })
        put("answers", JSONObject().apply {
            visit.answers.forEach { (id, answer) -> put(id, JSONObject().apply {
                put("status", answer.status); put("observation", answer.observation); put("correctiveAction", answer.correctiveAction)
                put("responsible", answer.responsible); put("dueDate", answer.dueDate); put("photoUri", answer.photoUri)
                put("photoUris", JSONArray().apply { answer.photoUris.forEach { put(it) } })
                put("completed", answer.completed)
            }) }
        })
    }

    private fun decodeVisits(raw: String) = JSONArray(raw).let { array ->
        (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            val answersJson = item.optJSONObject("answers") ?: JSONObject()
            val answers = answersJson.keys().asSequence().associateWith { key ->
                val value = answersJson.getJSONObject(key)
                val legacyPhoto = value.optString("photoUri")
                val photosJson = value.optJSONArray("photoUris") ?: JSONArray()
                val photos = (0 until photosJson.length()).map { i -> photosJson.optString(i) }.filter { it.isNotBlank() }
                    .ifEmpty { if (legacyPhoto.isNotBlank()) listOf(legacyPhoto) else emptyList() }
                ChecklistAnswer(value.optString("status", "N/A"), value.optString("observation"),
                    value.optString("correctiveAction"), value.optString("responsible"), value.optString("dueDate"),
                    legacyPhoto, photos, value.optBoolean("completed", false))
            }
            val breaksJson = item.optJSONArray("stockBreaks") ?: JSONArray()
            val stockBreaks = (0 until breaksJson.length()).map { i ->
                val b = breaksJson.getJSONObject(i)
                StockBreak(b.optString("code"), b.optString("description"), b.optString("status", "Activo"))
            }
            StoreVisit(
                id = item.optString("id", UUID.randomUUID().toString()),
                salesLead = item.optString("salesLead"), zonalSupervisor = item.optString("zonalSupervisor"),
                store = item.optString("store"), administrator = item.optString("administrator"),
                date = item.optString("date", LocalDate.now().toString()), salesTarget = item.optString("salesTarget"),
                salesActual = item.optString("salesActual"), customerCount = item.optString("customerCount"),
                shrinkPercent = item.optString("shrinkPercent"), ageMonths = item.optString("ageMonths"),
                visitBookStatus = item.optString("visitBookStatus"), visitBookObservation = item.optString("visitBookObservation"),
                summaryCommitments = item.optString("summaryCommitments"), stockBreaks = stockBreaks, answers = answers
            )
        }
    }

    private fun encodeQuestion(question: ChecklistQuestion) = JSONObject().apply {
        put("id", question.id); put("section", question.section); put("text", question.text); put("active", question.active)
    }

    private fun decodeQuestions(raw: String?): List<ChecklistQuestion> {
        if (raw == null) return defaultQuestions()
        return JSONArray(raw).let { array -> (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            ChecklistQuestion(item.getString("id"), item.getString("section"), item.getString("text"), item.optBoolean("active", true))
        } }
    }
}