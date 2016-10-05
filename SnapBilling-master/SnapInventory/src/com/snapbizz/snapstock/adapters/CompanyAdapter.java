package com.snapbizz.snapstock.adapters;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.domains.Company;

public class CompanyAdapter extends ArrayAdapter<Company> {

	private LayoutInflater inflater;
	private Context context;
	private boolean isSelectedCompany;
	private List<Company> companyList;
	private int[] totalgrid;
	private OnCompanyEditListener onCompanyEditListener;

	public CompanyAdapter(Context context, List<Company> companyList, OnCompanyEditListener onCompanyEditListener) {
		super(context, android.R.id.text1, companyList);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.companyList = companyList;
		this.onCompanyEditListener = onCompanyEditListener;
	}

	public CompanyAdapter(Context context, List<Company> objects, boolean isSelectdCompany) {
		super(context, android.R.id.text1, objects);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.isSelectedCompany = isSelectdCompany;
	}

	public int[] getTotalgrid() {
		return totalgrid;
	}

	public void setTotalgrid(int[] totalgrid) {
		this.totalgrid = totalgrid;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CompanyAdapterWrapper companyAdapterWrapper;
		if (convertView == null) {
			companyAdapterWrapper = new CompanyAdapterWrapper();
			convertView = inflater.inflate(
					R.layout.griditem_company, null);
			companyAdapterWrapper.companyNameTextView = (TextView) convertView
					.findViewById(R.id.companyname_textview);
			companyAdapterWrapper.companyBrandTextView = (TextView) convertView.findViewById(R.id.brandname_textview);
			companyAdapterWrapper.cancelImageView = (ImageView) convertView.findViewById(R.id.cancel_imageview);
			companyAdapterWrapper.companyHeaderTextView = (TextView) convertView.findViewById(R.id.companyheader_textview);
			companyAdapterWrapper.companyEditImageButton = (ImageButton) convertView.findViewById(R.id.company_edit_button);
			companyAdapterWrapper.companyEditImageButton.setOnClickListener(onCompanyEditClickListener);
			convertView.setTag(companyAdapterWrapper);
		} else {
			companyAdapterWrapper = (CompanyAdapterWrapper) convertView
					.getTag();
		}
		Company company = getItem(position);

		if (company == null) {
			convertView.setBackgroundResource(R.color.default_background_color);
			companyAdapterWrapper.companyNameTextView.setVisibility(View.INVISIBLE);
			companyAdapterWrapper.companyEditImageButton.setVisibility(View.INVISIBLE);
			companyAdapterWrapper.companyBrandTextView.setVisibility(View.INVISIBLE);
			companyAdapterWrapper.cancelImageView.setVisibility(View.INVISIBLE);
			companyAdapterWrapper.companyHeaderTextView.setVisibility(View.INVISIBLE);
		} else if (company.isHeader()) {
			convertView.setBackgroundResource(R.color.transparent_color);
			companyAdapterWrapper.companyEditImageButton.setVisibility(View.INVISIBLE);
			companyAdapterWrapper.companyNameTextView.setVisibility(View.INVISIBLE);
			companyAdapterWrapper.companyBrandTextView.setVisibility(View.INVISIBLE);
			companyAdapterWrapper.cancelImageView.setVisibility(View.INVISIBLE);
			companyAdapterWrapper.companyHeaderTextView.setVisibility(View.VISIBLE);
			companyAdapterWrapper.companyHeaderTextView.setText(company.getHeaderValue());
		} else {
			convertView.setBackgroundResource(R.drawable.distributor_griditem_bgselector);
			companyAdapterWrapper.companyEditImageButton.setTag(position);
			companyAdapterWrapper.companyNameTextView.setVisibility(View.VISIBLE);
			companyAdapterWrapper.companyBrandTextView.setVisibility(View.VISIBLE);
			companyAdapterWrapper.cancelImageView.setVisibility(View.VISIBLE);
			companyAdapterWrapper.companyHeaderTextView.setVisibility(View.GONE);
			companyAdapterWrapper.companyNameTextView.setText(company.getCompanyName());
			companyAdapterWrapper.cancelImageView.setVisibility(isSelectedCompany && company.isSelected() ? View.VISIBLE : View.GONE);
			companyAdapterWrapper.companyBrandTextView.setText(company.getCompanyBrandData());
			if(!company.isGDB() && !isSelectedCompany)
				companyAdapterWrapper.companyEditImageButton.setVisibility(View.VISIBLE);
			else
				companyAdapterWrapper.companyEditImageButton.setVisibility(View.GONE);
		}
		return convertView;
	}

	View.OnClickListener onCompanyEditClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onCompanyEditListener.onCompanyEdit((Integer) v.getTag());
		}
	};

	private class CompanyAdapterWrapper {
		public TextView companyNameTextView;
		public TextView companyBrandTextView;
		public TextView companyHeaderTextView;
		public ImageButton companyEditImageButton;
		public ImageView cancelImageView;
	}

	public interface OnCompanyEditListener {
		public void onCompanyEdit(int position);
	}

	@Override
	public void add(Company company) {
		// TODO Auto-generated method stub
		if(!isSelectedCompany)
			companyList.add(company);
		else
			super.add(company);
	}

	@Override
	public void addAll(Collection<? extends Company> collection) {
		// TODO Auto-generated method stub
		if(!isSelectedCompany)
			companyList.addAll(collection);
		else
			super.addAll(companyList);
	}

	@Override
	public int getCount() {
		if(!isSelectedCompany)
			return totalgrid.length;
		else
			return super.getCount();
	}

	@Override
	public Company getItem(int position) {
		if(!isSelectedCompany) {
			if (totalgrid[position] == -1 || companyList.size() == 0) {
				return null;
			} else {
				return companyList.get(totalgrid[position]);
			}
		} else 
			return super.getItem(position);
	}

	public void clear() {
		if(!isSelectedCompany)
			companyList.clear();
		else
			super.clear();
	}
}
