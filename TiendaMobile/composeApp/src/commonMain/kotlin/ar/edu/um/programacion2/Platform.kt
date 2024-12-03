package ar.edu.um.programacion2

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform