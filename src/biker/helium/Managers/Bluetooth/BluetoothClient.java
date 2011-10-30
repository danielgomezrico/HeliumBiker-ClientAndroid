package biker.helium.Managers.Bluetooth;

import java.io.IOException;


public class BluetoothClient extends BluetoothManager {

	public final static int MESSAGE_SIZE = 30;
	
	/**
	 * Reference a type of a message
	 */
	public enum MessageType {
		  A, P, S
		}
	
	public BluetoothClient(IBluetoothObserver mainActivity) throws IOException {
		super(mainActivity);
	}
	
	/**
	 * Fill the message with spaces if the message length
	 * is mayor than @see(MESSAGE_SIZE) constant
	 * @param message
	 * @return
	 */
	private String fillMessage(String message){
		int difference = MESSAGE_SIZE - message.length();
		
		for (int i = 0; i < difference; i++) {
			message = message.concat(" ");
		}
		
		return message;
	}
	
	/**
	 * Send a message to the connected device
	 * @param type type of the message
	 * @param x change in x accelerometer
	 * @param y change in y accelerometer
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean send(MessageType type, float x, float y){
		String message = new StringBuilder(type.toString()).append(' ').append(y).append(' ').append(x).toString();
//		Log.d("**P", String.valueOf(message.length()) + " " + message);
		if(message.length() < MESSAGE_SIZE){
			message = fillMessage(message);
		}

		return sendMessage(message);
	}
	
}
