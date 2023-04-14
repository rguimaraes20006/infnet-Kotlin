package br.com.biexpert.bicm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter

import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import br.com.biexpert.bicm.dto.TaskModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.appcompat.app.AlertDialog
import br.com.biexpert.bicm.fragments.TaskList


class MainActivity : AppCompatActivity() {

    private lateinit var fbUserID: String

    private lateinit var fbDatabase: DatabaseReference

    private  lateinit var   fragment:  TaskList


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        //region Google Firebase Setup
        //se n estiver logado redireciona pro login
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if (firebaseUser == null) {
            val intent = Intent(this, LoginScreenActivity::class.java);
            startActivity(intent);
        } else {

            //seta o userID
            fbUserID = firebaseUser.uid

            //seta a referencia do banco
            fbDatabase = FirebaseDatabase.getInstance().getReference("/users/$fbUserID/tasks")

        }
        //endregion

        //region btn sair (faz logoff no firebase e volta para a tela de login)
        findViewById<View>(R.id.logout).setOnClickListener {
            val activity = Intent(this, LoginScreenActivity::class.java);
            FirebaseAuth.getInstance().signOut()
            startActivity(activity);
        }
        //endregion


        //inicia a lista vazia
        fragment = TaskList.newInstance( arrayListOf(), arrayListOf())
        supportFragmentManager.beginTransaction().replace(R.id.taskListFragment, fragment)
            .commit()




        //region btnTranslate (Traduz o texto da tarefa para ingles)
        /*
        findViewById<View>(R.id.btnTranslate).setOnClickListener {

            val tradutor = AzureTranslator()
            val createTask = findViewById<EditText>(R.id.createTask)

            Thread {
                val textoTraduzido =
                    tradutor.translate(createTask.text.toString()).translations.first().text
                runOnUiThread {
                    createTask.setText(textoTraduzido.replace("+", " "))
                }

            }.start()


        }
        */
        //endregion

        //region realtime database
        fbDatabase.addValueEventListener(object : ValueEventListener {

            val ctx = this@MainActivity


            override fun onDataChange(snapshot: DataSnapshot) {


                var itemList: ArrayList<String> = ArrayList()
                var keyList: ArrayList<String> = ArrayList()

                for (item in snapshot.children) {
                    itemList.add(item.child("title").value.toString())
                    keyList.add(item.key.toString())

                }

                fragment.UpdateData(itemList, keyList)

                println(itemList)

                //Refaz a bosta do fragmento
                //val fragment = TaskList.newInstance( keyList, itemList)
                //supportFragmentManager.beginTransaction().replace(R.id.taskListFragment, fragment)
                 //   .commit()


            }//ondatachange

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(ctx, getString(R.string.database_error), Toast.LENGTH_LONG).show()
            }
        })
        //endregion

        //region btnAdd

        findViewById<View>(R.id.add).setOnClickListener {
            val activity = Intent(this, TaskActivity::class.java);
            startActivity(activity)
            finish()
        }
        //endregion

    }
    fun RemoveTask(taskId : String) {
        fbDatabase.child(taskId).removeValue()
    }
}