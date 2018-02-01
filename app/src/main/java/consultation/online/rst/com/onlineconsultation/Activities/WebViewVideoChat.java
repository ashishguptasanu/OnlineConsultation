package consultation.online.rst.com.onlineconsultation.Activities;


import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.tuyenmonkey.mkloader.MKLoader;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import consultation.online.rst.com.onlineconsultation.APIs.TimeSlotContract;
import consultation.online.rst.com.onlineconsultation.APIs.TimeSlotPresenter;
import consultation.online.rst.com.onlineconsultation.R;
import pub.devrel.easypermissions.EasyPermissions;

public class WebViewVideoChat extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, TimeSlotContract.View {
    WebView wv1;
    String url, order_id;
    MKLoader crpv;
    TextView tvProgress, tvLoading;
    DatabaseReference mDatabase;
    AudioManager mgr=null;
    SharedPreferences sharedPreferences;
    TimeSlotPresenter timeSlotPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        mgr=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        methodRequiresTwoPermission();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        if(r != null){
            r.stop();
        }
    }
    private void loadViews() {
        url = getIntent().getStringExtra("url_web_view");
        if(getIntent().getExtras().containsKey("order_id")){
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        }
        order_id = getIntent().getStringExtra("order_id");
        //Log.d("Database Key", FirebaseDatabase.getInstance().getReference().child("time_slots").child(getIntent().getStringExtra("order_id")).child("isCallInitiated").getKey());
        mDatabase = FirebaseDatabase.getInstance().getReference("time_slots");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(getIntent().getStringExtra("label") != null){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getIntent().getStringExtra("label"));
        }
        if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        tvLoading = (TextView)findViewById(R.id.tv_loading_webview);
        crpv = (MKLoader) findViewById(R.id.progress_web_view);
        wv1 = (WebView)findViewById(R.id.web_view);
        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.getSettings().setAppCacheEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.setWebViewClient(new MyBrowser());
        wv1.loadUrl(url);
        timeSlotPresenter = new TimeSlotPresenter(this);
        final String processName = sharedPreferences.getString("process_name","");
        sharedPreferences.edit().putString("reject_status","").apply();
        sharedPreferences.edit().putString("order_id_firebase","").apply();
        sharedPreferences.edit().putInt("ignored",1).apply();
        timeSlotPresenter.timeSlotTask(order_id, processName, "2","1", FirebaseInstanceId.getInstance().getToken());
        wv1.setWebChromeClient(new WebChromeClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
            public void onProgressChanged(WebView view, int progress) {
                view.setVisibility(View.GONE);
                crpv.setVisibility(View.VISIBLE);
                tvLoading.setVisibility(View.VISIBLE);
                view.loadUrl("javascript:(function() { " +
                        "var head = document.getElementsByTagName('header')[0];"
                        + "head.parentNode.removeChild(head);" + "console.log('ashish');" +
                        "})()");
                view.loadUrl("javascript:(function() { " +
                        "var footer = document.getElementsByTagName('footer')[0];"
                        + "footer.parentNode.removeChild(footer);" +
                        "})()");
                view.loadUrl("javascript:(function() { " +
                        "var nav = document.getElementsByTagName('nav')[0];"
                        + "nav.parentNode.removeChild(nav);" +
                        "})()");
                view.loadUrl("javascript:(function() { " +
                        "var set = document.getElementsByClassName('banner');"
                        + "set[0].style.margin = '0px';" +
                        "})()");
                if(progress == 100){
                    while(!injectJavaScript(view)){
                        crpv.setVisibility(View.GONE);
                        tvProgress.setVisibility(View.GONE);
                        tvLoading.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        wv1.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                if(Objects.equals("https://appear.in/", url)){
                    wv1.destroy();
                    finish();
                    timeSlotPresenter.timeSlotTask(order_id, processName, "2","4", FirebaseInstanceId.getInstance().getToken());
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);

                }
                else{
                    injectCSS();
                    view.loadUrl("javascript:(function() { " +
                            "var head = document.getElementsByTagName('header')[0];"
                            + "head.parentNode.removeChild(head);" + "console.log('ashish');" +
                            "})()");
                    view.loadUrl("javascript:(function() { " +
                            "var footer = document.getElementsByTagName('footer')[0];"
                            + "footer.parentNode.removeChild(footer);" +
                            "})()");
                    view.loadUrl("javascript:(function() { " +
                            "var nav = document.getElementsByTagName('nav')[0];"
                            + "nav.parentNode.removeChild(nav);" +
                            "})()");
                    view.loadUrl("javascript:(function() { " +
                            "var set = document.getElementsByClassName('banner');"
                            + "set[0].style.margin = '0px';" +
                            "})()");
                    crpv.setVisibility(View.GONE);
                    tvLoading.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_view_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_home) {
            mDatabase.child(order_id).child("isCallInitiated").setValue(4);
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaskSuccess(String message) {

    }

    @Override
    public void onTaskFailure(String message) {

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (wv1.canGoBack()) {
                        wv1.goBack();
                    } else {
                        mDatabase.child(order_id).child("isCallInitiated").setValue(4);
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    private boolean injectJavaScript(WebView view){
        view.loadUrl("javascript:(function() { " +
                "var head = document.getElementsByTagName('header')[0];"
                + "head.parentNode.removeChild(head);" + "console.log('ashish');"+
                "})()");
        view.loadUrl("javascript:(function() { " +
                "var footer = document.getElementsByTagName('footer')[0];"
                + "footer.parentNode.removeChild(footer);" +
                "})()");
        view.loadUrl("javascript:(function() { " +
                "var nav = document.getElementsByTagName('nav')[0];"
                + "nav.parentNode.removeChild(nav);" +
                "})()");
        view.loadUrl("javascript:(function() { " +
                "var set = document.getElementsByClassName('banner');"
                + "set[0].style.margin = '0px';" +
                "})()");
        return true;
    }
    private void injectCSS() {
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.style);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            wv1.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
            wv1.loadUrl("javascript:(function() { " +
                    "$(\"document.querySelector(\".jstest-cat-img._34LMY4SPe-4YhTdU-IWzqy\");\").attr(\"src\", \"https://s3.amazonaws.com/poly-screenshots.angel.co/enhanced_screenshots/259049-original.png\");" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        wv1.destroy();
        mDatabase.child(order_id).child("isCallInitiated").setValue(4);
        finish();
    }
    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {
            loadViews();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "Allow access for Camera & Microphone to start a video chat.",
                    0, perms);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        loadViews();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(getApplicationContext(), "Permission Denied",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        mDatabase.child(order_id).child("isCallInitiated").setValue(4);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // mDatabase.child(order_id).child("isCallInitiated").setValue(4);
    }
}

