package asia.sendit.testnoti;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        findViewById(R.id.subscribeButton).setOnClickListener(this);
        findViewById(R.id.unsubscribeButton).setOnClickListener(this);
        findViewById(R.id.logTokenButton).setOnClickListener(this);
//        findViewById(R.id.imgGo).setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            initializeView();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subscribeButton:
                FirebaseMessaging.getInstance().subscribeToTopic("test-koykoy");
                Log.d(TAG, "SubscribeToTopic");
                Toast.makeText(MainActivity.this, "SubscribeToTopic", Toast.LENGTH_SHORT).show();
                break;
            case R.id.unsubscribeButton:
                FirebaseMessaging.getInstance().unsubscribeFromTopic("test-koykoy");
                Log.d(TAG, "UnsubscribeFromTopic");
                Toast.makeText(MainActivity.this, "UnsubscribeFromTopic", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logTokenButton:
                String token = FirebaseInstanceId.getInstance().getToken();
                Log.d(TAG, "Token : " + token);
                Toast.makeText(MainActivity.this, "Token : " + token, Toast.LENGTH_SHORT).show();
                break;
//            case R.id.imgGo:
//                Intent goPage = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(goPage);
        }
    }

    private void initializeView() {
        findViewById(R.id.notify_me).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test", "clicked drawing overrrrrrrr");
                startService(new Intent(MainActivity.this, FloatingViewService.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {

            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK) {
                initializeView();
            } else { //Permission is not available
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
