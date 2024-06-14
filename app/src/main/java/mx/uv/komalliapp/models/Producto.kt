package mx.uv.komalliapp.models

import android.os.Parcel
import android.os.Parcelable

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Int,
    val descuento: Int,
    val categoriaProductoId: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )
    fun toParcelable(): ParcelableProducto {
        return ParcelableProducto(id, nombre, precio,descuento,categoriaProductoId)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
        parcel.writeInt(precio)
        parcel.writeInt(descuento)
        parcel.writeInt(categoriaProductoId)
    }

    override fun describeContents(): Int {
        return 0
    }



    companion object CREATOR : Parcelable.Creator<Producto> {
        override fun createFromParcel(parcel: Parcel): Producto {
            return Producto(parcel)
        }

        override fun newArray(size: Int): Array<Producto?> {
            return arrayOfNulls(size)
        }
    }
}
fun Producto.toParcelable(): ParcelableProducto {
    return ParcelableProducto(id, nombre, precio, descuento, categoriaProductoId)
}