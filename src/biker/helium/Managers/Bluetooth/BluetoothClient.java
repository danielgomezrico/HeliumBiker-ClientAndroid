package biker.helium.Managers.Bluetooth;

import java.io.IOException;

import biker.helium.activities.BluetoothActivity;


public class BluetoothClient extends BluetoothManager {

	public final static int MESSAGE_SIZE = 30;
	
	/**
	 * Reference a type of a message
	 */
	public enum MessageType {
		  A, S
		}
	
	public BluetoothClient(BluetoothActivity mainActivity) throws IOException {
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
	public void send(MessageType type, float x, float y) throws IOException, Exception{
		String message = new StringBuilder(type.toString()).append(' ').append(y).append(' ').append(x).toString();

		if(message.length() < MESSAGE_SIZE){
			message = fillMessage(message);
		}

		sendMessage(message);
	}
	
	/**
	 * Send a message to the connected device
	 * @param type type type of the message
	 * @param x coordinate of slingshot release
	 * @param y coordinate of slingshot release
	 * @throws IOException
	 * @throws Exception
	 */
	public void send(MessageType type, int x, int y) throws IOException, Exception{
		String message = new StringBuilder(type.toString()).append(' ').append(x).append(' ').append(y).toString();

		if(message.length() < MESSAGE_SIZE){
			message = fillMessage(message);
		}

		sendMessage(message);
	}

}
