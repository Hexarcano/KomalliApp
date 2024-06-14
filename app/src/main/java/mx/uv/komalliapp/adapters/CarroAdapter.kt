package mx.uv.komalliapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.uv.komalliapp.R
import mx.uv.komalliapp.models.Producto

class CarroAdapter(
    private val productosEnCarrito: List<Producto>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<CarroAdapter.CarroViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int, eliminar: Boolean)
    }

    inner class CarroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreProducto: TextView = itemView.findViewById(R.id.tv_productoUnitario)
        private val precioProducto: TextView = itemView.findViewById(R.id.tv_precioUnitario)
        private val btnDisminuir: Button = itemView.findViewById(R.id.btn_disminuir)

        fun bind(producto: Producto) {
            nombreProducto.text = producto.nombre
            precioProducto.text = "${producto.precio} MXN"

            btnDisminuir.setOnClickListener {
                listener.onItemClick(adapterPosition, true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarroViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_carrito, parent, false)
        return CarroViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarroViewHolder, position: Int) {
        val producto = productosEnCarrito[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int {
        return productosEnCarrito.size
    }
}
