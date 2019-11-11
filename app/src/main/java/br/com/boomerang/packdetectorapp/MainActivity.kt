package br.com.boomerang.packdetectorapp

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import clarifai2.api.ClarifaiBuilder
import clarifai2.dto.input.ClarifaiInput
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
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

        val authority = "br.com.boomerang.packdetectorapp.fileprovider"
        val uri = FileProvider.getUriForFile(this, authority, foto)

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
        val resultados = ClarifaiBuilder("fa402aa874a74644843c29f5ac353a7f")
            .buildSync()
            .defaultModels
            .generalModel()
            .predict()
            .withInputs(ClarifaiInput.forImage(foto))
            .executeSync()
            .get()

        val contemXicara = resultados
            .flatMap { it.data() }
            .any { it.name() == "cup" }

        Log.d(TAG, "Contém xícara: $contemXicara")

        apagaArquivo()
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
