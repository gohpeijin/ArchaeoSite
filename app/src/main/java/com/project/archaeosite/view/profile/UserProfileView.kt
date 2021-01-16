package com.project.archaeosite.view.profile

import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.R
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_user_profile_view.*


class UserProfileView : BaseView() {

    lateinit var presenter: UserProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_view)

        presenter = initPresenter (UserProfilePresenter(this)) as UserProfilePresenter

        super.init(mytoolbar, true)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            textView_useremail.text = "Email: " + user.email
            //textView_password.text=user.updatePassw
        }
    }
}