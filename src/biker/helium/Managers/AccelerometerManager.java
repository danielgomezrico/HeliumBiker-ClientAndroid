package biker.helium.Managers;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Abstract the use of the acceleromether on a android device allowing an object
 * to recieve accel changes implementing @see(AccelerometerListener) object
 *
 */
public class AccelerometerManager implements SensorEventListener{

	private static final int DELAY_ACCELEROMETHER = SensorManager.SENSOR_DELAY_GAME;
	
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final IAccelerometerObserver listener;
    private boolean isRegistered;//If the AccelerometherManager has already been registered to listen
    
    /**
     * 
     * @param activity must implement AccelerometerListener to listen
     * @throws Exception if activity does not implement AccelerometerListener
     */
	public AccelerometerManager(Activity activity) throws Exception{
		this.listener = (IAccelerometerObserver) activity;
		
		if(listener == null){
			throw new Exception("The paramether activity does not implement AccelerometerListener");
		}
		
        mSensorManager = (SensorManager)activity.getSystemService(Activity.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        
        isRegistered = false;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {
		listener.changeAcceleromether(arg0.values[0], arg0.values[1]);
	}
	
	/**
	 * Start to listen the acceleromether events
	 */
	public void startListen(){
		if(!isRegistered){
			mSensorManager.registerListener(this, mAccelerometer, DELAY_ACCELEROMETHER);
			isRegistered = true;
		}
	}
	
	/**
	 * Make the manager to stops listen the acceleromether changes
	 */
	public void stopListen(){
        mSensorManager.unregisterListener(this);
        isRegistered = false;
	}
	
}
