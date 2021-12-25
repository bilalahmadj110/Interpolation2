package com.example.bilalahmad.interpolation;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.lang.ref.Reference;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<Type> mArrayList = new ArrayList<>();
    LinearLayout layout;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        setTitle("History");
        layout = findViewById(R.id.history_to);


        SharedPreferences preferences = getSharedPreferences("", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> hash_Set = new HashSet<String>();
        editor.putStringSet("", hash_Set);

        RefreshHistory();


    }

    public void RefreshHistory() {
        db.collection("InterpolationCalculations" + mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                      @Override
                      public void onSuccess(QuerySnapshot documentSnapshots) {
                          if (documentSnapshots.isEmpty()) {
                              return;
                          } else {
//                              user.put("x_y_Values", xValues[i] + "," + yValues[i] + " " + (user.get("x_y_Values")==null?"":
//                                      user.get("x_y_Values")) + " ");
//                          }
//                          user.put("Using_Formula", dropDownSelection.getSelectedItem());
//                          user.put("At_Value", ((EditText) (findViewById(R.id.calculate_at))).getText().toString());
                              int i = 1;
                              for (DocumentChange documentChange : documentSnapshots.getDocumentChanges()) {
                                  String   using =  documentChange.getDocument().getData().get("Using_Formula").toString();
                                  String ans  =  documentChange.getDocument().getData().get("Answer").toString();
                                  String dated  =  documentChange.getDocument().getData().get("Dated").toString();
                                  LayoutInflater inflater = getLayoutInflater();
                                  View otherView = inflater.inflate(R.layout.xy_container, null);

                                  TextView textView1 = otherView.findViewById(R.id.history_answer);
                                  textView1.setText(ans);
                                  TextView textView2 = otherView.findViewById(R.id.history_date);
                                  textView2.setText(dated.replace("UTC+5", "")
                                  .replace("GMT+05:00", ""));
                                  TextView textView3 = otherView.findViewById(R.id.history_using);
                                  textView3.setText(using);
                                  ((TextView) otherView.findViewById(R.id.history_no)).setText("" + i);
                                  CardView linearLayout = otherView.findViewById(R.id.history_from);

                                  ((ViewGroup) linearLayout.getParent()).removeView(linearLayout);
                                  layout.addView(linearLayout);
                                  i++;


                              }
                          }
                      }
                  });


    }
}
