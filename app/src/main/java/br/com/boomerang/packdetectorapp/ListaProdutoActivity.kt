package br.com.boomerang.packdetectorapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.com.boomerang.packdetectorapp.domain.Identificador
import br.com.boomerang.packdetectorapp.domain.Produto
import br.com.boomerang.packdetectorapp.service.PackbackService
import br.com.boomerang.packdetectorapp.service.ProdutoService
import br.com.boomerang.packdetectorapp.ui.adapter.ProdutoAdapter
import kotlinx.android.synthetic.main.activity_lista_produto.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaProdutoActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ListaProdutoActivity"
        private const val defaultTitle = "Produtos"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_produto)
        title = defaultTitle
    }

    private fun buscaProdutos(identificador: Identificador) {
        PackbackService().cria(ProdutoService::class.java)?.let { service ->
            service.getProdutosPorDescricao(identificador).enqueue(
                object : Callback<List<Produto>> {

                override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
                    Log.e(TAG, "Erro ao buscar produtos", t)
                }

                override fun onResponse(
                        call: Call<List<Produto>>,
                        response: Response<List<Produto>>
                ) {
                    response.body()?.let { produtos ->
                        Log.d(TAG, "Produtos encontrados $produtos")
                        this@ListaProdutoActivity.title = parseTitle(produtos)
                        lista_produto.adapter = ProdutoAdapter(this@ListaProdutoActivity, produtos)
                    }
                }

            })
        }
    }

    private fun parseTitle(produtos: List<Produto>): String {
        if(produtos.isEmpty()) return defaultTitle

        return "$defaultTitle - Setor de ${produtos.first().regiao.setor}"
    }
}
