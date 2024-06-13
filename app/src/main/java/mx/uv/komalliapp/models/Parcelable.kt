package mx.uv.komalliapp.models

import android.os.Parcel
import android.os.Parcelable
import mx.uv.komalliapp.models.Producto

data class ParcelableProducto(val producto: Producto) : Parcelable {
    constructor(parcel: Parcel) : this(
        Producto(
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
        )
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(producto.id)
        parcel.writeString(producto.nombre)
        parcel.writeInt(producto.precio)
        parcel.writeInt(producto.descuento)
        parcel.writeInt(producto.categoriaProductoId)
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
