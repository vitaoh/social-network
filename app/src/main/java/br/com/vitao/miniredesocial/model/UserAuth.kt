package br.com.vitao.miniredesocial.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Encapsula toda a lógica de autenticação com o FirebaseAuth.
 * As Activities devem chamar esta classe em vez de usar FirebaseAuth diretamente.
 */
class UserAuth {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    /** Usuário atualmente autenticado, ou null se não estiver logado */
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /** Retorna true se há um usuário autenticado */
    val isAuthenticated: Boolean
        get() = firebaseAuth.currentUser != null

    /** E-mail do usuário logado, ou null */
    val currentEmail: String?
        get() = firebaseAuth.currentUser?.email

    /** Realiza login com e-mail e senha */
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Erro desconhecido")
                }
            }
    }

    /** Cria uma nova conta com e-mail e senha */
    fun signUp(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Erro desconhecido")
                }
            }
    }

    /** Realiza logout */
    fun logout() {
        firebaseAuth.signOut()
    }
}