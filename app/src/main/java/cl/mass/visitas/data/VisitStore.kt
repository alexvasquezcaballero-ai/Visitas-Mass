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
    val completed: Boolean = false,
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
    val answers: Map<String, ChecklistAnswer> = emptyMap(),
)

object VisitStore {
    val sections = listOf(
        "Cliente y Venta",
        "Ejecución Comercial",
        "Merma",
        "Inventario y Trastienda",
        "Procesos y Seguridad",
        "Personas y Gestión",
    )

    private val starterQuestions = listOf(
        "Atención y experiencia del cliente", "Conocimiento de metas y resultados", "Venta sugerida y disponibilidad de productos",
        "Precios y promociones correctamente señalizados", "Exhibiciones completas y planograma vigente", "Frentes ordenados y productos disponibles",
        "Productos dañados o vencidos identificados", "Registros de merma completos y al día", "Conteos cíclicos realizados según programa",
        "Bodega ordenada, limpia y con pasillos despejados", "Recepción y almacenamiento según procedimiento", "Extintores y rutas de evacuación despejados",
        "Apertura y cierre ejecutados según pauta", "Registros operacionales completos y firmados", "Dotación, turnos y asistencia revisados",
        "Roles, metas y prioridades comunicados al equipo", "Retroalimentación y capacitación del equipo revisadas",
        "Incidentes, reconocimientos y acuerdos con seguimiento",
    )

    fun load(context: Context): Pair<List<StoreVisit>, List<ChecklistQuestion>> {
        val prefs = context.getSharedPreferences("visitas_mass", Context.MODE_PRIVATE)
        val visits = runCatching { decodeVisits(prefs.getString("visits", "[]").orEmpty()) }.getOrDefault(emptyList())
        val questions = runCatching { decodeQuestions(prefs.getString("questions", null)) }
            .getOrElse { defaultQuestions() }
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
        ChecklistQuestion(section = sections[index / 3], text = text)
    }

    private fun encodeVisit(visit: StoreVisit) = JSONObject().apply {
        put("id", visit.id); put("salesLead", visit.salesLead); put("zonalSupervisor", visit.zonalSupervisor)
        put("store", visit.store); put("administrator", visit.administrator); put("date", visit.date)
        put("salesTarget", visit.salesTarget); put("salesActual", visit.salesActual); put("customerCount", visit.customerCount)
        put("answers", JSONObject().apply {
            visit.answers.forEach { (id, answer) -> put(id, JSONObject().apply {
                put("status", answer.status); put("observation", answer.observation); put("correctiveAction", answer.correctiveAction)
                put("responsible", answer.responsible); put("dueDate", answer.dueDate); put("photoUri", answer.photoUri)
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
                ChecklistAnswer(value.optString("status", "N/A"), value.optString("observation"),
                    value.optString("correctiveAction"), value.optString("responsible"), value.optString("dueDate"),
                    value.optString("photoUri"), value.optBoolean("completed", false))
            }
            StoreVisit(item.optString("id", UUID.randomUUID().toString()), item.optString("salesLead"),
                item.optString("zonalSupervisor"), item.optString("store"), item.optString("administrator"),
                item.optString("date", LocalDate.now().toString()), item.optString("salesTarget"),
                item.optString("salesActual"), item.optString("customerCount"), answers)
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