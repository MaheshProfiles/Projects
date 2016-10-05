package com.snapbizz.snapbilling.fragments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snapbilling.utils.SnapBillingConstants;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snapbilling.domains.SmartStore;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class TemplateFragment extends Fragment implements OnTabChangeListener {
	private static final String TAG = "[TemplateFragment]";
	private TabHost mTabHost;
	private RelativeLayout mTemplateMain;
	private TextView mTextStoreName;
	private TextView mTextStoreMsg;
	private TextView mTextStorePhone;
	private Button mbuttonSave;
	private int currentTabId;
	private List<SmartStore> smartStoreList = new ArrayList<SmartStore>();
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private ProgressDialog loadingDialog = null;
	private AlertDialog screenInfoDialog;
	private View screenInfoDialogView;
	
	private void setFont(TextView tv) {
		Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/oswald.ttf");
		tv.setTypeface(face);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_template, null);
		mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		mTabHost.setOnTabChangedListener(this);
		mTemplateMain = (RelativeLayout) view.findViewById(R.id.template_main);
		mTextStoreName = (TextView) view.findViewById(R.id.textStoreName);
		mTextStoreMsg = (TextView) view.findViewById(R.id.textStoreMsg);
		mTextStorePhone = (TextView) view.findViewById(R.id.textStorePhone);
		mbuttonSave = (Button) view.findViewById(R.id.button_save);
		mTemplateMain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changeMyStoreInfo();
			}
		});
		
		mbuttonSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveBitmap(mTemplateMain) ;
				GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			}
		});

		setFont(mTextStoreName);
		setFont(mTextStoreMsg);
		setFont(mTextStorePhone);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		setHasOptionsMenu(true);
		ActionBar actionbar = getActivity().getActionBar();
		if(!actionbar.isShowing())
			actionbar.show();
		actionbar.setCustomView(R.layout.actionbar_layout);
		((TextView)getActivity().findViewById(R.id.actionbar_header)).setText(R.string.menu_my_screen);
		loadingDialog = ProgressDialog.show(getActivity(), null,
											getActivity().getResources().getString(R.string.loading_please_wait), true);
		loadingDialog.setCancelable(false);
		loadingDialog.setCanceledOnTouchOutside(false);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				initTabHost();
			}
		}, 500);
	}

	@Override
	public void onStart() {
		super.onStart();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		screenInfoDialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_mystoreinfo, null);
		builder.setView(screenInfoDialogView);
		screenInfoDialog = builder.create();
		screenInfoDialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
		} catch (Exception e) {
			throw new ClassCastException("activity " + activity.toString()+getString(R.string.exc_implementnavigation));
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation("",
				menuItem.getItemId());
		return true;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.findItem(R.id.my_screen_menuitem).setVisible(false);
	}
	
	private void initTabHost() {
		Resources res = getResources();
		TabWidget tw = mTabHost.getTabWidget();
		tw.setOrientation(LinearLayout.VERTICAL);
		List<String> imageList = null;
		try {
			imageList = getTabImage(getActivity()) ;
		} catch (IOException e) { }
		smartStoreList = SnapBillingUtils.getSmartStoreList(getActivity());
		if(smartStoreList == null)
			smartStoreList = new ArrayList<SmartStore>();

		for(int i=0; imageList != null && i<imageList.size();i++) {
			TabSpec templateOneSpec = mTabHost.newTabSpec(String.valueOf(i));
			templateOneSpec.setContent(R.id.tabs_template_frame);
			templateOneSpec.setIndicator(createIndicatorView(imageList.get(i), res.getDrawable(R.drawable.summary_tabselector)));

			if(smartStoreList.size() > i) {
				smartStoreList.get(i).imageUrl = imageList.get(i);
			} else {
				SmartStore newStore = new SmartStore();
				newStore.imageUrl = imageList.get(i);
				smartStoreList.add(i, newStore);
			}
			mTabHost.addTab(templateOneSpec);
		}
		SnapBillingUtils.storeSmartStoreList(smartStoreList, getActivity());
		if(imageList != null && imageList.size() > 1) {
			mTabHost.setCurrentTab(1);
			mTabHost.setCurrentTab(0);
		}
		loadingDialog.dismiss();
	}
	
	private View createIndicatorView(String imageName, Drawable background) {
		
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View tabIndicator = inflater.inflate(R.layout.template_indicator,mTabHost.getTabWidget(), false);
		final Button tv = (Button) tabIndicator.findViewById(R.id.tab_title);
		tv.setBackground(getBitmapFromAsset(getActivity(),imageName));
		tv.setClickable(false);
		tabIndicator.setBackground(background);
		return tabIndicator;
	}
	
	@Override
	public void onTabChanged(String tabId) {
		if(mTemplateMain != null && smartStoreList != null) {
			 int position = Integer.parseInt(tabId);
			 currentTabId = position;
			 try {
				 mTemplateMain.setBackground(getBitmapFromAsset(getActivity(), getTabImage(getActivity()).get(position)));
				 if(smartStoreList.size() > position) {
					 SmartStore currentScreen = smartStoreList.get(position);
					 setStoreDetails(currentScreen);
				 }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void changeMyStoreInfo() {
		final EditText editStoreName = (EditText)screenInfoDialogView.findViewById(R.id.et_store_name);
		final EditText editStoreMsg = (EditText)screenInfoDialogView.findViewById(R.id.et_store_msg);
		final EditText editStorePh = (EditText)screenInfoDialogView.findViewById(R.id.et_store_phonenumber);
		if(smartStoreList != null && !smartStoreList.isEmpty() && smartStoreList.size() > currentTabId) {
			editStoreName.setText(smartStoreList.get(currentTabId).storeName);
			editStoreName.setSelection(editStoreName.getText().length());
			editStoreMsg.setText(smartStoreList.get(currentTabId).storeMsg);
			editStorePh.setText(smartStoreList.get(currentTabId).storePhone);
			editStorePh.setSelection(editStorePh.getText().length());
		}
		screenInfoDialogView.findViewById(R.id.button_apply).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SmartStore currentStore = null;
				if(smartStoreList != null)
					 currentStore = smartStoreList.get(currentTabId);
				else
					currentStore = new SmartStore();
				currentStore.storeName = editStoreName.getText().toString();
				currentStore.storeMsg = editStoreMsg.getText().toString();
				currentStore.storePhone = editStorePh.getText().toString();
				setStoreDetails(currentStore);
				if(smartStoreList != null)
					smartStoreList.remove(currentTabId);
				smartStoreList.add(currentTabId, currentStore);
				SnapBillingUtils.storeSmartStoreList(smartStoreList, getActivity());
				SnapCommonUtils.hideSoftKeyboard(getActivity(), screenInfoDialogView.findViewById(R.id.template_layout).getWindowToken());
				screenInfoDialog.dismiss();
			}
		});
		if(!screenInfoDialog.isShowing())
			screenInfoDialog.show();
		
	}
	
	public void  saveBitmap(RelativeLayout view) {
	    Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap);
	    view.draw(canvas);
	    try {
	    	 File imagePath = new File(SnapToolkitConstants.VISIBILITY_IMAGE_PATH, "/mystore/");
	    	 if(!imagePath.exists())
	 			imagePath.mkdirs(); 
	    	 File imageName = new File(imagePath, SnapBillingConstants.STORE_IMAGE);
	         FileOutputStream output = new FileOutputStream(imageName);
	         bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
	         output.close();
	         Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.smart_store_saved), Toast.LENGTH_LONG).show();
	         return;
	    } catch (FileNotFoundException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.unknown_error), Toast.LENGTH_LONG).show();
	}
	
	private void setStoreDetails(SmartStore smartStore) {
		if(smartStore != null) {
			mTextStoreName.setText(smartStore.storeName);
			mTextStoreMsg.setText(smartStore.storeMsg);
			mTextStorePhone.setText(smartStore.storePhone);
		}
	}
	
	private List<String> getTabImage(Context conetx) throws IOException {
		AssetManager assetManager = conetx.getAssets();
		String[] files = assetManager.list(SnapBillingConstants.STORE_LOCATION);   
		List<String> imageList=Arrays.asList(files);
		return imageList; 
	}
	
	private Drawable getBitmapFromAsset(Context context, String strName){
        AssetManager assetManager = context.getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(SnapBillingConstants.STORE_LOCATION+"/"+strName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        Drawable drawable = new BitmapDrawable(getResources(), bitmap); 
        return drawable;
    }

}
