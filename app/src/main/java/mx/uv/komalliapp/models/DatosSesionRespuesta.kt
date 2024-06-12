package mx.uv.komalliapp.models

class DatosSesionRespuesta(
    var tokenType: String? = null,
    var accessToken: String? = null,
    var mensaje: String? = null
) : ISerializable {
    constructor() : this(null, null, null)
}