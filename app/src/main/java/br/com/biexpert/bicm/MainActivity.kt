package br.com.biexpert.bicm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.content.Context
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.FirebaseDatabase



class MainActivity : AppCompatActivity() {

    private lateinit var fbUserID: String
    private lateinit var itemList: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {

        //region setup
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        //sharedPreferences = getSharedPreferences("TodoList", Context.MODE_PRIVATE)

        //se n tiver logado redireciona pro login
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if (firebaseUser == null) {
            val intent = Intent(this, LoginScreenActivity::class.java);
            startActivity(intent);
        } else {
            //val userId = FirebaseAuth.getInstance().currentUser?.uid
            fbUserID = firebaseUser.uid
        }
        //endregion

        //region btn sair
        findViewById<View>(R.id.logout).setOnClickListener {
            val activity = Intent(this, LoginScreenActivity::class.java);
            FirebaseAuth.getInstance().signOut()
            startActivity(activity);
        }
        //endregion


        //region btnTranslate
        findViewById<View>(R.id.btnTranslate).setOnClickListener {

            val tradutor = AzureTranslator()
            val createTask = findViewById<EditText>(R.id.createTask)

            Thread {
                val textoTraduzido = tradutor.translate(createTask.text.toString()).translations.first().text
                runOnUiThread {
                    createTask.setText(textoTraduzido.replace( "+",  " " ))
                }

            }.start()


        }
        //endregion

        //region listView
        itemList = arrayListOf();

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            itemList
        );

        val listViewTasks = findViewById<ListView>(R.id.listViewTasks);
        listViewTasks.adapter = adapter;

        getDataFromFirebase();


        //region btnAdd

        val createTask = findViewById<EditText>(R.id.createTask);
        findViewById<View>(R.id.add).setOnClickListener {
            itemList.add(createTask.text.toString());
            //faz o refresh da tela
            adapter.notifyDataSetChanged()
            createTask.text.clear()
            saveData(itemList)


        }
        //endregion

        //region btnClear
        findViewById<View>(R.id.clear).setOnClickListener {
            itemList.clear()
            adapter.notifyDataSetChanged()
            saveData(itemList)


        }
        //endregion

        //region btnDeletar
        findViewById<View>(R.id.delete).setOnClickListener {

            val selectedItems = listViewTasks.checkedItemPositions
            for (i in selectedItems.size() - 1 downTo 0) {
                if (selectedItems.valueAt(i)) {
                    val selectedItem = adapter.getItem(selectedItems.keyAt(i)) as String
                    adapter.remove(selectedItem)
                }
            }
            listViewTasks.clearChoices()
            adapter.notifyDataSetChanged()
            saveData(itemList)


        }
        //endregion

        //endregion


    }
    private fun getDataFromFirebase() {

        val db = FirebaseDatabase.getInstance().getReference("tasks")
        db.child(fbUserID).get().addOnSuccessListener {

            if (it.exists()) {

                itemList = it.value as ArrayList<String>
                adapter.notifyDataSetChanged()

/* troquei o localstorage pelo o firebase, favor considerar o firebase
                itemList = gson.fromJson(
                    it.value.toString(),
                    object : TypeToken<ArrayList<String>>() {})
*/


            } else {
                println("Não existe")
            }
        }

    }
    private fun saveData(array: ArrayList<String>) {

        val db = FirebaseDatabase.getInstance().getReference("tasks")
        //  val task = TaskModel(fbUserID, array)

        db.child(fbUserID).setValue(array).addOnSuccessListener {
            Toast.makeText(this, "Salvo com sucesso", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Falhou", Toast.LENGTH_SHORT).show()
            println(it.message)
        }

/*
        val arrayJson = sharedPreferences.getString("tasks", null);
        return if(arrayJson.isNullOrEmpty()){
            arrayListOf();
        }else{
            gson.fromJson(arrayJson, object: TypeToken<ArrayList<String>>(){}.type)
        }*/

    }

}