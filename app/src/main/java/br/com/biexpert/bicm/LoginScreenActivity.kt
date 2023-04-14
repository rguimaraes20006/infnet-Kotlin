package br.com.biexpert.bicm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import br.com.biexpert.bicm.fragments.EmailValidator
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import  com.google.firebase.auth.GoogleAuthProvider

class LoginScreenActivity : AppCompatActivity() {

    //campos
    lateinit var txtEmail: EmailValidator
    lateinit var txtPassword: EditText

    lateinit var mGoogle: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)


        //region google e fb
        var gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()


        mGoogle = GoogleSignIn.getClient(this, gso);

        firebaseAuth = FirebaseAuth.getInstance()

        //endregion


        //remove a actionbar
        supportActionBar?.hide()


        //txtEmail = findViewById<EditText>(R.id.etEmail)
        txtEmail = supportFragmentManager.findFragmentById(R.id.etEmail) as EmailValidator

        txtPassword = findViewById<EditText>(R.id.etPassword)

        findViewById<View>(R.id.registerView).setOnClickListener {

            val activity = Intent(this, CreateAccountActivity::class.java);
            startActivity(activity);
        }



        findViewById<View>(R.id.btnEnter).setOnClickListener {

            loginFirebase()

        }

        findViewById<View>(R.id.SigninGoogle).setOnClickListener {
            // Toast.makeText(this, "Entrar google", Toast.LENGTH_SHORT).show()
            signInGoogle()
        }

        findViewById<View>(R.id.forgotPasswd).setOnClickListener {
            val activity = Intent(this, ForgotPasswordActivity::class.java);
            startActivity(activity);
        }





    }

    //region GOOGLE
    private fun signInGoogle() {
        var signItent: Intent = mGoogle.signInIntent
        startActivityForResult(signItent, Req_Code)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Req_Code) {
            var result = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleResult(result);
        }

    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {

        try {
            var account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)

            if (account != null) {
                UpdateUser(account)
            }

        } catch (e: ApiException) {
            println(e)
        }
    }

    private fun UpdateUser(account: GoogleSignInAccount) {
        var credential = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                var activity = Intent(this, MainActivity::class.java)
                startActivity((activity))
                finish()
            }
        }


    }

    //endregion


    //region LoginFirebase
    private fun loginFirebase() {

        if (!txtEmail.text.toString().isNullOrEmpty() && !txtPassword.text.toString()
                .isNullOrEmpty() && txtEmail.isValid
        ) {

            firebaseAuth.signInWithEmailAndPassword(
                txtEmail.text.toString().trim(),
                txtPassword.text.toString().trim()
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
                        if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else if (firebaseUser != null && !firebaseUser.isEmailVerified) {
                            firebaseAuth.signOut()
                            Toast.makeText(
                                this,
                                getString(R.string.err_emailnverificado),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {


                            Toast.makeText(this, task.exception?.message.toString() ,
                                Toast.LENGTH_LONG ).show()

                        }
                    } else {
                        Toast.makeText(this, task.exception?.message.toString() ,
                            Toast.LENGTH_LONG ).show()

                      //  Toast.makeText(this, R.string.err_generico, Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, R.string.err_campos_obrigatorios, Toast.LENGTH_SHORT).show()
        }
    }
//endregion


}