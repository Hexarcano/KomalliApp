package mx.uv.komalliapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import mx.uv.komalliapp.databinding.ActivityNotaBinding

class ActivityNota : AppCompatActivity() {

    private lateinit var binding: ActivityNotaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityNotaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aplicar padding según los insets de las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.brMenuS) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar OnClickListener para el botón btAgregarNota
        binding.btAgregarNota.setOnClickListener {
            val nota = binding.inputNota.text.toString()
            if (nota.isNotEmpty()) {
                val intent = Intent()
                intent.putExtra("nota", nota)
                setResult(Activity.RESULT_OK, intent)
                finish() // Finalizar la actividad actual
            } else {
                Toast.makeText(this, "No se recibió ninguna nota", Toast.LENGTH_LONG).show()
            }
        }
    }
}
