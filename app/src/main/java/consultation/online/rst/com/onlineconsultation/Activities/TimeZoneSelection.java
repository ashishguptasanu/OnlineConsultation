package consultation.online.rst.com.onlineconsultation.Activities;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import consultation.online.rst.com.onlineconsultation.Adapter.AdapterTimeZone;
import consultation.online.rst.com.onlineconsultation.Model.TimeZone;
import consultation.online.rst.com.onlineconsultation.R;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TimeZoneSelection extends AppCompatActivity {
    String TIMEZONE_URL = "https://rtg-rst.com/v1/scheduler/get-data/timezone";
    OkHttpClient client = new OkHttpClient();
    List<TimeZone> timeZoneList = new ArrayList<>();
    List<String> timeZoneData = new ArrayList<>();
    RecyclerView recyclerTimeZone;
    LinearLayoutManager linearLayoutManager;
    AdapterTimeZone adapterTimeZone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_zone_selection);
        initViews();
    }

    private void initViews() {
        recyclerTimeZone = (RecyclerView)findViewById(R.id.recycler_timezone);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        adapterTimeZone = new AdapterTimeZone(getApplicationContext(), timeZoneList);
        adapterTimeZone.notifyDataSetChanged();
        recyclerTimeZone.setLayoutManager(linearLayoutManager);
        recyclerTimeZone.setHasFixedSize(true);
        getTimeZoneData();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }
    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Char",newText);
                adapterTimeZone.getFilter().filter(newText);

                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void getTimeZoneData(){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("test","test")
                .build();
        Request request = new Request.Builder().url(TIMEZONE_URL).addHeader("Token","$2y$12$ryzJkF6ovr8Bny1Im4y2QeqzyvFV5ogAeHX8NM2J4O1bwCO4SwBbK").post(requestBody).build();
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
                                         String countryCode = dataobject.getString("countryCode");
                                         String zoneName = dataobject.getString("zoneName");
                                         TimeZone timeZone = new TimeZone(countryId,countryName, countryCode, zoneName);
                                         timeZoneList.add(timeZone);
                                     }



                                     runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                            recyclerTimeZone.setAdapter(adapterTimeZone);
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
