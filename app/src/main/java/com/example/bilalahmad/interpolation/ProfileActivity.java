package com.example.bilalahmad.interpolation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.internal.firebase_auth.zzap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.MoreObjects;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProfileActivity extends AppCompatActivity {
    EditText name_, dob, contact, created, lastLogin, accountType;
    Button deleteAccount;
    CommonClass commonClass = new CommonClass();
    FirebaseAuth mAuth;
    ImageView imageView;
    int[] Ids =  new int[]{R.id.profile_name, R.id.contact, R.id.profile_account_type, R.id.profile_account_login,
                            R.id.profile_account_created};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("User Profile");

        mAuth = FirebaseAuth.getInstance();


        name_ = findViewById(R.id.profile_name);


        contact = findViewById(R.id.contact);
        created = findViewById(R.id.profile_account_created);
        lastLogin = findViewById(R.id.profile_account_login);
        accountType = findViewById(R.id.profile_account_type);
        deleteAccount = findViewById(R.id.delete_account);

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if  (commonClass.showConfirmation(Html.fromHtml("<b>Delete my account</b>"),
                        Html.fromHtml("Do you really want to delete your account permanently from our server?<br><b>Note:</b> It can't be undone!"),
                        R.drawable.ic_delete_forever_black_24dp, ProfileActivity.this)) {
                     mAuth.getCurrentUser().delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        mAuth.getCurrentUser().delete();
                                        mAuth.signOut();
                                        Toast.makeText(getApplicationContext(),
                                                "Your account has been vanished!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(ProfileActivity.this,
                                                LoginActivity.class));
                                        finish();
                                    }
                                });
                 }
            }
        });
        String type =  mAuth.getCurrentUser().getProviders().toString().replace(".com", "")
                .replace("[", "").replace("]", "");
        if (mAuth.getCurrentUser().getEmail() != null) {
            contact.setText(mAuth.getCurrentUser().getEmail());
        } else {
            contact.setText(mAuth.getCurrentUser().getPhoneNumber());
        }


        name_.setText(mAuth.getCurrentUser().getDisplayName());

        accountType.setText(type);
        imageView = findViewById(R.id.profile_image);

        if (type == "facebook" || type == "google")
        Picasso.with(this).load(mAuth.getCurrentUser().getPhotoUrl().toString().replace("/s96-c/","/s300-c/"))
                .transform(new CircleTransform())
                .error(getResources().getDrawable(R.drawable.ic_account_circle_black_24dp))
                .placeholder(getResources().getDrawable(R.drawable.ic_account_circle_black_24dp))
                .into(imageView);

        Date lastlogin = new Date(mAuth.getCurrentUser().getMetadata().getLastSignInTimestamp());
        DateFormat f = new SimpleDateFormat("MMM dd, yyyy - hh:mm a");
        Date creation = new Date(mAuth.getCurrentUser().getMetadata().getCreationTimestamp());
        created.setText(f.format(creation));
        lastLogin.setText(f.format(lastlogin));



    }

    public void updateProfile() {
        for (int iter : Ids) {
            ((EditText) findViewById(iter)).setFocusable(true);
            ((EditText) findViewById(iter)).setClickable(true);
        }
        mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = null;
        if (mAuth.getCurrentUser().getDisplayName() != ((EditText) findViewById(R.id.profile_name)).getText().toString())
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(((EditText) findViewById(R.id.profile_name)).getText().toString())
                    .build();


        mAuth.getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });


    }

    public void editProfile() {
        for (int iter : Ids) {
            ((EditText) findViewById(iter)).setFocusable(false);
            ((EditText) findViewById(iter)).setClickable(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit_profile:
                editProfile();
            case R.id.update_profile:
                updateProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menus, menu);
        return true;
    }
}
