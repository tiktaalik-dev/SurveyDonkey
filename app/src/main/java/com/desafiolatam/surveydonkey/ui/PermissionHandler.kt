
package com.desafiolatam.surveydonkey.ui

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

interface PermissionHandler {

    private fun permissionGranted(context: Context) {
        Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
    }

    private fun permissionDenied(context: Context) {
        Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    fun createPermissionLauncher(context: Context): ActivityResultLauncher<String> {

        // Crear variable de trabajo
        val contract = ActivityResultContracts.RequestPermission()

        // Retornar el lanzador
        return (context as AppCompatActivity).registerForActivityResult(contract) { isGranted ->
            if (isGranted) {
                permissionGranted(context)
            } else {
                permissionDenied(context)
            }
        }
    }

    fun requestPermission(context: Context, activity: Activity, requestedPermission: String) {

        // Consultar al sistema el estado del permiso solicitado para esta app
        val permissionStatus = ContextCompat.checkSelfPermission(context, requestedPermission)

        // Consultar al sistema si es necesario justificar ante el usuario el conceder el permiso
        val shouldJustifyPermission = ActivityCompat.shouldShowRequestPermissionRationale(activity, requestedPermission)

        // Decidir qué hacer según las respuestas del sistema
        when (permissionStatus) {

            // Si el permiso fue concedido derivar la acción
            PackageManager.PERMISSION_GRANTED -> {
                permissionGranted(context)
            }

            // Si el permiso no ha sido concedido, pedirlo
            else -> {
                // Si es necesario, justificar el porqué es necesario el permiso
                if (shouldJustifyPermission) {
                    Toast.makeText(context,
                        "Este permiso es necesario para el normal funcionamiento de la aplicación. Por favor, concédelo.",
                        Toast.LENGTH_SHORT).show()
                }
                // Pedir el permiso al usuario
                createPermissionLauncher(context).launch(requestedPermission)
            }
        }
    }
}


/*import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

interface PermissionHandler {

    // Crear variables para el contexto y la actividad
    val currentContext: Context
    val currentActivity: Activity

    private fun permissionGranted(){
        Toast.makeText(currentContext,"Permiso concedido", Toast.LENGTH_SHORT).show()
    }

    private fun permissionDenied(){
        Toast.makeText(currentContext,"Permiso denegado", Toast.LENGTH_SHORT).show()
    }

    fun createPermissionLauncher(): ActivityResultLauncher<String> {
        return (currentContext as AppCompatActivity).registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                permissionGranted()
            } else {
                permissionDenied()
            }
        }
    }

    fun requestPermission(requestedPermission: String) {

        // Consultar al sistema el estado del permiso solicitado para esta app
        val permissionStatus = ContextCompat.checkSelfPermission(currentContext, requestedPermission)

        // Consultar al sistema si es necesario justificar ante el usuario el conceder el permiso
        val shouldJustifyPermission = ActivityCompat.shouldShowRequestPermissionRationale(currentActivity, requestedPermission)

        // Decidir qué hacer según las respuestas del sistema
        when (permissionStatus){

            // Si el permiso fue concedido derivar la acción
            PackageManager.PERMISSION_GRANTED -> { permissionGranted() }

            // Si el permiso no ha sido concedido, pedirlo
            else -> {
            // Si es necesario, justificar el porqué es necesario el permiso
            if (shouldJustifyPermission) {
                Toast.makeText(currentContext,"Solicitar nuevamente el permiso...",Toast.LENGTH_SHORT).show()
            }
                // Pedir el permiso al usuario
                createPermissionLauncher().launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

}*/
