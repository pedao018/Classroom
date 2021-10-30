package com.raywenderlich.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable

class Classroom(
    val id: Int,
    var name: String,
    val studentList: MutableList<Student> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        arrayListOf<Student>().apply {
            parcel.readList(this, Student::class.java.classLoader)
        }
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
        dest.writeList(studentList)
    }

    companion object CREATOR : Parcelable.Creator<Classroom> {
        override fun createFromParcel(parcel: Parcel): Classroom {
            return Classroom(parcel)
        }

        override fun newArray(size: Int): Array<Classroom?> {
            return arrayOfNulls(size)
        }
    }
}