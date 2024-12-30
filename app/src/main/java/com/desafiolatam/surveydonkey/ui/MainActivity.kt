package com.desafiolatam.surveydonkey.ui

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.desafiolatam.surveydonkey.databinding.ActivityMainBinding
import com.desafiolatam.surveydonkey.ui.adapter.SurveyPagerAdapter
import com.desafiolatam.surveydonkey.ui.fragment.FileHelper

class MainActivity : AppCompatActivity(), PermissionHandler {

//    // Sobrescribir el contexto y la actividad de la interface
//    override val currentContext: Context = this
//    override val currentActivity: Activity = this

    // Declarar la vista, el adaptador, y el FileHelper
    lateinit var binding: ActivityMainBinding
    lateinit var viewPager: SurveyPagerAdapter
    lateinit var fileHelper: FileHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        // Llamar al constructor de la superclase
        super.onCreate(savedInstanceState)

        // Inflar la vista
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Establecer la vista
        setContentView(binding.root)

        // Inicializar FileHelper para que esté disponible cuando el último fragmento lo necesite
        fileHelper = FileHelper(this)
        fileHelper.initialize(this)

        // Pedir el permiso de ubicación
        requestPermission(this, this, android.Manifest.permission.ACCESS_COARSE_LOCATION)

        // Pedir el permiso de internet
        requestPermission(this, this, android.Manifest.permission.INTERNET)

        // Instanciar el adaptador
        viewPager = SurveyPagerAdapter(this)

        // Establecer el adaptador en el ViewPager
        binding.vpMain.adapter = viewPager

        // Colocar un escuchador de clics en el botón de acción flotante
        binding.fab.setOnClickListener {

            // Decidir qué hacer según la página actual del ViewPager que está visible
            when (binding.vpMain.currentItem) {
                0 -> binding.vpMain.setCurrentItem(1, true)
                1 -> binding.vpMain.setCurrentItem(2, true)
                2 -> binding.vpMain.setCurrentItem(3, true)
                3 -> binding.vpMain.setCurrentItem(4, true)
                4 -> binding.vpMain.setCurrentItem(5, true)
                else -> binding.vpMain.setCurrentItem(0, true)
            }
        }
    }
}