package ar.edu.um.programacion2.views.compras


import androidx.lifecycle.ViewModel


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class venta : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

//    private val _password = MutableStateFlow("")
//    val password: StateFlow<String> = _password
//
//    private val _loginEnable = MutableStateFlow(false)
//    val loginEnable: StateFlow<Boolean> = _loginEnable
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
//
//
//    fun onLoginChanged(email: String, password: String) {
//        _email.value = email
//        _password.value = password
//        _loginEnable.value = isValidEmail(email) && isValidPassword(password)
//    }
//
//    private fun isValidPassword(password: String): Boolean = password.length > 8
//
//    private fun isValidEmail(email: String): Boolean = emailPattern.matches(email)
//
//    suspend fun onLoginSelected(): Boolean {
//        _isLoading.value = true
//        // Simulación de una llamada de inicio de sesión
//        delay(2000)  // Simula un tiempo de espera para la llamada
//        _isLoading.value = false
//
//        return email.value == "user@example.com" && password.value == "password123"  // Simula un login exitoso
//    }
}