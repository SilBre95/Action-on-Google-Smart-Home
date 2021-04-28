package com.example.googleassistantproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ActivityManager;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.plus.Plus;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private DeviceArrayAdapter deviceArrayAdapter;
    public FirebaseFirestore db;
    public ArrayList<Devices> devices;
    public Firestore toDb;
    public Utils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final RequestQueue queue = Volley.newRequestQueue(this);

        final ListView listview = (ListView) findViewById(R.id.connectedDevicesList);
        devices = new ArrayList<Devices>();
        deviceArrayAdapter = new DeviceArrayAdapter(this,devices);
        listview.setAdapter(deviceArrayAdapter);
        db = FirebaseFirestore.getInstance();

        toDb = new Firestore(db);

        queue.start();
        utils = Utils.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              NewDevicePopup d = new NewDevicePopup();
              d.show(getSupportFragmentManager(),"NewDevicePopup");
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View component, int pos, long id){
                // qui dentro stabilisco cosa fare dopo il click
                final Devices device = (Devices) adapterView.getItemAtPosition(pos);
                StartDeviceMgr(device);

            }
        });


    }

    public void StartDeviceMgr(Devices dev){
        Intent intent = new Intent(this, DeviceManager.class);
        intent.putExtra("device",dev);
        intent.putExtra("type",dev.enumtype.toString());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            UpdateDev();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void UpdateDev() throws InterruptedException {
        Thread.sleep(1500);
        deviceArrayAdapter.clear();
        toDb.getDocument(deviceArrayAdapter);
    }



}