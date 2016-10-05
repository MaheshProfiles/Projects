package com.snapbizz.snapstock.adapters;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.fragments.DistributorListFragment.DistributorSelectListener;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Distributor;

public class DistributorListAdapter extends ArrayAdapter<Distributor> {

	private LayoutInflater layoutInflater;
	private List<Distributor> distributorList;
	private int[] totalgrid;
	private Context context;
	private boolean isEditMode;
	private DistributorSelectListener distributorSelectListener;
	
	public DistributorListAdapter(Context context, List<Distributor> distributorList,boolean isEditMode,DistributorSelectListener distributorSelectListener) {
		super(context, android.R.id.text1, distributorList);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.distributorList = distributorList;
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.isEditMode=isEditMode;
		this.distributorSelectListener = distributorSelectListener;
	}

	public int[] getTotalgrid() {
		return totalgrid;
	}

	public void setTotalgrid(int[] totalgrid) {
		this.totalgrid = totalgrid;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
	    totalgrid=SnapInventoryUtils.getDistributorGrid(distributorList);
		DistributorAdapterWrapper distributorAdapterWrapper;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.distributor_list, parent, false);
			distributorAdapterWrapper = new DistributorAdapterWrapper();
			distributorAdapterWrapper.distributorNameTextView = (TextView) convertView.findViewById(R.id.distributor_list_text);
			distributorAdapterWrapper.distributorHeaderTextView = (TextView) convertView.findViewById(R.id.distributor_header_text);
			distributorAdapterWrapper.distributorEditButton =(ImageButton) convertView.findViewById(R.id.distributor_edit_button);
			distributorAdapterWrapper.distributorCompaniesTextView = (TextView) convertView.findViewById(R.id.distributor_companies_text);
			distributorAdapterWrapper.distributorEditButton.setOnClickListener(onDistributorEditClickListener);
		//	distributorAdapterWrapper.distributorEditButton.setTag(position);
			convertView.setTag(distributorAdapterWrapper);
		} else {
			distributorAdapterWrapper = (DistributorAdapterWrapper) convertView
					.getTag();
		}
		distributorAdapterWrapper.distributorEditButton.setTag(position);
		Distributor distributorData = getItem(position);
		if (distributorData == null) {
		    convertView.setBackgroundResource(android.R.color.transparent);
			distributorAdapterWrapper.distributorNameTextView.setText("");
			distributorAdapterWrapper.distributorNameTextView.setVisibility(View.INVISIBLE);
			distributorAdapterWrapper.distributorHeaderTextView.setVisibility(View.INVISIBLE);
			distributorAdapterWrapper.distributorEditButton.setVisibility(View.INVISIBLE);
			distributorAdapterWrapper.distributorCompaniesTextView.setVisibility(View.INVISIBLE);
            distributorAdapterWrapper.distributorCompaniesTextView.setText("");
		} else if (distributorData.getIsHeader()) {
			convertView.setBackgroundResource(R.color.default_background_color);
			distributorAdapterWrapper.distributorHeaderTextView.setText(getItem(position).getHeader());
			distributorAdapterWrapper.distributorNameTextView.setVisibility(View.INVISIBLE);
			distributorAdapterWrapper.distributorHeaderTextView.setVisibility(View.VISIBLE);
			distributorAdapterWrapper.distributorHeaderTextView.setTextColor(context.getResources().getColor(R.color.alphabet_selected_color));
			distributorAdapterWrapper.distributorEditButton.setVisibility(View.INVISIBLE);
			distributorAdapterWrapper.distributorCompaniesTextView.setVisibility(View.INVISIBLE);
            distributorAdapterWrapper.distributorCompaniesTextView.setText("");
		} else {
			convertView.setBackgroundResource(android.R.color.white);
			distributorAdapterWrapper.distributorNameTextView.setText(distributorData
					.getAgencyName());
			distributorAdapterWrapper.distributorNameTextView.setVisibility(View.VISIBLE);
			distributorAdapterWrapper.distributorHeaderTextView.setVisibility(View.INVISIBLE);
			if(distributorData.getCompanyNames()!=null){
		         distributorAdapterWrapper.distributorCompaniesTextView.setVisibility(View.VISIBLE);
				distributorAdapterWrapper.distributorCompaniesTextView.setText(distributorData.getCompanyNames());
			}else{
			     distributorAdapterWrapper.distributorCompaniesTextView.setVisibility(View.INVISIBLE);
		            distributorAdapterWrapper.distributorCompaniesTextView.setText("");
			}
			if(isEditMode){
				distributorAdapterWrapper.distributorEditButton.setVisibility(View.VISIBLE);
			}else{
				distributorAdapterWrapper.distributorEditButton.setVisibility(View.INVISIBLE);
			}
		}
		return convertView;

	}
	
	OnClickListener onDistributorEditClickListener= new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			distributorSelectListener.onDistributorEditClicked(getItem((Integer)v.getTag()));
		}
	};

	@Override
	public void add(Distributor distributor) {
		// TODO Auto-generated method stub
		distributorList.add(distributor);
	}

	@Override
	public void addAll(Collection<? extends Distributor> collection) {
		// TODO Auto-generated method stub
		distributorList.addAll(collection);
	}

	private static class DistributorAdapterWrapper {
		TextView distributorNameTextView;
		TextView distributorHeaderTextView;
		ImageButton distributorEditButton;
		TextView distributorCompaniesTextView;
	}

	@Override
	public int getCount() {
		return totalgrid.length;
	}

	@Override
	public Distributor getItem(int position) {
		if (totalgrid[position] == -1 || distributorList.size() == 0) {
			return null;
		} else {
			return distributorList.get(totalgrid[position]);
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void clear() {
		distributorList.clear();
	}

}
