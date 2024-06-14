package mx.uv.komalliapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import mx.uv.komalliapp.databinding.ActivityPagoEfectivoBinding
import mx.uv.komalliapp.models.ParcelableProducto
import mx.uv.komalliapp.models.Producto

class ActivityPagoEfectivo : AppCompatActivity() {
    private lateinit var binding: ActivityPagoEfectivoBinding
    private var productosEnCarrito: MutableList<Producto> = mutableListOf()
    private var precioTotalCarrito: Int = 0
    private var cantidadProductosAgregados: Int = 0
    private var notaRecibida: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pago_efectivo)
        binding = ActivityPagoEfectivoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.br_menuS)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val productosParcelable =
            intent.getParcelableArrayListExtra<ParcelableProducto>("productos_en_carrito")
        productosEnCarrito = productosParcelable?.map {
            Producto(it.id, it.nombre, it.precio, it.descuento, it.categoriaProductoId)
        }?.toMutableList() ?: mutableListOf()
        val cantidadProductos = intent.getIntExtra("cantidad_productos", 0)
        val precioTotal = intent.getIntExtra("precio_total", 0)
        val nota = intent.getStringExtra("nota")

        val tvProductosPE = findViewById<TextView>(R.id.tv_productosPE)
        val tvTotalPE = findViewById<TextView>(R.id.tv_TotalPE)

        // Mostrar productos en el carrito
        val productosText = productosEnCarrito?.joinToString(separator = "\n") {
            "${it.nombre} -> ${it.precio} MXN"
        } ?: "No hay productos en el carrito"
        tvProductosPE.text = "Productos en el carrito:\n$productosText"

        // Mostrar total a pagar
        tvTotalPE.text = "Total a pagar: $precioTotal MXN"


    }
}