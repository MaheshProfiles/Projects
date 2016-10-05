package com.sysfore.azure;

import java.util.ArrayList;
import java.util.List;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sysfore.azure.model.RowItem;

public class AccountSettingFragment extends ListFragment implements OnItemClickListener {

    String[] menutitles;
    TypedArray menuIcons;
    ViewGroup root;
    LinearLayout panel1,panel2;
    CustomAdapter adapter;
    private List<RowItem> rowItems;
    View openLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	root= (ViewGroup) inflater.inflate(R.layout.account_setting_list, null,false);
		 setHasOptionsMenu(true);
		panel1 = (LinearLayout) root.findViewById(R.id.panel1);
		panel2 = (LinearLayout) root.findViewById(R.id.panel2);

        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        menutitles = getResources().getStringArray(R.array.titles);
        menuIcons = getResources().obtainTypedArray(R.array.icons);

        rowItems = new ArrayList<RowItem>();
       

        for (int i = 0; i < menutitles.length; i++) {
            RowItem items = new RowItem(menutitles[i], menuIcons.getResourceId(
                    i, -1));

            rowItems.add(items);
        }

        adapter = new CustomAdapter(getActivity(), rowItems);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {

        Toast.makeText(getActivity(), menutitles[position], Toast.LENGTH_SHORT).show();
        if(position==0){
        	panel1.setVisibility(View.VISIBLE);
        	panel2.setVisibility(View.GONE);
        }
        else if(position==1){
        	panel2.setVisibility(View.VISIBLE);
        	panel1.setVisibility(View.GONE);
        }
        else if(position==2){
        	panel2.setVisibility(View.GONE);
        	panel1.setVisibility(View.GONE);
        }
        else if(position==3){
        	panel2.setVisibility(View.GONE);
        	panel1.setVisibility(View.GONE);
        }
        else if(position==4){
        	panel2.setVisibility(View.GONE);
        	panel1.setVisibility(View.GONE);
        }
      
       

    }
    
	
}
