package com.nicolas.recetasdeluxe.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.exifinterface.media.ExifInterface
import com.squareup.picasso.Picasso
import java.io.InputStream

// Utilidad para cargar imagenes de la galeria respetando su orientacion vertical/horizontal (EXIF)
object ImageUtils {

    fun loadOrientedImage(context: Context, imageUriString: String, imageView: ImageView) {
        if (imageUriString.isBlank()) return

        try {
            val uri = Uri.parse(imageUriString)
            var degrees = 0f

            // Leemos los metadatos EXIF de la imagen desde la galeria
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val exif = ExifInterface(stream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                degrees = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
            }

            // Aplicamos la rotacion correcta con Picasso
            if (degrees != 0f) {
                Picasso.get().load(uri).rotate(degrees).into(imageView)
            } else {
                Picasso.get().load(uri).into(imageView)
            }
        } catch (_: Exception) {
            // Fallback si no se pueden leer los metadatos EXIF
            try {
                Picasso.get().load(Uri.parse(imageUriString)).into(imageView)
            } catch (_: Exception) {}
        }
    }
}
