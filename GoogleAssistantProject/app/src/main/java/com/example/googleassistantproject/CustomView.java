package com.example.googleassistantproject;

import androidx.databinding.BaseObservable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class CustomView extends BaseObservable {
    private DatabaseReference myRef;
    private Devices d;

    public  CustomView(Devices d) {
        String baseurl = "https://test-758f8-default-rtdb.firebaseio.com/";
        String url = baseurl.concat(d.id);
        this.d = d;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReferenceFromUrl(url);
    }

    public void DeleteDev(){
        myRef.removeValue();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Firestore toDb = new Firestore(db);
        toDb.removeDoc(d);
    }
}
