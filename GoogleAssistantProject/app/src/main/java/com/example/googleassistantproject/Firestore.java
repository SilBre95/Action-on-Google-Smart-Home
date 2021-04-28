package com.example.googleassistantproject;

import android.bluetooth.BluetoothClass;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Firestore {
    private static final String TAG = "Firestore";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4,
            60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private final FirebaseFirestore db;
    private static Firestore instance = null;

    public Firestore(FirebaseFirestore db) {
        this.db = db;
    }




    public void getDocument(DeviceArrayAdapter adapter) {
        Utils utils = Utils.getInstance();
        db.collection("devices").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Devices> dev = new ArrayList<>();;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                       // if(utils.devicesList.co)
                       // Devices d = document.toObject(Devices.class);
                        String name =(String) document.get("name");
                        String type =(String) document.get("type");
                        String model = (String) document.get("model");
                        String deviceManufact = (String) document.get("deviceManufact");
                        DeviceTypes ty = DeviceTypes.valueOf(type);
                        Devices toAdd = null;
                        try {
                            toAdd = new Devices(name,ty,deviceManufact,model);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dev.add(toAdd);
                        //utils.SetDevice(d);
                    }
                    adapter.addAll(dev);

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }

        });
    }

    public void removeDoc(Devices d){
        db.collection("devices").document(d.id).delete();
    }


}
