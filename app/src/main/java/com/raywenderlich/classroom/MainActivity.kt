package com.raywenderlich.classroom

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.classroom.databinding.MainActivityBinding
import com.raywenderlich.model.Classroom
import com.raywenderlich.model.DialogBuilder
import com.raywenderlich.student.StudentActivity
import com.raywenderlich.student.StudentFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var startActivity_ForResult: ActivityResultLauncher<Intent>
    private lateinit var clickAnimListener: ((view: View) -> Unit)
    private lateinit var mainFragment: MainFragment

    companion object {
        const val PREFERENCE_NAME = "PREFERENCE_NAME"
        const val CLASSROOM_INTENT_KEY = "CLASSROOM_INTENT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(sharedPreferences)
        ).get(MainViewModel::class.java)

        binding = MainActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        clickAnimListener = { view: View ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
        }

        if (savedInstanceState == null) {
            mainFragment = MainFragment.newInstance()
            val fragmentMainContainer =
                if (binding.mainFragmentContainer == null) {
                    R.id.main_activity_container
                } else {
                    R.id.main_fragment_container
                }

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(fragmentMainContainer, mainFragment, MainFragment.MAIN_FRAGMENT_TAG)
            }
        } else {
            mainFragment = if (binding.mainFragmentContainer == null)
                supportFragmentManager.findFragmentById(R.id.main_activity_container) as MainFragment
            else
                supportFragmentManager.findFragmentById(R.id.main_fragment_container) as MainFragment
        }
        mainFragment.onClassroomClick = { classRoom ->
            if (binding.mainFragmentContainer == null) {
                //openStudentActivity(classRoom)
                openStudentActivity_WithResult(classRoom)
            } else {
                val bundle = bundleOf(CLASSROOM_INTENT_KEY to classRoom)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace(
                        R.id.student_fragment_container,
                        StudentFragment::class.java, bundle, null
                    )
                }
                setUpView_WorkWithStudentForm(classRoom)
            }

        }

        val studentFragment =
            supportFragmentManager.findFragmentById(R.id.student_fragment_container)
        setUpView_WorkWithStudentForm(
            if (studentFragment == null)
                null
            else
                viewModel.classroom
        )

        startActivity_ForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    // Handle the Intent
                    intent?.let {
                        viewModel.refresh_Classroom()
                    }
                }
            }
    }

    private fun setUpView_WorkWithStudentForm(classRoom: Classroom?) {
        if (classRoom == null) {
            title = resources.getString(R.string.app_name)
            binding.floatingActionButtonAdd.setOnClickListener {
                showDialog_CreateClassroom()
            }

            binding.floatingActionButtonClearAll.setOnClickListener { view ->
                clickAnimListener(view)
                viewModel.clearClassroomList()
            }

        } else {
            title = classRoom.name ?: resources.getString(R.string.app_name)
            binding.floatingActionButtonAdd.setOnClickListener {
                showDialog_AddStudent()
            }
            binding.floatingActionButtonClearAll.setOnClickListener { view ->
                clickAnimListener(view)
                viewModel.clearStudentList()
            }
        }
    }

    private fun showDialog_CreateClassroom() {
        DialogBuilder.showDialog(activity = this,
            title = getString(R.string.dialog_create_title_class),
            positiveButtonTitle = getString(R.string.dialog_create_create_classroom),
            onPositiveButtonClick = { dialog, editText ->
                viewModel.createClassroom(editText.text.toString())
                dialog.dismiss()
            })
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

    private fun openStudentActivity(classRoom: Classroom) {
        val intent = Intent(this, StudentActivity::class.java).apply {
            putExtra(MainActivity.CLASSROOM_INTENT_KEY, classRoom)
        }
        startActivity(intent)
    }

    private fun openStudentActivity_WithResult(classRoom: Classroom) {
        val intent = Intent(this, StudentActivity::class.java)
        intent.putExtra(MainActivity.CLASSROOM_INTENT_KEY, classRoom)
        startActivity_ForResult.launch(intent)
    }

    override fun onBackPressed() {
        val studentFragment =
            supportFragmentManager.findFragmentById(R.id.student_fragment_container)
        if (studentFragment == null) {
            super.onBackPressed()
        } else {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                remove(studentFragment)
            }
            setUpView_WorkWithStudentForm(null)
        }
    }

}