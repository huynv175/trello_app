package gst.trainingcourse.trelloapp.viewmodel.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import gst.trainingcourse.trelloapp.model.User
import gst.trainingcourse.trelloapp.request.UserRequest
import gst.trainingcourse.trelloapp.utils.ResultMode
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ValidateViewModel : ViewModel() {

    private lateinit var user: User

    private val _user = MutableLiveData<User>()
    val userId: LiveData<User>
    get() = _user

//    private val _userInfo = MutableLiveData<User>()
//    val userInfo: LiveData<User>
//        get() = _userInfo

    private var _isValidInput = MutableLiveData<ResultMode>()
    val isValidInput: LiveData<ResultMode>
        get() = _isValidInput

    private var _resultLogin = MutableLiveData<ResultMode>()
    val resultLogin: LiveData<ResultMode>
        get() = _resultLogin

    private var _resultSignUp = MutableLiveData<ResultMode>()
    val resultSignUp: LiveData<ResultMode>
        get() = _resultSignUp

    fun checkInput(user: User) {
        this.user = user
        viewModelScope.launch(IO) {
            if (Patterns.EMAIL_ADDRESS.matcher(user.email).matches()
                && user.password.length >= 6
            ) {
                _isValidInput.postValue(ResultMode.VALID)
                checkSignUp()
            } else {
                _isValidInput.postValue(ResultMode.INVALID)
            }
        }
    }

    fun checkSignUp() {
        viewModelScope.launch(IO) {
            UserRequest.checkUserRegister(user) { isExisted ->
                if (isExisted) {
                    _resultSignUp.postValue(ResultMode.ERROR)
                } else {
                    UserRequest.register(user) { user ->
                        if (user != null) {
                            _user.postValue(user)
                            _resultSignUp.postValue(ResultMode.SUCCESS)
                        }
                    }
                }
            }
        }
    }

    fun checkLogin() {
        viewModelScope.launch(IO) {
            UserRequest.login(user.email, user.password) { user ->
                if (user != null) {
                    _user.postValue(user)
                    _resultLogin.postValue(ResultMode.SUCCESS)
                } else {
                    _resultLogin.postValue(ResultMode.ERROR)
                }
            }
        }
    }

}