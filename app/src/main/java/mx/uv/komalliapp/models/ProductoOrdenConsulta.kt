package mx.uv.komalliapp.models

import android.os.Parcel
import android.os.Parcelable

data class ProductoOrdenConsulta(
    val ordenId: Int,
    val productoId: String,
    val precioUnitario: Int,
    var cantidad: Int,
    var subtotalProductos: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ordenId)
        parcel.writeString(productoId)
        parcel.writeInt(precioUnitario)
        parcel.writeInt(cantidad)
        parcel.writeInt(subtotalProductos)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProductoOrdenConsulta> {
        override fun createFromParcel(parcel: Parcel): ProductoOrdenConsulta {
            return ProductoOrdenConsulta(parcel)
        }

        override fun newArray(size: Int): Array<ProductoOrdenConsulta?> {
            return arrayOfNulls(size)
        }
    }
}
