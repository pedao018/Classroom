package com.raywenderlich.student

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.classroom.MainActivity
import com.raywenderlich.classroom.MainViewModel
import com.raywenderlich.classroom.MainViewModelFactory
import com.raywenderlich.classroom.R
import com.raywenderlich.classroom.databinding.StudentActivityBinding
import com.raywenderlich.model.Classroom
import com.raywenderlich.model.DialogBuilder

class StudentActivity : AppCompatActivity() {
    private lateinit var binding: StudentActivityBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences =
            getSharedPreferences(MainActivity.PREFERENCE_NAME, Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(sharedPreferences)
        ).get(MainViewModel::class.java)

        binding = StudentActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.student_activity_container, StudentFragment.newInstance())
                .commitNow()
        }

        //Receive From Another Activity
        val classRoom = intent.getParcelableExtra<Classroom>(MainActivity.CLASSROOM_INTENT_KEY)!!
        title = classRoom.name
        viewModel.classroom = classRoom

        binding.floatingActionButtonAdd.setOnClickListener {
            showDialog_AddStudent()
        }

        val clickAnimListener = { view: View ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)

        }
        binding.floatingActionButtonClearAll.setOnClickListener {
            clickAnimListener(it)
            viewModel.clearStudentList()
        }
    }

    private fun showDialog_AddStudent() {
        DialogBuilder.showDialog(activity = this,
            title = getString(R.string.dialog_create_title_student),
            positiveButtonTitle = getString(R.string.dialog_create_add_student),
            onPositiveButtonClick = { dialog, editText ->
                viewModel.addStudent(editText.text.toString())
                dialog.dismiss()
            })
    }

    override fun onBackPressed() {
        val bundle = Bundle()
        bundle.putParcelable(MainActivity.CLASSROOM_INTENT_KEY, viewModel.classroom)
        val intent = Intent()
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }
}