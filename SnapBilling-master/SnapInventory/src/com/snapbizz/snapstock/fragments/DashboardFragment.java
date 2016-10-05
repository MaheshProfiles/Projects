package com.snapbizz.snapstock.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.tabs.DashboardAlertsTab;
import com.snapbizz.snapstock.tabs.DashboardChartsTab;
import com.snapbizz.snapstock.tabs.DashboardMoneyTab;
import com.snapbizz.snapstock.tabs.DashboardStockReportTab;
import com.snapbizz.snapstock.tabs.DashboardSummaryTab;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;

public class DashboardFragment extends Fragment {
	private static final String TAG = DashboardFragment.class.getSimpleName();

	private TabHost mTabHost;
	private OnActionbarNavigationListener onActionbarNavigationListener;
	private DashboardSummaryTab dashboardSummeryTab;
	private DashboardChartsTab dashboardCharts;
	private DashboardAlertsTab dashboardAlertsTab;
	private DashboardStockReportTab dashboardStockReportTab;
	private DashboardMoneyTab dashboardMoneyTab;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dashboard, null);
		mTabHost = (TabHost) view.findViewById(android.R.id.tabhost);
		if(dashboardCharts == null)
			dashboardCharts = new DashboardChartsTab(getActivity());
		if(dashboardSummeryTab == null)
			dashboardSummeryTab = new DashboardSummaryTab(getActivity());
		if(dashboardAlertsTab == null)
			dashboardAlertsTab = new DashboardAlertsTab(getActivity());
		if(dashboardStockReportTab == null)
			dashboardStockReportTab = new DashboardStockReportTab(getActivity());
		if(dashboardMoneyTab == null)
			dashboardMoneyTab = new DashboardMoneyTab(getActivity());
		dashboardSummeryTab.setView(view.findViewById(R.id.tabs_dashboard_summary));
		dashboardCharts.setView(view.findViewById(R.id.tabs_dashboard_charts));
		dashboardAlertsTab.setView(view.findViewById(R.id.tabs_dashboard_alerts));
		dashboardStockReportTab.setView(view.findViewById(R.id.tabs_dashboard_stockreport));
		dashboardMoneyTab.setView(view.findViewById(R.id.tabs_dashboard_money));
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
		((TextView)getActivity().findViewById(R.id.actionbar_header)).setText(getString(R.string.dashboard));
		dashboardSummeryTab.loadSummaryTab();
		initTabHost();
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
		menu.findItem(R.id.dashboard_menuitem).setVisible(false);
		menu.findItem(R.id.search_meuitem).setVisible(false);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation(getTag(),
				menuItem.getItemId());
		return true;
	}

	private void initTabHost() {
		Resources res = getResources();
		mTabHost.setup();
		TabWidget tw = mTabHost.getTabWidget();
		tw.setOrientation(LinearLayout.VERTICAL);

		TabSpec summary_spec = mTabHost.newTabSpec(res
				.getString(R.string.summary_tag));
		summary_spec.setContent(R.id.tabs_dashboard_summary);
		summary_spec.setIndicator(createIndicatorView(
				res.getString(R.string.summary),
				res.getDrawable(R.drawable.summary_tabselector)));

		TabSpec charts_spec = mTabHost.newTabSpec(res
				.getString(R.string.charts_tag));
		charts_spec.setContent(R.id.tabs_dashboard_charts);
		charts_spec.setIndicator(createIndicatorView(
				res.getString(R.string.charts),
				res.getDrawable(R.drawable.charts_tabselector)));

		TabSpec alerts_spec = mTabHost.newTabSpec(res
				.getString(R.string.alerts_tag));
		alerts_spec.setContent(R.id.tabs_dashboard_alerts);
		alerts_spec.setIndicator(createIndicatorView(
				res.getString(R.string.alerts),
				res.getDrawable(R.drawable.alerts_tabselector)));

		TabSpec stockreport_spec = mTabHost.newTabSpec(res
				.getString(R.string.stock_report_tag));
		stockreport_spec.setContent(R.id.tabs_dashboard_stockreport);
		stockreport_spec.setIndicator(createIndicatorView(
				res.getString(R.string.stock_report),
				res.getDrawable(R.drawable.stockreport_tabselector)));

		TabSpec money_spec = mTabHost.newTabSpec(res
				.getString(R.string.money_tag));
		money_spec.setContent(R.id.tabs_dashboard_money);
		money_spec.setIndicator(createIndicatorView(
				res.getString(R.string.money),
				res.getDrawable(R.drawable.money_tabselector)));
		
		mTabHost.addTab(summary_spec);
		mTabHost.addTab(charts_spec);
		mTabHost.addTab(alerts_spec);
		mTabHost.addTab(stockreport_spec);
		mTabHost.addTab(money_spec);
		tw.getChildTabViewAt(2).setEnabled(false);

		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {	
			@Override
			public void onTabChanged(String tabId) {
				if(tabId.equals(getString(R.string.alerts_tag))){
					dashboardAlertsTab.loadAlertsTab();
				} else if(tabId.equals(getString(R.string.charts_tag))) {
					dashboardCharts.loadChartsTab();
				} else if(tabId.equals(getString(R.string.stock_report_tag))) {
					dashboardStockReportTab.loadStockReportTab();
				} else if(tabId.equals(getString(R.string.money_tag))) {
					dashboardMoneyTab.loadMoneyTab();
				}
			}
		});
	}
	
	private View createIndicatorView(CharSequence label, Drawable background) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View tabIndicator = inflater.inflate(R.layout.tab_indicator,
				mTabHost.getTabWidget(), false);
		final TextView tv = (TextView) tabIndicator.findViewById(R.id.tab_title);
		tv.setText(label);
		tv.setClickable(false);// this is required
		tv.setBackground(background);

		return tabIndicator;
	}

}
