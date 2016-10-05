package com.nerve24.doctor.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
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

import com.nerve24.doctor.Listeners.Listener_Calendar_DayView;
import com.nerve24.doctor.R;
import com.nerve24.doctor.Utility.CircularTextView;
import com.nerve24.doctor.pojo.Appointment;
import com.nerve24.doctor.pojo.SlotWithAppointment;

import java.util.ArrayList;
import java.util.HashMap;


public class AdapterCalendarDayView extends RecyclerView.Adapter<AdapterCalendarDayView.CalendarViewHolder> implements Filterable {
    private String TAG = "", MODULE = "AdapterCalendarDayView";
    private HashMap<String, ArrayList<SlotWithAppointment>> itemList;
    private ArrayList<String> timeList;
    private ArrayList<String> filtered_timeList;
    private HashMap<String, ArrayList<SlotWithAppointment>> filteredList;
    private SearchFilter searchFilter;
    private Context context;
    private Listener_Calendar_DayView listener_listClick;
    private int colorWhite, colorGrey, colorBlack;



    public AdapterCalendarDayView(Context context, HashMap<String, ArrayList<SlotWithAppointment>> itemList, ArrayList<String> timeList, Listener_Calendar_DayView listener_listClick) {
        this.itemList = itemList;
        this.filteredList = itemList;
        this.timeList = timeList;
        this.filtered_timeList = timeList;
        this.context = context;
        this.listener_listClick = listener_listClick;
        colorWhite = this.context.getResources().getColor(R.color.white);
        colorGrey = this.context.getResources().getColor(R.color.grey_txt);
        colorBlack = this.context.getResources().getColor(R.color.black);

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
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        try {
            TAG = "onBindViewHolder";
            String time = filtered_timeList.get(position);
            ArrayList<SlotWithAppointment> slotResponse = filteredList.get(time);
            holder.tvTime.setText(time);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (holder.llAppointments.getChildCount() > 0)
                holder.llAppointments.removeAllViewsInLayout();
            View viewRoot = inflater.inflate(R.layout.item_appointment_day_view, null);
            LinearLayout llRoot = (LinearLayout) viewRoot.findViewById(R.id.ll_root);

            if (slotResponse != null) {
                for (int i = 0; i < slotResponse.size(); i++) {
                    SlotWithAppointment slotWithAppointment = slotResponse.get(i);
                    String premium = slotWithAppointment.premium;
                    ArrayList<Appointment> appointmentsList = slotWithAppointment.appointmentsList;
                    Log.e("appointmentsList size",""+appointmentsList.size());
                    for (int z = 0; z < appointmentsList.size(); z++) {
                        Log.e("z",""+z);
                        final Appointment appointment = appointmentsList.get(z);
                        View view = null;
                        int count = llRoot.getChildCount();
                        if (count % 2 == 0)
                            view = inflater.inflate(R.layout.item_appointment_day_view_left, null);
                        else
                            view = inflater.inflate(R.layout.item_appointment_day_view_right, null);
                        RelativeLayout rlAppointment = (RelativeLayout) view.findViewById(R.id.rl_appointment);
                        CircularTextView tvNo = (CircularTextView) view.findViewById(R.id.tv_no);
                        tvNo.setVisibility(View.GONE);
                        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                        ImageView ivStar = (ImageView) view.findViewById(R.id.iv_star);
                        String appointmentStatus = "", name = "";
                        appointmentStatus = appointment.appointmentStatus;
                        name = appointment.firstName;
                        tvName.setText(name);
                        if (premium.equals("true"))
                            ivStar.setVisibility(View.VISIBLE);
                        else
                            ivStar.setVisibility(View.GONE);

                        rlAppointment.setBackgroundResource(R.drawable.round_rect_red);
                        if (appointmentStatus.equals("Missed") || appointmentStatus.equals("Completed")) {
                            rlAppointment.setBackgroundResource(R.drawable.round_rect_light_grey);
                            tvName.setText(name);
                            tvName.setTextColor(colorGrey);
                            ivStar.setBackgroundResource(R.drawable.ic_star);
                        } else if (appointmentStatus.equals("In Consultation")) {
                            rlAppointment.setBackgroundResource(R.drawable.round_rect_green);
                            tvName.setText(name);
                            tvName.setTextColor(colorWhite);
                            ivStar.setBackgroundResource(R.drawable.ic_star_white);
                        } else if (appointmentStatus.equals("Checked In")) {
                            rlAppointment.setBackgroundResource(R.drawable.round_rect_red);
                            tvName.setText(name);
                            tvName.setTextColor(colorWhite);
                            ivStar.setBackgroundResource(R.drawable.ic_star_white);
                        } else if (appointmentStatus.equals("Booked")) {
                            rlAppointment.setBackgroundResource(R.drawable.round_rect_light_red);
                            tvName.setText(name);
                            tvName.setTextColor(colorBlack);
                            ivStar.setBackgroundResource(R.drawable.ic_star_black);
                        }
                        tvName.setTypeface(null, Typeface.BOLD);

                        rlAppointment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                listener_listClick.onClickAppointment(appointment);
                            }
                        });

                        llRoot.addView(view);
                    }
                }
                holder.llAppointments.addView(viewRoot);
            }


        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day_view, null);
        CalendarViewHolder rcv = new CalendarViewHolder(layoutView);
        return rcv;
    }

    @Override
    public int getItemCount() {
        return this.filtered_timeList.size();
    }


    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTime;
        public LinearLayout llAppointments;

        public CalendarViewHolder(View itemView) {
            super(itemView);

            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            llAppointments = (LinearLayout) itemView.findViewById(R.id.ll_appointments);
        }
    }


    private class SearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                Log.e("constraint",""+constraint);
                HashMap<String, ArrayList<SlotWithAppointment>> tempList = new HashMap<>();
                ArrayList<String> filtered_timeList1=new ArrayList<>();
                for (int i = 0; i < filtered_timeList.size(); i++) {
                    String time = filtered_timeList.get(i);
                    ArrayList<SlotWithAppointment> appointmentArrayList = itemList.get(time);
                    if (appointmentArrayList != null) {
                        for (SlotWithAppointment appointment : appointmentArrayList) {
                            ArrayList<Appointment> appointmentsList = appointment.appointmentsList;

                            boolean flag = false;
                            for (Appointment appointment1 : appointmentsList) {
                                if (appointment1.firstName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                                    flag = true;
                                    break;
                                }
                            }

                            if (flag) {
                                tempList.put(time, appointmentArrayList);
                                if(!filtered_timeList1.contains(time))
                                filtered_timeList1.add(time);
                            }

                        }
                    }
                }

                filterResults.count = tempList.size();
                filtered_timeList=filtered_timeList1;
                filterResults.values = tempList;
            } else {
                filterResults.count = timeList.size();
                filtered_timeList=timeList;
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
            filteredList = (HashMap<String, ArrayList<SlotWithAppointment>>) results.values;
            Log.e("filteredList", "" + filteredList);
            notifyDataSetChanged();
        }
    }

}





