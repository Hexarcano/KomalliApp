package mx.uv.komalliapp.models
data class RespuestaProductos(
    val mensaje: String,
    val productos: List<Producto>,
    val categorias: List<CategoriaProducto>
)
