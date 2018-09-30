package com.think.onepass.view;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import com.think.onepass.R;

public class KeyboardUtil {
    private EditText metPassWord;
    private Keyboard mkbNumberKeyBoard;//数字键盘
    private KeyboardView keyboardView;

    /**
     * 构造函数
     * @param act
     * @param ed
     */
    public  KeyboardUtil(View act, EditText ed) {
        this.metPassWord = ed;
        //实例化数字键盘
        mkbNumberKeyBoard = new Keyboard(act.getContext(), R.layout.keyboard);
        //键盘容器
        keyboardView = (KeyboardView)act.findViewById(R.id.keyboardview);
        keyboardView.setKeyboard(mkbNumberKeyBoard);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(false);//是否上浮信息
        keyboardView.setOnKeyboardActionListener(listener);
    }

    //键盘动作监听
    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener(){
        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = metPassWord.getText();
            int start = metPassWord.getSelectionStart();
            if(primaryCode == Keyboard.KEYCODE_DELETE){//删除
                if(editable != null && editable.length() > 0){
                    editable.delete(start-1, start);
                }
            }
            else if (primaryCode == Keyboard.KEYCODE_CANCEL){//清除
                editable.clear();
            }
            else{//键盘其他键就插入
                editable.insert(start, Character.toString((char)primaryCode));
            }
        }
        @Override
        public void onPress(int primaryCode) {
        }
        @Override
        public void onRelease(int primaryCode) {
        }
        @Override
        public void onText(CharSequence text) {
        }
        @Override
        public void swipeDown() {
        }
        @Override
        public void swipeLeft() {
        }
        @Override
        public void swipeRight() {
        }
        @Override
        public void swipeUp() {
        }
    };

}

