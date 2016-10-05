package com.snapbizz.snapstock.adapters;

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
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class BrandSelectionAdapter extends ArrayAdapter<Brand> {

    private LayoutInflater inflater;
    private Context context;
    private int layoutId = R.layout.griditem_brand;
    public int lastSelectedPos=-1;
    private OnBrandEditListener onBrandEditListener;
    
    public BrandSelectionAdapter(Context context, List<Brand> objects, OnBrandEditListener onBrandEditListener) {
        super(context, android.R.id.text1, objects);
        // TODO Auto-generated constructor stub
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.onBrandEditListener = onBrandEditListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        BrandAdapterWrapper brandAdapterWrapper;
        if (convertView == null) {
            brandAdapterWrapper = new BrandAdapterWrapper();
            convertView = inflater.inflate(layoutId, null);
            brandAdapterWrapper.brandImageView = (ImageView) convertView
                    .findViewById(R.id.brand_imageview);
            brandAdapterWrapper.brandNameTextView = (TextView) convertView
                    .findViewById(R.id.brandname_textview);
            brandAdapterWrapper.brandEditImageButton = (ImageButton) convertView.findViewById(R.id.brand_edit_button);
            brandAdapterWrapper.brandSelectionView = (ImageView) convertView.findViewById(R.id.brand_selected_imagebutton);
            brandAdapterWrapper.brandEditImageButton.setOnClickListener(onBrandEditClickListener);
            convertView.setTag(brandAdapterWrapper);
        } else {
            brandAdapterWrapper = (BrandAdapterWrapper) convertView.getTag();
        }
        Brand brand = getItem(position);
      /*  if(!brand.isGDB()) {
            brandAdapterWrapper.brandEditImageButton.setTag(position);
        	brandAdapterWrapper.brandEditImageButton.setVisibility(View.VISIBLE);
        } else*/
        	brandAdapterWrapper.brandEditImageButton.setVisibility(View.INVISIBLE);
        brandAdapterWrapper.brandImageView.setImageDrawable(SnapCommonUtils
                .getBrandDrawable(brand, context));
        brandAdapterWrapper.brandSelectionView.setSelected(brand.isSelected());
        brandAdapterWrapper.brandNameTextView.setText(brand.getBrandName());
        return convertView;
    }

    View.OnClickListener onBrandEditClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			onBrandEditListener.onBrandEdit((Integer) v.getTag());
		}
	};
    
    private static class BrandAdapterWrapper {
        public ImageView brandImageView;
        public TextView brandNameTextView;
        public ImageView brandSelectionView;
        public ImageButton brandEditImageButton;
    }

    public interface OnBrandEditListener {
    	public void onBrandEdit(int position);
    }
    
}
