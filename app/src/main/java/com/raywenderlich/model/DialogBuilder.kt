package com.raywenderlich.model

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.widget.EditText
import com.raywenderlich.classroom.R

class DialogBuilder {
    companion object {
        fun showDialog(
            activity: Activity,
            title: String,
            positiveButtonTitle: String,
            onPositiveButtonClick: ((dialog: DialogInterface, editText: EditText) -> Unit)
        ) {
            val builder = AlertDialog.Builder(activity)
            val inflater = activity.layoutInflater
            val dialogLayout = inflater.inflate(R.layout.dialog_create, null)
            builder.setView(dialogLayout)
            builder.setTitle(title)

            val editText = dialogLayout.findViewById<EditText>(R.id.dialog_create_name_edt)

            builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
                onPositiveButtonClick.invoke(dialog, editText)
            }
            builder.create().show()
        }
    }
}