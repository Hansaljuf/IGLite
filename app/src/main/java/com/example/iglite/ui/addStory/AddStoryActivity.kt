package com.example.iglite.ui.addStory

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.load
import com.example.iglite.R
import com.example.iglite.databinding.ActivityAddStoryBinding
import com.example.iglite.ui.main.MainActivity
import com.example.iglite.util.createCustomTempFile
import com.example.iglite.util.reduceFileImage
import com.example.iglite.util.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@AndroidEntryPoint
class AddStoryActivity : AppCompatActivity() {

    private val viewModel: AddStoryViewModel by viewModels()
    private var _binding: ActivityAddStoryBinding? = null
    private val binding get() = _binding
    private lateinit var currentPhotoPath: String
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, "Tidak mendapatkan permission.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val token = intent.getStringExtra("TOKEN")!!

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        playAnimation()

        binding?.apply {
            cameraButton.setOnClickListener { startTakePhoto() }
            galleryButton.setOnClickListener { startGallery() }
            buttonAdd.setOnClickListener { uploadImage(token) }
            switchLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getCurrentLocation()
                } else {
                    latitude = 0.0
                    longitude = 0.0
                }
            }
        }
    }

    private fun uploadImage(token: String) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description =
                binding?.edAddDescription?.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo", file.name, requestImageFile
            )
            val latitudeRequestBody = latitude.toString().toRequestBody("text/plain".toMediaType())
            val longitudeRequestBody =
                longitude.toString().toRequestBody("text/plain".toMediaType())

            viewModel.instagramLiteAddStory(
                token, imageMultipart, description, latitudeRequestBody, longitudeRequestBody
            )
            viewModel.addNewStoryResult.observe(this) {
                if (it.error == false) {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
            }
        } else {
            Toast.makeText(
                this@AddStoryActivity,
                getString(R.string.error_message_add_story),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity, "com.example.iglite", it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
//              Silakan gunakan kode ini jika mengalami perubahan rotasi
//              rotateFile(file)
                getFile = file
                binding?.previewImageView?.load(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding?.previewImageView?.load(uri)
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.previewImageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val cameraButton =
            ObjectAnimator.ofFloat(binding?.cameraButton, View.ALPHA, 1f).setDuration(550)
        val galleryButton =
            ObjectAnimator.ofFloat(binding?.galleryButton, View.ALPHA, 1f).setDuration(550)
        val descriptionEditText =
            ObjectAnimator.ofFloat(binding?.edAddDescription, View.ALPHA, 1f).setDuration(550)
        val switchLocationText = ObjectAnimator.ofFloat(
            binding?.switchLocationText, View.ALPHA, 1f
        ).setDuration(550)
        val switchLocation =
            ObjectAnimator.ofFloat(binding?.switchLocation, View.ALPHA, 1f).setDuration(550)
        val uploadButton =
            ObjectAnimator.ofFloat(binding?.buttonAdd, View.ALPHA, 1f).setDuration(550)
        val playTogether = AnimatorSet().apply { playTogether(cameraButton, galleryButton) }

        AnimatorSet().apply {
            playSequentially(
                switchLocationText, switchLocation, descriptionEditText, playTogether, uploadButton,
            )
            start()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            resultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getCurrentLocation()
            } else {
                Toast.makeText(
                    this, "Location permission not granted", Toast.LENGTH_SHORT
                ).show()
            }
        }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TOKEN = "TOKEN"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}