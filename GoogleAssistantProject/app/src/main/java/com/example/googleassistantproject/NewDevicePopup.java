package com.example.googleassistantproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class NewDevicePopup extends DialogFragment {

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.addDevice)
                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(), AddNewDevice.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        return builder.create();
    }
    public static String TAG = "NewDevicePopup";
}
