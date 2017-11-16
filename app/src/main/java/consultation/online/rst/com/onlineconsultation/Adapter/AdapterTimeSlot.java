package consultation.online.rst.com.onlineconsultation.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import consultation.online.rst.com.onlineconsultation.Activities.PaymentResponse;
import consultation.online.rst.com.onlineconsultation.Model.BookedTimeSlot;
import consultation.online.rst.com.onlineconsultation.Model.Slot;
import consultation.online.rst.com.onlineconsultation.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
public class AdapterTimeSlot extends RecyclerView.Adapter<AdapterTimeSlot.MyViewHolder> {
    Context context;
    List<Slot> slots = new ArrayList();
    String selectedTimeSlotDate, indianTimeSlot;
    Button btnSubmit;
    JSONObject jsonObject = new JSONObject();
    String REGISTER_URL = "https://rtg-rst.com/v1/scheduler/get-data/book";
    SharedPreferences sharedPreferences;
    int data;
    String dataMsg;
    String TIMESLOT_REGISTRATION_URL = "https://uk-immigrationhub.com/v1.1/consultApi/consultapi.php?gofor=consultForm";
    OkHttpClient client = new OkHttpClient();
    private DatabaseReference mDatabase;
    public AdapterTimeSlot(Context context, List<Slot> slots, Button btnSubmit){
        this.context = context;
        this.slots = slots;
        this.btnSubmit = btnSubmit;
    }
    @Override
    public AdapterTimeSlot.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_time_slot,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterTimeSlot.MyViewHolder holder, int position) {
        holder.tvTimeSlot.setText(slots.get(position).getSlot());
        if(Objects.equals(slots.get(position).getStatus(), "1")){
            holder.tvTimeSlot.setTextColor(Color.parseColor("#FFDD2C00"));
        }else if(Objects.equals(slots.get(position).getStatus(), "2")){
            holder.tvTimeSlot.setTextColor(Color.parseColor("#FF00C853"));
        }else if(Objects.equals(slots.get(position).getStatus(), "3")){
            holder.tvTimeSlot.setTextColor(Color.parseColor("#797979"));
        }
    }
    @Override
    public int getItemCount() {
        return slots.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTimeSlot;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTimeSlot = (TextView)itemView.findViewById(R.id.tv_time_slot);
            tvTimeSlot.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if(Objects.equals(slots.get(getAdapterPosition()).getStatus(), "1")){
                Toast.makeText(context,"Already Booked", Toast.LENGTH_SHORT).show();
            }else if(Objects.equals(slots.get(getAdapterPosition()).getStatus(), "2")){
                btnSubmit.setVisibility(View.VISIBLE);
                btnSubmit.setText("Confirm Booking for " + slots.get(getAdapterPosition()).getSlot());
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectedTimeSlotDate = slots.get(getAdapterPosition()).getOriginal_slot_date();
                        indianTimeSlot = slots.get(getAdapterPosition()).getIndian_slot_date();
                        Toast.makeText(context,"Please Wait..", Toast.LENGTH_SHORT).show();
                        sendRegistrationTimeSlot(sharedPreferences.getString("order_id",""),indianTimeSlot);
                        Log.d("selectedDate",selectedTimeSlotDate);
                        new ConnectionTask().execute();
                    }
                });
            }else if(Objects.equals(slots.get(getAdapterPosition()).getStatus(), "3")){
                Toast.makeText(context,"Unavailable", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void sendRegistrationTimeSlot(String order_id, String indianTimeSlot) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("orderCount", order_id)
                .addFormDataPart("appointment_time", indianTimeSlot)
                .build();
        Request request = new Request.Builder().url(TIMESLOT_REGISTRATION_URL).post(requestBody).build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
                         @Override
                         public void onFailure(okhttp3.Call call, IOException e) {
                             System.out.println("Registration Error" + e.getMessage());
                         }
                         @Override
                         public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                             if (response.isSuccessful()) {

                             }
                         }
                     }
        );
    }
    private class ConnectionTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            if(data == 0){
                Toast.makeText(context,dataMsg, Toast.LENGTH_SHORT).show();
            }
            Log.d("Mode_id",sharedPreferences.getString("consultationModeId",""));
        }
        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject.put("process",sharedPreferences.getString("process_name_consultation","") );
                jsonObject.put("consultant", sharedPreferences.getString("lawyer_id_selected",""));
                jsonObject.put("date", selectedTimeSlotDate);
                jsonObject.put("in_date",indianTimeSlot);
                jsonObject.put("mode", sharedPreferences.getString("consultation_mode_id",""));
                jsonObject.put("name", sharedPreferences.getString("applicant_name_consultation",""));
                jsonObject.put("email", sharedPreferences.getString("applicant_email_consultation",""));
                jsonObject.put("mobile", sharedPreferences.getString("applicant_mobile_consultation",""));
                jsonObject.put("age", sharedPreferences.getString("applicant_age_consultation",""));
                jsonObject.put("gender", sharedPreferences.getString("applicant_gender_consultation",""));
                jsonObject.put("desc", sharedPreferences.getString("applicant_extra_info_consultation",""));
                jsonObject.put("reference", sharedPreferences.getString("order_id",""));
                jsonObject.put("consultee_time", sharedPreferences.getString("time_zone_id",""));
                jsonObject.put("currency_code","USD");
                jsonObject.put("fee",sharedPreferences.getString("applicant_consultation_fee",""));

                Log.d("selected_time_slot",selectedTimeSlotDate);
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
                    Log.d("Response", obj_response.toString());
                    JSONObject obj_status=obj_response.getJSONObject("status");
                    JSONObject obj_data=obj_response.getJSONObject("data");
                    data = obj_data.getInt("booking_id");
                    dataMsg = obj_data.getString("data");
                    if(data > 0){
                        BookedTimeSlot bookedTimeSlot = new BookedTimeSlot(sharedPreferences.getString("applicant_email_consultation",""),"",sharedPreferences.getString("order_id",""), 0,0, sharedPreferences.getString("fcm_token", FirebaseInstanceId.getInstance().getToken()));
                        Log.d("Consultation Mode",sharedPreferences.getString("consultationModeId",""));
                        if(Objects.equals(sharedPreferences.getString("consultationModeId", ""), "1")){
                            sendDataToFirebase(bookedTimeSlot);
                        }
                        Intent intent = new Intent(context, PaymentResponse.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
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

    private void sendDataToFirebase(BookedTimeSlot bookedTimeSlot) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("time_slots").child(sharedPreferences.getString("order_id","")).setValue(bookedTimeSlot);
    }

}
