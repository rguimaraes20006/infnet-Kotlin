package br.com.biexpert.bicm.fragments




import android.util.Patterns
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.biexpert.bicm.R


class SaveButton : Fragment() {


    interface OnSaveClickListener {
        fun onSaveClick()
    }

    private var onSaveClickListener: OnSaveClickListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_save_button, container, false)
        val saveButton = view.findViewById<Button>(R.id.save_button)
        saveButton.setOnClickListener {
            onSaveClickListener?.onSaveClick()
        }
        return view
    }

    fun setOnSaveClickListener(listener: OnSaveClickListener) {
        onSaveClickListener = listener
    }


}
