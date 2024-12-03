package ar.edu.um.programacion2

import kotlinx.serialization.Serializable

@Serializable
data class Dispositivo(
    val id: Int,
    val nombre: String?,
    val descripcion: String?,
    val precioBase: Double?,
    val moneda: String?
)