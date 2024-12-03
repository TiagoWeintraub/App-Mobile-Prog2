package ar.edu.um.programacion2


data class DispositivoCompleto (
    val dispositivo: Dispositivo,
    val personalizaciones: List<Personalizacion>,
    val adicionales: List<Adicional>,
    val caracteristicas: List<Caracteristica>
)
