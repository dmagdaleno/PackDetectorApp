package br.com.boomerang.packdetectorapp.domain

import java.io.Serializable

data class Identificador (
    val tags: List<String> = emptyList()
): Serializable