package com.hyperether.util;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyperether.howlucky.HowLuckyActivity;
import com.hyperether.howlucky.R;

public class ResultDialogFragment extends DialogFragment {
    public static final int CLOVER = 0;
    public static final int HORSESHOE = 1;
    public static final int RABBIT = 2;

    private String mMessage;
    private int type;
    private int percent;

    public static ResultDialogFragment newInstance(int type, int percent, String msg) {
        ResultDialogFragment fragment = new ResultDialogFragment();
        Bundle b = new Bundle();
        b.putInt("type", type);
        b.putInt("perc", percent);
        b.putString("msg", msg);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_result, container, false);
        TextView percentage = (TextView) view.findViewById(R.id.percentage_lucky_txt);
        TextView message = (TextView) view.findViewById(R.id.msg_txt);
        ImageView img = (ImageView) view.findViewById(R.id.img_result);
        type = getArguments().getInt("type", 0);
        percent = getArguments().getInt("perc", 0);
        mMessage = getArguments().getString("msg", "");
        percentage.setText(percent + "%");
        message.setText(mMessage);
        Button btnOk = (Button) view.findViewById(R.id.btn_close);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HowLuckyActivity callingActivity = (HowLuckyActivity) getActivity();
                callingActivity.onUserConfirm();
                getDialog().dismiss();
            }
        });

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        switch (type) {
            case CLOVER:
                img.setImageDrawable(getResources().getDrawable(R.drawable.clover));
                break;
            case HORSESHOE:
                img.setImageDrawable(getResources().getDrawable(R.drawable.horseshoe));
                break;
            case RABBIT:
                img.setImageDrawable(getResources().getDrawable(R.drawable.rabbit));
                break;
        }
        return view;
    }
}
