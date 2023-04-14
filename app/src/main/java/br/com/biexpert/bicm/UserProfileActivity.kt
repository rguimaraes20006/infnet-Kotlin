
package br.com.biexpert.bicm



import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class UserProfileActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CAMERA = 0;
    private val PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;

    var _image: Bitmap? = null;

    companion object{
        private const val REQUEST_IMAGE_GALLERY = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        supportActionBar?.hide()


        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();



        val user = firebaseAuth.currentUser
        if(user != null){
            val displayName = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            if(displayName.toString() != "") {
                val nameSplit = displayName.toString().split(" ")
                if(nameSplit.size > 1){
                    findViewById<EditText>(R.id.name).setText(nameSplit[0])
                    findViewById<EditText>(R.id.last_name).setText(nameSplit[1])
                }else{
                    findViewById<EditText>(R.id.name).setText(displayName.toString())
                }
            }
            findViewById<EditText>(R.id.email).setText(email)

            if(photoUrl != null){
                Thread{
                    val file = saveLocalFile(photoUrl.toString())
                    runOnUiThread{
                        val bitmap = BitmapFactory.decodeFile(file.path)
                        findViewById<ImageView>(R.id.profile_image).setImageBitmap(bitmap)
                    }
                }.start()
            }
        }

        findViewById<View>(R.id.update_profile).setOnClickListener{
            saveProfile()
        }

        findViewById<View>(R.id.home).setOnClickListener{
            val activity = Intent(this, MainActivity::class.java);
            startActivity(activity)
            finish()
        }

        findViewById<View>(R.id.fab_files).setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
        }

        findViewById<View>(R.id.fab_camera).setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_READ_EXTERNAL_STORAGE)
        }

        findViewById<View>(R.id.logout).setOnClickListener{
            firebaseAuth.signOut();

            val activity = Intent(this, LoginScreenActivity::class.java);
            startActivity(activity)
            finish()
        }

        findViewById<View>(R.id.change_password).setOnClickListener {
            val activity = Intent(this, ChangePasswordActivity::class.java);
            startActivity(activity)
            finish()
        }
    }

    fun saveLocalFile(_url: String): File{
        val url = URL(_url)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val input = connection.inputStream
        val dir = File(getExternalFilesDir(null), "images")
        if(!dir.exists()){
            dir.mkdir()
        }

        val file = File(dir, "imagem.jpg")
        val output = FileOutputStream(file)

        val buffer = ByteArray(1024)
        var read: Int;
        while(input.read(buffer).also { read = it} != -1){
            output.write(buffer, 0, read)
        }

        output.flush()
        output.close()
        input.close()

        return file
    }

    fun saveProfile(){

        val name = findViewById<EditText>(R.id.name).text.toString()
        val last_name = findViewById<EditText>(R.id.last_name).text.toString()



        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        val imageRef = storageRef.child("profile_images/${uid}")

        val arrImg = ByteArrayOutputStream()
        this._image?.compress(Bitmap.CompressFormat.JPEG, 100, arrImg)

        val data = arrImg.toByteArray()
        val uploadTask = imageRef.putBytes(data)


        uploadTask.addOnFailureListener {
            Toast.makeText(this,  getString(R.string.database_error), Toast.LENGTH_SHORT).show()
        }.addOnSuccessListener { _ ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                val profileUpdates = userProfileChangeRequest {
                    displayName = "${name} ${last_name}"
                    photoUri = Uri.parse(uri.toString())
                }

                user!!.updateProfile(profileUpdates).addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this,  getString(R.string.Sucess), Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,  getString(R.string.database_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_CAMERA){
            if(!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(this, getString(R.string.perm_camera_msg), Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE){
            if(!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(this, getString(R.string.perm_gallery_msg), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK){
            when(requestCode){
                REQUEST_IMAGE_GALLERY -> {
                    val selectedImage: Uri? = data?.data
                    val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage)

                    findViewById<ImageView>(R.id.profile_image).setImageBitmap(imageBitmap)
                    this._image = imageBitmap
                }
                REQUEST_IMAGE_CAPTURE -> {
                    val imageCaptured =  data?.extras?.get("data") as Bitmap
                    findViewById<ImageView>(R.id.profile_image).setImageBitmap(imageCaptured)
                    this._image = imageCaptured
                }
            }
        }
    }
}