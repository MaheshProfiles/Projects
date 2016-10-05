package com.snapbizz.snaptoolkit.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.domains.SyncInformationBean;

public class SyncDataAdapter extends ArrayAdapter<SyncInformationBean> {
	  private final Context mContext;
	  private LayoutInflater mLayoutInflater;
	  private SyncInformationBean mSyncInformationBean;

	  public SyncDataAdapter(Context context, List<SyncInformationBean> values) {
	    super(context, R.layout.listitem_sync_summary, values);
	    this.mContext = context;
	    this.mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }
	  
	  @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TimestampWrapper timestampWrapper;
		mSyncInformationBean = getItem(position);
		if (null == convertView) {
			convertView = mLayoutInflater.inflate(R.layout.listitem_sync_summary, parent, false);
			timestampWrapper = new TimestampWrapper();
			timestampWrapper.syncTitleTextView = (TextView) convertView.findViewById(R.id.sync_title_textView);
			timestampWrapper.syncTimeTextView = (TextView) convertView.findViewById(R.id.sync_date_textView);
			timestampWrapper.syncRemainingTextView = (TextView) convertView.findViewById(R.id.sync_remaining_textView);
			convertView.setTag(timestampWrapper);
	
		}
		
		timestampWrapper = (TimestampWrapper) convertView.getTag();
		timestampWrapper.syncTitleTextView.setText(mSyncInformationBean.getSyncTitle());
		timestampWrapper.syncTimeTextView.setText(mSyncInformationBean.getSyncDate());
		timestampWrapper.syncRemainingTextView.setText(mSyncInformationBean.getSyncRemaining());
		return convertView;
		
	}

	
	public static class TimestampWrapper {
		public TextView syncTitleTextView;
		public TextView syncTimeTextView;
		public TextView syncRemainingTextView;
	}
}
