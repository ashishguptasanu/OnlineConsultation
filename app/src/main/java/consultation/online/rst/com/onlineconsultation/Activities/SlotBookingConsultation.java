package consultation.online.rst.com.onlineconsultation.Activities;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import consultation.online.rst.com.onlineconsultation.Adapter.AdapterTimeSlot;
import consultation.online.rst.com.onlineconsultation.Model.Slot;
import consultation.online.rst.com.onlineconsultation.R;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class SlotBookingConsultation extends AppCompatActivity implements View.OnClickListener{
    JSONObject jsonObject = new JSONObject();
    String TIME_SLOT_URL = "https://rtg-rst.com/v1/scheduler/get-data/schedule", currentDate, currentDay;
    List<Slot> slots = new ArrayList<>();
    List<Slot> morningSlot = new ArrayList<>();
    List<Slot> afternoonSlot = new ArrayList<>();
    List<Slot> eveningSlot = new ArrayList<>();
    List<Slot> nightSlot = new ArrayList<>();
    List<Slot> postMidnight = new ArrayList<>();
    Button btnSubmit;
    String selectedDate;
    MaterialProgressBar materialProgressBar;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerSlot, recyclerSlot2, recyclerSlot3, recyclerSlot4, recyclerSlot5;
    GridLayoutManager gridLayoutManager, gridLayoutManager2, gridLayoutManager3, gridLayoutManager4, gridLayoutManager5;
    AdapterTimeSlot adapterTimeSlot, adapterTimeSlot2, adapterTimeSlot3, adapterTimeSlot4, adapterTimeSlot5;
    TextView tvMorningSlotSize, tvAfternoonSlotSize, tvEveningSlotSize, tvNightSlotSize, tvPostMidnightSlotSize, tvLawyerName, tvLawyerInfo, tvDate;
    ImageView imgExpandMorning, imgExpandAfternoon, imgExpandEvening, imgExpandNight, imgExpandPostMidnight, imgPostMidnight, imgMorning, imgAfternoon, imgEvening, imgNight, imgLawyer;
    LinearLayout layoutMorning, layoutAfternoon, layoutEvening, layoutNight, layoutPostMidnight;
    String IMG_URL1 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fmoon-and-stars.png?alt=media&token=fb631e6f-651d-4e9e-8533-8c1ebadc5d72";
    String IMG_URL2 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fsun-shining-and-cloud.png?alt=media&token=4a1b3a01-21a2-402e-8482-af74c595a5b6";
    String IMG_URL3 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fsun.png?alt=media&token=01d28301-7d1a-4d27-980a-43f873798119";
    String IMG_URL4 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fsunrise.png?alt=media&token=60b1ed12-e191-48ac-bf2c-d2c9445361d9";
    String IMG_URL5 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fcloud-and-moon.png?alt=media&token=e4553bbc-78d6-4b99-b95b-5343bc47a3e6";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_booking_consultation);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        initViews();
    }
    private void initViews() {
        materialProgressBar = (MaterialProgressBar)findViewById(R.id.progress_slot_booking);
        recyclerSlot = (RecyclerView)findViewById(R.id.recycler_time_slot);
        recyclerSlot2 = (RecyclerView)findViewById(R.id.recycler_time_slot2);
        recyclerSlot3 = (RecyclerView)findViewById(R.id.recycler_time_slot3);
        recyclerSlot4 = (RecyclerView)findViewById(R.id.recycler_time_slot4);
        recyclerSlot5 = (RecyclerView)findViewById(R.id.recycler_time_slot5);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 4);
        gridLayoutManager2 = new GridLayoutManager(getApplicationContext(), 4);
        gridLayoutManager3 = new GridLayoutManager(getApplicationContext(), 4);
        gridLayoutManager4 = new GridLayoutManager(getApplicationContext(), 4);
        gridLayoutManager5 = new GridLayoutManager(getApplicationContext(), 4);
        recyclerSlot2.setLayoutManager(gridLayoutManager2);
        recyclerSlot.setLayoutManager(gridLayoutManager);
        recyclerSlot3.setLayoutManager(gridLayoutManager3);
        recyclerSlot4.setLayoutManager(gridLayoutManager4);
        recyclerSlot5.setLayoutManager(gridLayoutManager5);
        imgExpandMorning = (ImageView) findViewById(R.id.img_expand_morning);
        imgExpandMorning.setOnClickListener(this);
        imgExpandAfternoon = (ImageView)findViewById(R.id.img_expand_afternoon);
        imgExpandAfternoon.setOnClickListener(this);
        imgExpandEvening = (ImageView)findViewById(R.id.img_expand_evening);
        imgExpandEvening.setOnClickListener(this);
        imgExpandPostMidnight = (ImageView)findViewById(R.id.img_expand_post_midnight);
        imgExpandPostMidnight.setOnClickListener(this);
        imgExpandNight = (ImageView)findViewById(R.id.img_expand_night);
        imgExpandNight.setOnClickListener(this);
        layoutMorning = (LinearLayout)findViewById(R.id.layout_morning_slot);
        layoutAfternoon = (LinearLayout)findViewById(R.id.layout_afternoon_slot);
        layoutEvening = (LinearLayout)findViewById(R.id.layout_evening_slot);
        layoutPostMidnight = (LinearLayout)findViewById(R.id.layout_post_midnight_slot);
        layoutNight = (LinearLayout)findViewById(R.id.layout_night_slot);
        imgPostMidnight = (ImageView)findViewById(R.id.img_post_midnight);
        imgMorning = (ImageView)findViewById(R.id.img_morng);
        imgAfternoon = (ImageView)findViewById(R.id.img_afternoon);
        imgEvening = (ImageView)findViewById(R.id.img_evening);
        imgNight = (ImageView)findViewById(R.id.img_night);
        Picasso.with(getApplicationContext()).load(IMG_URL1).into(imgPostMidnight);
        Picasso.with(getApplicationContext()).load(IMG_URL2).into(imgMorning);
        Picasso.with(getApplicationContext()).load(IMG_URL3).into(imgAfternoon);
        Picasso.with(getApplicationContext()).load(IMG_URL4).into(imgEvening);
        Picasso.with(getApplicationContext()).load(IMG_URL5).into(imgNight);
        btnSubmit = (Button)findViewById(R.id.btn_book_slot_consultation);
        tvLawyerName = (TextView)findViewById(R.id.tv_name_lawyer_slot_booking);
        tvLawyerInfo = (TextView)findViewById(R.id.tv_info_lawyer_slot_booking);
        imgLawyer = (ImageView)findViewById(R.id.img_lawyer_slot_booking);
        tvDate = (TextView)findViewById(R.id.tv_slot_date);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frontDatePicker();

            }
        });
        if(sharedPreferences != null && sharedPreferences.contains("lawyer_name")){
            tvLawyerName.setText(sharedPreferences.getString("lawyer_name",""));
            tvLawyerInfo.setText(sharedPreferences.getString("lawyer_info_selected",""));
            Picasso.with(getApplicationContext()).load(sharedPreferences.getString("lawyer_img_selected","")).into(imgLawyer);
        }
        new ConnectionTask().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_expand_morning:
                if(layoutMorning.getVisibility() == View.VISIBLE){
                    layoutMorning.setVisibility(View.GONE);
                    imgExpandMorning.setImageResource(R.drawable.expand_more);
                }else{
                    layoutMorning.setVisibility(View.VISIBLE);
                    imgExpandMorning.setImageResource(R.drawable.expand_less);
                }
                break;
            case R.id.img_expand_afternoon:
                if(layoutAfternoon.getVisibility() == View.VISIBLE){
                    layoutAfternoon.setVisibility(View.GONE);
                    imgExpandAfternoon.setImageResource(R.drawable.expand_more);
                }else{
                    layoutAfternoon.setVisibility(View.VISIBLE);
                    imgExpandAfternoon.setImageResource(R.drawable.expand_less);
                }
                break;
            case R.id.img_expand_evening:
                if(layoutEvening.getVisibility() == View.VISIBLE){
                    layoutEvening.setVisibility(View.GONE);
                    imgExpandEvening.setImageResource(R.drawable.expand_more);
                }else{
                    layoutEvening.setVisibility(View.VISIBLE);
                    imgExpandEvening.setImageResource(R.drawable.expand_less);
                }
                break;
            case R.id.img_expand_post_midnight:
                if(layoutPostMidnight.getVisibility() == View.VISIBLE){
                    layoutPostMidnight.setVisibility(View.GONE);
                    imgExpandPostMidnight.setImageResource(R.drawable.expand_more);
                }else{
                    layoutPostMidnight.setVisibility(View.VISIBLE);
                    imgExpandPostMidnight.setImageResource(R.drawable.expand_less);
                }
                break;
            case R.id.img_expand_night:
                if(layoutNight.getVisibility() == View.VISIBLE){
                    layoutNight.setVisibility(View.GONE);
                    imgExpandNight.setImageResource(R.drawable.expand_more);
                }else{
                    layoutNight.setVisibility(View.VISIBLE);
                    imgExpandNight.setImageResource(R.drawable.expand_less);
                }
                break;
        }
    }


    private class ConnectionTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            materialProgressBar.setVisibility(View.GONE);
            adapterTimeSlot = new AdapterTimeSlot(getApplicationContext(), morningSlot, btnSubmit);
            recyclerSlot.setAdapter(adapterTimeSlot);
            adapterTimeSlot2 = new AdapterTimeSlot(getApplicationContext(), afternoonSlot, btnSubmit);
            recyclerSlot2.setAdapter(adapterTimeSlot2);
            adapterTimeSlot3 = new AdapterTimeSlot(getApplicationContext(), eveningSlot, btnSubmit);
            recyclerSlot3.setAdapter(adapterTimeSlot3);
            adapterTimeSlot4 = new AdapterTimeSlot(getApplicationContext(), postMidnight, btnSubmit);
            recyclerSlot4.setAdapter(adapterTimeSlot4);
            adapterTimeSlot5 = new AdapterTimeSlot(getApplicationContext(), nightSlot, btnSubmit);
            recyclerSlot5.setAdapter(adapterTimeSlot5);
            Log.d("Size", morningSlot.size() + "=" + afternoonSlot.size() + "=" + eveningSlot.size());
            tvMorningSlotSize = (TextView)findViewById(R.id.tv_morning_slot_size);
            tvAfternoonSlotSize = (TextView)findViewById(R.id.tv_afternoon_slot_size);
            tvEveningSlotSize = (TextView)findViewById(R.id.tv_evening_slot_size);
            tvPostMidnightSlotSize = (TextView)findViewById(R.id.tv_post_midnight_slot_size);
            tvNightSlotSize = (TextView)findViewById(R.id.tv_night_slot_size);
            tvMorningSlotSize.setText(morningSlot.size() + " Slots");
            tvAfternoonSlotSize.setText(afternoonSlot.size()+ " Slots");
            tvEveningSlotSize.setText(eveningSlot.size()+ " Slots");
            tvPostMidnightSlotSize.setText(postMidnight.size()+ " Slots");
            tvNightSlotSize.setText(nightSlot.size()+ " Slots");
            tvDate.setText(currentDay + ", " + currentDate);
        }
        @Override
        protected String doInBackground(String... urls) {
            materialProgressBar.setVisibility(View.VISIBLE);
            try {
                jsonObject.put("process", sharedPreferences.getString("process_name_consultation",""));
                jsonObject.put("consultant",sharedPreferences.getString("lawyer_id_selected",""));
                jsonObject.put("timezone",sharedPreferences.getString("time_zone_id",""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            okhttp3.RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(TIME_SLOT_URL)
                    .post(body)
                    .addHeader("Token","$2y$12$ryzJkF6ovr8Bny1Im4y2QeqzyvFV5ogAeHX8NM2J4O1bwCO4SwBbK")
                    .build();
            okhttp3.Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String networkResp = response.body().string();
                JSONObject obj= null;
                try {
                    obj = new JSONObject(networkResp);
                    JSONObject obj_response=obj.getJSONObject("Response");
                    JSONObject obj_status=obj_response.getJSONObject("status");
                    JSONObject obj_data=obj_response.getJSONObject("data");
                    JSONArray dataArray = obj_data.getJSONArray("data");
                    JSONObject slotObject = dataArray.getJSONObject(0);
                    currentDate = slotObject.getString("date");
                    currentDay = slotObject.getString("day");
                    JSONArray slotArray = slotObject.getJSONArray("slot");
                    for(int i=0; i<slotArray.length(); i++){
                        JSONObject slotObjectFinal = slotArray.getJSONObject(i);
                        String status = slotObjectFinal.getString("status");
                        String msg = slotObjectFinal.getString("msg");
                        String slot = slotObjectFinal.getString("modified_slot");
                        String slab = slotObjectFinal.getString("slab");
                        String originalSlotDate = slotObjectFinal.getString("original_slot_date");
                        String indianSlotDate = slotObjectFinal.getString("original_slot_date");
                        Slot slotData =  new Slot(status, msg, slot, slab, originalSlotDate, indianSlotDate);
                        slots.add(slotData);
                    }
                    for(int j=0; j<slots.size();j++){
                        if(Objects.equals(slots.get(j).getSlab(), "2")){
                            morningSlot.add(slots.get(j));
                        }else if(Objects.equals(slots.get(j).getSlab(), "3")){
                            afternoonSlot.add(slots.get(j));
                        }else if(Objects.equals(slots.get(j).getSlab(), "4")){
                            eveningSlot.add(slots.get(j));
                        }else if(Objects.equals(slots.get(j).getSlab(), "1")){
                            postMidnight.add(slots.get(j));
                        }else if(Objects.equals(slots.get(j).getSlab(), "5")){
                            nightSlot.add(slots.get(j));
                        }
                        else{
                            slots.get(j).getSlab();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
    private class ConnectionTaskReload extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            //materialProgressBar.setVisibility(View.GONE);
            adapterTimeSlot = new AdapterTimeSlot(getApplicationContext(), morningSlot, btnSubmit);
            recyclerSlot.setAdapter(adapterTimeSlot);
            adapterTimeSlot2 = new AdapterTimeSlot(getApplicationContext(), afternoonSlot, btnSubmit);
            recyclerSlot2.setAdapter(adapterTimeSlot2);
            adapterTimeSlot3 = new AdapterTimeSlot(getApplicationContext(), eveningSlot, btnSubmit);
            recyclerSlot3.setAdapter(adapterTimeSlot3);
            adapterTimeSlot4 = new AdapterTimeSlot(getApplicationContext(), postMidnight, btnSubmit);
            recyclerSlot4.setAdapter(adapterTimeSlot4);
            adapterTimeSlot5 = new AdapterTimeSlot(getApplicationContext(), nightSlot, btnSubmit);
            recyclerSlot5.setAdapter(adapterTimeSlot5);
            Log.d("Size", morningSlot.size() + "=" + afternoonSlot.size() + "=" + eveningSlot.size());
            tvMorningSlotSize = (TextView)findViewById(R.id.tv_morning_slot_size);
            tvAfternoonSlotSize = (TextView)findViewById(R.id.tv_afternoon_slot_size);
            tvEveningSlotSize = (TextView)findViewById(R.id.tv_evening_slot_size);
            tvPostMidnightSlotSize = (TextView)findViewById(R.id.tv_post_midnight_slot_size);
            tvNightSlotSize = (TextView)findViewById(R.id.tv_night_slot_size);
            tvMorningSlotSize.setText(morningSlot.size() + " Slots");
            tvAfternoonSlotSize.setText(afternoonSlot.size()+ " Slots");
            tvEveningSlotSize.setText(eveningSlot.size()+ " Slots");
            tvPostMidnightSlotSize.setText(postMidnight.size()+ " Slots");
            tvNightSlotSize.setText(nightSlot.size()+ " Slots");
            tvDate.setText(currentDay + ", " + currentDate);
        }
        @Override
        protected String doInBackground(String... urls) {
            //;materialProgressBar.setVisibility(View.VISIBLE);
            try {
                jsonObject.put("process", sharedPreferences.getString("process_name_consultation",""));
                jsonObject.put("consultant",sharedPreferences.getString("lawyer_id_selected",""));
                jsonObject.put("timezone",sharedPreferences.getString("time_zone_id",""));
                jsonObject.put("date",selectedDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            okhttp3.RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(TIME_SLOT_URL)
                    .post(body)
                    .addHeader("Token","$2y$12$ryzJkF6ovr8Bny1Im4y2QeqzyvFV5ogAeHX8NM2J4O1bwCO4SwBbK")
                    .build();
            okhttp3.Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String networkResp = response.body().string();
                JSONObject obj= null;
                try {
                    slots.clear();
                    obj = new JSONObject(networkResp);
                    JSONObject obj_response=obj.getJSONObject("Response");
                    JSONObject obj_status=obj_response.getJSONObject("status");
                    JSONObject obj_data=obj_response.getJSONObject("data");
                    JSONArray dataArray = obj_data.getJSONArray("data");
                    JSONObject slotObject = dataArray.getJSONObject(0);
                    currentDate = slotObject.getString("date");
                    currentDay = slotObject.getString("day");
                    JSONArray slotArray = slotObject.getJSONArray("slot");
                    for(int i=0; i<slotArray.length(); i++){
                        JSONObject slotObjectFinal = slotArray.getJSONObject(i);
                        String status = slotObjectFinal.getString("status");
                        String msg = slotObjectFinal.getString("msg");
                        String slot = slotObjectFinal.getString("modified_slot");
                        String slab = slotObjectFinal.getString("slab");
                        String originalSlotDate = slotObjectFinal.getString("original_slot_date");
                        String indianSlotDate = slotObjectFinal.getString("original_slot_date");
                        Slot slotData =  new Slot(status, msg, slot, slab, originalSlotDate, indianSlotDate);
                        slots.add(slotData);
                    }
                    morningSlot.clear();
                    afternoonSlot.clear();
                    eveningSlot.clear();
                    postMidnight.clear();
                    nightSlot.clear();
                    for(int j=0; j<slots.size();j++){
                        if(Objects.equals(slots.get(j).getSlab(), "2")){
                            morningSlot.add(slots.get(j));
                        }else if(Objects.equals(slots.get(j).getSlab(), "3")){
                            afternoonSlot.add(slots.get(j));
                        }else if(Objects.equals(slots.get(j).getSlab(), "4")){
                            eveningSlot.add(slots.get(j));
                        }else if(Objects.equals(slots.get(j).getSlab(), "1")){
                            postMidnight.add(slots.get(j));
                        }else if(Objects.equals(slots.get(j).getSlab(), "5")){
                            nightSlot.add(slots.get(j));
                        }
                        else{
                            slots.get(j).getSlab();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finishAffinity();
        } else {
            Toast.makeText(this, "Your current progress will be lost, Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;

        }
    }
    private void frontDatePicker() {
        final String[] date = {""};
        Calendar mcurrentDate= Calendar.getInstance();
        final int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog mDatePicker=new DatePickerDialog(this,android.R.style.Theme_Holo_Light_Dialog_MinWidth, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedDate = selectedyear +"-"+(selectedmonth+1)+"-"+selectedday;
                new ConnectionTaskReload().execute();
            }
        },mYear, mMonth, mDay);
        mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        mDatePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDatePicker.setTitle("Select Your Date");
        mDatePicker.show();

    }
}
