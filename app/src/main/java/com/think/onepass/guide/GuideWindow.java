package com.think.onepass.guide;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.think.onepass.R;
import com.think.onepass.util.SharePreferenceUtils;

public class GuideWindow extends CommonWindow{
    private Callback mCallback;
    private Button mClickButton;
    private ImageView mImageView;
    private TextView mTextView;
    private int imageId,textId;
    public GuideWindow(Context c, int layoutRes,int imageId,int textId,Callback callback) {
        super(c, layoutRes);
        mCallback = callback;
        this.imageId = imageId;
        this.textId = textId;
        createWindow();
        initWindow();
        initView();
        initEvent();
    }

    @Override
    public void initView() {
        mClickButton = contentView.findViewById(R.id.guide_bt);
        mImageView = contentView.findViewById(R.id.guide_iv);
        mImageView.setImageResource(imageId);
        mTextView = contentView.findViewById(R.id.guide_tv);
        mTextView.setText(textId);
    }

    @Override
    public void initEvent() {
        mClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInstance.dismiss();
                mCallback.onClickOk();
            }
        });
    }

    public interface Callback{
        public void onClickOk();
    }

}
