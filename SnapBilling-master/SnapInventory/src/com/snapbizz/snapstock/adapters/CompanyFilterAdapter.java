package com.snapbizz.snapstock.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.domains.Company;

public class CompanyFilterAdapter extends ArrayAdapter<Company> {

	private LayoutInflater inflater;
	private Context context;
	private int[] totalgrid;
	private List<Company> companyList;

	public CompanyFilterAdapter(Context context, List<Company> objects) {
		super(context, android.R.id.text1, objects);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		this.companyList=objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CompanyFilterWrapper companyFilterWrapper;
		if (convertView == null) {
			companyFilterWrapper = new CompanyFilterWrapper();
			convertView = inflater.inflate(R.layout.company_filter_listitem,
					null);
			companyFilterWrapper.companyImage = (ImageView) convertView
					.findViewById(R.id.company_filter_image);
			companyFilterWrapper.companyHeaderText = (TextView) convertView.findViewById(R.id.company_header_text);
			convertView.setTag(companyFilterWrapper);
		}else{
			companyFilterWrapper=(CompanyFilterWrapper) convertView.getTag();
		}
		Company company = getItem(position);
		if(company==null){
			companyFilterWrapper.companyImage.setVisibility(View.INVISIBLE);
			companyFilterWrapper.companyHeaderText.setVisibility(View.INVISIBLE);
			companyFilterWrapper.companyImage.setImageAlpha(0);

		}else if(company.isHeader()){
//			companyFilterWrapper.companyImage.setBackgroundColor(context.getResources().getColor(R.color.transparent_color));
			companyFilterWrapper.companyImage.setImageAlpha(0);
			companyFilterWrapper.companyHeaderText.setVisibility(View.VISIBLE);
			companyFilterWrapper.companyHeaderText.setText(company.getHeaderValue());
			companyFilterWrapper.companyHeaderText.setTextColor(context.getResources().getColor(R.color.alphabet_selected_color));
		//	companyFilterWrapper.companyImage.setVisibility(View.INVISIBLE);
		}
		else{
			companyFilterWrapper.companyImage.setImageAlpha(500);
//			companyFilterWrapper.companyImage.setImageDrawable(SnapInventoryUtils
//					.getCompanyDrawable(getItem(position).getCompanyName(),getItem(position).getCompanyImageUrl(), context));
			companyFilterWrapper.companyImage.setVisibility(View.VISIBLE);
			companyFilterWrapper.companyHeaderText.setVisibility(View.INVISIBLE);
		}
	//	SnapInventoryUtils.getProductDrawable(productCode, imageUrl, context)
		return convertView;
	}
	
	public int[] getTotalgrid() {
		return totalgrid;
	}

	public void setTotalgrid(int[] totalgrid) {
		this.totalgrid = totalgrid;
	}

	private class CompanyFilterWrapper {
		private ImageView companyImage;
		private TextView companyHeaderText;
	}

	@Override
	public int getCount() {
		return totalgrid.length;
	}
	
	public void clear() {
		companyList.clear();
	}


	@Override
	public Company getItem(int position) {
		if (totalgrid[position] == -1 || companyList.size() == 0) {
			return null;
		} else {
			return companyList.get(totalgrid[position]);
		}
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
