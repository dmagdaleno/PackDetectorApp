package br.com.boomerang.packdetectorapp

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import br.com.boomerang.packdetectorapp.domain.Identificador
import br.com.boomerang.packdetectorapp.util.Keys
import clarifai2.api.ClarifaiBuilder
import clarifai2.dto.input.ClarifaiInput
import clarifai2.dto.model.output.ClarifaiOutput
import clarifai2.dto.prediction.Concept
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val defaultTitle = "Produtos"
    }

    private var localFoto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = defaultTitle

        btn_tira_foto.setOnClickListener { captura() }
    }

    private fun captura() {
        val intentFoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        intentFoto.resolveActivity(packageManager) ?: return

        val foto = try {
            val arquivoFoto = File.createTempFile("snapshot", ".jpg", filesDir)
            localFoto = arquivoFoto.absolutePath
            arquivoFoto
        }
        catch (e: IOException) {
            Log.e(TAG, "Erro ao salvar foto", e)
            atualizaMensagem(getString(R.string.erro_foto))
            return
        }

        val uri = FileProvider.getUriForFile(this, Keys.AUTHORITY, foto)

        intentFoto.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        startActivityForResult(intentFoto, Keys.REQ_TIRA_FOTO)
    }

    override fun onResume() {
        super.onResume()

        if(localFoto.isNotBlank()) {
            GlobalScope.launch {
                enviaFoto(File(localFoto))
            }
        }
    }

    private fun enviaFoto(foto: File) {
        atualizaMensagem(getString(R.string.enviando_foto))
        try {
            val resultados = buscaResultados(foto)

            atualizaMensagem(getString(R.string.foto_analisada))

            val tags = resultados
                .flatMap { it.data() }
                .mapNotNull { it.name() }

            val identificador = Identificador(tags = tags)

            val intent = Intent(this, ListaProdutoActivity::class.java)
                .apply { putExtra(Keys.IDENTIFICADOR, identificador) }

            startActivity(intent)

        }
        catch (e: Exception) {
            Log.e(TAG, "Erro ao tentar enviar foto", e)
            atualizaMensagem(getString(R.string.erro_foto))
        }
        finally {
            apagaArquivo()
        }

    }

    private fun atualizaMensagem(mensagem: String) {
        runOnUiThread { txt_info.text = mensagem }
    }

    private fun buscaResultados(foto: File): List<ClarifaiOutput<Concept>> {
        return ClarifaiBuilder(Keys.CLARIFAI_API_KEY)
            .buildSync()
            .defaultModels
            .generalModel()
            .predict()
            .withInputs(ClarifaiInput.forImage(foto))
            .executeSync()
            .get()
    }

    private fun apagaArquivo() {
        try {
            File(localFoto).delete()
            localFoto = ""
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao apagar arquivo", e)
        }
    }
}