package mx.uv.komalliapp

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mx.uv.komalliapp.adapters.CarroAdapter
import mx.uv.komalliapp.models.ParcelableProducto
import mx.uv.komalliapp.models.Producto

class ActivityCarrito : AppCompatActivity(), CarroAdapter.OnItemClickListener {

    private lateinit var productosEnCarritoRecyclerView: RecyclerView
    private lateinit var carritoAdapter: CarroAdapter
    private var productosEnCarrito: MutableList<Producto> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carrito)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.br_menuS)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val extras = intent.extras
        if (extras != null) {
            val productosParcelable: ArrayList<ParcelableProducto>? = extras.getParcelableArrayList("productos_en_carrito")
            if (productosParcelable != null) {
                productosEnCarrito.addAll(productosParcelable.map { it.producto })
            }
        }

        // Configurar RecyclerView y su adaptador
        productosEnCarritoRecyclerView = findViewById(R.id.recyclerCarrito)
        carritoAdapter = CarroAdapter(productosEnCarrito)
        productosEnCarritoRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ActivityCarrito)
            adapter = carritoAdapter
        }

        carritoAdapter.setOnItemClickListener(this)

        // Actualizar la visibilidad del mensaje si el carrito está vacío
        actualizarVisibilidadMensajeCarritoVacio()
        actualizarTotalPagar()
    }

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

    override fun onItemClick(position: Int, agregar: Boolean) {
        val producto = productosEnCarrito[position]
        if (agregar) {
            producto.aumentarCantidad()
        } else {
            if (producto.cantidad > 0) {
                producto.disminuirCantidad()
            }
        }
        carritoAdapter.notifyItemChanged(position)
        actualizarVisibilidadMensajeCarritoVacio()
        actualizarTotalPagar()
    }

    private fun actualizarTotalPagar() {
        val tvTotalPagar = findViewById<TextView>(R.id.tv_totalPagar)
        var totalPagar = 0
        for (producto in productosEnCarrito) {
            totalPagar += producto.precio * producto.cantidad
        }
        tvTotalPagar.text = getString(R.string.total_pagar, totalPagar)
    }
}
