package biker.helium.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class UIHelper {

	public static void showMessageDialog(String message,String title, boolean cancelable, Activity activity){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    	builder.setMessage(message)
    	       .setCancelable(false)
    	       .setTitle(title)
    	       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	               //moveTaskToBack(true);
    	        	   dialog.cancel();
    	           }
    	       });
    	
    	AlertDialog alert = builder.create();
    	alert.setCancelable(cancelable);
		alert.show();
	}
}
