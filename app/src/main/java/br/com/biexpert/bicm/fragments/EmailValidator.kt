package br.com.biexpert.bicm.fragments


import android.util.Patterns
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.biexpert.bicm.R


class EmailValidator : Fragment() {

    lateinit var text: Editable
    var isValid: Boolean = false

    private lateinit var textEmailAddress: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_emailvalidator, container, false)

        textEmailAddress = view.findViewById(R.id.email_input) as EditText



        textEmailAddress.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}


            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    text = p0
                }

                isValid = Patterns.EMAIL_ADDRESS.matcher(p0).matches()
            }
        })

        return view
    }


}
