package com.example.healthkiosk

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class UserViewModel: ViewModel() {
    var name by mutableStateOf("")
    var id by mutableStateOf("")
    var age by mutableStateOf("")
    var sex by mutableStateOf("")
    var height by mutableStateOf("")
    var weight by mutableStateOf("")
    var pwd by mutableStateOf("")

    fun updateName(newName: String) { name = newName }
    fun updateId(newId: String) { id = newId }
    fun updateAge(newAge: String) { age = newAge }
    fun updateSex(newSex: String) { sex = newSex }
    fun updateHeight(newHeight: String) { height = newHeight }
    fun updateWeight(newWeight: String) { weight = newWeight }
    fun updatePwd(newPwd: String) { pwd = newPwd }


}