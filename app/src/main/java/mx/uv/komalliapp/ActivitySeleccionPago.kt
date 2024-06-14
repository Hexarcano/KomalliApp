package mx.uv.komalliapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import mx.uv.komalliapp.databinding.ActivitySeleccionPagoBinding
import mx.uv.komalliapp.models.ParcelableProducto
import mx.uv.komalliapp.models.Producto

class ActivitySeleccionPago : AppCompatActivity() {

    private lateinit var binding: ActivitySeleccionPagoBinding
    private var productosEnCarrito: MutableList<Producto> = mutableListOf()
    private var precioTotalCarrito: Int = 0
    private var cantidadProductosAgregados: Int = 0
    private var notaRecibida: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeleccionPagoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

        binding.btnPagoEfectivo.setOnClickListener {
            val intent = Intent(this, ActivityPagoEfectivo::class.java)
            val productosParcelable = productosEnCarrito.map { it.toParcelable() }
            intent.putParcelableArrayListExtra("productos_en_carrito", ArrayList(productosParcelable))
            intent.putExtra("cantidad_productos", cantidadProductosAgregados)
            intent.putExtra("precio_total", precioTotalCarrito)
            intent.putExtra("nota", notaRecibida)
            Log.d("ActivityCarrito", "Enviando NOTA: $notaRecibida")
            startActivity(intent)
        }
    }
}