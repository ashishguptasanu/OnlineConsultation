package consultation.online.rst.com.onlineconsultation.Activities;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

import consultation.online.rst.com.onlineconsultation.R;


public class PaymentResponse extends AppCompatActivity {
    ImageView successImage;
    TextView referenceNum, tvDate, tvTime, ammount;
    SharedPreferences sharedPreferences;
    Button buttonBack;
    String localTime, formattedDate, IMEI, androidID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_response);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
        Date currentLocalTime = cal.getTime();
        DateFormat date = new SimpleDateFormat("HH:mm a");
        date.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        localTime = date.format(currentLocalTime);
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c.getTime());
        referenceNum = (TextView)findViewById(R.id.tv_reference_number);
        tvDate = (TextView)findViewById(R.id.tv_date_response);
        tvTime = (TextView)findViewById(R.id.tv_time_response);
        ammount = (TextView)findViewById(R.id.tv_amount_response);
        tvDate.setText( String.valueOf(formattedDate));
        tvTime.setText(localTime);
        if(sharedPreferences != null && sharedPreferences.getInt("visa_id",0) == 102){
            ammount.setText("USD " + String.valueOf(sharedPreferences.getString("applicant_consultation_fee","")));
            referenceNum.setText(sharedPreferences.getString("order_id",""));
        }
        else{

            String totalFee = ("USD " + (sharedPreferences.getFloat("govt_fee", (float) 0.0) + sharedPreferences.getFloat("service_fee", (float) 0.0)));
            ammount.setText(totalFee);
            referenceNum.setText(sharedPreferences.getString("response",""));
            IMEI = Build.SERIAL;
            androidID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if(Objects.equals(sharedPreferences.getString("utm_source", ""), "affiliate")){
                loadIframe(sharedPreferences.getString("response",""), String.valueOf((sharedPreferences.getFloat("govt_fee", (float) 0.0) + sharedPreferences.getFloat("service_fee", (float) 0.0))),(sharedPreferences.getString("visa_name","")+ " " + sharedPreferences.getString("service_type","")) , IMEI, androidID);
            }

        }
            }

    private void showToast(String s){
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
    }
    private void loadIframe(String orderId, String ammount, String category, String IMEI, String androidId) {
        String visaType = "";
        if(sharedPreferences.getInt("visa_id",0)== 9){
            visaType = "UAE Visa";
        }else if(sharedPreferences.getInt("visa_id",1) == 231){
            visaType = "USA Visa";
        }else if(sharedPreferences.getInt("visa_id",1) == 190){
            visaType = "Singapore Visa";
        }else if(sharedPreferences.getInt("visa_id",1) == 167){
            visaType = "Oman Visa";
        }else if(sharedPreferences.getInt("visa_id",1) == 104){
            visaType = "Iran Visa";
        }
        String iFrameNew = "<IFRAME SRC = \"https://visa.trackbridge.com//catch/?p=4.21&order={transaction_id:"+orderId+"}&user_id="+IMEI+"&device_id="+androidId+"&amount="+ammount+"&travel_date="+(sharedPreferences.getString("arrival_date","")+ " to " + sharedPreferences.getString("departure_date",""))+"&applied_on="+formattedDate+"("+localTime+")"+"&pasport_no="+sharedPreferences.getString("passport_number","")+"&contact_no="+sharedPreferences.getString("mobile","")+"&applicant_name="+sharedPreferences.getString("full_name","")+"&applied_from="+sharedPreferences.getString("nationality_name","")+"&payment_status=Success&category="+category+"&device_type=Mobile&visa_type="+visaType+"&living_in="+sharedPreferences.getString("living_in_country","")+"&email="+sharedPreferences.getString("email","")+"&device_OS=Android&currency=USD&utm_value="+sharedPreferences.getString("utm_value","")+"\"HEIGHT = \"1\" WIDTH = \"1\" SCROLLING = \"NO\" FRAMEBORDER = \"0\"><IFRAME>";
        WebView mWebView = new WebView(getApplicationContext());
        mWebView.clearCache(true);
        Toast.makeText(getApplicationContext(),"Successfully started Iframe", Toast.LENGTH_SHORT).show();
        mWebView.loadData(iFrameNew, "text/html", null);
    }
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finishAffinity();
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;

        }
    }

}
