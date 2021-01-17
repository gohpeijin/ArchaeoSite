package com.project.archaeosite.view.profile

import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView

class UserProfilePresenter (view: BaseView) : BasePresenter(view){
    fun doComputeSite ():Int{
       return app.sites.findAll().size
    }
}