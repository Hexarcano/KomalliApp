package mx.uv.komalliapp.models

class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val porcentajeDescuento: Int,
    val categoriaDescuentoId: Int
) : ISerializable