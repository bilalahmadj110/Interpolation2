package com.example.bilalahmad.interpolation;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.example.bilalahmad.interpolation.R.*;

public class WelcomeActivity extends AppCompatActivity {
    @SuppressLint("UseSparseArrays")
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // I'm declaring this ARRAY LIST for views so that I can remove EditText box (boxes) when user click on remove (clear) button
    ArrayList<LinearLayout> views = new ArrayList<>();
    ArrayList<TextBoxes> textBoxes = new ArrayList<>();

    String msg = "         This application is used to calculate simple <b>Numerical Methods</b> problems.<br>"+
            "<br><b><u>Features:</u></b><br>"+
            "&#8226; Your profile data is saved on our backend server (Firebase server).<br>"+
            "&#8226; All your calculated history will automatically be saved on server.<br>"+
            "&#8226; You can access data anywhere in the world using this app.<br>"+
            "<br><b><u>Most Important:</b></u><br>" +
            "This Application is dedicated to <b>Telecom Alpha 2k17.</b><br>"+
            "This Application is developed by <b>Bilal Ahmad.</b>";

    AppCompatImageButton addButton, removeButton, clearButton;
    CommonClass commonClass = new CommonClass();
    AppCompatButton calculate;
    TextView notification;
    EditText calculateAt;
    LinearLayout mainLayout;
    Spinner dropDownSelection;
    public int count = 0;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        mainLayout = findViewById(id.add_entry_to);
        mAuth = FirebaseAuth.getInstance();

        dropDownSelection = findViewById(id.select_method);
        notification = findViewById(id.notification);
        addButton = findViewById(id.add_button);
        removeButton = findViewById(id.remove_button);
        clearButton = findViewById(id.clear_button);
        calculate = findViewById(id.calculate);
        calculateAt = findViewById(id.calculate_at);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEntry();
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeEntry();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearEntries();
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    if (dropDownSelection.getSelectedItemPosition() == 5)
                        calculateLagrange();
                    else if (dropDownSelection.getSelectedItemPosition() == 0)
                        calculateForward();
                    else if (dropDownSelection.getSelectedItemPosition() == 1)
                        calculateBackward();

                }
            }
        });
        if (savedInstanceState != null) {
            calculateAt.setText(savedInstanceState.getString("calculate-at", ""));
            int totalSize = savedInstanceState.getInt("size", 0);
            for (int i = 0; i < totalSize; i++)
                addEntry();
            changeAllText(savedInstanceState, totalSize);
        } else {
            addEntry();
        }
    }

    public void changeAllText(Bundle saved, int size) {
        for (int i = 0; i < size; i++) {
            ((EditText) findViewById(textBoxes.get(i).getYId())).setText(saved.getString("-" + (i + 1), ""));
            ((EditText) findViewById(textBoxes.get(i).getXId())).setText(saved.getString("" + (i + 1), ""));
        }

    }

    @Override
    public void onBackPressed() {
    }

    public void logOut() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("loginCredentials",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            mAuth.signOut();
            Toast.makeText(getApplicationContext(), "Logout Successfully!", Toast.LENGTH_LONG).show();
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Logout Unsuccessful!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case id.history:
                startActivity(new Intent(WelcomeActivity.this, HistoryActivity.class));
                break;
            case id.profile:
                startActivity(new Intent(WelcomeActivity.this, ProfileActivity.class));
                break;
            case id.logout:
                logOut();
                break;
            case id.about:
                commonClass.showDialog(Html.fromHtml("About App"),
                        Html.fromHtml(msg),
                        drawable.ic_info_black_24dp, WelcomeActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addEntry() {
        count++;
        LayoutInflater inflater = getLayoutInflater();
        View otherView = inflater.inflate(layout.xy_container, null);
        LinearLayout layout = otherView.findViewById(id.add_entry_from);
        ((TextView) otherView.findViewById(id.entry_no)).setText("Entry - " + count);
        int x = count;
        int y = count * 1000;
        TextBoxes textBoxes1 = new TextBoxes(x, y);
        otherView.findViewById(id.x_value).setId(x);
        otherView.findViewById(id.y_value).setId(y);

        textBoxes.add(textBoxes1);
        ((ViewGroup) layout.getParent()).removeView(layout);
        mainLayout.addView(layout);
        views.add(layout);
    }

    public void removeEntry() {
        if (count > 1) {
            mainLayout.removeView(views.get(count - 1));
            views.remove(count - 1);
            textBoxes.remove(count - 1);
            count--;
        }
    }

    public void clearEntries() {
        while (count > 1)
            removeEntry();
        ((EditText) findViewById(textBoxes.get(0).getXId())).setText("");
        ((EditText) findViewById(textBoxes.get(0).getYId())).setText("");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("size", views.size());
        outState.putString("calculate-at", calculateAt.getText().toString());
        for (int i = 0; i < views.size(); i++) {
            outState.putString("" + (i + 1), ((EditText) findViewById(textBoxes.get(i).getXId())).getText().toString());
            outState.putString("-" + (i + 1), ((EditText) findViewById(textBoxes.get(i).getYId())).getText().toString());
        }
    }

    public boolean validateInput() {
        boolean x = false, y = false, find = false;
        int i;
        for (i = 0; i < count; i++) {
            if (((EditText) findViewById(textBoxes.get(i).getXId())).getText().toString().equals("")) {
                x = true;
                break;
            } else if (((EditText) findViewById(textBoxes.get(i).getYId())).getText().toString().equals("")) {
                y = true;
                break;
            } else if (((EditText) (findViewById(id.calculate_at))).getText().toString().equals("")) {
                find = true;
                break;
            }
        }
        if (!(!x && (!y && !find)))
            commonClass.showDialog(
                    Html.fromHtml("<b>Empty Field Error</b>"),
                    Html.fromHtml("Please enter into all the required fields!"),
                    drawable.ic_error_outline_black_24dp, WelcomeActivity.this);
        if (x)
            findViewById(textBoxes.get(i).getXId()).requestFocus();
        else if (y)
            findViewById(textBoxes.get(i).getYId()).requestFocus();
        else if (find)
            findViewById(id.calculate_at).requestFocus();
        return (!x && (!y && !find));
    }

    public void calculateBackward() {
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        for (int i = 0; i < count; i++) {
            xValues[i] = Double.parseDouble(((EditText) findViewById(textBoxes.get(i).getXId())).getText().toString());
            yValues[i] = Double.parseDouble(((EditText) findViewById(textBoxes.get(i).getYId())).getText().toString());
        }
        NewtonBackwardFormula backwardFormula = new NewtonBackwardFormula();
        ans = backwardFormula.calculateBackward(xValues, yValues, Double.parseDouble(
                ((EditText) (findViewById(id.calculate_at))).getText().toString()));
        commonClass.showDialog(
                Html.fromHtml("<b>Newton Backward Result</b>"),
                Html.fromHtml(
                        "The value of f(x) at x = " + ((EditText) (findViewById(id.calculate_at))).getText().toString() +
                                " is <b>" + ans + "</b>."), drawable.ic_info_black_24dp,
                WelcomeActivity.this);
        saveValues();
    }

    public void calculateForward() {
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        for (int i = 0; i < count; i++) {
            xValues[i] = Double.parseDouble(((EditText) findViewById(textBoxes.get(i).getXId())).getText().toString());
            yValues[i] = Double.parseDouble(((EditText) findViewById(textBoxes.get(i).getYId())).getText().toString());
        }
        NewtonForwardFormula forwardFormula = new NewtonForwardFormula();
        ans = forwardFormula.calculateForward(xValues, yValues, Double.parseDouble(
                ((EditText) (findViewById(id.calculate_at))).getText().toString()));
        commonClass.showDialog(
                Html.fromHtml("<b>Newton Forward Result</b>"),
                Html.fromHtml(
                        "The value of f(x) at x = " + ((EditText) (findViewById(id.calculate_at))).getText().toString() +
                                " is <b>" + ans + "</b>."), drawable.ic_info_black_24dp,
                WelcomeActivity.this);
        saveValues();
    }
    double ans;
    public void calculateLagrange() {
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        for (int i = 0; i < count; i++) {
            xValues[i] = Double.parseDouble(((EditText) findViewById(textBoxes.get(i).getXId())).getText().toString());
            yValues[i] = Double.parseDouble(((EditText) findViewById(textBoxes.get(i).getYId())).getText().toString());
        }
        ans = LagrangeFormula.calculateLagrangeValue(
                xValues, yValues, Double.parseDouble(((EditText) (findViewById(id.calculate_at))).getText().toString()));
        commonClass.showDialog(
                Html.fromHtml("<b>Lagrange Result</b>"),
                Html.fromHtml(
                        "The value of f(x) at x = " + ((EditText) (findViewById(id.calculate_at))).getText().toString() +
                                " is <b>" + ans + "</b>."), drawable.ic_info_black_24dp,
                WelcomeActivity.this);
        saveValues();
    }

    public void saveValues() {
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        Map<String, Object> user = new HashMap<>();

        for (int i = 0; i < count; i++) {
            xValues[i] = Double.parseDouble(((EditText) findViewById(textBoxes.get(i).getXId())).getText().toString());
            yValues[i] = Double.parseDouble(((EditText) findViewById(textBoxes.get(i).getYId())).getText().toString());
            user.put("x_y_Values", xValues[i] + "," + yValues[i] + " " + (user.get("x_y_Values")==null?"":
                    user.get("x_y_Values")) + " ");
        }

        user.put("Using_Formula", dropDownSelection.getSelectedItem().toString().replace("Formula", ""));
        user.put("Answer", ans);
        user.put("Dated", new Timestamp(System.currentTimeMillis()));
        user.put("At_Value", ((EditText) (findViewById(id.calculate_at))).getText().toString());


        db.collection("InterpolationCalculations" + mAuth.getCurrentUser().getUid())
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("0", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("1", "Error adding document", e);
                    }
                });
    }
}

