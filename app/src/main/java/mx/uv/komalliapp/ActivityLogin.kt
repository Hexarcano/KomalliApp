package mx.uv.komalliapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ActivityLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val bt_inicio = findViewById<Button>(R.id.bt_sesion)
        val texUsuario = findViewById<EditText>(R.id.tx_usuario)
        val texPass = findViewById<EditText>(R.id.tx_password)

        bt_inicio.setOnClickListener{
            val usuario = texUsuario.text.toString()
            val pass = texPass.text.toString()

            if(usuario == "juli"){
                if(pass == "juli"){
                    val intent = Intent(this, ActivityMenu::class.java)
                    intent.putExtra("usuario",usuario)
                    startActivity(intent)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}