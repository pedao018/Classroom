package com.raywenderlich.classroom

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.classroom.databinding.MainActivityBinding
import com.raywenderlich.model.DialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    private lateinit var viewModel: MainViewModel

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

        if (savedInstanceState == null) {
            val mainFragment = MainFragment.newInstance()
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.main_activity_container, mainFragment, MainFragment.MAIN_FRAGMENT_TAG)
            }
        }
        binding.floatingActionButtonAdd.setOnClickListener {
            showDialog_CreateClassroom()
        }

        val clickAnimListener = { view: View ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)

        }
        binding.floatingActionButtonClearAll.setOnClickListener { view ->
            clickAnimListener(view)
            viewModel.clearClassroomList()
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

}