package consultation.online.rst.com.onlineconsultation.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;
import consultation.online.rst.com.onlineconsultation.R;


public class WebViewActivity extends AppCompatActivity {
    WebView wv1;
    String url;
    MKLoader crpv;
    TextView tvProgress, tvLoading;
    String firebaseToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);
        url = getIntent().getStringExtra("url_web_view");
        if(getIntent().getStringExtra("label") != null){
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getIntent().getStringExtra("label"));
        }
        tvLoading = (TextView)findViewById(R.id.tv_loading_webview);
        crpv = (MKLoader) findViewById(R.id.progress_web_view);
        wv1 = (WebView)findViewById(R.id.web_view);
        wv1.getSettings().setLoadsImagesAutomatically(true);
        wv1.getSettings().setJavaScriptEnabled(true);
        wv1.getSettings().setAppCacheEnabled(true);
        wv1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wv1.setWebViewClient(new MyBrowser());
        firebaseToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("token",firebaseToken);
        String postData = null;
        try {
            postData = "device_type=" + URLEncoder.encode("android", "UTF-8") + "&firebase_token=" + URLEncoder.encode(firebaseToken, "UTF-8");
            Log.d("dataPosted","True");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        wv1.postUrl(url,postData.getBytes());

        wv1.setWebChromeClient(new WebChromeClient() {
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
            public void onProgressChanged(WebView view, int progress) {

                /*Log.d("Progress", String.valueOf(progress));
                view.setVisibility(View.GONE);
                crpv.setVisibility(View.VISIBLE);
                tvLoading.setVisibility(View.VISIBLE);
                *//*view.loadUrl("javascript:(function() { " +
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
                        "})()");*/
                if(progress == 100){
                    injectJavaScript(view);
                    /*while(!injectJavaScript(view)){
                        crpv.setVisibility(View.GONE);
                        tvProgress.setVisibility(View.GONE);
                        tvLoading.setVisibility(View.GONE);
                        view.setVisibility(View.VISIBLE);
                    }*/
                }
            }
        });
        wv1.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setType("application/pdf");
                i.setData(Uri.parse(url));
                Log.d("URL",url);
                startActivity(i);
            }
        });
        wv1.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                if(Objects.equals("https://sss-numerologist.com/", url)){
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
                view.loadUrl("javascript:(function() { " +
                        "var head = document.getElementsByTagName('nav')[0];"
                        + "head.parentNode.removeChild(head);" + "console.log('ashish');" +
                        "})()");
                view.loadUrl("javascript:(function() { " +
                        "var footer = document.getElementsByTagName('footer')[0];"
                        + "footer.parentNode.removeChild(footer);" +
                        "})()");
                /*view.loadUrl("javascript:(function() { " +
                        "var nav = document.getElementsByTagName('nav')[0];"
                        + "nav.parentNode.removeChild(nav);" +
                        "})()");
                view.loadUrl("javascript:(function() { " +
                        "var set = document.getElementsByClassName('banner');"
                        + "set[0].style.margin = '0px';" +
                        "})()");*/
                crpv.setVisibility(View.GONE);
                tvLoading.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
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
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String postData = null;
            try {
                postData = "device_type=" + URLEncoder.encode("android", "UTF-8") + "&firebase_token=" + URLEncoder.encode(firebaseToken, "UTF-8");
                Log.d("dataPosted","True");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            wv1.postUrl(url,postData.getBytes());
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
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    private boolean injectJavaScript(WebView view){
        view.loadUrl("javascript:(function() { " +
                "var head = document.getElementsByTagName('nav')[0];"
                + "head.parentNode.removeChild(head);" + "console.log('ashish');"+
                "})()");
        view.loadUrl("javascript:(function() { " +
                "var footer = document.getElementsByTagName('footer')[0];"
                + "footer.parentNode.removeChild(footer);" +
                "})()");
        /*view.loadUrl("javascript:(function() { " +
                "var nav = document.getElementsByTagName('nav')[0];"
                + "nav.parentNode.removeChild(nav);" +
                "})()");
        view.loadUrl("javascript:(function() { " +
                "var set = document.getElementsByClassName('banner');"
                + "set[0].style.margin = '0px';" +
                "})()");*/
        return true;
    }
}
