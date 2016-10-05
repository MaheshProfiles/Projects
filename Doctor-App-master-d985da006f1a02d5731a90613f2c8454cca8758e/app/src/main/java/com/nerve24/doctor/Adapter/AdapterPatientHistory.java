package com.nerve24.doctor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nerve24.doctor.Listeners.Listener_Click_PatientHistory;
import com.nerve24.doctor.R;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.PatientHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class AdapterPatientHistory extends RecyclerView.Adapter<AdapterPatientHistory.PriceViewHolders> implements Filterable {
    private String TAG = "", MODULE = "AdapterPatientHistory";
    private List<PatientHistory> itemList;
    private ArrayList<PatientHistory> filteredList;
    private SearchFilter searchFilter;
    private Context context;
    private Listener_Click_PatientHistory listener_listClick;


    public AdapterPatientHistory(Context context, ArrayList<PatientHistory> itemList,
                                 Listener_Click_PatientHistory listener_listClick) {
        this.itemList = itemList;
        this.filteredList = itemList;
        this.context = context;
        this.listener_listClick = listener_listClick;
        getFilter();
    }

    @Override
    public Filter getFilter() {
        if (searchFilter == null) {
            searchFilter = new SearchFilter();
        }

        return searchFilter;
    }

    public void removeItem(int pos) {
        filteredList.remove(pos);
        notifyItemRemoved(pos);
    }


    @Override
    public void onBindViewHolder(PriceViewHolders holder, int position) {
        try {
            TAG = "onBindViewHolder";
            PatientHistory response = filteredList.get(position);
            String referred = "", name = "", age = "0", gender = "", picture = "", id = "", date = "", time = "", clinic = "", episode = "", encounter = "";
            name = response.patientName;
            age = response.age;
            age=age.equals("0")?"":age+" Years/";
            gender = response.sex;
            id = response.patientNerve24Id;
            date = response.appointmentDate;
            String day=findDay(date);
            date=day.length()==0?date:day;
            time = response.appointmentTime;
            time= Utils.convertTime(time,"HH:mm:ss","hh:mm a");
            episode = response.episodeType;
            encounter = response.encounter;
            encounter=encounter.length()==1?"0"+encounter:encounter;
            referred = response.referedBy;
            clinic=response.clinicName;

            holder.tvName.setText(name);
            holder.tvAge.setText(age + gender);
            holder.tvId.setText("ID " + id);
            holder.tvDayTime.setText(date + " : " + time);
            if(referred.length()>0) {
                holder.tvReferredBy.setVisibility(View.VISIBLE);
                holder.tvReferredBy.setText("Referred By : " + referred);
            }
            else
                holder.tvReferredBy.setVisibility(View.GONE);

            holder.tvClinic.setText("Clinic : " + clinic);
            holder.tvEpisode.setText("Episode "+episode + " : ");
            holder.tvEncounter.setText(encounter);




            holder.tvRefer.setTag(position);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private String findDay(String date) {
        String format="yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format);
        String day = "";
        String today = Utils.getCurrentTimeStamp(format);
        if (today.equals(date)) day = "Today";
        String yesterday=Utils.addDay(today,simpleDateFormat,-1);
        if (yesterday.equals(date)) day = "Yesterday";
        return day;
    }

    @Override
    public PriceViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_history,parent, false);
        PriceViewHolders rcv = new PriceViewHolders(layoutView);
        return rcv;
    }

    @Override
    public int getItemCount() {
        return this.filteredList.size();
    }


    public class PriceViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName, tvAge, tvId, tvDayTime, tvReferredBy, tvClinic, tvEpisode, tvEncounter, tvRefer;
        public ImageView ivpatient;


        public PriceViewHolders(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvAge = (TextView) itemView.findViewById(R.id.tv_age);
            tvId = (TextView) itemView.findViewById(R.id.tv_id);
            tvDayTime = (TextView) itemView.findViewById(R.id.tv_day_time);
            tvReferredBy = (TextView) itemView.findViewById(R.id.tv_referred_by);
            tvClinic = (TextView) itemView.findViewById(R.id.tv_clinic);
            tvEpisode = (TextView) itemView.findViewById(R.id.tv_episode);
            tvEncounter = (TextView) itemView.findViewById(R.id.tv_encounter);
            tvRefer = (TextView) itemView.findViewById(R.id.tv_refer);

            tvRefer.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                TAG = "onClick";
                switch (v.getId()) {
                    case R.id.tv_refer:
                        String data = v.getTag().toString();
                        int pos = Integer.parseInt(data);
                        listener_listClick.onClickPatientHistory(filteredList.get(pos));
                        break;

                }
            } catch (Exception e) {
                Log.e(MODULE, TAG);
                e.printStackTrace();
            }
        }

    }


    private class SearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<PatientHistory> tempList = new ArrayList<>();

                // search content in friend list
                for (PatientHistory PatientHistory : itemList) {
                    if (PatientHistory.patientName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(PatientHistory);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = itemList.size();
                filterResults.values = itemList;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         *
         * @param constraint text
         * @param results    filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<PatientHistory>) results.values;
            Log.e("filteredList", "" + filteredList);
            notifyDataSetChanged();
        }
    }

}





