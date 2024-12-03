package ar.edu.um.programacion2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class DataAssembler(private val endpoints: Endpoints) {

    suspend fun mapearModelos(): List<DispositivoCompleto> = withContext(Dispatchers.IO) {
        val dispositivos = endpoints.getDispositivos() // Lista de dispositivos
        val personalizaciones = endpoints.getPersonalizaciones() // Lista de personalizaciones
        val opciones = endpoints.getOpciones() // Lista de opciones para personalizaciones
        val adicionales = endpoints.getAdicionales() // Lista de adicionales
        val caracteristicas = endpoints.getCaracteristicas() // Lista de características

        println("Dispositivos recibidos: ${dispositivos.size}")
        println("Adicionales Detalles: $adicionales")
        println("Personalizaciones Detalles: $personalizaciones")
        println("Opciones Detalles: $opciones")


        dispositivos.map { dispositivo ->
            // Filtrar personalizaciones para este dispositivo
            val dispositivoPersonalizaciones = personalizaciones
                .filter { it.dispositivo?.id == dispositivo.id }
                .map { personalizacion ->
                    // Agregar opciones a cada personalización
                    personalizacion.copy(
                        opciones = opciones.filter { it.personalizacion.id == personalizacion.id }
                    )
                }

            // Filtrar adicionales y características
            val dispositivoAdicionales = adicionales.filter { it.dispositivo.id == dispositivo.id }
            val dispositivoCaracteristicas = caracteristicas.filter { it.dispositivo.id == dispositivo.id }

            // Crear el objeto con todos los datos correspondientes
            DispositivoCompleto(
                dispositivo = dispositivo,
                personalizaciones = dispositivoPersonalizaciones,
                adicionales = dispositivoAdicionales,
                caracteristicas = dispositivoCaracteristicas
            )
        }




    }

}






