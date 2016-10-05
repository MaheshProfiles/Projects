package com.nerve24.doctor.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nerve24.doctor.Listeners.Listener_Daily_Wise_Slot;
import com.nerve24.doctor.R;
import com.nerve24.doctor.pojo.DailyWiseSlotDay;

import java.util.ArrayList;
import java.util.List;


public class AdapterDailyWise extends RecyclerView.Adapter<AdapterDailyWise.PriceViewHolders> implements Filterable  {
    private String TAG = "", MODULE = "AdapterDailyWise";

    private List<DailyWiseSlotDay> itemList;
    private ArrayList<DailyWiseSlotDay> filteredList;
    private SearchFilter searchFilter;
    private Context context;
    private Listener_Daily_Wise_Slot listener_listClick;

    public AdapterDailyWise(Context context, ArrayList<DailyWiseSlotDay> itemList, Listener_Daily_Wise_Slot listener_listClick) {
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
        DailyWiseSlotDay response = filteredList.get(position);
        String clinicName = response.clinicName;
        String startTime = response.startTime;
        String endTime = response.endTime;
        holder.tvClinicName.setText(clinicName);
        holder.tvClinicTime.setText("Time Slot : "+startTime+" - "+endTime);
        holder.llRoot.setTag(position);
    }

    @Override
    public PriceViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dailywise, null);
        PriceViewHolders rcv = new PriceViewHolders(layoutView);
        return rcv;
    }

    @Override
    public int getItemCount() {
        return this.filteredList.size();
    }


    public class PriceViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvClinicName, tvClinicTime;
        public LinearLayout llRoot;


        public PriceViewHolders(View itemView) {
            super(itemView);
            tvClinicName = (TextView) itemView.findViewById(R.id.tv_clinic_name);
            tvClinicTime = (TextView) itemView.findViewById(R.id.tv_clinic_timing);
            llRoot=(LinearLayout)  itemView.findViewById(R.id.ll_root);
            llRoot.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_root:
                    String data = v.getTag().toString();
                    int pos = Integer.parseInt(data);
                    listener_listClick.onClickDailyWiseItem(filteredList.get(pos));
                    break;

            }
        }

    }

    private class SearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<DailyWiseSlotDay> tempList = new ArrayList<>();

                // search content in friend list
                for (DailyWiseSlotDay dailyWiseSlotDay : itemList) {
                    if (dailyWiseSlotDay.clinicName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(dailyWiseSlotDay);
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
            filteredList = (ArrayList<DailyWiseSlotDay>) results.values;
            Log.e("filteredList", "" + filteredList);
            notifyDataSetChanged();
        }
    }

}





