package com.snapbizz.snapbilling.fragments;

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

import com.snapbizz.snapbilling.R;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;


public class InventoryMainMenuFragment extends Fragment {

	private MenuOptionSelectListener menuOptionSelectListener;
	private OnActionbarNavigationListener onActionbarNavigationListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inventory_main, null);
		
		return view;
	}

	View.OnClickListener onMenuOptionSelectListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			menuOptionSelectListener.onMenuOptionSelectListener(view);
		}
	};

	@Override
	public void onAttach(Activity activity) {
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
					+getString(R.string.exc_implementnavigation));
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
			getActivity().getActionBar().setCustomView(R.layout.actionbar_layout);
		((TextView)getActivity().findViewById(R.id.actionbar_header)).setText(getString(R.string.stock));
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
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
