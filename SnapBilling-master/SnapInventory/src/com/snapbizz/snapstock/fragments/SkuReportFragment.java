package com.snapbizz.snapstock.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import net.sqlcipher.Cursor;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.MonthlySkuReportAdpater;
import com.snapbizz.snapstock.asynctasks.SearchProductBySubCatIdTask;
import com.snapbizz.snapstock.interfaces.GetResponseBackToUpdateFragment;
import com.snapbizz.snaptoolkit.asynctasks.GetMonthlyProductSkuReportTask;
import com.snapbizz.snaptoolkit.domains.BillItem;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

public class SkuReportFragment extends Fragment implements
		OnQueryCompleteListener {
	private final int GET_MONTHLY_SKU_REPORT_TASKCODE = 2;
	private final int GET_PROD_CAT_REPORT_TASKCODE = 1;
	private final int GET_PRV_MONTHLY_SKU_REPORT_TASKCODE = 3;
	private float totalQtySoldWk1 = 0;
	private float totalQtySoldWk2 = 0;
	private float totalQtySoldWk3 = 0;
	private float totalQtySoldWk4 = 0;
	private float totalQtySoldMonthWk1 = 0;
	private float totalQtySoldMonthWk2 = 0;
	private float totalQtySoldMonthWk3 = 0;
	private float totalQtySoldMonthWk4 = 0;
	private float totalAmountWk1 = 0;
	private float totalAmountWk2 = 0;
	private float totalAmountWk3 = 0;
	private float totalAmountWk4 = 0;
	private View stockLayout = null;
	private View stockweeklyLayout = null;
	private MonthlySkuReportAdpater monthlySkuReportAdpt = null;
	private String monthPos = "";
	private Date now;
	private ImageView alertPrdSoldPrWk = null;
	private TextView alertPrdSoldPrWkTxt = null;
	private ImageView alertImgOne = null;
	private TextView alert_text_one = null;
	private GetResponseBackToUpdateFragment getResponseBackToUpdateFragment = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		now = new Date();

		View view = inflater.inflate(R.layout.fragment_sku_report, null);
		view.setFocusableInTouchMode(true);
		view.requestFocus();
		view.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {

					try {
						getActivity().getActionBar().setCustomView(
								R.layout.actionbar_update_layout);

						((TextView) getActivity()
								.getActionBar()
								.getCustomView()
								.findViewById(
										R.id.update_category_name_textview))
								.setVisibility(View.VISIBLE);
						((TextView) getActivity()
								.getActionBar()
								.getCustomView()
								.findViewById(
										R.id.update_category_name_textview))
								.setText(SnapToolkitConstants.CAT_VALUE);
						((TextView) getActivity()
								.getActionBar()
								.getCustomView()
								.findViewById(
										R.id.update_sub_category_name_textview))
								.setText(SnapToolkitConstants.SUB_CAT_VALUE);
						setHasOptionsMenu(true);
						getActivity().getActionBar().setHomeButtonEnabled(true);
						View homeIcon = getActivity().findViewById(
								android.R.id.home);
						((View) homeIcon.getParent())
								.setVisibility(View.VISIBLE);
						((View) homeIcon).setVisibility(View.VISIBLE);
						getResponseBackToUpdateFragment.getResponse();
						getActivity().onBackPressed();
						return true;
					} catch (NullPointerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return false;
					}
				} else {

					return false;

				}
			}
		});
		stockLayout = view.findViewById(R.id.stockLayout);
		stockweeklyLayout = view.findViewById(R.id.stockweeklyLayout);
		alertPrdSoldPrWk = (ImageView) view.findViewById(R.id.alertImgTwo);
		alert_text_one = (TextView) view.findViewById(R.id.alert_text_one);
		alertImgOne = (ImageView) view.findViewById(R.id.alertImgOne);
		alertPrdSoldPrWkTxt = (TextView) view.findViewById(R.id.alert_text_two);
		alertPrdSoldPrWk.setVisibility(View.GONE);
		alertPrdSoldPrWkTxt.setVisibility(View.GONE);
		alert_text_one.setVisibility(View.GONE);
		alertImgOne.setVisibility(View.GONE);
		return view;
	}

	OnClickListener showListClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				PopupMenu popup = new PopupMenu(getActivity(), v);
				popup.getMenuInflater().inflate(R.menu.month, popup.getMenu());
				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						((TextView) getActivity().getActionBar()
								.getCustomView()
								.findViewById(R.id.monthOptionText))
								.setText(item.toString());
						alertPrdSoldPrWk.setVisibility(View.GONE);
						alertPrdSoldPrWkTxt.setVisibility(View.GONE);
						alert_text_one.setVisibility(View.GONE);
						alertImgOne.setVisibility(View.GONE);
						switch (item.getItemId()) {
						case R.id.jan_menuitem:
							monthPos = "01";
							String prvYr = "";
//							if (!(((Integer.parseInt(new SimpleDateFormat("MM")
//									.format(now)) - 01) + "").length() > 1)) {
//								prvYr = "0"
//										+ (Integer
//												.parseInt(new SimpleDateFormat(
//														"yyyy").format(now)) - 01);
//							} else {
								prvYr = ""
										+ (Integer
												.parseInt(new SimpleDateFormat(
														"yyyy").format(now)) - 01);

//							}
							if (getArguments().getString("code") != null) {

								new GetMonthlyProductSkuReportTask(
										getActivity(), SkuReportFragment.this,
										GET_MONTHLY_SKU_REPORT_TASKCODE,
										getArguments().getString("code"), ""
												+ monthPos + "",
										new SimpleDateFormat("yyyy")
												.format(now)).execute();

								new GetMonthlyProductSkuReportTask(
										getActivity(), SkuReportFragment.this,
										GET_PRV_MONTHLY_SKU_REPORT_TASKCODE,
										getArguments().getString("code"), ""
												+ "12", prvYr).execute();
							}
							return true;

						case R.id.feb_menuitem:
							GetMonthlyProductSkuReportMethod("02", "01");
							return true;
						case R.id.march_menuitem:

							GetMonthlyProductSkuReportMethod("03", "02");

							return true;
						case R.id.april_menuitem:
							GetMonthlyProductSkuReportMethod("04", "03");

							return true;
						case R.id.may_menuitem:
							GetMonthlyProductSkuReportMethod("05", "04");

							return true;
						case R.id.june_menuitem:
							GetMonthlyProductSkuReportMethod("06", "05");

							return true;
						case R.id.july_menuitem:
							GetMonthlyProductSkuReportMethod("07", "06");

							return true;
						case R.id.aug_menuitem:
							GetMonthlyProductSkuReportMethod("08", "07");

							return true;
						case R.id.sept_menuitem:
							GetMonthlyProductSkuReportMethod("09", "08");

							return true;
						case R.id.oct_menuitem:
							GetMonthlyProductSkuReportMethod("10", "09");

							return true;
						case R.id.nov_menuitem:
							GetMonthlyProductSkuReportMethod("11", "10");

							return true;
						case R.id.dec_menuitem:
							GetMonthlyProductSkuReportMethod("12", "11");
							return true;

						default:
							return false;

						}

					}
				});

				popup.show();
			} catch (NullPointerException e) {
				e.printStackTrace();
			}

		}
	};

	OnClickListener homeClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				getActivity().getActionBar().setCustomView(
						R.layout.actionbar_update_layout);
				((TextView) getActivity().getActionBar().getCustomView()
						.findViewById(R.id.update_category_name_textview))
						.setText(SnapToolkitConstants.CAT_VALUE);
				((TextView) getActivity().getActionBar().getCustomView()
						.findViewById(R.id.update_sub_category_name_textview))
						.setText(SnapToolkitConstants.SUB_CAT_VALUE);
				setHasOptionsMenu(true);
				getActivity().getActionBar().setHomeButtonEnabled(true);
				View homeIcon = getActivity().findViewById(android.R.id.home);
				((View) homeIcon.getParent()).setVisibility(View.VISIBLE);
				((View) homeIcon).setVisibility(View.VISIBLE);
				getResponseBackToUpdateFragment.getResponse();

				getActivity().onBackPressed();
			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getActivity().getActionBar().setCustomView(
				R.layout.stock_report_actionbar);
		SnapCommonUtils.hideSoftKeyboard(getActivity(), getView().getWindowToken());
		View homeIcon = getActivity().findViewById(android.R.id.home);
		((View) homeIcon.getParent()).setVisibility(View.GONE);
		((View) homeIcon).setVisibility(View.GONE);
		setHasOptionsMenu(false);
		getActivity().getActionBar().setHomeButtonEnabled(false);
		getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
		if (getArguments().getString("name") != null) {
			((TextView) getActivity().getActionBar().getCustomView()
					.findViewById(R.id.update_prodname_textview))
					.setText(getArguments().getString("name"));
		}
		if (getArguments().getString("code") != null) {

			// ((TextView) getActivity().getActionBar().getCustomView()
			// .findViewById(R.id.update_prodcode_textview))
			// .setText(getArguments().getString("code"));
		}
		if (getArguments().getString("stock_qty") != null) {

			((TextView) stockLayout.findViewById(R.id.current_frst_rw_text))
					.setText(getArguments().getString("stock_qty"));
		}
		if (getArguments().getString("mrp") != null) {
			if(getArguments().getString("mrp").contains("e")){
				Double t = Double.valueOf(getArguments().getString("mrp"));
				long n = t.longValue();
				((TextView) stockLayout
						.findViewById(R.id.current_scnd_rw_text))
						.setText(String.valueOf(n));
			}
			else{
				((TextView) stockLayout
						.findViewById(R.id.current_scnd_rw_text))
						.setText(getArguments().getString("mrp"));
			}
			
		}		
		((RelativeLayout) getActivity().getActionBar().getCustomView()
				.findViewById(R.id.monthOptionTextLayout))
				.setOnClickListener(showListClickListener);
		((RelativeLayout) getActivity().getActionBar().getCustomView()
				.findViewById(R.id.hm_layout)).setOnClickListener(homeClick);

		if (getArguments().getString("sub_cat_id") != null) {
			new SearchProductBySubCatIdTask(getActivity(),
					SkuReportFragment.this, GET_PROD_CAT_REPORT_TASKCODE,
					getArguments().getString("sub_cat_id")).execute();
		}
		((TextView) getActivity().getActionBar().getCustomView()
				.findViewById(R.id.monthOptionText)).setText(Calendar
				.getInstance().getDisplayName(Calendar.MONTH, Calendar.LONG,
						Locale.ENGLISH));

		if (getArguments().getString("code") != null) {
			new GetMonthlyProductSkuReportTask(getActivity(),
					SkuReportFragment.this, GET_MONTHLY_SKU_REPORT_TASKCODE,
					getArguments().getString("code"), ""
							+ new SimpleDateFormat("MM").format(now) + "",
					new SimpleDateFormat("yyyy").format(now)).execute();
		}

		if (getArguments().getString("code") != null) {
			String prvMonth = "";
			if (!(((Integer.parseInt(new SimpleDateFormat("MM").format(now)) - 01) + "")
					.length() > 1)) {
				prvMonth = "0"
						+ (Integer.parseInt(new SimpleDateFormat("MM")
								.format(now)) - 01);
			} else {
				prvMonth = ""
						+ (Integer.parseInt(new SimpleDateFormat("MM")
								.format(now)) - 01);

			}

			new GetMonthlyProductSkuReportTask(getActivity(),
					SkuReportFragment.this,
					GET_PRV_MONTHLY_SKU_REPORT_TASKCODE, getArguments()
							.getString("code"), "" + prvMonth,
					new SimpleDateFormat("yyyy").format(now)).execute();
		}

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof OnActionbarNavigationListener))
			throw new ClassCastException(activity.getLocalClassName()
					+ " must implement OnActionbarNavigationListener");
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {

		if (taskCode == GET_PROD_CAT_REPORT_TASKCODE) {

			if (((Cursor) responseList).getString(0) != null) {
				if(((Cursor) responseList).getString(0).contains("e")){
					long n = Double.valueOf(((Cursor) responseList).getString(0)).longValue();
					((TextView) stockLayout
							.findViewById(R.id.category_first_rw_text))
							.setText(String.valueOf(n));
				}
				else{
					((TextView) stockLayout
							.findViewById(R.id.category_first_rw_text))
							.setText(((Cursor) responseList).getString(0));
				}
				
			}
			if (((Cursor) responseList).getString(1) != null) {
				if(((Cursor) responseList).getString(1).contains("e")){
					long n = Double.valueOf(((Cursor) responseList).getString(1)).longValue();
					((TextView) stockLayout
							.findViewById(R.id.category_scnd_rw_text))
							.setText(String.valueOf(n));
				}
				else{
					((TextView) stockLayout
							.findViewById(R.id.category_scnd_rw_text))
							.setText(((Cursor) responseList).getString(1));
				}
				
			}

			// }
		}

		else if (taskCode == GET_MONTHLY_SKU_REPORT_TASKCODE) {
			
			if (((ArrayList<BillItem>) responseList != null)) {
				
				getFilterByMonth(((ArrayList<BillItem>) responseList));

				((ListView) getView().findViewById(
						R.id.sku_product_stock_list))
						.setVisibility(View.VISIBLE);
				monthlySkuReportAdpt = new MonthlySkuReportAdpater(
						getActivity(), (ArrayList<BillItem>) responseList);
				if (getView() != null) {
					((ListView) getView().findViewById(
							R.id.sku_product_stock_list))
							.setAdapter(monthlySkuReportAdpt);
				}
			} else {
				getFilterByMonth(null);
			}
			
			
			
//			if(monthlySkuReportAdpt!=null){
//				if (((ArrayList<BillItem>) responseList).size() == 0) {
//					getFilterByMonth(((ArrayList<BillItem>) responseList));
//					monthlySkuReportAdpt.notifyDataSetChanged();
//					((ListView) getView().findViewById(
//							R.id.sku_product_stock_list))
//							.setVisibility(View.INVISIBLE);
//				} else {
//					monthlySkuReportAdpt.clear();
//					getFilterByMonth(((ArrayList<BillItem>) responseList));
//					((ListView) getView().findViewById(
//							R.id.sku_product_stock_list))
//							.setVisibility(View.VISIBLE);
//					if (getView() != null) {
//						((ListView) getView().findViewById(
//								R.id.sku_product_stock_list))
//								.setAdapter(monthlySkuReportAdpt);
//					}
////					if (monthlySkuReportAdpt != null)
////
////						monthlySkuReportAdpt.notifyDataSetChanged();
//				}
//			}
//			else{
//if ((ArrayList<BillItem>) responseList != null) {
//					
//					getFilterByMonth(((ArrayList<BillItem>) responseList));
//
//					((ListView) getView().findViewById(
//							R.id.sku_product_stock_list))
//							.setVisibility(View.VISIBLE);
//					monthlySkuReportAdpt = new MonthlySkuReportAdpater(
//							getActivity(), (ArrayList<BillItem>) responseList);
//					if (getView() != null) {
//						((ListView) getView().findViewById(
//								R.id.sku_product_stock_list))
//								.setAdapter(monthlySkuReportAdpt);
//					}
//				} else {
//					getFilterByMonth(null);
//				}
//
//			}
			
			
			
//			if (monthlySkuReportAdpt == null) {
//				if ((ArrayList<BillItem>) responseList != null) {
//					
//					getFilterByMonth(((ArrayList<BillItem>) responseList));
//
//					((ListView) getView().findViewById(
//							R.id.sku_product_stock_list))
//							.setVisibility(View.VISIBLE);
//					monthlySkuReportAdpt = new MonthlySkuReportAdpater(
//							getActivity(), (ArrayList<BillItem>) responseList);
//					if (getView() != null) {
//						((ListView) getView().findViewById(
//								R.id.sku_product_stock_list))
//								.setAdapter(monthlySkuReportAdpt);
//					}
//				} else {
//					getFilterByMonth(null);
//				}
//
//			} else {
//				if (((ArrayList<BillItem>) responseList).size() == 0) {
//					getFilterByMonth(((ArrayList<BillItem>) responseList));
//					monthlySkuReportAdpt.notifyDataSetChanged();
//					((ListView) getView().findViewById(
//							R.id.sku_product_stock_list))
//							.setVisibility(View.INVISIBLE);
//				} else {
//
//					getFilterByMonth(((ArrayList<BillItem>) responseList));
//					((ListView) getView().findViewById(
//							R.id.sku_product_stock_list))
//							.setVisibility(View.VISIBLE);
//					if (monthlySkuReportAdpt != null)
//
//						monthlySkuReportAdpt.notifyDataSetChanged();
//				}
//
//			}
		} else if (taskCode == GET_PRV_MONTHLY_SKU_REPORT_TASKCODE) {
			if ((ArrayList<BillItem>) responseList != null
					&& ((ArrayList<BillItem>) responseList).size() > 0) {
				getFilterByPrevMonth((ArrayList<BillItem>) responseList);

			}
			showMonthlyAlarm();

		}

	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {

	}

	public void getFilterByMonth(ArrayList<BillItem> responseList) {
		ArrayList<BillItem> currentMonthBillList = new ArrayList<BillItem>();
		String trimDate[] = null;
		totalQtySoldWk1 = 0;
		totalQtySoldWk2 = 0;
		totalQtySoldWk3 = 0;
		totalQtySoldWk4 = 0;
		totalQtySoldMonthWk1 = 0;
		totalQtySoldMonthWk2 = 0;
		totalQtySoldMonthWk3 = 0;
		totalQtySoldMonthWk4 = 0;
		totalAmountWk1 = 0;
		totalAmountWk2 = 0;
		totalAmountWk3 = 0;
		totalAmountWk4 = 0;
		if (responseList != null) {
			if (responseList.size() > 0) {
				for (int i = 0; i < responseList.size(); i++) {

					if (responseList.get(i).getLastModifiedTimestamp() != null) {

						if (responseList.get(i).getLastModifiedTimestamp()
								.toString().contains(" ")) {
							trimDate = responseList.get(i)
									.getLastModifiedTimestamp().toString()
									.split("\\s+");
						}
						if (trimDate != null && trimDate.length > 0) {
							// SimpleDateFormat formatter = new
							// SimpleDateFormat(
							// "yyyy/MM/dd");
							int month = 0;

							month = Integer.parseInt(trimDate[2]);
							// Date date = formatter.parse(trimDate[0]);
							if ((month > 0) && (month < 8)) {
								totalAmountWk1 = totalAmountWk1
										+ responseList.get(i)
												.getProductSkuMrp();
								totalQtySoldWk1 = totalQtySoldWk1
										+ responseList.get(i)
												.getProductSkuQuantity();
							} else if ((month > 7) && (month < 15)) {
								totalQtySoldWk2 = totalQtySoldWk2
										+ responseList.get(i)
												.getProductSkuQuantity();
								totalAmountWk2 = totalAmountWk2
										+ responseList.get(i)
												.getProductSkuMrp();

							} else if ((month > 14) && (month < 22)) {
								totalQtySoldWk3 = totalQtySoldWk3
										+ responseList.get(i)
												.getProductSkuQuantity();
								totalAmountWk3 = totalAmountWk3
										+ responseList.get(i)
												.getProductSkuMrp();

							} else if ((month > 21) && (month < 32)) {
								totalAmountWk4 = totalAmountWk4
										+ responseList.get(i)
												.getProductSkuMrp();

								totalQtySoldWk4 = totalQtySoldWk4
										+ responseList.get(i)
												.getProductSkuQuantity();

							}
						}

					}

				}

			}
		}
		((TextView) stockweeklyLayout.findViewById(R.id.one_seven_scnd_row))
				.setText(totalAmountWk1 + "");
		((TextView) stockweeklyLayout
				.findViewById(R.id.eight_forteen_scnd_rw_txt))
				.setText(totalAmountWk2 + "");
		((TextView) stockweeklyLayout.findViewById(R.id.ftn__secnd_rw_text))
				.setText(totalAmountWk3 + "");
		((TextView) stockweeklyLayout
				.findViewById(R.id.twenty_sec_secnd_rw_text))
				.setText(totalAmountWk4 + "");
		((TextView) stockweeklyLayout.findViewById(R.id.one_seven_frst_row))
				.setText(totalQtySoldWk1 + "");
		((TextView) stockweeklyLayout
				.findViewById(R.id.eight_forteen_frst_rw_txt))
				.setText(totalQtySoldWk2 + "");
		((TextView) stockweeklyLayout.findViewById(R.id.ftn__frst_rw_text))
				.setText(totalQtySoldWk3 + "");
		((TextView) stockweeklyLayout
				.findViewById(R.id.twenty_sec_frst_rw_text))
				.setText(totalQtySoldWk4 + "");
	}
	


	public void getFilterByPrevMonth(ArrayList<BillItem> responseList) {
		String trimDate[] = null;

		for (int i = 0; i < responseList.size(); i++) {

			if (responseList.get(i).getLastModifiedTimestamp() != null) {

				if (responseList.get(i).getLastModifiedTimestamp().toString()
						.contains(" ")) {
					trimDate = responseList.get(i).getLastModifiedTimestamp()
							.toString().split("\\s+");
				}
				if (trimDate != null && trimDate.length > 0) {
					// SimpleDateFormat formatter = new SimpleDateFormat(
					// "yyyy/MM/dd");
					int month = 0;

					month = Integer.parseInt(trimDate[2]);
					// Date date = formatter.parse(trimDate[0]);
					if ((month > 0) && (month < 8)) {
						totalQtySoldMonthWk1 = totalQtySoldMonthWk1
								+ responseList.get(i).getProductSkuQuantity();

						if (responseList.get(i).getProductSkuQuantity() > totalQtySoldWk1) {

							alertPrdSoldPrWk.setVisibility(View.VISIBLE);
							alertPrdSoldPrWkTxt.setVisibility(View.VISIBLE);

						}
					} else if ((month > 7) && (month < 15)) {
						totalQtySoldMonthWk2 = totalQtySoldMonthWk2
								+ responseList.get(i).getProductSkuQuantity();

						if (responseList.get(i).getProductSkuQuantity() > totalQtySoldWk2) {
							alertPrdSoldPrWk.setVisibility(View.VISIBLE);
							alertPrdSoldPrWkTxt.setVisibility(View.VISIBLE);

						}

					} else if ((month > 14) && (month < 22)) {
						totalQtySoldMonthWk3 = totalQtySoldMonthWk3
								+ responseList.get(i).getProductSkuQuantity();

						if (responseList.get(i).getProductSkuQuantity() > totalQtySoldWk3) {
							alertPrdSoldPrWk.setVisibility(View.VISIBLE);
							alertPrdSoldPrWkTxt.setVisibility(View.VISIBLE);

						}
					} else if ((month > 21) && (month < 32)) {
						totalQtySoldMonthWk4 = totalQtySoldMonthWk4
								+ responseList.get(i).getProductSkuQuantity();

						if (responseList.get(i).getProductSkuQuantity() > totalQtySoldWk4) {
							alertPrdSoldPrWk.setVisibility(View.VISIBLE);
							alertPrdSoldPrWkTxt.setVisibility(View.VISIBLE);

						}
					}
				}

			}

		}

	}

	private void showMonthlyAlarm() {
		int day = (Integer.parseInt(new SimpleDateFormat("dd").format(now)));

		if ((day > 0) && (day < 8)) {
			if ((totalQtySoldMonthWk1 + totalQtySoldMonthWk2 + totalQtySoldMonthWk3
					+ totalQtySoldMonthWk4 + totalQtySoldWk1 <= 0)) {
				alert_text_one.setVisibility(View.VISIBLE);
				alertImgOne.setVisibility(View.VISIBLE);

			}
		} else if ((day > 7) && (day < 15)) {

			if ((totalQtySoldMonthWk2 + totalQtySoldMonthWk3 + totalQtySoldMonthWk4
					+ totalQtySoldWk1 + totalQtySoldWk2 <= 0)) {
				alert_text_one.setVisibility(View.VISIBLE);
				alertImgOne.setVisibility(View.VISIBLE);

			}

		} else if ((day > 14) && (day < 22)) {
			if ((totalQtySoldMonthWk3 + totalQtySoldMonthWk4 + totalQtySoldWk1
					+ totalQtySoldWk2 + totalQtySoldWk3 <= 0)) {
				alert_text_one.setVisibility(View.VISIBLE);
				alertImgOne.setVisibility(View.VISIBLE);

			}
		} else if ((day > 21) && (day < 32)) {
			if ((totalQtySoldWk4 + totalQtySoldWk1 + totalQtySoldWk2+totalQtySoldWk3+
					 totalQtySoldMonthWk4 <= 0)) {
				alert_text_one.setVisibility(View.VISIBLE);
				alertImgOne.setVisibility(View.VISIBLE);

			}
		}
	}

	public void setBackListener(
			GetResponseBackToUpdateFragment mResponseBackToUpdateFragment) {
		getResponseBackToUpdateFragment = mResponseBackToUpdateFragment;
	}

	private void GetMonthlyProductSkuReportMethod(String selectedMonth,
			String prevMonth) {
		if (getArguments().getString("code") != null) {
			new GetMonthlyProductSkuReportTask(getActivity(),
					SkuReportFragment.this, GET_MONTHLY_SKU_REPORT_TASKCODE,
					getArguments().getString("code"), "" + selectedMonth + "",
					new SimpleDateFormat("yyyy").format(now)).execute();
			new GetMonthlyProductSkuReportTask(getActivity(),
					SkuReportFragment.this,
					GET_PRV_MONTHLY_SKU_REPORT_TASKCODE, getArguments()
							.getString("code"), "" + prevMonth,
					new SimpleDateFormat("yyyy").format(now)).execute();
		}
	}

}
