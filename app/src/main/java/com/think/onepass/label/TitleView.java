package com.think.onepass.label;

import android.content.Context;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.think.onepass.R;
import com.think.onepass.model.Secret;

public class TitleView extends ConstraintLayout{
    private static final String TAG = "TitleView";
    private Secret secret;
    private Context context;
    private TextView title;
    private ImageButton delete;
    private long startTime,endTime;
    private CallBack mCallBack;
    public TitleView(Context context) {
        super(context);
    }

    public TitleView(Context context, AttributeSet attrs, final Secret secret) {
        super(context, attrs);
        this.context=context;
        this.secret=secret;
        View view=LayoutInflater.from(context).inflate(R.layout.secret_title_item,this,true);
        title=view.findViewById(R.id.label_title);
        title.setText(secret.getTitle());
        delete=view.findViewById(R.id.label_delete);
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.deleteSecret(secret,TitleView.this);

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action=event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                startTime=System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                endTime=System.currentTimeMillis();
                if((endTime-startTime)<=0.1*1000){
                    int pointX=(int)event.getX();
                    int pointY=(int)event.getY(); 
                    Rect rect = new Rect();
                    delete.getGlobalVisibleRect(rect);
                    if(!rect.contains(pointX,pointY)) {
                        mCallBack.showSecret(secret);
                    }
                }
                break;
        }
        return true;
    }

    public void setCallBack(CallBack callBack){
        mCallBack=callBack;
    }
    public static interface CallBack{
        public void deleteSecret(Secret secret,View view);
        public void showSecret(Secret secret);
    }
}
