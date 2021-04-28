package com.example.googleassistantproject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static Utils instance = null;
    public List<Devices> devicesList;
    public List<JsonObjectRequest> requests;
    String url = "https://us-central1-test-758f8.cloudfunctions.net/addMessage";

    public Utils(){

    }

    public static Utils getInstance() {
        // Crea l'oggetto solo se NON esiste:
        if (instance == null) {
            instance = new Utils();
        }
        return instance;
    }

    //Faccio partire l'applicazione con qualche device già impostato per comodità
    public List<JsonObjectRequest> StartAppWithDefautDevices() throws JSONException {

        JSONObject jsonBody = new JSONObject();
        Devices washer= new Devices("Washer", DeviceTypes.TYPEWASHER,"Bosh","12-LDK");
        Devices dishwasher = new Devices("Dishwasher", DeviceTypes.TYPEDISHWASHER,"LG","ASK23");
        Devices oven = new Devices("Oven", DeviceTypes.TYPEOVEN,"Candy","OWR45");
        devicesList = new ArrayList<Devices>();
        requests = new ArrayList<JsonObjectRequest>();
        devicesList.add(washer);
        devicesList.add(dishwasher);
        devicesList.add(oven);
        for(int i=0; i< devicesList.size();i++){
            JsonObjectRequest jsObjRequest = SendDataToDb(devicesList.get(i));
            requests.add(jsObjRequest);
        }
        return requests;
    }

    public List<Devices> getDevicesList() {
        return devicesList;
    }

    public JsonObjectRequest AddDeviceToList(Devices device){
        if(devicesList == null)
            devicesList = new ArrayList<Devices>();
        devicesList.add(device);
        JsonObjectRequest jsObjRequest = SendDataToDb(device);
        return jsObjRequest;
    }

    public JsonObjectRequest SendDataToDb(Devices i){
        JSONObject test = null;
        try {
            test = i.GetJSONToSend();

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url,test,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                System.out.println(response.getString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("That didn't work!");
                }

            });
            return jsObjRequest;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void SetDevice(Devices d) throws JSONException {
        DeviceTypes ty = DeviceTypes.valueOf(d.type);
        Devices toAdd = new Devices(d.name,ty,d.deviceManufact,d.model);
        if(!devicesList.contains(toAdd)){
            devicesList.add(toAdd);
        }
    }


}
