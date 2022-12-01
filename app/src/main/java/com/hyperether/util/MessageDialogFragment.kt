package com.hyperether.util

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hyperether.howlucky.HowLuckyActivity
import com.hyperether.howlucky.R

class MessageDialogFragment : DialogFragment() {
    private var mMessage: String? = null
    fun setmMessage(mMessage: String?) {
        this.mMessage = mMessage
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(mMessage)
            .setPositiveButton(R.string.confirm) { dialog, id -> // get the calling activity
                val callingActivity = activity as HowLuckyActivity?
                callingActivity!!.onUserConfirm()
                dialog.dismiss()
            }
        // Create the AlertDialog object and return it
        return builder.create()
    }
}