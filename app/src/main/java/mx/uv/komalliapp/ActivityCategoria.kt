package mx.uv.komalliapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import mx.uv.komalliapp.databinding.ActivityCategoriaBinding
import mx.uv.komalliapp.models.CategoriaProductoRespuesta
import mx.uv.komalliapp.models.ProductoRespuesta
import mx.uv.komalliapp.requests.PeticionHTTP
import mx.uv.komalliapp.utils.ProductoRVAdapter

class ActivityCategoria : AppCompatActivity() {
    private lateinit var binding: ActivityCategoriaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val categoriaId = intent.getIntExtra("categoriaId", 0)
        val sharedPreferences = this.getSharedPreferences("sesion", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        PeticionHTTP.peticionGET(
            this@ActivityCategoria,
            "",
            CategoriaProductoRespuesta::class.java,
            "api/CategoriaProducto/${categoriaId}",
            token
        ) { exito, result ->
            val datos = result as CategoriaProductoRespuesta

            binding.tvNombreCategoria.text = datos.categorias[0].nombre
        }

        PeticionHTTP.peticionGET(
            this@ActivityCategoria,
            "",
            ProductoRespuesta::class.java,
            "api/Producto/Categoria/${categoriaId}",
            token
        ) { exito, result ->
            val datos = result as ProductoRespuesta

            val linearLayoutManager =
                LinearLayoutManager(this@ActivityCategoria, LinearLayoutManager.VERTICAL, false)

            val adapter = ProductoRVAdapter(datos.productos)

            binding.rvProductos.layoutManager = linearLayoutManager
            binding.rvProductos.adapter = adapter
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}