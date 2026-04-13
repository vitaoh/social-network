package br.com.vitao.miniredesocial.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.vitao.miniredesocial.databinding.ActivityAddPostBinding
import br.com.vitao.miniredesocial.util.Base64Converter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddPostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPostBinding

    private val galeria = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            binding.imgPost.setImageURI(uri)
        } else {
            Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSelecionarFoto.setOnClickListener {
            galeria.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnPublicar.setOnClickListener {
            publicarPost()
        }

        binding.btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun publicarPost() {
        val descricao = binding.edtDescricao.text.toString().trim()

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Escreva uma descrição para o post", Toast.LENGTH_SHORT).show()
            return
        }

        val imageString = try {
            Base64Converter.drawableToString(binding.imgPost.drawable)
        } catch (e: Exception) {
            Toast.makeText(this, "Selecione uma imagem para o post", Toast.LENGTH_SHORT).show()
            return
        }

        val firebaseAuth = FirebaseAuth.getInstance()
        val emailAutor = firebaseAuth.currentUser?.email ?: run {
            Toast.makeText(this, "Usuário não logado", Toast.LENGTH_SHORT).show()
            return
        }

        binding.btnPublicar.isEnabled = false

        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").document(emailAutor).get()
            .addOnSuccessListener { document ->
                val username   = document.getString("username") ?: ""
                val fotoPerfil = document.getString("fotoPerfil") ?: ""

                val post = hashMapOf(
                    "descricao"   to descricao,
                    "imageString" to imageString,
                    "emailAutor"  to emailAutor,
                    "username"    to username,
                    "fotoAutor"   to fotoPerfil,
                    "timestamp"   to com.google.firebase.Timestamp.now()
                )

                db.collection("posts").add(post)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Post publicado!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao publicar: ${e.message}", Toast.LENGTH_LONG).show()
                        binding.btnPublicar.isEnabled = true
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar perfil do usuário", Toast.LENGTH_SHORT).show()
                binding.btnPublicar.isEnabled = true
            }
    }
}