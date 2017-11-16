package consultation.online.rst.com.onlineconsultation.Activities;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import consultation.online.rst.com.onlineconsultation.R;

public class HomeActivity extends AppCompatActivity {
    ImageView serviceImg1, serviceImg2, serviceImg3, serviceImg4, serviceImg5, serviceImg6;
    private static String IMG_URL1 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fflr.png?alt=media&token=8e5baad8-68e0-4774-bae6-2a926816d748";
    private static String IMG_URL2 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fbecome_nurologist.png?alt=media&token=0af0b470-765f-4c43-b9e0-78816d7ed755";
    private static String IMG_URL3 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fbuy_gems.png?alt=media&token=fc6d7393-24fa-4c05-830f-ffbeea56d608";
    private static String IMG_URL4 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fvcs.png?alt=media&token=dfb7fe1b-eee3-4e39-87e2-cdd39790559a";
    private static String IMG_URL5 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fpcs.png?alt=media&token=f5048b53-d7c9-42d1-906c-c4edcd6fb920";
    private static String IMG_URL6 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fbcs.png?alt=media&token=31d97b3d-e844-4e5c-8abf-60e65a1dd66e";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        initViews();
    }

    private void initViews() {
        serviceImg1 = (ImageView)findViewById(R.id.service_img1);
        serviceImg2 = (ImageView)findViewById(R.id.service_img2);
        serviceImg3 = (ImageView)findViewById(R.id.service_img3);
        serviceImg4 = (ImageView)findViewById(R.id.service_img4);
        serviceImg5 = (ImageView)findViewById(R.id.service_img5);
        serviceImg6 = (ImageView)findViewById(R.id.service_img6);
        Picasso.with(getApplicationContext()).load(IMG_URL1).into(serviceImg1);
        Picasso.with(getApplicationContext()).load(IMG_URL2).into(serviceImg2);
        Picasso.with(getApplicationContext()).load(IMG_URL3).into(serviceImg3);
        Picasso.with(getApplicationContext()).load(IMG_URL4).into(serviceImg4);
        Picasso.with(getApplicationContext()).load(IMG_URL5).into(serviceImg5);
        Picasso.with(getApplicationContext()).load(IMG_URL6).into(serviceImg6);

    }
}
