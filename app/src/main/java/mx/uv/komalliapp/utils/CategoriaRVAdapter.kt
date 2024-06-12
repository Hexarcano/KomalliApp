package mx.uv.komalliapp.utils

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mx.uv.komalliapp.R
import mx.uv.komalliapp.models.CategoriaProducto

class CategoriaRVAdapter(
    private val datos: List<CategoriaProducto>,
    private val onItemClick: (CategoriaProducto) -> Unit
) : RecyclerView.Adapter<CategoriaRVAdapter.ItemCategoria>() {
    class ItemCategoria(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCategoria = itemView.findViewById<TextView>(R.id.tv_categoria)
        val ivCategoria = itemView.findViewById<ImageView>(R.id.iv_categoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemCategoria {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return ItemCategoria(view)
    }

    override fun getItemCount(): Int {
        return datos.size
    }

    override fun onBindViewHolder(itemCategoria: ItemCategoria, position: Int) {
        val datosCategoria = datos[position]
        val cadena = Base64.decode(datosCategoria.imagenBase64, Base64.DEFAULT)
        val bitmapImagen = BitmapFactory.decodeByteArray(cadena, 0, cadena.size)

        itemCategoria.ivCategoria.setImageBitmap(bitmapImagen)
        itemCategoria.tvCategoria.text = datos[position].nombre

        itemCategoria.itemView.setOnClickListener {
            onItemClick(datosCategoria)
        }
    }
}