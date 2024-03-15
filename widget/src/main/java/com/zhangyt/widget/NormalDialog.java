package com.zhangyt.widget;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.zhangyt.utils.DipUtils;

public class NormalDialog extends DialogFragment {
    private ImageView icon;
    private TextView tvTitle, tvMessage;
    private Button btnPositive, btnNegative;
    private View.OnClickListener positiveClickListener;
    private View.OnClickListener negativeClickListener;
    private String title, message, positiveStr, negativeStr;

    public NormalDialog(Builder builder) {
        this.title = builder.title;
        this.message = builder.message;
        this.positiveStr = builder.positiveStr;
        this.negativeStr = builder.negativeStr;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_normal, container);
        //去掉默认背景
        final Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(R.color.transparent);

        icon = view.findViewById(R.id.icon);
        tvTitle = view.findViewById(R.id.title);
        tvMessage = view.findViewById(R.id.message);
        btnNegative = view.findViewById(R.id.btn_negative);
        btnPositive = view.findViewById(R.id.btn_positive);
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (positiveClickListener != null)
                    positiveClickListener.onClick(view);
                dismiss();
            }
        });
        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (negativeClickListener != null)
                    negativeClickListener.onClick(view);
                dismiss();
            }
        });
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        }
        tvMessage.setText(message);
        if (TextUtils.isEmpty(negativeStr)||negativeClickListener==null){
            btnNegative.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DipUtils.dipToPx(getContext(),200),DipUtils.dipToPx(getContext(),45));
            btnPositive.setLayoutParams(params);
        }else {
            btnNegative.setVisibility(View.VISIBLE);
            btnNegative.setText(negativeStr);
        }
        btnPositive.setText(positiveStr);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
    }

    public void setPositiveClickListener(View.OnClickListener listener) {
        positiveClickListener = listener;
    }

    public void setNegativeClickListener(View.OnClickListener listener) {
        negativeClickListener = listener;
    }

    public void showIcon(int res){
        icon.setImageDrawable(getContext().getDrawable(res));
        icon.setVisibility(View.VISIBLE);
    }
    public void hideIcon(){
        icon.setVisibility(View.GONE);
    }
    public static class Builder {
        private String title, message, positiveStr, negativeStr;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setPositiveStr(String positiveStr) {
            this.positiveStr = positiveStr;
            return this;
        }

        public Builder setNegativeStr(String negativeStr) {
            this.negativeStr = negativeStr;
            return this;
        }

        public NormalDialog build() {
            return new NormalDialog(this);
        }
    }
}
