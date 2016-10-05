package com.snapbizz.snapbilling.fragments;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.stmt.QueryBuilder;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.asynctasks.GetCompositeVatTask;
import com.snapbizz.snapbilling.asynctasks.GetDetailedPurchaseReportTask;
import com.snapbizz.snapbilling.asynctasks.GetDetailedSalesReportTask;
import com.snapbizz.snapbilling.asynctasks.GetPurchaseVatTask;
import com.snapbizz.snapbilling.asynctasks.GetSalesVatTask;
import com.snapbizz.snapbilling.utils.GoogleAnalyticsTracker;
import com.snapbizz.snapbilling.utils.Month;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.asynctasks.GetStateVatRatesTask;
import com.snapbizz.snaptoolkit.domains.State;
import com.snapbizz.snaptoolkit.domains.VAT;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;
import com.snapbizz.snaptoolkit.utils.WriteToXls;

public class VatSummaryFragment extends Fragment implements
		OnQueryCompleteListener {

	private final String TAG = "[VatSummaryFragment]";
	private final int GET_SALES_VAT_TASKCODE = 0;
	private final int GET_PURCHASE_VAT_TASKCODE = 1;
	private final int GET_COMPOSITE_VAT_TASKCODE = 2;
	private final int GET_STATE_VAT_RATE_TASKCODE = 3;
	private final int GET_DETAILED_SALES_TASKCODE = 4;
	private final int GET_DETAILED_PURCHASE_TASKCODE = 5;
	String CompositeRate;
	private float mTotalVatSales;
	private float mTotalOutputPayable;
	private float mTotalPurchaseVat;
	private float mTotalInputPayable;
	private float outputVats[] = null;
	private double mCompositeVatValue;
	private double mCompositeSalesValue;
	private HashMap<Float, String[]> mSalesVatValues;
	private HashMap<Float, String[]> mPurchaseVatValues;
	private List<VAT> mVatList;
	private boolean isCompositeReport;
	private Spinner mMonthSpinner;
	private Spinner mYearSpinner;
	private Button mGetVatReportBtn;
	private Button mRegularVatBtn;
	private Button mCompositeVatBtn;
	private TextView mVatCategory1TextView;
	private TextView mVatCategory2TextView;
	private TextView mVatCategory3TextView;
	private TextView mVatCategory4TextView;
	private TextView mSalesValue1TextView;
	private TextView mSalesValue2TextView;
	private TextView mSalesValue3TextView;
	private TextView mSalesValue4TextView;
	private TextView mNetPayableValue1TextView;
	private TextView mNetPayableValue2TextView;
	private TextView mNetPayableValue3TextView;
	private TextView mNetPayableValue4TextView;
	private TextView mSalesTotalValueTextView;
	private TextView mOutputPayable1TextView;
	private TextView mOutputPayable2TextView;
	private TextView mOutputPayable3TextView;
	private TextView mOutputPayable4TextView;
	private TextView mCompositeValueTextView;
	private TextView mCompositeSalesTextView;
	private TextView mPurchaseValue1TextView;
	private TextView mPurchaseValue2TextView;
	private TextView mPurchaseValue3TextView;
	private TextView mPurchaseValue4TextView;
	private TextView mInputCredit1TextView;
	private TextView mInputCredit2TextView;
	private TextView mInputCredit3TextView;
	private TextView mInputCredit4TextView;
	private TextView mNetPayableTotalValueTextView;
	private TextView mOutputPayableTotalTextView;
	private TextView mInputPayableTotalTextView;
	private TextView mPurchaseTotalValueTextView;
	private EditText mCompositeVatEditText;
	private String mStartDate;
	private String mEndDate;
	private String[] mVatCaptions;
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private GetSalesVatTask mGetSalesVatTask;
	private GetPurchaseVatTask mGetPurchaseVatTask;
	private GetCompositeVatTask mGetCompositeVatTask;
	private GetStateVatRatesTask mGetStateVatRatesTask;
	private GetDetailedSalesReportTask mGetDetailedSalesReportTask;
	private RelativeLayout mCompositeLayout;
	private LinearLayout mRegularLayout;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		GoogleAnalyticsTracker.getInstance(getActivity()).fragmentLoaded(getClass().getSimpleName(), getActivity());
		View view = inflater.inflate(R.layout.fragment_vat_summary, container,
				false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getView().findViewById(R.id.saveSummaryButton).setOnClickListener(
				onSaveSummaryClickListener);
		getView().findViewById(R.id.saveReportButton).setOnClickListener(
				onSaveDetailedReportClickListener);
		mRegularVatBtn = (Button) getView().findViewById(
				R.id.regular_view_button);
		mCompositeVatBtn = (Button) getView().findViewById(
				R.id.composite_view_button);
		mCompositeLayout = (RelativeLayout) getView().findViewById(
				R.id.composite_vat_layout);
		mRegularLayout = (LinearLayout) getView().findViewById(
				R.id.regular_vat_layout);
		mRegularVatBtn.setOnClickListener(onShowRegularLayoutListener);
		mCompositeVatBtn.setOnClickListener(onShowCompositelayoutListener);
		mRegularVatBtn.setEnabled(false);
		ActionBar actionBar = getActivity().getActionBar();
		if (!actionBar.isShowing())
			actionBar.show();
		setHasOptionsMenu(true);
		actionBar.setCustomView(R.layout.actionbar_layout);
		((TextView) getActivity().findViewById(R.id.actionbar_header))
				.setText(getString(R.string.header_vat_summary));
		mGetVatReportBtn = (Button) getView().findViewById(
				R.id.get_vat_summary_btn);
		mGetVatReportBtn.setOnClickListener(onGetReportListener);
		mMonthSpinner = (Spinner) getView()
				.findViewById(R.id.vat_month_spinner);
		mMonthSpinner.setAdapter(new ArrayAdapter<Month>(getActivity(),
				android.R.layout.simple_list_item_1, Month.values()));
		mYearSpinner = (Spinner) getView().findViewById(R.id.vat_year_spinner);
	    int year = Calendar.getInstance().get(Calendar.YEAR);
	    String[] yearsList = getResources().getStringArray(R.array.years_string_array);
	    for (int i = 0; (i < yearsList.length); i++) {
	        if (year == Integer.parseInt(yearsList[i])) {
	        	mYearSpinner.setSelection(i);
	        	break;
	        }
	    }
		mVatCategory1TextView = (TextView) getView().findViewById(
				R.id.vatCat1TextView);
		mVatCategory2TextView = (TextView) getView().findViewById(
				R.id.vatCat2TextView);
		mVatCategory3TextView = (TextView) getView().findViewById(
				R.id.vatCat3TextView);
		mVatCategory4TextView = (TextView) getView().findViewById(
				R.id.vatCat4TextView);
		mSalesValue1TextView = ((TextView) getView().findViewById(
				R.id.salesValueCat1TextView));
		mSalesValue2TextView = ((TextView) getView().findViewById(
				R.id.salesValueCat2TextView));
		mSalesValue3TextView = ((TextView) getView().findViewById(
				R.id.salesValueCat3TextView));
		mSalesValue4TextView = ((TextView) getView().findViewById(
				R.id.salesValueCat4TextView));
		mOutputPayable1TextView = ((TextView) getView().findViewById(
				R.id.outputPayableCat1TextView));
		mOutputPayable2TextView = ((TextView) getView().findViewById(
				R.id.outputPayableCat2TextView));
		mOutputPayable3TextView = ((TextView) getView().findViewById(
				R.id.outputPayableCat3TextView));
		mOutputPayable4TextView = ((TextView) getView().findViewById(
				R.id.outputPayableCat4TextView));
		mNetPayableValue1TextView = ((TextView) getView().findViewById(
				R.id.netPayableCat1TextView));
		mNetPayableValue2TextView = ((TextView) getView().findViewById(
				R.id.netPayableCat2TextView));
		mNetPayableValue3TextView = ((TextView) getView().findViewById(
				R.id.netPayableCat3TextView));
		mNetPayableValue4TextView = ((TextView) getView().findViewById(
				R.id.netPayableCat4TextView));
		mPurchaseValue1TextView = (TextView) getView().findViewById(
				R.id.purchaseValueCat1TextView);
		mPurchaseValue2TextView = (TextView) getView().findViewById(
				R.id.purchaseValueCat2TextView);
		mPurchaseValue3TextView = (TextView) getView().findViewById(
				R.id.purchaseValueCat3TextView);
		mPurchaseValue4TextView = (TextView) getView().findViewById(
				R.id.purchaseValueCat4TextView);
		mInputCredit1TextView = (TextView) getView().findViewById(
				R.id.inputCreditCat1TextView);
		mInputCredit2TextView = (TextView) getView().findViewById(
				R.id.inputCreditCat2TextView);
		mInputCredit3TextView = (TextView) getView().findViewById(
				R.id.inputCreditCat3TextView);
		mInputCredit4TextView = (TextView) getView().findViewById(
				R.id.inputCreditCat4TextView);
		mCompositeValueTextView = ((TextView) getView().findViewById(
				R.id.composite_value_text_view));
		mCompositeSalesTextView = (TextView) getView().findViewById(
				R.id.sales_value_text_view);
		mNetPayableTotalValueTextView = ((TextView) getView().findViewById(
				R.id.netPayableTotalTextView));
		mOutputPayableTotalTextView = ((TextView) getView().findViewById(
				R.id.outputPayableTotalTextView));
		mSalesTotalValueTextView = (TextView) getView().findViewById(
				R.id.salesValueTotalTextView);
		mPurchaseTotalValueTextView = ((TextView) getView().findViewById(
				R.id.purchaseValueTotalTextView));
		mInputPayableTotalTextView = (TextView) getView().findViewById(
				R.id.inputCreditTotalTextView);
		mCompositeVatEditText = (EditText) getView().findViewById(
				R.id.compositeVAT_edit_text);
		String[] dateArray = SnapBillingUtils.getVatReportDates(
				((Month) mMonthSpinner.getSelectedItem()).ordinal(),
				mYearSpinner.getSelectedItem().toString());
		if (null != dateArray) {
			mStartDate = dateArray[0];
			mEndDate = dateArray[1];
		}
		try {
			QueryBuilder<State, Integer> stateQB = SnapCommonUtils
					.getDatabaseHelper(getActivity()).getStateDao()
					.queryBuilder();
			State state = stateQB
					.where()
					.eq("name",
							SnapSharedUtils.getStoreDetails(
									SnapCommonUtils
											.getSnapContext(getActivity()))
									.getState()).queryForFirst();
			mGetStateVatRatesTask = new GetStateVatRatesTask(getActivity(),
					VatSummaryFragment.this, GET_STATE_VAT_RATE_TASKCODE);
			mGetStateVatRatesTask.execute(String.valueOf(state.getStateID()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			onActionbarNavigationListener = (OnActionbarNavigationListener) getActivity();
		} catch (ClassCastException e) {
			throw new ClassCastException(getActivity().toString()
					+ getString(R.string.exc_implementnavigation));
		}
	}

	View.OnClickListener onSaveSummaryClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if (null == mVatCaptions) {
				mVatCaptions = new String[] {
						getResources().getString(
								R.string.sales_assessable_value),
						getResources().getString(R.string.output_vat_payable),
						getResources().getString(R.string.purchase_value),
						getResources().getString(R.string.input_vat_credit),
						getResources().getString(R.string.net_vat_payable) };
			}
			String xlsName;
			boolean xlsWritten;
			if (isCompositeReport) {
				xlsName = getString(R.string.xl_composite_report).toString();
				xlsWritten = WriteToXls
						.writeCompositeReportToXls(xlsName, getRange()
								+ mYearSpinner.getSelectedItem().toString(),
								mCompositeVatValue, mCompositeSalesValue,
								getActivity());
			} else {
				xlsName = getString(R.string.xl_vat_summary_report).toString();
				xlsWritten = WriteToXls.writeRegularReportToXls(xlsName,
						getRange() + mYearSpinner.getSelectedItem().toString(),
						mVatCaptions, mSalesVatValues, mPurchaseVatValues,
						mVatList, mTotalVatSales, mTotalOutputPayable, mTotalPurchaseVat, mTotalInputPayable,
						mCompositeSalesValue, getActivity());
			}
			if (xlsWritten) {
				CustomToast.showCustomToast(getActivity(),
						getString(R.string.xl_created), Toast.LENGTH_SHORT,
						CustomToast.INFORMATION);
			} else {
				CustomToast.showCustomToast(getActivity(),
						getString(R.string.xl_not_created),
						Toast.LENGTH_SHORT, CustomToast.ERROR);
			}
		}
	};

	View.OnClickListener onSaveDetailedReportClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			if (isCompositeReport) {
				CustomToast.showCustomToast(getActivity(),
						getString(R.string.msg_no_report), Toast.LENGTH_SHORT,
						CustomToast.INFORMATION);
			} else {
				mGetDetailedSalesReportTask = new GetDetailedSalesReportTask(
						getActivity(), VatSummaryFragment.this,
						GET_DETAILED_SALES_TASKCODE, mStartDate, mEndDate, mVatList);
				mGetDetailedSalesReportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				GetDetailedPurchaseReportTask mGetDetailedSalesReportTask = new GetDetailedPurchaseReportTask(
						getActivity(), VatSummaryFragment.this,
						GET_DETAILED_PURCHASE_TASKCODE, mStartDate, mEndDate);
				mGetDetailedSalesReportTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
	};

	View.OnClickListener onGetReportListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), arg0, TAG);
			SnapCommonUtils.hideSoftKeyboard(getActivity(), mCompositeLayout.getWindowToken());
			String[] dateArray = SnapBillingUtils.getVatReportDates(
					((Month) mMonthSpinner.getSelectedItem()).ordinal(),
					mYearSpinner.getSelectedItem().toString());
			String CompositeRate = mCompositeVatEditText.getText().toString();
			
			double mCompositeRate = 0;

       if (mCompositeLayout.isShown()) {
				
				if (CompositeRate.equals("")) {
					CustomToast.showCustomToast(getActivity(),
							getString(R.string.msg_enter_vat), Toast.LENGTH_SHORT,
							CustomToast.ERROR);
				} 
				
				else {
					if(CompositeRate!=null && (!CompositeRate.equalsIgnoreCase(""))){
						double mRate = Double.valueOf(CompositeRate);
						if(mRate>100){
							CustomToast.showCustomToast(getActivity(),
									getString(R.string.msg_compositevat), Toast.LENGTH_SHORT,
									CustomToast.ERROR);
							mCompositeValueTextView.setText("0");
							
						}
						else
						{
						if (null != dateArray) {
							mStartDate = dateArray[0];
							mEndDate = dateArray[1];
							resetViews();
							if(CompositeRate!=null && (!CompositeRate.equalsIgnoreCase(""))){
							mCompositeRate = Double.parseDouble(CompositeRate);
							mGetCompositeVatTask = new GetCompositeVatTask(
									getActivity(), VatSummaryFragment.this,
									GET_COMPOSITE_VAT_TASKCODE, mStartDate,
									mEndDate, mCompositeRate);
							mGetCompositeVatTask
									.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
						}
						}
					}
					
				}
			}
			else 			
				{
					if (mRegularLayout.isShown())
						
						if (null != dateArray) {
							mStartDate = dateArray[0];
							mEndDate = dateArray[1];
							resetViews();
							mCompositeVatEditText.setText("");
							mGetSalesVatTask = new GetSalesVatTask(
									getActivity(), VatSummaryFragment.this,
									GET_SALES_VAT_TASKCODE, mStartDate,
									mEndDate, mVatList);
							mGetSalesVatTask.execute();
							mGetPurchaseVatTask = new GetPurchaseVatTask(getActivity(), VatSummaryFragment.this,
									 GET_PURCHASE_VAT_TASKCODE,mStartDate, mEndDate);
							mGetPurchaseVatTask.execute();
						}
			}
	}
	};

	View.OnClickListener onShowRegularLayoutListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			SnapCommonUtils.hideSoftKeyboard(getActivity(), mCompositeLayout.getWindowToken());
			mRegularLayout.setVisibility(View.VISIBLE);
			mCompositeLayout.setVisibility(View.GONE);
			mRegularVatBtn.setEnabled(false);
			mCompositeVatBtn.setEnabled(true);
			isCompositeReport = false;
		}
	};

	View.OnClickListener onShowCompositelayoutListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GoogleAnalyticsTracker.sendGoogleAnalyticsOnView(getActivity(), v, TAG);
			SnapCommonUtils.hideSoftKeyboard(getActivity(), mCompositeLayout.getWindowToken());
			mCompositeVatEditText.setText("");
			mCompositeValueTextView.setText("");
			mRegularLayout.setVisibility(View.GONE);
			mCompositeLayout.setVisibility(View.VISIBLE);
			mRegularVatBtn.setEnabled(true);
			mCompositeVatBtn.setEnabled(false);
			isCompositeReport = true;
		}
	};

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		if (getView() == null || getActivity() == null)
			return;
		if (taskCode == GET_SALES_VAT_TASKCODE) {
			if (getActivity() != null) {
				outputVats = new float[mVatList.size()];
				mTotalOutputPayable = 0;
				mTotalVatSales = 0;
				mSalesVatValues = (HashMap<Float, String[]>) responseList;
				String[] vatSaleValues;
				TextView[] salesVat = {mSalesValue1TextView, mSalesValue2TextView, mSalesValue3TextView, mSalesValue4TextView};
				TextView[] outputPayableVat = {mOutputPayable1TextView, mOutputPayable2TextView, mOutputPayable3TextView, mOutputPayable4TextView};
				TextView[] netPayableText = {mNetPayableValue1TextView, mNetPayableValue2TextView, mNetPayableValue3TextView, mNetPayableValue4TextView};
				for (int i = 0; i < mVatList.size(); i++) {
					if (null != mSalesVatValues.get(mVatList.get(i).getVat())) {
						vatSaleValues = mSalesVatValues.get(mVatList.get(i).getVat());
						salesVat[i].setText(SnapToolkitTextFormatter.formatPriceText(
								Float.parseFloat(vatSaleValues[0]), getActivity()));
						outputPayableVat[i].setText(SnapToolkitTextFormatter.formatPriceText(
								Float.parseFloat(vatSaleValues[1]), getActivity()));
						outputVats[i] = Float.parseFloat(vatSaleValues[1]);
						netPayableText[i].setText(SnapToolkitTextFormatter.formatPriceText(
								Float.parseFloat(vatSaleValues[1]), getActivity()));
						mTotalOutputPayable += Float.parseFloat(vatSaleValues[1]);
						mTotalVatSales += Float.parseFloat(vatSaleValues[0]);
					}
				}
				mSalesTotalValueTextView.setText(SnapToolkitTextFormatter
						.formatPriceText(mTotalVatSales, getActivity()));
				mOutputPayableTotalTextView.setText(SnapToolkitTextFormatter
						.formatPriceText(mTotalOutputPayable, getActivity()));
				mNetPayableTotalValueTextView.setText(SnapToolkitTextFormatter
						.formatPriceText(mTotalOutputPayable, getActivity()));
			}
		} else if (taskCode == GET_PURCHASE_VAT_TASKCODE) {
			 if (getActivity()!=null) {
				 mTotalInputPayable = 0;
				 mTotalPurchaseVat = 0;
				 mPurchaseVatValues = (HashMap<Float, String[]>) responseList;
				 String[] purchaseValue;
				 TextView[] purchaseVat = {mPurchaseValue1TextView, mPurchaseValue2TextView, mPurchaseValue3TextView, mPurchaseValue4TextView};
				 TextView[] creditText = {mInputCredit1TextView, mInputCredit2TextView, mInputCredit3TextView, mInputCredit4TextView};
				 TextView[] netPayableText = {mNetPayableValue1TextView, mNetPayableValue2TextView, mNetPayableValue3TextView, mNetPayableValue4TextView};
				 if (mPurchaseVatValues != null && !mPurchaseVatValues.isEmpty()) {
					 for (int i = 0; i < mVatList.size(); i++) {
						 if (null != mPurchaseVatValues.get(mVatList.get(i).getVat())) {
							 purchaseValue = mPurchaseVatValues.get(mVatList.get(i).getVat());
							 purchaseVat[i].setText(SnapToolkitTextFormatter.formatPriceText(
									Float.parseFloat(purchaseValue[0]), getActivity()));
							 creditText[i].setText(SnapToolkitTextFormatter.formatPriceText(
									Float.parseFloat(purchaseValue[1]), getActivity()));
							 float value = 0;
							 if(outputVats != null)
								 value = outputVats[i];
							 value = value - Float.parseFloat(purchaseValue[1]);
							 netPayableText[i].setText(SnapToolkitTextFormatter.formatPriceText(value, getActivity()));
							 mTotalInputPayable += Float.parseFloat(purchaseValue[1]);
							 mTotalPurchaseVat += Float.parseFloat(purchaseValue[0]);
						 }
					 }
					 mPurchaseTotalValueTextView.setText(SnapToolkitTextFormatter
							.formatPriceText(mTotalPurchaseVat, getActivity()));
					 mInputPayableTotalTextView.setText(SnapToolkitTextFormatter
							.formatPriceText(mTotalInputPayable, getActivity()));
					 mNetPayableTotalValueTextView.setText(SnapToolkitTextFormatter
							.formatPriceText((mTotalOutputPayable - mTotalInputPayable), getActivity()));
				 } else {
					 CustomToast.showCustomToast(getActivity(), getActivity().getString(R.string.error_purchase_vat),
								Toast.LENGTH_SHORT, CustomToast.ERROR);
				 }
			 }
		    
		} else if (GET_COMPOSITE_VAT_TASKCODE == taskCode) {
			mCompositeSalesValue = ((Double[]) responseList)[0];
			mCompositeVatValue = ((Double[]) responseList)[1];
			mCompositeValueTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(mCompositeVatValue, getActivity()));
			mCompositeSalesTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(mCompositeSalesValue, getActivity()));

		} else if (GET_STATE_VAT_RATE_TASKCODE == taskCode) {
			mGetVatReportBtn.setEnabled(true);
			mVatList = (List<VAT>) responseList;
			try {
				mVatCategory1TextView.setText(mVatList.get(0).getVat() + "%");
				mVatCategory2TextView.setText(mVatList.get(1).getVat() + "%");
				mVatCategory3TextView.setText(mVatList.get(2).getVat() + "%");
				mVatCategory4TextView.setText(mVatList.get(3).getVat() + "%");
			} catch (IndexOutOfBoundsException e) {
				Log.d(TAG, "caught in exception");
			}
		} else if (GET_DETAILED_SALES_TASKCODE == taskCode) {
			boolean isXLSWritten = WriteToXls.writeRegularDetailedReportToXls(
					getString(R.string.xl_sales_detailed_report).toString(), getRange()
							+ mYearSpinner.getSelectedItem().toString(),
					(List<String[]>) responseList, mCompositeSalesValue,
					getActivity());
			if (isXLSWritten) {
				CustomToast.showCustomToast(getActivity(),
						getString(R.string.msg_detailed_report), Toast.LENGTH_SHORT,
						CustomToast.INFORMATION);
			} else {
				CustomToast.showCustomToast(getActivity(), getString(R.string.err_no_val),
						Toast.LENGTH_SHORT, CustomToast.ERROR);
			}
		} else if (GET_DETAILED_PURCHASE_TASKCODE == taskCode) {
			boolean isXLSWritten = WriteToXls.writeDetailedPurchaseReportToXls(
					getString(R.string.xl_purchase_detailed_report).toString(), getRange()
							+ mYearSpinner.getSelectedItem().toString(),
					(List<String[]>) responseList, getActivity());
			if (isXLSWritten)
				CustomToast.showCustomToast(getActivity(),
						getString(R.string.msg_purchase_detailed_report), Toast.LENGTH_SHORT,
						CustomToast.INFORMATION);
			else
				CustomToast.showCustomToast(getActivity(), getString(R.string.err_no_val),
						Toast.LENGTH_SHORT, CustomToast.ERROR);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.findItem(R.id.vat_summary_menuitem).setVisible(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation(getTag(),
				menuItem.getItemId());
		return true;
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
		if (getView() == null || getActivity() == null) {
			return;
		}
		if (GET_COMPOSITE_VAT_TASKCODE == taskCode) {
			mCompositeValueTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(0, getActivity()));
			mCompositeSalesTextView.setText(SnapToolkitTextFormatter
					.formatPriceText(0, getActivity()));
		} else if (GET_SALES_VAT_TASKCODE == taskCode) {
			outputVats = null;
		} else if (GET_PURCHASE_VAT_TASKCODE == taskCode) {
		}
		clearValues();
		CustomToast.showCustomToast(getActivity(), errorMessage,
				Toast.LENGTH_SHORT, CustomToast.ERROR);
	}
	

	private void clearValues() {
		mTotalVatSales = 0;
		mTotalOutputPayable = 0;
		mCompositeVatValue = 0;
		mCompositeSalesValue = 0;
		if (null != mSalesVatValues && mSalesVatValues.size() > 0) {
			mSalesVatValues.clear();
		}
		if (null != mPurchaseVatValues && mPurchaseVatValues.size() > 0) {
			mPurchaseVatValues.clear();
		}
	}

	private void resetViews() {
		mSalesValue1TextView.setText("");
		mSalesValue2TextView.setText("");
		mSalesValue3TextView.setText("");
		mSalesValue4TextView.setText("");
		mNetPayableValue1TextView.setText("");
		mNetPayableValue2TextView.setText("");
		mNetPayableValue3TextView.setText("");
		mNetPayableValue4TextView.setText("");
		mOutputPayable1TextView.setText("");
		mOutputPayable2TextView.setText("");
		mOutputPayable3TextView.setText("");
		mOutputPayable4TextView.setText("");
		mCompositeValueTextView.setText("");
		mInputCredit1TextView.setText("");
		mInputCredit2TextView.setText("");
		mInputCredit3TextView.setText("");
		mInputCredit4TextView.setText("");
		mPurchaseValue1TextView.setText("");
		mPurchaseValue2TextView.setText("");
		mPurchaseValue3TextView.setText("");
		mPurchaseValue4TextView.setText("");
		mNetPayableTotalValueTextView.setText("");
		mOutputPayableTotalTextView.setText("");
		mPurchaseTotalValueTextView.setText("");
		mInputPayableTotalTextView.setText("");
		mSalesTotalValueTextView.setText("");
		
	}
	
	private String getRange() {
		if (((Month) mMonthSpinner.getSelectedItem()).ordinal() == 12) {
			return "First_Quarter";
		} else if (((Month) mMonthSpinner.getSelectedItem()).ordinal() == 13) {
			return "Second_Quarter";
		} else if (((Month) mMonthSpinner.getSelectedItem()).ordinal() == 14) {
			return "Third_Quarter";
		} else if (((Month) mMonthSpinner.getSelectedItem()).ordinal() == 15) {
			return "Forth_Quarter";
		} else {
			return SnapCommonUtils.getMonthName(((Month) mMonthSpinner
					.getSelectedItem()).ordinal() + 1);
		}
	}
}
