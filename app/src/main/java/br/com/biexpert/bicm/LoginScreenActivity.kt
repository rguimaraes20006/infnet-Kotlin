package br.com.biexpert.bicm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class LoginScreenActivity : AppCompatActivity() {

    //campos
    lateinit var txtEmail : EditText
    lateinit var txtPassword : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        //remove a actionbar
        supportActionBar?.hide()


        txtEmail = findViewById<EditText>(R.id.etEmail)
        txtPassword = findViewById<EditText>(R.id.etPassword)

        findViewById<View>(R.id.registerView).setOnClickListener{
            val activity = Intent(this, CreateAccountActivity::class.java);
            startActivity(activity);
        }

        findViewById<View>(R.id.SigninGoogle).setOnClickListener {
            Toast.makeText(this, "Entrar google", Toast.LENGTH_SHORT).show()




        }

        findViewById<View>(R.id.btnEnter).setOnClickListener {
            Toast.makeText(this, "Entrando com login normal", Toast.LENGTH_SHORT).show()
        }

    }
}