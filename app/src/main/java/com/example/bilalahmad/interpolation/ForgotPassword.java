package com.example.bilalahmad.interpolation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    
    Button reset;
    EditText email;
    CommonClass commonClass;
    private FirebaseAuth mAuth;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle("Forget Password");


        mAuth = FirebaseAuth.getInstance();

        commonClass = new CommonClass();


        email = findViewById(R.id.email_textbox_reset);

        reset = findViewById(R.id.signin_button_reset);

        reset.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean[] get = commonClass.Listener(motionEvent, reset, ForgotPassword.this);
                if (get[1])
                    Reset();
                return get[0];
            }
        });
    }

    public void Reset() {
        EditText editText = new EditText(this);
        editText.setText("123456");
        if (commonClass.validateInput(email, editText)) {
            commonClass.showProgress(ForgotPassword.this, "Loading", "Requesting reset password, please wait...");
            mAuth.sendPasswordResetEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Sending reset password!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "We are unable to send reset password," +
                                    " please try again later.", Toast.LENGTH_LONG).show();
                        }
                    });
            commonClass.cancelProgress();
        }
    }
}
