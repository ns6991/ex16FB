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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.ex16fb.FBref.refMeals;
import static com.example.ex16fb.FBref.refOrders;

public class PreviousOrders extends AppCompatActivity implements  AdapterView.OnItemClickListener,AdapterView.OnItemSelectedListener{
    Intent si;
    Spinner spin;
    ListView lv;

    Order1 order;
    Meal1 meal;
    TextView wkrN, resN, fm,mm,em,dr,dess,date,time;

    ValueEventListener ordrListener;
    ValueEventListener mlListener;

    ArrayList<String> tbl;
    ArrayList<String> cardIDs;
    ArrayAdapter<String> adp;
    ArrayList<String> ordrList = new ArrayList<String>();
    ArrayList<Order1> ordrValues = new ArrayList<Order1>();
    ArrayList<String> ordrPositions = new ArrayList<String>();

    String userKeyId = "";
    final String[] sortAD = {"Name A→Z", "Restaurant A→Z"};
    String sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_orders);

        lv = (ListView) findViewById(R.id.ol);
        wkrN = (TextView)findViewById(R.id.wrkO);
        resN = (TextView)findViewById(R.id.resO);
        fm = (TextView)findViewById(R.id.fmO);
        mm = (TextView)findViewById(R.id.mmO);
        em= (TextView)findViewById(R.id.exmO);
        dr = (TextView)findViewById(R.id.dO);
        dess = (TextView)findViewById(R.id.dsmO);
        date = (TextView)findViewById(R.id.dateO);
        time = (TextView)findViewById(R.id.time);

        lv = (ListView) findViewById(R.id.ol);
        lv.setOnItemClickListener(this);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        update_orders(sort);

        spin = (Spinner) findViewById(R.id.options);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, sortAD);
        spin.setAdapter(adp);
    }

    public void newOrder(View view) {
        si =new Intent(this,newOrder.class);
        startActivity(si);
    }

    public void load(){
        fm.setText("First Meal: \n" + meal.getAPPETIZER());
        mm.setText("Main Meal: \n" + meal.getMAIN());
        em.setText("Extra Meal: \n" + meal.getSIDE());
        dess.setText("Dessert: \n" + meal.getDESSERT());
        dr.setText("Drinks: \n" + meal.getDRINK());



        wkrN.setText("Worker: \n" + order.getUSER_NAME());
        resN.setText("Restaurant: \n" + order.getRESTAURANT_NAME());
        date.setText(order.getDATE());
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        refMeals.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("==================================================================");
                for (DataSnapshot data : snapshot.getChildren()) {
                    if (ordrPositions.get(position).equals((String) data.getKey())) {
                        order = ordrValues.get(position);
                        meal = data.getValue(Meal1.class);
                    }
                }
                load();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (pos == 0) {
            sort = null;
        } else if (pos == 1) {
            sort = "user_NAME";
        } else if (pos == 2) {
            sort = "restaurant_NAME";
        }
        update_orders(sort);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void update_orders(String sortPar) {
        Query query;
        if (sortPar == null) {
            query = refOrders.orderByKey();
        } else {
            query = refOrders.orderByChild(sortPar);
        }

        ordrListener = new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ordrList.clear();
                ordrValues.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String str1 = (String) data.getKey();
                    ordrPositions.add(str1);
                    Order1 ordrTmp = data.getValue(Order1.class);
                    ordrValues.add(ordrTmp);
                    ordrList.add(ordrTmp.getUSER_NAME() + ", " + ordrTmp.getRESTAURANT_NAME() + " (At: " + ordrTmp.getDATE() + ")");

                }
                if (sortPar == null) {
                    Collections.reverse(ordrList);
                }
                adp = new ArrayAdapter<String>(PreviousOrders.this, R.layout.support_simple_spinner_dropdown_item, ordrList);
                lv.setAdapter(adp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        };
        query.addValueEventListener(ordrListener);

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