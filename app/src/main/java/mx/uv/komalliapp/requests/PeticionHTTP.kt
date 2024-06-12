package mx.uv.komalliapp.requests

import android.content.Context
import com.google.gson.Gson
import com.koushikdutta.ion.Ion
import mx.uv.komalliapp.models.ISerializable
import mx.uv.komalliapp.models.DatosSesionRespuesta
import mx.uv.komalliapp.utils.Constantes

class PeticionHTTP {
    companion object {
        /**
         * Realiza una petición POST
         *
         * @param context El contexto desde donde se realiza la petición (Activity)
         * @param contenido Parametros que se envian en el cuerpo de la petición
         * @param tipoRespuesta Modelo al que se mapean los datos de la respuesta (Generico)
         * @param path Ruta a la que se realiza la petición. Ejem. "path/to/resource"
         */
        fun <T : ISerializable> peticionPOST(
            context: Context,
            contenido: ISerializable,
            tipoRespuesta: Class<T>,
            path: String,
            respuestaCallback: (Boolean, ISerializable) -> Unit
        ) {
            Ion.with(context)
                .load("POST", "${Constantes.urlBase}/${path}")
                .setHeader("Content-Type", "application/json")
                .setJsonPojoBody(contenido)
                .asString()
                .setCallback { error, result ->
                    run {
                        val gson = Gson()

                        if (error != null) {
                            respuestaCallback(false, respuestaSesionError("Error en la petición"))
                        } else {
                            val respuesta = gson.fromJson(result, tipoRespuesta)

                            respuestaCallback(true, respuesta)
                        }
                    }
                }
        }

        private fun respuestaSesionError(message: String): DatosSesionRespuesta {
            return DatosSesionRespuesta(null, null, message)
        }

        fun <T : ISerializable> peticionGET(
            context: Context,
            id: String,
            tipoRespuesta: Class<T>,
            path: String,
            token: String?,
            respuestaCallback: (Boolean, ISerializable) -> Unit
        ) {
            Ion.with(context)
                .load("GET", "${Constantes.urlBase}/${path}/${id}")
                .setHeader("Content-Type", "application/json")
                .setHeader("Authorization", token)
                .asString()
                .setCallback { error, result ->
                    run {
                        val gson = Gson()

                        if (error != null) {
                            respuestaCallback(false, respuestaSesionError("Error en la petición"))
                        } else {
                            val respuesta = gson.fromJson(result, tipoRespuesta)

                            respuestaCallback(true, respuesta)
                        }
                    }
                }
        }
    }
}