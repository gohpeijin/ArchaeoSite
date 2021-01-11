package com.project.archaeosite.view.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.archaeosite.R
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_login_view.*
import org.jetbrains.anko.toast

class LoginView : BaseView() {

    lateinit var presenter: LoginPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_view)

        presenter = initPresenter(LoginPresenter(this)) as LoginPresenter

        signUp.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            if (email == "" || password == "") {
                toast("Please provide email + password")
            }
            else {
                presenter.doSignUp(email,password)
            }
        }

        logIn.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()
            if (email == "" || password == "") {
                toast("Please provide email + password")
            }
            else {
                presenter.doLogin(email,password)
            }
        }


    }
}