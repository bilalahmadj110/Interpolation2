package com.example.bilalahmad.interpolation;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterAccount extends AppCompatActivity {
    Button signIn, already;
    EditText email, password;
    private FirebaseAuth mAuth;
    CommonClass commonClass;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        setTitle("Register Account");

        commonClass = new CommonClass();

        already = findViewById(R.id.already_button_register);
        email = findViewById(R.id.email_textbox_register);
        password = findViewById(R.id.password_textbox_register);
        signIn = findViewById(R.id.signin_button_register);
        mAuth = FirebaseAuth.getInstance();
        ((TextView) findViewById(R.id.caution)).setText(getString(R.string.cautions));


        signIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean[] get = commonClass.Listener(motionEvent, signIn, getApplicationContext());
                if (get[1])
                    SignUp();
                return get[0];
            }
        });

        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterAccount.this, LoginActivity.class));
            }
        });

    }

    public void SignUp() {
        if (commonClass.validateInput(email, password)) {
            commonClass.showProgress(RegisterAccount.this, "Loading",
                    "Registering account, please wait...");
            mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            commonClass.cancelProgress();
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification();
                                commonClass.showDialog(Html.fromHtml("<b>Account Regisstered!</b>"),
                                        Html.fromHtml("<b>Congralutions!</b> Your account has been registered successfully," +
                                        " but you can't login until you verify email link we've just sent it to you!"),
                                        R.drawable.ic_info_black_24dp, RegisterAccount.this);
                            } else {
                                commonClass.showDialog(
                                        Html.fromHtml("<b>Authentication Error</b>"),
                                        Html.fromHtml(task.getException().getMessage()),
                                        R.drawable.ic_error_outline_black_24dp, RegisterAccount.this);
                            }
                        }
                    });
        }
    }

}
