package com.project.archaeosite.view.login

import com.google.firebase.auth.FirebaseAuth
import com.project.archaeosite.models.ArchaeoUser
import com.project.archaeosite.models.firebase.SiteFireStore
import com.project.archaeosite.view.base.BasePresenter
import com.project.archaeosite.view.base.BaseView
import com.project.archaeosite.view.base.VIEW
import org.jetbrains.anko.toast

class LoginPresenter(view: BaseView) : BasePresenter(view) {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var fireStore: SiteFireStore? = null
    var user= ArchaeoUser()

    init {
        if (app.sites is SiteFireStore) {
            fireStore = app.sites as SiteFireStore
        }
    }

    fun doLogin(email: String, password: String) {
        view?.showProgress()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(view!!) { task ->
            if (task.isSuccessful) {
                if (fireStore != null) {
                    fireStore!!.fetchSites {
                        view?.hideProgress()
                        view?.navigateTo(VIEW.LIST)
                    }
                }
                else {
                    view?.hideProgress()
                    view?.navigateTo(VIEW.LIST)
                }
            }
            else {
                view?.hideProgress()
                view?.toast("Login Failed: ${task.exception?.message}")
            }
        }
    }

    fun doSignUp(email: String, password: String) {
        view?.showProgress()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(view!!) { task ->
            if (task.isSuccessful) {
                fireStore!!.fetchSites {
                    view?.hideProgress()
                    view?.navigateTo(VIEW.LIST)
                    addUserdetail(email, password)
                }
            } else {
                view?.hideProgress()
                view?.toast("Sign Up Failed: ${task.exception?.message}")
            }
        }
    }

    fun addUserdetail(email: String, password: String){
        user.email=email
        user.password=password
        fireStore!!.createUser(user)
    }
}