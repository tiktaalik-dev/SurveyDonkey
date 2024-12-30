package com.desafiolatam.surveydonkey.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.os.Build
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.extension.fromTreeUri
import java.io.File
import java.io.FileOutputStream
import android.content.Context
import android.os.Bundle
import androidx.documentfile.provider.DocumentFile
import android.util.Log
import java.io.IOException

class FileHelper(private val activity: AppCompatActivity) {

    private lateinit var createDocumentLauncher: ActivityResultLauncher<Intent>
    private var onFileCreated: ((Uri?) -> Unit)? = null
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var storageHelper: SimpleStorageHelper
    private var selectedFolder: Uri? = null

    fun initialize(activity: AppCompatActivity) {
        storageHelper = SimpleStorageHelper(activity)
        createDocumentLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                onFileCreated?.invoke(uri)
            } else {
                onFileCreated?.invoke(null)
            }
        }
        filePickerLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                onFileCreated?.invoke(uri)
            } else {
                onFileCreated?.invoke(null)
            }
        }
        storageHelper.onFileSelected = { _, files ->
            onFileCreated?.invoke(files.firstOrNull()?.uri)
        }
        storageHelper.onFileCreated = { _, file ->
            onFileCreated?.invoke(file.uri)
        }
        storageHelper.onFolderSelected = { _, folder ->
            selectedFolder = folder.uri
        }
    }

    fun createFile(fileName: String, mimeType: String, onFileCreated: (Uri?) -> Unit) {
        this.onFileCreated = onFileCreated
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use Storage Access Framework for Android 10 and higher
            if (selectedFolder == null) {
                openFolderPicker()
                return
            }
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = mimeType
                putExtra(Intent.EXTRA_TITLE, fileName)
            }
            createDocumentLauncher.launch(intent)
        } else {
            // Use file picker for older Android versions
            if (selectedFolder == null) {
                openFolderPicker()
                return
            }
            selectedFolder?.let { uri ->
                val documentFile = activity.fromTreeUri(uri)
                val file = documentFile?.createFile(mimeType, fileName)
                onFileCreated(file?.uri)
            } ?: run {
                onFileCreated(null)
            }
        }
    }

    private fun launchFilePicker(fileName: String, mimeType: String) {
        storageHelper.openFilePicker(filterMimeTypes = arrayOf(mimeType))
    }

    private fun openFolderPicker() {
        storageHelper.openFolderPicker()
    }

    fun writeFile(uri: Uri, content: String) {
        Log.d("FileHelper", "Writing to URI: $uri")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                activity.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(content.toByteArray())
                    outputStream.flush()
                }
            } catch (e: IOException) {
                Log.e("FileHelper", "Error writing to file", e)
            }
        } else {
            // Handle writing to file for older versions
            try {
                val file = File(uri.path)
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(content.toByteArray())
                    outputStream.flush()
                }
            } catch (e: Exception) {
                Log.e("FileHelper", "Error writing to file", e)
            }
        }
    }
    fun onSaveInstanceState(outState: Bundle) {
        storageHelper.onSaveInstanceState(outState)
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle) {
        storageHelper.onRestoreInstanceState(savedInstanceState)
    }
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        storageHelper.storage.onActivityResult(requestCode, resultCode, data)
    }
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        storageHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}