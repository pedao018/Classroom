package com.raywenderlich.classroom

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.raywenderlich.model.Classroom
import com.raywenderlich.model.Student

class MainViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    lateinit var onClassroomAdded: (() -> Unit)
    lateinit var onStudentAdded: (() -> Unit)
    lateinit var onClassroomListRefresh: (() -> Unit)
    lateinit var classroom: Classroom

    val classRoomList: MutableList<Classroom> by lazy {
        retrieveClassroomList()
    }

    private fun retrieveClassroomList(): MutableList<Classroom> {
        val sharedPreferencesContent = sharedPreferences.all
        val classroomList = ArrayList<Classroom>()
        var classroom: Classroom
        val gson = Gson()
        for (item in sharedPreferencesContent) {
            //Json to Object
            classroom = gson.fromJson("${item.value}", Classroom::class.java)
            classroomList.add(classroom)
        }

        //Đưa classroom có số 5 lên đầu
        classroomList.sortBy {
            when (it.id) {
                5 -> 0
                else -> it.id
            }
        }
        return classroomList
    }

    fun createClassroom(className: String) {
        val classroom = Classroom(id = classRoomList.size + 1, name = className)
        classRoomList.add(classroom)
        saveData(classroom)
        onClassroomAdded.invoke()
    }

    fun addStudent(studentName: String) {
        val student = Student(id = classroom.studentList.size + 1, name = studentName)
        classroom.studentList.add(student)
        saveData(classroom)
        onStudentAdded.invoke()
    }

    private fun saveData(classroom: Classroom) {
        val gson = Gson()
        //Object to Json
        val jsonString = gson.toJson(classroom)
        sharedPreferences.edit().putString(
            "${classroom.id}",
            jsonString
        ).apply()
    }

    fun refresh_Classroom() {
        this.classRoomList.clear()
        classRoomList.addAll(retrieveClassroomList())
        onClassroomListRefresh.invoke()
    }

    fun clearClassroomList() {
        this.classRoomList.clear()
        sharedPreferences.edit().clear().commit()
        onClassroomListRefresh.invoke()
    }

    fun clearStudentList() {
        this.classroom.studentList.clear()
        saveData(classroom)
        onStudentAdded.invoke()
    }

}