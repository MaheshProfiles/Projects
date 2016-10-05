package com.snapbizz.snapbilling.adapters;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.domains.Campaigns;
import com.snapbizz.snaptoolkit.services.GetCampaignImageService;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CampaignAdapter extends ArrayAdapter<Campaigns> {
	private static final String TAG=CampaignAdapter.class.getSimpleName();
	private LayoutInflater inflater;
	private int lastSelectedPos = -1;
	public View lastSelectedView;
	private Context context;
	private List<Campaigns> mCampList = null;
	public static  ProgressDialog progress = null;
	public CampaignAdapter(Context context, List<Campaigns> campList) {
		super(context, android.R.id.text1, campList);
		this.context = context;
		mCampList = campList;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mCampList.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final CampaignProductsWrapper campProductsWrapper;
		if (convertView == null) {
			campProductsWrapper = new CampaignProductsWrapper();
			convertView = inflater.inflate(R.layout.campaign_row, null);
			campProductsWrapper.productNameTextView = (TextView) convertView
					.findViewById(R.id.campaign_name_text);
			campProductsWrapper.productCampCodeTextView = (TextView) convertView
					.findViewById(R.id.campaign_code_text);
			campProductsWrapper.productCompNmTextView = (TextView) convertView
					.findViewById(R.id.campaign_comapny_text);
			campProductsWrapper.productStartDtTextView = (TextView) convertView
					.findViewById(R.id.campaign_startdt_text);
			campProductsWrapper.productEndDtTextView = (TextView) convertView
					.findViewById(R.id.campaign_enddt_text);
			campProductsWrapper.productCampTypeTextView = (TextView) convertView
					.findViewById(R.id.campaign_type_text);
			campProductsWrapper.productImageView = (ImageView) convertView
					.findViewById(R.id.campaign_imageview);
			campProductsWrapper.imageDownloadedView=(ImageView) convertView
					.findViewById(R.id.image_status);
			convertView.setTag(campProductsWrapper);
		} else {
			campProductsWrapper = (CampaignProductsWrapper) convertView
					.getTag();
		}
		Campaigns camp = getItem(position);
		campProductsWrapper.productNameTextView.setText(camp.getName()
				.toString());

		campProductsWrapper.productCampTypeTextView.setText(camp
				.getCampaign_type().toString());
		
		campProductsWrapper.imageDownloadedView.setSelected(camp.isHasImage());
		Log.e("sync Camp", SnapToolkitConstants.VISIBILITY_IMAGE_PATH+"campaign/"+camp.getImage_uid());
		Log.e("sync Camp", "Campaign Image task----->"+camp.getName()+"----"+camp.isHasImage());
				 

		try {
			campProductsWrapper.productImageView
			.setImageDrawable(SnapCommonUtils.getProductDrawable(SnapToolkitConstants.VISIBILITY_IMAGE_PATH+"campaign/"+camp.getImage_uid(), SnapCommonUtils
			                .getSnapContext(context)));
			
			campProductsWrapper.productImageView.setTag(position);
			campProductsWrapper.productImageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (SnapCommonUtils.isNetworkAvailable(context)) {
						if(SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(context))==0){
							progress = new ProgressDialog(context);
							progress.setCanceledOnTouchOutside(false);
							progress.setMessage(context.getResources().getString(R.string.downloadingCampaign));
							progress.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.alert_sync_restart) + "\n" +context.getString(R.string.alert_multiple_download), new DialogInterface.OnClickListener() {
							    @Override
							    public void onClick(DialogInterface dialog, int which) {
							    	DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
							    	int number=downloadManager.remove(SnapSharedUtils.getDownloadingId(SnapCommonUtils.getSnapContext(context)));
							    	Log.e("TAG", "downloadManager--------number--->"+number);
							    	if(number>0){
							    		SnapSharedUtils.setCampaignSyncStatus(false,SnapCommonUtils.getSnapContext(context));
							    		SnapSharedUtils.storeDownloadingId(SnapCommonUtils.getSnapContext(context),0);
							    		CustomToast.showCustomToast(context
											.getApplicationContext(), context.getString(R.string.alert_stop_download),
											Toast.LENGTH_SHORT, CustomToast.ERROR);
							    		dialog.dismiss();
							    	}
							    }
							});
							progress.show();
							Campaigns currentCampaign = getItem( (Integer) v.getTag());
							List<Campaigns> campaignsList= new ArrayList<Campaigns>();
							campaignsList.add(currentCampaign);
							Intent imageDownloadIntent = new Intent(context,GetCampaignImageService.class);
							Bundle bundleObject = new Bundle();
							bundleObject.putSerializable("key", (Serializable) campaignsList);               
							imageDownloadIntent.putExtras(bundleObject);
							context.startService(imageDownloadIntent);
						}
						}else {
							CustomToast.showCustomToast(context
								.getApplicationContext(), context.getString(R.string.no_network),
								Toast.LENGTH_SHORT, CustomToast.ERROR);
						}
				}
			});
			
		} catch (Exception e1) {
			Log.e("Image Store Excepion", e1.getMessage());
			e1.printStackTrace();
		}

		
		campProductsWrapper.productCampCodeTextView.setText(camp.getCode()
				.toString());

		campProductsWrapper.productCompNmTextView.setText(camp.getCompany()
				.toString());
		SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd");
		try {
			campProductsWrapper.productStartDtTextView.setText(DATE_FORMAT.format(DATE_FORMAT.parse(camp.getStart_date())));
			campProductsWrapper.productEndDtTextView.setText(DATE_FORMAT.format(DATE_FORMAT.parse(camp.getEnd_date())));
		} catch (Exception e) {
			Log.e(TAG, "Date format error-"+e);
		}
		campProductsWrapper.productImageView.setTag(position);
		campProductsWrapper.productNameTextView.setTag(position);
		campProductsWrapper.productCampCodeTextView.setTag(position);
		campProductsWrapper.productCampTypeTextView.setTag(position);
		campProductsWrapper.productCompNmTextView.setTag(position);
		campProductsWrapper.productStartDtTextView.setTag(position);
		campProductsWrapper.productEndDtTextView.setTag(position);
		campProductsWrapper.imageDownloadedView.setTag(position);
		return convertView;
	}

	public Campaigns getLastSelectedItem() {
		if (lastSelectedPos != -1)
			return getItem(lastSelectedPos);
		else
			return null;
	}
	
	public void dismissProgressDialog(){
		if (progress != null) {
			progress.dismiss();
		}
	}

	private class CampaignProductsWrapper {
		private TextView productNameTextView;
		private ImageView productImageView;
		private TextView productCampCodeTextView;
		private TextView productCompNmTextView;
		private TextView productStartDtTextView;
		private TextView productEndDtTextView;
		private TextView productCampTypeTextView;
		private ImageView imageDownloadedView;

	}

}
