package consultation.online.rst.com.onlineconsultation.Network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Objects;

import consultation.online.rst.com.onlineconsultation.Activities.OnlineConsultation;
import consultation.online.rst.com.onlineconsultation.Activities.WebViewVideoChat;
import consultation.online.rst.com.onlineconsultation.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    long[] pattern = new long[]{250,250,250,250};
    DatabaseReference mDatabase;
    AudioManager mgr=null;
    Ringtone r;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            String title = remoteMessage.getData().get("title");
            String data = remoteMessage.getData().get("order_id");
            String username = remoteMessage.getData().get("username");
            String uid = remoteMessage.getData().get("uid");
            String fcmToken = remoteMessage.getData().get("fcm_token");
            Log.d("data", title + "=" + data);
            String id = remoteMessage.getData().get("id");
            if (Objects.equals(id, "1")) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(0);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                if (r != null) {
                    r.stop();
                }
                mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                mgr.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            } else if (Objects.equals(id, "0")) {
                /*Intent intent = new Intent(getApplicationContext(), IncomingCallScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                sendCallHead(title, data);
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
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,"MyLock");
            wl.acquire(10000);
            PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

            wl_cpu.acquire(10000);
        }
    }

    private void sendCallHead(String title, final String data) {
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent acceptIntent = new Intent(this, WebViewVideoChat.class);
        acceptIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        acceptIntent.putExtra("url_web_view",title);
        acceptIntent.putExtra("label","Video Consultation");
        acceptIntent.putExtra("order_id",data);
        acceptIntent.setAction("international.rst.com.rstsimplified.Activities.FullscreenActivity");
        PendingIntent acceptPendingIntent = PendingIntent.getActivity(this, 12345, acceptIntent, PendingIntent.FLAG_ONE_SHOT);
        Intent rejectIntent = new Intent(this, OnlineConsultation.class);
        acceptIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        acceptIntent.putExtra("order_id",data);
        rejectIntent.setAction("international.rst.com.rstsimplified.Activities.FullscreenActivity");
        PendingIntent rejectPendingIntent = PendingIntent.getActivity(this,  12346, rejectIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.call_icon);
        builder.setContentTitle("R.Online Consultation");
        builder.setContentText("Incoming via R.");
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
                        mDatabase.child(data).child("isCallInitiated").setValue(3);
                        // Preference_Manager.getInstance(mCtx).deleteKeyMessageid(NOTIFICATION);
                    }
                }, 25000/*howMany */);
            }
        });
    }




}
