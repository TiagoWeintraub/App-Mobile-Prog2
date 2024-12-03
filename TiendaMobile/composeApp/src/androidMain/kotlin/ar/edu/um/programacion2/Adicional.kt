package ar.edu.um.programacion2

import kotlinx.serialization.Serializable

@Serializable
data class Adicional(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val precioGratis: Double,
    val dispositivo: Dispositivo
)
