package ar.edu.um.programacion2

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class Endpoints(private val client: HttpClient) {

    suspend fun getDispositivos(): List<Dispositivo> {
        return client.get("api/dispositivos").body()
    }

    suspend fun getPersonalizaciones(): List<Personalizacion> {
        return client.get("api/personalizacions").body()
    }

    suspend fun getAdicionales(): List<Adicional> {
        return client.get("api/adicionals").body()
    }

    suspend fun getCaracteristicas(): List<Caracteristica> {
        return client.get("api/caracteristicas").body()
    }

    suspend fun getOpciones(): List<Opcion> {
        return client.get("api/opcions").body()
    }

    suspend fun realizarVenta(venta: Venta): VentaResponse {
        return client.post("api/registrar-venta") {
            contentType(ContentType.Application.Json) // Especifica el tipo de contenido
            setBody(venta) // Establece el cuerpo de la solicitud
        }.body()
    }
}
