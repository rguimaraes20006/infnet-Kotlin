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


class MainActivity : AppCompatActivity() {

    private lateinit var fbUserID: String
    private lateinit var itemList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var fbDatabase: DatabaseReference


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

        //region listView
        itemList = ArrayList<String>()

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            itemList
        );

        val listViewTasks = findViewById<ListView>(R.id.listViewTasks);
        listViewTasks.adapter = adapter;

        //endregion

        //region realtime database
        fbDatabase.addValueEventListener(object : ValueEventListener {

            val ctx = this@MainActivity
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()

                for (item in snapshot.children) {
                    itemList.add(item.child("title").value.toString())
                }
                adapter.notifyDataSetChanged()

                listViewTasks.setOnItemClickListener { parent, view, position, id ->
                    val activity = Intent(ctx, TaskActivity::class.java)
                    activity.putExtra("id", snapshot.children.toList()[position].key)
                    startActivity(activity)
                }


                listViewTasks.setOnItemLongClickListener { parent, view, position, id ->
                    val itemId = snapshot.children.toList()[position].key

                    if (itemId != null) {
                        AlertDialog.Builder(ctx)
                            .setTitle(getString(R.string.confirm_action))
                            .setMessage(getString(R.string.task_delete_confirm_message))
                            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                                fbDatabase.child(itemId).removeValue()
                                Toast.makeText(ctx, getString(R.string.Sucess), Toast.LENGTH_SHORT)
                                    .show()
                            }
                            .setNegativeButton(getString(R.string.not)) { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    }

                    true
                }


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


}