package com.snapbizz.snapstock.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.snapbizz.snapstock.R;
import com.snapbizz.snaptoolkit.fragments.HelpVideosFragment;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;

public class InventoryHelpVideoFragment extends HelpVideosFragment{

	
	private OnActionbarNavigationListener onActionbarNavigationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }
	  
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof OnActionbarNavigationListener))
			throw new ClassCastException(activity.getLocalClassName()
					+ " must implement OnActionbarNavigationListener");
		onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
	}
    
   
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//menu.findItem(R.id.help_videos_menuitem).setVisible(false);
		menu.findItem(R.id.search_meuitem).setVisible(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation(getTag(),
				menuItem.getItemId());
		return true;
	}

}
