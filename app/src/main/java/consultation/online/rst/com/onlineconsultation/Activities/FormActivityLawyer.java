package consultation.online.rst.com.onlineconsultation.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import consultation.online.rst.com.onlineconsultation.R;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;

public class FormActivityLawyer extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks{
    private static final String POST_URL = "https://uk-immigrationhub.com/v1.1/consultApi/consultapi.php?gofor=consultForm";
    private static  final String FILE_URL ="https://uk-immigrationhub.com/v1.1/consultApi/consultapi.php?gofor=doc_upload";
    EditText genderVideoConsultation, extraInfo, name, mobile, age, uploadDocs, email, modeVideoConsultation;
    OkHttpClient client = new OkHttpClient();
    int selectedGender = 0;
    Button buttonSubmit;
    CheckBox checkBox;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    String fee = "";
    int selectedFileType;
    String consultationModeId = "";
    ArrayList<String> filePaths = new ArrayList<>();
    File file;
    Uri uri ;
    String lastSegment = "", fileType = "", orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_lawyer);
        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }
    private void initViews() {
        genderVideoConsultation = findViewById(R.id.gender_video_consultation);
        genderVideoConsultation.setOnClickListener(this);
        modeVideoConsultation = findViewById(R.id.mode_video_consultation);
        modeVideoConsultation.setOnClickListener(this);
        extraInfo = findViewById(R.id.edt_extra_info_consult);
        name = findViewById(R.id.edt_full_name_consult);
        mobile = findViewById(R.id.edt_mobile_consult);
        age = findViewById(R.id.edt_age_consult);
        uploadDocs = findViewById(R.id.edt_upload_docs_consult);
        uploadDocs.setOnClickListener(this);
        uploadDocs.setFocusable(false);
        buttonSubmit = findViewById(R.id.btn_submit_consult);
        buttonSubmit.setOnClickListener(this);
        checkBox = findViewById(R.id.checkbox_consult);
        email = findViewById(R.id.edt_email_consult);
    }
    private void openBottomSheetGender() {
        new BottomSheet.Builder(this)
                .setSheet(R.menu.bottomsheet)
                .setTitle("Select Your Gender")
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(@NonNull BottomSheet bottomSheet) {
                    }
                    @Override
                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem) {
                        genderVideoConsultation.setText(menuItem.getTitle());
                        selectedGender = menuItem.getItemId();
                        Log.d("Selected Gender", String.valueOf(menuItem.getItemId()));
                    }
                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int i) {
                    }
                })
                .show();
    }
    private void openBottomSheetPickFile(){
        new BottomSheet.Builder(this)
                .setSheet(R.menu.picker)
                .setTitle("Upload File")
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(@NonNull BottomSheet bottomSheet) {
                    }
                    @Override
                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.photo){
                            selectedFileType = 0;
                            methodRequiresPermission();
                        }else if(menuItem.getItemId() == R.id.doc){
                            selectedFileType = 1;
                            methodRequiresPermission();
                        }
                    }
                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int i) {
                    }
                })
                .show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gender_video_consultation:
                openBottomSheetGender();
                break;
            case R.id.mode_video_consultation:
                openBottomSheetMode();

                break;
            case R.id.edt_upload_docs_consult:
                openBottomSheetPickFile();
                break;
            case R.id.btn_submit_consult:
                if(!TextUtils.isEmpty(extraInfo.getText().toString()) && !TextUtils.isEmpty(name.getText().toString())  && !TextUtils.isEmpty(mobile.getText().toString()) && !TextUtils.isEmpty(email.getText().toString()) && !TextUtils.isEmpty(age.getText().toString())){
                    if(checkBox.isChecked()){
                        saveData(name.getText().toString(), genderVideoConsultation.getText().toString(), age.getText().toString(), extraInfo.getText().toString(), email.getText().toString(), mobile.getText().toString());
                        progressDialog = ProgressDialog.show(this,"","Please Wait...");
                        progressDialog.show();
                        postData(extraInfo.getText().toString(), name.getText().toString(), email.getText().toString(), mobile.getText().toString(), age.getText().toString(), genderVideoConsultation.getText().toString());
                    }else{
                        showToast("Allow terms & condition to continue..");
                    }
                }else{
                    showToast("Fields can not be empty");
                }
                break;
        }
    }
    private void openBottomSheetMode() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setMinimumHeight(100);
        final TextView mode1 = new TextView(this);
        final TextView mode2 = new TextView(this);
        final TextView tvConsultationMode = new TextView(this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.setMargins(30, 30, 10, 10); //substitute parameters for left, top, right, bottom
        tvConsultationMode.setTextSize(16);
        tvConsultationMode.setLayoutParams(params2);
        tvConsultationMode.setText("Select Consultation Mode");
        linearLayout.addView(tvConsultationMode);
        linearLayout.addView(mode1);
        linearLayout.addView(mode2);
        mode1.setPadding(60,20,10,20);
        mode2.setPadding(60,20,10,40);
        mode1.setTextColor(Color.parseColor("#000000"));
        mode2.setTextColor(Color.parseColor("#000000"));
        mode1.setTextSize(16);
        mode2.setTextSize(16);
        if(getIntent().getExtras().containsKey("voice") || getIntent().getExtras().containsKey("video")){
            if(!Objects.equals(getIntent().getStringExtra("voice"), 0.0)){
                mode1.setText("Voice Consultation (USD " + getIntent().getExtras().get("voice") + "/hr)");
            }else{
                mode1.setVisibility(View.GONE);
            }
            if(!Objects.equals(getIntent().getExtras().get("video"), 0.0)){
                mode2.setText("Video Consultation (USD " + getIntent().getExtras().get("video") + "/hr)");
            }else{
                mode2.setVisibility(View.GONE);
            }
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 40, 10, 50);
        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        new BottomSheet.Builder(this)
                .setView(linearLayout)
                .setTitle("Select Consultation Mode")
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(@NonNull final BottomSheet bottomSheet) {
                        mode1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomSheet.dismiss();
                                modeVideoConsultation.setText("Voice Consultation");
                                sharedPreferences.edit().putString("consultationModeId","2").apply();
                                consultationModeId = "2";
                                fee = getIntent().getStringExtra("voice");
                            }
                        });
                        mode2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomSheet.dismiss();
                                modeVideoConsultation.setText("Video Consultation");
                                sharedPreferences.edit().putString("consultationModeId","1").apply();
                                consultationModeId = "1";
                                fee = getIntent().getStringExtra("video");
                            }
                        });
                    }
                    @Override
                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem) {
                        genderVideoConsultation.setText(menuItem.getTitle());
                        selectedGender = menuItem.getItemId();
                    }
                    @Override
                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int i) {
                    }
                })
                .show();
    }
    private void saveData(String name, String gender, String age, String extraInfo, String email, String mobile) {
        sharedPreferences.edit().putString("applicant_name_consultation", name).apply();
        sharedPreferences.edit().putString("applicant_gender_consultation",gender).apply();
        sharedPreferences.edit().putString("applicant_age_consultation",age).apply();
        sharedPreferences.edit().putString("applicant_extra_info_consultation",extraInfo).apply();
        sharedPreferences.edit().putString("applicant_consultation_mode", modeVideoConsultation.getText().toString()).apply();
        sharedPreferences.edit().putString("applicant_consultation_fee", fee).apply();
        sharedPreferences.edit().putString("applicant_email_consultation", email).apply();
        sharedPreferences.edit().putString("applicant_mobile_consultation",mobile).apply();
    }
    private void showToast(String s) {
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
    }
    private void postData(String extraInfo, String name, String email, String mobileNumber, String age, String gender){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("name", name)
                .addFormDataPart("email", email)
                .addFormDataPart("mobile_number", mobileNumber)
                .addFormDataPart("age", age)
                .addFormDataPart("gender", gender)
                .addFormDataPart("extra_info", extraInfo)
                .addFormDataPart("consultation_mode",consultationModeId)
                .build();
        Request request = new Request.Builder().url(POST_URL).addHeader("Token", "d75542712c868c1690110db641ba01a").post(requestBody).build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
                         @Override
                         public void onFailure(okhttp3.Call call, IOException e) {
                             progressDialog.dismiss();
                             System.out.println("Registration Error" + e.getMessage());
                         }
                         @Override
                         public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                             progressDialog.dismiss();
                             if (response.isSuccessful()) {
                                 String resp = response.body().string();
                                 Log.d("Resp", resp);
                                 JSONObject obj = null;
                                 try {
                                     obj = new JSONObject(resp);
                                     JSONObject obj_response=obj.getJSONObject("Response");
                                     JSONObject obj_status=obj_response.getJSONObject("status");
                                     JSONObject obj_data=obj_response.getJSONObject("data");
                                     final String msgFinal = obj_data.getString("type");
                                     if(Objects.equals(msgFinal, "Success")){
                                         orderId = obj_data.getString("order_id");
                                         Log.d("Resp", orderId);
                                         if(filePaths.size() > 0){
                                             sendFile();
                                         }
                                         Intent intent = new Intent(getApplicationContext(), SummaryOnlineConsultation.class);
                                         intent.putExtra("fee",fee);
                                         intent.putExtra("mode",modeVideoConsultation.getText().toString());
                                         sharedPreferences.edit().putInt("visa_id",102).apply();
                                         sharedPreferences.edit().putString("order_id",orderId).apply();
                                         intent.putExtra("order_id", orderId);
                                         startActivity(intent);
                                     }else{
                                         showToast("Unsuccessful");
                                     }
                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }
                             }
                         }
                     }
        );
    }
    private void sendFile(){
        Log.d("uri", uri.toString() + "=" + uri.getLastPathSegment() + "=" + fileType + "=" + new File(uri.getPath()));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("order_id", orderId)
                .addFormDataPart("uploaded_file", uri.getLastPathSegment(),
                        RequestBody.create(MediaType.parse("image/jpeg"), new File(uri.getPath())))
                .build();
        Request request = new Request.Builder().url(FILE_URL).addHeader("Token", "d75542712c868c1690110db641ba01a").post(requestBody).build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
                         @Override
                         public void onFailure(okhttp3.Call call, IOException e) {
                             System.out.println("Registration Error" + e.getMessage());
                         }
                         @Override
                         public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                             if (response.isSuccessful()) {
                                 String resp = response.body().string();
                                 Log.d("Resp", resp);
                             }
                         }
                     }
        );
    }
    private void methodRequiresPermission() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            if(selectedFileType == 0){
                pickPhoto();
            }else if(selectedFileType == 1){
                pickDocument();
            }
        } else {
            EasyPermissions.requestPermissions(this, "Allow access for Storage to upload a document.",
                    0, perms);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(selectedFileType == 0){
            pickPhoto();
        }else if(selectedFileType == 1){
            pickDocument();
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
    private void pickDocument() {
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.AppTheme)
                .pickFile(this);
    }
    private void pickPhoto() {
        FilePickerBuilder.getInstance().setMaxCount(1)
                .setSelectedFiles(filePaths)
                .setActivityTheme(R.style.AppTheme)
                .pickPhoto(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case FilePickerConst.REQUEST_CODE_PHOTO:
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                break;
            case FilePickerConst.REQUEST_CODE_DOC:
                if(resultCode== Activity.RESULT_OK && data!=null)
                {
                    filePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                break;
        }
        if(filePaths.size() > 0){
            uri = Uri.parse(filePaths.get(0));
            uploadDocs.setText(uri.getLastPathSegment());
            lastSegment = uri.getLastPathSegment();
            fileType = getApplicationContext().getContentResolver().getType(uri);
            file  = new File(uri.getPath());
        }
    }
}
