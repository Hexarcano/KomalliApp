package mx.uv.komalliapp.models

import android.os.Parcel
import android.os.Parcelable

data class ParcelableProducto(
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

    companion object CREATOR : Parcelable.Creator<ParcelableProducto> {
        override fun createFromParcel(parcel: Parcel): ParcelableProducto {
            return ParcelableProducto(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableProducto?> {
            return arrayOfNulls(size)
        }
    }
}

fun ParcelableProducto.toProducto(): Producto {
    return Producto(id, nombre, precio, descuento, categoriaProductoId)
}



