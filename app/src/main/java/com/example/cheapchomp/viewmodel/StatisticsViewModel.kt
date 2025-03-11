package com.example.cheapchomp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cheapchomp.repository.DatabaseRepository
import com.google.firebase.auth.FirebaseAuth

//Statistics View Model
class StatisticsViewModel(
    private val databaseRepository: DatabaseRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    //Retrieve expenses from the database for each user
    fun getExpenses(onExpensesFetched: (List<Float>) -> Unit) {
        databaseRepository.getExpenses { expenses ->
            onExpensesFetched(expenses)
        }

    }
    //Retrieve current month from the database
    fun getCurrentMonth(): Int {
        return databaseRepository.getCurrentMonth()
    }

    fun getMonthLabel(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> "Invalid Month"
        }
    }
    //Sign out function with Firebase
    fun signOut() {
        auth.signOut()
    }


}
