package com.project.archaeosite.view.profile

import android.annotation.SuppressLint
import android.os.Bundle
import com.project.archaeosite.R
import com.project.archaeosite.models.HillfortModel
import com.project.archaeosite.view.base.BaseView
import kotlinx.android.synthetic.main.activity_user_profile_view.*

class UserProfileView : BaseView() {

    lateinit var presenter: UserProfilePresenter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_view)

        presenter = initPresenter (UserProfilePresenter(this)) as UserProfilePresenter

        super.init(mytoolbar, true)

//            //textView_password.text=user.updatePassw

        textView_useremail.text = "Email: ${presenter.doGetUserMail()}"
        textView_indisites.text="Individual Sites: ${presenter.doComputeIndiSite()}"
        textView_indisites_visited.text="Individual Sites: ${presenter.doComputeIndiVisitedSite()}"

        presenter.doLoadHillfortSite()
    }

    @SuppressLint("SetTextI18n")
    override fun showHillfortList(hillfortList: List<HillfortModel>) {
        textView_hillfortsites.text="Hillfort Sites: ${presenter.doComputeHillfortSite(hillfortList)}"
        textView_totalsites.text="Total number of Sites: ${presenter.doComputeIndiSite()+presenter.doComputeHillfortSite(hillfortList)}"

        textView_hillfortsites_visited.text ="Hillfort Sites: ${presenter.doComputevisitedHillfort(hillfortList)}"
        textView_numvisited.text="Total Number visited of Sites: ${presenter.doComputeIndiVisitedSite()+presenter.doComputevisitedHillfort(hillfortList)}"
    }


}
