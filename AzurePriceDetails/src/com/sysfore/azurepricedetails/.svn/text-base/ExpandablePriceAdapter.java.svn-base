package com.sysfore.azurepricedetails;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.sysfore.azurepricedetails.model.ResultPojo;

public class ExpandablePriceAdapter extends BaseExpandableListAdapter {

	Context context;
	List<ResultPojo> priceHeaderdetail;
	List<List<ResultPojo>> priceChilddetail;
	LayoutInflater infalInflater;

	public ExpandablePriceAdapter(Context context,
			List<ResultPojo> priceHeaderdetail,List<List<ResultPojo>> priceChilddetail) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.priceHeaderdetail = priceHeaderdetail;
		this.priceChilddetail = priceChilddetail;
	
	}
	

	
	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		// TODO Auto-generated method stub
		return null;
		
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		 return 0;
	}

	@Override
	public View getChildView(int groupPosition,  int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
	
		 if(childPosition == 0){
			 infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 return convertView = infalInflater.inflate(R.layout.childheader, null);
		 }
		 
		 ChildViewHolder holder = null;
		 ResultPojo  row_pos = priceChilddetail.get(groupPosition).get(childPosition);
		 
		if (convertView != null) {
			holder = (ChildViewHolder) convertView.getTag();
		   }
			
		
		if(holder == null){
           convertView = infalInflater.inflate(R.layout.price_detail_list_row, null);
           holder = new ChildViewHolder(convertView);
           convertView.setTag(holder);
            }
			
		holder.product = (TextView) convertView.findViewById(R.id.product);
		holder.tariff = (TextView) convertView.findViewById(R.id.tariff);
		holder.unit = (TextView) convertView.findViewById(R.id.unit);
		holder.consumed = (TextView) convertView.findViewById(R.id.consumed);
		holder.total = (TextView) convertView.findViewById(R.id.total);

		holder.product.setText(row_pos.getProduct());
		holder.tariff.setText(row_pos.getPerUnitCommitted());
		holder.unit.setText(row_pos.getUnitofMeasure());
		holder.consumed.setText(row_pos.getUnitConsumed());
		holder.total.setText(row_pos.getBillTotal());
	
		return convertView;

}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return priceChilddetail.get(groupPosition).size();

	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return this.priceHeaderdetail.size();
	}
	
	
	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		// TODO Auto-generated method stub
		super.onGroupExpanded(groupPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
	
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(
					R.layout.price_detail_list_header, null);
		}
		TextView lblListHeader = (TextView) convertView.findViewById(R.id.ComponentTotal);
		TextView lblListHeadertotal = (TextView) convertView.findViewById(R.id.componentname);
		String component = priceHeaderdetail.get(groupPosition).getComponent();
		String[] split = component.split("\\(");
		String fComponent = split[0];
		lblListHeader.setText(fComponent);
		lblListHeadertotal.setText(priceHeaderdetail.get(groupPosition).getBillTotal());
		
	/*	
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeadertotal.setTypeface(null, Typeface.BOLD);*/
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	static class ChildViewHolder {
		public ChildViewHolder(View convertView) {
			// TODO Auto-generated constructor stub
		}

		TextView product, tariff, unit, consumed,total;

	}
	
	

}
