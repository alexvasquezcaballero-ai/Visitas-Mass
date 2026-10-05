package cl.mass.visitas

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.mass.visitas.data.ChecklistAnswer
import cl.mass.visitas.data.ChecklistQuestion
import cl.mass.visitas.data.StoreVisit
import cl.mass.visitas.data.VisitStore
import cl.mass.visitas.data.StoreDirectory
import cl.mass.visitas.data.StockBreak
import kotlinx.coroutines.launch

private val MassBlue = Color(0xFF123B73)
private val MassYellow = Color(0xFFF3C400)
private val Canvas = Color(0xFFF4F6F8)
private val Ink = Color(0xFF172536)
private val destinations = listOf("Dashboard", "Nueva visita", "Seguimiento", "Memorándum", "Historial", "Configuración")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(18, 59, 115)
        setContent { MassApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MassApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val (initialVisits, initialQuestions) = remember { VisitStore.load(context) }
    val visits = remember { mutableStateListOf<StoreVisit>().apply { addAll(initialVisits) } }
    val questions = remember { mutableStateListOf<ChecklistQuestion>().apply { addAll(initialQuestions) } }
    var destination by remember { mutableStateOf("Dashboard") }
    var selectedVisitId by remember { mutableStateOf<String?>(null) }
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    MaterialTheme(colorScheme = androidx.compose.material3.lightColorScheme(
        primary = MassBlue, onPrimary = Color.White, secondary = MassYellow, onSecondary = Ink,
        background = Canvas, surface = Color.White, onSurface = Ink, error = Color(0xFFB42318),
    )) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(drawerContainerColor = Color.White) {
                    Column(Modifier.fillMaxWidth().background(MassBlue).padding(24.dp)) {
                        Text("MASS", color = MassYellow, fontSize = 27.sp, fontWeight = FontWeight.Black)
                        Text("Gestión de tiendas", color = Color.White, fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(10.dp))
                    destinations.forEach { page ->
                        val icon = when (page) {
                            "Dashboard" -> Icons.Default.Assessment
                            "Nueva visita" -> Icons.Default.Storefront
                            "Seguimiento" -> Icons.Default.Assignment
                            "Memorándum" -> Icons.Default.ListAlt
                            "Historial" -> Icons.Default.History
                            else -> Icons.Default.Edit
                        }
                        NavigationDrawerItem(
                            label = { Text(page) }, selected = destination == page,
                            icon = { Icon(icon, contentDescription = null) },
                            onClick = { destination = page; scope.launch { drawerState.close() } },
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }
                }
            },
        ) {
            Scaffold(
                containerColor = Canvas,
                topBar = {
                    TopAppBar(
                        title = { Text(destination, fontWeight = FontWeight.Bold, color = Color.White) },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, "Abrir menú", tint = Color.White)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MassBlue),
                    )
                },
                floatingActionButton = {
                    if (destination == "Historial" || destination == "Dashboard") {
                        FloatingActionButton(onClick = { destination = "Nueva visita" }, containerColor = MassYellow, contentColor = Ink) {
                            Icon(Icons.Default.Add, "Nueva visita")
                        }
                    }
                },
            ) { padding ->
                when (destination) {
                    "Dashboard" -> DashboardScreen(visits, questions, Modifier.padding(padding))
                    "Nueva visita" -> NewVisitScreen(questions, { visit ->
                        visits.add(0, visit); VisitStore.saveVisits(context, visits); selectedVisitId = visit.id; destination = "Historial"
                    }, Modifier.padding(padding))
                    "Seguimiento" -> FollowUpScreen(visits, questions, { updated ->
                        val index = visits.indexOfFirst { it.id == updated.id }
                        if (index >= 0) visits[index] = updated
                        VisitStore.saveVisits(context, visits)
                    }, Modifier.padding(padding))
                    "Memorándum" -> MemoScreen(visits, questions, selectedVisitId, Modifier.padding(padding))
                    "Historial" -> HistoryScreen(visits, questions, onMemo = { id -> selectedVisitId = id; destination = "Memorándum" }, Modifier.padding(padding))
                    else -> SettingsScreen(questions, { VisitStore.saveQuestions(context, questions) }, Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
private fun PageColumn(modifier: Modifier = Modifier, content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit) {
    Column(modifier.fillMaxSize().padding(horizontal = 18.dp, vertical = 14.dp), content = content)
}

@Composable
private fun DashboardScreen(visits: List<StoreVisit>, questions: List<ChecklistQuestion>, modifier: Modifier = Modifier) {
    val allFindings = visits.flatMap { visit -> VisitStore.findings(visit, questions).map { Triple(visit, it.first, it.second) } }
    val findings = allFindings.size
    val openActions = allFindings.count { !it.third.completed }
    val totalAnswers = visits.sumOf { it.answers.values.count { a -> a.status != "N/A" } }
    val conformity = if (totalAnswers == 0) 0 else ((totalAnswers - findings) * 100 / totalAnswers)
    val recurring = allFindings.groupBy { it.second.section }.mapValues { it.value.size }.entries.sortedByDescending { it.value }
    val storeRanking = allFindings.groupBy { it.first.store }.mapValues { it.value.size }.entries.sortedByDescending { it.value }
    val leadRanking = allFindings.groupBy { it.first.salesLead }.mapValues { it.value.size }.entries.sortedByDescending { it.value }

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 14.dp)) {
        Text("Dashboard gerencial", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Recurrencias y prioridades de acción", color = Color(0xFF667085))
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricTile("Visitas", visits.size.toString(), Icons.Default.Storefront, Modifier.weight(1f))
            MetricTile("Hallazgos", findings.toString(), Icons.Default.Assignment, Modifier.weight(1f))
            MetricTile("Conformidad", "$conformity%", Icons.Default.Check, Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Text("$openActions acciones pendientes", color = if (openActions > 0) Color(0xFFB54708) else Color(0xFF067647), fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(20.dp))
        SectionTitle("Pareto de observaciones")
        if (recurring.isEmpty()) Text("Se alimentará automáticamente con las visitas.", color = Color(0xFF667085))
        else recurring.take(6).forEachIndexed { index, item ->
            val pct = if (findings == 0) 0 else item.value * 100 / findings
            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${index + 1}. ${item.key}", Modifier.weight(1f), fontSize = 13.sp)
                Text("${item.value} · $pct%", fontWeight = FontWeight.Bold, color = MassBlue)
            }
        }
        Spacer(Modifier.height(18.dp))
        SectionTitle("Tiendas con mayor recurrencia")
        storeRanking.take(5).forEach { item ->
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                Text(item.key.ifBlank { "Sin tienda" }, Modifier.weight(1f), fontSize = 13.sp)
                Text("${item.value} hallazgos", fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(Modifier.height(18.dp))
        SectionTitle("Hallazgos por Jefe de Ventas")
        leadRanking.forEach { item ->
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                Text(item.key.ifBlank { "Sin jefe" }, Modifier.weight(1f), fontSize = 13.sp)
                Text(item.value.toString(), fontWeight = FontWeight.Bold, color = MassBlue)
            }
        }
        Spacer(Modifier.height(18.dp))
        SectionTitle("Actividad reciente")
        if (visits.isEmpty()) EmptyState("Aún no hay visitas", "Registra la primera visita para comenzar.")
        else visits.take(5).forEach { VisitRow(it, questions) }
    }
}
@Composable
private fun MetricTile(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(12.dp)) {
            Icon(icon, null, tint = MassBlue, modifier = Modifier.size(19.dp))
            Spacer(Modifier.height(10.dp))
            Text(value, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 11.sp, color = Color(0xFF667085), maxLines = 1)
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Storefront, null, tint = Color(0xFF98A2B3), modifier = Modifier.size(34.dp))
        Spacer(Modifier.height(8.dp)); Text(title, fontWeight = FontWeight.SemiBold)
        Text(subtitle, color = Color(0xFF667085), fontSize = 13.sp)
    }
}

@Composable
private fun VisitRow(visit: StoreVisit, questions: List<ChecklistQuestion>, trailing: @Composable (() -> Unit)? = null) {
    val findingCount = VisitStore.findings(visit, questions).size
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(visit.store.ifBlank { "Tienda sin nombre" }, fontWeight = FontWeight.Bold)
                Text("${visit.date} · ${visit.administrator.ifBlank { "Sin administrador" }}", color = Color(0xFF667085), fontSize = 12.sp)
                Text("$findingCount hallazgos", color = if (findingCount > 0) Color(0xFFB54708) else Color(0xFF067647), fontSize = 12.sp)
            }
            trailing?.invoke()
        }
    }
}

@Composable
private fun NewVisitScreen(questions: List<ChecklistQuestion>, onSave: (StoreVisit) -> Unit, modifier: Modifier = Modifier) {
    var salesLead by remember { mutableStateOf("") }
    var supervisor by remember { mutableStateOf("") }
    var store by remember { mutableStateOf("") }
    var administrator by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(java.time.LocalDate.now().toString()) }
    var target by remember { mutableStateOf("") }
    var actual by remember { mutableStateOf("") }
    var customers by remember { mutableStateOf("") }
    var shrink by remember { mutableStateOf("") }
    var ageMonths by remember { mutableStateOf("") }
    var breakCode by remember { mutableStateOf("") }
    var breakDescription by remember { mutableStateOf("") }
    var breakStatus by remember { mutableStateOf("Activo") }
    val stockBreaks = remember { mutableStateListOf<StockBreak>() }
    var visitBookStatus by remember { mutableStateOf("") }
    var visitBookObservation by remember { mutableStateOf("") }
    var summaryCommitments by remember { mutableStateOf("") }
    val answers = remember { mutableStateMapOf<String, ChecklistAnswer>() }
    var editingQuestion by remember { mutableStateOf<ChecklistQuestion?>(null) }
    var error by remember { mutableStateOf(false) }

    PageColumn(modifier) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            item { Text("Datos de la visita", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            item { SelectionField("Jefe de Ventas *", salesLead, StoreDirectory.salesLeads) { salesLead = it; supervisor = ""; store = "" } }
            item { SelectionField("Supervisor Zonal *", supervisor, StoreDirectory.supervisors(salesLead), enabled = salesLead.isNotBlank()) { supervisor = it; store = "" } }
            item { SelectionField("Tienda *", store, StoreDirectory.stores(salesLead, supervisor), enabled = supervisor.isNotBlank()) { store = it } }
            item { FormField("Administrador", administrator, { administrator = it }) }
            item { FormField("Fecha (AAAA-MM-DD)", date, { date = it }) }
            item { Text("Indicadores", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
            item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormField("Meta de venta", target, { target = it }, Modifier.weight(1f), numeric = true)
                FormField("Venta real", actual, { actual = it }, Modifier.weight(1f), numeric = true)
            } }
            item { FormField("Venta del periodo (S/)", actual, { actual = it }, numeric = true) }
            item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FormField("Merma del periodo (%)", shrink, { shrink = it }, Modifier.weight(1f), numeric = true)
                FormField("Antigüedad (meses)", ageMonths, { ageMonths = it }, Modifier.weight(1f), numeric = true)
            } }
            item { Text("Checklist", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp)) }
            VisitStore.sections.forEach { section ->
                val sectionQuestions = VisitStore.activeQuestions(questions).filter { it.section == section }
                if (sectionQuestions.isNotEmpty()) item { Text(section, fontWeight = FontWeight.SemiBold, color = MassBlue, modifier = Modifier.padding(top = 8.dp)) }
                items(sectionQuestions, key = { it.id }) { question ->
                    val answer = answers[question.id] ?: ChecklistAnswer()
                    QuestionAnswerRow(question, answer, onStatus = { status ->
                        answers[question.id] = answer.copy(status = status)
                        if (status == "No Conforme") editingQuestion = question
                    }, onEdit = { editingQuestion = question })
                }
            }
            item { Text("Quiebres de mercadería", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp)) }
            item { FormField("Código", breakCode, { breakCode = it }) }
            item { FormField("Descripción", breakDescription, { breakDescription = it }) }
            item { SelectionField("Estado", breakStatus, listOf("Bloqueado", "Obsoleto", "Inactivo", "Activo")) { breakStatus = it } }
            item {
                OutlinedButton(onClick = {
                    if (breakCode.isNotBlank() || breakDescription.isNotBlank()) {
                        stockBreaks.add(StockBreak(breakCode.trim(), breakDescription.trim(), breakStatus))
                        breakCode = ""; breakDescription = ""; breakStatus = "Activo"
                    }
                }, modifier = Modifier.fillMaxWidth()) { Text("+ Agregar quiebre") }
            }
            items(stockBreaks) { b ->
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(10.dp)) {
                        Text("${b.code} · ${b.description}", fontWeight = FontWeight.SemiBold)
                        Text("${store.ifBlank { "Sin tienda" }} · ${b.status}", fontSize = 12.sp, color = Color(0xFF667085))
                    }
                }
            }
            item { Text("Cuaderno de visitas", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp)) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = visitBookStatus == "Sí", onClick = { visitBookStatus = "Sí" }, label = { Text("Sí, se registró") })
                    FilterChip(selected = visitBookStatus == "No", onClick = { visitBookStatus = "No" }, label = { Text("No se registró") })
                }
            }
            item { FormField("Observación sobre el cuaderno", visitBookObservation, { visitBookObservation = it }) }
            item { Text("Resumen / compromisos", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp)) }
            item { FormField("Acuerdos, responsable y fecha de regularización", summaryCommitments, { summaryCommitments = it }) }
            item {
                if (error) Text("Completa Jefe de Ventas, Supervisor, Tienda y Administrador.", color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                Button(onClick = {
                    if (salesLead.isBlank() || supervisor.isBlank() || store.isBlank() || administrator.isBlank()) error = true
                    else onSave(StoreVisit(salesLead = salesLead, zonalSupervisor = supervisor, store = store,
                        administrator = administrator, date = date, salesTarget = target, salesActual = actual,
                        customerCount = customers, shrinkPercent = shrink, ageMonths = ageMonths,
                        visitBookStatus = visitBookStatus, visitBookObservation = visitBookObservation,
                        summaryCommitments = summaryCommitments, stockBreaks = stockBreaks.toList(), answers = answers.toMap()))
                }, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) { Text("Guardar visita") }
            }
        }
    }
    editingQuestion?.let { question ->
        FindingEditorDialog(question, answers[question.id] ?: ChecklistAnswer(status = "No Conforme"),
            onDismiss = { editingQuestion = null }, onSave = { answers[question.id] = it; editingQuestion = null })
    }
}

@Composable
private fun QuestionAnswerRow(question: ChecklistQuestion, answer: ChecklistAnswer, onStatus: (String) -> Unit, onEdit: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(10.dp)) {
        Text(question.text, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(top = 7.dp)) {
            listOf("Conforme", "No Conforme", "N/A").forEach { status ->
                FilterChip(selected = answer.status == status, onClick = { onStatus(status) }, label = { Text(status, fontSize = 11.sp) })
            }
            if (answer.status == "No Conforme") IconButton(onClick = onEdit, modifier = Modifier.size(38.dp)) {
                Icon(Icons.Default.Edit, "Editar hallazgo", tint = MassBlue, modifier = Modifier.size(19.dp))
            }
        }
        if (answer.status == "No Conforme") {
            Text("${if (answer.photoUri.isNotBlank()) "Foto adjunta · " else ""}${answer.observation.ifBlank { "Agregar observación y acción" }}",
                color = Color(0xFF667085), fontSize = 11.sp, modifier = Modifier.clickable(onClick = onEdit))
        }
    }
}

@Composable
private fun FindingEditorDialog(question: ChecklistQuestion, initial: ChecklistAnswer, onDismiss: () -> Unit, onSave: (ChecklistAnswer) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var observation by remember(question.id) { mutableStateOf(initial.observation) }
    var action by remember(question.id) { mutableStateOf(initial.correctiveAction) }
    var responsible by remember(question.id) { mutableStateOf(initial.responsible) }
    var dueDate by remember(question.id) { mutableStateOf(initial.dueDate) }
    var photoUri by remember(question.id) { mutableStateOf(initial.photoUri) }
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            photoUri = uri.toString()
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hallazgo", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                item { Text(question.text, color = MassBlue, fontWeight = FontWeight.Medium) }
                item { FormField("Observación", observation, { observation = it }, singleLine = false) }
                item { FormField("Acción correctiva", action, { action = it }, singleLine = false) }
                item { FormField("Responsable", responsible, { responsible = it }) }
                item { FormField("Fecha compromiso (AAAA-MM-DD)", dueDate, { dueDate = it }) }
                item { OutlinedButton(onClick = { photoPicker.launch(arrayOf("image/*")) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.PhotoCamera, null); Spacer(Modifier.width(8.dp)); Text(if (photoUri.isBlank()) "Adjuntar fotografía" else "Fotografía adjunta")
                } }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(initial.copy(status = "No Conforme", observation = observation,
            correctiveAction = action, responsible = responsible, dueDate = dueDate, photoUri = photoUri)) }) { Text("Guardar hallazgo") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
private fun FollowUpScreen(visits: List<StoreVisit>, questions: List<ChecklistQuestion>, onVisitChange: (StoreVisit) -> Unit, modifier: Modifier = Modifier) {
    val followUps = visits.flatMap { visit -> VisitStore.findings(visit, questions).map { (question, answer) -> Triple(visit, question, answer) } }
    PageColumn(modifier) {
        SectionTitle("Compromisos de tienda")
        Text("${followUps.size} hallazgos registrados", color = Color(0xFF667085), fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        if (followUps.isEmpty()) EmptyState("Sin compromisos pendientes", "Los hallazgos no conformes aparecerán aquí.")
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(followUps, key = { it.second.id + it.first.id }) { (visit, question, answer) ->
                Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(question.section.uppercase(), color = MassBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(question.text, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                        Text("${visit.store} · ${visit.date}", color = Color(0xFF667085), fontSize = 12.sp)
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                        DetailLine("Observación", answer.observation)
                        DetailLine("Acción", answer.correctiveAction)
                        DetailLine("Responsable", answer.responsible)
                        DetailLine("Compromiso", answer.dueDate)
                        if (answer.photoUri.isNotBlank()) Text("Fotografía adjunta", color = MassBlue, fontSize = 12.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = answer.completed, onCheckedChange = { completed ->
                                onVisitChange(visit.copy(answers = visit.answers + (question.id to answer.copy(completed = completed))))
                            })
                            Text(if (answer.completed) "Compromiso cumplido" else "Marcar como cumplido", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    if (value.isNotBlank()) Row(Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text("$label: ", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Text(value, fontSize = 12.sp, color = Color(0xFF475467))
    }
}

@Composable
private fun MemoScreen(visits: List<StoreVisit>, questions: List<ChecklistQuestion>, selectedId: String?, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var currentId by remember { mutableStateOf(selectedId ?: visits.firstOrNull()?.id) }
    val visit = visits.firstOrNull { it.id == currentId }
    PageColumn(modifier) {
        SectionTitle("Memorándum de visita")
        if (visit == null) EmptyState("Sin visitas para resumir", "Guarda una visita para generar su memorándum.")
        else {
            Box {
                OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text("${visit.store} · ${visit.date}") }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    visits.forEach { item -> DropdownMenuItem(text = { Text("${item.store} · ${item.date}") }, onClick = { currentId = item.id; expanded = false }) }
                }
            }
            OutlinedButton(onClick = {
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Memorándum de visita · ${visit.store}")
                    putExtra(Intent.EXTRA_TEXT, memoText(visit, questions))
                }
                context.startActivity(Intent.createChooser(send, "Compartir memorándum"))
            }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Icon(Icons.Default.Share, null); Spacer(Modifier.width(8.dp)); Text("Compartir memorándum")
            }
            Spacer(Modifier.height(12.dp))
            MemoDocument(visit, questions)
        }
    }
}

private fun memoText(visit: StoreVisit, questions: List<ChecklistQuestion>): String = buildString {
    appendLine("MEMORÁNDUM DE VISITA · ${visit.store}")
    appendLine("Fecha: ${visit.date}")
    appendLine("Jefe de Ventas: ${visit.salesLead}")
    appendLine("Supervisor Zonal: ${visit.zonalSupervisor}")
    appendLine("Administrador: ${visit.administrator}")
    appendLine("Indicadores: meta ${visit.salesTarget}, venta real ${visit.salesActual}, clientes ${visit.customerCount}")
    appendLine()
    val findings = VisitStore.findings(visit, questions)
    appendLine("HALLAZGOS (${findings.size})")
    findings.forEach { (question, answer) ->
        appendLine("- [${question.section}] ${question.text}")
        appendLine("  Observación: ${answer.observation}")
        appendLine("  Acción correctiva: ${answer.correctiveAction}")
        appendLine("  Responsable: ${answer.responsible} · Compromiso: ${answer.dueDate}")
    }
}

@Composable
private fun MemoDocument(visit: StoreVisit, questions: List<ChecklistQuestion>) {
    val findings = VisitStore.findings(visit, questions)
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = MassBlue)) {
                Column(Modifier.fillMaxWidth().padding(17.dp)) {
                    Text("MASS  /  OPERACIONES", color = MassYellow, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    Text("MEMORÁNDUM DE VISITA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(top = 8.dp))
                    Text("${visit.store} · ${visit.date}", color = Color.White.copy(alpha = .8f))
                }
            }
        }
        item { Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(14.dp)) {
                DetailLine("Jefe de Ventas", visit.salesLead); DetailLine("Supervisor Zonal", visit.zonalSupervisor)
                DetailLine("Administrador", visit.administrator); DetailLine("Tienda", visit.store); DetailLine("Fecha", visit.date)
                DetailLine("Meta de venta", visit.salesTarget); DetailLine("Venta real", visit.salesActual)
                DetailLine("Clientes atendidos", visit.customerCount)
            }
        } }
        item { SectionTitle("Resumen de hallazgos · ${findings.size}") }
        if (findings.isEmpty()) item { Text("Sin hallazgos no conformes registrados.", color = Color(0xFF067647)) }
        items(findings) { (question, answer) -> Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(13.dp)) {
                Text(question.section, color = MassBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(question.text, fontWeight = FontWeight.SemiBold)
                DetailLine("Observación", answer.observation); DetailLine("Acción correctiva", answer.correctiveAction)
                DetailLine("Responsable", answer.responsible); DetailLine("Fecha compromiso", answer.dueDate)
                if (answer.photoUri.isNotBlank()) Text("Fotografía adjunta", color = MassBlue, fontSize = 12.sp)
            }
        } }
    }
}

@Composable
private fun HistoryScreen(visits: List<StoreVisit>, questions: List<ChecklistQuestion>, onMemo: (String) -> Unit, modifier: Modifier = Modifier) {
    PageColumn(modifier) {
        SectionTitle("Historial de visitas")
        Text("${visits.size} visitas guardadas en este dispositivo", color = Color(0xFF667085), fontSize = 13.sp)
        Spacer(Modifier.height(8.dp))
        if (visits.isEmpty()) EmptyState("Historial vacío", "Usa + para registrar una visita.")
        else LazyColumn {
            items(visits, key = { it.id }) { visit -> VisitRow(visit, questions) {
                IconButton(onClick = { onMemo(visit.id) }) { Icon(Icons.Default.ListAlt, "Ver memorándum", tint = MassBlue) }
            } }
        }
    }
}

@Composable
private fun SettingsScreen(questions: MutableList<ChecklistQuestion>, persist: () -> Unit, modifier: Modifier = Modifier) {
    var editing by remember { mutableStateOf<ChecklistQuestion?>(null) }
    var creating by remember { mutableStateOf(false) }
    PageColumn(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { SectionTitle("Preguntas del checklist"); Text("${questions.count { it.active }} activas", color = Color(0xFF667085), fontSize = 12.sp) }
            Button(onClick = { creating = true; editing = ChecklistQuestion(section = VisitStore.sections.first(), text = "") }) {
                Icon(Icons.Default.Add, null); Text("Agregar")
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            VisitStore.sections.forEach { section ->
                val sectionQuestions = questions.filter { it.section == section }
                item { Text(section, fontWeight = FontWeight.Bold, color = MassBlue, modifier = Modifier.padding(top = 10.dp, bottom = 3.dp)) }
                items(sectionQuestions, key = { it.id }) { question ->
                    val index = questions.indexOfFirst { it.id == question.id }
                    Card(shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(Modifier.padding(start = 10.dp, end = 4.dp, top = 6.dp, bottom = 6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = question.active, onCheckedChange = { checked ->
                                    questions[index] = question.copy(active = checked); persist()
                                })
                                Text(question.text, Modifier.weight(1f), fontSize = 13.sp)
                                IconButton(onClick = { editing = question; creating = false }) { Icon(Icons.Default.Edit, "Editar", modifier = Modifier.size(18.dp)) }
                                IconButton(onClick = { questions.remove(question); persist() }) { Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp)) }
                            }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(onClick = {
                                    val previous = (index - 1).coerceAtLeast(0)
                                    if (previous != index && questions[previous].section == section) {
                                        val item = questions.removeAt(index); questions.add(previous, item); persist()
                                    }
                                }, modifier = Modifier.size(36.dp)) { Icon(Icons.Default.MoveUp, "Subir", modifier = Modifier.size(18.dp)) }
                                IconButton(onClick = {
                                    val next = (index + 1).coerceAtMost(questions.lastIndex)
                                    if (next != index && questions[next].section == section) {
                                        val item = questions.removeAt(index); questions.add(next, item); persist()
                                    }
                                }, modifier = Modifier.size(36.dp)) { Icon(Icons.Default.MoveDown, "Bajar", modifier = Modifier.size(18.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
    editing?.let { question -> QuestionEditorDialog(question, creating, onDismiss = { editing = null }, onSave = { updated ->
        if (creating) questions.add(updated) else {
            val index = questions.indexOfFirst { it.id == question.id }
            if (index >= 0) questions[index] = updated
        }
        persist(); editing = null
    }) }
}

@Composable
private fun QuestionEditorDialog(question: ChecklistQuestion, creating: Boolean, onDismiss: () -> Unit, onSave: (ChecklistQuestion) -> Unit) {
    var text by remember(question.id) { mutableStateOf(question.text) }
    var section by remember(question.id) { mutableStateOf(question.section) }
    var menuOpen by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (creating) "Nueva pregunta" else "Editar pregunta") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Pregunta") }, modifier = Modifier.fillMaxWidth())
                Box {
                    OutlinedButton(onClick = { menuOpen = true }, modifier = Modifier.fillMaxWidth()) { Text(section) }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        VisitStore.sections.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { section = option; menuOpen = false }) }
                    }
                }
            }
        },
        confirmButton = { TextButton(enabled = text.isNotBlank(), onClick = { onSave(question.copy(section = section, text = text.trim())) }) { Text("Guardar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}


@Composable
private fun SelectionField(label:String, value:String, options:List<String>, enabled:Boolean=true, onSelect:(String)->Unit) {
    var open by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedButton(onClick={ if(enabled) open=true }, enabled=enabled, modifier=Modifier.fillMaxWidth()) {
            Text(if(value.isBlank()) if(enabled) "Seleccionar..." else "Seleccionar anterior primero..." else value, modifier=Modifier.weight(1f))
        }
        DropdownMenu(expanded=open, onDismissRequest={open=false}) {
            options.forEach { option -> DropdownMenuItem(text={Text(option)}, onClick={onSelect(option);open=false}) }
        }
    }
}

@Composable
private fun FormField(label: String, value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier, numeric: Boolean = false, singleLine: Boolean = true) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label) }, modifier = modifier.fillMaxWidth(),
        singleLine = singleLine, keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Decimal else KeyboardType.Text),
        shape = RoundedCornerShape(8.dp),
    )
}