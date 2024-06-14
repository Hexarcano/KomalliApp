package mx.uv.komalliapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import mx.uv.komalliapp.models.ProductoOrdenConsulta
import mx.uv.komalliapp.models.RespuestaProductos
import mx.uv.komalliapp.models.toParcelable
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

        val btAyuda: ImageView = findViewById(R.id.bt_ayuda)
        btAyuda.setOnClickListener {
            val intent = Intent(this, ActivityAyuda::class.java)
            startActivity(intent)
        }

        val productos = obtenerProductos()
        productoAdapter = ProductoAdapter(productos, emptyList())
        binding.recyclerProducto.layoutManager = LinearLayoutManager(this)
        binding.recyclerProducto.adapter = productoAdapter

        val sharedPreferences = this.getSharedPreferences("sesion", MODE_PRIVATE)
        val token = obtenerTokenDeAutenticacion()

        val btMenu: ImageView = findViewById(R.id.btn_menu)
        btMenu.setOnClickListener {
            val intent = Intent(this, ActivityNav::class.java)
            startActivity(intent)
        }

        // Configurar listener para el botón del carrito usando binding
        binding.btnCarrito.setOnClickListener {
            Log.d("ActivityMenu", "Precio total a pasar al carrito: $precioTotalCarrito MXN")

            val intent = Intent(this, ActivityCarrito::class.java)
            val productosParcelable = productosEnCarrito.map { it.toParcelable() }
            intent.putParcelableArrayListExtra("productos_en_carrito", ArrayList(productosParcelable))
            intent.putExtra("cantidad_productos", cantidadProductosAgregados)
            intent.putExtra("precio_total", precioTotalCarrito)
            startActivityForResult(intent, 1)
        }


        productoAdapter.setOnItemClickListener(this)
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

                val linearLayoutManager = LinearLayoutManager(this@ActivityMenu, LinearLayoutManager.HORIZONTAL, false)
                val adapter = CategoriaRVAdapter(datos.categorias) { categoria ->
                    // Imprimir los datos antes de iniciar la actividad
                    Log.d("ActivityMenu", "Iniciando ActivityCategoria con los siguientes datos:")
                    Log.d("ActivityMenu", "Categoria ID: ${categoria.id}")
                    Log.d("ActivityMenu", "Contador carrito: $contadorCarrito")
                    Log.d("ActivityMenu", "Productos seleccionados: ${productosEnCarrito.joinToString { it.nombre }}")
                    Log.d("ActivityMenu", "Precio total carrito: $precioTotalCarrito MXN")

                    val intent = Intent(this, ActivityCategoria::class.java)
                    val productosParcelable = productosEnCarrito.map { it.toParcelable() }
                    intent.putExtra("categoriaId", categoria.id)
                    intent.putExtra("contador_carrito", contadorCarrito)
                    intent.putParcelableArrayListExtra("productos_seleccionados", ArrayList(productosParcelable))
                    intent.putExtra("precio_total_carrito", precioTotalCarrito)
                    startActivityForResult(intent, 1)
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
        cantidadProductosAgregados++
        contadorCarrito = cantidadProductosAgregados
        precioTotalCarrito += producto.precio
        binding.tvContadorCarrito.text = cantidadProductosAgregados.toString()
        Log.d("ActivityMenu", "Precio total del carrito: $precioTotalCarrito MXN")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                contadorCarrito = data.getIntExtra("cantidad_productos", 0)
                cantidadProductosAgregados = contadorCarrito
                val productosList = data.getParcelableArrayListExtra<ParcelableProducto>("productos_en_carrito")
                productosEnCarrito.clear()
                if (productosList != null) {
                    productosEnCarrito.addAll(productosList.map {
                        Producto(it.id, it.nombre, it.precio, it.descuento, it.categoriaProductoId)
                    })
                }
                precioTotalCarrito = data.getIntExtra("precio_total", 0)
                binding.tvContadorCarrito.text = cantidadProductosAgregados.toString()
            }
        }
    }





    private fun obtenerProductos(): List<Producto> {
        return listOf(Producto(1, "Producto de ejemplo", 100, 0, 0))
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
        for (i in 1..5) {
            val categoria = "api/Producto/Categoria/$i"
            PeticionHTTP.peticionGETP(this@ActivityMenu, "", categoria, token) { success, response ->
                if (success && response != null && response is RespuestaProductos) {
                    val productos = response.productos
                    todosLosProductos.addAll(productos.map {
                        Producto(it.id, it.nombre, it.precio, it.descuento, it.categoriaProductoId)
                    })
                    if (i == 5) {
                        mostrarProductos(todosLosProductos)
                    }
                } else {
                    val errorMessage = response?.let {
                        Log.e("ActivityMenu", "Error al obtener la lista de nombres y precios de productos: $it")
                        "Error al obtener la lista de nombres y precios de productos: $it"
                    } ?: run {
                        Log.e("ActivityMenu", "La respuesta del servidor es nula.")
                        "La respuesta del servidor es nula."
                    }
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
        productoAdapter.setOnItemClickListener(this)
    }

    private fun obtenerTokenDeAutenticacion(): String? {
        val sharedPreferences = getSharedPreferences("sesion", MODE_PRIVATE)
        return sharedPreferences.getString("token", null)
    }
}

