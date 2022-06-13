package com.example.ex16fb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class newWorker extends AppCompatActivity {

    ArrayList<String> list_id = new ArrayList<>();
    Intent si;
    EditText idET, fnameET, lnameET, companyET, phoneET;
    String id_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_worker);

        idET = (EditText) findViewById(R.id._wid);
        fnameET = (EditText) findViewById(R.id.fn);
        lnameET = (EditText) findViewById(R.id.ln);
        companyET = (EditText) findViewById(R.id.comp);
        phoneET = (EditText) findViewById(R.id.pn);

    }

    public void cencelbk(View view) {
        si =new Intent(this,WorkersList.class);
        startActivity(si);
    }

    public void newWorOC(View view) {
        if (checkValid()) {
            Worker1 worker = new Worker1(lnameET.getText().toString(),fnameET.getText().toString(),companyET.getText().toString(), idET.getText().toString(),phoneET.getText().toString(),"1");
            FBref.refWorkers.child(idET.getText().toString()).setValue(worker);

            Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_LONG).show();
            si =new Intent(this,WorkersList.class);
            startActivity(si);
        }
    }

    private boolean checkValid(){
        int no =0;
        if(isExist(idET.getText().toString())){
            Toast.makeText(this, "User already exist", Toast.LENGTH_LONG).show();
            no++;
        }
        if (!isValidId(idET.getText().toString())) {
            idET.setError("enter valid id");
            no++;
        }
        if (!(phoneET.getText().length() == 10) || phoneET.getText().toString().charAt(0)!='0' || !(phoneET.getText().toString().matches("[0-9]+") && phoneET.getText().toString().length() > 2)) {
            phoneET.setError("Must Be 10 Digits Long");
            no++;
        }
        if (fnameET.getText().length() == 0) {
            fnameET.setError("can't be empty");
            no++;
        }
        if (lnameET.getText().length() == 0) {
            lnameET.setError("can't be empty");
            no++;
        }
        if (companyET.getText().length() == 0) {
            companyET.setError("can't be empty");
            no++;
        }


        if(no!=0){
            Toast.makeText(this, "Error, please enter again..", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isExist(String str){
        list_id.clear();
        FBref.refWorkers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dS) {
                for (DataSnapshot data : dS.getChildren()) {
                    list_id.add((String) data.getKey());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        return list_id.contains(idET.getText().toString());

    }

    private static boolean isValidId(String str){
        if (str.length() > 9) return false;
        int x;
        int sum = 0;
        int len = 9 - str.length();
        for (int i = 0; i < len; i++) {
            str = "0" + str;
        }
        for (int i = 0; i < str.length(); i++) {
            try {
                x = Integer.parseInt(str.substring(i, i + 1));
            } catch (Exception e) {
                return false;
            }
            if (i % 2 == 1) x = x * 2;
            if (x > 9) x = x % 10 + x / 10;
            sum += x;
        }
        return sum % 10 == 0;
    }

}