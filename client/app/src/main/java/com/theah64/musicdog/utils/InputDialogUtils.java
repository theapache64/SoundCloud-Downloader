package com.theah64.musicdog.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.theah64.musicdog.R;
import com.theah64.musicdog.widgets.AdvancedEditText;
import com.theah64.musicdog.widgets.ValidTextInputLayout;


/**
 * Created by shifar on 23/4/16.
 */
public class InputDialogUtils {

    private static final String X = InputDialogUtils.class.getSimpleName();
    public static final int MAX_LENGTH_INFINITE = -1;
    private final Context context;
    private LayoutInflater inflater;

    public InputDialogUtils(Context context) {
        this.context = context;
    }

    public void showInputDialog(@StringRes int title, @StringRes int subTitle, @StringRes int placeHolder, final @StringRes int errorMessage,
                                final BasicInputCallback inputCallback, int maxLength, final String regEx, int inputType, String preText) {

        if (inflater == null) {
            inflater = LayoutInflater.from(context);
        }

        @SuppressLint("InflateParams")
        final View inputLayout = inflater.inflate(R.layout.input_dialog_layout, null);

        //Setting hint
        final ValidTextInputLayout vtilInput = (ValidTextInputLayout) inputLayout.findViewById(R.id.vtilInput);
        vtilInput.setErrorMessage(errorMessage);
        vtilInput.setRegEx(regEx);
        vtilInput.setText(preText);
        vtilInput.setHint(context.getString(placeHolder));

        //Setting input length
        final AdvancedEditText aetInput = (AdvancedEditText) vtilInput.getEditText();

        if (aetInput == null) {
            throw new IllegalArgumentException("aetInput is null");
        }

        aetInput.setInputType(inputType);

        if (maxLength != MAX_LENGTH_INFINITE) {
            aetInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }

        //Creating input dialog
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(true)
                .setMessage(subTitle)
                .setView(inputLayout)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //to mock the dialog builder, real action below.
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);


        final AlertDialog dialog = dialogBuilder.create();

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Validating data here
                if (vtilInput.isMatch()) {

                    final String inputData = vtilInput.getString().trim();

                    //Informing launched activity
                    vtilInput.setErrorEnabled(false);
                    Log.i(X, "Valid data :" + inputData);
                    inputCallback.onValidInput(inputData);
                    dialog.dismiss();
                }

            }
        });

    }

    public interface BasicInputCallback {
        void onValidInput(final String inputTex);
    }

}
