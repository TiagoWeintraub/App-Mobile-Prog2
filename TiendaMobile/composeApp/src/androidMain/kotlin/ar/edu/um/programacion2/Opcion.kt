package ar.edu.um.programacion2

import kotlinx.serialization.Serializable

@Serializable
data class Opcion(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val precioAdicional: Double,
    val personalizacion: Personalizacion
)