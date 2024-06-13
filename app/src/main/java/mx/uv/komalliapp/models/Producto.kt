package mx.uv.komalliapp.models
import mx.uv.komalliapp.models.ISerializable
data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Int,
    val descuento: Int,
    val categoriaProductoId: Int
) {
    var cantidad: Int = 0
        private set

    fun aumentarCantidad() {
        cantidad++
    }

    fun disminuirCantidad() {
        if (cantidad > 0) {
            cantidad--
        }
    }
    }