package br.com.boomerang.packdetectorapp.domain

data class Produto(
    val id: Long = 0,
    val embalagem: Embalagem = Embalagem(),
    val regiao: Regiao = Regiao(),
    val descricao: String = "indefinido",
    val valor: Double = .0
)