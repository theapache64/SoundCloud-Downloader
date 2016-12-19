package com.theah64.zoundloader.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * Created by shifar on 18/1/16.
 */
public class AdvancedEditText extends AppCompatEditText {

    public AdvancedEditText(Context context) {
        super(context);
        init();
    }

    public AdvancedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AdvancedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSingleLine(true);
    }

    @Nullable
    public String getString() {
        final String data = getText().toString().trim();
        if (data.isEmpty()) {
            return null;
        }
        return data;
    }
}
