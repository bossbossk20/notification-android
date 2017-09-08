package asia.sendit.testnoti;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Message";
    private static Handler handler = new Handler();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        final String text = remoteMessage.getData().get("txt");
        Log.d("noti", text+"");
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
