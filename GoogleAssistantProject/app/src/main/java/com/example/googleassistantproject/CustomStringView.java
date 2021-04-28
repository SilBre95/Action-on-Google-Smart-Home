package com.example.googleassistantproject;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomStringView extends BaseObservable {
    private String description;
    private boolean switchValue;
    private Devices device;
    private MyListner listner;
    private DatabaseReference myRef;
    FirebaseDatabase database;
    String baseurl;

    public interface MyListner
    {
        public void onStringChange(String value);
    }

    public CustomStringView(Devices devices, String path){
        this.listner = null;
        this.device = devices;
        baseurl = "https://test-758f8-default-rtdb.firebaseio.com/";
        String url =baseurl.concat(path);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReferenceFromUrl(url);
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String dbvalue = dataSnapshot.getValue(String.class);
                if(dbvalue != null) {
                    if (listner != null)
                        listner.onStringChange(dbvalue);
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

    public void SetValueOnDb(String value){
        myRef.setValue(value);
    }

    public void SetValueOnDb(String value, String path){
        myRef.setValue(value);
        String p= baseurl.concat(path);
        DatabaseReference rgb = database.getReferenceFromUrl(p);
        switch (value){
            case "navy blue":{
                rgb.setValue(128);
            }
            break;
            case "gold":{
                rgb.setValue(16766720);
            }
            break;
            case "magenta":{
                rgb.setValue(16711935);
            }
            break;
            case "forest green":{
                rgb.setValue(2263842);
            }
            break;
            case "dark red":{
                rgb.setValue(9109504);
            }
            break;
        }
    }
}
