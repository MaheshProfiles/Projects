package com.sysfore.azure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sysfore.azure.modal.DataProcess;
import com.sysfore.azure.model.BillingPojo;
import com.sysfore.azure.model.CalculationPojo;
import com.sysfore.azure.model.ProcessLog;
import com.sysfore.azure.model.ResultPojo;
import com.sysfore.azure.model.ServicePojo;
import com.sysfore.azure.model.Subscription;
import com.sysfore.azure.service.BillingService;
import com.sysfore.azure.sqlite.helper.DatabaseHelper;

public class Pricemenu extends FragmentActivity implements OnClickListener,
		OnCheckedChangeListener {

	private static final int REQUEST_PATH = 1;
	private static final int REQUEST_PICK_FILE = 1;
	public static final String MYPREFS = "mySharedPreferences";
	int mode = Activity.MODE_PRIVATE;
	private File selectedFile;
	// NumberPicker month, year;
	List<Subscription> subcription;
	Button browsepricesheet, monthplus, monthminus, yearplus, yearminus,
			calculate;
	TextView month, year, filename;
	CheckBox forcedchecked;
	long count;
	int nStartmonth = 1;
	int nEndmonth = 12;
	int login_id, curent, current, currentDay, currentMonth, currentYear;
	int nStartyear = 2011;
	int nEndyear = 2014;
	int chkvalue;
	DatabaseHelper db;
	ProcessLog processlog;

	String subcriptionID, path, usagesumary, enrollmentId, token, selectedPath,
			date, targetedfile, selectfile, subId;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.price_menu);
		db = new DatabaseHelper(this);
		processlog = new ProcessLog();

		// month = (NumberPicker) findViewById(R.id.month);
		// year = (NumberPicker) findViewById(R.id.year);
		browsepricesheet = (Button) findViewById(R.id.pricesheet);
		browsepricesheet.setOnClickListener(this);
		monthplus = (Button) findViewById(R.id.monthplus);
		monthplus.setOnClickListener(this);
		monthminus = (Button) findViewById(R.id.monthminus);
		monthminus.setOnClickListener(this);
		yearplus = (Button) findViewById(R.id.yearplus);
		yearplus.setOnClickListener(this);
		yearminus = (Button) findViewById(R.id.yearminus);
		yearminus.setOnClickListener(this);
		calculate = (Button) findViewById(R.id.calculate);
		calculate.setOnClickListener(this);
		month = (TextView) findViewById(R.id.month);

		year = (TextView) findViewById(R.id.year);
		forcedchecked = (CheckBox) findViewById(R.id.forcedcheck);
		forcedchecked.setOnCheckedChangeListener(this);
		filename = (TextView) findViewById(R.id.filename);

		Typeface face = Typeface.createFromAsset(getAssets(),
				"HelveNeuUltLig.ttf");

		month.setTypeface(face);
		year.setTypeface(face);
		date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		Calendar localCalendar = Calendar.getInstance(TimeZone.getDefault());
		currentDay = localCalendar.get(Calendar.DATE);
		Log.d("currentDay********************", "" + currentDay);
		currentMonth = localCalendar.get(Calendar.MONTH) + 1;
		Log.d("currentMonth********************", "" + currentMonth);
		currentYear = localCalendar.get(Calendar.YEAR);

		month.setText("06");
		year.setText("2014");
		loadpreferences();
		getSubscriptionDetails();
		getActionBar();
		// getDate();

		// month.setMinValue(1);

		// month.setMaxValue(12);
		// month.setWrapSelectorWheel(false);

		// year.setMinValue(2013);
		// year.setMaxValue(2017);
		// year.setWrapSelectorWheel(false);

		/*
		 * View upButton = month.getChildAt(0);
		 * upButton.setBackgroundResource(R.drawable.linearlayout_bg);
		 * 
		 * 
		 * EditText edDate = (EditText) month.getChildAt(1);
		 * edDate.setTextSize(17);
		 * edDate.setBackgroundResource(R.drawable.editbox_background_focus_green
		 * );
		 */

		// View downButton = month.getChildAt(2);
		// downButton.setBackgroundResource(R.drawable.linearlayout_bg);
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
			Log.d("subcriptionID*********************", "" + subcriptionID);
			usagesumary = subcription.get(i).getUsagesummary();
			Log.d("usagesumary*********************", "" + usagesumary);
			/*
			 * if (usagesumary != null) { filename.setText(usagesumary); //
			 * selectedPath=usagesumary; } else { filename.setText("Filename");
			 * }
			 */

			enrollmentId = subcription.get(i).getEmplomentID();

			token = subcription.get(i).getToken();

			if (usagesumary != null) {
				File sourceFile = new File(usagesumary);
				selectfile = sourceFile.getName();
				filename.setText(selectfile);
				selectedPath = usagesumary;

			} else {
				filename.setText("Filename");
			}

		}

		// certificateKey = "descfile.pfx";
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String getmonth = String.valueOf(month.getText());
		int curent = Integer.parseInt(getmonth);

		String getyear = String.valueOf(year.getText());
		int current = Integer.parseInt(getyear);
		switch (v.getId()) {

		case R.id.pricesheet:

			Intent intent1 = new Intent(Pricemenu.this, Filepicker.class);
			startActivityForResult(intent1, REQUEST_PATH);

			break;

		case R.id.monthplus:

			if (curent < nEndmonth) {
				curent++;
				if (curent < 10)
					month.setText("0" + Integer.toString(curent++));
				else
					month.setText(Integer.toString(curent++));
				Log.d("month*****************", "" + month.getText());
				/*
				 * if(month.getText().length()==1){
				 * month.setText("0"+Integer.toString(curent++)); }
				 */

			}

			break;

		case R.id.monthminus:

			if (curent > nStartmonth) {
				curent--;
				if (curent < 10)
					month.setText("0" + Integer.toString(curent));
				else
					month.setText(Integer.toString(curent));

			}

			break;

		case R.id.yearplus:

			if (current < nEndyear) {
				current++;
				year.setText(Integer.toString(current++));
				Log.d("month*****************", "" + year.getText().length());
				/*
				 * if(month.getText().length()==1){
				 * month.setText("0"+Integer.toString(curent++)); }
				 */

			}

			break;

		case R.id.yearminus:

			if (current > nStartyear) {
				current--;
				year.setText(Integer.toString(current));
			}

			break;

		case R.id.calculate:

			if (chkvalue == 1) {
				processBilling();

			} else {

				processBilling();

			}

			break;

		default:
			break;
		}

	}

	private void processBilling() {
		// TODO Auto-generated method stub
		Log.d("Inside process billing***********************",
				"Process billing");
		if (filename.getText().equals("Filename")) {

			Toast.makeText(getApplicationContext(), "Please browse price sheet",
					Toast.LENGTH_SHORT).show();

		}

		else {

			AlertDialog.Builder alert = new AlertDialog.Builder(Pricemenu.this);

			alert.setTitle("Confirmation");

			alert.setIcon(R.drawable.helpic);
			if (selectedFile != null) {

				alert.setMessage("Do you want to continue with "
						+ selectedFile.getName());
			} else {

				alert.setMessage("Do you want to continue with " + selectfile);
			}
			alert.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							File sourceFile = new File(selectedPath);
							targetedfile = sourceFile.getName();
							Log.d("targetedfile***********************",
									targetedfile.toString());
							try {
								copyDirectoryOneLocationToAnotherLocation(
										sourceFile, targetedfile);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							// path = selectedFile.getAbsolutePath();
							path = selectedPath;
							int dot = path.lastIndexOf(".");

							String ext = path.substring(dot + 1);

							if (ext.equals("xls")) {

								Subscription subscription = new Subscription();
								subscription.setCreatedby(Integer
										.toString(login_id));
								subscription.setSubcriptionId(subcriptionID);
								// subscription.setUsagesummary(targetedfile);
								subscription.setUsagesummary(selectedPath);
								long upstatus = db
										.updateSubscription(subscription);

								if (upstatus == 0) {
									Toast.makeText(getApplicationContext(),
											"Update fail", Toast.LENGTH_SHORT)
											.show();
								} else {

									Toast.makeText(getApplicationContext(),
											"Updated Successfully",
											Toast.LENGTH_SHORT).show();

								}

								getSubscriptionDetails();

								processlog.setSubscriptionID(subcriptionID);
								processlog.setMonthyear(month.getText()
										.toString()
										+ "-"
										+ year.getText().toString());
								processlog.setLastprocess(date);
								db.createProcessLog(processlog);

								count = db.getPriceDetailcount(month.getText()
										.toString(), year.getText().toString(),
										subcriptionID);
								Log.d("count********************", "" + count);

								if (chkvalue == 1 && count >= 0) {
									db.clearPricedetails(month.getText()
											.toString(), year.getText()
											.toString(), subcriptionID);
									new PriceDetailDownload().execute();

									processlog.setSubscriptionID(subcriptionID);
									processlog.setMonthyear(month.getText()
											.toString()
											+ "-"
											+ year.getText().toString());
									processlog.setLastprocess(date);
									db.createProcessLog(processlog);

								} else {

									if (Integer.parseInt(month.getText()
											.toString()) == currentMonth) {
									/*	Toast.makeText(getApplicationContext(),
												"true", Toast.LENGTH_SHORT)
												.show();
*/
										String processedLog1 = db
												.getProcessLog(
														subcriptionID,
														month.getText()
																.toString()
																+ "-"
																+ year.getText()
																		.toString());
										String[] parts = processedLog1
												.split("-");
										String day = parts[0];
										String mnth = parts[1];
										String yearr = parts[2];
										Log.d("process log111111111111********************",
												"" + processedLog1);

										if (Integer.parseInt(day) == currentDay) {

											count = db.getPriceDetailcount(
													month.getText().toString(),
													year.getText().toString(),
													subcriptionID);
											Log.d("count********************",
													"" + count);

											if (count == 0) {
												db.clearPricedetails(month
														.getText().toString(),
														year.getText()
																.toString(),
														subcriptionID);
												new PriceDetailDownload()
														.execute();

												processlog
														.setSubscriptionID(subcriptionID);
												processlog.setMonthyear(month
														.getText().toString()
														+ "-"
														+ year.getText()
																.toString());
												processlog.setLastprocess(date);
												db.createProcessLog(processlog);

											} else {
												Intent pricedetail = new Intent(
														Pricemenu.this,
														PriceDetails.class);
												pricedetail.putExtra(
														"PRICEDETAIL",
														"pricedetail");
												pricedetail.putExtra("Month",
														month.getText()
																.toString());
												pricedetail.putExtra("Year",
														year.getText()
																.toString());
												startActivity(pricedetail);

											}
										}
									}

									else {

										if (Integer.parseInt(month.getText()
												.toString()) < currentMonth) {
											/*Toast.makeText(
													getApplicationContext(),
													"true lesser",
													Toast.LENGTH_SHORT).show();*/

											String processedLog1 = db
													.getProcessLog(
															subcriptionID,
															month.getText()
																	.toString()
																	+ "-"
																	+ year.getText()
																			.toString());
											String[] parts = processedLog1
													.split("-");
											String day = parts[0];
											String mnth = parts[1];

											String yearr = parts[2];

											if (Integer.parseInt(mnth) > Integer
													.parseInt(month.getText()
															.toString())) {
												count = db.getPriceDetailcount(
														month.getText()
																.toString(),
														year.getText()
																.toString(),
														subcriptionID);
												Log.d("count in second if********************",
														"" + count);

												if (count == 0) {
													db.clearPricedetails(month
															.getText()
															.toString(), year
															.getText()
															.toString(),
															subcriptionID);
													new PriceDetailDownload()
															.execute();
													processlog
															.setSubscriptionID(subcriptionID);
													processlog
															.setMonthyear(month
																	.getText()
																	.toString()
																	+ "-"
																	+ year.getText()
																			.toString());
													processlog
															.setLastprocess(date);
													db.createProcessLog(processlog);

												} else {
													Intent pricedetail = new Intent(
															Pricemenu.this,
															PriceDetails.class);
													pricedetail.putExtra(
															"PRICEDETAIL1",
															"pricedetail1");
													pricedetail
															.putExtra(
																	"Month",
																	month.getText()
																			.toString());
													pricedetail
															.putExtra(
																	"Year",
																	year.getText()
																			.toString());
													startActivity(pricedetail);

												}

											}

										} else {
											Toast.makeText(
													getApplicationContext(),
													"No data",
													Toast.LENGTH_SHORT).show();
										}

									}
								}

							}

							else {
								Toast.makeText(
										getApplicationContext(),
										"Please select correct file to proceed",
										Toast.LENGTH_SHORT).show();
							}

						}
					});

			alert.setNegativeButton("No",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
							/*
							 * final String HALLOWEEN_ORANGE = "#FF7F27";
							 * 
							 * QustomDialogBuilder qustomDialogBuilder = new
							 * QustomDialogBuilder( Pricemenu.this)
							 * .setTitle("Set IP Address")
							 * .setTitleColor(HALLOWEEN_ORANGE)
							 * .setDividerColor(HALLOWEEN_ORANGE) .setMessage(
							 * "You are now entering the 10th dimension.")
							 * .setIcon( getResources().getDrawable(
							 * R.drawable.ic_launcher));
							 * 
							 * qustomDialogBuilder.show();
							 */

						}
					});
			alert.show();

		}
	}

	public void copyDirectoryOneLocationToAnotherLocation(File sourceLocation,
			String targetFile) throws IOException {

		try {
			InputStream in = new FileInputStream(sourceLocation);

			FileOutputStream fos;
			fos = openFileOutput(targetFile, Context.MODE_PRIVATE);
			// openFileOutput(targetFile, Context.MODE_PRIVATE);
			// Copy the bits from instream to outstream

			Log.d("targetFile************", "" + targetFile.toString());
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				fos.write(buf, 0, len);
			}
			in.close();
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {

			switch (requestCode) {

			case REQUEST_PICK_FILE:

				if (data.hasExtra(Filepicker.EXTRA_FILE_PATH)) {

					selectedFile = new File(
							data.getStringExtra(Filepicker.EXTRA_FILE_PATH));
					selectedPath = selectedFile.getAbsolutePath();

					filename.setText(selectedFile.getName());
				}
				break;
			}
		}
	}

	public class PriceDetailDownload extends AsyncTask<Void, String, Void>
			implements OnCancelListener {
		ProgressHUD mProgressHUD;

		@Override
		protected void onPreExecute() {
			mProgressHUD = ProgressHUD.show(Pricemenu.this, "Connecting", true,
					true, this);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				publishProgress("Connecting");
				Thread.sleep(2000);
				publishProgress("Processing");
				Thread.sleep(5000);
				billing();

				publishProgress("Done");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			mProgressHUD.setMessage(values[0]);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Void result) {

			mProgressHUD.dismiss();

			super.onPostExecute(result);
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			this.cancel(true);
			mProgressHUD.dismiss();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pricemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {

		case R.id.switchsubscription:
			Intent switchsubs = new Intent(Pricemenu.this,
					SubscriptionList.class);
			startActivity(switchsubs);
			return true;

		case R.id.action_resource:
			Intent resource = new Intent(Pricemenu.this, Resource.class);
			startActivity(resource);
			return true;

		case R.id.monitormachine:
			Intent monitormachine = new Intent(Pricemenu.this,
					MachineMonitorActivity.class);
			startActivity(monitormachine);
			return true;

		case R.id.subscription:
			Intent sub = new Intent(Pricemenu.this, SubscriptionActivity.class);
			startActivity(sub);
			return true;

		case R.id.home:
			Intent home = new Intent(Pricemenu.this, Dashbaord.class);
			startActivity(home);
			return true;

		case R.id.subscriptionmenu:
			Intent subscriptionmenu = new Intent(Pricemenu.this,
					SubscriptionActivity.class);
			startActivity(subscriptionmenu);
			return true;

		case R.id.pricedetails:
			Intent pricedetail = new Intent(Pricemenu.this, PriceDetails.class);
			startActivity(pricedetail);
			return true;

		case R.id.account_settings:
			Intent accsettings = new Intent(Pricemenu.this,
					AccountSettingActivity.class);
			startActivity(accsettings);
			return true;

		case R.id.action_logout:
			Intent home1 = new Intent(Pricemenu.this, LoginActivity.class);
			startActivity(home1);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void billing() {
		Log.d("billing function start", "billing function");
		/*
		 * StrictMode.ThreadPolicy policy = new
		 * StrictMode.ThreadPolicy.Builder().permitAll().build();
		 * StrictMode.setThreadPolicy(policy);
		 */

		Map<String, ServicePojo> servicePojoMap = new HashMap<String, ServicePojo>();
		try {

			// InputStream fis = this.getAssets().open("Sysfore.xls");
			InputStream fis = openFileInput(targetedfile);
			// InputStream fis = openFileInput(targetedfile);
			Log.d("billing function", "billing function");
			Workbook wb = Workbook.getWorkbook(fis);
			Sheet ws = wb.getSheet(1);

			int rowNum = ws.getRows() + 1;

			int colNum = 7;

			for (int j = 7; j < ws.getRows(); j++) {
				ServicePojo servicePojo = new ServicePojo();

				for (int i = 0; i < ws.getColumns(); i++) {
					Cell cell = ws.getCell(i, j);
					if (i == 0) {
						servicePojo.setService(cell.getContents());
					} else if (i == 1) {
						servicePojo.setUnitOfMeasure(cell.getContents());
					} else if (i == 3) {
						servicePojo.setCommitmentUnitPrice(cell.getContents());
					} else if (i == 6) {
						servicePojo.setCommitmentDiscount(cell.getContents());
					}
				}

				servicePojoMap.put(servicePojo.getService(), servicePojo);

			}

		} catch (Exception e) {
			// e.printStackTrace();
		}

		/*
		 * for(String key :servicePojoMap.keySet()) { Log.d("servicepojo: ",
		 * servicePojoMap.get(key).toString()); }
		 */
		List<ResultPojo> resultList = new ArrayList<ResultPojo>();
		BillingService billingService = new BillingService();
		Map<String, List<BillingPojo>> pojoMap = billingService.getBillDetails(
				enrollmentId, token, month.getText().toString(), year.getText()
						.toString());
		DecimalFormat df = new DecimalFormat("#,###,##0.00");
		for (String key : pojoMap.keySet()) {
			// if(key.equals("\"fc7b7fb1-2b20-4fdc-addb-42abd01f1317\"")) {

			if (key.equals("\"" + subcriptionID + "\"")) {

				DataProcess dataProcess = new DataProcess();
				List<BillingPojo> billingPojoList = pojoMap.get(key);
				Map<String, List<BillingPojo>> billingPojoMap = dataProcess
						.processServiceResources(billingPojoList);
				Map<String, List<CalculationPojo>> calculationPojoMap = dataProcess
						.calculateAzureUsage(billingPojoMap);
				for (String resourceKey : calculationPojoMap.keySet()) {
					List<CalculationPojo> calculationList = calculationPojoMap
							.get(resourceKey);
					for (CalculationPojo cpj : calculationList) {
						ServicePojo service = servicePojoMap.get(cpj
								.getProduct());
						String sum = "0";
						try {
							String split[] = (service.getUnitOfMeasure())
									.split(" ", 2);
							sum = split[0].replace(",", "");
							BigDecimal calculate = new BigDecimal(""
									+ cpj.getSum());
							ResultPojo result = new ResultPojo();
							result.setProduct(cpj.getProduct());
							result.setPerUnitCommitted(service
									.getCommitmentUnitPrice());

							result.setUnitConsumed("" + df.format(cpj.getSum()));
							result.setUnitofMeasure(service.getUnitOfMeasure());
							if (!service.getCommitmentUnitPrice().equals("0.0")) {
								BigDecimal value = calculate.divide(
										new BigDecimal(sum)).multiply(
										new BigDecimal(service
												.getCommitmentUnitPrice()));
								if(cpj.getProduct().equalsIgnoreCase("Storage Transactions")){
								result.setBillTotal(""
										+ df.format((value.doubleValue()*10000)));
								}else{
									result.setBillTotal(""
											+ df.format(value.doubleValue()));
								}
							} else {
								result.setBillTotal("0.00");
							}
							// resultList.add(result);
							result.setMonth(month.getText().toString());
							result.setYear(year.getText().toString());
							result.setCreatedby(Integer.toString(login_id));
							result.setSubcriptionId(subcriptionID);
							db.createPriceDetail(result);

						} catch (Exception e) {

						}

					}
				}
			}

		}
		Intent pricedetail = new Intent(Pricemenu.this, PriceDetails.class);
		pricedetail.putExtra("Month", month.getText().toString());
		pricedetail.putExtra("Year", year.getText().toString());

		startActivity(pricedetail);
		/*
		 * for(ResultPojo result : resultList) { Log.d("My result:",
		 * result.toString()); }
		 */
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
		// TODO Auto-generated method stub
		if (isChecked) {
			chkvalue = 1;
		} else
			chkvalue = 0;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent dash = new Intent(Pricemenu.this, Dashbaord.class);
		startActivity(dash);
	}
}
