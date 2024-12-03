package ar.edu.um.programacion2

import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ar.edu.um.programacion2.KtorClient
import ar.edu.um.programacion2.Endpoints
import ar.edu.um.programacion2.Dispositivo
import ar.edu.um.programacion2.DispositivoCompleto
import ar.edu.um.programacion2.Tema
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import kotlinx.coroutines.GlobalScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import androidx.lifecycle.lifecycleScope


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tema {
                Surface (
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                    )
                {
                    vistaListadoDispositivos()
                }
            }
        }
    }

    fun realizarVenta(
        dispositivoCompleto: DispositivoCompleto,
        selectedOpciones: Map<String, Opcion>,
        selectedAdicionales: Map<Int, Boolean>,
        totalPrice: Double,
        onVentaExitosa: () -> Unit,
        onError: (String) -> Unit
    ) {
        val httpClient = KtorClient.client

        val venta = Venta(
            idDispositivo = dispositivoCompleto.dispositivo.id,
            personalizaciones = dispositivoCompleto.personalizaciones.map { personalizacion ->
                val opcionSeleccionada = selectedOpciones[personalizacion.nombre]
                    ?: personalizacion.opciones.first() // Selecciona la primera opción si no hay una seleccionada
                PersonalizacionVenta(
                    id = personalizacion.id,
                    precio = opcionSeleccionada.precioAdicional,
                    opcion = OpcionVenta(id = opcionSeleccionada.id)
                )
            },
            adicionales = selectedAdicionales.filterValues { it }.keys.map { id ->
                val adicional = dispositivoCompleto.adicionales.first { it.id == id }
                AdicionalVenta(
                    id = adicional.id,
                    precio = adicional.precio
                )
            },
            precioFinal = totalPrice
        )

        // Usar lifecycleScope para hacer la solicitud de red
        lifecycleScope.launch {
            try {
                val response: HttpResponse = withContext(Dispatchers.IO) {
                    httpClient.post("http://10.0.2.2:8080/api/registrar-venta") {
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToString(venta))
                    }
                }

                if (response.status == HttpStatusCode.OK) {
                    withContext(Dispatchers.Main) {
                        onVentaExitosa()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onError("Error al realizar la venta: ${response.status}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError("Error al realizar la venta: ${e.message}")
                }
            }
        }
    }
}

@Composable
fun vistaListadoDispositivos() {
    val endpoints = remember { Endpoints(KtorClient.client) }
    val assembler = remember { DataAssembler(endpoints) }
    val mapearDispositivos = remember { mutableStateOf<List<DispositivoCompleto>>(emptyList()) }
    val selectedDispositivo = remember { mutableStateOf<DispositivoCompleto?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            // Realizar solicitud de red en el despachador IO
            val dispositivosMapeados = assembler.mapearModelos()
            mapearDispositivos.value = dispositivosMapeados
        }
        catch (e: Exception) {
            errorMessage.value = "Error al cargar los dispositivos: ${e.message}"
        }
        finally {
            isLoading.value = false
        }
    }


    if (selectedDispositivo.value != null) {
        vistaVentaDispositivo(
            dispositivoCompleto = selectedDispositivo.value!!,
            onBack = { selectedDispositivo.value = null },
            onVentaExitosa = {
                println("Compra realizada con éxito")
                selectedDispositivo.value = null
            },
            onError = { mensaje ->
                println("Error durante la compra: $mensaje")
            }
        )
    } else if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (errorMessage.value != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMessage.value ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Box {
                Text(
                    text = "TIENDA DE COMPUTADORAS",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            ListaDispositivos(
                dispositivos = mapearDispositivos.value,
                onDispositivoSelected = { selectedDispositivo.value = it }
            )
        }
    }
}

@Composable
fun ListaDispositivos(
    dispositivos: List<DispositivoCompleto>,
    onDispositivoSelected: (DispositivoCompleto) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(dispositivos) { dispositivoCompleto ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onDispositivoSelected(dispositivoCompleto) },
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = dispositivoCompleto.dispositivo.nombre  ?: "No disponible", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = dispositivoCompleto.dispositivo.descripcion  ?: "No disponible", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Precio: ${dispositivoCompleto.dispositivo.precioBase} ${dispositivoCompleto.dispositivo.moneda}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun vistaVentaDispositivo(
    dispositivoCompleto: DispositivoCompleto,
    onBack: () -> Unit,
    onVentaExitosa: () -> Unit,
    onError: (String) -> Unit
) {
    println("Adicionales recibidos: ${dispositivoCompleto.adicionales}")
    val selectedOpciones = remember { mutableStateMapOf<String, Opcion>() }
    val selectedAdicionales = remember { mutableStateMapOf<Int, Boolean>() }
    var mostrarDialogoExito by remember { mutableStateOf(false) }

    val precioDispositivoConPersonalizacionesAndOpciones by remember {
        derivedStateOf {
            (dispositivoCompleto.dispositivo.precioBase ?: 0.0) + selectedOpciones.values.sumOf { it.precioAdicional }
        }
    }

    val precioTotal by remember {
        derivedStateOf {
            val adicionalPrice = selectedAdicionales.filterValues { it }.keys.sumOf { id ->
                val adicional = dispositivoCompleto.adicionales.firstOrNull { it.id == id }
                if (adicional != null && adicional.precioGratis != -1.0 &&
                    precioDispositivoConPersonalizacionesAndOpciones >= adicional.precioGratis
                ) {
                    println("Adicional en promoción: ${adicional.nombre}")
                    0.0
                } else {
                    adicional?.precio ?: 0.0
                }
            }
            precioDispositivoConPersonalizacionesAndOpciones + adicionalPrice
        }
    }

    var isProcessingVenta by remember { mutableStateOf(false) }

    if (mostrarDialogoExito) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoExito = false
                onVentaExitosa()
            },
            title = { Text("¡Venta Exitosa!") },
            text = { Text("La venta se ha realizado correctamente.") },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogoExito = false
                    onVentaExitosa()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { onBack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Volver a la lista")
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            item {
                Text(text = dispositivoCompleto.dispositivo.nombre ?: "No disponible", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = dispositivoCompleto.dispositivo.descripcion ?: "No disponible", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Características:", style = MaterialTheme.typography.titleLarge)
                dispositivoCompleto.caracteristicas.forEach { caracteristica ->
                    Text(text = "- ${caracteristica.nombre}: ${caracteristica.descripcion}")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Personalizaciones:", style = MaterialTheme.typography.titleLarge)
                dispositivoCompleto.personalizaciones.forEach { personalizacion ->
                    seleccionarPersonalizacion(personalizacion, selectedOpciones)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                mostrarListadoAdicionales(
                    adicionales = dispositivoCompleto.adicionales,
                    selectedAdicionales = selectedAdicionales,
                    basePlusPersonalizations = precioDispositivoConPersonalizacionesAndOpciones
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Precio Total: $${String.format("%.2f", precioTotal)}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (isProcessingVenta) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    val mainActivity = MainActivity()
                    mainActivity.realizarVenta(
                        dispositivoCompleto = dispositivoCompleto,
                        selectedOpciones = selectedOpciones,
                        selectedAdicionales = selectedAdicionales,
                        totalPrice = precioTotal,
                        onVentaExitosa = {
                            isProcessingVenta = false
                            mostrarDialogoExito = true
                        },
                        onError = { mensaje: String ->
                            isProcessingVenta = false
                            onError(mensaje)
                        }
                    )
                }, modifier = Modifier.weight(1f)) {
                    Text("Comprar")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { onBack() }, modifier = Modifier.weight(1f)) {
                    Text("Cancelar")
                }
            }
        }
    }
}


@Composable
fun seleccionarPersonalizacion(
    personalizacion: Personalizacion,
    selectedOpciones: MutableMap<String, Opcion>
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = personalizacion.nombre  ?: "No disponible", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        personalizacion.opciones.forEach { opcion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedOpciones[personalizacion.nombre ?: "No disponible"] = opcion
                    }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically // Centrar verticalmente
            ) {
                RadioButton(
                    selected = selectedOpciones[personalizacion.nombre] == opcion,
                    onClick = {
                        selectedOpciones[personalizacion.nombre ?: "No disponible"] = opcion
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${opcion.nombre} (+$${opcion.precioAdicional})")
            }
        }
    }
}


@Composable
fun seleccionarAdicional(
    adicional: Adicional,
    selectedAdicionales: MutableMap<Int, Boolean>,
    basePlusPersonalizations: Double
) {
    val conPrecioGratis = adicional.precioGratis != -1.0 && basePlusPersonalizations >= adicional.precioGratis

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val currentSelection = selectedAdicionales[adicional.id] ?: false
                selectedAdicionales[adicional.id] = !currentSelection
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = selectedAdicionales[adicional.id] ?: false,
            onCheckedChange = {
                selectedAdicionales[adicional.id] = it
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = adicional.nombre,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = if (conPrecioGratis) {
                    "Precio: Gratis (Llevalo de regalo con tu compra!)"
                } else {
                    "Precio: $${String.format("%.2f", adicional.precio)}"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun mostrarListadoAdicionales(
    adicionales: List<Adicional>,
    selectedAdicionales: MutableMap<Int, Boolean>,
    basePlusPersonalizations: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "Adicionales:", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(adicionales) { adicional ->
                seleccionarAdicional(
                    adicional = adicional,
                    selectedAdicionales = selectedAdicionales,
                    basePlusPersonalizations = basePlusPersonalizations
                )
            }
        }
    }
}




