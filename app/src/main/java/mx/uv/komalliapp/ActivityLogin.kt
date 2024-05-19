package mx.uv.komalliapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.koushikdutta.ion.Ion
import mx.uv.komalliapp.databinding.ActivityLoginBinding
import mx.uv.komalliapp.models.DatosLogin
import mx.uv.komalliapp.models.DatosSesionRespuesta
import mx.uv.komalliapp.requests.PeticionHTTP

class ActivityLogin : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Ion.getDefault(this@ActivityLogin).conscryptMiddleware.enable(false)

        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnSesion.setOnClickListener {
            val usuario = binding.etUsuario.text.toString()
            val contrasenia = binding.etContrasenia.text.toString()

            if (validarDatos(usuario, contrasenia)) {
                val datosLogin = DatosLogin(usuario, contrasenia)

                PeticionHTTP.peticionPOST(
                    this@ActivityLogin,
                    datosLogin,
                    DatosSesionRespuesta::class.java,
                    "api/cliente/login"
                ) { exito, respuesta ->
                    val datos = respuesta as DatosSesionRespuesta

                    if (datos.accessToken.isNullOrBlank()) {
                        Toast.makeText(this@ActivityLogin, datos.mensaje, LENGTH_LONG).show()
                    }

                    if(exito && !datos.accessToken.isNullOrBlank()) {
                        cambiarPantalla(datos)
                    }
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validarDatos(usuario: String, contrasenia: String): Boolean {
        if (usuario.isBlank() || contrasenia.isBlank()) {
            return false;
        }

        return true;
    }

    private fun cambiarPantalla(datosSesionRespuesta: DatosSesionRespuesta) {
        val intent = Intent(this, ActivityMenu::class.java)
        val authHeader = "${datosSesionRespuesta.tokenType} ${datosSesionRespuesta.accessToken}"

        intent.putExtra("authHeader", authHeader)
        startActivity(intent)
    }
}