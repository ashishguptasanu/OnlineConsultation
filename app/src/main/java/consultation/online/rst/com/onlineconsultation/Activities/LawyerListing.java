package consultation.online.rst.com.onlineconsultation.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import consultation.online.rst.com.onlineconsultation.Adapter.AdapterLawyer;
import consultation.online.rst.com.onlineconsultation.Model.LawyerList;
import consultation.online.rst.com.onlineconsultation.Model.Mode;
import consultation.online.rst.com.onlineconsultation.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class LawyerListing extends AppCompatActivity {
    String REGISTER_URL = "https://rtg-rst.com/v1/scheduler/get-data/person", zoneName, zoneId;
    List<LawyerList> lawyerList = new ArrayList<>();
    RecyclerView recyclerLawyer;
    LinearLayoutManager linearLayoutManager;
    AdapterLawyer adapterLawyer;
    JSONObject jsonObject = new JSONObject();
    List<Mode> modes = new ArrayList<>();
    int selectedLivingInId;
    SharedPreferences sharedPreferences;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lawyer_listing);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        initViews();
    }
    private void initViews() {
        recyclerLawyer = (RecyclerView)findViewById(R.id.recycler_lawyer);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerLawyer.setLayoutManager(linearLayoutManager);
        if(getIntent().getExtras().containsKey("selected_living_in_id")){
            selectedLivingInId = getIntent().getIntExtra("selected_living_in_id",0);
            Log.d("LIVING IN", String.valueOf(selectedLivingInId));
        }
        new ConnectionTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.video, menu);
        this.menu = menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.timezone) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private class ConnectionTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            MenuItem menuItem  = menu.findItem(R.id.timezone);
            if(getIntent().getExtras().containsKey("zone_name")){
                menuItem.setTitle(getIntent().getStringExtra("zone_name"));
                sharedPreferences.edit().putString("time_zone_id",getIntent().getStringExtra("zone_id")).apply();
            }else{
                menuItem.setTitle(zoneName);
                sharedPreferences.edit().putString("time_zone_id",zoneId).apply();
            }

            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Intent intent = new Intent(getApplicationContext(), TimeZoneSelection.class);
                    startActivity(intent);
                    return true;
                }
            });
            adapterLawyer = new AdapterLawyer( lawyerList, getApplicationContext(), modes);
            recyclerLawyer.setAdapter(adapterLawyer);

            sharedPreferences.edit().putString("process_name_consultation","sss-numerologist.com").apply();
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject.put("process", "sss-numerologist.com");
                jsonObject.put("living_in", String.valueOf(selectedLivingInId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();
            okhttp3.RequestBody body = RequestBody.create(JSON, jsonObject.toString());
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(REGISTER_URL)
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
                     zoneId = obj_data.getString("zoneId");
                     zoneName = obj_data.getString("zoneName");
                    Log.d("Zone Name", zoneName);

                    JSONArray dataArray = obj_data.getJSONArray("data");
                    Log.d("Resp", obj_response.toString());
                    for(int i=0; i<dataArray.length(); i++){
                        JSONObject lawyerObject = dataArray.getJSONObject(i);
                        String id = lawyerObject.getString("per_id");
                        String name = lawyerObject.getString("per_name");
                        String email = lawyerObject.getString("per_email");
                        String contact = lawyerObject.getString("contact");
                        String about = lawyerObject.getString("per_about");
                        String img_url = lawyerObject.getString("per_img");
                        String slot_time = lawyerObject.getString("per_slot_time");
                        String designation = lawyerObject.getString("per_designation");
                        String location = lawyerObject.getString("per_address");
                        JSONArray modesArray = lawyerObject.getJSONArray("modes");
                        for(int j=0; j<modesArray.length();j++){
                            JSONObject modesObject = modesArray.getJSONObject(j);
                            String modeId = modesObject.getString("mode_id");
                            String modeName = modesObject.getString("mode_name");
                            String modePrice = modesObject.getString("mode_price");
                            Log.d("mode_name", modeName);
                            Mode mode = new Mode(modeId, modeName, String.valueOf(i), modePrice);
                            modes.add(mode);
                        }
                        LawyerList lawyerListNew = new LawyerList(id, name, email, contact, img_url, about, slot_time, modes, designation, location);
                        lawyerList.add(lawyerListNew);
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
}
