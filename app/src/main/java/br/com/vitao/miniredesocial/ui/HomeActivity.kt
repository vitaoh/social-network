package br.com.vitao.miniredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.vitao.miniredesocial.adapter.PostAdapter
import br.com.vitao.miniredesocial.databinding.ActivityHomeBinding
import br.com.vitao.miniredesocial.model.Post
import br.com.vitao.miniredesocial.util.Base64Converter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var posts = ArrayList<Post>()
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Redireciona para login se não houver usuário autenticado
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
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

    // Recarrega o feed ao voltar de AddPostActivity
    override fun onResume() {
        super.onResume()
        carregarFeed()
    }

    private fun setupUI() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.let { user ->
            val email = user.email ?: return@let

            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(email)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val username = document.getString("username") ?: "Usuário"
                        binding.tvWelcome.text  = "Olá, $username!"
                        binding.tvUserEmail.text = user.email
                        binding.tvUsername.text  = "@$username"
                    } else {
                        binding.tvWelcome.text  = "Olá, ${user.email}"
                        binding.tvUserEmail.text = user.email ?: "Email não disponível"
                    }
                }
                .addOnFailureListener {
                    binding.tvWelcome.text  = "Olá, ${user.email}"
                    binding.tvUserEmail.text = user.email ?: "Email não disponível"
                }
        }
    }

    private fun setupListeners() {
        // Navega para edição de perfil
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // ✅ NOVO — Botão para adicionar post: abre AddPostActivity
        binding.btnAddPost.setOnClickListener {
            startActivity(Intent(this, AddPostActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        // Carrega o feed manualmente pelo botão
        binding.btnCarregarFeed.setOnClickListener {
            carregarFeed()
        }

        // Logout
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logout realizado!", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun carregarFeed() {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts").get()
            .addOnSuccessListener { result ->
                posts = ArrayList()
                for (document in result.documents) {
                    val imageString = document.data?.get("imageString")?.toString() ?: continue
                    val descricao   = document.data?.get("descricao")?.toString() ?: ""
                    val bitmap      = Base64Converter.stringToBitmap(imageString)
                    posts.add(Post(descricao, bitmap))
                }
                adapter = PostAdapter(posts.toTypedArray())
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar feed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}