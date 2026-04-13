package br.com.vitao.miniredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.vitao.miniredesocial.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import android.util.Patterns

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFirebase()
        setupListeners()
    }

    private fun setupFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        checkUserStatus()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            autenticarUsuario()
        }

        binding.btnCreateUser.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun checkUserStatus() {
        if (firebaseAuth.currentUser != null) {
            irParaHome()
        }
    }

    private fun autenticarUsuario() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtSenha.text.toString().trim()

        when {
            email.isEmpty() -> {
                binding.edtEmail.error = "Email obrigatório"
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.edtEmail.error = "Email inválido"
                return
            }
            password.isEmpty() -> {
                binding.edtSenha.error = "Senha obrigatória"
                return
            }
            password.length < 6 -> {
                binding.edtSenha.error = "Mínimo 6 caracteres"
                return
            }
        }

        binding.btnLogin.isEnabled = false
        binding.btnLogin.text = "Entrando..."

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Restaura botão
                binding.btnLogin.isEnabled = true
                binding.btnLogin.text = "Entrar"

                if (task.isSuccessful) {
                    Toast.makeText(this, "Bem-vindo!", Toast.LENGTH_SHORT).show()
                    irParaHome()
                } else {
                    // Mensagens de erro específicas
                    val erro = when {
                        task.exception?.message?.contains("no user") == true ->
                            "Usuário não encontrado"
                        task.exception?.message?.contains("wrong-password") == true ->
                            "Senha incorreta"
                        task.exception?.message?.contains("network") == true ->
                            "Sem conexão com internet"
                        else ->
                            "Erro: ${task.exception?.message}"
                    }
                    Toast.makeText(this, erro, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun irParaHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        checkUserStatus()
    }
}
