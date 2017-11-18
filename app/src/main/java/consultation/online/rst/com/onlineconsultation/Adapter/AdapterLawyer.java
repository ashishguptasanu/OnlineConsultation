package consultation.online.rst.com.onlineconsultation.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import consultation.online.rst.com.onlineconsultation.Activities.FormActivityLawyer;
import consultation.online.rst.com.onlineconsultation.Model.LawyerList;
import consultation.online.rst.com.onlineconsultation.Model.Mode;
import consultation.online.rst.com.onlineconsultation.R;



public class AdapterLawyer extends RecyclerView.Adapter<AdapterLawyer.MyViewHolder> {
    List<LawyerList> lawyerList = new ArrayList<>();
    List<Mode> modes = new ArrayList<>();
    String shortDescription, longDescription;
    Context context;
    float modePrizeVoice = (float) 0.0;
    float modePriceVideo = (float) 0.0;
    String MODE_URL_1 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fvideo-camera.png?alt=media&token=6704fbb4-34d6-48ab-9c0d-1ed1a81deadb";
    String MODE_URL_2 = "https://firebasestorage.googleapis.com/v0/b/rst-simplified.appspot.com/o/images%2Fauricular-phone-symbol-in-a-circle.png?alt=media&token=b65cfa67-c024-44ab-b182-266502f957e1";
    SharedPreferences sharedPreferences;
    public AdapterLawyer(List<LawyerList> lawyerList, Context context, List<Mode> modes){
        this.lawyerList = lawyerList;
        this.context = context;
        this.modes = modes;

    }
    @Override
    public AdapterLawyer.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lawyer_card,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterLawyer.MyViewHolder holder, int position) {
        Picasso.with(context).load(lawyerList.get(position).getPerImg()).into(holder.imgLawyer);
        holder.tvName.setText(lawyerList.get(position).getPerName());
        holder.tvdesignation.setText(lawyerList.get(position).getPerDesignation());
        holder.tvLocation.setText(lawyerList.get(position).getPerLocation());
        int stringLength = lawyerList.get(position).getPerAbout().length();
        if(stringLength > 100){
            holder.aboutLawyerShort.setText(lawyerList.get(position).getPerAbout().substring(0,88).concat("..."));
        }else{
            holder.aboutLawyerShort.setText(lawyerList.get(position).getPerAbout());
            holder.tvReadDetails.setVisibility(View.GONE);
        }

        for(int x=0; x<modes.size(); x++){
            if(Objects.equals(modes.get(x).getLawyerId(), String.valueOf(position))){
                if(Objects.equals(modes.get(x).getModeId(), "2")){
                    modePrizeVoice = Float.parseFloat(modes.get(x).getModePrice());
                    Picasso.with(context).load(MODE_URL_2).into(holder.imgMode1);
                }

                if(Objects.equals(modes.get(x).getModeId(), "1")){
                    modePriceVideo  = Float.parseFloat(modes.get(x).getModePrice());
                    Picasso.with(context).load(MODE_URL_1).into(holder.imgMode2);
                }
                if(modePrizeVoice < modePriceVideo){
                    holder.modePrice.setText("Starts From USD" + modePrizeVoice);
                }else if(modePriceVideo < modePrizeVoice){
                    holder.modePrice.setText("Starts From USD" + modePriceVideo);
                }

            }
        }
    }

    @Override
    public int getItemCount() {
        return lawyerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvName, tvdesignation, tvLocation, aboutLawyerShort, aboutLawyerLong, modePrice, tvReadDetails;
        ImageView imgLawyer, imgMode1, imgMode2, moveNxt;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView)itemView.findViewById(R.id.tv_lawyer_name);
            //tvName.setOnClickListener(this);
            imgLawyer = (ImageView)itemView.findViewById(R.id.img_lawyer);
            tvdesignation = (TextView)itemView.findViewById(R.id.tv_lawyer_designation);
            tvLocation = (TextView)itemView.findViewById(R.id.tv_lawyer_location);
            imgMode1 = (ImageView)itemView.findViewById(R.id.img_mode1);
            imgMode2 = (ImageView)itemView.findViewById(R.id.img_mode2);
            moveNxt = (ImageView) itemView.findViewById(R.id.move_next_lawyer_listing);
            moveNxt.setOnClickListener(this);
            aboutLawyerShort = (TextView)itemView.findViewById(R.id.about_lawyer_short_description);
            aboutLawyerLong = (TextView)itemView.findViewById(R.id.about_lawyer_long_description);
            modePrice = (TextView)itemView.findViewById(R.id.starting_price_lawyer);
            tvReadDetails = (TextView)itemView.findViewById(R.id.tv_read_details);
            tvReadDetails.setOnClickListener(this);


        }
        @Override
        public void onClick(View view) {
            float voice = 0, video = 0;
            switch (view.getId()){
                case R.id.move_next_lawyer_listing:
                    for(int x=0; x<modes.size(); x++){
                        if(Objects.equals(modes.get(x).getLawyerId(), String.valueOf(getAdapterPosition()))){
                            if(Objects.equals(modes.get(x).getModeId(), "2")){
                                voice = Float.parseFloat(modes.get(x).getModePrice());

                            }
                            if(Objects.equals(modes.get(x).getModeId(), "1")){
                                video  = Float.parseFloat(modes.get(x).getModePrice());
                            }
                        }
                    }
                    sharedPreferences.edit().putString("lawyer_name", lawyerList.get(getAdapterPosition()).getPerName()).apply();
                    sharedPreferences.edit().putString("lawyer_id_selected",lawyerList.get(getAdapterPosition()).getPerId()).apply();
                    Intent intent = new Intent(context, FormActivityLawyer.class);
                    sharedPreferences.edit().putString("lawyer_info_selected",lawyerList.get(getAdapterPosition()).getPerDesignation() + ", " + lawyerList.get(getAdapterPosition()).getPerLocation()).apply();
                    sharedPreferences.edit().putString("lawyer_img_selected",lawyerList.get(getAdapterPosition()).getPerImg()).apply();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("voice", String.valueOf(voice));
                    intent.putExtra("video", String.valueOf(video));
                    context.startActivity(intent);
                    break;
                case R.id.tv_read_details:
                    if(aboutLawyerShort.getVisibility() == View.VISIBLE){
                        aboutLawyerShort.setVisibility(View.GONE);
                        aboutLawyerLong.setVisibility(View.VISIBLE);
                        aboutLawyerLong.setText(lawyerList.get(getAdapterPosition()).getPerAbout());
                        tvReadDetails.setText("Read Less");
                    }else{
                        aboutLawyerShort.setVisibility(View.VISIBLE);
                        aboutLawyerLong.setVisibility(View.GONE);
                        aboutLawyerShort.setText(lawyerList.get(getAdapterPosition()).getPerAbout().substring(0,88).concat("..."));
                        tvReadDetails.setText("Read More");
                    }
                    break;

            }


        }
    }
}
