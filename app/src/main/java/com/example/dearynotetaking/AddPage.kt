package com.example.dearynotetaking
import android.Manifest
import android.app.DatePickerDialog
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

class AddPage : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var editTextDate: Button
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var imageViewPreview: ImageView
    private lateinit var buttonAddImage: ImageButton
    private lateinit var saveButton: FloatingActionButton

    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null

    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private lateinit var requestCameraPermissionLauncher: ActivityResultLauncher<String>

    private var isEdit = false
    private var editingNoteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_page)
        dbHelper = DatabaseHelper(this)

        // Initialize UI elements
        editTextDate = findViewById(R.id.editTextDate)
        editTextTitle = findViewById(R.id.editTextTitle)
        editTextDescription = findViewById(R.id.editTextDescription)
        imageViewPreview = findViewById(R.id.imagePlaceholder)
        buttonAddImage = findViewById(R.id.addImageButton)
        saveButton = findViewById(R.id.save_new)

        // Initialize ActivityResultLaunchers
        initializeLaunchers()

        // Check if we are in edit mode
        isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            editingNoteId = intent.getIntExtra("note_id", -1)
            val noteDate = intent.getStringExtra("note_date")
            val noteTitle = intent.getStringExtra("note_title")
            val noteDesc = intent.getStringExtra("note_desc")
            val noteImg = intent.getStringExtra("note_img")

            editTextDate.text = noteDate
            editTextTitle.setText(noteTitle)
            editTextDescription.setText(noteDesc)

            if (!noteImg.isNullOrEmpty()) {
                val imgFile = File(noteImg)
                if (imgFile.exists()) {
                    selectedImageUri = Uri.fromFile(imgFile)
                    imageViewPreview.setImageURI(selectedImageUri)
                    imageViewPreview.visibility = View.VISIBLE
                    currentPhotoPath = noteImg // Set currentPhotoPath for updates
                } else {
                    imageViewPreview.visibility = View.GONE
                }
            } else {
                imageViewPreview.visibility = View.GONE
            }
        } else {
            // New entry, set current date as default
            val calendar = Calendar.getInstance()
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = formatter.format(calendar.time)
            editTextDate.text = formattedDate
        }

        // Set listeners
        editTextDate.setOnClickListener {
            showDatePicker()
        }

        buttonAddImage.setOnClickListener {
            showImageSourceDialog()
        }

        saveButton.setOnClickListener {
            saveNote() // This will now handle both insert and update
        }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datepicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, dayOfMonth ->
                editTextDate.text = "$dayOfMonth/${selectedMonth + 1}/$selectedYear"
            }, year, month, day
        )
        datepicker.show()
    }

    private fun saveNote() {
        val title = editTextTitle.text.toString().trim()
        val date = editTextDate.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        if (title.isBlank() || description.isBlank() || date.isBlank()) {
            Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        var imagePathToSave: String? = currentPhotoPath
        if (selectedImageUri != null) {
            // Check if the URI is a content URI (from gallery) and needs to be copied
            if (selectedImageUri.toString().startsWith("content://")) {
                imagePathToSave = saveImageToAppSpecificStorage(selectedImageUri!!)
            } else {
                // If it's a file URI (from camera), the path is already set in currentPhotoPath
                imagePathToSave = currentPhotoPath
            }
        }

        if (isEdit) {
            val updated = dbHelper.updateData(
                editingNoteId.toString(),
                date,
                title,
                description,
                imagePathToSave
            )
            if (updated > 0) {
                Toast.makeText(this, "Page updated!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Failed to update page.", Toast.LENGTH_SHORT).show()
            }
        } else {
            val id = dbHelper.insertData(date, title, description, imagePathToSave)
            if (id > 0) {
                Toast.makeText(this, "Deary page added!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Couldn't make a page. Database error.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeLaunchers() {
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                selectedImageUri = Uri.fromFile(File(currentPhotoPath!!))
                imageViewPreview.setImageURI(selectedImageUri)
                imageViewPreview.visibility = View.VISIBLE
            } else {
                Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
                currentPhotoPath = null
                selectedImageUri = null
                imageViewPreview.visibility = View.GONE
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                imageViewPreview.setImageURI(selectedImageUri)
                imageViewPreview.visibility = View.VISIBLE
                currentPhotoPath = null // Reset path as we're now dealing with a content URI
            }
        }

        requestCameraPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showImageSourceDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Remove Image", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add/Remove Image")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> {
                    checkCameraPermissionAndTakePhoto()
                }
                "Choose from Gallery" -> {
                    pickImageFromGallery()
                }
                "Remove Image" -> {
                    selectedImageUri = null
                    currentPhotoPath = null
                    imageViewPreview.setImageResource(R.color.gray) // Reset to placeholder
                    imageViewPreview.visibility = View.VISIBLE
                }
                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun checkCameraPermissionAndTakePhoto() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(this, "Error creating image file: ${ex.message}", Toast.LENGTH_SHORT).show()
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",
                it
            )
            takePictureLauncher.launch(photoURI)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun pickImageFromGallery() {
        pickImageLauncher.launch("image/*")
    }

    private fun saveImageToAppSpecificStorage(uri: Uri): String? {
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "JPEG_${timeStamp}_copied.jpg"
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (storageDir == null || !storageDir.exists()) {
                storageDir?.mkdirs()
            }
            val file = File(storageDir, fileName)
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save image copy: ${e.message}", Toast.LENGTH_SHORT).show()
            return null
        }
    }
}