package br.com.vitao.miniredesocial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.vitao.miniredesocial.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.let { user ->
            val email = user.email ?: return@let

            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios")
                .document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username") ?: "Usuário"
                        val nomeCompleto = document.getString("nomeCompleto") ?: email

                        binding.tvWelcome.text = "Olá, $username!"
                        binding.tvUserEmail.text = user.email
                        binding.tvUsername.text = "@$username"
                    } else {
                        binding.tvWelcome.text = "Olá, ${user.email}"
                        binding.tvUserEmail.text = user.email ?: "Email não disponível"
                    }
                }
                .addOnFailureListener {
                    binding.tvWelcome.text = "Olá, ${user.email}"
                    binding.tvUserEmail.text = user.email ?: "Email não disponível"
                }
        }
    }

    private fun setupListeners() {
        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logout realizado!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
