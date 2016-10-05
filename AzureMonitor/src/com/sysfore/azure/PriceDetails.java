package com.sysfore.azure;

import java.text.DecimalFormat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sysfore.azure.model.ResultPojo;
import com.sysfore.azure.model.Subscription;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

public class PriceDetails extends FragmentActivity implements OnClickListener {

	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	DatabaseHelper db;
	ListView lv;
	int login_id;
	List<Subscription> subcription;
	String subcriptionID, Month, Year, pricedetail, pricedetail1, subId;
	PriceAdapter adapter;
	TextView month, year, grandtotal, totalamount;
	List<ResultPojo> priceddetaillist;
	LinearLayout panel1;
	View openLayout;
	ImageView arrowdown, arrowup;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.price_detail);
		Month = getIntent().getStringExtra("Month");
		Log.d("Month***************", "" + Month);

		Year = getIntent().getStringExtra("Year");
		Log.d("Year***************", "" + Year);

		pricedetail = getIntent().getStringExtra("PRICEDETAIL");
		Log.d("pricedetail***************", "" + pricedetail);

		pricedetail1 = getIntent().getStringExtra("PRICEDETAIL1");
		Log.d("pricedetail1***************", "" + pricedetail1);

		month = (TextView) findViewById(R.id.pricedetailmonth);
		year = (TextView) findViewById(R.id.pricedetailyear);
		grandtotal = (TextView) findViewById(R.id.grandtotal);
		totalamount = (TextView) findViewById(R.id.totalamount);
		panel1 = (LinearLayout) findViewById(R.id.panel1);
		arrowdown = (ImageView) findViewById(R.id.arrowdown);
		arrowdown.setOnClickListener(this);

		arrowup = (ImageView) findViewById(R.id.arrowup);
		arrowup.setOnClickListener(this);
		Typeface face = Typeface.createFromAsset(getAssets(),"HelveNeuUltLig.ttf");
		month.setTypeface(face);
		year.setTypeface(face);
		grandtotal.setTypeface(face);
		grandtotal.setOnClickListener(this);
		// totalamount.setTypeface(face);

		db = new DatabaseHelper(this);

		loadpreferences();
		getSubscriptionDetails();
		getPriceDetails();
		lv = (ListView) findViewById(R.id.price_list_item);
		// adapter = new PriceAdapter(getApplicationContext(),
		// priceddetaillist);
		// lv.setAdapter(adapter);

		if (pricedetail != null
				&& getIntent().getStringExtra("PRICEDETAIL").equalsIgnoreCase(
						"pricedetail")) {

			adapter = new PriceAdapter(getApplicationContext(),
					priceddetaillist);
			lv.setAdapter(adapter);
		}

		else if (pricedetail1 != null
				&& getIntent().getStringExtra("PRICEDETAIL1").equalsIgnoreCase(
						"pricedetail1")) {

			adapter = new PriceAdapter(getApplicationContext(),
					priceddetaillist);
			lv.setAdapter(adapter);

		} else {
			adapter = new PriceAdapter(getApplicationContext(),
					priceddetaillist);
			lv.setAdapter(adapter);

		}

	}

	private void getPriceDetails() {
		// TODO Auto-generated method stub
		priceddetaillist = db.getAllPriceDetails(Integer.toString(login_id),
				subcriptionID, Month, Year);
		Double grandTot = new Double("0.00");
		DecimalFormat df = new DecimalFormat("#,###,##0.00");

		for (int i = 0; i < priceddetaillist.size(); i++) {
			String currentMonth = priceddetaillist.get(i).getMonth();
			month.setText(currentMonth);
			String currentYear = priceddetaillist.get(i).getYear();
			year.setText(currentYear);
			String billTotal = priceddetaillist.get(i).getBillTotal();
			billTotal = billTotal.replace(",", "");
			grandTot = grandTot + new Double(billTotal);
			/*
			 * String split[] = (grandTot.toString().split(" ", 2)); sum =
			 * split[0].replace(",", "");
			 */

		}
		totalamount.setText("" + df.format(grandTot));

	}

	private void loadpreferences() {
		// TODO Auto-generated method stub
		SharedPreferences mySharedPreferences = getSharedPreferences(MYPREFS,
				mode);
		login_id = mySharedPreferences.getInt("login_id", 0);
		Log.d("login_id", "" + login_id);
		subId = mySharedPreferences.getString("SubcriptionID", "");

	}

	private void getSubscriptionDetails() {
		// TODO Auto-generated method stub
		subcription = db.getAllSubcription(Integer.toString(login_id), subId);
		for (int i = 0; i < subcription.size(); i++) {
			subcriptionID = subcription.get(i).getSubcriptionId();

		}

	}

	private void hideThemAll() {
		if (openLayout == null)
			return;
		if (openLayout == panel1)
			panel1.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel1, true));
		/*
		 * if (openLayout == panel2) panel2.startAnimation(new
		 * ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f, 500, panel2, true)); if
		 * (openLayout == panel3) panel3.startAnimation(new
		 * ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f, 500, panel3, true));
		 */

	}

	private void hideOthers(View layoutView) {
		{
			int v;
			if (layoutView.getId() == R.id.arrowdown) {
				v = panel1.getVisibility();
				if (v != View.VISIBLE) {
					panel1.setVisibility(View.VISIBLE);

					Log.v("CZ", "height..." + panel1.getHeight());
				}

				// panel1.setVisibility(View.GONE);
				// Log.v("CZ","again height..." + panel1.getHeight());
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
			/*
			 * else if (layoutView.getId() == R.id.text3) { v =
			 * panel3.getVisibility(); hideThemAll(); if (v != View.VISIBLE) {
			 * panel3.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f, 0.0f,
			 * 500, panel3, true)); } }
			 */
		}
	}

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
			// mMarginBottomFromY = (int) (height * fromY) +
			// mLayoutParams.bottomMargin + height;
			// mMarginBottomToY = (int) (0 - ((height * toY) +
			// mLayoutParams.bottomMargin)) + height;

			mMarginBottomFromY = 0;
			mMarginBottomToY = height;

			// Log.v("CZ",".................height..." + height +
			// " , mMarginBottomFromY...." + mMarginBottomFromY +
			// " , mMarginBottomToY.." +mMarginBottomToY);
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
				// Log.v("CZ","newMarginBottom..." + newMarginBottom +
				// " , mLayoutParams.topMargin..." + mLayoutParams.topMargin);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pricedetails, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.subscription:
			Intent sub = new Intent(PriceDetails.this,
					SubscriptionActivity.class);
			startActivity(sub);
			return true;

		case R.id.home:
			Intent home = new Intent(PriceDetails.this, Dashbaord.class);
			startActivity(home);
			return true;

		case R.id.switchsubscription:
			Intent switchsubs = new Intent(PriceDetails.this,
					SubscriptionList.class);
			startActivity(switchsubs);
			return true;

		case R.id.action_resource:
			Intent resource = new Intent(PriceDetails.this, Resource.class);
			startActivity(resource);
			return true;

		case R.id.monitormachine:
			Intent monitormachine = new Intent(PriceDetails.this,
					MachineMonitorActivity.class);
			startActivity(monitormachine);
			return true;

		case R.id.pricemenu:
			Intent price = new Intent(PriceDetails.this, Pricemenu.class);
			startActivity(price);
			return true;

		case R.id.account_settings:
			Intent accsettings = new Intent(PriceDetails.this,
					AccountSettingActivity.class);
			startActivity(accsettings);
			return true;

		case R.id.action_logout:
			Intent home1 = new Intent(PriceDetails.this, LoginActivity.class);
			startActivity(home1);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent back = new Intent(PriceDetails.this, Pricemenu.class);
		startActivity(back);
	}

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
}