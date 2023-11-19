package com.example.debtmanagerapp.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DebtViewModel(application: Application) : AndroidViewModel(application) {
    private val database = DatabaseClient.getDatabase(application)

    fun insertDebt(debt: DebtEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            database.debtDao().insert(debt)
        }
    }

    fun getAllDebtsLiveData(): LiveData<List<DebtEntity>> {
        return database.debtDao().getAllDebtsLiveData() // LiveData を返す関数を呼び出す
    }

    fun updateDebt(debt: DebtEntity){
        viewModelScope.launch(Dispatchers.IO){
            database.debtDao().update(debt)
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            database.debtDao().delete(debt)
        }
    }
}