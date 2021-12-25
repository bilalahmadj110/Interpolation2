package com.example.bilalahmad.interpolation;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseUser;

public class FirbaseHandler {
    FirebaseUser user;
    Context context;

    public FirbaseHandler(FirebaseUser user, Context context) {
//        "https://graph.facebook.com/" + user.getProviderData().get(1).getUid() + "/picture?type=large"
        this.user = user;
        SharedPreferences sharedPreferences = context.getSharedPreferences("loginCredentials",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("loginCredentials", user.getUid());
//        editor.putString("token", user.getIdToken(true));
        editor.putString("name", user.getDisplayName());
        editor.putString("email", user.getEmail());
        editor.putString("phone", user.getPhoneNumber());
//        editor.putString("photo", user.getPhotoUrl().toString());
        editor.putString("provider", user.getProviderId());
        editor.putLong("creationData", user.getMetadata().getCreationTimestamp());
        editor.putLong("lastSignIn", user.getMetadata().getCreationTimestamp());


//        editor.putString("providerData", user.getProviderData().toArray());
    }


}
