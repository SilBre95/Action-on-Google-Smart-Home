package com.example.googleassistantproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import com.example.googleassistantproject.databinding.BoolComponetBinding;
import com.example.googleassistantproject.databinding.DevicemgrBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.BaseObservable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeviceManager extends AppCompatActivity {

    private View myLayout;
    boolean dbState;
    List<View> active_component;
    Devices d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DevicemgrBinding binding = DataBindingUtil.setContentView(this, R.layout.devicemgr);
       //active_component  = new ArrayList<>();
        Bundle bound = getIntent().getExtras();
        Devices d = bound.getParcelable("device");
        String type = bound.getString("type");
        binding.setViewmodel(d);
        SetComponetVisibility(d.traits,type,d);
        this.d = d;
        LinearLayout custom = (LinearLayout) findViewById(R.id.custom);

    }

    private void SetComponetVisibility(ArrayList<String> traits, String devType, Devices d) {
        for (String s : traits) {
            switch (s) {
                case "action.devices.traits.OnOff": {
                    Switch onoff = findViewById(R.id.onOff);
                    onoff.setVisibility(View.VISIBLE);
                    String path = d.id +"/" + "OnOff/"+ "on";
                    CustomBoolView v = new CustomBoolView(d,path);
                    v.setListner(new CustomBoolView.MyListner() {
                        @Override
                        public void onBoolChange(boolean value) {
                            UpdateBoolView(onoff, value);
                        }
                    });
                    onoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            v.SetValueOnDb(isChecked);
                        }
                    });
                }
                break;
                case "action.devices.traits.Cook": {
                    findViewById(R.id.cookTemp).setVisibility(View.VISIBLE);
                    Switch stCook = findViewById(R.id.startCook);
                    stCook.setVisibility(View.VISIBLE);

                    ArrayList<String> tipi = new ArrayList<>();
                    if (devType.equals(DeviceTypes.TYPEOVEN.toString())) {
                        for (OvenCookMode m : OvenCookMode.values()) {
                            tipi.add(m.toString());
                        }
                    } else if (devType.equals(DeviceTypes.TYPECOFFEE_MAKER.toString())) {
                        for (CoffeeModes m : CoffeeModes.values()) {
                            tipi.add(m.toString());
                        }
                    } else if (devType.equals(DeviceTypes.TYPEBLENDER.toString())) {
                        for (BlenderCookMode m : BlenderCookMode.values()) {
                            tipi.add(m.toString());
                        }
                    } else if (devType.equals(DeviceTypes.TYPEMICROWAVE.toString())) {
                        for (MicrowaveCookMode m : MicrowaveCookMode.values()) {
                            tipi.add(m.toString());
                        }
                    }
                    ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item_typology, tipi);
                    final AutoCompleteTextView text = (AutoCompleteTextView) findViewById(R.id.cookMode);
                    text.setAdapter(adapter);

                    String path = d.id + "/" + "CookMode/" + "CookMode";
                    CustomStringView sv = new CustomStringView(d, path);
                    sv.setListner(new CustomStringView.MyListner() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onStringChange(String value) {
                            UpdateStringView(text, value);

                        }
                    });
                    text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object item = parent.getItemAtPosition(position);
                            sv.SetValueOnDb(item.toString());
                        }
                    });
                    String pathco = d.id +"/" + "CookStart/"+ "isCookStart";
                    CustomBoolView v = new CustomBoolView(d,pathco);
                    v.setListner(new CustomBoolView.MyListner() {
                        @Override
                        public void onBoolChange(boolean value) {
                            UpdateBoolView(stCook, value);
                        }
                    });
                    stCook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            v.SetValueOnDb(isChecked);
                        }
                    });
                }
                break;
                case "action.devices.traits.TemperatureControl": {
                    findViewById(R.id.tempCon).setVisibility(View.VISIBLE);
                    NumberPicker temp = findViewById(R.id.tempSet);
                    temp.setMaxValue(250);
                    String pathset = d.id +"/" + "Temperature/"+ "Temperature";
                    CustomNumberView n = new CustomNumberView(d,pathset);
                    n.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(temp, value.toString());
                        }
                    });

                    temp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Long l = new Long(newVal);
                            n.SetValueOnDb(l);
                        }
                    });
                }
                break;
                case "action.devices.traits.StartStop": {
                    Switch startStop = findViewById(R.id.startStop);
                    startStop.setVisibility(View.VISIBLE);
                    String path = d.id +"/" + "StartStop/"+ "isRunning";
                    CustomBoolView v = new CustomBoolView(d,path);
                    v.setListner(new CustomBoolView.MyListner() {
                        @Override
                        public void onBoolChange(boolean value) {
                            UpdateBoolView(startStop, value);

                        }
                    });
                    startStop.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            v.SetValueOnDb(isChecked);
                        }
                    });
                }
                break;
                case "action.devices.traits.Modes": {
                    findViewById(R.id.modes).setVisibility(View.VISIBLE);
                    List<String> setMode = new ArrayList<>();
                    setMode.add("small");
                    setMode.add("large");
                    setMode.add("medium");
                    ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item_typology, setMode);
                    final AutoCompleteTextView text = (AutoCompleteTextView) findViewById(R.id.modesText);
                    text.setAdapter(adapter);
                    String path = d.id +"/" + "Modes/"+ "load";
                    CustomStringView sv = new CustomStringView(d,path);
                    sv.setListner(new CustomStringView.MyListner() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onStringChange(String value) {
                            UpdateStringView(text, value);

                        }
                    });
                    text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object item = parent.getItemAtPosition(position);
                            sv.SetValueOnDb(item.toString());
                        }
                    });
                }
                break;
                case "action.devices.traits.Toggles": {
                    Switch toogle = findViewById(R.id.toogle);
                    toogle.setVisibility(View.VISIBLE);
                    String path = d.id +"/" + "Toggles/"+ "Turbo";
                    CustomBoolView v = new CustomBoolView(d,path);
                    v.setListner(new CustomBoolView.MyListner() {
                        @Override
                        public void onBoolChange(boolean value) {
                            UpdateBoolView(toogle, value);

                        }
                    });
                    toogle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            v.SetValueOnDb(isChecked);
                        }
                    });
                }
                break;
                case "action.devices.traits.FanSpeed": {
                    findViewById(R.id.fanmodes).setVisibility(View.VISIBLE);
                    Switch rev = findViewById(R.id.reverse);
                    rev.setVisibility(View.VISIBLE);
                    List<String> speeds = new ArrayList<>();
                    speeds.add("low");
                    speeds.add("high");
                    speeds.add("medium");
                    ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item_typology, speeds);
                    final AutoCompleteTextView text = (AutoCompleteTextView) findViewById(R.id.fanModesText);
                    text.setAdapter(adapter);
                   /* String path = d.id +"/" + "reversible/"+ "reversible";
                    CustomBoolView v = new CustomBoolView(d,path);
                    v.setListner(new CustomBoolView.MyListner() {
                        @Override
                        public void onBoolChange(boolean value) {
                            UpdateBoolView(rev, value);

                        }
                    });*/
                    String pathfan = d.id +"/" + "fanSpeed/"+ "fanSpeed";
                    CustomStringView sv = new CustomStringView(d,pathfan);
                    sv.setListner(new CustomStringView.MyListner() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onStringChange(String value) {
                            UpdateStringView(text, value);

                        }
                    });
                   /* rev.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            v.SetValueOnDb(isChecked);
                        }
                    });*/
                    text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object item = parent.getItemAtPosition(position);
                            sv.SetValueOnDb(item.toString());
                        }
                    });
                }
                break;
                case "action.devices.traits.HumiditySetting": {
                    findViewById(R.id.ambientHumidityRow).setVisibility(View.VISIBLE);
                    findViewById(R.id.humiditySetRow).setVisibility(View.VISIBLE);
                    TextView text = findViewById(R.id.ambientHumidity);
                    NumberPicker hum = findViewById(R.id.humiditySet);
                    hum.setMaxValue(100);
                    String pathfan = d.id +"/" + "humidityAmbientPercent/"+ "humidityAmbientPercent";
                    CustomNumberView n = new CustomNumberView(d,pathfan);
                    n.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(text, value.toString());
                        }
                    });
                    String pathset = d.id +"/" + "humiditySetpointPercent/"+ "humiditySetpointPercent";
                    CustomNumberView nh = new CustomNumberView(d,pathset);
                    nh.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(hum, value.toString());
                        }
                    });
                    hum.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Long l = new Long(newVal);
                            nh.SetValueOnDb(l);
                        }
                    });
                }
                break;
                case "action.devices.traits.TemperatureSetting": {
                    findViewById(R.id.termostmodes).setVisibility(View.VISIBLE);
                    ArrayList<String> tipi = new ArrayList<>();
                        for (ThermostatModes m : ThermostatModes.values()) {
                            tipi.add(m.toString());
                        }
                    ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item_typology, tipi);
                    final AutoCompleteTextView text = (AutoCompleteTextView) findViewById(R.id.termostText);
                    text.setAdapter(adapter);

                    findViewById(R.id.temSetRow).setVisibility(View.VISIBLE);

                    TextView ambientTemp =findViewById(R.id.ambientTemp);
                    NumberPicker temp = findViewById(R.id.TempSet);
                    temp.setMaxValue(100);
                    NumberPicker tempHigh = findViewById(R.id.TempSetHigh);
                    tempHigh.setMaxValue(100);
                    NumberPicker tempLow = findViewById(R.id.TempSetLow);
                    tempLow.setMaxValue(100);

                    String pathfan = d.id +"/" + "thermostatMode/"+ "thermostatMode";
                    CustomStringView sv = new CustomStringView(d,pathfan);
                    sv.setListner(new CustomStringView.MyListner() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onStringChange(String value) {
                            HandleTempoState(value);
                            UpdateStringView(text, value);
                        }
                    });
                    String patha = d.id +"/" + "thermostatTemperatureAmbient/"+ "thermostatTemperatureAmbient";
                    CustomNumberView rsv = new CustomNumberView(d,patha);
                    rsv.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(ambientTemp, value.toString());
                        }
                    });
                    String pathset = d.id +"/" + "thermostatTemperatureSetpoint/"+ "thermostatTemperatureSetpoint";
                    CustomNumberView n = new CustomNumberView(d,pathset);
                    n.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(temp, value.toString());
                        }
                    });

                    temp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Long l = new Long(newVal);
                            n.SetValueOnDb(l);
                        }
                    });

                    text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object item = parent.getItemAtPosition(position);
                            HandleTempoState(item.toString());
                            sv.SetValueOnDb(item.toString());
                        }
                    });
                    String pathsethigh = d.id +"/" + "thermostatTemperatureSetpointHigh/"+ "thermostatTemperatureSetpointHigh";
                    CustomNumberView nhig = new CustomNumberView(d,pathsethigh);
                    nhig.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(tempHigh, value.toString());
                        }
                    });

                    tempHigh.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Long l = new Long(newVal);
                            nhig.SetValueOnDb(l);
                        }
                    });
                    String pathsetLow = d.id +"/" + "thermostatTemperatureSetpointLow/"+ "thermostatTemperatureSetpointLow";
                    CustomNumberView nlow = new CustomNumberView(d,pathsetLow);
                    nlow.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(tempLow, value.toString());
                        }
                    });

                    tempLow.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Long l = new Long(newVal);
                            nlow.SetValueOnDb(l);
                        }
                    });
                }
                break;
                case "action.devices.traits.OpenClose": {
                    findViewById(R.id.openClRow).setVisibility(View.VISIBLE);
                    NumberPicker temp = findViewById(R.id.openClose);
                    temp.setMaxValue(100);
                    String pathfan = d.id +"/" + "OpenClose/"+ "OpenClose";
                    CustomNumberView n = new CustomNumberView(d,pathfan);
                    n.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(temp, value.toString());
                        }
                    });
                    temp.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Long l = new Long(newVal);
                            n.SetValueOnDb(l);
                        }
                    });
                }
                break;
                case "action.devices.traits.Fill": {
                    Switch fill = findViewById(R.id.fill);
                    fill.setVisibility(View.VISIBLE);
                    String path = d.id +"/" + "isFilled/"+ "isFilled";
                    CustomBoolView v = new CustomBoolView(d,path);
                    v.setListner(new CustomBoolView.MyListner() {
                        @Override
                        public void onBoolChange(boolean value) {
                            UpdateBoolView(fill, value);

                        }
                    });
                    fill.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            v.SetValueOnDb(isChecked);
                        }
                    });
                }
                break;
                case "action.devices.traits.Rotation": {
                    findViewById(R.id.rotationRow).setVisibility(View.VISIBLE);
                    NumberPicker rot = findViewById(R.id.rotazione);
                    rot.setMaxValue(360);
                    String pathfan = d.id +"/" + "rotationDegrees/"+ "rotationDegrees";
                    CustomNumberView n = new CustomNumberView(d,pathfan);
                    n.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(rot, value.toString());
                        }
                    });
                    rot.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Long l = new Long(newVal);
                            n.SetValueOnDb(l);
                        }
                    });
                }
                break;
                case "action.devices.traits.SensorState": {
                    findViewById(R.id.sensRow).setVisibility(View.VISIBLE);
                    TextView sen = findViewById(R.id.sensorNum);
                    String pathfan = d.id +"/" + "rawValue/"+ "rawValue";
                    CustomNumberView n = new CustomNumberView(d,pathfan);
                    n.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(sen, value.toString());
                        }
                    });
                }
                break;
                case "action.devices.traits.ColorSetting": {
                    findViewById(R.id.col).setVisibility(View.VISIBLE);
                    List<String> col = new ArrayList<>();
                    col.add("navy blue"); //128
                    col.add("gold"); //16766720
                    col.add("magenta"); //16711935
                    col.add("forest green"); //2263842
                    col.add("dark red"); //9109504
                    ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item_typology, col);
                    final AutoCompleteTextView text = (AutoCompleteTextView) findViewById(R.id.colorSet);
                    text.setAdapter(adapter);
                    String pathfan = d.id +"/" + "name/"+ "name";
                    CustomStringView sv = new CustomStringView(d,pathfan);
                    sv.setListner(new CustomStringView.MyListner() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onStringChange(String value) {
                            UpdateStringView(text, value);
                        }
                    });
                    text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object item = parent.getItemAtPosition(position);
                            String pathrgb = d.id +"/" + "spectrumRGB/"+ "spectrumRGB";
                            sv.SetValueOnDb(item.toString(),pathrgb);

                        }
                    });
                }
                break;
                case "action.devices.traits.Brightness": {
                    findViewById(R.id.brithRow).setVisibility(View.VISIBLE);
                    NumberPicker br = findViewById(R.id.brithset);
                    br.setMaxValue(100);
                    String pathfan = d.id +"/" + "brightness/"+ "brightness";
                    CustomNumberView n = new CustomNumberView(d,pathfan);
                    n.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(br, value.toString());
                        }
                    });
                    br.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                        @Override
                        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                            Long l = new Long(newVal);
                            n.SetValueOnDb(l);
                        }
                    });
                }
                break;
                case "action.devices.traits.AppSelector": {
                    findViewById(R.id.appsel).setVisibility(View.VISIBLE);
                    List<String> col = new ArrayList<>();
                    col.add("netflix");
                    col.add("youtube");
                    col.add("primevideo");
                    ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item_typology, col);
                    final AutoCompleteTextView text = (AutoCompleteTextView) findViewById(R.id.appSet);
                    text.setAdapter(adapter);
                    String pathfan = d.id +"/" + "currentApplication/"+ "currentApplication";
                    CustomStringView sv = new CustomStringView(d,pathfan);
                    sv.setListner(new CustomStringView.MyListner() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onStringChange(String value) {
                            UpdateStringView(text, value);
                        }
                    });
                    text.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object item = parent.getItemAtPosition(position);
                            sv.SetValueOnDb(item.toString());
                        }
                    });
                }
                break;
                case "action.devices.traits.Volume": {
                    findViewById(R.id.volRow).setVisibility(View.VISIBLE);
                    Switch mute = findViewById(R.id.mute);
                    mute.setVisibility(View.VISIBLE);

                    SeekBar seekBar = findViewById(R.id.seekBar);
                    String pathfan = d.id +"/" + "currentVolume/"+ "currentVolume";
                    CustomNumberView n = new CustomNumberView(d,pathfan);
                    n.setListner(new CustomNumberView.MyListner() {
                        @Override
                        public void onNumberChange(Long value) {
                            UpdateStringView(seekBar, value.toString());
                        }
                    });

                    String path = d.id +"/" + "isMuted/"+ "isMuted";
                    CustomBoolView v = new CustomBoolView(d,path);
                    v.setListner(new CustomBoolView.MyListner() {
                        @Override
                        public void onBoolChange(boolean value) {
                            UpdateBoolView(mute, value);

                        }
                    });
                    mute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            v.SetValueOnDb(isChecked);
                        }
                    });

                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            int i = seekBar.getProgress();
                            Long l = new Long(i);
                            n.SetValueOnDb(l);
                        }
                    });
                }
                break;
                case "action.devices.traits.LockUnlock": {
                    Switch lock = findViewById(R.id.lock);
                    lock.setVisibility(View.VISIBLE);
                    String path = d.id +"/" + "isLocked/"+ "isLocked";
                    CustomBoolView v = new CustomBoolView(d,path);
                    v.setListner(new CustomBoolView.MyListner() {
                        @Override
                        public void onBoolChange(boolean value) {
                            UpdateBoolView(lock, value);

                        }
                    });
                    lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            v.SetValueOnDb(isChecked);
                        }
                    });
                }
                break;
            }
        }
    }

    private void UpdateBoolView(Switch onoff, boolean value) {
        onoff.setChecked(value);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void UpdateStringView(AutoCompleteTextView combo, String value) {
        combo.setText(value,false);
    }

    private void UpdateStringView(TextView combo, String value) {
        combo.setText(value);
    }
    private void UpdateStringView(NumberPicker combo, String value) {
        combo.setValue(Integer.parseInt(value));
    }
    private void UpdateStringView(SeekBar combo, String value) {
        combo.setProgress(Integer.parseInt(value));
    }

    public void deleteClicked(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete)
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        CustomView c = new CustomView(d);
                        c.DeleteDev();
                        dialog.cancel();
                        finish();
                    }
                }
                );


        builder.create();
        builder.show();
    }

public void HandleTempoState(String mode){
        if(mode.equals(ThermostatModes.heatcool.toString())){
            findViewById(R.id.temDesRowHigh).setVisibility(View.VISIBLE);
            findViewById(R.id.temDesRowLow).setVisibility(View.VISIBLE);
            findViewById(R.id.temDesRow).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.temDesRow).setVisibility(View.VISIBLE);
            findViewById(R.id.temDesRowHigh).setVisibility(View.GONE);
            findViewById(R.id.temDesRowLow).setVisibility(View.GONE);
        }
}

}