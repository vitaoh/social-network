package br.com.vitao.miniredesocial.model

import android.graphics.Bitmap

data class Post(
    val descricao: String,
    val imagem: Bitmap,
    val username: String = "",
    val fotoPerfilBitmap: Bitmap? = null
)