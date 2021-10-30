package com.raywenderlich.student

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.classroom.MainActivity
import com.raywenderlich.classroom.MainViewModel
import com.raywenderlich.classroom.MainViewModelFactory
import com.raywenderlich.classroom.databinding.StudentFragmentBinding
import com.raywenderlich.model.Classroom

class StudentFragment : Fragment() {
    private lateinit var binding: StudentFragmentBinding
    private lateinit var viewModel: MainViewModel

    companion object {
        fun newInstance() = StudentFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = StudentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(
            requireActivity(),
            MainViewModelFactory(PreferenceManager.getDefaultSharedPreferences(requireActivity()))
        ).get(MainViewModel::class.java)

        //Receive From Activity (MainActivity open StudentFragment)
        val classRoom: Classroom? = arguments?.getParcelable(MainActivity.CLASSROOM_INTENT_KEY)
        classRoom?.let {
            viewModel.classroom = classRoom
            requireActivity().title = classRoom.name
        }

        binding.studentFragmentListsRecyclerview.layoutManager =
            LinearLayoutManager(requireContext())
        val recyclerViewAdapter =
            StudentRecyclerViewAdapter(viewModel.classroom.studentList, onItemClick = { student ->
                Toast.makeText(
                    requireContext(),
                    "Student click " + student.name,
                    Toast.LENGTH_SHORT
                ).show()
            })
        binding.studentFragmentListsRecyclerview.adapter = recyclerViewAdapter
        viewModel.onStudentAdded = {
            recyclerViewAdapter.notifyDataSetChanged()
        }
    }

}