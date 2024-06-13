package mx.uv.komalliapp

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
import mx.uv.komalliapp.databinding.ActivityMenuBinding
import mx.uv.komalliapp.models.CategoriaProducto
import mx.uv.komalliapp.models.CategoriaProductoRespuesta
import mx.uv.komalliapp.models.ParcelableProducto
import mx.uv.komalliapp.models.Producto
import mx.uv.komalliapp.models.RespuestaProductos
import mx.uv.komalliapp.requests.PeticionHTTP
import mx.uv.komalliapp.utils.CategoriaRVAdapter

class ActivityMenu : AppCompatActivity(), ProductoAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var productoAdapter: ProductoAdapter
    private var categorias: List<CategoriaProducto> = emptyList()
    private val productosEnCarrito: MutableList<Producto> = mutableListOf()
    private var contadorCarrito: Int = 0
    private var precioTotalCarrito: Int = 0
    private var cantidadProductosAgregados: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = this.getSharedPreferences("sesion", MODE_PRIVATE)
        //val token = sharedPreferences.getString("token", "")
        val token = obtenerTokenDeAutenticacion()

        val btMenu: ImageView = findViewById(R.id.btn_menu)
        btMenu.setOnClickListener {
            val intent = Intent(this, ActivityNav::class.java)
            startActivity(intent)
        }

        val btCarrito: ImageView = findViewById(R.id.btn_carrito)
        btCarrito.setOnClickListener {
            val intent = Intent(this, ActivityCarrito::class.java)
            val productosParcelable = productosEnCarrito.map { ParcelableProducto(it) }
            intent.putParcelableArrayListExtra("productos_en_carrito", ArrayList(productosParcelable))
            intent.putExtra("cantidad_productos", cantidadProductosAgregados)
            intent.putExtra("precio_total", precioTotalCarrito)
            startActivity(intent)
        }

        // Obtener las categorías
        obtenerCategorias(token)

        PeticionHTTP.peticionGET(
            this@ActivityMenu,
            "",
            CategoriaProductoRespuesta::class.java,
            "api/CategoriaProducto",
            token
        ) { exito, result ->
            if (exito && result != null && result is CategoriaProductoRespuesta) {
                val datos = result

                val linearLayoutManager =
                    LinearLayoutManager(this@ActivityMenu, LinearLayoutManager.HORIZONTAL, false)

                val adapter = CategoriaRVAdapter(datos.categorias) { categoria ->
                    val intent = Intent(this, ActivityCategoria::class.java)
                    intent.putExtra("categoriaId", categoria.id)
                    startActivity(intent)
                }
                binding.rvCategorias.layoutManager = linearLayoutManager
                binding.rvCategorias.adapter = adapter
            } else {
                Toast.makeText(this, "Error al obtener las categorías", Toast.LENGTH_LONG).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.menu) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onItemClick(producto: Producto) {
        productosEnCarrito.add(producto)
        contadorCarrito++
        binding.tvContadorCarrito.text = contadorCarrito.toString()
        precioTotalCarrito = productosEnCarrito.sumBy { it.precio * it.cantidad }
        Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
    }

    private fun obtenerCategorias(token: String?) {
        PeticionHTTP.peticionGETP(
            this@ActivityMenu,
            "",
            "api/CategoriaProducto",
            token
        ) { success, response ->
            if (success && response != null && response is RespuestaProductos) {
                categorias = response.categorias
                obtenerProductos(token)
            } else {
                Toast.makeText(this, "Error al obtener las categorías", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun obtenerProductos(token: String?) {
        val todosLosProductos = mutableListOf<Producto>()
        // Obtener los productos de cada categoría y almacenar los nombres de las categorías en el mapa
        for (i in 1..5) {
            val categoria = "api/Producto/Categoria/$i"

            PeticionHTTP.peticionGETP(this@ActivityMenu, "", categoria, token) { success, response ->
                if (success && response != null && response is RespuestaProductos) {
                    val productos = response.productos
                    // Agregar los productos de esta categoría a la lista de todos los productos
                    todosLosProductos.addAll(productos.map {
                        Producto(
                            it.id,
                            it.nombre,
                            it.precio,
                            it.descuento,
                            it.categoriaProductoId
                        )
                    })

                    // Si todas las solicitudes han finalizado, actualizar el RecyclerView
                    if (i == 5) {
                        mostrarProductos(todosLosProductos)
                    }
                } else {
                    // Si hubo un error o la respuesta es nula
                    val errorMessage = response?.let {
                        // Si la respuesta no es nula, pero hubo un error en la respuesta
                        Log.e(
                            "ActivityMenu",
                            "Error al obtener la lista de nombres y precios de productos: $it"
                        )
                        "Error al obtener la lista de nombres y precios de productos: $it"
                    } ?: run {
                        // Si la respuesta es nula
                        Log.e("ActivityMenu", "La respuesta del servidor es nula.")
                        "La respuesta del servidor es nula."
                    }
                    // Mostrar el mensaje de error
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun mostrarProductos(productos: List<Producto>) {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        productoAdapter = ProductoAdapter(productos, categorias)
        binding.recyclerProducto.apply {
            layoutManager = linearLayoutManager
            adapter = productoAdapter
        }
        // Establecer el listener del adaptador
        productoAdapter.setOnItemClickListener(this)
    }

    private fun obtenerTokenDeAutenticacion(): String? {
        val sharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }
}
