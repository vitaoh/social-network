package br.com.vitao.miniredesocial

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.vitao.miniredesocial.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val galeria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.imgProfile.setImageURI(uri)
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
        loadProfileData()
    }

    private fun loadProfileData() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.let { user ->
            val email = user.email ?: return

            val db = FirebaseFirestore.getInstance()
            db.collection("usuarios")
                .document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        binding.edtUser.setText(document.getString("username") ?: "")
                        binding.edtNome.setText(document.getString("nomeCompleto") ?: "")
                    } else {
                        binding.edtUser.setText("")
                        binding.edtNome.setText("")
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar perfil", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun setupClickListeners() {
        binding.btnAlterarFoto.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnSalvar.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            val email = firebaseAuth.currentUser!!.email.toString()
            val username = binding.edtUser.text.toString().trim()
            val nomeCompleto = binding.edtNome.text.toString().trim()

            if (username.isEmpty() || nomeCompleto.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return
            }

            val db = FirebaseFirestore.getInstance()
            val dados = hashMapOf(
                "nomeCompleto" to nomeCompleto,
                "username" to username
            )

            binding.btnSalvar.isEnabled = false

            db.collection("usuarios").document(email)
                .set(dados)
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar: ${it.message}", Toast.LENGTH_LONG).show()
                    binding.btnSalvar.isEnabled = true
                }
        } else {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
        }
    }

}


