package com.example.bilalahmad.interpolation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;

import static com.google.android.gms.auth.api.Auth.GOOGLE_SIGN_IN_API;


public class LoginActivity extends AppCompatActivity {
    Button signIn, forgot, create;
    EditText email, password;
    private FirebaseAuth mAuth;
    boolean service;
    CommonClass commonClass;
    private static final int RC_SIGN_IN = 1;
    private  CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Login Account");

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
            finish();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        commonClass = new CommonClass();

        email = findViewById(R.id.email_textbox);
        password = findViewById(R.id.password_textbox);
        create = signIn = findViewById(R.id.create);
        signIn = findViewById(R.id.signin_button);

        forgot = findViewById(R.id.forgot);

        signIn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean[] get = commonClass.Listener(motionEvent, signIn, getApplicationContext());
                if (get[1])
                    SignInCall();
                return get[0];
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Forgot();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Create();
            }
        });

        findViewById(R.id.phone_layout).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignInPhone.class));
            }
        });

        findViewById(R.id.google_layout).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SignInGoogle();
            }
        });
        findViewById(R.id.facebook_layout).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please wait while Signing with Facebook...", Toast.LENGTH_LONG).show();
                SigInFacebook();
            }
        });


        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (status == ConnectionResult.SUCCESS)
            service = true;
        else
            service = false;


    }

    @Override
    public void onBackPressed() {
    }

    public void SignInCall() {
        if (service) {
            if (commonClass.validateInput(email, password)) {
                commonClass.showProgress(this, "Loading", "Signing, please wait...");
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                commonClass.cancelProgress();
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (!user.isEmailVerified()) {
                                        String msg = "You account has been created but isn't verified yet! Please " +
                                                "<b>open your mail</b> to <b>verify</b> link we've sent you, then came back.";
                                        commonClass.showDialog(Html.fromHtml("<b>Email Verification Error</b>"),
                                                Html.fromHtml(msg),
                                                R.drawable.ic_info_black_24dp, LoginActivity.this);
                                    } else {
                                        FirbaseHandler firbaseHandler = new FirbaseHandler(user, getApplicationContext());
                                        startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                                        finish();
                                    }
                                } else {
                                    commonClass.showDialog(Html.fromHtml("<b>Authentication Error</b>"),
                                            Html.fromHtml(task.getException().getMessage()),
                                            R.drawable.ic_error_outline_black_24dp, LoginActivity.this);
                                }
                            }
                        });
            }
        } else
            commonClass.showDialog(Html.fromHtml("<b>Google Play Error</b>"), Html.fromHtml(
                    "Interpolation App won't run unless you <b>update Google Play services!</b>"),
                    R.drawable.ic_error_outline_black_24dp, LoginActivity.this);
    }

    public void Forgot() {
        startActivity(new Intent(LoginActivity.this, ForgotPassword.class));
    }

    public void Create() {
        startActivity(new Intent(LoginActivity.this, RegisterAccount.class));
    }


    private GoogleSignInClient mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;


    public void SignInGoogle() {
        mCallbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Sign In Failed", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(GOOGLE_SIGN_IN_API, gso)
                .build();
        SignIn();

    }

    private void SignIn() {
        Toast.makeText(getApplicationContext(), "Please wait while Signing with Google...", Toast.LENGTH_LONG).show();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleFacebookAccessToken(AccessToken token) {


        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            LoginManager.getInstance().logOut();
                            FirbaseHandler firbaseHandler = new FirbaseHandler(user, getApplicationContext());
                            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Facebook Authentication Failed!" + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void SigInFacebook() {
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        try {
                                            String email = object.getString("email");
                                            String birthday = object.getString("birthday"); // 01/31/1980 format
                                        } catch (Exception e) {}
                                    }
                                });
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Facebook login cancelled!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, "We are unable to Sign In using facebook! Please try again later.", Toast.LENGTH_LONG).show();
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                Arrays.asList("public_profile", "email"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getApplicationContext(), "Google Login Cancelled!", Toast.LENGTH_LONG).show();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirbaseHandler firbaseHandler = new FirbaseHandler(user, getApplicationContext());
                            mGoogleSignInClient.signOut();
                            mGoogleApiClient.maybeSignOut();
                            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                            finish();
                        } else {
                             Toast.makeText(getApplicationContext(),
                                     "Error! Can't Sign In, Please try again, or check your Internet Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}