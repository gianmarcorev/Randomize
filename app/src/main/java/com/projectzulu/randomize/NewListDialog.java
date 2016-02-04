package com.projectzulu.randomize;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by gianmarco on 26/01/16.
 */
public class NewListDialog extends DialogFragment {

    public NewListDialog() {
    }

    public interface NewListDialogListener {
        public void onDialogPositiveClick(String name);
    }

    private NewListDialogListener mListener;

    public void setListener(NewListDialogListener listener) {
        mListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_list_name_title)
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_new_list, null))
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = (EditText) ((AlertDialog) dialog)
                                .findViewById(R.id.new_list_name);
                        String name = editText.getText().toString();
                        mListener.onDialogPositiveClick(name);
                    }
                });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        AlertDialog dialog = (AlertDialog) getDialog();

        final Button createButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        createButton.setEnabled(false);

        final EditText editText = (EditText) dialog.findViewById(R.id.new_list_name);
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        createButton.setEnabled(s.length() > 0);
                    }
                }
        );
    }
}