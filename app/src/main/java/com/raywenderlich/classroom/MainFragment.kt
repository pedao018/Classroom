package com.raywenderlich.classroom

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.classroom.databinding.MainFragmentBinding
import com.raywenderlich.model.Classroom
import com.raywenderlich.student.StudentActivity

class MainFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var startActivity_ForResult: ActivityResultLauncher<Intent>

    companion object {
        const val MAIN_FRAGMENT_TAG = "MAIN_FRAGMENT_TAG"


        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(),
            MainViewModelFactory(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
        ).get(MainViewModel::class.java)

        binding.mainFragmentListsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        val recyclerViewAdapter = ClassroomRecyclerViewAdapter(viewModel.classRoomList,
            onItemClick = { classRoom ->
                //openStudentActivity(classRoom)
                openStudentActivity_WithResult(classRoom)
            })
        binding.mainFragmentListsRecyclerview.adapter = recyclerViewAdapter
        viewModel.onClassroomAdded = {
            recyclerViewAdapter.listUpdated()
        }

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
        viewModel.onClassroomListRefresh = {
            recyclerViewAdapter.notifyDataSetChanged()
        }
    }

    private fun openStudentActivity(classRoom: Classroom) {
        val intent = Intent(requireActivity(), StudentActivity::class.java).apply {
            putExtra(MainActivity.CLASSROOM_INTENT_KEY, classRoom)
        }
        startActivity(intent)
    }

    private fun openStudentActivity_WithResult(classRoom: Classroom) {
        val intent = Intent(requireActivity(), StudentActivity::class.java)
        intent.putExtra(MainActivity.CLASSROOM_INTENT_KEY, classRoom)
        startActivity_ForResult.launch(intent)
    }
}