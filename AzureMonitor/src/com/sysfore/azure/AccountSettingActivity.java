package com.sysfore.azure;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.sysfore.azure.model.LoginMaster;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

public class AccountSettingActivity extends FragmentActivity implements
		OnClickListener, OnCheckedChangeListener {

	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	LinearLayout panel1, panel2;
	TextView text1, text2, text3, text4, text5;
	View openLayout;
	EditText oldpassword, newpassword, confirmpassword;
	Button passwordsubmit;
	Switch modes;
	DatabaseHelper db;
	String login_id, subId;
	ListView lv;
	int loginId;
	DeleteAdapter adapter;
	LoginMaster userList;

	public AccountSettingActivity() {
	}

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.account_setting);
		db = new DatabaseHelper(this);
		panel1 = (LinearLayout) findViewById(R.id.panel1);
		panel2 = (LinearLayout) findViewById(R.id.panel2);

		text1 = (TextView) findViewById(R.id.changepassword);

		text3 = (TextView) findViewById(R.id.offlinemode);
		text4 = (TextView) findViewById(R.id.delete);
		oldpassword = (EditText) findViewById(R.id.oldpassword);
		newpassword = (EditText) findViewById(R.id.newpassword);
		confirmpassword = (EditText) findViewById(R.id.confirmpassword);
		modes = (Switch) findViewById(R.id.mode);
		modes.setOnCheckedChangeListener(this);
		passwordsubmit = (Button) findViewById(R.id.passowrd_submit);

		passwordsubmit.setOnClickListener(this);

		text1.setOnClickListener(this);
		// text2.setOnClickListener(this);
		text3.setOnClickListener(this);
		text4.setOnClickListener(this);
		loadpreferences();
		lv = (ListView) findViewById(R.id.delete_list_item);
		Log.d("allsubcrition",
				"" + db.getListofSubcription(Integer.toString(loginId)));
		adapter = new DeleteAdapter(AccountSettingActivity.this,
				db.getListofSubcription(Integer.toString(loginId)));
		lv.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		boolean mode = sharedPreferences.getBoolean("toggleButton", false);
		Log.d("modein account settting ******************", "" + mode);
		if (mode) {
			modes.setChecked(true);
		} else {
			modes.setChecked(false);
		}

	}

	private void loadpreferences() {
		// TODO Auto-generated method stub
		SharedPreferences mySharedPreferences = getSharedPreferences(MYPREFS,
				mode);
		loginId = mySharedPreferences.getInt("login_id", 0);
		Log.d("AccountseetingloginId", "" + login_id);
		subId = mySharedPreferences.getString("SubcriptionID", "");

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.changepassword:
			hideOthers(v);
			break;

		case R.id.delete:
			hideOthers(v);
			break;

		case R.id.passowrd_submit:
			String oldpass = oldpassword.getText().toString();
			String pass = newpassword.getText().toString();
			String confirmpass = confirmpassword.getText().toString();

			if (oldpass.length() == 0 || pass.length() == 0
					|| confirmpass.length() == 0) {
				Toast.makeText(AccountSettingActivity.this,
						"please fill All fields", Toast.LENGTH_LONG).show();
			} else {
				boolean ck = checkuser(oldpass);
				if (ck) {
					if (pass.equals(confirmpass)) {
						LoginMaster userList = new LoginMaster();
						userList.setLoginId(Integer.toString(loginId));
						userList.setPassword(pass);

						db.updateLoginUser(userList);
						/*
						 * Intent login = new
						 * Intent(AccountSettingActivity.this,
						 * LoginActivity.class); startActivity(login);
						 */

					} else {
						Toast.makeText(
								AccountSettingActivity.this,
								"New password and Confirm pasword doesn't match",
								Toast.LENGTH_LONG).show();
					}

				} else {
					Toast.makeText(AccountSettingActivity.this,
							"please Enter correct password", Toast.LENGTH_LONG)
							.show();
				}

			}

			break;

		default:
			break;
		}
	}

	private boolean checkuser(String password) {

		String pass = "";

		List<LoginMaster> getdetails = db.getPassword(password);
		boolean ckuser = false;
		if (getdetails.size() > 0) {
			int i = 0;
			while ((ckuser == false) && i <= getdetails.size()) {

				login_id = getdetails.get(i).getLoginId();
				pass = getdetails.get(i).getPassword();

				if (password.equals(pass)) {
					ckuser = true;
				}
				i++;
			}
		}

		return ckuser;
	}

	private void hideThemAll() {
		if (openLayout == null)
			return;
		if (openLayout == panel1)
			panel1.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel1, true));
		if (openLayout == panel2)
			panel2.startAnimation(new ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f,
					500, panel2, true));
		/*
		 * if (openLayout == panel3) panel3.startAnimation(new
		 * ScaleAnimToHide(1.0f, 1.0f, 1.0f, 0.0f, 500, panel3, true));
		 */

	}

	private void hideOthers(View layoutView) {
		{
			int v;
			if (layoutView.getId() == R.id.changepassword) {
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
			} else if (layoutView.getId() == R.id.delete) {
				v = panel2.getVisibility();
				hideThemAll();
				if (v != View.VISIBLE) {
					panel2.startAnimation(new ScaleAnimToShow(1.0f, 1.0f, 1.0f,
							0.0f, 500, panel2, true));
				}
			} /*
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
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.subscription:
			Intent settings = new Intent(AccountSettingActivity.this,
					SubscriptionActivity.class);
			startActivity(settings);
			return true;
		case R.id.home:

			Intent home = new Intent(AccountSettingActivity.this,
					Dashbaord.class);
			startActivity(home);
			return true;

		case R.id.subscriptionmenu:
			Intent subscriptionmenu = new Intent(AccountSettingActivity.this,
					SubscriptionActivity.class);
			startActivity(subscriptionmenu);
			return true;

		case R.id.pricedetails:
			Intent pricedetail = new Intent(AccountSettingActivity.this,
					PriceDetails.class);
			startActivity(pricedetail);
			return true;

		case R.id.account_settings:
			Intent accsettings = new Intent(this, AccountSettingActivity.class);
			startActivity(accsettings);
			return true;
		
			
		case R.id.monitormachine:
			Intent monitormachine = new Intent(AccountSettingActivity.this,
					MachineMonitorActivity.class);
			startActivity(monitormachine);
			return true;
	
		case R.id.pricemenu:
			Intent pricemenu = new Intent(AccountSettingActivity.this, Pricemenu.class);
			startActivity(pricemenu);
			return true;
			
		case R.id.action_resource:
			Intent resource = new Intent(AccountSettingActivity.this,Resource.class);
			startActivity(resource);
			return true;

		case R.id.action_logout:
			Intent home1 = new Intent(AccountSettingActivity.this,
					LoginActivity.class);
			startActivity(home1);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		// TODO Auto-generated method stub
		LoginMaster master;
		if (isChecked) {

			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			Editor editor = sharedPreferences.edit();
			editor.putBoolean("toggleButton", true);
			editor.commit();
			master = new LoginMaster();
			master.setLoginId(Integer.toString(loginId));
			master.setOfflinemode("Y");
			db.updateLoginUserMode(master);

			/*Toast.makeText(getApplicationContext(), "The switch is ON",
					Toast.LENGTH_SHORT).show();*/

		} else {

			SharedPreferences sharedPreferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			Editor editor = sharedPreferences.edit();
			editor.putBoolean("toggleButton", false);
			editor.commit();
			master = new LoginMaster();
			master.setLoginId(Integer.toString(loginId));
			master.setOfflinemode("N");
			db.updateLoginUserMode(master);

			/*Toast.makeText(getApplicationContext(), "The switch is OFF",
					Toast.LENGTH_SHORT).show();*/
		}

	}

}
