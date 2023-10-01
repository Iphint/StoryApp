package com.arifin.newest.view.tambah

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import app.story.mystoryappneww.response.createCustomTempFile
import app.story.mystoryappneww.response.reduceFileImage
import app.story.mystoryappneww.response.rotateFile
import app.story.mystoryappneww.response.uriToFile
import com.arifin.newest.R
import com.arifin.newest.data.UserRepository
import com.arifin.newest.data.preference.UserPreference
import com.arifin.newest.data.response.TambahResponse
import com.arifin.newest.data.retrofit.ApiConfig
import com.arifin.newest.databinding.ActivityTambahBinding
import com.arifin.newest.view.ViewModelFactory
import com.arifin.newest.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class TambahActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private lateinit var binding: ActivityTambahBinding
    private lateinit var viewModel: TambahActivityViewModel
    private var selectedFile: File? = null
    private lateinit var pref: UserPreference
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lat: String? = null
    private var lon: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = UserPreference.getInstance(this.dataStore)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory).get(TambahActivityViewModel::class.java)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLastLocation()
            }
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.buttonAdd.setOnClickListener { uploadImage() }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupView()
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    lat = location.latitude.toString()
                    lon = location.longitude.toString()
                    Log.d("LocationSuccess", "Successfully got location: lat=$lat, lon=$lon")
                } else {
                    Log.d("LocationSuccess", "Location object is null")
                }
            }.addOnFailureListener { e ->
                Log.e("LocationError", "Failed to get location: ${e.message}")
            }
        } else {
            Log.e("LocationPermission", "Location permission not granted")
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih Gambar")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@TambahActivity, "com.arifin.newest", it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@TambahActivity)
                selectedFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }

    private lateinit var currentPhotoPath: String

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                rotateFile(file)
                selectedFile = myFile
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun uploadImage() {
        selectedFile?.let { file ->
            val reducedFile = reduceFileImage(file)
            val desc = binding.edAddDescription.text.toString()
            if (desc.isEmpty()) {
                binding.descEditTextLayout.error = getString(R.string.empty_description)
            } else {
                val description = desc.toRequestBody("text/plain".toMediaType())
                val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    reducedFile.name,
                    requestImageFile
                )

                val lat = lat?.toRequestBody("text/plain".toMediaType())
                val lon = lon?.toRequestBody("text/plain".toMediaType())

                Log.d("LocationPost", "Preparing to post location: lat=$lat, lon=$lon")

                viewModel.getUser().observe(this) { user ->
                    val call: Call<TambahResponse> = ApiConfig.getApiServices().addNewStory(
                        token = "Bearer ${user.token}",
                        description = description,
                        file = imageMultipart,
                        lat = lat,
                        lon = lon
                    )
                    call.enqueue(object : Callback<TambahResponse> {
                        override fun onResponse(
                            call: Call<TambahResponse>,
                            response: Response<TambahResponse>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody != null) {
                                    Toast.makeText(
                                        this@TambahActivity,
                                        responseBody.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.d("UploadImage", "Gambar berhasil diunggah: ${responseBody.message}")
                                    Log.d("LocationPost", "Successfully posted location: lat=$lat, lon=$lon")
                                    val intent = Intent(this@TambahActivity, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@TambahActivity,
                                        response.message(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Log.e("UploadImage", "Gagal mengunggah gambar: Response body null")
                                }
                            } else {
                                Toast.makeText(
                                    this@TambahActivity,
                                    "Gagal mengunggah gambar",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<TambahResponse>, t: Throwable) {
                            Toast.makeText(
                                this@TambahActivity,
                                "Terjadi kesalahan saat mengunggah gambar: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("UploadImage", "Terjadi kesalahan saat mengunggah gambar: ${t.message}")
                        }
                    })
                }
            }
        } ?: run {
            Toast.makeText(this, getString(R.string.empty_img), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                getLastLocation()
            } else {
                Toast.makeText(
                    this, getString(R.string.not_permission), Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}