package consultation.online.rst.com.onlineconsultation.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.checkout.CheckoutKit;
import com.checkout.exceptions.CardException;
import com.checkout.exceptions.CheckoutException;
import com.checkout.httpconnector.Response;
import com.checkout.models.Card;
import com.checkout.models.CardTokenResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import consultation.online.rst.com.onlineconsultation.R;
import de.mrapp.android.dialog.MaterialDialog;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PaymentGateway extends AppCompatActivity {
    private  static String testingKey = "pk_test_73e56b01-8726-4176-9159-db71454ff4af";
    String PAYMENT_ONLINE_CONSULTATION = "https://uk-immigrationhub.com/checkout-form/api_cardToken.php";
    String EMAIL_ONLINE_CONSULTATION = "https://uk-immigrationhub.com/checkout-form/api_cardTokennxt.php";
    SharedPreferences sharedPreferences;
    Button mButton;
    EditText emailId, applicantName;
    TextView amountPayable, referenceNumber;
    String totalFee, resp, dataMsg;
    int paymentCode;
    ProgressBar progressPayment;
    private OkHttpClient client = new OkHttpClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_payment_gateway);
        amountPayable = (TextView)findViewById(R.id.tv_amount_payable);
        referenceNumber = (TextView)findViewById(R.id.tv_reference_no);
        mButton = (Button)findViewById(R.id.button_payment);
        emailId = (EditText)findViewById(R.id.card_applicant_email);
        applicantName = (EditText)findViewById(R.id.card_name);
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        progressPayment = (ProgressBar)findViewById(R.id.progress_payment);
        progressPayment.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressPayment.setVisibility(View.VISIBLE);
                if(sharedPreferences.getInt("visa_id",0) == 102){
                    new ConnectionTaskConsultation().execute();
                    /*Intent intent = new Intent(getApplicationContext(), SlotBookingConsultation.class);
                    startActivity(intent);*/
                }
            }
        });
        if(sharedPreferences != null && sharedPreferences.getInt("visa_id",0) == 102){
            amountPayable.setText("USD " + String.valueOf(sharedPreferences.getString("applicant_consultation_fee","")));
            referenceNumber.setText("Reference No: " + sharedPreferences.getString("order_id",""));
            emailId.setText(sharedPreferences.getString("applicant_email_consultation",""));
            applicantName.setText(sharedPreferences.getString("applicant_name_consultation",""));
        }
    }
    private class ConnectionTaskConsultation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            final EditText name = (EditText) findViewById(R.id.card_name);
            final EditText number = (EditText) findViewById(R.id.card_number);
            final EditText expMonth = (EditText) findViewById(R.id.card_month);
            final EditText expYear = (EditText) findViewById(R.id.card_year);
            final EditText cvv = (EditText) findViewById(R.id.card_cvv);

            try {
                /* Create the card object */
                Card card = new Card(number.getText().toString(), name.getText().toString(), expMonth.getText().toString(), expYear.getText().toString(), cvv.getText().toString());
                /* Create the CheckoutKit instance */
                CheckoutKit ck = CheckoutKit.getInstance(testingKey);
                final Response<CardTokenResponse> resp = ck.createCardToken(card);
                if (resp.hasError) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            paymentUnsuccessfulDialog();
                        }
                    });
                } else {
                    String cardToken = resp.model.getCardToken();
                    sendPaymentOnlineConsultation(cardToken, PAYMENT_ONLINE_CONSULTATION, EMAIL_ONLINE_CONSULTATION);

                }
            } catch (final CardException | CheckoutException e1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        paymentUnsuccessfulDialog();
                    }
                });

            } catch (IOException e) {
                //e.printStackTrace();
            }
            return "";
        }
    }
    private void paymentUnsuccessfulDialog(){
        progressPayment.setVisibility(View.GONE);
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this);
        dialogBuilder.setTitle("Oops! Payment Failed");
        dialogBuilder.setMessage("Please enter the valid details");
        dialogBuilder.showHeader(true);
        dialogBuilder.setHeaderBackground(R.mipmap.unsuccessful_background);
        dialogBuilder.setHeaderHeight(400);
        dialogBuilder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new ConnectionTaskConsultation().execute();
            }
        });
        dialogBuilder.setNegativeButton(android.R.string.cancel, null);
        MaterialDialog dialog = dialogBuilder.create();
        dialog.show();
    }
    private void sendPaymentOnlineConsultation(String cardToken, String paymentUrl, final String emailUrl){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("merchantTxnId", sharedPreferences.getString("order_id",""))
                .addFormDataPart("email", sharedPreferences.getString("applicant_email_consultation",""))
                .addFormDataPart("total", sharedPreferences.getString("applicant_consultation_fee",""))
                .addFormDataPart("card_token",cardToken)
                .addFormDataPart("currency", "USD")
                .addFormDataPart("card_holder_name", sharedPreferences.getString("applicant_name_consultation",""))
                .build();
        Request request = new Request.Builder().url(paymentUrl).post(requestBody).build();
        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            public static final String MODE_PRIVATE = "";
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                System.out.println("Registration Error" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        paymentUnsuccessfulDialog();
                    }
                });
            }
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String networkResp = response.body().string();
                        Log.d("resp",networkResp);
                        JSONObject obj= null;
                        try {
                            obj = new JSONObject(networkResp);
                            JSONObject obj_response=obj.getJSONObject("Response");
                            Log.d("response", String.valueOf(obj_response));
                            JSONObject obj_data=obj_response.getJSONObject("data");
                            paymentCode = obj_data.getInt("code");
                            dataMsg = obj_data.getString("message");
                            if(paymentCode == 10000){
                                sendEmailPayment(sharedPreferences.getString("order_id",""),10000,emailUrl);
                                Intent intent = new Intent(getApplicationContext(), SlotBookingConsultation.class);
                                startActivity(intent);
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        paymentUnsuccessfulDialog();
                                        Toast.makeText(getApplicationContext(),"Payment Unsuccessful",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                sendEmailPayment(sharedPreferences.getString("order_id",""),30015,emailUrl);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    Log.d("Resp", "caught error");
                }
            }

        });
    }
    private void sendEmailPayment(String response, int responseCode, String emailUrl){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("visa_id", response)
                .addFormDataPart("response_code", String.valueOf(responseCode))
                .build();
        Request request = new Request.Builder().url(emailUrl ).post(requestBody).build();
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
                }
            }
        });
    }
}
