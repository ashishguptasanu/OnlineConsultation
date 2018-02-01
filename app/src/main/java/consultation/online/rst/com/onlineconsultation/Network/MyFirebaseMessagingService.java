package consultation.online.rst.com.onlineconsultation.Network;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Objects;

import consultation.online.rst.com.onlineconsultation.APIs.TimeSlotContract;
import consultation.online.rst.com.onlineconsultation.APIs.TimeSlotPresenter;
import consultation.online.rst.com.onlineconsultation.Activities.HomeActivity;
import consultation.online.rst.com.onlineconsultation.Activities.OnlineConsultation;
import consultation.online.rst.com.onlineconsultation.Activities.WebViewVideoChat;
import consultation.online.rst.com.onlineconsultation.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService implements TimeSlotContract.View {
    private static final String TAG = "MyFirebaseMsgService";
    long[] pattern = new long[]{250,250,250,250};
    DatabaseReference mDatabase;
    AudioManager mgr=null;
    SharedPreferences sharedPreferences;
    TimeSlotPresenter timeSlotPresenter;
    Ringtone r;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        timeSlotPresenter = new TimeSlotPresenter(this);
        if (remoteMessage.getData().size() > 0) {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String title = remoteMessage.getData().get("title");
            String data = remoteMessage.getData().get("order_id");
            String username = remoteMessage.getData().get("username");
            String uid = remoteMessage.getData().get("uid");
            String fcmToken = remoteMessage.getData().get("fcm_token");
            String id = remoteMessage.getData().get("id");
            String processId = remoteMessage.getData().get("process");
            String processName = remoteMessage.getData().get("processName");
            if(Objects.equals(id, "1")){
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                if(r != null){
                    r.stop();
                }
                sharedPreferences.edit().putInt("ignored",1).apply();
                mgr=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
                mgr.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mgr.setStreamVolume(AudioManager.STREAM_RING, 7, AudioManager.FLAG_ALLOW_RINGER_MODES);
                            }
                        }, 25000/*howMany */);
                    }
                });
            }else if(Objects.equals(id,"0")){
                sharedPreferences.edit().putString("reject_status","").apply();
                /*Intent intent = new Intent(getApplicationContext(), IncomingCallScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                sharedPreferences.edit().putString("process_id",processId).apply();
                sharedPreferences.edit().putString("order_id_firebase",data).apply();
                //Log.d("Order_id_firebase",data);
                sharedPreferences.edit().putString("process_name",processName).apply();
                sendCallHead(title, data, processId, processName);
                sharedPreferences.edit().putInt("ignored",0).apply();
                mDatabase = FirebaseDatabase.getInstance().getReference("time_slots");
                screenOn();
            }
        }
    }
    private void screenOn() {
        PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        if(isScreenOn==false)
        {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");
            wl_cpu.acquire(10000);
        }
    }
    private void sendCallHead(String title, final String data, final String processId, final String processName) {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent acceptIntent = new Intent(this, WebViewVideoChat.class);
        acceptIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        acceptIntent.putExtra("url_web_view",title);
        acceptIntent.putExtra("label","Video Consultation");
        acceptIntent.putExtra("order_id",data);
        acceptIntent.putExtra("process_id",processId);
        acceptIntent.setAction("international.rst.com.rstsimplified.Activities.HomeActivity");
        PendingIntent acceptPendingIntent = PendingIntent.getActivity(this, 12345, acceptIntent, PendingIntent.FLAG_ONE_SHOT);
        Intent rejectIntent = new Intent(this, HomeActivity.class);
        acceptIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        acceptIntent.putExtra("order_id",data);
        rejectIntent.setAction("international.rst.com.rstsimplified.Activities.HomeActivity");
        PendingIntent rejectPendingIntent = PendingIntent.getActivity(this,  12346, rejectIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.call_icon);
        builder.setContentTitle("SSS-Numerologist");
        builder.setContentText("Incoming via Online Consultation");
        builder.setAutoCancel(true);
        builder.setOngoing(true);
        builder.setVibrate(pattern);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        r = RingtoneManager.getRingtone(getApplicationContext(),uri);
        r.play();
        builder.setSound(uri);
        builder.addAction(R.drawable.reject, "Reject", rejectPendingIntent);
        builder.addAction(R.drawable.call_icon, "Accept", acceptPendingIntent);
        notificationManager.notify(0, builder.build());
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notificationManager.cancel(0);
                        r.stop();
                        if(sharedPreferences.getString("reject_status","").length() > 3){
                            Log.d("Call Status Service","MyService Rejected");
                            //timeSlotPresenter.timeSlotTask(data, processName,"2","2", FirebaseInstanceId.getInstance().getToken());
                        }else{
                            Log.d("reject status service", sharedPreferences.getString("reject_status",""));
                            if(sharedPreferences.getInt("ignored",0) == 0){
                                timeSlotPresenter.timeSlotTask(data, processName,"2","3", FirebaseInstanceId.getInstance().getToken());
                            }

                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mgr=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
                                            mgr.setStreamVolume(AudioManager.STREAM_RING, 7, AudioManager.FLAG_ALLOW_RINGER_MODES);
                                        }
                                    }, 25000/*howMany */);
                                }
                            });
                        }

                    }
                }, 25000/*howMany */);
            }
        });
    }

    @Override
    public void onTaskSuccess(String message) {
        Log.d("Messaging Service", message);
    }
    @Override
    public void onTaskFailure(String message) {
        Log.d("Messaging Service", message);
    }




}
