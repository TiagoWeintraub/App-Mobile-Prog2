package ar.edu.um.programacion2

import io.ktor.client.*
//import io.ktor.client.engine.android.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json

object KtorClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Token de autenticación
    private const val TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTczNTc0MTQ3M30.Kg1YmzvlpFgQ8W49gYesNWcBElQBNEDY13G7L8VjXqn_YU2DV0GFmeGvESHPkQDr6ARoh1teNwBq_sl9t08_uA"

    val client = HttpClient() {
        // Plugin para serialización JSON con kotlinx.serialization
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true // Ignora claves desconocidas en la respuesta
                    isLenient = true         // Permite valores no estrictos
                    encodeDefaults = false    // Codifica valores predeterminados
                }
            )
        }

        // Configuración del logger para depuración
        install(Logging) {
            level = LogLevel.BODY // Nivel de detalle del log (BODY incluye solicitudes y respuestas)
        }

        // Configuración global de tiempos de espera
        install(HttpTimeout) {
            requestTimeoutMillis = 30000 // Tiempo de espera para solicitudes (30 segundos)
            connectTimeoutMillis = 30000 // Tiempo de espera al conectar (30 segundos)
            socketTimeoutMillis = 30000  // Tiempo de espera del socket (30 segundos)
        }

        // Configuración de solicitudes predeterminadas
        defaultRequest {
            url(BASE_URL) // URL base para todas las solicitudes
            headers {
                append(HttpHeaders.Authorization, "Bearer $TOKEN") // Agrega el token en el header
                append(HttpHeaders.ContentType, ContentType.Application.Json.toString()) // Especifica JSON como tipo de contenido
            }
        }
    }
}
