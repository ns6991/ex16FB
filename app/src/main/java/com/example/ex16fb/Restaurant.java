package com.example.ex16fb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Restaurant extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemSelectedListener{

    Intent si;
    Spinner spin;
    ListView lv;

    TextView tax;
    EditText name, ph1 , ph2 ;
    Button update;
    Switch active, filter;

    ValueEventListener cmpListener;
    ArrayList<String> tbl;
    ArrayList<String> cardIDs;
    ArrayAdapter<String> adp;
    ArrayList<String> cmpList = new ArrayList<String>();
    ArrayList<Restaurant1> cmpValues = new ArrayList<Restaurant1>();

    String userKeyId = "";
    final String[] sortAD = {"Card-ID", "Name A→Z"};
    String sort = "taxid", filterPar = "1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        name = (EditText) findViewById(R.id.nR);
        ph1 = (EditText) findViewById(R.id.p1);
        ph2 = (EditText) findViewById(R.id.p2);
        tax = (TextView) findViewById(R.id.tax);

        update = (Button) findViewById(R.id.updateRes);
        active = (Switch) findViewById(R.id.active);
        filter = (Switch) findViewById(R.id.filter1);

        lv = (ListView) findViewById(R.id.rl);
        lv.setOnItemClickListener(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        update_comps( sort);

        spin = (Spinner) findViewById(R.id.option1);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, sortAD);
        spin.setAdapter(adp);
    }

    public void addRes(View view) {
        si =new Intent(this,newRestaurant.class);
        startActivity(si);
    }
    protected void onResume() {
        super.onResume();
        update_comps( sort);
    }


    public void updateButton(View view) {
        if (update.getText().toString().equals("UPDATE")) {
            update.setText("SAVE");
            Toast.makeText(this, "please change the info above as you want", Toast.LENGTH_LONG).show();
            tax.setEnabled(false);
            name.setEnabled(true);
            name.setHint("name");
            ph1.setEnabled(true);
            ph1.setHint("first phone number");
            ph2.setEnabled(true);
            ph2.setHint("second phone number");
            active.setEnabled(true);

        } else if (update.getText().toString().equals("SAVE")) {
            if(checkValid()){
                String st = "";
                if(active.isChecked()) st = "1"; else st = "0";
                Restaurant1 res = new Restaurant1(name.getText().toString(),ph1.getText().toString(),ph2.getText().toString(), tax.getText().toString(),st);
                FBref.refRestaurant.child(tax.getText().toString()).setValue(res);

                Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_LONG).show();
                update.setText("UPDATE");
                name.setEnabled(false);
                ph1.setEnabled(false);
                ph2.setEnabled(false);
                active.setEnabled(false);
            }



        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        name.setEnabled(false);
        ph1.setEnabled(false);
        ph2.setEnabled(false);
        tax.setEnabled(false);
        active.setEnabled(false);

        Restaurant1 res = (Restaurant1) cmpValues.get(position);

        name.setText(res.getNAME());
        ph1.setText(res.getMAIN_PHONE());
        ph2.setText(res.getSECONDARY_PHONE());
        tax.setText(res.getTAX_ID());
        active.setChecked(res.getACTIVE().equals("1"));
    }

    public void isClicked1(View view) {
        if (filter.isChecked()) {
            filterPar ="1";
        }
        else
            filterPar = "0";
        update_comps(sort);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (pos == 0) {
            sort =  "tax_ID";
        } else if (pos == 1) {
            sort = "name";
        }
        update_comps(sort);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void update_comps(String sortp) {


        name.setEnabled(false);
        ph1.setEnabled(false);
        ph2.setEnabled(false);
        tax.setEnabled(false);
        active.setEnabled(false);

        Query query = FBref.refRestaurant.orderByChild(sortp);
        cmpListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dS) {
                cmpList.clear();
                cmpValues.clear();
                for (DataSnapshot data : dS.getChildren()) {
                    String str1 = (String) data.getKey();
                    Restaurant1 cmpTmp = data.getValue(Restaurant1.class);
                    if (filterPar == null || cmpTmp.getACTIVE().equals(filterPar)){
                        cmpValues.add(cmpTmp);
                        cmpList.add(cmpTmp.getNAME() + " (id: " + str1 + ")");
                    }
                }
                adp = new ArrayAdapter<String>(Restaurant.this, R.layout.support_simple_spinner_dropdown_item, cmpList);
                lv.setAdapter(adp);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        query.addValueEventListener(cmpListener);
    }


    private boolean checkValid(){
        int no =0;
        if (name.getText().length() == 0 ) {
            name.setError("Can't Be Empty");
            no++;
        }

        if (!(ph1.getText().length() == 10) || ph1.getText().toString().charAt(0)!='0' || !(ph1.getText().toString().matches("[0-9]+") && ph1.getText().toString().length() > 2)) {
            ph1.setError("Must Be 10 Digits Long and start with 0");

            no++;
        }
        String s = ph2.getText().toString();
        if (!ph2.getText().toString().isEmpty()) {
            if((ph2.getText().length() <9)|| ph1.getText().toString().charAt(0)!='0' || !(ph2.getText().toString().matches("[0-9]+") && ph2.getText().length() > 2)){
                ph2.setError("Must Be 9 or Digits Long");
                no++;
            }

        }

        if(no!=0){
            Toast.makeText(this, "Error, please enter again..", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.workersList) {
            si = new Intent(this, WorkersList.class);
            startActivity(si);
        } else if (item.getItemId() == R.id.credits) {
            si = new Intent(this, Credits.class);
            startActivity(si);
        } else if (item.getItemId() == R.id.restaurants) {
            si = new Intent(this, Restaurant.class);
            startActivity(si);
        }
        if (item.getItemId() == R.id.prevOrder) {
            si = new Intent(this, PreviousOrders.class);
            startActivity(si);
        }
        if (item.getItemId() == R.id.home) {
            si = new Intent(this, MainActivity.class);
            startActivity(si);
        }
        return true;
    }
}