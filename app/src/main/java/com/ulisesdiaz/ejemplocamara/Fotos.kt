package com.ulisesdiaz.ejemplocamara

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Fotos(private var activity: Activity, private var imageView: ImageView) {

    private val SOLICITUD_TOMAR_FOTO = 1
    private val SOLICITUD_SELECCIONAR_FOTO = 2

    private val permisoCamera = android.Manifest.permission.CAMERA
    private val permisoWriteStorage = android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val permisoReadStorage = android.Manifest.permission.READ_EXTERNAL_STORAGE

    var urlFotoActual = ""

    fun tomarFoto(){
        pedirPermisos()
    }

    fun selecionarFoto(){
        pedirPermisosSeleccionarFoto()
    }

    private fun pedirPermisos(){
        val proveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(activity, permisoCamera)

        if (proveerContexto){
            solicitudPermisos()
        }else{
            solicitudPermisos()
        }
    }

    private fun pedirPermisosSeleccionarFoto(){
        val proveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(activity, permisoReadStorage)

        if (proveerContexto){
            solicitudPermisosSeleccionarFoto()
        }else{
            solicitudPermisosSeleccionarFoto()
        }
    }

    @SuppressLint("NewApi")
    private fun solicitudPermisosSeleccionarFoto(){
        activity.requestPermissions(arrayOf(permisoReadStorage), SOLICITUD_SELECCIONAR_FOTO)
    }

    @SuppressLint("NewApi")
    private fun solicitudPermisos(){
        activity.requestPermissions(arrayOf(permisoCamera, permisoWriteStorage, permisoReadStorage), SOLICITUD_TOMAR_FOTO)
    }

    fun requesttPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            SOLICITUD_TOMAR_FOTO ->{
                if (grantResults.size > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED){
                    // Tenemos permiso
                    dispararIntentTomarFoto()
                }else{
                    // No tenemos permiso
                    Toast.makeText(activity.applicationContext, "No diste permiso a la camara y almacenamento", Toast.LENGTH_SHORT).show()
                }
            }

            SOLICITUD_SELECCIONAR_FOTO ->{
                if (grantResults.size > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Tenemos permiso
                    dispararIntentSelecionarFoto()
                }else{
                    // No tenemos permiso
                    Toast.makeText(activity.applicationContext, "No diste permiso para acceder a tus fotos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun dispararIntentTomarFoto(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Verificar si se cuenta con el permiso
        if (intent.resolveActivity(activity.packageManager) != null){
            var archivoFoto: File? = null
            archivoFoto = crearArchivoImagen()

            if (archivoFoto != null){
                val urlFoto = FileProvider.getUriForFile(activity.applicationContext, "com.ulisesdiaz.ejemplocamara", archivoFoto)

                intent.putExtra(MediaStore.EXTRA_OUTPUT, urlFoto)
                activity.startActivityForResult(intent, SOLICITUD_TOMAR_FOTO)
            }
        }
    }

    private fun dispararIntentSelecionarFoto(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // Verificar si se cuenta con el permiso

        intent.setType("image/*") // Delimita el tipo de archivo

        activity.startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), SOLICITUD_SELECCIONAR_FOTO)
    }

    /**
     * Se activa cuando se deja de ocupar la camara y espera el resultadod el intent
     */
    fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            SOLICITUD_TOMAR_FOTO ->{
                if (resultCode == Activity.RESULT_OK){
                    // Obtener imagen
                    Log.d("ACITVITY_RESULT", "obtener imagen")
                    /*val extras = data?.extras
                    val imageBitmap = extras!!.get("data") as Bitmap*/
                    mostrarBitMap(urlFotoActual)
                    anadirImagenGalria()
                }else{
                    // se Cancelo la captura
                }
            }

            SOLICITUD_SELECCIONAR_FOTO -> {
                if (resultCode == Activity.RESULT_OK){
                    mostrarBitMap(data?.data.toString())
                }
            }
        }
    }

    private fun mostrarBitMap(url: String){
        val uri = Uri.parse(url)
        val stream = activity.contentResolver.openInputStream(uri)
        val imageBitmap = BitmapFactory.decodeStream(stream)
        imageView?.setImageBitmap(imageBitmap)
    }

    private fun crearArchivoImagen(): File{
        val timeStamp = SimpleDateFormat("yyyMMdd_HHmmss").format(Date()) // Obtiene fecha de captura de foto
        val nombreArchivoImagen = "JPEG_" + timeStamp + "_"
        //val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES) // Guarda dentro del directorio de la carpeta de la app
        val directorio = Environment.getExternalStorageDirectory() // Guarda dentro de un directorio publico del sistema Android
        val directotioPictures = File(directorio.absolutePath + "/Pictures/") //Se elige el directorio publico donde se va a guardar
        val imagen = File.createTempFile(nombreArchivoImagen, ".jpg", directotioPictures)

        urlFotoActual = "file://" + imagen.absolutePath

        return imagen
    }

    /**
     * Permite escanear la foto qu se tomo para colocarla en el carrete de la galeria de Android
     */
    private fun anadirImagenGalria(){
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val file = File(urlFotoActual)
        val uri = Uri.fromFile(file)
        intent.setData(uri)
        activity.sendBroadcast(intent)
    }

}