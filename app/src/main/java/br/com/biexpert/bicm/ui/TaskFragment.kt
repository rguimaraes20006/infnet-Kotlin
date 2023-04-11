package br.com.biexpert.bicm.ui

import android.os.Bundle

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import br.com.biexpert.bicm.R
import java.text.SimpleDateFormat
import java.util.*


class TaskFragment : Fragment() {

    private var Title: String? = null
    private var Date: String? = null
    private var Time: String? = null
    private var Description: String? = null

    private lateinit var titleEditText: EditText
    private lateinit var dateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            Title = it.getString(TITLE_KEY)
            Date = it.getString(DATE_KEY)
            Time = it.getString(TIME_KEY)
            Description = it.getString(DESCRIPTION_KEY)
        }

        titleEditText.setText(Title)
        dateEditText.setText(Date)
        timeEditText.setText(Time)
        descriptionEditText.setText(Description)

        saveButton.setOnClickListener {
            Title = titleEditText.text.toString()
            Date = dateEditText.text.toString()
            Time = timeEditText.text.toString()
            Description = descriptionEditText.text.toString()

            // Save task
        }

        val calendar = Calendar.getInstance()
        val date = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault()).format(calendar.time)
        dateEditText.setText(date)

        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        timeEditText.setText(time)
    }

    companion object {


        const val TITLE_KEY = "title"
        const val DATE_KEY = "date"
        const val TIME_KEY = "time"
        const val DESCRIPTION_KEY = "description"

        fun newInstance(
            taskTitle: String?,
            taskDate: String?,
            taskTime: String?,
            taskDescription: String?
        ): TaskFragment {
            val fragment = TaskFragment()
            val args = Bundle()
            args.putString(TITLE_KEY, taskTitle)
            args.putString(DATE_KEY, taskDate)
            args.putString(TIME_KEY, taskTime)
            args.putString(DESCRIPTION_KEY, taskDescription)
            fragment.arguments = args
            return fragment
        }


    }
}