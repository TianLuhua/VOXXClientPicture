package com.sat.satpic.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.sat.satpic.R;

/**
 * Created by Tianluhua on 2018/3/26.
 */

public class NetworkDialog extends DialogFragment {

    private int title;
    private int message;
    private int positoveButton;
    private NetworkDialogInterface networkDialogInterface;

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public int getPositoveButton() {
        return positoveButton;
    }

    public void setPositoveButton(int positoveButton) {
        this.positoveButton = positoveButton;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(positoveButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (networkDialogInterface != null) {
                            networkDialogInterface.onClick(dialog, id);
                        }
                    }
                });

        return builder.create();
    }

    public void setNetworkDialogInterface(NetworkDialogInterface networkDialogInterface) {
        this.networkDialogInterface = networkDialogInterface;
    }

    public interface NetworkDialogInterface {
        void onClick(DialogInterface dialog, int id);
    }


}
