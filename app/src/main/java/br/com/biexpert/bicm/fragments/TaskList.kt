package br.com.biexpert.bicm.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import br.com.biexpert.bicm.MainActivity
import br.com.biexpert.bicm.R
import br.com.biexpert.bicm.TaskActivity


class TaskList : Fragment() {


    private var itemList: ArrayList<String>? = null
    private var keyList: ArrayList<String>? = null
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var listViewTasks: ListView
    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemList = it.getStringArrayList(ARG_ARRAY_LIST) as ArrayList<String>
            keyList = it.getStringArrayList(ARG_KEY_LIST) as ArrayList<String>
        }
    }

    fun UpdateData(_itemList: ArrayList<String>, _keyList: ArrayList<String>) {

        itemList = _itemList
        keyList = _keyList

        adapter = ArrayAdapter(
            ctx,
            android.R.layout.simple_list_item_multiple_choice,
            itemList!!
        );

        listViewTasks.adapter = adapter;
        adapter.notifyDataSetChanged()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)



        if (itemList === null) {
            println("itemlist vazio! sem dados")
            return view
        }

        listViewTasks = view.findViewById(R.id.listViewTasks) as ListView;


         ctx = this.context!!
        listViewTasks.setOnItemClickListener { parent, view, position, id ->
            val activity = Intent(ctx, TaskActivity::class.java)

            activity.putExtra("id", keyList?.get(position))
            startActivity(activity)
        }

        listViewTasks.setOnItemLongClickListener { parent, view, position, id ->
            val itemId = keyList?.get(position)

            if (itemId != null) {
                AlertDialog.Builder(ctx!!)
                    .setTitle(getString(R.string.confirm_action))
                    .setMessage(getString(R.string.task_delete_confirm_message))
                    .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                        (activity as MainActivity?)!!.RemoveTask(itemId)
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





        return view

    }


    companion object {
        private const val ARG_ARRAY_LIST = "arg_array_list"
        private const val ARG_KEY_LIST = "arg_key_list"


        @JvmStatic
        fun newInstance(keyList: ArrayList<String>, arrayList: ArrayList<String>): TaskList {
            return TaskList().apply {
                arguments = Bundle().apply {
                    putStringArrayList(ARG_ARRAY_LIST, arrayList)
                    putStringArrayList(ARG_KEY_LIST, keyList)
                }
            }
        }
    }


}