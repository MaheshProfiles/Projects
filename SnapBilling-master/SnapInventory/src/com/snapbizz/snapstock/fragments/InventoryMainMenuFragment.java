package com.snapbizz.snapstock.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;

public class InventoryMainMenuFragment extends Fragment {

	private MenuOptionSelectListener menuOptionSelectListener;
	private OnActionbarNavigationListener onActionbarNavigationListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_inventory_main, null);
		view.findViewById(R.id.order_button).setOnClickListener(
				onMenuOptionSelectListener);
		view.findViewById(R.id.receive_button).setOnClickListener(
				onMenuOptionSelectListener);
		view.findViewById(R.id.update_button).setOnClickListener(
				onMenuOptionSelectListener);
		return view;
	}

	View.OnClickListener onMenuOptionSelectListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			menuOptionSelectListener.onMenuOptionSelectListener(view);
		}
	};

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
			menuOptionSelectListener = (MenuOptionSelectListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("activity " + activity.toString()
					+ " must implement OnMenuOptionSelectListener =");
		}
		try {
			onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("activity " + activity.toString()
					+ " must implement OnActionbarNavigationListener =");
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		//if(getActivity().getActionBar().getCustomView().getId() != R.id.actionbar_layout)
			getActivity().getActionBar().setCustomView(R.layout.actionbar_layout);
		((TextView)getActivity().findViewById(R.id.actionbar_header)).setText(getString(R.string.stock));
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		menu.findItem(R.id.search_meuitem).setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation("", menuItem.getItemId());
		return true;
	}

	public interface MenuOptionSelectListener {
		public void onMenuOptionSelectListener(View view);
	}

}
