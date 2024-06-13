package mx.uv.komalliapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.uv.komalliapp.adapters.CarroAdapter
import mx.uv.komalliapp.models.ParcelableProducto
import mx.uv.komalliapp.models.ProductoOrdenConsulta

class ActivityCarrito : AppCompatActivity(), CarroAdapter.OnItemClickListener {

    private lateinit var productosEnCarritoRecyclerView: RecyclerView
    private lateinit var carritoAdapter: CarroAdapter
    private var productosEnCarrito: MutableList<ProductoOrdenConsulta> = mutableListOf()
    private var precioTotalCarrito: Int = 0
    private var cantidadProductosAgregados: Int = 0
    private var contadorCarrito: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // Obtener productos en el carrito desde el intent
        val productosParcelable = intent.getParcelableArrayListExtra<ParcelableProducto>("productos_en_carrito")
        productosEnCarrito = productosParcelable?.map {
            ProductoOrdenConsulta(it.id, it.nombre, it.precio, it.descuento, it.categoriaProductoId)
        }?.toMutableList() ?: mutableListOf()

        // Obtener el precio total del intent
        precioTotalCarrito = intent.getIntExtra("precio_total", 0)
        cantidadProductosAgregados = intent.getIntExtra("cantidad_productos", 0)
        contadorCarrito = intent.getIntExtra("contador_carrito", 0)

        // Inicializar RecyclerView y su adaptador
        productosEnCarritoRecyclerView = findViewById(R.id.recyclerCarrito)
        carritoAdapter = CarroAdapter(productosEnCarrito, this)
        productosEnCarritoRecyclerView.layoutManager = LinearLayoutManager(this)
        productosEnCarritoRecyclerView.adapter = carritoAdapter

        // Mostrar el total a pagar inicial
        actualizarTotalPagar()
        actualizarVisibilidadMensajeCarritoVacio()
    }

    // Actualizar visibilidad de elementos cuando el carrito está vacío o no
    private fun actualizarVisibilidadMensajeCarritoVacio() {
        val textViewEmptyCart = findViewById<TextView>(R.id.textViewEmptyCart)
        val emptyCartImageView = findViewById<ImageView>(R.id.iv_carritoVacio)
        val cardCarrito = findViewById<CardView>(R.id.cardCarrito)
        if (productosEnCarrito.isEmpty()) {
            cardCarrito.visibility = View.GONE
            textViewEmptyCart.visibility = View.VISIBLE
            emptyCartImageView.visibility = View.VISIBLE
        } else {
            cardCarrito.visibility = View.VISIBLE
            textViewEmptyCart.visibility = View.GONE
            emptyCartImageView.visibility = View.GONE
        }
    }

    // Manejar clics en elementos del carrito para eliminar productos
    override fun onItemClick(position: Int, eliminar: Boolean) {
        val productoOrden = productosEnCarrito[position]

        if (eliminar) {
            // Disminuir la cantidad a cero para eliminar el producto del carrito
            productoOrden.cantidad = 0
            productoOrden.subtotalProductos = 0

            // Reducir el precio total
            precioTotalCarrito -= productoOrden.precioUnitario
            cantidadProductosAgregados--

            // Remover el producto del carrito
            productosEnCarrito.removeAt(position)
            carritoAdapter.notifyItemRemoved(position)
        }

        // Actualizar visibilidad del mensaje y el total a pagar
        actualizarVisibilidadMensajeCarritoVacio()
        actualizarTotalPagar()
    }

    // Calcular y mostrar el total a pagar
    private fun actualizarTotalPagar() {
        val tvTotalPagar = findViewById<TextView>(R.id.tv_totalPagar)
        tvTotalPagar.text = "$precioTotalCarrito MXN"
    }

    override fun onBackPressed() {
        // Preparar datos para enviar de regreso a la actividad anterior
        val intent = Intent()
        val productosParcelable = productosEnCarrito.map { it.toParcelable() }
        intent.putParcelableArrayListExtra("productos_en_carrito", ArrayList(productosParcelable))
        intent.putExtra("cantidad_productos", cantidadProductosAgregados)
        intent.putExtra("precio_total", precioTotalCarrito)

        // Imprimir en el log los datos que estás enviando de regreso
        Log.d("ActivityCarrito", "Productos en el carrito: ${productosEnCarrito.map { it.productoId }}")
        Log.d("ActivityCarrito", "Cantidad de productos en el carrito: $cantidadProductosAgregados")
        Log.d("ActivityCarrito", "Precio total del carrito: $precioTotalCarrito MXN")

        // Devolver datos a la actividad anterior
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }
}
