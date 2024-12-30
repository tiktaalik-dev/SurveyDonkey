package com.desafiolatam.surveydonkey.ui.fragment

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.desafiolatam.surveydonkey.databinding.FragmentEndBinding
import com.desafiolatam.surveydonkey.ui.MainActivity
import com.desafiolatam.surveydonkey.ui.PermissionHandler
import com.desafiolatam.surveydonkey.viewmodel.MainViewModel

class EndFragment : Fragment(), PermissionHandler {

    // Declarar el binding para el fragmento
    private var _binding: FragmentEndBinding? = null
    private val binding get() = _binding!!

    // Declarar el ViewModel
    private val viewModel: MainViewModel by activityViewModels()

    // Declarar el FileHelper
    private lateinit var fileHelper: FileHelper

    private fun cargarDatos(){
        binding.tvFirstAnswer.text = viewModel.getFirstResult()
        binding.tvSecondAnswer.text = viewModel.getSecondResult()
        binding.tvThirdQuestion.text = viewModel.getThirdResult()
        binding.tvUserEmail.text = viewModel.getUserEmail()
        binding.tvUserSuggest.text = viewModel.getUserSuggest()
    }

    private fun writeFile(uri: Uri, fileContent: String) {
        fileHelper.writeFile(uri, fileContent)
        Toast.makeText(context, "El archivo fue escrito exitosamente", Toast.LENGTH_SHORT).show()
    }

    private fun saveToDisk(): Unit {
        // Show dialog to get file name
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("File Name")
        val input = EditText(requireContext())
        builder.setView(input)
        builder.setPositiveButton("OK") { _, _ ->
            val fileName = input.text.toString() + ".txt"
            val mimeType = "text/plain"
            val allData = viewModel.collectData()
            fileHelper.createFile(fileName, mimeType) { uri ->
                if (uri != null) {
                    // File created successfully, now write content
                    writeFile(uri, allData)
                } else {
                    // File creation failed
                    Toast.makeText(context, "El archivo no pudo ser creado", Toast.LENGTH_SHORT).show()
                }
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun onAttach(context: Context) {
        // Ejecutar el método padre
        super.onAttach(context)

        // Recuperar el fileHelper desde la Actividad
        if (context is MainActivity) {
            fileHelper = context.fileHelper
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inicializar el binding
        _binding = FragmentEndBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Ejecutar el método padre
        super.onViewCreated(view, savedInstanceState)

        // Cargar en la vista los datos del ViewModel
        cargarDatos()

        // Pedir permiso de escritura en el disco para versiones antiguas de Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            requestPermission(requireContext(), requireActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            Toast.makeText(requireContext(), "Elige dónde guardar el archivo con las respuestas...", Toast.LENGTH_SHORT).show()
        }
        // Guardar los datos en el disco
        saveToDisk()
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    override fun onPause() {
        super.onPause()
        cargarDatos()
    }
}