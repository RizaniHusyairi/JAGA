package com.example.jaga

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.jaga.databinding.ActivityEditProfileBinding
import com.example.jaga.ui.SettingActivity
import com.example.jaga.ui.VerifyActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.IOException
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var reference: DatabaseReference
    private lateinit var filePath: Uri
    private lateinit var storageReference: StorageReference
    private lateinit var editViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupViewModel()

        binding.btnBack.setOnClickListener {
            val btnBack = Intent(this,SettingActivity::class.java)
            startActivity(btnBack)
        }

        reference = FirebaseDatabase.getInstance().getReference(VerifyActivity.USERS)
        editViewModel.getUser().observe(this) {
            id_user = it.id
            number_user = it.number
            storageReference = FirebaseStorage.getInstance().getReference(it.id!!)

            if (it.foto == null) {
                Glide.with(this)
                    .load(R.drawable.foto_default)
                    .circleCrop()
                    .into(binding.userProfil)
            } else {
                Log.e("nama_foto" , it.foto!!)
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Sedang Menyiapkan gambar...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                val cekfoto = storageReference.child("images/${it.foto}")
                try {
                    val localTemp = File.createTempFile("tempfile",".jpg")
                    cekfoto.getFile(localTemp).addOnSuccessListener{
                        if(progressDialog.isShowing)
                            progressDialog.dismiss()

                        val bitmap = BitmapFactory.decodeFile(localTemp.absolutePath)

                        Glide.with(this)
                            .load(cekfoto.child("${id_user}/images/${bitmap}"))
                            .circleCrop()
                            .into(binding.userProfil)
                    }.addOnFailureListener{ error ->
                        if(progressDialog.isShowing)
                            progressDialog.dismiss()

                        Toast.makeText(this, "Gambar Error: ${error.message}",Toast.LENGTH_SHORT).show()

                    }

                }catch (e:IOException){
                    e.printStackTrace()
                }

            }

            binding.apply {
                namaUser.editText?.setText(it.name)
                tglLahirText.setText(it.tgl_lahir)
                tentang.editText?.setText(it.tentang)
            }
        }


        val calendar = Calendar.getInstance();
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.tglLahir.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val dialog: DatePickerDialog =
                    DatePickerDialog(this,
                        { p0, year, month, dayOfMonth ->
                            val month2 = month + 1
                            val date: String = "$dayOfMonth/$month2/$year"
                            binding.tglLahirText.setText(date)
                        }, year, month, day
                    )
                dialog.show()
            } else {
                Toast.makeText(this@EditProfileActivity, "Calendar Error", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        binding.btnUbahFoto.setOnClickListener {
            selectImage()
        }


        binding.btnSaveEdit.setOnClickListener {
            uploadImage()

        }


    }

    private fun selectImage() {
        val fileImg = Intent()
        fileImg.type = "image/*"
        fileImg.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                fileImg,
                "Pilih photo profil.."
            ),
            22
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 22
            && resultCode == RESULT_OK
            && data != null
            && data.data != null
        ) {
            filePath = data.data!!
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    filePath
                )
                binding.userProfil.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    private fun uploadImage() {
        if (filePath != null) {
            val progressDialog: ProgressDialog = ProgressDialog(this)
            progressDialog.setTitle("Sedang Mengunggah foto...")
            progressDialog.show()

            val ref: StorageReference = storageReference.child("$id_user/images")

            ref.putFile(filePath).addOnSuccessListener {
                progressDialog.dismiss();
                reference.child(id_user!!).child("foto").setValue(filePath.toString())

                Toast
                    .makeText(
                        this,
                        "Foto Berhasil Diunggah!!",
                        Toast.LENGTH_SHORT
                    )
                    .show();
            }.addOnFailureListener {
                progressDialog.dismiss();
                Toast
                    .makeText(
                        this,
                        "Foto gagal diunggah : ${it.message}",
                        Toast.LENGTH_SHORT
                    )
                    .show();
            }.addOnProgressListener {
                val progress:Double
                = (100.0
                    * it.bytesTransferred
                    / it.totalByteCount);
                progressDialog.setMessage(
                    "Uploaded "
                            + progress.toInt() + "%");
            }
        }
        reference.child(id_user!!).child("name").setValue(binding.namaUser.editText?.text.toString())
        reference.child(id_user!!).child("tgl_lahir").setValue(binding.tglLahir.editText?.text.toString())
        reference.child(id_user!!).child("tentang").setValue(binding.tentang.editText?.text.toString())
        val dataUser = User(
            id_user,
            number_user,
            binding.namaUser.editText?.text.toString(),
            binding.tglLahir.editText?.text.toString(),
            binding.tentang.editText?.text.toString(),
            filePath


        )
        editViewModel.login(dataUser)


    }


    private fun setupViewModel() {
        editViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }

    companion object {
        var id_user: String? = null
        var number_user: String? = null
    }


}