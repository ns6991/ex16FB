package com.example.ex16fb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class newOrder extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Intent si;
    FBref hlp;
    Spinner sp;
    EditText first_mealET ,main_mealET, extraET, dessertET, drinkET, worker_numberET;
    ArrayAdapter<String> adp;
    ArrayList<String> tbl;
    ArrayList<String> restIDlist, restNameList;
    int ind1, ind2;
    int i,flag;
    ValueEventListener usrListener;
    ArrayList<String> list_id;
    ArrayList<Worker1> usrList = new ArrayList<Worker1>();


    String  orderName, resID ,resName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        first_mealET = (EditText) findViewById(R.id.fm);
        main_mealET = (EditText) findViewById(R.id.mm);
        extraET = (EditText) findViewById(R.id.em);
        dessertET = (EditText) findViewById(R.id.dm);
        drinkET = (EditText) findViewById(R.id.dr);
        worker_numberET = (EditText) findViewById(R.id.wid);

        sp = (Spinner) findViewById(R.id.options);
        sp.setOnItemSelectedListener(this);


        restIDlist = new ArrayList<>();
        restIDlist.add("0");

        restNameList = new ArrayList<>();
        list_id = new ArrayList<>();
        restNameList.add("0");
        tbl = new ArrayList<>();
        tbl.add("Choose Restaurant:");

        FBref.refRestaurant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                for (DataSnapshot data : dS.getChildren()) {
                    String str1 = (String) data.getKey();
                    Restaurant1 cmpTmp = data.getValue(Restaurant1.class);
                    if (cmpTmp.getACTIVE().equals("1")) {
                        tbl.add(cmpTmp.getNAME());
                        restIDlist.add(str1);
                        restNameList.add(cmpTmp.getNAME());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        adp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, tbl);
        sp.setAdapter(adp);

        FBref.refWorkers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                for (DataSnapshot data : dS.getChildren()) {
                    Worker1 cmpTmp = data.getValue(Worker1.class);
                    if (cmpTmp.getACTIVE().equals("1")) {
                        System.out.println(cmpTmp.getFNAME());
                        list_id.add(cmpTmp.getID());
                        usrList.add(cmpTmp);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        i=0;
        FBref.refMeals.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                for (DataSnapshot data : dS.getChildren()) {
                    i++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    public void submitOrder(View view) {
        if(checkValid()  && checkValidRes()){
            Meal1 m = new Meal1(first_mealET.getText().toString(),main_mealET.getText().toString(),extraET.getText().toString(),dessertET.getText().toString(),drinkET.getText().toString());
            FBref.refMeals.child(i+"").setValue(m);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm");
            for(Worker1 i : usrList){
                if(i.getID().equals(worker_numberET.getText().toString())) orderName = i.getFNAME();
            }
            Order1 o = new Order1(resID,resName,worker_numberET.getText().toString(),orderName,formatter.format(calendar.getTime()));
            FBref.refOrders.child(i + "").setValue(o);

            Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_LONG).show();
            si =new Intent(this,PreviousOrders.class);
            startActivity(si);
        }
    }

    public void cancelBK(View view) {
        si =new Intent(this,PreviousOrders.class);
        startActivity(si);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position==0){
            resID =null;
            resName = null;
        }else{
            resID = restIDlist.get(position);
            resName = restNameList.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private boolean checkValid(){
        int no =0;
        if(resID==null){
            Toast.makeText(this, "you have to choose restaurant", Toast.LENGTH_LONG).show();
            return false;
        }
        if (first_mealET.getText().length() == 0) {
            first_mealET.setText("NO");
            no++;
        }
        if (main_mealET.getText().length() == 0) {
            main_mealET.setText("NO");
            no++;
        }
        if (extraET.getText().length() == 0) {
            extraET.setText("NO");
            no++;
        }
        if (dessertET.getText().length() == 0) {
            dessertET.setText("NO");
            no++;
        }
        if (drinkET.getText().length() == 0) {
            drinkET.setText("NO");
            no++;
        }
        if (worker_numberET.getText().length() == 0) {
            worker_numberET.setError("Can't Be Empty.");
            return false;
        }
        if(no==5 ){
            Toast.makeText(this, "you have to order at least one thing..", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean checkValidRes() {

        System.out.println(list_id.size()+"==============================");

        return list_id.contains(worker_numberET.getText().toString());

    }




}