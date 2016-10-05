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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nerve24.doctor.Listeners.Listener_ClinicWisePriceList;
import com.nerve24.doctor.R;
import com.nerve24.doctor.pojo.PriceSetup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class AdapterClinicWisePrice extends RecyclerView.Adapter<AdapterClinicWisePrice.PriceViewHolders> implements Filterable {
    private String TAG = "", MODULE = "AdapterClinicWisePrice";
    private List<PriceSetup> itemList;
    private ArrayList<PriceSetup> filteredList;
    private SearchFilter searchFilter;
    private Context context;
    private Listener_ClinicWisePriceList listener_listClick;


    public AdapterClinicWisePrice(Context context, ArrayList<PriceSetup> itemList, Listener_ClinicWisePriceList listener_listClick) {
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
            PriceSetup response = filteredList.get(position);
            String clinicName = response.clinicName;
            String clicnicAddress = response.locality;
            String doctorDiscount = response.doctorDiscount;
            String nerve24Discount = response.nerve24Discount;
            String fee = response.fee;
            String slotDuration = "";
            Map<String, String> tempMap = response.paymentMethodMasterMap;
            Map<String, String> paymentMethodMasterMap = new TreeMap<>(tempMap);

            String paymentMethod = "";
            if (paymentMethodMasterMap != null) {
                if (paymentMethodMasterMap.size() > 0) {
                    for (Map.Entry<String, String> entry : paymentMethodMasterMap.entrySet()) {
                        //String key = entry.getKey();
                        String value = entry.getValue();
                        if (paymentMethod.length() == 0) paymentMethod = value;
                        else
                            paymentMethod = paymentMethod + ", " + value;
                    }
                }
            }

            if (fee.contains(".0")) {
                fee = fee.replace(".0", "");
            }

            if (doctorDiscount.contains(".0")) {
                doctorDiscount = doctorDiscount.replace(".0", "");
            }

            if (nerve24Discount.contains(".0")) {
                nerve24Discount = nerve24Discount.replace(".0", "");
            }

            String rs = context.getResources().getString(R.string.Rs);
            holder.tvClinicName.setText(clinicName);
            holder.tvClinicAddress.setText(clicnicAddress);
            holder.tvfee.setText(rs + " " + fee);
            holder.tvDoctorDiscount.setText(doctorDiscount + " %");
            holder.tvnerve24Discount.setText(nerve24Discount + " %");
            holder.tvPayMethods.setText(paymentMethod);


            holder.llRoot.setTag(position);
            holder.ivEdit.setTag(position);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    @Override
    public PriceViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clinicwise_price_list, null);
        PriceViewHolders rcv = new PriceViewHolders(layoutView);
        return rcv;
    }

    @Override
    public int getItemCount() {
        return this.filteredList.size();
    }


    public class PriceViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvfee, tvClinicName, tvClinicAddress, tvDoctorDiscount, tvnerve24Discount, tvPayMethods;
        public LinearLayout llRoot;
        public ImageView ivEdit;


        public PriceViewHolders(View itemView) {
            super(itemView);

            tvfee = (TextView) itemView.findViewById(R.id.tv_fee);
            tvClinicName = (TextView) itemView.findViewById(R.id.tv_clinic_name);
            tvClinicAddress = (TextView) itemView.findViewById(R.id.tv_clinic_address);
            tvDoctorDiscount = (TextView) itemView.findViewById(R.id.tv_doctor_discount);
            tvnerve24Discount = (TextView) itemView.findViewById(R.id.tv_nerve24_dis);
            tvPayMethods = (TextView) itemView.findViewById(R.id.tv_pay_method);
            llRoot = (LinearLayout) itemView.findViewById(R.id.ll_root);

            ivEdit = (ImageView) itemView.findViewById(R.id.iv_edit);

            llRoot.setOnClickListener(this);
            ivEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                TAG = "onClick";
                switch (v.getId()) {
                    case R.id.ll_root:
                        String data = v.getTag().toString();
                        int pos = Integer.parseInt(data);
                        listener_listClick.onClickClinicWiseItem(filteredList.get(pos));
                        break;

                    case R.id.iv_edit:
                        data = v.getTag().toString();
                        pos = Integer.parseInt(data);
                        listener_listClick.oneditItem(filteredList.get(pos));
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
                ArrayList<PriceSetup> tempList = new ArrayList<>();

                // search content in friend list
                for (PriceSetup priceSetup : itemList) {
                    if (priceSetup.clinicName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(priceSetup);
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
            filteredList = (ArrayList<PriceSetup>) results.values;
            Log.e("filteredList", "" + filteredList);
            notifyDataSetChanged();
        }
    }

}





