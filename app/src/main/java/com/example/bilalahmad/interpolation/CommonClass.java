package com.example.bilalahmad.interpolation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;

public class CommonClass {
    ProgressDialog progress = null;
    boolean returning = false;
    public boolean validateInput(EditText email, EditText password) {
        boolean check = true;
        String emailText  = email.getText().toString().replace(" ", "");
        if (!emailText.contains("@") || !emailText.contains(".")) {
            email.setError("Email is not valid!");
            check = false;
        }
        if (password != null) {
            if (password.getText().toString().length() < 6) {
                password.setError("min Password length is 6!");
                check = false;
            }
        }
        return check;
    }

    public void showDialog(Spanned title, Spanned given, int icon, Context context) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(given)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) { }
                })
                .setIcon(icon)
                .show();
    }

    public boolean showConfirmation(Spanned title, Spanned given, int icon, Context context) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(given)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        returning = true;
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        returning = false;
                    }
                })
                .setIcon(icon)
                .show();
        return returning;
    }

    public boolean[] Listener(MotionEvent motionEvent, Button signIn, Context context) {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    signIn.setBackgroundDrawable(ContextCompat.getDrawable(context,
                            R.drawable.customborder3_1) );
                } else {
                    signIn.setBackground(ContextCompat.getDrawable(context,
                            R.drawable.customborder3_1));
                }
                return new boolean[]{true, false};
            case MotionEvent.ACTION_UP:
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    signIn.setBackgroundDrawable(ContextCompat.getDrawable(context,
                            R.drawable.customborder3) );
                } else {
                    signIn.setBackground(ContextCompat.getDrawable(context,
                            R.drawable.customborder3));
                }
                return new boolean[]{true, true};
            case MotionEvent.ACTION_CANCEL:
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    signIn.setBackgroundDrawable(ContextCompat.getDrawable(context,
                            R.drawable.customborder3) );
                } else {
                    signIn.setBackground(ContextCompat.getDrawable(context,
                            R.drawable.customborder3));
                }
                return new boolean[]{true, false};
        }
        return new boolean[]{false, false,};
    }

    public void showProgress(Context context, String title, String display) {
        progress = new ProgressDialog(context);
        progress.setTitle(Html.fromHtml(title));
        progress.setMessage(display);
        progress.setCancelable(false);
        progress.show();
    }

    public void cancelProgress() {
        try {
            progress.dismiss();
            progress.cancel();
        } catch (Exception e) {}
    }


}
