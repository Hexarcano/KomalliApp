package mx.uv.komalliapp.utils

import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.uv.komalliapp.R
import mx.uv.komalliapp.models.Producto

class ProductoRVAdapter(
    private val datos: List<Producto>
) : RecyclerView.Adapter<ProductoRVAdapter.ItemProducto>() {
    class ItemProducto(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tvProducto = itemview.findViewById<TextView>(R.id.tv_producto)
        val tvPrecio = itemview.findViewById<TextView>(R.id.tv_precio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemProducto {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_producto, parent, false)
        return ItemProducto(view)
    }

    override fun getItemCount(): Int {
        return datos.size
    }

    override fun onBindViewHolder(itemProducto: ItemProducto, position: Int) {
        itemProducto.tvProducto.text = "Producto: ${datos[position].nombre}"

        if (datos[position].porcentajeDescuento > 0) {
            val precioOriginal = datos[position].precio
            val porcentajeDescuento = datos[position].porcentajeDescuento
            val descuento = precioOriginal * (porcentajeDescuento / 100.0)
            val precioConDescuento = precioOriginal - descuento

            val spanString = SpannableString("Precio: ${precioOriginal}  ${precioConDescuento}")
            spanString.setSpan(StrikethroughSpan(), 8, 8 + precioOriginal.toString().length, 0)

            itemProducto.tvPrecio.text = spanString
        } else {
            itemProducto.tvPrecio.text = "Precio: ${datos[position].precio}"
        }
    }
}