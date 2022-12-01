package com.hyperether.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.hyperether.howlucky.HowLuckyActivity;
import com.hyperether.howlucky.R;

public class MessageDialogFragment extends DialogFragment {

    private String mMessage;

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mMessage)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // get the calling activity
                        HowLuckyActivity callingActivity = (HowLuckyActivity) getActivity();
                        callingActivity.onUserConfirm();
                        dialog.dismiss();

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
