package br.com.boomerang.packdetectorapp

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val REQ_TIRA_FOTO = 1
    }

    private lateinit var localFoto: String

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

    }
}
