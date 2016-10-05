package com.nerve24.doctor.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nerve24.doctor.R;
import com.nerve24.doctor.Utility.Utils;
import com.nerve24.doctor.pojo.SlotFee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AdapterSlotFee extends RecyclerView.Adapter<AdapterSlotFee.PriceViewHolders> {
    private String TAG = "", MODULE = "AdapterClinicWisePrice";
    private List<SlotFee> itemList;
    private Activity mActivity;
    private HashMap<String, String> mapFee = new HashMap<>();
    private HashMap<String, String> mapStar = new HashMap<>();
    private String fromTime = "", toTime = "", fee = "";

    private String rs = "";
    public View view_LastFocused = null;
    public boolean override = false;


    public AdapterSlotFee(Activity mActivity, ArrayList<SlotFee> itemList, String fromTime, String toTime, String fee) {
        this.itemList = itemList;
        this.mActivity = mActivity;
        rs = mActivity.getResources().getString(R.string.Rs);
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.fee = fee;
    }

    public HashMap<String, String> getFeeUpdate() {
        return mapFee;
    }

    public HashMap<String, String> getStarUpdate() {
        return mapStar;
    }

    public boolean getOverride() {
        return override;
    }

    public void removeItem(int pos) {
        itemList.remove(pos);
        notifyItemRemoved(pos);
    }


    @Override
    public void onBindViewHolder(PriceViewHolders holder, int position) {
        try {
            TAG = "onBindViewHolder";
            SlotFee response = itemList.get(position);
            String startTime = response.startTime;
            String endTime = response.endTime;
            String isChecked = response.premium;
            String fee = response.fee;
            String slotId = response.slotId;


            Log.e("response.override", "-->" + response.override);
            if (response.override.equals("true")) {
                fee = response.fee;
            }
            if (this.fee != null) {
                if (this.fee.length() > 0) {
                    if (Utils.isFromLaysForPricing(this.fromTime, this.toTime, startTime)) {

                        fee = this.fee;
                        mapFee.put(slotId, fee.trim());
                        override=true;
                        Log.e("override", "-->" + override);

                    }
                }
            }
            if (fee.contains(".0")) {
                fee = fee.replace(".0", "");
            }
            holder.etFee.setText(rs + " " + fee);
            holder.tvTime.setText(startTime);
            if (isChecked.equals("true")) {
                holder.ivStar.setBackgroundResource(R.drawable.ic_star_black);
            } else {
                holder.ivStar.setBackgroundResource(R.drawable.ic_star);
            }

            holder.ivStar.setTag(isChecked + position);
            holder.etFee.setTag(position);
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }

    }

    @Override
    public PriceViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slot_fee, null);
        PriceViewHolders rcv = new PriceViewHolders(layoutView);
        return rcv;
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }


    public class PriceViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvTime;
        public EditText etFee;
        public ImageView ivStar;


        public PriceViewHolders(View itemView) {
            super(itemView);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            etFee = (EditText) itemView.findViewById(R.id.et_fee);
            ivStar = (ImageView) itemView.findViewById(R.id.iv_star);
            ivStar.setOnClickListener(this);
            etFee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        feeProcess(v);
                    } else {
                        Log.e("focused", "focused");
                        view_LastFocused = v;
                    }
                }
            });

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_star:
                    starProcess(v);
                    break;
            }
        }

    }

    public void feeProcess(View v) {
        try {
            TAG = "feeProcess";
            EditText et_LastFocused = (EditText) v;
            int pos = Integer.parseInt(et_LastFocused.getTag().toString());
            String slotId = itemList.get(pos).slotId;
            String fee = et_LastFocused.getText().toString().trim();
            override=true;

            if (!fee.contains(rs)) {
                mapFee.put(slotId, fee);
                fee = rs + " " + fee;
                et_LastFocused.setText(fee);
            } else {
                fee = fee.replace(rs, "");
                mapFee.put(slotId, fee.trim());
                fee = rs + " " + fee;
                et_LastFocused.setText(fee);
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }

    private void starProcess(View v) {
        try {
            TAG = "starProcess";
            Log.e(MODULE, TAG);
            ImageView iv = (ImageView) v;
            String tag = iv.getTag().toString();
            Log.e("tag--->", "" + tag);
            int pos = 0;
            boolean checked = false;
            if (tag.contains("true")) {
                checked = true;
                tag = tag.replaceAll("true", "");
                pos = Integer.parseInt(tag);
            } else {
                checked = false;
                tag = tag.replaceAll("false", "");
                pos = Integer.parseInt(tag);
            }

            String slotId = itemList.get(pos).slotId;
            if (!checked) {
                Log.e("star", "checked");
                iv.setBackgroundResource(R.drawable.ic_star_black);
                mapStar.put(slotId, "true");
                iv.setTag("true" + pos);
            } else {
                Log.e("star", "unchecked");
                iv.setBackgroundResource(R.drawable.ic_star);
                mapStar.put(slotId, "false");
                iv.setTag("false" + pos);
            }
        } catch (Exception e) {
            Log.e(MODULE, TAG);
            e.printStackTrace();
        }
    }


}





