package br.com.biexpert.bicm


import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.biexpert.bicm.fragments.EmailValidator

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var txtEmail: EmailValidator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

        findViewById<View>(R.id.textEntrar).setOnClickListener {
            //so pode voltar pra login pois se voltar pro main vai rolar automaticamente o redirect pro login
            finish()
        }

        findViewById<View>(R.id.btnRecuperar).setOnClickListener {

            //val emailAddress = //findViewById<EditText>(R.id.etEmail).text.toString()


            txtEmail =
                supportFragmentManager.findFragmentById(R.id.emailValidator) as EmailValidator

            if (txtEmail.isValid) {

                firebaseAuth.sendPasswordResetEmail(txtEmail.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                getString(R.string.email_enviado_sucesso),
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
            } else {

                Toast.makeText(
                    this,
                    getString(R.string.invalid_email),
                    Toast.LENGTH_SHORT
                ).show()


            }


        }
    }


}