package br.com.boomerang.packdetectorapp

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import br.com.boomerang.packdetectorapp.domain.Identificador
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
        private const val AUTHORITY = "br.com.boomerang.packdetectorapp.fileprovider"
        private const val CLARIFAI_API_KEY = "fa402aa874a74644843c29f5ac353a7f"
        private const val REQ_TIRA_FOTO = 1
    }

    private var localFoto: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_tira_foto.setOnClickListener {
            captura()
        }
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
            return
        }

        val uri = FileProvider.getUriForFile(this, AUTHORITY, foto)

        intentFoto.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        startActivityForResult(intentFoto, REQ_TIRA_FOTO)
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
        try {
            val resultados = buscaResultados(foto)

            val tags = resultados
                .flatMap { it.data() }
                .mapNotNull { it.name() }

            val identificador = Identificador(tags = tags)


        }
        catch (e: Exception) {
            Log.e(TAG, "Erro ao tentar enviar foto", e)
        }
        finally {
            apagaArquivo()
        }

    }

    private fun buscaResultados(foto: File): List<ClarifaiOutput<Concept>> {
        return ClarifaiBuilder(CLARIFAI_API_KEY)
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