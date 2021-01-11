package com.project.archaeosite.view.login

import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.VIEW

class LoginPresenter(view: BaseView) : BasePresenter(view) {
    fun doLogin(email: String, password: String) {
        view?.navigateTo(VIEW.LIST)
    }

    fun doSignUp(email: String, password: String) {
        view?.navigateTo(VIEW.LIST)
    }
}