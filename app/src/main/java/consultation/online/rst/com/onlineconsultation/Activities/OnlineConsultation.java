package consultation.online.rst.com.onlineconsultation.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import consultation.online.rst.com.onlineconsultation.Model.CountryRes;
import consultation.online.rst.com.onlineconsultation.R;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OnlineConsultation extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener{
    ImageView videoBanner;
    String[] chooseService, serviceForImmigration, serviceForVisa, serviceForPassport, serviceForAttestation;
    Spinner spnrChooseService, spnrServiceFor, spnrLivingIn;
    Button submitVideo;
    int selectedService;
    String Living_IN_URL = "https://rtg-rst.com/v1/scheduler/get-data/country";
    OkHttpClient client = new OkHttpClient();
    List<CountryRes> livingIn = new ArrayList<>();
    List<String> livingInData = new ArrayList<>();
    ArrayAdapter<String> visaArrayAdapter;
    int selectedCountryId, selectedServiceFor, selectedConsultationFor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_consultation);
        init();
        Log.d("FireBase_Token", FirebaseInstanceId.getInstance().getToken());
        videoBanner = (ImageView)findViewById(R.id.video_banner);
        Picasso.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fconsultations-mobile-laptop-babylon-right.png?alt=media&token=a4b84b3a-04ec-4370-9cf0-9028124c740e").into(videoBanner);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {

            selectedService = 0;

        getLivingInData();
        chooseService = new String[]{"Select One", "Visa Service", "Passport Service", "Immigration Service", "Attestation Service"};
        serviceForVisa = new String[]{"Select One","United Arab Emirates", "United States of America", "Singapore", "Iran", "Oman"};
        serviceForPassport = new String[]{"Select One","United kingdom"};
        serviceForAttestation = new String[]{"Select One","Attestation Services"};
        serviceForImmigration = new String[]{"Select One", "Canada","India", "United Kingdom", "United States of America"};
        submitVideo = (Button)findViewById(R.id.btn_submit_video);
        submitVideo.setOnClickListener(this);
        initializeChooseServiceSpinner();
        spnrLivingIn = (Spinner)findViewById(R.id.spnr_living_in_video_consultation);
        spnrLivingIn.setOnItemSelectedListener(this);
        visaArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, livingInData);
        visaArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }
    private void initializeChooseServiceSpinner() {
        spnrChooseService = (Spinner)findViewById(R.id.spnr_consultation_choose);
        spnrChooseService.setOnItemSelectedListener(this);
        ArrayAdapter<String> visaArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, chooseService);
        visaArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrChooseService.setAdapter(visaArrayAdapter);
        if(selectedService != 0){
            spnrChooseService.setSelection(3);
        }
    }
    private void initializeServiceForSpinner(String[] list) {
        spnrServiceFor = (Spinner)findViewById(R.id.spnr_consultation_services_for);
        spnrServiceFor.setOnItemSelectedListener(this);
        ArrayAdapter<String> visaArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        visaArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrServiceFor.setAdapter(visaArrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.spnr_consultation_choose:
                if(i == 0){
                    initializeServiceForSpinner(new String[]{"Select One"});
                    selectedConsultationFor = 0;
                }else if(i == 1){
                    initializeServiceForSpinner(serviceForVisa);
                    selectedConsultationFor = 1;
                }else if(i == 2){
                    initializeServiceForSpinner(serviceForPassport);
                    selectedConsultationFor = 2;
                }else if(i == 3){
                    initializeServiceForSpinner(serviceForImmigration);
                    selectedConsultationFor = 3;
                }else if(i == 4){
                    initializeServiceForSpinner(serviceForAttestation);
                    selectedConsultationFor = 4;
                }
                break;
            case R.id.spnr_living_in_video_consultation:
                if(i > 0){
                    selectedCountryId = livingIn.get(i-1).getCountryid();
                    Log.d("living_in", String.valueOf(livingIn.get(i-1).getCountryid()));
                }
                break;
            case R.id.spnr_consultation_services_for:
                selectedServiceFor = i;
                break;
        }
    }
    private void showToast(String s){
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        if(selectedConsultationFor != 0 && selectedServiceFor != 0 && selectedCountryId != 0){
            Intent intent = new Intent(this, LawyerListing.class);
            intent.putExtra("selected_living_in_id", selectedCountryId);
            startActivity(intent);
        }else{
            showToast("Select all fields");
        }
    }
    private void getLivingInData(){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("test","test")
                .build();
        Request request = new Request.Builder().url(Living_IN_URL).addHeader("Token","$2y$12$ryzJkF6ovr8Bny1Im4y2QeqzyvFV5ogAeHX8NM2J4O1bwCO4SwBbK").post(requestBody).build();
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
                                 JSONObject obj = null;
                                 try {
                                     obj = new JSONObject(resp);
                                     JSONObject obj_response=obj.getJSONObject("Response");
                                     JSONObject obj_status=obj_response.getJSONObject("status");
                                     JSONObject obj_data=obj_response.getJSONObject("data");
                                     //final String msgFinal = obj_data.getString("type");

                                     JSONArray dataArray = obj_data.getJSONArray("data");
                                     for(int i=0; i<dataArray.length();i++){
                                         JSONObject dataobject = dataArray.getJSONObject(i);
                                         String countryName = dataobject.getString("countryName");
                                         String countryId = dataobject.getString("countryId");
                                         CountryRes country = new CountryRes(countryName, Integer.parseInt(countryId));
                                         livingIn.add(country);
                                     }
                                     livingInData.add(0, "Select One");
                                     for(int j=0; j<livingIn.size(); j++){
                                         livingInData.add(livingIn.get(j).getCountryName());

                                     }


                                     runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             spnrLivingIn.setAdapter(visaArrayAdapter);
                                         }
                                     });

                                 } catch (JSONException e) {
                                     e.printStackTrace();
                                 }

                             }
                         }
                     }
        );
    }
}
