package ar.edu.um.programacion2

import kotlinx.serialization.Serializable

@Serializable
data class Venta(
    val idDispositivo: Int,
    val personalizaciones: List<PersonalizacionVenta>,
    val adicionales: List<AdicionalVenta>,
    val precioFinal: Double
)

@Serializable
data class PersonalizacionVenta(
    val id: Int,
    val precio: Double,
    val opcion: OpcionVenta
)

@Serializable
data class AdicionalVenta(
    val id: Int,
    val precio: Double
)

@Serializable
data class OpcionVenta(
    val id: Int
)

@Serializable
data class VentaResponse(
    val success: Boolean,
    val message: String? = null
)