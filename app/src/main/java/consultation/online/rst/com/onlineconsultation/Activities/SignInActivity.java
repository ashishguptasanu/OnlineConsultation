package consultation.online.rst.com.onlineconsultation.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import consultation.online.rst.com.onlineconsultation.R;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class SignInActivity extends AppCompatActivity implements View.OnClickListener{
    TextView tvSingUp, tvLogin, tvErrorLogin, tvErrorSignup;
    LinearLayout layoutLogin, layoutSignUp;
    EditText loginEmail, loginPassword, firstNameSignup, numberSignup, emailSignup, passwordSignup1, passwordSignup2;
    Button btnLogin, btnSignup;
    int chatStatus;
    ProgressDialog progress;
    SharedPreferences sharedPreferences;
    CountryCodePicker ccp;
    String firebaseToken;
    OkHttpClient client = new OkHttpClient();
    private static String SIGNUP_URL = "https://testing.sss-numerologist.com/signup/api";
    private static String LOGIN_URL = "https://testing.sss-numerologist.com/login/api";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_sign_in);
        //chatStatus = getIntent().getIntExtra("chat",0);
        initializeViews();
        firebaseToken = FirebaseInstanceId.getInstance().getToken();
        ccp = (CountryCodePicker) findViewById(R.id.ccp);


    }
    private void initializeViews() {
        loginPassword = (EditText)findViewById(R.id.edt_password_login);
        firstNameSignup = (EditText)findViewById(R.id.edt_first_name_signup);
        numberSignup = (EditText)findViewById(R.id.edt_phone_number_signup);
        emailSignup = (EditText)findViewById(R.id.edt_email_signup);
        passwordSignup1 = (EditText)findViewById(R.id.edt_password_signup1);
        passwordSignup2 = (EditText)findViewById(R.id.edt_password_signup2);
        loginEmail = (EditText)findViewById(R.id.edt_email_login);
        tvSingUp = (TextView)findViewById(R.id.tv_signup);
        tvLogin = (TextView)findViewById(R.id.tv_login);
        tvLogin.setOnClickListener(this);
        tvSingUp.setOnClickListener(this);
        btnLogin = (Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        btnSignup = (Button)findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(this);
        layoutLogin = (LinearLayout)findViewById(R.id.layout_login);
        layoutSignUp = (LinearLayout)findViewById(R.id.layout_sign_up);
        tvErrorLogin = (TextView)findViewById(R.id.tv_error_login);
        tvErrorSignup = (TextView)findViewById(R.id.tv_error_signup);
        tvErrorLogin.setVisibility(View.GONE);
        tvErrorSignup.setVisibility(View.GONE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_signup:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layoutLogin.setVisibility(View.GONE);
                        layoutSignUp.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case R.id.tv_login:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        layoutLogin.setVisibility(View.VISIBLE);
                        layoutSignUp.setVisibility(View.GONE);
                    }
                });
                break;
            case R.id.btn_login:
                validateLoginData();
                break;
            case R.id.btn_signup:
                validateSignupdata();
                break;
        }
    }
    private void validateSignupdata() {
        if(!TextUtils.isEmpty(firstNameSignup.getText().toString())){
            if(!TextUtils.isEmpty(numberSignup.getText().toString())){
                if(!TextUtils.isEmpty(emailSignup.getText().toString())){
                    if(!TextUtils.isEmpty(passwordSignup1.getText().toString())){
                        if(!TextUtils.isEmpty(passwordSignup2.getText().toString())){
                            if(Patterns.EMAIL_ADDRESS.matcher(emailSignup.getText().toString()).matches()){
                                if(Objects.equals(passwordSignup1.getText().toString(), passwordSignup2.getText().toString())){
                                    progress = ProgressDialog.show(this, "Signing In", "Please wait, Adding your profile..", true);
                                    signUp(firstNameSignup.getText().toString(),numberSignup.getText().toString(), emailSignup.getText().toString(),passwordSignup1.getText().toString(),ccp.getSelectedCountryCodeWithPlus());
                                }else{tvErrorSignup.setVisibility(View.VISIBLE);
                                    tvErrorSignup.setText("Password doesn't match");}
                            }else{emailSignup.setError("Enter the valid email address");}
                        }else{passwordSignup2.setError("Enter the password",null);}
                    }else{passwordSignup1.setError("Enter the password", null);}
                }else{emailSignup.setError("Enter the Email Address");}
            }else {
                numberSignup.setError("Enter the last name");}
        }else{firstNameSignup.setError("Enter the first name");}
    }
    private void signUp(String name, String phone, String email, String password, String code) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("uname", name)
                .addFormDataPart("uemail", email)
                .addFormDataPart("upass", password)
                .addFormDataPart("country_code", code)
                .addFormDataPart("ucontactno", phone)
                .addFormDataPart("visit_ref",firebaseToken)
                .build();
        Request request = new Request.Builder().url(SIGNUP_URL).addHeader("Token", "d75542712c868c1690110db641ba01a").post(requestBody).build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
                         @Override
                         public void onFailure(okhttp3.Call call, IOException e) {
                             progress.dismiss();
                             showToast("Something went wrong!");
                             System.out.println("Registration Error" + e.getMessage());
                         }
                         @Override
                         public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                             String resp = response.body().string();
                             Log.d("resp",resp);

                             if (response.isSuccessful()) {
                                 JSONObject obj = null;
                                 try {
                                     obj = new JSONObject(resp);
                                     JSONObject obj_response=obj.getJSONObject("Response");
                                     final JSONObject obj_status=obj_response.getJSONObject("status");
                                     //
                                     final String msgFinal = obj_status.getString("type");
                                     //final String msgDetail = obj_data.getString("message");
                                     if(Objects.equals(msgFinal, "Success")){
                                         final JSONObject obj_data=obj_response.getJSONObject("data");
                                         if(obj_data.get("type") == "Success"){
                                             progress.dismiss();
                                             showToast("Successfully user added.");
                                         }else{
                                             progress.dismiss();
                                             showToast(obj_data.getString("message"));
                                         }

                                     }else{
                                         runOnUiThread(new Runnable() {
                                             @Override
                                             public void run() {
                                                 progress.dismiss();

                                                 try {
                                                     showToast(obj_status.getString("message"));
                                                 } catch (JSONException e) {
                                                     e.printStackTrace();
                                                 }
                                             }
                                         });
                                     }
                                 } catch (JSONException e) {
                                     showToast("Something went wrong!");
                                     progress.dismiss();
                                     e.printStackTrace();
                                 }
                             }
                         }
                     }
        );
    }
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void validateLoginData() {
        if(!TextUtils.isEmpty(loginEmail.getText().toString())){
            if(!TextUtils.isEmpty(loginPassword.getText().toString())){
                if(Patterns.EMAIL_ADDRESS.matcher(loginEmail.getText().toString()).matches()){
                    progress = ProgressDialog.show(this, "Logging In",
                            "Please wait, Verifying your credentials", true);
                    Login(loginEmail.getText().toString(),loginPassword.getText().toString());
                }else{loginEmail.setError("Email address is not valid");}
            }else{loginPassword.setError("Please enter the password", null);}
        }else{loginEmail.setError("Please enter the email");}
    }
    private void Login(String email, String password) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("login_email_id", email)
                .addFormDataPart("login_password", password)
                .addFormDataPart("visit_ref", firebaseToken)
                .build();
        Request request = new Request.Builder().url(LOGIN_URL).addHeader("Token", "d75542712c868c1690110db641ba01a").post(requestBody).build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
                         @Override
                         public void onFailure(okhttp3.Call call, IOException e) {
                             progress.dismiss();
                             showToast("Something went wrong!");
                             System.out.println("Registration Error" + e.getMessage());
                         }
                         @Override
                         public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                             String resp = response.body().string();
                             Log.d("resp",resp);

                             if (response.isSuccessful()) {
                                 JSONObject obj = null;
                                 try {
                                     obj = new JSONObject(resp);
                                     JSONObject obj_response=obj.getJSONObject("Response");
                                     final JSONObject obj_status=obj_response.getJSONObject("status");
                                     //
                                     final String msgFinal = obj_status.getString("type");
                                     //final String msgDetail = obj_data.getString("message");
                                     if(Objects.equals(msgFinal, "Success")){
                                         final JSONObject obj_data=obj_response.getJSONObject("data");
                                         if(obj_data.get("type") == "success"){
                                             progress.dismiss();
                                             showToast("Successfully Logged In.");
                                         }else{
                                             progress.dismiss();
                                             showToast(obj_data.getString("message"));
                                         }

                                     }else{
                                         runOnUiThread(new Runnable() {
                                             @Override
                                             public void run() {
                                                 progress.dismiss();

                                                 try {
                                                     showToast(obj_status.getString("message"));
                                                 } catch (JSONException e) {
                                                     e.printStackTrace();
                                                 }
                                             }
                                         });
                                     }
                                 } catch (JSONException e) {
                                     showToast("Something went wrong!");
                                     progress.dismiss();
                                     e.printStackTrace();
                                 }
                             }
                         }
                     }
        );
    }

}
