package consultation.online.rst.com.onlineconsultation.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import consultation.online.rst.com.onlineconsultation.Activities.LawyerListing;
import consultation.online.rst.com.onlineconsultation.Model.TimeZone;
import consultation.online.rst.com.onlineconsultation.R;

public class AdapterTimeZone extends RecyclerView.Adapter<AdapterTimeZone.MyViewHolder> implements Filterable {
    List<TimeZone> timeZones = new ArrayList<>();
    List<TimeZone> mFilteredList = new ArrayList<>();
    Context context;
    public AdapterTimeZone(Context context, List<TimeZone> timeZones){
        this.context = context;
        this.timeZones = timeZones;
        this.mFilteredList = timeZones;
    }
    @Override
    public AdapterTimeZone.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_timezone,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterTimeZone.MyViewHolder holder, int position) {
        holder.tvTimeZone.setText(mFilteredList.get(position).getZoneName()+ "-" + mFilteredList.get(position).getCountryName());
    }
    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {

                    mFilteredList = timeZones;
                } else {
                    List<TimeZone> filteredList = new ArrayList<>();
                    for (TimeZone timezones : timeZones) {
                        if (timezones.getCountryName().toLowerCase().contains(charString)) {
                            filteredList.add(timezones);
                        }
                    }
                    mFilteredList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (List<TimeZone>) filterResults.values;
                //Log.d("charSequence", String.valueOf(mFilteredList.get(0).getZoneName()));
                notifyDataSetChanged();
            }
        };
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvTimeZone;
        public MyViewHolder(View itemView) {
            super(itemView);
            tvTimeZone = (TextView)itemView.findViewById(R.id.tv_timezone);
            tvTimeZone.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, LawyerListing.class);
            intent.putExtra("selected_living_in_id",mFilteredList.get(getAdapterPosition()).getCountryId());
            intent.putExtra("zone_id",mFilteredList.get(getAdapterPosition()).getCountryId());
            intent.putExtra("zone_name", mFilteredList.get(getAdapterPosition()).getZoneName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
