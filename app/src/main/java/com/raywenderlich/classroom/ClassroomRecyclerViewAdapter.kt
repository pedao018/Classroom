package com.raywenderlich.classroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.classroom.databinding.ClassroomItemViewHolderBinding
import com.raywenderlich.model.Classroom

class ClassroomRecyclerViewAdapter(
    private val classRoomList: MutableList<Classroom>,
    private var onItemClick: ((classRoom: Classroom) -> Unit)
) : RecyclerView.Adapter<ClassroomRecyclerViewAdapter.ClassroomItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomItemViewHolder {
        val binding = ClassroomItemViewHolderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ClassroomItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassroomItemViewHolder, position: Int) {
        val classroom = classRoomList[position]
        holder.binding.classroomItemViewHolderClassid.text = "${classroom.id}"
        holder.binding.classroomItemViewHolderClassname.text = "${classroom.name}"
        holder.itemView.setOnClickListener {
            onItemClick.invoke(classroom)
        }
    }

    override fun getItemCount() = classRoomList.size

    fun listUpdated() {
        notifyItemInserted(classRoomList.size - 1)
    }

    class ClassroomItemViewHolder(val binding: ClassroomItemViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root)
}