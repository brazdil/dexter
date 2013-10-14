package uk.ac.cam.db538.dexter.aux;

import java.util.Random;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public final class LeakageNotification {

	private final static int NOTIFICATION_ID;
	
	static {
		NOTIFICATION_ID = new Random().nextInt();
	}
	
	private LeakageNotification() { }

	public static void notify(int taint, String taintStr, String sinkType) {
		Context context = DexterApplication.DexterContext;
		
		Notification notification = new Notification.Builder(context)
			.setContentTitle("Dexter: Leakage identified")
			.setContentText(sinkType + ": " + taintStr)
			.setSmallIcon(R.drawable.ic_delete)
			.build();
		
		NotificationManager manager = (NotificationManager) 
			context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		manager.notify(NOTIFICATION_ID, notification);
	}
	
}
