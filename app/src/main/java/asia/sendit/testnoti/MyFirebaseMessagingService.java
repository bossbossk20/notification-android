package asia.sendit.testnoti;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Message";
    private static Handler handler = new Handler();
    public static String messageText = "";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final String text = remoteMessage.getData().get("txt");
        final String box = remoteMessage.getData().get("box");
        messageText = text;
        Log.d("noti", text+"");
        sendNotification(text, box);
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                //startService(new Intent(MyFirebaseMessagingService.this, FloatingViewService.class));

                Intent intent = new Intent(getApplicationContext(), FloatingViewService.class);
                intent.putExtra("txt", text);
                startService(intent);
            }
        });
    }
    private void sendNotification(String txt, String box) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("message", box);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(txt)
                .setContentText(box)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
