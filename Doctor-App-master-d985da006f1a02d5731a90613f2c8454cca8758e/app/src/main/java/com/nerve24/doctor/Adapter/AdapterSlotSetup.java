package com.nerve24.doctor.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nerve24.doctor.Listeners.Listener_Click_SlotSetup;
import com.nerve24.doctor.R;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.SlotClinic;
import com.nerve24.doctor.pojo.SlotDay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class AdapterSlotSetup extends RecyclerView.Adapter<AdapterSlotSetup.SlotSetupViewHolders> implements Filterable {

    private List<SlotClinic> itemList;
    private ArrayList<SlotClinic> filteredList;
    private SearchFilter searchFilter;
    private Context context;
    private Listener_Click_SlotSetup listener_listClick;
    private ArrayList<String> daysList = new ArrayList<>();


    private String TAG = "AdapterSlotSetup", MODULE = "AdapterSlotSetup";

    public AdapterSlotSetup(Context context, ArrayList<SlotClinic> itemList, Listener_Click_SlotSetup listener_listClick) {
        this.itemList = itemList;
        this.filteredList = itemList;
        this.context = context;
        this.listener_listClick = listener_listClick;
        addDays();
        getFilter();
    }

    private void addDays() {
        daysList.add("monday");
        daysList.add("tuesday");
        daysList.add("wednesday");
        daysList.add("thursday");
        daysList.add("friday");
        daysList.add("saturday");
        daysList.add("sunday");
    }


    @Override
    public void onBindViewHolder(SlotSetupViewHolders holder, int position) {
        SlotClinic response = filteredList.get(position);
        String clinicName = response.clinicName;
        String clinicPlace = response.locality;
        HashMap<String, ArrayList<SlotDay>> timingSheduleMap = response.timingSheduleMap;
        HashMap<String, ArrayList<String>> dayTimemap = new HashMap<>();

        for (int i = 0; i < daysList.size(); i++) {
            ArrayList<SlotDay> slotDayList = timingSheduleMap.get(daysList.get(i));
            if (slotDayList != null) {
                ArrayList<String> timeList = new ArrayList<>();
                for (int j = 0; j < slotDayList.size(); j++) {
                    SlotDay slotDay = slotDayList.get(j);
                    if (timeList.contains(slotDay.startEndTime)) {
                    } else {
                        if (slotDay.checked.equals("true")) {
                            timeList.add(slotDay.startEndTime);
                        }
                    }
                }
                Collections.sort(timeList);
                dayTimemap.put(daysList.get(i), timeList);
            }
        }

        HashMap<String, ArrayList<String>> dayTimemapfinal = new HashMap<>();
        ArrayList<String> dayListfinal = new ArrayList<>();

        ArrayList<String> dayAddedList = new ArrayList<>();
        for (int i = 0; i < daysList.size(); i++) {
            String day = daysList.get(i);
            if (!dayAddedList.contains(day)) {
                ArrayList<String> timeList = dayTimemap.get(day);
                String days = day.substring(0, 1).toUpperCase() + day.substring(1, 3);

                for (int y = 1; y < daysList.size(); y++) {
                    String day1 = daysList.get(y);
                    if (!dayAddedList.contains(day1) && !day1.equals(day)) {
                        ArrayList<String> timeList1 = dayTimemap.get(day1);
                        if (timeList1 != null && timeList != null) {
                            if (timeList.equals(timeList1)) {
                                dayAddedList.add(day1);
                                day1 = day1.substring(0, 1).toUpperCase() + day1.substring(1, 3);
                                days = days + ", " + day1.substring(0, 3);
                            }
                        }
                    }
                }

                dayAddedList.add(day);

                dayTimemapfinal.put(days, timeList);
                dayListfinal.add(days);
            }

        }

        if (holder.llRoot.getChildCount() > 0) {
            holder.llRoot.removeAllViewsInLayout();
        }
        for (int i = 0; i < dayTimemapfinal.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View dayView = inflater.inflate(R.layout.item_slot_day, null);
            TextView tvDay = (TextView) dayView.findViewById(R.id.tv_days);
            LinearLayout llTime = (LinearLayout) dayView.findViewById(R.id.ll_time);

            String day = dayListfinal.get(i);
            tvDay.setText(day);
            ArrayList<String> timeList = dayTimemapfinal.get(day);
            if (timeList != null) {
                if (timeList.size() > 1) {
                    timeList = sort(timeList);
                }
                for (int k = 0; k < timeList.size(); k++) {
                    LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View timeView = inflater1.inflate(R.layout.item_slot_day_time, null);
                    TextView tvTime = (TextView) timeView.findViewById(R.id.tv_time);
                    tvTime.setText(timeList.get(k));
                    llTime.addView(timeView);
                }
                if (timeList.size() > 0) {
                    holder.llRoot.addView(dayView);
                }
            }


        }


        SpannableString ss1=  new SpannableString(clinicName);
        ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, ss1.length(), 0);
        //Spanned text = Html.fromHtml("<b>"+clinicName+"</b>, "+clinicPlace);
        Spanned text = Html.fromHtml(clinicName+", "+clinicPlace);

        holder.tvNamePlace.setText(text);
        holder.ivEdit.setTag(position);
        holder.rl_root.setTag(position);
    }

    private ArrayList<String> sort(ArrayList<String> timeList) {
        String time1 = timeList.get(0).substring(0, 8).trim();
        String time2 = timeList.get(1).substring(0, 8).trim();
        float time1_24 = Utils.convertTimeFormat(time1);
        float time2_24 = Utils.convertTimeFormat(time2);
        Log.e("time1_24",""+time1_24);
        Log.e("time2_24",""+time2_24);

        if (time1_24 < time2_24) {
        } else {
            Collections.reverse(timeList);
        }

        return timeList;
    }

    @Override
    public SlotSetupViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot, null);
        SlotSetupViewHolders rcv = new SlotSetupViewHolders(layoutView);
        return rcv;
    }

    @Override
    public int getItemCount() {
        return this.filteredList.size();
    }


    public class SlotSetupViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvNamePlace;
        public LinearLayout llRoot;
        public ImageView ivEdit;
        public RelativeLayout rl_root;


        public SlotSetupViewHolders(View itemView) {
            super(itemView);
            tvNamePlace = (TextView) itemView.findViewById(R.id.tv_name_place);
            llRoot = (LinearLayout) itemView.findViewById(R.id.ll_days);
            rl_root = (RelativeLayout) itemView.findViewById(R.id.rl_root);
            ivEdit = (ImageView) itemView.findViewById(R.id.iv_edit);
            rl_root.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_root:
                    String data = v.getTag().toString();
                    int pos = Integer.parseInt(data);
                    listener_listClick.onEditItem(filteredList.get(pos));
                    break;

            }
        }

    }

    @Override
    public Filter getFilter() {
        if (searchFilter == null) {
            searchFilter = new SearchFilter();
        }

        return searchFilter;
    }


    private class SearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<SlotClinic> tempList = new ArrayList<>();

                // search content in friend list
                for (SlotClinic slotClinic : itemList) {
                    if (slotClinic.clinicName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(slotClinic);
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
            filteredList = (ArrayList<SlotClinic>) results.values;
            Log.e("filteredList", "" + filteredList);
            notifyDataSetChanged();
        }
    }


}





