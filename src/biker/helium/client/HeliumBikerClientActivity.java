package biker.helium.client;

import java.io.IOException;

import biker.helium.client.BluetoothClient.MessageType;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class HeliumBikerClientActivity extends Activity implements AccelerometerListener  {
	private BluetoothClient bluetoothClient;
	private AccelerometerManager accelManager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        try {
			bluetoothClient = new BluetoothClient(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		} 
        try {
			accelManager = new AccelerometerManager(this);
		} catch (Exception e) {	}
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	accelManager.startListen();
    	
    	try {
			bluetoothClient = new BluetoothClient(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    protected void onPause() {
    	super.onPause();

    	accelManager.stopListen();
    	bluetoothClient.closeConnection();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	try {
			bluetoothClient.manageConnection();
		} catch (IOException e) {}
    	
    	super.onActivityResult(requestCode, resultCode, data);
    }

	@Override
	public void changeAcceleromether(float x, float y) {
		try {
			bluetoothClient.send(MessageType.A, x, y);
		} 
		catch (IOException e) {} 
		catch (Exception e) {}
	}

    
}