package br.com.biexpert.bicm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        //region setup
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        //endregion

        //region btn sair
        findViewById<View>(R.id.logout).setOnClickListener{
            val activity = Intent(this, LoginScreenActivity::class.java);
            startActivity(activity);
        }
        //endregion

        //region listView
        var listViewTasks = findViewById<android.widget.ListView>(R.id.listViewTasks);
        var createTask = findViewById<android.widget.EditText>(R.id.createTask);

        var itemList = arrayListOf<String>();
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, itemList);

        findViewById<View>(R.id.add).setOnClickListener {
            itemList.add(createTask.text.toString());
            listViewTasks.adapter = adapter;
            //faz o refresh da tela
            adapter.notifyDataSetChanged()

            createTask.text.clear()
        }

        //endregion



    }
}