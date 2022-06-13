package com.example.ex16fb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class newRestaurant extends AppCompatActivity {

    TextView nameET, mPhoneET, sPhoneET , tax;
    Intent si;
    ArrayList<String> list_id = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_restaurant);

        nameET = (TextView) findViewById(R.id.nameR);
        mPhoneET = (TextView) findViewById(R.id.fp);
        sPhoneET = (TextView) findViewById(R.id.sp);
        tax = (TextView) findViewById(R.id.taxNum);
    }

    public void cancelBK(View view) {
        si =new Intent(this,Restaurant.class);
        startActivity(si);
    }

    public void newResOC(View view) {
        if (checkValid()) {
            Restaurant1 res = new Restaurant1(nameET.getText().toString(),mPhoneET.getText().toString(),sPhoneET.getText().toString(), tax.getText().toString(),"1");
            FBref.refRestaurant.child(tax.getText().toString()).setValue(res);

            Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_LONG).show();
            si =new Intent(this,Restaurant.class);
            startActivity(si);
        }
    }

    private boolean checkValid(){
        int no =0;
        if (isExist()) {
            tax.setError("Already Exists!");
        } else if(tax.getText().toString().length()!=9){
            tax.setError("Invalid!");
        }
        if (nameET.getText().length() == 0) {
            nameET.setError("Can't Be Empty");
            no++;
        }
        if (!(mPhoneET.getText().length() == 10)  || mPhoneET.getText().toString().charAt(0) != '0' || !(mPhoneET.getText().toString().matches("[0-9]+") && mPhoneET.getText().toString().length() > 2)) {

            mPhoneET.setError("Must Be 10 Digits Long");
            no++;
        }
        if (sPhoneET.getText().toString() != "" ) {
            if((sPhoneET.getText().length() <9 || sPhoneET.getText().toString().charAt(0)!='0'|| !(mPhoneET.getText().toString().matches("[0-9]+") && mPhoneET.getText().toString().length() > 2))){
                sPhoneET.setError("Must Be 10 or 9 Digits Long");
                no++;
            }

        }

        if(no!=0){
            Toast.makeText(this, "Error, please enter again..", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isExist() {
        list_id.clear();
        FBref.refRestaurant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                for (DataSnapshot data : dS.getChildren()) {
                    list_id.add((String) data.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return list_id.contains(tax.getText().toString());
    }
}