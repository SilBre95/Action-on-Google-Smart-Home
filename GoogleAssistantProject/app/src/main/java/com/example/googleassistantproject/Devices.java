package com.example.googleassistantproject;

import android.animation.FloatArrayEvaluator;
import android.os.Parcel;
import android.os.Parcelable;
import android.se.omapi.SEService;
import android.view.View;

import androidx.constraintlayout.solver.state.Dimension;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Devices extends BaseObservable implements  Parcelable  {

    public  String name;
    public  DeviceTypes enumtype;
    public  String deviceManufact;
    public  String model;
    public  String typeToSend;
    public  int devImg;
    public String dataType;
    public String id;
    public String swVersion;
    public ArrayList<String> traits;
    public String type;
    public HashMap<String,JSONArray> map;
    public String lang = "en";
    public JSONObject avaibleModes;
    public JSONArray allModes;
    public List<Map<String, Object>>  av;
    JSONObject attributes;
    List<String> traitsSetted;
    public int visOn;


    public Devices() {
    }

    public Devices(String name, DeviceTypes type, String deviceManufact, String model) throws JSONException {
        this.name = name;
        this.enumtype = type;
        this.model = model;
        this.deviceManufact = deviceManufact;
        GetDevicesTraits(type);
        ArrayList<String> toSet = new ArrayList<>();
        if(traitsSetted!=null) {
            for (String s : traitsSetted) {
                toSet.add(s);
            }
        }
        this.traits = toSet;
        this.id = type.toString() + "-" + name;
        //GetDevicesTraits(type);

    }
    public Devices(String dataType, String deviceManufact, String hwVersion, String id, String model, String name, String swVersion, ArrayList<String> traits, String type, List<Map<String, Object>> av){
        this.name = name;
        this.type = type;
        this.model = model;
        this.deviceManufact = deviceManufact;
        this.dataType = dataType;
        this.id = id;
        this.model = model;
        this.swVersion = swVersion;
        this.traits = traits;
        this.av = av;

    }


    protected Devices(Parcel in) {
        name = in.readString();
        deviceManufact = in.readString();
        model = in.readString();
        typeToSend = in.readString();
        devImg = in.readInt();
        dataType = in.readString();
        id = in.readString();
        swVersion = in.readString();
        traits = in.createStringArrayList();
        type = in.readString();
        lang = in.readString();
        visOn = in.readInt();
    }

    public static final Creator<Devices> CREATOR = new Creator<Devices>() {
        @Override
        public Devices createFromParcel(Parcel in) {
            return new Devices(in);
        }

        @Override
        public Devices[] newArray(int size) {
            return new Devices[size];
        }
    };

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Bindable
    public String getTypeToSend() {
        return typeToSend;
    }

    public void setTypeToSend(String typeToSend) {
        this.typeToSend = typeToSend;
    }

    public int getDevImg() {
        return devImg;
    }

    public void setDevImg(int devImg) {
        this.devImg = devImg;
    }

    public ArrayList<String> getTraits() {
        return traits;
    }

    public DeviceTypes getEnumtype() {
        return enumtype;
    }

    public String getDataType() {
        return dataType;
    }

    public String getDeviceManufact() {
        return deviceManufact;
    }

    public String getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public String getSwVersion() {
        return swVersion;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public void setDeviceManufact(String deviceManufact) {
        this.deviceManufact = deviceManufact;
    }

    public void setEnumtype(DeviceTypes enumtype) {
        this.enumtype = enumtype;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    public void setTraits(ArrayList<String> traits) {
        this.traits = traits;
    }



    public JSONObject GetJSONToSend() throws JSONException {
       //List<String> traitsToSend = new ArrayList<String>();

        JSONArray traitsToSend = new JSONArray();
        avaibleModes = new JSONObject();

        traitsToSend = GetDevicesTraits(enumtype);
        //Object[] traitsToSend= GetDevicesTraits(type).toArray();
        //map = new HashMap<>();
        //JSONObject att = new JSONObject(map);
        JSONObject params = new JSONObject();

        //HashMap<String, String> params = new HashMap<String,String>();
        params.put("name", name);
        params.put("type", enumtype.toString());
        params.put("traits", traitsToSend);
        params.put("dataType", typeToSend);
        params.put("deviceManufact", deviceManufact);
        params.put("model", model);
        params.put("hwVersion", "1");
        params.put("swVersion", "1");
        params.put("attributes", attributes);

        //JSONObject dev = new JSONObject(params);
        return  params;
    }

    public JSONArray GetDevicesTraits(DeviceTypes type) throws JSONException {
        List<String> traitsToSend = new ArrayList<String>();
        JSONArray array = new JSONArray();
        allModes = new JSONArray();
        attributes = new JSONObject();
        traitsSetted = new ArrayList<>();
        switch (type){
            case TYPEOVEN: {
                typeToSend = "action.devices.types.OVEN";
                devImg = R.drawable.oven;
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.Cook");
                traitsToSend.add("action.devices.traits.TemperatureControl");
                traitsToSend.add("action.devices.traits.Timer");
                traitsSetted = traitsToSend;
                SetTemperatureAttributes();
                SetCookModes();
                SetTimer();
            }
            break;
            case TYPEWASHER:{
                devImg = R.drawable.washer;
                typeToSend = "action.devices.types.WASHER";
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.StartStop");
                traitsToSend.add("action.devices.traits.Modes");
                traitsToSend.add("action.devices.traits.Toggles");
                traitsSetted = traitsToSend;
                List<String> setMode = new ArrayList<>();
                setMode.add("small");
                setMode.add("large");
                setMode.add("medium");
                avaibleModes = GetAviableModes(setMode,"load");
                allModes.put(avaibleModes);
                attributes.put("pausable",true);
                attributes.put("availableModes",allModes);
            }
            break;
            case TYPEAIRCOOLER:{
                devImg = R.drawable.aircooler;
                typeToSend = "action.devices.types.AIRCOOLER";
                traitsToSend.add("action.devices.traits.FanSpeed");
                traitsToSend.add("action.devices.traits.HumiditySetting");
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.TemperatureSetting");
                traitsSetted = traitsToSend;
                List<String> speeds = new ArrayList<>();
                speeds.add("low");
                speeds.add("high");
                speeds.add("medium");
                SetFanSpeed(speeds);
                SetTemeratureSettings();
            }
            break;
            case TYPEAWNING:{
                devImg = R.drawable.blinds;
                typeToSend = "action.devices.types.AWNING";
                traitsToSend.add("action.devices.traits.OpenClose");
                traitsSetted = traitsToSend;
            }
            break;
            case TYPEBATHTUB:{
                devImg = R.drawable.bathub;
                typeToSend = "action.devices.types.BATHTUB";
                traitsToSend.add("action.devices.traits.StartStop");
                traitsToSend.add("action.devices.traits.Fill");
                traitsToSend.add("action.devices.traits.TemperatureControl");
                traitsSetted = traitsToSend;
                SetTemperatureAttributes();
            }
            break;
            case TYPEBLENDER:{
                devImg = R.drawable.blender;
                typeToSend = "action.devices.types.BLENDER";
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.StartStop");
                traitsToSend.add("action.devices.traits.Timer");
                traitsToSend.add("action.devices.traits.Cook");
                traitsSetted = traitsToSend;
                SetTimer();
                SetBlendCookModes();
            }
            break;
            case TYPEBLINDS:{
                devImg = R.drawable.blinds;
                typeToSend = "action.devices.types.BLINDS";
                traitsToSend.add("action.devices.traits.OpenClose");
                traitsToSend.add("action.devices.traits.Rotation");
                traitsSetted = traitsToSend;
                SetRotationAtt();
            }
            break;
            case TYPEBOILER:{
                devImg = R.drawable.blender;
                typeToSend = "action.devices.types.BOILER";
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.TemperatureControl");
                traitsSetted = traitsToSend;
                SetTemperatureAttributes();
            }
            break;
            case TYPECARBON_MONOXIDE_DETECTOR:{
                devImg = R.drawable.sensor;
                typeToSend = "action.devices.types.CARBON_MONOXIDE_DETECTOR";
                traitsToSend.add("action.devices.traits.SensorState");
                traitsSetted = traitsToSend;
                SetSensorAttribute();
            }
            break;
            case TYPECOFFEE_MAKER:{
                devImg = R.drawable.coffemaker;
                typeToSend = "action.devices.types.COFFEE_MAKER";
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.Cook");
                traitsToSend.add("action.devices.traits.TemperatureControl");
                traitsSetted = traitsToSend;
                SetTemperatureAttributes();
                SetCoffeModes();
            }
            break;
            case TYPEDEHUMIDIFIER:{
                devImg = R.drawable.dehum;
                typeToSend = "action.devices.types.DEHUMIDIFIER";
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.FanSpeed");
                traitsToSend.add("action.devices.traits.HumiditySetting");
                traitsToSend.add("action.devices.traits.StartStop");
                traitsSetted = traitsToSend;
                List<String> speeds = new ArrayList<>();
                speeds.add("low");
                speeds.add("high");
                speeds.add("medium");
                SetFanSpeed(speeds);
            }
            break;
            case TYPEDISHWASHER:{
                devImg = R.drawable.washer;
                typeToSend = "action.devices.types.DISHWASHER";
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.StartStop");
                traitsSetted = traitsToSend;
            }
            break;
            case TYPEDRYER:{
                devImg = R.drawable.dryrer;
                typeToSend = "action.devices.types.DRYER";
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.StartStop");
                traitsSetted = traitsToSend;

            }
            break;
            case TYPEFAN:{
                devImg = R.drawable.fan;
                typeToSend = "action.devices.types.FAN";
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.FanSpeed");
                traitsSetted = traitsToSend;
                List<String> speeds = new ArrayList<>();
                speeds.add("low");
                speeds.add("high");
                speeds.add("medium");
                SetFanSpeed(speeds);
            }
            break;
            case TYPEFREEZER:{
                devImg = R.drawable.freezer;
                typeToSend = "action.devices.types.FREEZER";
                traitsToSend.add("action.devices.traits.TemperatureControl");
                traitsSetted = traitsToSend;
                SetTemperatureAttributes();
            }
            break;
            case TYPELIGHT:{
                devImg = R.drawable.light;
                typeToSend = "action.devices.types.LIGHT";
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.ColorSetting");
                traitsToSend.add("action.devices.traits.Brightness");
                traitsSetted = traitsToSend;
                SetColorSettings();
            }
            break;
            case TYPEMICROWAVE:{
                devImg = R.drawable.microwave;
                typeToSend = "action.devices.types.MICROWAVE";
                traitsToSend.add("action.devices.traits.StartStop");
                traitsToSend.add("action.devices.traits.Timer");
                traitsToSend.add("action.devices.traits.Cook");
                traitsSetted = traitsToSend;
                SetMicrowaveMode();
                SetTimer();
            }
            break;
            case TYPESMOKE_DETECTOR:{
                devImg = R.drawable.smoke;
                typeToSend = "action.devices.types.SMOKE_DETECTOR";
                traitsToSend.add("action.devices.traits.SensorState");
                traitsSetted = traitsToSend;
                SetSensorAttribute();
            }
            break;
            case TYPETHERMOSTAT:{
                devImg = R.drawable.thermostat;
                typeToSend = "action.devices.types.THERMOSTAT";
                traitsToSend.add("action.devices.traits.TemperatureSetting");
                traitsSetted = traitsToSend;
                SetTemeratureSettings();
            }
            break;
            case TYPETV:{
                devImg = R.drawable.tv;
                typeToSend = "action.devices.types.TV";
                traitsToSend.add("action.devices.traits.AppSelector");
                traitsToSend.add("action.devices.traits.OnOff");
                traitsToSend.add("action.devices.traits.Volume");
                traitsSetted = traitsToSend;
                List<String> app = new ArrayList<>();
                app.add("youtube");
                app.add("netflix");
                app.add("primevideo");
                SetAppSelector(app);
                SetVolume();
            }
            break;
            case TYPEWINDOW:{
                devImg = R.drawable.window;
                typeToSend = "action.devices.types.WINDOW";
                traitsToSend.add("action.devices.traits.OpenClose");
                traitsToSend.add("action.devices.traits.LockUnlock");
                traitsSetted = traitsToSend;
            }
            break;

            default:
        }
        for (int i=0; i<traitsToSend.size(); i++){
            array.put(traitsToSend.get(i));
        }
        return array;
    }

    public JSONObject GetAviableModes(List<String> setModes, String functionName) throws JSONException {

        JSONObject avModes = new JSONObject();
        JSONArray name = new JSONArray();
        JSONArray set = new JSONArray();
        JSONArray nameValuesArray = new JSONArray();
        name.put(functionName);

        for(int i =0;i<setModes.size();i++){
            JSONArray selValArray = new JSONArray();
            JSONObject setVal = new JSONObject();
            JSONObject settings = new JSONObject();
            JSONArray mode = new JSONArray();
            mode.put(setModes.get(i));

            setVal.put("setting_synonym", mode);
            setVal.put("lang", lang);
            selValArray.put(setVal);
            settings.put("setting_name", setModes.get(i));
            settings.put("setting_values",selValArray);
            set.put(settings);
        }
        JSONObject nameValues = new JSONObject();
        nameValues.put("name_synonym",name);
        nameValues.put("lang", lang);
        nameValuesArray.put(nameValues);

        avModes.put("name", functionName);
        avModes.put("name_values",nameValuesArray);
        avModes.put("settings", set);
        return avModes;
    }

    public void SetTemperatureAttributes() throws JSONException {
        JSONObject range = new JSONObject();
        range.put("minThresholdCelsius", 0);
        range.put("maxThresholdCelsius", 250);
        attributes.put("pausable",true);
        attributes.put("temperatureRange",range);
        attributes.put("temperatureUnitForUX","C");

    }

    public void SetCookModes() throws JSONException {

        JSONArray cookModes = new JSONArray();
        for(OvenCookMode mode : OvenCookMode.values() ){
            cookModes.put(mode);
        }
        attributes.put("supportedCookingModes", cookModes);

    }

    public void SetTimer() throws JSONException {
        attributes.put("maxTimerLimitSec", 7200);
    }

    public void SetFanSpeed(List<String> speeds) throws JSONException {
        JSONObject availableFanSpeeds = new JSONObject();
        JSONArray speedsArray = new JSONArray();
        for (String s: speeds) {
            JSONObject sObj = new JSONObject();
            JSONArray sVal = new JSONArray();
            JSONObject val = new JSONObject();
            JSONArray sSyn = new JSONArray();

            sSyn.put(s);
            val.put("speed_synonym", sSyn);
            val.put("lang", "en");
            sVal.put(val);

            sObj.put("speed_name", s);
            sObj.put("speed_values", sVal);

            speedsArray.put(sObj);
        }
        availableFanSpeeds.put("speeds",speedsArray);
        availableFanSpeeds.put("ordered",true);

        attributes.put("reversible",true);
        attributes.put("availableFanSpeeds", availableFanSpeeds);
        attributes.put("supportsFanSpeedPercent",false);
    }

    public void SetTemeratureSettings() throws JSONException {
        JSONArray tempModes = new JSONArray();
        for(ThermostatModes mode : ThermostatModes.values() ){
            tempModes.put(mode);
        }
        attributes.put("availableThermostatModes", tempModes);
        attributes.put("thermostatTemperatureUnit", "C");
    }

    public void SetBlendCookModes() throws JSONException {

        JSONArray cookModes = new JSONArray();
        for(BlenderCookMode mode : BlenderCookMode.values() ){
            cookModes.put(mode);
        }
        attributes.put("supportedCookingModes", cookModes);
    }

    public void SetRotationAtt() throws JSONException {
        JSONObject range = new JSONObject();
        attributes.put("supportsDegrees",true);
        attributes.put("supportsPercent",false);
        range.put("rotationDegreesMin",0);
        range.put("rotationDegreesMax",360);
        attributes.put("rotationDegreesRange",range);
    }

    public void SetSensorAttribute() throws JSONException {
        JSONArray supSen = new JSONArray();
        JSONObject obj = new JSONObject();
        JSONObject val = new JSONObject();

        obj.put("name ","CarbonMonoxideLevel");
        val.put("rawValueUnit","PARTS_PER_MILLION");
        obj.put("numericCapabilities", val);
        supSen.put(obj);
        attributes.put("sensorStatesSupported",supSen);
    }

    public void SetCoffeModes() throws JSONException {

        JSONArray cookModes = new JSONArray();
        for(CoffeeModes mode : CoffeeModes.values() ){
            cookModes.put(mode);
        }
        attributes.put("supportedCookingModes", cookModes);

    }

    public void SetColorSettings() throws JSONException {
        JSONObject range = new JSONObject();
        attributes.put("colorModel","rgb");
        range.put("temperatureMinK",2000);
        range.put("temperatureMaxK",9000);
        attributes.put("colorTemperatureRange", range);
    }

    public void SetMicrowaveMode() throws JSONException {

        JSONArray cookModes = new JSONArray();
        for(MicrowaveCookMode mode : MicrowaveCookMode.values() ){
            cookModes.put(mode);
        }
        attributes.put("supportedCookingModes", cookModes);
    }

    public void SetAppSelector(List<String> app) throws JSONException {
        JSONArray aviableApp = new JSONArray();
        JSONArray appNames;

        for(String s : app){
            JSONObject appSel = new JSONObject();
            JSONArray nameSyn = new JSONArray();
            appNames= new JSONArray();

            JSONObject name = new JSONObject();
            nameSyn.put(s);
            name.put("name_synonym", nameSyn);
            name.put("lang","en");
            appNames.put(name);

            appSel.put("key", s);
            appSel.put("names", appNames);
            aviableApp.put(appSel);
        }
        attributes.put("availableApplications",aviableApp);
    }

    public void SetVolume() throws JSONException {
        attributes.put(  "volumeMaxLevel",100);
        attributes.put("volumeCanMuteAndUnmute",true);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(deviceManufact);
        dest.writeString(model);
        dest.writeString(typeToSend);
        dest.writeInt(devImg);
        dest.writeString(dataType);
        dest.writeString(id);
        dest.writeString(swVersion);
        dest.writeStringList(traits);
        dest.writeString(type);
        dest.writeString(lang);
        dest.writeInt(visOn);

    }
}
