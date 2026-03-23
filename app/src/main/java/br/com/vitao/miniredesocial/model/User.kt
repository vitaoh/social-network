package br.com.vitao.miniredesocial.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val username: String?,
    val nomeCompleto: String?,
    val fotoPerfil: String? = null  // Base64 para imagem
)
