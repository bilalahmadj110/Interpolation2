package com.example.bilalahmad.interpolation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.signin.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignInPhone extends AppCompatActivity {
    Button verifyButton, getCodeButton;
    EditText phoneNoTextBox, verificationTextBox;
    String code;
    String verid ;
    private FirebaseAuth mAuth;
    CommonClass commonClass = new CommonClass();
    ButtonResendTask buttonResendTask = new ButtonResendTask();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_phone);


        setTitle("Phone Sign In");
        mAuth = FirebaseAuth.getInstance();

        phoneNoTextBox = findViewById(R.id.phoneno);
        verificationTextBox = findViewById(R.id.verify_textbox);

        verifyButton = findViewById(R.id.verify_button);
        verifyButton.setEnabled(false);
        verifyButton.setTextColor(getResources().getColor(R.color.background_entry));
        getCodeButton = findViewById(R.id.get_code);

        verifyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean[] get = commonClass.Listener(motionEvent, verifyButton, getApplicationContext());
                if (get[1])
                    verifyCode();
                return get[0];
            }
        });
        getCodeButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean[] get = commonClass.Listener(motionEvent, getCodeButton, getApplicationContext());
                if (get[1])
                    sendVerificationCode();
                return get[0];
            }
        });
    }

    public void verifyCode() {
        if (validateCode()) {
            if (verid == verificationTextBox.getText().toString()) {
                Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_LONG).show();
            } try {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code, verificationTextBox.getText().toString());
                SignInWithPhoneAuthCredentials(credential);
            } catch ( Exception e) {}
        }
    }

    private void SignInWithPhoneAuthCredentials(PhoneAuthCredential cred) {
        mAuth.signInWithCredential(cred)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successfully!", Toast.LENGTH_LONG).show();
                            SharedPreferences sharedPreferences = getSharedPreferences("loginCredentials",
                                    MODE_PRIVATE);
                            int flags = Base64.NO_WRAP | Base64.URL_SAFE;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("accessToken", Base64.encodeToString(mAuth.getAccessToken(true).
                                            toString().getBytes(),
                                    flags));
                            editor.apply();
                            startActivity(new Intent(SignInPhone.this, WelcomeActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    public boolean validatePhone() {
        boolean check = true;
        if (phoneNoTextBox.getText().toString().length() < 11)
            check = false;
        if (phoneNoTextBox.getText().toString().length() == 11) {
            if (Integer.parseInt(phoneNoTextBox.getText().toString().substring(0, 2)) != 03)
                check = false;
            if (Integer.parseInt(phoneNoTextBox.getText().toString().substring(2, 4)) > 49)
                check = false;
        }
        if (!check)
            phoneNoTextBox.setError("Phone no is not valid");
        return check;
    }

    public boolean validateCode() {
        boolean check = true;
        if (verificationTextBox.getText().toString().length() < 6)
            check = false;
        if (!check)
            verificationTextBox.setError("Code is not valid");
        return check;
    }

    private class ButtonResendTask extends AsyncTask<Void, Integer, Void> {
        int remaining = 60;

        @Override
        protected Void doInBackground(Void... Initial) {
            for (int i = remaining; i > 0; i--) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}
                publishProgress(remaining);
                remaining--;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            getCodeButton.setText("Resend code (" + remaining + ")");
        }

        @Override
        protected void onPostExecute(Void result) {
            getCodeButton.setEnabled(true);
            getCodeButton.setText("Get Code");
            getCodeButton.setTextColor(getResources().getColor(R.color.white));
        }

        @Override
        protected void onPreExecute() {
            getCodeButton.setText("Resend code (" + remaining + ")");
            getCodeButton.setEnabled(false);
            getCodeButton.setTextColor(getResources().getColor(R.color.background_entry));
        }

    }

    private void sendVerificationCode() {
        if (validatePhone()) {
            commonClass.showProgress(SignInPhone.this, "Loading", "Requesting code, please wait...");
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+92" + phoneNoTextBox.getText().toString().substring(1), 59, TimeUnit.SECONDS,
                    SignInPhone.this, mCallbacks);

            buttonResendTask.execute();
            verifyButton.setTextColor(getResources().getColor(R.color.white));
            verifyButton.setEnabled(true);
        }
    }
    PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {

            if (!buttonResendTask.isCancelled()) {
                try {
                    buttonResendTask.cancel(true);
                } catch (Exception e) {}
            }
            getCodeButton.setEnabled(true);
            getCodeButton.setText("Get Code");
            getCodeButton.setTextColor(getResources().getColor(R.color.white));
            verid = credential.getSmsCode();
            buttonResendTask.cancel(true);
            commonClass.cancelProgress();
            verificationTextBox.setText("" + credential.getSmsCode());
            SignInWithPhoneAuthCredentials(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            if (!buttonResendTask.isCancelled()) {
                try {
                    buttonResendTask.cancel(true);
                } catch (Exception ee) {}
            }
            getCodeButton.setEnabled(true);
            getCodeButton.setText("Get Code");
            getCodeButton.setTextColor(getResources().getColor(R.color.white));
            buttonResendTask.cancel(true);
            commonClass.cancelProgress();
            Toast.makeText(getApplicationContext(), "We are unable to send code! Please try again later.", Toast.LENGTH_LONG).show();
            if (e instanceof FirebaseAuthInvalidCredentialsException)
                Toast.makeText(getApplicationContext(), "Invalid Request!", Toast.LENGTH_LONG).show();
            else if (e instanceof FirebaseTooManyRequestsException)
                Toast.makeText(getApplicationContext(), "SMS quota reached!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
            try {
                commonClass.cancelProgress();
            } catch (Exception e){}
            code = verificationId;
            Toast.makeText(getApplicationContext(), "SMS sent!", Toast.LENGTH_LONG).show();
        }
    };
}
