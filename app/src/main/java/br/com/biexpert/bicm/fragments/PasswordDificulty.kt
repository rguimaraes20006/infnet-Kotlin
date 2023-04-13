package br.com.biexpert.bicm.fragments

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

class PasswordDificulty : Fragment() {


    private lateinit var txtPasswordDificulty: TextView
    private lateinit var etPassword: EditText

    lateinit var text: Editable

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
        val view = inflater.inflate(R.layout.fragment_password_dificulty, container, false)

        txtPasswordDificulty = view.findViewById(R.id.txtPasswordDificulty) as TextView
        etPassword = view.findViewById(R.id.etPassword) as EditText

        etPassword.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}


            override fun afterTextChanged(p0: Editable?) {
                if (p0 != null) {
                    text = p0
                }

                txtPasswordDificulty.text = getPasswordDifficulty(p0.toString())


            }


        })


        return view

    }


    fun getPasswordDifficulty(password: String): String {

        var score = 0

        if (password.length >= 8) score += 1
        if (password.length >= 12) score += 1
        if (password.length >= 16) score += 1

        if (password.matches("(?=.*[A-Z])(?=.*[a-z]).*".toRegex())) score += 1

        if (password.matches("(?=.*[0-9]).*".toRegex())) score += 1
        if (password.matches("(?=.*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).*".toRegex())) score += 1

        val strRet = when (score) {
            0 -> getString(R.string.sem_senha)
            1 -> getString(R.string.veryweak)
            2 -> getString(R.string.weak)
            3 -> getString(R.string.weak)
            4 -> getString(R.string.moderate)
            5 -> getString(R.string.strong)
            6 -> getString(R.string.verystrong)
            else -> getString(R.string.ultramothefuckastrong)
        }

        return strRet
    }


}