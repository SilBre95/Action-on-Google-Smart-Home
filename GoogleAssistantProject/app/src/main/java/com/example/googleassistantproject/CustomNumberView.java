package com.example.googleassistantproject;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomNumberView extends BaseObservable {
    private String description;
    private boolean switchValue;
    private Devices device;
    private MyListner listner;
    DatabaseReference myRef;

    public interface MyListner
    {
        public void onNumberChange(Long value);
    }

    public CustomNumberView(Devices devices, String path){
        this.listner = null;
        this.device = devices;
        String baseurl = "https://test-758f8-default-rtdb.firebaseio.com/";
        String url =baseurl.concat(path);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReferenceFromUrl(url);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Long dbvalue = dataSnapshot.getValue(Long.class);
                if(dbvalue != null) {
                    if (listner != null)
                        listner.onNumberChange(dbvalue);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public Devices getDevice() {
        return device;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSwitchValue() {
        return switchValue;
    }

    public void setDevice(Devices device) {
        this.device = device;
    }

    public void setSwitchValue(boolean switchValue) {
        this.switchValue = switchValue;
    }

    public void setListner(MyListner listner) {
        this.listner = listner;
    }

    public void SetValueOnDb(Long value){
        myRef.setValue(value);
    }

}
