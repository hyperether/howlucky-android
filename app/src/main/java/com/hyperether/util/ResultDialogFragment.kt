package com.hyperether.util

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.hyperether.howlucky.HowLuckyActivity
import com.hyperether.howlucky.R

class ResultDialogFragment : DialogFragment() {
    private var mMessage: String? = null
    private var type = 0
    private var percent = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_result, container, false)
        val percentage = view.findViewById<View>(R.id.percentage_lucky_txt) as TextView
        val message = view.findViewById<View>(R.id.msg_txt) as TextView
        val img = view.findViewById<View>(R.id.img_result) as ImageView
        type = requireArguments().getInt("type", 0)
        percent = requireArguments().getInt("perc", 0)
        mMessage = requireArguments().getString("msg", "")
        percentage.text = "$percent%"
        message.text = mMessage
        val btnOk = view.findViewById<View>(R.id.btn_close) as Button
        btnOk.setOnClickListener {
            val callingActivity = activity as HowLuckyActivity?
            callingActivity!!.onUserConfirm()
            dialog!!.dismiss()
        }
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        when (type) {
            CLOVER -> img.setImageDrawable(resources.getDrawable(R.drawable.clover))
            HORSESHOE -> img.setImageDrawable(resources.getDrawable(R.drawable.horseshoe))
            RABBIT -> img.setImageDrawable(resources.getDrawable(R.drawable.rabbit))
        }
        return view
    }

    companion object {
        const val CLOVER = 0
        const val HORSESHOE = 1
        const val RABBIT = 2
        fun newInstance(type: Int, percent: Int, msg: String?): ResultDialogFragment {
            val fragment = ResultDialogFragment()
            val b = Bundle()
            b.putInt("type", type)
            b.putInt("perc", percent)
            b.putString("msg", msg)
            fragment.arguments = b
            return fragment
        }
    }
}