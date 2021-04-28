package com.example.googleassistantproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AddNewDevice extends AppCompatActivity {
    public EditText edit_name;
    public EditText edit_type;
    public EditText edit_man;
    public EditText edit_mod;
    public RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);
        edit_name   = (EditText)findViewById(R.id.name);
        edit_type   = (EditText)findViewById(R.id.type);
        edit_man   = (EditText)findViewById(R.id.factory);
        edit_mod   = (EditText)findViewById(R.id.model);
        DeviceTypes[] types = DeviceTypes.values();
        ArrayList<String> tipi = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        queue.start();
        for(int i=0;i<types.length;i++){
            tipi.add(types[i].toString().replace("TYPE",""));
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item_typology, tipi);
        final AutoCompleteTextView text = (AutoCompleteTextView) findViewById(R.id.type);
        text.setAdapter(adapter);

    }

    //Viene invocato al click su annulla
    public void annullaClicked(View view) {
        this.finish();
    }

    //Viene invocato al click su salva
    public void salvaClicked(View view) throws JSONException {
        String name = edit_name.getText().toString();
        String type = edit_type.getText().toString();
        String fac = edit_man.getText().toString();
        String mod = edit_mod.getText().toString();

        if(!name.isEmpty() && !type.isEmpty()&& !fac.isEmpty() && !mod.isEmpty()){
            String typeToSend = "TYPE"+type;
            DeviceTypes t = DeviceTypes.valueOf(typeToSend);
            Devices deviceToAdd = new Devices(name,t,fac, mod);
            JsonObjectRequest req = Utils.getInstance().AddDeviceToList(deviceToAdd);
            queue.add(req);
            //Intent intent = new Intent();
            //setResult(RESULT_OK, intent);
            this.finish();
        }else
            {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.datiObb)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            builder.create();
            builder.show();
        }
    }


}