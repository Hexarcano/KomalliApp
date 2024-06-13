package mx.uv.komalliapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import mx.uv.komalliapp.adapters.ProductoAdapter
import mx.uv.komalliapp.databinding.ActivityCategoriaBinding
import mx.uv.komalliapp.models.CategoriaProductoRespuesta
import mx.uv.komalliapp.models.ParcelableProducto
import mx.uv.komalliapp.models.Producto
import mx.uv.komalliapp.models.ProductoRespuesta
import mx.uv.komalliapp.models.toParcelable
import mx.uv.komalliapp.models.toProducto
import mx.uv.komalliapp.requests.PeticionHTTP
import mx.uv.komalliapp.utils.ProductoRVAdapter

class ActivityCategoria : AppCompatActivity(), ProductoAdapter.OnItemClickListener {
    private lateinit var binding: ActivityCategoriaBinding
    private lateinit var productoAdapter: ProductoAdapter
    private var contadorCarrito: Int = 0
    private var cantidadCarrito: Int = 0
    private val productosSeleccionados: MutableList<Producto> = mutableListOf()
    private var precioTotalCarrito: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCategoriaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener datos pasados por intent
        obtenerDatosDesdeIntent()

        // Actualizar contador de carrito en la interfaz de usuario
        actualizarCarrito()

        val categoriaId = intent.getIntExtra("categoriaId", 0)
        val sharedPreferences = this.getSharedPreferences("sesion", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        // Realizar solicitud HTTP para obtener datos de categoría
        PeticionHTTP.peticionGET(
            this@ActivityCategoria,
            "",
            CategoriaProductoRespuesta::class.java,
            "api/CategoriaProducto/${categoriaId}",
            token
        ) { exito, result ->
            if (exito && result is CategoriaProductoRespuesta) {
                if (result.categorias != null && result.categorias.isNotEmpty()) {
                    binding.tvNombreCategoria.text = result.categorias[0].nombre
                } else {
                    // Manejo para caso de lista de categorías vacía o nula
                }
            } else {
                // Manejo para caso de solicitud no exitosa
            }
        }

        // Realizar solicitud HTTP para obtener datos de productos
        PeticionHTTP.peticionGET(
            this@ActivityCategoria,
            "",
            ProductoRespuesta::class.java,
            "api/Producto/Categoria/${categoriaId}",
            token
        ) { exito, result ->
            if (exito && result is ProductoRespuesta) {
                if (result.productos != null) {
                    mostrarProductos(result.productos)
                } else {
                    // Manejo para caso de lista de productos nula
                }
            } else {
                // Manejo para caso de solicitud no exitosa
            }
        }

        // Configurar listener para el botón del carrito usando binding
        binding.btnCarrito.setOnClickListener {
            val intent = Intent(this, ActivityCarrito::class.java)
            val productosParcelable = productosSeleccionados.map { it.toParcelable() }
            intent.putParcelableArrayListExtra("productos_en_carrito", ArrayList(productosParcelable))
            intent.putExtra("cantidad_productos", contadorCarrito)
            intent.putExtra("precio_total", precioTotalCarrito)
            startActivity(intent)
        }

        // Ajustar el padding de la ventana para manejar la barra de sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun obtenerDatosDesdeIntent() {
        contadorCarrito = intent.getIntExtra("contador_carrito", 0)
        val productosList = intent.getParcelableArrayListExtra<ParcelableProducto>("productos_seleccionados")
        productosSeleccionados.clear()
        if (productosList != null) {
            productosSeleccionados.addAll(productosList.map { it.toProducto() }) // Convertir de ParcelableProducto a Producto si es necesario
        }
        precioTotalCarrito = intent.getIntExtra("precio_total_carrito", 0)
    }

    private fun actualizarCarrito() {
        // Actualizar el contador de carrito en la interfaz de usuario
        binding.tvContadorCarrito.text = contadorCarrito.toString()
    }

    private fun mostrarProductos(productos: List<Producto>) {
        val linearLayoutManager = LinearLayoutManager(this@ActivityCategoria, LinearLayoutManager.VERTICAL, false)
        productoAdapter = ProductoAdapter(productos, emptyList())
        productoAdapter.setOnItemClickListener(this)
        binding.rvProductos.layoutManager = linearLayoutManager
        binding.rvProductos.adapter = productoAdapter
    }

    override fun onItemClick(producto: Producto) {
        productosSeleccionados.add(producto)
        contadorCarrito++
        cantidadCarrito = contadorCarrito
        precioTotalCarrito += producto.precio
        binding.tvContadorCarrito.text = contadorCarrito.toString()
        Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()

        // Imprimir la sumatoria total del precio de los productos
        Log.d("ActivityCategoria", "Precio total del carrito: $precioTotalCarrito MXN")
    }

    override fun onBackPressed() {
        val intent = Intent()
        val productosParcelable = productosSeleccionados.map { it.toParcelable() }
        intent.putParcelableArrayListExtra("productos_en_carrito", ArrayList(productosParcelable))
        intent.putExtra("cantidad_productos", cantidadCarrito)
        intent.putExtra("precio_total", precioTotalCarrito)

        // Imprimir en el log los datos que estás enviando de regreso
        Log.d("ActivityCategoria", "Productos en el carrito: ${productosSeleccionados.map { it.nombre }}")
        Log.d("ActivityCategoria", "Cantidad de productos en el carrito: $cantidadCarrito")
        Log.d("ActivityCategoria", "Precio total del carrito: $precioTotalCarrito MXN")

        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

}
