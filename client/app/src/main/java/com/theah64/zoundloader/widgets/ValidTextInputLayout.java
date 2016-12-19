package com.theah64.zoundloader.widgets;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;


import com.theah64.zoundloader.R;

import java.util.regex.Pattern;

/**
 * Created by theapache64 on 7/2/16.
 */
public final class ValidTextInputLayout extends TextInputLayout {

    private AdvancedEditText et;
    private Pattern regExPattern;
    private int errorMessage = -1;

    public ValidTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initEt() {
        if (this.et == null) {
            this.et = (AdvancedEditText) getEditText();
            if (this.et == null) {
                throw new IllegalArgumentException("No EditText child found");
            }
        }
    }

    public void setRegEx(final String regEx) {
        this.regExPattern = Pattern.compile(regEx);
    }

    public void setErrorMessage(@StringRes final int errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isMatch() {

        initEt();

        if (et != null) {

            final String data = et.getString();

            if (data != null) {

                if (regExPattern != null) {

                    //This will match or nothing
                    final boolean isMatch = this.regExPattern.matcher(data).matches();

                    if (!isMatch) {

                        //Invalid
                        if (errorMessage != -1) {
                            setErrorEnabled(true);
                            setError(getContext().getString(errorMessage));
                        } else {
                            throw new IllegalArgumentException("REGEX is set, but error message is " + errorMessage);
                        }


                        return false;
                    } else {
                        //Valid
                        setErrorEnabled(false);
                        return true;
                    }
                } else {
                    //No regex is not , set it's valid
                    setErrorEnabled(false);
                    return true;
                }

            } else {
                //Data is empty
                setErrorEnabled(true);
                setError(getContext().getString(R.string.Empty));
                return false;
            }

        } else {
            throw new IllegalArgumentException("TextInputLayout doesn't has an EditText to validate");
        }

    }

    public void setText(String text) {
        initEt();
        et.setText(text);
    }

    public String getString() {
        initEt();
        return et.getString();
    }


    /**
     * Created by shifar on 7/2/16.
     */
    public static final class InputValidator {

        private final ValidTextInputLayout[] validInputLayouts;

        public InputValidator(ValidTextInputLayout... validInputLayouts) {
            this.validInputLayouts = validInputLayouts;
        }


        public boolean isAllValid() {
            boolean isAllValid = true;
            for (final ValidTextInputLayout inputLayout : validInputLayouts) {
                isAllValid = inputLayout.isMatch() && isAllValid;
            }
            return isAllValid;
        }

        /**
         * Used to clear all field's data.
         */
        public void clearFields() {
            for (final ValidTextInputLayout vtil : validInputLayouts) {
                vtil.setText(null);
            }
        }
    }


}