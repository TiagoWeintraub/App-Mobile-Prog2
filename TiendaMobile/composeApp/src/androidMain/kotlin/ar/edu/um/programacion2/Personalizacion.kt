package ar.edu.um.programacion2

import android.view.Display
import kotlinx.serialization.Serializable

@Serializable
data class Personalizacion(
    val id: Int,
    val nombre: String?,
    val descripcion: String?,
    val opciones: List<Opcion> = emptyList(),
    val dispositivo: Dispositivo?
)