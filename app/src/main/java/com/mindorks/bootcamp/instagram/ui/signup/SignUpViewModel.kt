package com.mindorks.bootcamp.instagram.ui.signup

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class SignUpViewModel(
    val userRepository: UserRepository,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val usernameLiveData = MutableLiveData<String>()
    val passwordLiveData = MutableLiveData<String>()
    val emailLiveData = MutableLiveData<String>()
    val signUpProgressStatusData = MutableLiveData<Boolean>();
    val signStatusLiveData = MutableLiveData<Boolean>();

    override fun onCreate() {

    }

    fun onSignUp() {
        val email = emailLiveData.value;
        val password = passwordLiveData.value;
        val name = usernameLiveData.value
        if (networkHelper.isNetworkConnected() && email != null && password != null && name != null) {
            signUpProgressStatusData.postValue(true)
            compositeDisposable.add(
                userRepository.doSignUpUser(email, password, name)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe({
                        userRepository.saveCurrentUser(it)
                        signUpProgressStatusData.postValue(false)
                        signStatusLiveData.postValue(true)

                    }, {
                        handleNetworkError(it)
                        signStatusLiveData.postValue(false)
                        signUpProgressStatusData.postValue(false)
                    })
            )
        }


    }

    fun onUserNameChange(s: String) {
        usernameLiveData.value = s
    }

    fun onPasswordChange(s: String) {
        passwordLiveData.value = s
    }

    fun onEmailIdChange(s: String) {
        emailLiveData.value = s
    }

}