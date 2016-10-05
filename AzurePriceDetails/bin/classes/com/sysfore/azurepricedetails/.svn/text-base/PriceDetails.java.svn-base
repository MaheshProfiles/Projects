package com.sysfore.azurepricedetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;

import com.sysfore.azurepricedetails.modal.AzureUtil;
import com.sysfore.azurepricedetails.model.ResultPojo;
import com.sysfore.azurepricedetails.model.Subscription;
import com.sysfore.azurepricedetails.sqlite.helper.DatabaseHelper;

public class PriceDetails extends FragmentActivity implements OnClickListener,
		OnItemSelectedListener,OnGroupExpandListener{

	

	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE,login_id;
	DatabaseHelper db;
	ExpandableListView lv;

	List<Subscription> subcription;
	String subcriptionID, Month, Year, subscriptionName, SubcriptionIdPrice,
			SubcriptionName, pricedetail, pricedetail1,subid,component;
	
	TextView month, year, grandtotal, totalamount, textsubscription,
			subscriptionprice;
	List<ResultPojo> pricedHeaderDetails;
	List<List<ResultPojo>> priceChildDetails = new ArrayList<List<ResultPojo>>();
	
	LinearLayout panel1;
	Spinner subscriptionid;

	View openLayout;
	ImageView arrowdown, arrowup;
	ExpandablePriceAdapter adapter;
	

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.price_detail);
		Month = getIntent().getStringExtra("Month");
		Year = getIntent().getStringExtra("Year");
		pricedetail = getIntent().getStringExtra("PRICEDETAIL");
		pricedetail1 = getIntent().getStringExtra("PRICEDETAIL1");
		month = (TextView) findViewById(R.id.pricedetailmonth);
		year = (TextView) findViewById(R.id.pricedetailyear);
		grandtotal = (TextView) findViewById(R.id.grandtotal);
		totalamount = (TextView) findViewById(R.id.totalamount);
		panel1 = (LinearLayout) findViewById(R.id.panel1);
		arrowdown = (ImageView) findViewById(R.id.arrowdown);
		arrowdown.setOnClickListener(this);
		subscriptionprice = (TextView) findViewById(R.id.subscriptiontotalamount);
		arrowup = (ImageView) findViewById(R.id.arrowup);
		arrowup.setOnClickListener(this);
		lv = (ExpandableListView) findViewById(R.id.price_list_item);
		lv.setOnGroupExpandListener(this);
		Typeface face = Typeface.createFromAsset(getAssets(),"HelveNeuUltLig.ttf");
		subscriptionid = (Spinner) findViewById(R.id.Spinnersubscription);
		subscriptionid.setOnItemSelectedListener(this);
		month.setTypeface(face);
		year.setTypeface(face);
		grandtotal.setTypeface(face);
		grandtotal.setOnClickListener(this);
		db = new DatabaseHelper(this);
		//priceChildDetails=new ArrayList<ResultPojo>();
		loadpreferences();
		getSubscriptionDetails();
		loadSpinnerData();	
		getPriceHeaderData();
		getPriceHeaderSubscriptionListData();
	
	}
	
	private void loadpreferences() {
		// TODO Auto-generated method stub
		SharedPreferences mySharedPreferences = getSharedPreferences(MYPREFS,mode);
		login_id = mySharedPreferences.getInt("login_id", 0);
		Log.d("login_id", "" + login_id);

	}

	
	private void getSubscriptionDetails() {
		// TODO Auto-generated method stub
		subcription = db.getAllSubcription(Integer.toString(login_id));
		for (int i = 0; i < subcription.size(); i++) {
			subcriptionID = AzureUtil.decrypt(subcription.get(i).getSubcriptionId());
		}

	}

	private void loadSpinnerData() {
		// TODO Auto-generated method stub
		ArrayAdapter<ResultPojo> dataAdapter = new ArrayAdapter<ResultPojo>(this, android.R.layout.simple_spinner_item,db.getsubscriptionPriceDetails(Integer.toString(login_id), AzureUtil.decrypt(subcriptionID),Month, Year));
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		subscriptionid.setAdapter(dataAdapter);
		
	}
	
	
	private void getPriceHeaderData() {
		// TODO Auto-generated method stub
	
		pricedHeaderDetails = db.getpriceHeaderDetail(Integer.toString(login_id),Month, Year);
		
		Double grandTot = new Double("0.00");
		DecimalFormat df = new DecimalFormat("#,###,##0.00");

		for (int i = 0; i < pricedHeaderDetails.size(); i++) {
			String currentMonth = pricedHeaderDetails.get(i).getMonth();
			month.setText(currentMonth);
			String currentYear = pricedHeaderDetails.get(i).getYear();
			year.setText(currentYear);
			String billTotal = pricedHeaderDetails.get(i).getBillTotal();
			component=pricedHeaderDetails.get(i).getComponent();
			subid=pricedHeaderDetails.get(i).getSubcriptionId();
			billTotal = billTotal.replace(",", "");
			grandTot = grandTot + new Double(billTotal);
			totalamount.setText("" + df.format(grandTot));
			subscriptionprice.setText("" + df.format(grandTot));
		}
		
		priceChildDetails.clear();
		String compNAme = "",subID="";
		for(ResultPojo p : pricedHeaderDetails) {
			compNAme=p.getComponent();
			subID=p.getSubcriptionId();
			List<ResultPojo> resultlist=db.getChildPriceDetails(Month, Year, compNAme, subID);
						priceChildDetails.add(resultlist);
		}
	
		if (pricedetail != null && getIntent().getStringExtra("PRICEDETAIL").equalsIgnoreCase("pricedetail")) {
			adapter = new ExpandablePriceAdapter(getApplicationContext(),pricedHeaderDetails,priceChildDetails);
			lv.setAdapter(adapter);
		}

		else if (pricedetail1 != null && getIntent().getStringExtra("PRICEDETAIL1").equalsIgnoreCase("pricedetail1")) {
			adapter = new ExpandablePriceAdapter(getApplicationContext(),pricedHeaderDetails,priceChildDetails);
			lv.setAdapter(adapter);

		} else {
			adapter = new ExpandablePriceAdapter(getApplicationContext(),pricedHeaderDetails,priceChildDetails);
			lv.setAdapter(adapter);

		}
		
			
	}

	/**
	 * Get selected Price Details
	 */
	private void getPriceHeaderSubscriptionListData() {
		
		pricedHeaderDetails = db.getAllListPriceDetails(Integer.toString(login_id),subscriptionName, Month, Year);
	
		Double grandTot = new Double("0.00");
		DecimalFormat df = new DecimalFormat("#,###,##0.00");

		for (int i = 0; i < pricedHeaderDetails.size(); i++) {
			String currentMonth = pricedHeaderDetails.get(i).getMonth();
			month.setText(currentMonth);
			String currentYear = pricedHeaderDetails.get(i).getYear();
			year.setText(currentYear);
			String billTotal = pricedHeaderDetails.get(i).getBillTotal();
			billTotal = billTotal.replace(",", "");
			component=pricedHeaderDetails.get(i).getComponent();
			subid=pricedHeaderDetails.get(i).getSubcriptionId();
			grandTot = grandTot + new Double(billTotal);
			subscriptionprice.setText("" + df.format(grandTot));
		}
		
		priceChildDetails.clear();
		String compNAme = "",subID="";
		for(ResultPojo p : pricedHeaderDetails) {
			compNAme=p.getComponent();
			subID=p.getSubcriptionId();
			List<ResultPojo> resultlist=db.getChildPriceDetails(Month, Year, compNAme, subID);
						priceChildDetails.add(resultlist);
		}
	
		if (pricedetail != null&& getIntent().getStringExtra("PRICEDETAIL").equalsIgnoreCase("pricedetail")) {
			adapter = new ExpandablePriceAdapter(getApplicationContext(),pricedHeaderDetails,priceChildDetails);
			lv.setAdapter(adapter);
		}

		else if (pricedetail1 != null && getIntent().getStringExtra("PRICEDETAIL1").equalsIgnoreCase("pricedetail1")) {
			adapter = new ExpandablePriceAdapter(getApplicationContext(),pricedHeaderDetails,priceChildDetails);
			lv.setAdapter(adapter);

		} else {
			adapter = new ExpandablePriceAdapter(getApplicationContext(),pricedHeaderDetails,priceChildDetails);
			lv.setAdapter(adapter);
			
		}

		}

			
	

	private void hideThemAll() {
		if (openLayout == null)
			return;
		if (openLayout == panel1)
			panel1.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel1, true));
	}

	/**
	 * Hide others when one is opened
	 */
	private void hideOthers(View layoutView) {
		{
			int v;
			if (layoutView.getId() == R.id.arrowdown) {
				v = panel1.getVisibility();
				if (v != View.VISIBLE) {
					panel1.setVisibility(View.VISIBLE);
					Log.v("CZ", "height..." + panel1.getHeight());
				}
				hideThemAll();
				if (v != View.VISIBLE) {
					panel1.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel1, true));
				}
			} else if (layoutView.getId() == R.id.arrowup) {
				v = panel1.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel1.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel1, true));
				}
			}
		}
	}

	/**
	 * Open and close list view
	 */
	public class ScaleAnimToHide extends ScaleAnimation {

		private View mView;

		private LayoutParams mLayoutParams;

		private int mMarginBottomFromY, mMarginBottomToY;

		private boolean mVanishAfter = false;

		public ScaleAnimToHide(float fromX, float toX, float fromY, float toY,
				int duration, View view, boolean vanishAfter) {
			super(fromX, toX, fromY, toY);
			setDuration(duration);
			openLayout = null;
			mView = view;
			mVanishAfter = vanishAfter;
			mLayoutParams = (LayoutParams) view.getLayoutParams();
			int height = mView.getHeight();
			mMarginBottomFromY = (int) (height * fromY)
					+ mLayoutParams.bottomMargin - height;
			mMarginBottomToY = (int) (0 - ((height * toY) + mLayoutParams.bottomMargin))
					- height;

			Log.v("CZ", "height..." + height + " , mMarginBottomFromY...."
					+ mMarginBottomFromY + " , mMarginBottomToY.."
					+ mMarginBottomToY);
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				int newMarginBottom = mMarginBottomFromY
						+ (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime);
				mLayoutParams.setMargins(mLayoutParams.leftMargin,
						mLayoutParams.topMargin, mLayoutParams.rightMargin,
						newMarginBottom);
				mView.getParent().requestLayout();
				// Log.v("CZ","newMarginBottom..." + newMarginBottom +
				// " , mLayoutParams.topMargin..." + mLayoutParams.topMargin);
			} else if (mVanishAfter) {
				mView.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * Open and close list view
	 */
	public class ScaleAnimToShow extends ScaleAnimation {

		private View mView;

		private LayoutParams mLayoutParams;

		private int mMarginBottomFromY, mMarginBottomToY;

		@SuppressWarnings("unused")
		private boolean mVanishAfter = false;

		public ScaleAnimToShow(float toX, float fromX, float toY, float fromY,
				int duration, View view, boolean vanishAfter) {
			super(fromX, toX, fromY, toY);
			openLayout = view;
			setDuration(duration);
			mView = view;
			mVanishAfter = vanishAfter;
			mLayoutParams = (LayoutParams) view.getLayoutParams();
			mView.setVisibility(View.VISIBLE);
			int height = mView.getHeight();

			mMarginBottomFromY = 0;
			mMarginBottomToY = height;

		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			super.applyTransformation(interpolatedTime, t);
			if (interpolatedTime < 1.0f) {
				int newMarginBottom = (int) ((mMarginBottomToY - mMarginBottomFromY) * interpolatedTime)
						- mMarginBottomToY;
				mLayoutParams.setMargins(mLayoutParams.leftMargin,
						mLayoutParams.topMargin, mLayoutParams.rightMargin,
						newMarginBottom);
				mView.getParent().requestLayout();

			}
		}

	}

	/**
	 * Create Menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Menu Selected
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.pricemenu:
			Intent price = new Intent(PriceDetails.this, Pricemenu.class);
			startActivity(price);
			return true;

		case R.id.accounts:
			Intent accounts = new Intent(PriceDetails.this,
					SubscriptionActivity.class);
			startActivity(accounts);
			return true;
		case R.id.changepassword:
			Intent changepassword = new Intent(PriceDetails.this,
					ChangePassword.class);
			startActivity(changepassword);
			return true;
		case R.id.action_logout:
			Intent home1 = new Intent(PriceDetails.this, LoginActivity.class);
			startActivity(home1);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Back Pressed
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent back = new Intent(PriceDetails.this, Pricemenu.class);
		back.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(back);
	}

	/**
	 * Arrow/Image On click
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.arrowdown:

			hideOthers(v);
			arrowup.setVisibility(View.VISIBLE);
			arrowdown.setVisibility(View.GONE);

			break;

		case R.id.arrowup:

			hideOthers(v);
			arrowup.setVisibility(View.GONE);
			arrowdown.setVisibility(View.VISIBLE);

			break;

		default:
			break;
		}

	}

	/**
	 * List Item selected
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		subscriptionName = subscriptionid.getSelectedItem().toString();
		if (subscriptionName.equalsIgnoreCase("Select")) {
			getPriceHeaderData();
		} else {
			subscriptionName = parent.getSelectedItem().toString();
			Log.d("subscriptionName", ""+subscriptionName);
			getPriceHeaderSubscriptionListData();
	
		
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onGroupExpand(int groupPosition) {
		// TODO Auto-generated method stub
		int len = adapter.getGroupCount();
		for (int i = 0; i < len; i++) {
			if (i != groupPosition) {
				lv.collapseGroup(i);
			}
		}
	}
	
	
	

	
}
