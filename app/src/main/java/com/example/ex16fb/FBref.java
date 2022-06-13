package com.example.ex16fb;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class FBref {
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://fbex16-3133e-default-rtdb.europe-west1.firebasedatabase.app/");
    public static DatabaseReference refWorkers=FBDB.getReference("Worker1");
    public static DatabaseReference refRestaurant=FBDB.getReference("Restaurant1");
    public static DatabaseReference refOrders=FBDB.getReference("Order1");
    public static DatabaseReference refMeals=FBDB.getReference("Meal1");
}
