package mx.uv.komalliapp.utils

class Request(
    val requestType: RequestType,
    val route: String
) {
    val url = "${Constantes.urlBase}/${route}"
}