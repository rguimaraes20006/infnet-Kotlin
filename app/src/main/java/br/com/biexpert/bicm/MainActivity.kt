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
        findViewById<View>(R.id.logout).setOnClickListener {
            val activity = Intent(this, LoginScreenActivity::class.java);
            startActivity(activity);
        }
        //endregion

        //region listView
        val listViewTasks = findViewById<android.widget.ListView>(R.id.listViewTasks);
        val createTask = findViewById<android.widget.EditText>(R.id.createTask);

        val itemList = arrayListOf<String>();
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, itemList);


        //region btnAdd
        findViewById<View>(R.id.add).setOnClickListener {
            itemList.add(createTask.text.toString());
            listViewTasks.adapter = adapter;
            //faz o refresh da tela
            adapter.notifyDataSetChanged()
            createTask.text.clear()

        }
        //endregion

        //region btnClear
        findViewById<View>(R.id.clear).setOnClickListener {
            itemList.clear()
            adapter.notifyDataSetChanged()

        }
        //endregion


        //region btnDeletar
//        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        findViewById<View>(R.id.delete).setOnClickListener{

            val selectedItems = listViewTasks.checkedItemPositions
            for (i in selectedItems.size() - 1 downTo 0) {
                if (selectedItems.valueAt(i)) {
                    val selectedItem = adapter.getItem(selectedItems.keyAt(i)) as String
                    adapter.remove(selectedItem)
                }
            }
            listViewTasks.clearChoices()


        }
        //endregion




        //endregion


    }
}