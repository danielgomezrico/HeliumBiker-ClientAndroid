package biker.helium.activities;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import biker.helium.Managers.AccelerometerManager;
import biker.helium.Managers.IAccelerometerObserver;
import biker.helium.Managers.Bluetooth.BluetoothClient.MessageType;
import biker.helium.view.DrawableView;

public class GameActivity extends Activity implements IAccelerometerObserver  {
	
	private AccelerometerManager accelManager;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        try {
			accelManager = new AccelerometerManager(this);
		} catch (Exception e) {	}
        
        setContentView(new DrawableView(this.getBaseContext()));

    }
        
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	if(accelManager != null){
    		accelManager.startListen();
    	}
    }

    @Override
    protected void onPause() {
    	super.onPause();
   
    	if(accelManager!= null){
    		accelManager.stopListen();
    	}
    }
   
    @Override
    protected void onDestroy() {
    	accelManager.stopListen();
    	super.onDestroy();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("ÀAre you sure you want to go to bluetooth window? (The actual connection will be lost)")
        	       .setCancelable(false)
        	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	               //moveTaskToBack(true);
        	        	   finish();
        	           }
        	       })
        	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	               //moveTaskToBack(false);
        	               dialog.cancel();
        	           }
        	       });
        	
        	AlertDialog alert = builder.create();
        	alert.setCancelable(false);
			alert.show();
			
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
	@Override
	public void changeAcceleromether(float x, float y) {
		try {
			BluetoothActivity.bluetoothClient.send(MessageType.A, x, y);
		} 
		catch (IOException e) {} 
		catch (Exception e) {}
	}

    
}