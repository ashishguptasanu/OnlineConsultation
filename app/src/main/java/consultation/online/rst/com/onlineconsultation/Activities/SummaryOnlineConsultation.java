package consultation.online.rst.com.onlineconsultation.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import consultation.online.rst.com.onlineconsultation.R;


public class SummaryOnlineConsultation extends AppCompatActivity implements View.OnClickListener{
    TextView nameLawyer, nameLawyerCopy, applicantName, applicantGender, applicantAge, applicantReason, orderId, fee, mode;
    SharedPreferences sharedPreferences;
    Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_online_consultation);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        initViews();
    }

    private void initViews() {
        nameLawyer = (TextView)findViewById(R.id.tv_lawyer_name_summary);
        nameLawyerCopy = (TextView)findViewById(R.id.tv_lawyer_name_copy_summary);
        applicantName = (TextView)findViewById(R.id.tv_applicant_name_summary_consultation);
        applicantGender = (TextView)findViewById(R.id.tv_gender_summary_consultation);
        applicantAge = (TextView)findViewById(R.id.tv_age_summary_consultation);
        applicantReason = (TextView)findViewById(R.id.tv_extra_info_summary_consultation);
        orderId = (TextView)findViewById(R.id.tv_order_id_consultation);
        mode = (TextView)findViewById(R.id.tv_mode_consultation);
        fee = (TextView)findViewById(R.id.tv_fee_video_consultation);
        btnSubmit = (Button)findViewById(R.id.btn_submit_summary_online_consultation) ;
        btnSubmit.setOnClickListener(this);
        setDataToViews();
    }

    private void setDataToViews() {
        if(getIntent().getExtras().containsKey("mode")){
            orderId.setText(getIntent().getStringExtra("order_id"));
            fee.setText("USD "+getIntent().getStringExtra("fee"));
            mode.setText(getIntent().getStringExtra("mode"));
        }
        nameLawyer.setText(sharedPreferences.getString("lawyer_name",""));
        nameLawyerCopy.setText(sharedPreferences.getString("lawyer_name",""));
        applicantName.setText(sharedPreferences.getString("applicant_name_consultation",""));
        applicantGender.setText(sharedPreferences.getString("applicant_gender_consultation",""));
        applicantAge.setText(sharedPreferences.getString("applicant_age_consultation",""));
        applicantReason.setText(sharedPreferences.getString("applicant_extra_info_consultation",""));
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), PaymentGateway.class);
        startActivity(intent);
    }
}
