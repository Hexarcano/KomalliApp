package mx.uv.komalliapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import mx.uv.komalliapp.databinding.ActivityMenuBinding
import mx.uv.komalliapp.models.CategoriaProductoRespuesta
import mx.uv.komalliapp.requests.PeticionHTTP
import mx.uv.komalliapp.utils.CategoriaRVAdapter

class ActivityMenu : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = this.getSharedPreferences("sesion", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        PeticionHTTP.peticionGET(
            this@ActivityMenu,
            "",
            CategoriaProductoRespuesta::class.java,
            "api/CategoriaProducto",
            token
        ) { exito, result ->

            val datos = result as CategoriaProductoRespuesta

            val linearLayoutManager =
                LinearLayoutManager(this@ActivityMenu, LinearLayoutManager.HORIZONTAL, false)

            val adapter = CategoriaRVAdapter(datos.categorias) { categoria ->
                val intent = Intent(this, ActivityCategoria::class.java)
                intent.putExtra("categoriaId", categoria.id)
                startActivity(intent)
            }
            binding.rvCategorias.layoutManager = linearLayoutManager
            binding.rvCategorias.adapter = adapter
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.menu)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

