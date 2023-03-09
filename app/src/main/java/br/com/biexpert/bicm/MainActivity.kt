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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var gson = Gson()


    private fun saveData(array: ArrayList<String>) {
        var strJson = gson.toJson(array)
        var editor = sharedPreferences.edit()
        editor.putString("Lista", strJson)
    }

    private fun getData() : ArrayList<String>{
        var arrayJson =sharedPreferences.getString("Lista", null);

        return if(arrayJson.isNullOrEmpty()) {
            arrayListOf()
        }
        else {
            gson.fromJson(arrayJson, object :   TypeToken<ArrayList<String>>(){})
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //region setup
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        sharedPreferences = getSharedPreferences("TodoList", Context.MODE_PRIVATE)



        //se n tiver logado redireciona pro login
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();
        val firebaseUser: FirebaseUser? = firebaseAuth.currentUser
        if(firebaseUser == null){
            val intent = Intent(this, LoginScreenActivity::class.java);
            startActivity(intent);
        }
        //endregion

        //region btn sair
        findViewById<View>(R.id.logout).setOnClickListener {
            val activity = Intent(this, LoginScreenActivity::class.java);
            FirebaseAuth.getInstance().signOut()
            startActivity(activity);
        }
        //endregion

        //region listView
        val listViewTasks = findViewById<ListView>(R.id.listViewTasks);
        val createTask = findViewById<EditText>(R.id.createTask);

        val itemList =  getData()//arrayListOf<String>();
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, itemList);


        //region btnAdd
        findViewById<View>(R.id.add).setOnClickListener {
            itemList.add(createTask.text.toString());
            listViewTasks.adapter = adapter;
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
}