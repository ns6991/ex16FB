package com.example.ex16fb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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


public class WorkersList extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemSelectedListener {
    Intent si;
    Spinner spin;
    ListView lv;
    ValueEventListener usrListener;

    EditText fn, ln, phN, comp;
    TextView idW;
    Switch active, filter;
    Button update;
    ArrayList<String> tbl;
    ArrayList<String> cardIDs;
    ArrayAdapter<String> adp;

    ArrayList<String> usrList = new ArrayList<String>();
    ArrayList<Worker1> usrValues = new ArrayList<Worker1>();

    final String[] sortAD = {"Card-ID", "First Name A→Z", "Last Name A→Z", "Company A→Z"};
    String sort = "id", filterPar = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workers_list);

        fn = (EditText) findViewById(R.id.fnameW);
        ln = (EditText) findViewById(R.id.snameW);
        idW = (TextView) findViewById(R.id.idW);
        phN = (EditText) findViewById(R.id.phoneW);
        comp = (EditText) findViewById(R.id.compW);

        update = (Button) findViewById(R.id.updateWo);
        active = (Switch) findViewById(R.id.switch1);
        filter = (Switch) findViewById(R.id.filter);


        lv = (ListView) findViewById(R.id.wl);
        lv.setOnItemClickListener(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        update_users(sort);

        spin = (Spinner) findViewById(R.id.options2);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, sortAD);
        spin.setAdapter(adp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        usrList.clear();
        usrValues.clear();
        update_users(sort);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (usrListener != null) {
            FBref.refWorkers.removeEventListener(usrListener);
        }
    }

    public void newWorkerOC(View view) {
        si = new Intent(this, newWorker.class);
        startActivity(si);
    }

    public void updateButton(View view) {
        if (update.getText().toString().equals("UPDATE")) {
            update.setText("SAVE");
            Toast.makeText(this, "please change the info above as you want", Toast.LENGTH_LONG).show();
            fn.setEnabled(true);
            ln.setEnabled(true);
            idW.setEnabled(false);
            phN.setEnabled(true);
            comp.setEnabled(true);
            active.setEnabled(true);
            fn.setHint("first name");
            ln.setHint("last name");
            phN.setHint("phone number");
            comp.setHint("company");

        } else if (update.getText().toString().equals("SAVE")) {
            if(checkValid()){
                String st = "";
                if(active.isChecked()) st = "1"; else st = "0";
                Worker1 worker = new Worker1(ln.getText().toString(),fn.getText().toString(),comp.getText().toString(), idW.getText().toString(),phN.getText().toString(),st);
                FBref.refWorkers.child(idW.getText().toString()).setValue(worker);
                Toast.makeText(this, "Saved Successfully!", Toast.LENGTH_LONG).show();
                update.setText("UPDATE");
                fn.setEnabled(false);
                ln.setEnabled(false);
                phN.setEnabled(false);
                comp.setEnabled(false);
                active.setEnabled(false);
            }



        }
    }
    private boolean checkValid(){
        int no =0;
        if (!(phN.getText().length() == 10) || phN.getText().toString().charAt(0)!='0' || !(phN.getText().toString().matches("[0-9]+") && phN.getText().toString().length() > 2)) {
            phN.setError("Must Be 10 Digits Long and start with 0");
            no++;
        }
        if (fn.getText().length() == 0) {
            fn.setError("can't be empty");
            no++;
        }
        if (ln.getText().length() == 0) {
            ln.setError("can't be empty");
            no++;
        }
        if (comp.getText().length() == 0) {
            comp.setError("can't be empty");
            no++;
        }


        if(no!=0){
            Toast.makeText(this, "Error, please enter again..", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void isClicked1(View view) {
        if (filter.isChecked()) {
            filterPar ="1";
        }
        else
            filterPar = "0";
        update_users(sort);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        fn.setEnabled(false);
        ln.setEnabled(false);
        idW.setEnabled(false);
        phN.setEnabled(false);
        comp.setEnabled(false);
        active.setEnabled(false);

        Worker1 user = (Worker1) usrValues.get(position);

        fn.setText(user.getFNAME());
        ln.setText(user.getLNAME());
        comp.setText(user.getCOMPANY());
        idW.setText(user.getID());
        phN.setText(user.getPHONE());
        active.setChecked(user.getACTIVE().equals("1"));

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (pos == 0) {
            sort = "id";
        } else if (pos == 1) {
            sort = "fname";
        } else if (pos == 2) {
            sort = "lname";
        } else if (pos == 3) {
            sort = "company";
        }
        update_users( sort);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void update_users(String sortPar) {
        fn.setEnabled(false);
        ln.setEnabled(false);
        phN.setEnabled(false);
        comp.setEnabled(false);
        active.setEnabled(false);

        Query query = FBref.refWorkers.orderByChild(sortPar);
        usrListener = new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usrList.clear();
                usrValues.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String str1 = (String) data.getKey();
                    Worker1 usrTmp = data.getValue(Worker1.class);
                    if (filterPar == null || usrTmp.getACTIVE().equals(filterPar)) {
                        usrValues.add(usrTmp);
                        usrList.add(usrTmp.getFNAME() + " " + usrTmp.getLNAME() + " (id: " + str1 + ")");
                    }
                }
                adp = new ArrayAdapter<String>(WorkersList.this, R.layout.support_simple_spinner_dropdown_item, usrList);
                lv.setAdapter(adp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        query.addValueEventListener(usrListener);

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