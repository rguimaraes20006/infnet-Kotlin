package br.com.biexpert.bicm

import androidx.appcompat.app.AppCompatActivity


import android.app.*
import android.content.Context
import android.content.Intent

import android.os.Build

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import br.com.biexpert.bicm.databinding.ActivityTaskBinding
import br.com.biexpert.bicm.dto.TaskModel
import br.com.biexpert.bicm.fragments.SaveButton
import br.com.biexpert.bicm.fragments.TaskList


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*


class TaskActivity : AppCompatActivity(), SaveButton.OnSaveClickListener {


    private lateinit var binding: ActivityTaskBinding;
    private lateinit var fbDatabase: DatabaseReference
    private lateinit var fbUserID: String
    private lateinit var taskId: String


    //controles
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dateEditText = findViewById<EditText>(R.id.dateEditText)
        timeEditText = findViewById<EditText>(R.id.timeEditText)
        titleEditText = findViewById<EditText>(R.id.titleEditText)
        descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)


        val cal = Calendar.getInstance()


        //region Google Firebase Setup
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


        //verifica se chegou um id da tarefa
        taskId = intent.getStringExtra("id") ?: ""

        if (taskId.isNotEmpty()) {
            getTask(taskId)
        } else {

            dateEditText.setText(
                String.format(
                    "%02d/%02d/%04d",
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH + 1),
                    cal.get(Calendar.YEAR)
                )
            )

            timeEditText.setText(
                String.format(
                    "%02d:%02d",
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE)
                )
            )

        }

        findViewById<Button>(R.id.selectDateButton).setOnClickListener {
            val datePickerDialog =
                DatePickerDialog(
                    this, { _, yearOfYear, monthOfYear, dayOfMonth ->
                        dateEditText.setText(
                            String.format(
                                "%02d/%02d/%04d",
                                dayOfMonth,
                                monthOfYear + 1,
                                yearOfYear
                            )
                        )
                    },
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH + 1),
                    cal.get(Calendar.YEAR)
                )

            datePickerDialog.show()
        }

        findViewById<Button>(R.id.selectTimeButton).setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this, { _, hourOfDay, minuteOfHour ->
                    timeEditText.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour))
                }, cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            )
            timePickerDialog.show()
        }



        val saveButtonFragment = SaveButton()
        saveButtonFragment.setOnSaveClickListener(this)
        supportFragmentManager.beginTransaction()
            .replace(R.id.saveButtonFragment, saveButtonFragment)
            .commit()



    }


    private fun getTask(Id: String) {

        val taskRef = FirebaseDatabase.getInstance().getReference("/users/$fbUserID/tasks/$Id")

        taskRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return
                titleEditText.setText(snapshot.child("title").value.toString())
                descriptionEditText.setText(snapshot.child("description").value.toString())
                dateEditText.setText(snapshot.child("date").value.toString())
                timeEditText.setText(snapshot.child("time").value.toString())
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@TaskActivity,
                    getString(R.string.database_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })


    }

    override fun onSaveClick() {

        var taskToUpdate = TaskModel(
            taskId,
            titleEditText.text.toString(),
            descriptionEditText.text.toString(),
            dateEditText.text.toString(),
            timeEditText.text.toString()
        )
        createOrUpdateTask(taskToUpdate)
        finishAffinity()
    }


    private fun createOrUpdateTask(task: TaskModel) {

        if (task.taskId.isNullOrEmpty()) //criar
        {
            val objRet = fbDatabase.push()
            objRet.setValue(task)


        } else  //atualizar
        {
            val taskToUpdate =
                FirebaseDatabase.getInstance().getReference("/users/$fbUserID/tasks/${task.taskId}")

            taskToUpdate.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) return
                    val bdtask = snapshot.value as HashMap<String, String>

                    bdtask["title"] = task.title
                    bdtask["description"] = task.description
                    bdtask["date"] = task.date
                    bdtask["time"] = task.time

                    taskToUpdate.setValue(task)
                    Toast.makeText(
                        this@TaskActivity,
                        getString(R.string.task_updated_ok),
                        Toast.LENGTH_SHORT
                    ).show()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@TaskActivity,
                        getString(R.string.database_error),
                        Toast.LENGTH_LONG
                    ).show()

                }

            });

        }

        finishAffinity()


    }


}