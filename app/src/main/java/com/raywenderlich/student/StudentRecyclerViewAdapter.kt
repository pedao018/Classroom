package com.raywenderlich.student

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.classroom.databinding.StudentItemViewHolderBinding
import com.raywenderlich.model.Student

class StudentRecyclerViewAdapter(
    private val studentList: MutableList<Student>,
    private var onItemClick: ((student: Student) -> Unit)
) : RecyclerView.Adapter<StudentRecyclerViewAdapter.StudentItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentItemViewHolder {
        val binding = StudentItemViewHolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return StudentItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentItemViewHolder, position: Int) {
        val student = studentList[position]
        holder.binding.studentItemViewHolderClassid.text = "${student.id}"
        holder.binding.studentItemViewHolderClassname.text = "${student.name}"
        holder.itemView.setOnClickListener {
            onItemClick.invoke(student)
        }
    }

    override fun getItemCount() = studentList.size

    fun listUpdated() {
        notifyItemInserted(studentList.size - 1)
    }

    class StudentItemViewHolder(val binding: StudentItemViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root)
}