package biker.helium.activities;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import biker.helium.Managers.Bluetooth.BluetoothClient;
import biker.helium.Managers.Bluetooth.BluetoothClient.MessageType;
import biker.helium.Managers.Bluetooth.IBluetoothObserver;
import biker.helium.client.R;
import biker.helium.helpers.UIHelper;

public class BluetoothActivity extends Activity implements IBluetoothObserver {

	/**
	 * Contains the client that is connected with a bluetooth device that was selected
	 * by this activity
	 */
	private static BluetoothClient bluetoothClient;
	
	/**
	 * Bluetooth device selected by the user to connect and send messages
	 */
	public BluetoothDevice deviceToConnect;

//	****************************** ON... *********************************
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle("Bluetooth Connection");
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

        try {
			bluetoothClient = new BluetoothClient(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
        
		setContentView(R.layout.main);
	}

	@Override
	protected void onResume() {		
		super.onResume();
		
//		TODO:Cambiar mensaje
		Toast.makeText(BluetoothActivity.this,"Select your PC",Toast.LENGTH_LONG).show();

		bluetoothClient.closeConnection();
		
		enableButton(R.id.bSearchPairedDevices, true);
		enableButton(R.id.bSearchDevices, true);
		enableProgressBar(false);
		
		if(deviceToConnect != null){
			enableButton(R.id.bConnectDevice, true);
			
		}else{
			enableButton(R.id.bConnectDevice, false);
		}
	}
	
	@Override
	protected void onDestroy() {
		if(bluetoothClient != null){
			bluetoothClient.closeConnection();
		}
		
		super.onDestroy();
	}
//	**********************************************************************
	
//	************************* UI CHANGES *********************************
	/**
	 * Enable or disable the Button with id = id (R...)
	 * @param id
	 * @param enable
	 */
	private void enableButton(int id, boolean enable){
		final Button buttonS = (Button) findViewById(id);
		buttonS.setEnabled(enable);
	}

	/**
	 * Set visible or invisible the progressBar with id = id (R...)
	 * @param id
	 * @param enable
	 */
	private void enableProgressBar(boolean enable){
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility( enable ? View.VISIBLE : View.INVISIBLE);
	}

	/**
	 * Set the text for the TextView with id = id (R...)
	 * @param id
	 * @param text
	 */
	private void setText(int id, String text){
		final TextView textView = (TextView) findViewById(id);
		textView.setText(text);
	}
//	**********************************************************************

//	************************** UI Events *********************************
	/**
	 * Event Button bSearchDevicesCLick event
	 * @param view
	 */
	public void bSearchPairedDevicesClick(View view) {
		if(bluetoothClient != null){
			try {
				deviceDiscoveringFinished(bluetoothClient.searchPairedDevices());
			} catch (IOException e) {
				UIHelper.showMessageDialog("The bluetooth device can't discover devices", "Error", false, this);
			}
		}
	}
	 
	/**
	 * Event Button bSearchDevicesCLick event
	 * @param view
	 */
	public void bSearchDevicesClick(View view) {
		if(bluetoothClient != null){
			try {
				enableButton(R.id.bSearchDevices, false);
				enableButton(R.id.bSearchPairedDevices, false);
				enableButton(R.id.bConnectDevice, false);

				enableProgressBar(true);
				
				bluetoothClient.searchDevices();
			} catch (IOException e) {
				UIHelper.showMessageDialog("The bluetooth device can't discover devices", "Error", false, this);
			}
		}
	}
	
	/**
	 * Event Button bSearchDevicesCLick event
	 * @param view
	 */
	public void bConnectClick(View view) {
		if(bluetoothClient != null){
			
				enableButton(R.id.bSearchDevices, false);
				enableButton(R.id.bSearchPairedDevices, false);
				enableButton(R.id.bConnectDevice, false);

				enableProgressBar(true);
				try{
					bluetoothClient.manageConnection(deviceToConnect);

					changeActivity();
				} catch (IOException e) {
					UIHelper.showMessageDialog("The PC Bluetooth is not ready. Please check your PC", "Error", false, this);
					enableButton(R.id.bSearchDevices, true);
					enableButton(R.id.bSearchPairedDevices, true);
					enableButton(R.id.bConnectDevice, true);

					enableProgressBar(false);
				}
		}
	}
//	**********************************************************************
	
	/**
	 * Launch the GameActivity
	 */
	private void changeActivity(){
		Context context = getApplicationContext();
		
		Intent myIntent = new Intent(context, GameActivity.class);
		myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(myIntent);
		
		enableButton(R.id.bSearchDevices, true);
		enableButton(R.id.bConnectDevice, true);
		enableProgressBar(false);
	}
	
	/**
	 * Show a list selector with each device and set the selected device to
	 * @see #deviceToConnect
	 */
	@Override
	public void deviceDiscoveringFinished(final ArrayList<BluetoothDevice> devices) {
		if(devices.size() > 0){
			CharSequence[] nameDevices = new CharSequence[devices.size()];

			for (int i = 0; i < nameDevices.length; i++) {
				nameDevices[i] = devices.get(i).getName(); 
			}

			enableButton(R.id.bConnectDevice, false);
			enableProgressBar(false);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Pick your PC Bluetooth Device Name");
			builder.setItems(nameDevices, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					deviceToConnect = devices.get(item);

					setText(R.id.tvSelectedDevice, deviceToConnect.getName());

					enableButton(R.id.bSearchDevices, true);
					enableButton(R.id.bConnectDevice, true);
					enableProgressBar(false);
				}
			});
			AlertDialog alert = builder.create();
			alert.show();		
		}else{
			Toast.makeText(BluetoothActivity.this,"There's no devices to show.",Toast.LENGTH_LONG).show();
			
			enableButton(R.id.bSearchDevices, true);
			enableButton(R.id.bSearchPairedDevices, true);
			enableButton(R.id.bConnectDevice, false);
			enableProgressBar(false);


		}
	}
	
	/**
	 * Called when the intent to enable bluetooth ends
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

			dlgAlert.setMessage("This application needs bluetooth enabled to work. The application will be closed");
			dlgAlert.setTitle("Helium Biker");
			dlgAlert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);
				}
			});

			dlgAlert.setCancelable(false);
			dlgAlert.create().show();
		}else{
			
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * Send a message to the bluetooth server
	 * @param type
	 * @param x
	 * @param y
	 * @return true if successfully false if not
	 */
	public static boolean sendMessage(MessageType type, float x, float y){			
		if(bluetoothClient != null){
			return bluetoothClient.send(type, x, y);
		}else {
			return false;
		}
	}
	 

}
