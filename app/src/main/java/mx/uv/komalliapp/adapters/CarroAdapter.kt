package mx.uv.komalliapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.uv.komalliapp.R
import mx.uv.komalliapp.models.Producto

class CarroAdapter(private var productosEnCarrito: MutableList<Producto>) :
    RecyclerView.Adapter<CarroAdapter.CarritoViewHolder>() {

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return CarritoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val producto = productosEnCarrito[position]
        holder.bind(producto)
    }

    override fun getItemCount(): Int {
        return productosEnCarrito.size
    }

    inner class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombreProductoTextView: TextView = itemView.findViewById(R.id.tv_productoCarrito)
        private val precioProductoTextView: TextView = itemView.findViewById(R.id.tv_precioCarrito)
        private val disminuirButton: Button = itemView.findViewById(R.id.btn_disminuir)

        init {
            disminuirButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position, false)
                    notifyItemChanged(position) // Notificar al adaptador sobre el cambio en la cantidad
                }
            }
        }

        fun bind(producto: Producto) {
            nombreProductoTextView.text = producto.nombre
            precioProductoTextView.text = producto.precio.toString()
        }
    }



    interface OnItemClickListener {
        fun onItemClick(position: Int, eliminar: Boolean)
    }

    fun actualizarProductosEnCarrito(nuevaListaProductos: List<Producto>) {
        productosEnCarrito.clear()
        productosEnCarrito.addAll(nuevaListaProductos)
        notifyDataSetChanged()
    }

}
