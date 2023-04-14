package br.com.biexpert.bicm


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)


        findViewById<View>(R.id.btnChangePassword).setOnClickListener {
            changePassword()
        }
    }

    private fun notEmpty(): Boolean = etPassword.text.toString().trim().isNotEmpty() &&
            etConfirmPassword.text.toString().trim().isNotEmpty()

    private fun verifySizePassword(): Boolean{
        var correct = false
        if(etPassword.text.toString().trim().length >= 6) {
            correct = true
        }
        return correct;
    }

    fun changePassword(){
        if(notEmpty()) {
            if (verifySizePassword()) {
                if (etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()) {
                    val user = firebaseAuth.currentUser

                    user!!.updatePassword(etPassword.text.toString().trim())
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, R.string.Sucess, Toast.LENGTH_SHORT).show()
                            }
                        }

                    val activity = Intent(this, UserProfileActivity::class.java);
                    startActivity(activity)
                    finish()
                } else {
                    Toast.makeText(this, R.string.err_senha_nao_confere, Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, R.string.err_senha_mtocurta, Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, R.string.err_campos_obrigatorios, Toast.LENGTH_SHORT).show()
        }
    }
}

