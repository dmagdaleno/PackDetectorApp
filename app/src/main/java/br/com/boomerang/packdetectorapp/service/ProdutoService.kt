package br.com.boomerang.packdetectorapp.service

import br.com.boomerang.packdetectorapp.domain.Identificador
import br.com.boomerang.packdetectorapp.domain.Produto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProdutoService {

    @POST("produtos/identifica")
    fun buscaProdutos(@Body identificador: Identificador): Call<List<Produto>>

    @GET("produtos/regiao/{id}")
    fun getProdutosPorRegiao(@Path("id") id: Long): Call<List<Produto>>
}