
'use strict';

const functions = require('firebase-functions');
const {smarthome} = require('actions-on-google');
const {google} = require('googleapis');
const util = require('util');
const admin = require('firebase-admin');
//const firestore = require('./firestore');

// Initialize Firebase
admin.initializeApp();
const firebaseRef = admin.database().ref('/');
// Initialize Homegraph
const auth = new google.auth.GoogleAuth({
  scopes: ['https://www.googleapis.com/auth/homegraph'],
});
const homegraph = google.homegraph({
  version: 'v1',
  auth: auth,
});
// Hardcoded user ID
const USER_ID = '123';
const db = admin.firestore();
var docRef;
//const docRef = db.collection('devices').doc('Device2');
var x;
var test;
var toPrint;

//import SmartHomeV1SyncDevices from 'actions-on-google';

exports.addMessage = functions.https.onRequest(async (req, res) =>  { 
	var name = req.body.name; // your data 
	var type = req.body.type; // your data
	var traits = req.body.traits; // your data 
	var dataType = req.body.dataType; // your data 
	var deviceManufact = req.body.deviceManufact; // your data 
	var model = req.body.model; // your data 
	var hwVersion = req.body.hwVersion; // your data 
	var swVersion = req.body.swVersion; // your data 
	var attributes = req.body.attributes;
	var id = type +"-" + name;
	await quickstartAddData(db, name, type,traits,dataType,deviceManufact,model,hwVersion,swVersion,attributes);

	res.status(200).json({ 
		message: "Data received successfully" 
	}); 
}); 

const quickstartAddData = async(db,name,type,traits,dataType,deviceManufact,model,hwVersion,swVersion,attributes)=> {
	docRef = db.collection('devices').doc(type +"-" + name);
	var dbId = type +"-" + name;
	
await docRef.set({
  id: type +"-" + name,
  name: name,
  type: type,
  traits: traits,
  dataType : dataType,
  deviceManufact: deviceManufact,
  model:model,
  hwVersion:hwVersion,
  swVersion:swVersion,
  attributes:attributes
});
	Inizialize(dbId, traits);
};

exports.login = functions.https.onRequest((request, response) => {
  if (request.method === 'GET') {
    functions.logger.log('Requesting login page');
    response.send(`
    <html>
      <meta name="viewport" content="width=device-width, initial-scale=1">
      <body>
        <form action="/login" method="post">
          <input type="hidden"
            name="responseurl" value="${request.query.responseurl}" />
          <button type="submit" style="font-size:14pt">
            Link this service to Google
          </button>
        </form>
      </body>
    </html>
  `);
  } else if (request.method === 'POST') {
    // Here, you should validate the user account.
    // In this sample, we do not do that.
    const responseurl = decodeURIComponent(request.body.responseurl);
    functions.logger.log(`Redirect to ${responseurl}`);
    return response.redirect(responseurl);
  } else {
    // Unsupported method
    response.send(405, 'Method Not Allowed');
  }
});

exports.fakeauth = functions.https.onRequest((request, response) => {
  const responseurl = util.format('%s?code=%s&state=%s',
      decodeURIComponent(request.query.redirect_uri), 'xxxxxx',
      request.query.state);
  functions.logger.log(`Set redirect as ${responseurl}`);
  return response.redirect(
      `/login?responseurl=${encodeURIComponent(responseurl)}`);
});

exports.faketoken = functions.https.onRequest((request, response) => {
  const grantType = request.query.grant_type ?
    request.query.grant_type : request.body.grant_type;
  const secondsInDay = 86400; // 60 * 60 * 24
  const HTTP_STATUS_OK = 200;
  functions.logger.log(`Grant type ${grantType}`);

  let obj;
  if (grantType === 'authorization_code') {
    obj = {
      token_type: 'bearer',
      access_token: '123access',
      refresh_token: '123refresh',
      expires_in: secondsInDay,
    };
  } else if (grantType === 'refresh_token') {
    obj = {
      token_type: 'bearer',
      access_token: '123access',
      expires_in: secondsInDay,
    };
  }
  response.status(HTTP_STATUS_OK)
      .json(obj);
});

const app = smarthome();

app.onSync(async(body,headers) => {
	let state; let ref;
    const devices = await getDevices();
    const syncResponse = {
    requestId: body.requestId,
    payload: {
      agentUserId: USER_ID,
      devices,
    },
  };
   /*state = {Turbo: false};
   ref = firebaseRef.child('TYPEWASHER-washer').child('Toggles');
   ref.update(state)
      .then(() => state);*/
  return syncResponse;
});

var getDevices = async function(){
   const querySnapshot = await db.collection('devices').get();
   const devices = [];
   let map = new Map();
   querySnapshot.forEach(doc => {
	const data = doc.data();
	map = data.attributes;

	const device =
	{
      id: data.id,
      type: data.dataType,
      traits: data.traits,
      name: {
        defaultNames: [data.name],
        name: data.name,
        nicknames: [data.name],
      },
      deviceInfo: {
        manufacturer: data.deviceManufact,
        model: data.model,
        hwVersion: data.hwVersion,
        swVersion: data.swVersion,
      },
      willReportState: true,
	  attributes: map,
     };
	
	devices.push(device);
	
 });
 return devices;
}

const Inizialize = function (id,type) {
	let state; let ref;
	
	type.forEach(doc => {
	const data = doc;
	
	switch (data) {
    case 'action.devices.traits.OnOff':
      state = {on: false};
      ref = firebaseRef.child(id).child('OnOff');
	  SetState(ref,id,state);
      break;
    case 'action.devices.traits.StartStop':
      state = {isRunning: false};
      ref = firebaseRef.child(id).child('StartStop');
	  SetState(ref,id,state);
      break;
    case 'action.devices.traits.RunCycle':
      state = {isRunCycle: false};
      ref = firebaseRef.child(id).child('RunCycle');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.Cook':
      state = {CookMode: ""};
      ref = firebaseRef.child(id).child('CookMode');
	  SetState(ref,id,state);
	  state = {isCookStart: false};
      ref = firebaseRef.child(id).child('CookStart');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.TemperatureControl':
      state = {Temperature: 0};
      ref = firebaseRef.child(id).child('Temperature');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.Timer':
      state = {Timer: 0};
      ref = firebaseRef.child(id).child('Timer');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.FanSpeed':
      state = {fanSpeed: ""};
      ref = firebaseRef.child(id).child('fanSpeed');
	  SetState(ref,id,state);
	  state = {reversible: true};
      ref = firebaseRef.child(id).child('reversible');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.HumiditySetting':
      state = {humiditySetpointPercent: 0};
      ref = firebaseRef.child(id).child('humiditySetpointPercent');
	  SetState(ref,id,state);
	  state = {humidityAmbientPercent: 70};
      ref = firebaseRef.child(id).child('humidityAmbientPercent');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.TemperatureSetting':
      state = {activeThermostatMode: "none"};
      ref = firebaseRef.child(id).child('activeThermostatMode');
	  SetState(ref,id,state);
	  state = {thermostatMode: "none"};
      ref = firebaseRef.child(id).child('thermostatMode');
	  SetState(ref,id,state);
	  state = {thermostatTemperatureSetpoint: 0};
      ref = firebaseRef.child(id).child('thermostatTemperatureSetpoint');
	  SetState(ref,id,state);
	  state = {thermostatTemperatureAmbient: 20};
      ref = firebaseRef.child(id).child('thermostatTemperatureAmbient');
	  SetState(ref,id,state);
	  state = {thermostatTemperatureSetpointLow: 0};
      ref = firebaseRef.child(id).child('thermostatTemperatureSetpointLow');
	  SetState(ref,id,state);
	  state = {thermostatTemperatureSetpointHigh: 0};
      ref = firebaseRef.child(id).child('thermostatTemperatureSetpointHigh');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.OpenClose':
      state = {OpenClose: 0};
      ref = firebaseRef.child(id).child('OpenClose');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.Fill':
      state = {isFilled: false};
      ref = firebaseRef.child(id).child('isFilled');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.Rotation':
      state = {rotationDegrees: 0};
      ref = firebaseRef.child(id).child('rotationDegrees');
	  SetState(ref,id,state);
	  break;

	case 'action.devices.traits.SensorState':
      state = {currentSensorState: ""};
      ref = firebaseRef.child(id).child('currentSensorState');
	  SetState(ref,id,state);
	  state = {name: ""};
      ref = firebaseRef.child(id).child('name');
	  SetState(ref,id,state);
	  state = {rawValue: 0};
      ref = firebaseRef.child(id).child('rawValue');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.ColorSetting':
      state = {spectrumRGB: 0};
      ref = firebaseRef.child(id).child('spectrumRGB');
	  SetState(ref,id,state);
	  state = {name: ""};
      ref = firebaseRef.child(id).child('name');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.Brightness':
      state = {brightness: 0};
      ref = firebaseRef.child(id).child('brightness');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.LockUnlock':
      state = {isLocked: false};
      ref = firebaseRef.child(id).child('isLocked');
	  SetState(ref,id,state);
	  state = {isJammed: false};
      ref = firebaseRef.child(id).child('isJammed');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.AppSelector':
      state = {currentApplication: ""};
      ref = firebaseRef.child(id).child('currentApplication');
	  SetState(ref,id,state);
	  break;
	case 'action.devices.traits.Volume':
      state = {currentVolume: 20};
      ref = firebaseRef.child(id).child('currentVolume');
	  SetState(ref,id,state);
	  state = {isMuted: false};
      ref = firebaseRef.child(id).child('isMuted');
	  SetState(ref,id,state);
	  break;
	};

	});
	if(id.includes("TYPEWASHER")){
	 state = {load: ""};
     ref = firebaseRef.child(id).child('Modes');
	 SetState(ref,id,state);
	 
	  
	 state = {Turbo: false};
     ref = firebaseRef.child(id).child('Toggles');
	 SetState(ref,id,state);
	}
};

const SetState = function(ref,id,state){
	ref.update(state)
      .then(() => state);
};

const queryFirebase = async (deviceId) => {

  const snapshot = await firebaseRef.child(deviceId).once('value');
  const snapshotVal = snapshot.val();
  if(deviceId.includes("TYPEWASHER")){
  return {
	on: snapshotVal.OnOff.on,
    isRunning: snapshotVal.StartStop.isRunning,
	// Add Modes snapshot
    load: snapshotVal.Modes.load,
	//Turbo: snapshotVal.Toggles.Turbo,
  };
  }
  else if(deviceId.includes('TYPEOVEN')){
	  return {
			on:snapshotVal.OnOff.on,
			CookMode: snapshotVal.CookMode.CookMode,
			isCookStart: snapshotVal.CookStart.isCookStart,
			Temperature : snapshotVal.Temperature.Temperature,
			Timer: snapshotVal.Timer.Timer,
	};
  }
    else if(deviceId.includes('TYPEAIRCOOLER')){
	  return {
			on:snapshotVal.OnOff.on,
			fanSpeed: snapshotVal.fanSpeed.fanSpeed,
			reversible: snapshotVal.reversible.reversible,
			humiditySetpointPercent: snapshotVal.humiditySetpointPercent.humiditySetpointPercent,
			humidityAmbientPercent: snapshotVal.humidityAmbientPercent.humidityAmbientPercent,
			activeThermostatMode: snapshotVal.activeThermostatMode.activeThermostatMode,
			thermostatMode:snapshotVal.thermostatMode.thermostatMode,
			thermostatTemperatureSetpointHigh :snapshotVal.thermostatTemperatureSetpointHigh.thermostatTemperatureSetpointHigh,
			thermostatTemperatureSetpointLow:snapshotVal.thermostatTemperatureSetpointLow.thermostatTemperatureSetpointLow,
			thermostatTemperatureAmbient:snapshotVal.thermostatTemperatureAmbient.thermostatTemperatureAmbient,
			thermostatTemperatureSetpoint :snapshotVal.thermostatTemperatureSetpoint.thermostatTemperatureSetpoint,
	};
  }
  else if(deviceId.includes('TYPEAWNING')){
	  return {
			openPercent:snapshotVal.OpenClose.OpenClose,			
		};
	}
  else if(deviceId.includes('TYPEBATHTUB')){
	  return {
			isRunning: snapshotVal.StartStop.isRunning,
			Temperature : snapshotVal.Temperature.Temperature,
			isFilled:snapshotVal.isFilled.isFilled,

	};
  }
  else if(deviceId.includes('TYPEBLENDER')){
	  return {
			on: snapshotVal.OnOff.on,
			isRunning: snapshotVal.StartStop.isRunning,
			Timer: snapshotVal.Timer.Timer,
			CookMode: snapshotVal.CookMode.CookMode,
			isCookStart: snapshotVal.CookStart.isCookStart,
	};
  }
  else if(deviceId.includes('TYPEBLINDS')){
	  return {
			openPercent:snapshotVal.OpenClose.OpenClose,	
			rotationDegrees: snapshotVal.rotationDegrees.rotationDegrees,

	};
  }
  else if(deviceId.includes('TYPEBOILER')){
	  return {
			on: snapshotVal.OnOff.on,	
			Temperature : snapshotVal.Temperature.Temperature,
	};
  }
   else if(deviceId.includes('TYPECARBON_MONOXIDE_DETECTOR')|| deviceId.includes('TYPESMOKE_DETECTOR')){
	  return {
			name: snapshotVal.name.name,	
			rawValue : snapshotVal.rawValue.rawValue,
	};
  }
   else if(deviceId.includes('TYPECOFFEE_MAKER')){
	  return {
			on:snapshotVal.OnOff.on,
			CookMode: snapshotVal.CookMode.CookMode,
			isCookStart: snapshotVal.CookStart.isCookStart,
			Temperature : snapshotVal.Temperature.Temperature,
	};
  }
   else if(deviceId.includes('TYPEDEHUMIDIFIER')){
	  return {
			on:snapshotVal.OnOff.on,
			isRunning: snapshotVal.StartStop.isRunning,
			fanSpeed: snapshotVal.fanSpeed.fanSpeed,
			reversible: snapshotVal.reversible.reversible,
			humiditySetpointPercent: snapshotVal.humiditySetpointPercent.humiditySetpointPercent,
			humidityAmbientPercent: snapshotVal.humidityAmbientPercent.humidityAmbientPercent,
	};
  }
     else if(deviceId.includes('TYPEDISHWASHER')|| deviceId.includes('TYPEDRYER')){
	  return {
			on:snapshotVal.OnOff.on,
			isRunning: snapshotVal.StartStop.isRunning,		
	};
  }
    else if(deviceId.includes('TYPEFAN')){
	  return {
			on:snapshotVal.OnOff.on,
			fanSpeed: snapshotVal.fanSpeed.fanSpeed,
	};
  }
    else if(deviceId.includes('TYPEFREEZER')){
	  return {
			Temperature : snapshotVal.Temperature.Temperature,
	};
  }
    else if(deviceId.includes('TYPELIGHT')){
	  return {
			spectrumRGB : snapshotVal.spectrumRGB.spectrumRGB,
			brightness : snapshotVal.brightness.brightness,
	};
  }
    else if(deviceId.includes('TYPEMICROWAVE')){
	  return {
			isRunning: snapshotVal.StartStop.isRunning,
			Timer: snapshotVal.Timer.Timer,
			CookMode: snapshotVal.CookMode.CookMode,
			isCookStart: snapshotVal.CookStart.isCookStart,
	};
  }
    else if(deviceId.includes('TYPETHERMOSTAT')){
	  return {
			activeThermostatMode: snapshotVal.activeThermostatMode.activeThermostatMode,
			thermostatMode:snapshotVal.thermostatMode.thermostatMode,
			thermostatTemperatureSetpointHigh :snapshotVal.thermostatTemperatureSetpointHigh.thermostatTemperatureSetpointHigh,
			thermostatTemperatureSetpointLow:snapshotVal.thermostatTemperatureSetpointLow.thermostatTemperatureSetpointLow,
			thermostatTemperatureAmbient:snapshotVal.thermostatTemperatureAmbient.thermostatTemperatureAmbient,
			thermostatTemperatureSetpoint :snapshotVal.thermostatTemperatureSetpoint.thermostatTemperatureSetpoint,
	};
  }
    else if(deviceId.includes('TYPEWINDOW')){
	  return {
			openPercent:snapshotVal.OpenClose.OpenClose,
			isLocked : snapshotVal.isLocked.isLocked,
			isJammed: snapshotVal.isJammed.isJammed,
	};
  }
    else if(deviceId.includes('TYPETV')){
	  return {
			on:snapshotVal.OnOff.on,
			currentVolume : snapshotVal.currentVolume.currentVolume,
			isMuted : snapshotVal.isMuted.isMuted,
			currentApplication:snapshotVal.currentApplication.currentApplication,
			
	};
  }
  else{
	  return {
		 // on:snapshotVal.OnOff.on,
	}
	};
};

const queryDevice = async (deviceId) => {
	const data = await queryFirebase(deviceId);
	if(deviceId.includes('TYPEWASHER')){
  return {
	on: data.on,
    isRunning: data.isRunning,

    // Add currentModeSettings
    currentModeSettings: {
      load: data.load,
    },
   /* currentToggleSettings: {
      Turbo: data.Turbo,
    },*/
  };
	}
  else if(deviceId.includes('TYPEOVEN')){
	  return {
			on: data.on,
			currentCookingMode: data.CookMode,
			temperatureSetpointCelsius : data.Temperature,
			timerRemainingSec : data.Timer,
	};
  }
  else if(deviceId.includes('TYPEAIRCOOLER')){
	  return {
			on: data.on,
			currentFanSpeedSetting: data.fanSpeed,
			humiditySetpointPercent: data.humiditySetpointPercent,
			humidityAmbientPercent: data.humidityAmbientPercent,
			activeThermostatMode: data.activeThermostatMode,
			thermostatMode: data.thermostatMode,
			thermostatTemperatureSetpoint: data.thermostatTemperatureSetpoint,
			thermostatTemperatureAmbient: data.thermostatTemperatureAmbient,
			thermostatTemperatureSetpointHigh:data.thermostatTemperatureSetpointHigh,
			thermostatTemperatureSetpointLow:data.thermostatTemperatureSetpointLow,
	};
  }
   else if(deviceId.includes('TYPEAWNING')){
	  return {
			openPercent: data.openPercent,		
	};
  }
    else if(deviceId.includes('TYPEBATHTUB')){
	  return {
			isRunning: data.isRunning,	
			temperatureSetpointCelsius : data.Temperature,
			timerRemainingSec : data.Timer,
			isFilled :data.isFilled,

	};
  }
     else if(deviceId.includes('TYPEBLENDER')){
	  return {
			on: data.on,
			currentCookingMode: data.CookMode,
			timerRemainingSec : data.Timer,
			isRunning: data.isRunning,
	};
  }  
    else if(deviceId.includes('TYPEBLINDS')){
	  return {
			openPercent: data.openPercent,		
			rotationDegrees: data.rotationDegrees,

	};
  }
       else if(deviceId.includes('TYPEBOILER')){
	  return {
			on: data.on,
			temperatureSetpointCelsius : data.Temperature,
	};
  }
      else if(deviceId.includes('TYPECARBON_MONOXIDE_DETECTOR')|| deviceId.includes('TYPESMOKE_DETECTOR')){
	  return {
			name: data.name,
			rawValue : data.rawValue,
	};
  }
       else if(deviceId.includes('TYPECOFFEE_MAKER')){
	  return {
			on: data.on,
			currentCookingMode: data.CookMode,
			temperatureSetpointCelsius : data.Temperature,
	};
  }
    else if(deviceId.includes('TYPEDEHUMIDIFIER')){
	  return {
			on: data.on,
			isRunning: data.isRunning,
			currentFanSpeedSetting: data.fanSpeed,
			humiditySetpointPercent: data.humiditySetpointPercent,
			humidityAmbientPercent: data.humidityAmbientPercent,
	};
  }
    else if(deviceId.includes('TYPEDISHWASHER')|| deviceId.includes('TYPEDRYER')){
	  return {
			on: data.on,
			isRunning: data.isRunning,		
	};
  }
    else if(deviceId.includes('TYPEFAN')){
	  return {
			on: data.on,
			currentFanSpeedSetting: data.fanSpeed,
	};
  }
      else if(deviceId.includes('TYPEFREEZER')){
	  return {
			temperatureSetpointCelsius : data.Temperature,
	};
  }
    else if(deviceId.includes('TYPELIGHT')){
	  return {

		brightness: data.brightness,
					color: {
				spectrumRgb: data.spectrumRGB,
		},
	};
  }
    else if(deviceId.includes('TYPEMICROWAVE')){
	  return {
			isRunning: data.isRunning,
			currentCookingMode: data.CookMode,
			timerRemainingSec : data.Timer,
	};
  }
    else if(deviceId.includes('TYPETHERMOSTAT')){
	  return {
			activeThermostatMode: data.activeThermostatMode,
			thermostatMode: data.thermostatMode,
			thermostatTemperatureSetpoint: data.thermostatTemperatureSetpoint,
			thermostatTemperatureAmbient: data.thermostatTemperatureAmbient,
			thermostatTemperatureSetpointHigh:data.thermostatTemperatureSetpointHigh,
			thermostatTemperatureSetpointLow:data.thermostatTemperatureSetpointLow,
	};
  }
    else if(deviceId.includes('TYPEWINDOW')){
	  return {
			openPercent: data.openPercent,	
			isLocked: data.isLocked,
			isJammed: data.isJammed,
	};
  }
    else if(deviceId.includes('TYPETV')){
	  return {
			on: data.on,	
			currentVolume: data.currentVolume,
			isMuted: data.isMuted,
			currentApplication: data.currentApplication,	

	};
  }
	else{
		return {
			on: data.on,			

	};
}
};

app.onQuery(async (body) => {
  const {requestId} = body;
  const payload = {
    devices: {},
  };
  const queryPromises = [];
  const intent = body.inputs[0];
  for (const device of intent.payload.devices) {
    const deviceId = device.id;
    queryPromises.push(
        queryDevice(deviceId)
            .then((data) => {
              // Add response to device payload
              payload.devices[deviceId] = data;
            }) );
  }
  // Wait for all promises to resolve
  await Promise.all(queryPromises);
  return {
    requestId: requestId,
    payload: payload,
  };
});

const updateDevice = async (execution, deviceId) => {
  const {params, command} = execution;
  let state; let ref;
  switch (command) {
    case 'action.devices.commands.OnOff':
      state = {on: params.on};
      ref = firebaseRef.child(deviceId).child('OnOff');
      break;
    case 'action.devices.commands.StartStop':
      state = {isRunning: params.start};
      ref = firebaseRef.child(deviceId).child('StartStop');
      break;
    case 'action.devices.commands.PauseUnpause':
      state = {isPaused: params.pause};
      ref = firebaseRef.child(deviceId).child('StartStop');
	  break;
	case 'action.devices.commands.SetModes':
      state = {load: params.updateModeSettings.load};
      ref = firebaseRef.child(deviceId).child('Modes');
      break;
	case 'action.devices.commands.SetToggles':
      state = {Turbo: params.updateToggleSettings.Turbo};
      ref = firebaseRef.child(deviceId).child('Toggles');
      break;
	case 'action.devices.commands.Cook':
	  state = {isCookStart: params.start};
      ref = firebaseRef.child(deviceId).child('CookStart');
	  SetState(ref,deviceId,state);
      state = {CookMode: params.cookingMode};
      ref = firebaseRef.child(deviceId).child('CookMode');
	  SetState(ref,deviceId,state);
	  break;
	case 'action.devices.commands.SetTemperature':
      state = {Temperature: params.temperature};
      ref = firebaseRef.child(deviceId).child('Temperature');
	  break;
	case 'action.devices.commands.TimerStart':
      state = {Timer: params.timerTimeSec};
      ref = firebaseRef.child(deviceId).child('Timer');
	  break;
	case 'action.devices.commands.TimerAdjust':
      state = {Timer: params.timerTimeSec};
      ref = firebaseRef.child(deviceId).child('Timer');
	  break;
	case 'action.devices.commands.TimerResume':
      state = {Timer: params.timerTimeSec};
      ref = firebaseRef.child(deviceId).child('Timer');
	  break;
	case 'action.devices.commands.TimerCancel':
      state = {Timer: params.timerTimeSec};
      ref = firebaseRef.child(deviceId).child('Timer');
	  break;
	case 'action.devices.commands.SetFanSpeed':
      state = {fanSpeed: params.fanSpeed};
      ref = firebaseRef.child(deviceId).child('fanSpeed');
	  break;
	case 'action.devices.commands.Reverse':
      state = {};
	  break;
	case 'action.devices.commands.SetHumidity':
      state = {humiditySetpointPercent: params.humidity};
      ref = firebaseRef.child(deviceId).child('humiditySetpointPercent');
	  break;
	case 'action.devices.commands.ThermostatTemperatureSetpoint':
      state = {thermostatTemperatureSetpoint: params.thermostatTemperatureSetpoint};
      ref = firebaseRef.child(deviceId).child('thermostatTemperatureSetpoint');
	  break;
	case 'action.devices.commands.ThermostatTemperatureSetRange':
      state = {thermostatTemperatureSetpointLow: params.thermostatTemperatureSetpointLow};
      ref = firebaseRef.child(deviceId).child('thermostatTemperatureSetpointLow');
	  SetState(ref,deviceId,state);
	  state = {thermostatTemperatureSetpointHigh: params.thermostatTemperatureSetpointHigh};
      ref = firebaseRef.child(deviceId).child('thermostatTemperatureSetpointHigh');
	  SetState(ref,deviceId,state);
	  break;
	case 'action.devices.commands.ThermostatSetMode':
      state = {thermostatMode: params.thermostatMode};
      ref = firebaseRef.child(deviceId).child('thermostatMode');
	  break;
	case 'action.devices.commands.OpenClose':
      state = {OpenClose: params.openPercent};
      ref = firebaseRef.child(deviceId).child('OpenClose');
	  break;
	case 'action.devices.commands.Fill':
      state = {isFilled: params.fill};
      ref = firebaseRef.child(deviceId).child('isFilled');
	  SetState(ref,deviceId,state);
	  break;
	case 'action.devices.commands.RotateAbsolute':
      state = {rotationDegrees: params.rotationDegrees};
      ref = firebaseRef.child(deviceId).child('rotationDegrees');
	  SetState(ref,deviceId,state);
	  break;
	case 'action.devices.commands.ColorAbsolute':
      state = {name: params.color.name};
      ref = firebaseRef.child(deviceId).child('name');
	  SetState(ref,deviceId,state);
	  state = {spectrumRGB: params.color.spectrumRGB};
      ref = firebaseRef.child(deviceId).child('spectrumRGB');
	  SetState(ref,deviceId,state);
	  break;
	case 'action.devices.commands.BrightnessAbsolute':
      state = {brightness: params.brightness};
      ref = firebaseRef.child(deviceId).child('brightness');
	  SetState(ref,deviceId,state);
	  break;
	case 'action.devices.commands.LockUnlock':
      state = {isLocked: params.lock};
      ref = firebaseRef.child(deviceId).child('isLocked');
	  SetState(ref,deviceId,state);
	  break;
	case 'action.devices.commands.appSelect':
      state = {currentApplication: params.newApplication};
      ref = firebaseRef.child(deviceId).child('currentApplication');
	  SetState(ref,deviceId,state);
	  break;
	case 'action.devices.commands.mute':
      state = {isMuted: params.mute};
      ref = firebaseRef.child(deviceId).child('isMuted');
	  SetState(ref,deviceId,state);
	  break;
	case 'action.devices.commands.setVolume':
      state = {currentVolume: params.volumeLevel};
      ref = firebaseRef.child(deviceId).child('currentVolume');
	  SetState(ref,deviceId,state);
	  break;
  }
if(command != 'action.devices.commands.Reverse'){
  return ref.update(state)
      .then(() => state);
}
else{
	return state;
}
	  
};

app.onExecute(async (body) => {
  const {requestId} = body;
  // Execution results are grouped by status
  const result = {
    ids: [],
    status: 'SUCCESS',
    states: {
      online: true,
    },
  };

  const executePromises = [];
  const intent = body.inputs[0];
  for (const command of intent.payload.commands) {
    for (const device of command.devices) {
      for (const execution of command.execution) {
		
        executePromises.push(
            updateDevice(execution, device.id)
                .then((data) => {
                  result.ids.push(device.id);
                  Object.assign(result.states, data);
                })
                .catch(() => functions.logger.error('EXECUTE', device.id)));
      }
    }
  }

  await Promise.all(executePromises);
  return {
    requestId: requestId,
    payload: {
      commands: [result],
    },
  };
});

app.onDisconnect((body, headers) => {
  functions.logger.log('User account unlinked from Google Assistant');
  // Return empty response
  return {};
});

exports.smarthome = functions.https.onRequest(app);

exports.requestsync = functions.https.onRequest(async (request, response) => {
  response.set('Access-Control-Allow-Origin', '*');
  functions.logger.info(`Request SYNC for user ${USER_ID}`);
  try {
    const res = await homegraph.devices.requestSync({
      requestBody: {
        agentUserId: USER_ID,
      },
    });
    functions.logger.info('Request sync response:', res.status, res.data);
    response.json(res.data);
  } catch (err) {
    functions.logger.error(err);
    response.status(500).send(`Error requesting sync: ${err}`);
  }
});

/**
 * Send a REPORT STATE call to the homegraph when data for any device id
 * has been changed.
 */
exports.reportstate = functions.database.ref('{deviceId}').onWrite(
    async (change, context) => {
      functions.logger.info('Firebase write event triggered Report State');
      const snapshot = change.after.val();
	  const deviceId = context.params.deviceId;
	  var requestBody;
	  functions.logger.info(snapshot);
if(deviceId.includes('TYPEWASHER')){
      requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              /* Report the current state of our washer */
              [context.params.deviceId]: {
                on: snapshot.OnOff.on,
                isRunning: snapshot.StartStop.isRunning,
				currentModeSettings: {
					load: snapshot.Modes.load,
				},
              },
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEOVEN')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				  on: snapshot.OnOff.on,
				  currentCookingMode: snapshot.CookMode.CookMode,
				  temperatureSetpointCelsius : snapshot.Temperature.Temperature,
			      timerRemainingSec : snapshot.Timer.Timer,           
              },
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEAIRCOOLER')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
			on:snapshot.OnOff.on,
			currentFanSpeedSetting:snapshot.fanSpeed.fanSpeed,
			humiditySetpointPercent: snapshot.humiditySetpointPercent.humiditySetpointPercent,
			humidityAmbientPercent: snapshot.humidityAmbientPercent.humidityAmbientPercent,
			activeThermostatMode: snapshot.activeThermostatMode.activeThermostatMode,
			thermostatMode:snapshot.thermostatMode.thermostatMode,
			thermostatTemperatureSetpointHigh :snapshot.thermostatTemperatureSetpointHigh.thermostatTemperatureSetpointHigh,
			thermostatTemperatureSetpointLow:snapshot.thermostatTemperatureSetpointLow.thermostatTemperatureSetpointLow,
			thermostatTemperatureAmbient:snapshot.thermostatTemperatureAmbient.thermostatTemperatureAmbient,
			thermostatTemperatureSetpoint :snapshot.thermostatTemperatureSetpoint.thermostatTemperatureSetpoint,			            
			
			},
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEAWNING')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				  openPercent:snapshot.OpenClose.OpenClose,
				},
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEBATHTUB')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				  isRunning: snapshot.StartStop.isRunning,
				  temperatureSetpointCelsius : snapshot.Temperature.Temperature,
				  isFilled:snapshot.isFilled.isFilled,
				},
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEBLENDER')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				on: snapshot.OnOff.on,
			    isRunning: snapshot.StartStop.isRunning,
				timerRemainingSec: snapshot.Timer.Timer,
				currentCookingMode: snapshot.CookMode.CookMode,
				},
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEBLINDS')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				openPercent:snapshot.OpenClose.OpenClose,	
				rotationDegrees: snapshot.rotationDegrees.rotationDegrees,
				},
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEBOILER')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				on: snapshot.OnOff.on,
				temperatureSetpointCelsius : snapshot.Temperature.Temperature,
				},
            },
          },
        },
      };
} 
else if(deviceId.includes('TYPECARBON_MONOXIDE_DETECTOR')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				name: snapshot.name.name,
				rawValue : snapshot.rawValue.rawValue, 
				},
            },
          },
        },
      };
} 
else if(deviceId.includes('TYPECOFFEE_MAKER')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
			on: snapshot.OnOff.on,
			currentCookingMode: snapshot.CookMode.CookMode,
			temperatureSetpointCelsius : snapshot.Temperature.Temperature,
				},
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEDEHUMIDIFIER')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
			on: snapshot.OnOff.on,
			isRunning: snapshot.StartStop.isRunning,
			currentFanSpeedSetting: snapshot.fanSpeed.fanSpeed,
			humiditySetpointPercent: snapshot.humiditySetpointPercent.humiditySetpointPercent,
			humidityAmbientPercent: snapshot.humidityAmbientPercent.humidityAmbientPercent,
				},
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEDISHWASHER') || deviceId.includes('TYPEDRYER')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
			on: snapshot.OnOff.on,
			isRunning: snapshot.StartStop.isRunning,		
				},
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEFAN')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
			on: snapshot.OnOff.on,
			currentFanSpeedSetting: snapshot.fanSpeed.fanSpeed,
				},
            },
          },
        },
      };
} 
else if(deviceId.includes('TYPEFREEZER')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				temperatureSetpointCelsius : snapshot.Temperature.Temperature,
				},
            },
          },
        },
      };
}  
else if(deviceId.includes('TYPELIGHT')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				brightness: snapshot.brightness.brightness,
					color: {
				spectrumRgb: snapshot.spectrumRGB.spectrumRGB,},
				},
            },
          },
        },
      };
} 
else if(deviceId.includes('TYPEMICROWAVE')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				isRunning: snapshot.StartStop.isRunning,
				currentCookingMode: snapshot.CookMode.CookMode,
				timerRemainingSec :  snapshot.Timer.Timer,				
				},
            },
          },
        },
      };
}   
else if(deviceId.includes('TYPETHERMOSTAT')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				activeThermostatMode: snapshot.activeThermostatMode.activeThermostatMode,
				thermostatMode: snapshot.thermostatMode.thermostatMode,
				thermostatTemperatureSetpoint: snapshot.thermostatTemperatureSetpoint.thermostatTemperatureSetpoint,
				thermostatTemperatureAmbient: snapshot.thermostatTemperatureAmbient.thermostatTemperatureAmbient,
				thermostatTemperatureSetpointHigh:snapshot.thermostatTemperatureSetpointHigh.thermostatTemperatureSetpointHigh,
				thermostatTemperatureSetpointLow:snapshot.thermostatTemperatureSetpointLow.thermostatTemperatureSetpointLow,			
				
				},
            },
          },
        },
      };
}
else if(deviceId.includes('TYPEWINDOW')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				openPercent: snapshot.OpenClose.OpenClose,	
				isLocked: snapshot.isLocked.isLocked,
				isJammed: snapshot.isJammed.isJammed,			
				},
            },
          },
        },
      };
}  
else if(deviceId.includes('TYPETV')){
	requestBody = {
        requestId: 'ff36a3cc', /* Any unique ID */
        agentUserId: USER_ID,
        payload: {
          devices: {
            states: {
              
              [context.params.deviceId]: {
				on: snapshot.OnOff.on,
				currentVolume: snapshot.currentVolume.currentVolume,
				isMuted: snapshot.isMuted.isMuted,
				currentApplication:snapshot.currentApplication.currentApplication,
			
				},
            },
          },
        },
      };
}  
 const res = await homegraph.devices.reportStateAndNotification({
        requestBody,
      });
      functions.logger.info('Report state response:', res.status, res.data);
    });