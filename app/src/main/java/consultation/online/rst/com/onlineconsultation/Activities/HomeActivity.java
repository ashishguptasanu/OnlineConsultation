package consultation.online.rst.com.onlineconsultation.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import consultation.online.rst.com.onlineconsultation.APIs.TimeSlotContract;
import consultation.online.rst.com.onlineconsultation.APIs.TimeSlotPresenter;
import consultation.online.rst.com.onlineconsultation.R;

public class HomeActivity extends AppCompatActivity implements TimeSlotContract.View, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    ImageView serviceImg1, serviceImg2, serviceImg3, serviceImg4, serviceImg5, serviceImg6;
    LinearLayout layoutService1, layoutService2, layoutService3, layoutService4, layoutService5, layoutService6;
    SharedPreferences sharedPreferences;
    FirebaseFirestore db;
    TimeSlotPresenter mTimeSlotPresenter;
    AudioManager mgr=null;
    private static String IMG_URL1 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fflr.png?alt=media&token=8e5baad8-68e0-4774-bae6-2a926816d748";
    private static String IMG_URL2 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fbecome_nurologist.png?alt=media&token=0af0b470-765f-4c43-b9e0-78816d7ed755";
    private static String IMG_URL3 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fbuy_gems.png?alt=media&token=fc6d7393-24fa-4c05-830f-ffbeea56d608";
    private static String IMG_URL4 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fvcs.png?alt=media&token=dfb7fe1b-eee3-4e39-87e2-cdd39790559a";
    private static String IMG_URL5 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fpcs.png?alt=media&token=f5048b53-d7c9-42d1-906c-c4edcd6fb920";
    private static String IMG_URL6 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fbcs.png?alt=media&token=31d97b3d-e844-4e5c-8abf-60e65a1dd66e";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mTimeSlotPresenter = new TimeSlotPresenter(this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View mContentView = findViewById(R.id.content_fullscreen);
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        if(sharedPreferences != null && sharedPreferences.getString("order_id_firebase", "").length() > 3){
            mgr=(AudioManager)getSystemService(Context.AUDIO_SERVICE);
            mgr.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            String processId = sharedPreferences.getString("process_id","");
            String orderId = sharedPreferences.getString("order_id_firebase", "");
            String processName = sharedPreferences.getString("process_name","");
            //mDatabase.child(processId).child(orderId).child("isCallInitiated").setValue(2);
            mTimeSlotPresenter.timeSlotTask(orderId, processName,"2","2", FirebaseInstanceId.getInstance().getToken());
            sharedPreferences.edit().putString("reject_status","reject").apply();
        }
        initViews();
    }
    private void initViews() {
        serviceImg1 = findViewById(R.id.service_img1);
        serviceImg2 = findViewById(R.id.service_img2);
        serviceImg3 = findViewById(R.id.service_img3);
        serviceImg4 = findViewById(R.id.service_img4);
        serviceImg5 = findViewById(R.id.service_img5);
        serviceImg6 = findViewById(R.id.service_img6);
        Picasso.with(getApplicationContext()).load(IMG_URL1).into(serviceImg1);
        Picasso.with(getApplicationContext()).load(IMG_URL2).into(serviceImg2);
        Picasso.with(getApplicationContext()).load(IMG_URL3).into(serviceImg3);
        Picasso.with(getApplicationContext()).load(IMG_URL4).into(serviceImg4);
        Picasso.with(getApplicationContext()).load(IMG_URL5).into(serviceImg5);
        Picasso.with(getApplicationContext()).load(IMG_URL6).into(serviceImg6);
        layoutService1 = findViewById(R.id.layout_service1);
        layoutService2 = findViewById(R.id.layout_service2);
        layoutService3 = findViewById(R.id.layout_service3);
        layoutService4 = findViewById(R.id.layout_service4);
        layoutService5 = findViewById(R.id.layout_service5);
        layoutService6 = findViewById(R.id.layout_service6);
        layoutService1.setOnClickListener(this);
        layoutService2.setOnClickListener(this);
        layoutService3.setOnClickListener(this);
        layoutService4.setOnClickListener(this);
        layoutService5.setOnClickListener(this);
        layoutService6.setOnClickListener(this);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutUs.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_full_life_report) {
            Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
            intent.putExtra("url_web_view","https://sss-numerologist.com/services/numerology-reports-predictions");
            intent.putExtra("label","Full Life Report");
            startActivity(intent);
        }
        else if(id == R.id.menu_become_numerologist){
            showToast("Coming Soon");
        }
        else if(id == R.id.menu_buy_gems){
            showToast("Coming Soon");
        }
        else if(id == R.id.menu_vastu){
            Intent intent3 = new Intent(getApplicationContext(), WebViewActivity.class);
            intent3.putExtra("url_web_view","https://sss-numerologist.com/services/vastu-consultation-service");
            intent3.putExtra("label","Vastu Consultation");
            startActivity(intent3);
        }
        else if(id == R.id.menu_personal){
            Intent intent4 = new Intent(getApplicationContext(), WebViewActivity.class);
            intent4.putExtra("url_web_view","https://sss-numerologist.com/services/personal-consultation-service");
            intent4.putExtra("label","Personal Consultation");
            startActivity(intent4);
        }
        else if(id == R.id.menu_business){
            Intent intent5 = new Intent(getApplicationContext(), WebViewActivity.class);
            intent5.putExtra("url_web_view","https://sss-numerologist.com/services/business-consultation-service");
            intent5.putExtra("label","Business Consultation");
            startActivity(intent5);
        }
        else if(id == R.id.menu_contact_us){
            Intent intent6 = new Intent(getApplicationContext(), WebViewActivity.class);
            intent6.putExtra("url_web_view","https://sss-numerologist.com/contact-us");
            intent6.putExtra("label","Contact Us");
            startActivity(intent6);
        }
        else if(id == R.id.menu_sign_in){
            Intent intent6 = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent6);
        }
        else if(id == R.id.menu_sign_out){
            Intent intent6 = new Intent(getApplicationContext(), WebViewActivity.class);
            startActivity(intent6);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_service1:
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("url_web_view","https://sss-numerologist.com/services/numerology-reports-predictions");
                intent.putExtra("label","Reports & Predictions");
                startActivity(intent);
                break;
            case R.id.layout_service2:
                Intent intent1 = new Intent(getApplicationContext(), WebViewActivity.class);
                intent1.putExtra("url_web_view","https://sss-numerologist.com/services/become-a-numerologist");
                intent1.putExtra("label","Become a Numerologist");
                startActivity(intent1);

                break;
            case R.id.layout_service3:
                Intent intent2 = new Intent(getApplicationContext(), WebViewActivity.class);
                intent2.putExtra("url_web_view","https://sss-numerologist.com/services/purchase-gems-online");
                intent2.putExtra("label","Buy Gems");
                startActivity(intent2);

                break;
            case R.id.layout_service4:
                Intent intent3 = new Intent(getApplicationContext(), WebViewActivity.class);
                intent3.putExtra("url_web_view","https://sss-numerologist.com/services/vastu-consultation-service");
                intent3.putExtra("label","Vastu Consultation");
                startActivity(intent3);
                break;
            case R.id.layout_service5:
                Intent intent4 = new Intent(getApplicationContext(), WebViewActivity.class);
                intent4.putExtra("url_web_view","https://sss-numerologist.com/services/personal-consultation-service");
                intent4.putExtra("label","Personal Consultation");
                startActivity(intent4);
                break;
            case R.id.layout_service6:
                Intent intent5 = new Intent(getApplicationContext(), WebViewActivity.class);
                intent5.putExtra("url_web_view","https://sss-numerologist.com/services/business-consultation-service");
                intent5.putExtra("label","Business Consultation");
                startActivity(intent5);
                break;
        }
    }

    private void initFireStore() {
        /*db.collection("images")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });*/
        /*Map<String, Object> user = new HashMap<>();
        user.put("first", "Alan");
        user.put("middle", "Mathison");
        user.put("last", "Turring");
        user.put("born", 1912);*/

// Add a new document with a generated ID
        /*db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });*/
        /*Map<String, Object> user = new HashMap<>();
        user.put("id", "1");
        user.put("data", "Gupta");

// Add a new document with a generated ID
        db.collection("images")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
*/
    }

    private void showToast(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onTaskSuccess(String message) {
        showToast("Success");
    }

    @Override
    public void onTaskFailure(String message) {
        showToast("Failed");
    }
}