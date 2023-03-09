package br.com.biexpert.bicm

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson

class CreateAccountActivity : AppCompatActivity() {

    lateinit var mGoogle: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth

    lateinit var txtEmail: EditText
    lateinit var txtPassword: EditText
    lateinit var txtPassword2: EditText
    lateinit var createAccountInputArray: Array<EditText>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        txtEmail = findViewById(R.id.etEmail)
        txtPassword = findViewById(R.id.etPassword)
        txtPassword2 = findViewById(R.id.etConfirmPassword)

        //region GOOGLE

        var gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        ).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        mGoogle = GoogleSignIn.getClient(this, gso);

        //endregion

        //region Firebase
        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()
        createAccountInputArray = arrayOf(txtEmail, txtPassword, txtPassword2)

        findViewById<View>(R.id.btnCreateAccount).setOnClickListener {
            createAccount()
        }

        //endregion

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


    //region Firebase
    private fun isValid(): Boolean {

        if (txtEmail.text.toString().trim().isNullOrEmpty() || txtPassword.text.toString().trim()
                .isNullOrEmpty() || txtPassword2.text.toString().trim().isNullOrEmpty()
        ) {
            Toast.makeText(this, getString(R.string.err_campos_obrigatorios), Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (txtPassword.text.toString().trim() != txtPassword2.text.toString().trim()) {
            Toast.makeText(this, getString(R.string.err_senha_nao_confere), Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (txtPassword.text.toString().length < 8) {
            Toast.makeText(this, getString(R.string.err_senha_mtocurta), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun createAccount() {
        if (!isValid()) return

        firebaseAuth.createUserWithEmailAndPassword(
            txtEmail.text.toString().trim(),
            txtPassword.text.toString()
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendEmailVerification()
                finish()
                Toast.makeText(
                    this,
                    getString(R.string.msg_create_account_sucess),
                    Toast.LENGTH_SHORT
                ).show();
                finish()
            } else {
                val exception = task.exception;
                if (exception is FirebaseAuthException && exception.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                    Toast.makeText(
                        this,
                        getString(R.string.err_email_ja_cadastrado),
                        Toast.LENGTH_SHORT
                    ).show();
                } else if (exception is FirebaseAuthException && exception.errorCode == "ERROR_WEAK_PASSWORD") {
                    Toast.makeText(this, getString(R.string.err_senha_fraca), Toast.LENGTH_SHORT)
                        .show();
                } else {
                    Toast.makeText(this, getString(R.string.err_generico), Toast.LENGTH_SHORT)
                        .show();
                }
            }

        }


    }
    private fun sendEmailVerification() {
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

        firebaseUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this, getString(R.string.err_envioemail), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    //endregion


}//classe
