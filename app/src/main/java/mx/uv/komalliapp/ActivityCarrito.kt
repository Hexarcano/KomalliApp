package mx.uv.komalliapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.uv.komalliapp.adapters.CarroAdapter
import mx.uv.komalliapp.databinding.ActivityCarritoBinding
import mx.uv.komalliapp.models.ParcelableProducto
import mx.uv.komalliapp.models.Producto
import mx.uv.komalliapp.models.ProductoOrdenConsulta

class ActivityCarrito : AppCompatActivity(), CarroAdapter.OnItemClickListener {

    private lateinit var binding: ActivityCarritoBinding
    private lateinit var productosEnCarritoRecyclerView: RecyclerView
    private lateinit var carritoAdapter: CarroAdapter
    private var productosEnCarrito: MutableList<Producto> = mutableListOf()
    private var precioTotalCarrito: Int = 0
    private var cantidadProductosAgregados: Int = 0
    private var contadorCarrito: Int = 0
    private var notaRecibida: String? = ""

    companion object {
        private const val REQUEST_CODE_NOTA = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)
        binding = ActivityCarritoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nota = intent.getStringExtra("nota")
        if (nota != null) {
            Log.d("ActivityCarrito", "Nota recibida: $nota")
            Toast.makeText(this, "Nota guardada: $nota", Toast.LENGTH_LONG).show()
        } else {
            Log.d("ActivityCarrito", "No se recibió ninguna nota")
        }

        val productosParcelable =
            intent.getParcelableArrayListExtra<ParcelableProducto>("productos_en_carrito")
        productosEnCarrito = productosParcelable?.map {
            Producto(it.id, it.nombre, it.precio, it.descuento, it.categoriaProductoId)
        }?.toMutableList() ?: mutableListOf()

        precioTotalCarrito = intent.getIntExtra("precio_total", 0)
        cantidadProductosAgregados = intent.getIntExtra("cantidad_productos", 0)
        contadorCarrito = intent.getIntExtra("contador_carrito", 0)

        productosEnCarritoRecyclerView = findViewById(R.id.recyclerCarrito)
        carritoAdapter = CarroAdapter(productosEnCarrito, this)
        productosEnCarritoRecyclerView.layoutManager = LinearLayoutManager(this)
        productosEnCarritoRecyclerView.adapter = carritoAdapter

        actualizarTotalPagar()
        actualizarVisibilidadMensajeCarritoVacio()

        binding.btNota.setOnClickListener {
            val intent = Intent(this, ActivityNota::class.java)
            startActivityForResult(intent, REQUEST_CODE_NOTA)
        }

        binding.btIrPagar.setOnClickListener {
            val intent = Intent(this, ActivityOrden::class.java)
            val productosParcelable = productosEnCarrito.map { it.toParcelable() }
            intent.putParcelableArrayListExtra("productos_en_carrito", ArrayList(productosParcelable))
            intent.putExtra("cantidad_productos", cantidadProductosAgregados)
            intent.putExtra("precio_total", precioTotalCarrito)
            intent.putExtra("nota", notaRecibida)
            Log.d("ActivityCarrito", "Enviando NOTA: $notaRecibida")
            startActivity(intent)
        }
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
            // Reducir el precio total
            precioTotalCarrito -= productoOrden.precio
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
        val intent = Intent()
        val productosParcelable = productosEnCarrito.map { it.toParcelable() }
        intent.putParcelableArrayListExtra("productos_en_carrito", ArrayList(productosParcelable))
        intent.putExtra("cantidad_productos", cantidadProductosAgregados)
        intent.putExtra("precio_total", precioTotalCarrito)

        Log.d("ActivityCarrito", "Productos en el carrito: ${productosEnCarrito.map { it.id }}")
        Log.d("ActivityCarrito", "Cantidad de productos en el carrito: $cantidadProductosAgregados")
        Log.d("ActivityCarrito", "Precio total del carrito: $precioTotalCarrito MXN")

        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    // Manejar el resultado de ActivityNota
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NOTA && resultCode == Activity.RESULT_OK) {
            val nota = data?.getStringExtra("nota")
            notaRecibida = nota
            nota?.let {
                Log.d("ActivityCarrito", "Nota recibida: $nota")
                Toast.makeText(this, "Nota guardada: $nota", Toast.LENGTH_LONG).show()
                // Actualizar la nota en la variable y actualizar la vista si es necesario
            }
        }
    }
}
