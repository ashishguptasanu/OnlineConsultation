package consultation.online.rst.com.onlineconsultation.APIs;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class TimeSlotInteractor implements TimeSlotContract.Interactor {
    private static final String TAG = TimeSlotInteractor.class.getSimpleName();

    private TimeSlotContract.OnTaskListener mOnTaskListener;

    public TimeSlotInteractor(TimeSlotContract.OnTaskListener onTaskListener) {
        this.mOnTaskListener = onTaskListener;
    }
    @Override
    public void performTimeSlotTask(String orderId, String process, String deviceType, final String callStatus, String fcmToken) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("process", process);
            jsonObject.put("reference", orderId);
            jsonObject.put("type", deviceType);
            jsonObject.put("callStatus", callStatus);
            jsonObject.put("fcmToken", fcmToken);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        okhttp3.RequestBody body = RequestBody.create(JSON, jsonObject.toString());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().
                url("https://rtg-rst.com/v1/scheduler/update-call/call")
                .addHeader("Token","$2y$12$ryzJkF6ovr8Bny1Im4y2QeqzyvFV5ogAeHX8NM2J4O1bwCO4SwBbK")
                .post(body)
                .build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
                         @Override
                         public void onFailure(okhttp3.Call call, IOException e) {
                             System.out.println("Registration Error" + e.getMessage());
                         }
                         @Override
                         public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                             String resp = response.body().string();
                             Log.d("reponse",callStatus + "=" + resp);
                             if (response.isSuccessful()) {

                                 JSONObject obj = null;
                                 try {
                                     obj = new JSONObject(resp);
                                     JSONObject obj_response=obj.getJSONObject("Response");
                                     JSONObject obj_status=obj_response.getJSONObject("status");
                                     String status = obj_status.getString("type");
                                     if(Objects.equals("Success",status)){
                                         JSONObject obj_data=obj_response.getJSONObject("data");
                                         String responseData = obj_data.getString("data");
                                             mOnTaskListener.onSuccess(responseData);

                                     }else{
                                         mOnTaskListener.onFailure(obj_status.getString("message"));
                                     }
                                 } catch (JSONException e) {
                                     mOnTaskListener.onFailure("Failed");
                                     e.printStackTrace();
                                 }
                             }
                         }
                     }
        );

    }
}
