package mx.uv.komalliapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.uv.komalliapp.R
import mx.uv.komalliapp.models.Producto
import mx.uv.komalliapp.models.CategoriaProducto

class ProductoAdapter(private var productos: List<Producto>, private val categorias: List<CategoriaProducto>) :
    RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {
    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = productos[position]
        // Obtener el nombre de la categoría según el ID del producto
        val nombreCategoria = categorias.find { it.id == producto.categoriaProductoId }?.nombre
        // Verificar si se encontró el nombre de la categoría
        if (!nombreCategoria.isNullOrEmpty()) {
            // Si se encontró, establecer el nombre de la categoría en el TextView tvCategoria
            holder.tvCategoria.text = nombreCategoria
        } else {
            // Si no se encontró, establecer un mensaje predeterminado o manejarlo según sea necesario
            holder.tvCategoria.text = "Categoría desconocida"
        }
        holder.tvProducto.text = "nombreProducto: ${producto.nombre}"
        holder.tvPrecio.text = producto.precio.toString()
        holder.btn_agregar.setOnClickListener {
            listener?.onItemClick(producto)
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    fun actualizarProductos(nuevaListaProductos: List<Producto>) {
        productos = nuevaListaProductos
        notifyDataSetChanged()
    }

    class ProductoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btn_agregar: Button = itemView.findViewById(R.id.btn_agregar)
        val tvProducto: TextView = itemView.findViewById(R.id.tv_producto)
        val tvPrecio: TextView = itemView.findViewById(R.id.tv_precio)
        val tvCategoria: TextView = itemView.findViewById(R.id.tv2_categoria)
    }

    interface OnItemClickListener {
        fun onItemClick(producto: Producto)
    }

}
