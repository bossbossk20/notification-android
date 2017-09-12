package asia.sendit.testnoti;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Process;

import static android.content.ContentValues.TAG;


public class FloatingViewService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private Handler handler;
    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand");

        try{
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            msg.obj = intent;
            mServiceHandler.sendMessage(msg);
        }catch (Exception e){
            Log.d(TAG, e.getMessage()+"");
        }
        return START_STICKY;
    }

    @Override
    public void onCreate()
    {
        Log.d(TAG, "onCreate");
        handler = new Handler(Looper.getMainLooper());
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

//        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    /**
     * Detect if the floating view is collapsed or expanded.
     *
     * @return true if the floating view is collapsed.
     */
    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

     ////////////////////////////////////////////////////////////////
    private final class ServiceHandler extends Handler {
        private ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            Log.d(TAG, "handleMessage start listener");
            if( mFloatingView != null ){
                mFloatingView.setVisibility(View.GONE);
            }
            mFloatingView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_drawing, null);
            Intent intent = (Intent) msg.obj;
            if( intent != null ){
                Toast.makeText(getApplicationContext(), intent.getStringExtra("txt")+"", Toast.LENGTH_SHORT).show();
                TextView drawOverText = (TextView) mFloatingView.findViewById(R.id.drawOverId);
                drawOverText.setText(intent.getStringExtra("txt")+"");

            }else{
                Toast.makeText(getApplicationContext(), "Some thing error", Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "handleMessage");
            //Add the view to the window.
            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            //Specify the view position
            params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
            params.x = 0;
            params.y = 100;

            //Add the view to the window
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);

            //The root element of the collapsed view layout
            final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);

            //Set the close button
            ImageView closeButtonCollapsed = (ImageView) mFloatingView.findViewById(R.id.close_btn);
            closeButtonCollapsed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //close the service and remove the from from the window
                    mFloatingView.setVisibility(View.GONE);
                }
            });

            ImageButton gotoApp = (ImageButton) mFloatingView.findViewById(R.id.gotoApp);
            gotoApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    stopSelf();
                }
            });

            //Drag and move floating view using user's touch action.
            mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            //remember the initial position.
                            initialX = params.x;
                            initialY = params.y;

                            //get the touch location
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            return true;
                        case MotionEvent.ACTION_UP:
                            int Xdiff = (int) (event.getRawX() - initialTouchX);
                            int Ydiff = (int) (event.getRawY() - initialTouchY);
                            //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                            //So that is click event.
                            if (Xdiff < 10 && Ydiff < 10) {
                                if (isViewCollapsed()) {
                                    collapsedView.setVisibility(View.VISIBLE);
                                }
                            }
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            //Calculate the X and Y coordinates of the view.
                            params.x = initialX + (int) (event.getRawX() - initialTouchX);
                            params.y = initialY + (int) (event.getRawY() - initialTouchY);

                            //Update the layout with new X & Y coordinate
                            mWindowManager.updateViewLayout(mFloatingView, params);
                            return true;
                    }
                    return false;
                }
            });
        }
    }
}



