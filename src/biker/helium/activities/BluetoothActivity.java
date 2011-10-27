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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import biker.helium.Managers.Bluetooth.BluetoothClient;
import biker.helium.Managers.Bluetooth.IBluetoothObserver;
import biker.helium.client.R;

public class BluetoothActivity extends Activity implements IBluetoothObserver {

	/**
	 * Contains the client that is connected with a bluetooth device that was selected
	 * by this activity
	 */
	public static BluetoothClient bluetoothClient;
	
	public BluetoothDevice deviceToConnect;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.main);
	}

	@Override
	protected void onResume() {		
		super.onResume();
		
		init();
	}
	
	private void init(){
		
		try {
			bluetoothClient = new BluetoothClient(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		deviceToConnect = null;
		
		enableButton(R.id.bSearchDevices, true);
		enableButton(R.id.bConnectDevice, false);
		enableProgressBar(false);
		
		setText(R.id.tvSelectedDevice, " ...");
	}
	
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

	/**
	 * Called when the intent to enable bluetooth ends
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);

			dlgAlert.setMessage("This application needs bluetooth enabled to work. The application will be closed");
			dlgAlert.setTitle("Helium Biker GamePad");
			dlgAlert.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					System.exit(0);
				}
			});

			dlgAlert.setCancelable(false);
			dlgAlert.create().show();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void deviceDiscoveringFinished(final ArrayList<BluetoothDevice> devices) {
		CharSequence[] nameDevices = new CharSequence[devices.size()];

		for (int i = 0; i < nameDevices.length; i++) {
			nameDevices[i] = devices.get(i).getName(); 
		}

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
		alert.setCancelable(false);
		alert.show();		
	}

	/**
	 * Event Button bSearchDevicesCLick event
	 * @param view
	 */
	public void bSearchDevicesClick(View view) {
		if(bluetoothClient != null){
			try {
				enableButton(R.id.bSearchDevices, false);
				enableButton(R.id.bConnectDevice, false);
				enableProgressBar(true);
				
				bluetoothClient.searchDevices();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Event Button bSearchDevicesCLick event
	 * @param view
	 */
	public void bConnectClick(View view) {
		if(bluetoothClient != null){
			try {
				enableButton(R.id.bSearchDevices, false);
				enableButton(R.id.bConnectDevice, false);

				enableProgressBar(true);
				
				
				bluetoothClient.manageConnection(deviceToConnect);
				
				changeActivity();
				
			} catch (IOException e) {
				// TODO Imprimir un mensaje con el error
				Toast.makeText(getApplicationContext(), "There was an error connecting with the bluetooth device", Toast.LENGTH_LONG);
				enableButton(R.id.bSearchDevices, true);
				enableButton(R.id.bConnectDevice, true);
				enableProgressBar(false);
			}
		}
	}
	 
}
