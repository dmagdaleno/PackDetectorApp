package br.com.boomerang.packdetectorapp.service

import br.com.boomerang.packdetectorapp.domain.Identificador
import br.com.boomerang.packdetectorapp.domain.Produto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ProdutoService {

    @POST("produtos/identifica")
    fun getProdutosPorDescricao(@Body identificador: Identificador): Call<List<Produto>>
}