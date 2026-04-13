package br.com.vitao.miniredesocial.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.vitao.miniredesocial.R
import br.com.vitao.miniredesocial.model.Post
import com.google.android.material.imageview.ShapeableImageView

class PostAdapter(private val posts: Array<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPerfil: ShapeableImageView = itemView.findViewById(R.id.imgPerfilItem)
        val tvUsername: TextView          = itemView.findViewById(R.id.tvUsernameItem)
        val imgPost: ImageView            = itemView.findViewById(R.id.imgPostItem)
        val tvDescricao: TextView         = itemView.findViewById(R.id.tvDescricaoItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.tvUsername.text = if (post.username.isNotEmpty()) "@${post.username}" else "Usuário"
        holder.imgPost.setImageBitmap(post.imagem)
        holder.tvDescricao.text = post.descricao

        if (post.fotoPerfilBitmap != null) {
            holder.imgPerfil.setImageBitmap(post.fotoPerfilBitmap)
        } else {
            holder.imgPerfil.setImageResource(R.drawable.empty_profile)
        }
    }

    override fun getItemCount(): Int = posts.size
}