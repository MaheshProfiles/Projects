package com.snapbizz.snapstock.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.CompanyFilterAdapter;
import com.snapbizz.snapstock.adapters.DistributorListAdapter;
import com.snapbizz.snapstock.asynctasks.GetDistributorListTask;
import com.snapbizz.snapstock.asynctasks.GetDistributorsCompanyTask;
import com.snapbizz.snapstock.asynctasks.SearchCompanyFilterTask;
import com.snapbizz.snapstock.asynctasks.SearchDistributorFilterTask;
import com.snapbizz.snapstock.utils.DistributorFilterType;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;

public class DistributorListFragment extends Fragment implements
OnQueryCompleteListener {

    private NewDistributorListener newDistributorListener;
    private final int FILTER_DISTRIBUTOR_TASKCODE = 0;
    private final int GET_ALL_DISTRIBUTORS_TASKCODE = 1;
    private final int GET_ALL_COMPANIES_TASKCODE = 2;
    private final int GET_COMPANY_DISTRIBUTORS_TASKCODE = 3;
    private final int SEARCH_COMPANY_TASKCODE = 4;
    private final int SEARCH_DISTRIBUTORS_TASKCODE = 5;
    private final int LOWEST_CHARACTER_VALUE = 35;

    private final String TAG = "DistributorList";
    private DistributorFilterType distributorFilterType = DistributorFilterType.DISTRIBUTOR_FILTER;
    private View lastSelectedFilterView;
    private DistributorListAdapter distributorAdapter;
    private CompanyFilterAdapter companyFilterAdapter;
    private TwoWayGridView distributorGridView;
    private TwoWayGridView companyGridView;
    private EditText productSearchEditText;
    private boolean isToOrder;
    private DistributorSelectListener distributorSelectListener;
    private OnActionbarNavigationListener onActionbarNavigationListener;
    private List<Distributor> distributorAdapterList;
    private List<Distributor> allDistributorList;
    private List<Company> allCompanyList;
    private List<Company> companyAdapterList;
    private List<View> viewIds;
    private Button clearSearchButton;
    private RelativeLayout productSearchLayout;
    private AsyncTask<String, Void, List<Distributor>> searchDistributorFilterTask;

    private boolean isNoFilterSelected = true;
    private int[] alphabetFilter = new int[100];

    public void setIsToOrder(Boolean isToOrder) {
        this.isToOrder = isToOrder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_distributorlist, null);

        view.findViewById(R.id.button_hash).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_a).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_b).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_c).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_d).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_e).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_f).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_g).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_h).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_i).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_j).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_k).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_l).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_m).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_n).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_o).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_p).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_q).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_r).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_s).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_t).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_u).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_v).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_w).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_x).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_y).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.button_z).setOnClickListener(
                onAlphabetFilterClickListener);
        view.findViewById(R.id.add_distributor_button).setOnClickListener(
                onNewDistributorClickListener);

        distributorGridView = (TwoWayGridView) view
                .findViewById(R.id.distributor_gridview);
        distributorGridView.setOnItemClickListener(onDistributorClickListener);
        companyGridView = (TwoWayGridView) view
                .findViewById(R.id.company_filter_gridview);
        companyGridView.setOnItemClickListener(onCompanyClickListener);
        return view;
    }

    View.OnClickListener onNewDistributorClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (viewIds != null) {
                for (int i = 0; i < viewIds.size(); i++) {
                    (viewIds.get(i)).setSelected(false);
                }
                viewIds.clear();
            }
            Arrays.fill(alphabetFilter, 0);
            newDistributorListener.onNewDistributorClick();
        }
    };

    View.OnClickListener onFilterTypeClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (viewIds != null) {
                for (int i = 0; i < viewIds.size(); i++) {
                    (viewIds.get(i)).setSelected(false);
                }
                viewIds.clear();
            }
            if (!v.isSelected()) {
                lastSelectedFilterView.setSelected(false);
                v.setSelected(true);
                lastSelectedFilterView = v;
                distributorFilterType = DistributorFilterType.getEnum(v
                        .getTag().toString());
                if (v.getId() == R.id.filter_distributor_button) {
                    Arrays.fill(alphabetFilter, 0);
                    getView().findViewById(R.id.filter_linearlayout).setVisibility(View.VISIBLE);
                    new GetDistributorListTask(getActivity(),
                            DistributorListFragment.this,
                            GET_ALL_DISTRIBUTORS_TASKCODE,
                            distributorFilterType).execute();
                } /*else if (v.getId() == R.id.filter_company_button) {
					Arrays.fill(alphabetFilter, 0);
					getView().findViewById(R.id.filter_linearlayout).setVisibility(View.VISIBLE);
					new GetCompaniesTask(getActivity(),
							DistributorListFragment.this,
							GET_ALL_COMPANIES_TASKCODE).execute();
				}*/
                if(productSearchEditText!=null){
                    productSearchEditText.removeTextChangedListener(productSearchTextWatcher);
                    productSearchEditText.setText("");
                    productSearchEditText.addTextChangedListener(productSearchTextWatcher);
                }
            }

        }
    };

    View.OnClickListener onAlphabetFilterClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            if (viewIds == null)
                viewIds = new ArrayList<View>();

            view.setSelected(!view.isSelected());

            char filterChar = ((Button) view).getText().charAt(0);
            if (distributorFilterType.getDistributorFilterTypeValue().equals(
                    getString(R.string.filter_distributor_column))) {
                if (view.isSelected()) {
                    viewIds.add(view);
                    if (distributorAdapterList == null)
                        return;
                    distributorAdapterList.clear();
                    alphabetFilter[filterChar - LOWEST_CHARACTER_VALUE] = 1;
                    for (int i = 0; i < allDistributorList.size(); i++) {
                        Distributor distributor = allDistributorList.get(i);
                        if ((distributor.getIsHeader() && alphabetFilter[distributor
                                                                         .getHeader().charAt(0) - LOWEST_CHARACTER_VALUE] != 0)
                                                                         || (!distributor.getIsHeader() && alphabetFilter[distributor
                                                                                                                          .getAgencyName().charAt(0) - LOWEST_CHARACTER_VALUE] != 0) || (!distributor.getIsHeader() && !Character.isLetter(distributor.getAgencyName().charAt(0)) && alphabetFilter[0] != 0)) {
                            distributorAdapterList.add(distributor);
                        }
                    }
                    distributorAdapter.setTotalgrid(SnapInventoryUtils.getDistributorGrid(distributorAdapterList));
                    distributorAdapter.notifyDataSetChanged();
                } else {
                    viewIds.remove(view);
                    if (distributorAdapterList == null)
                        return;
                    alphabetFilter[filterChar - LOWEST_CHARACTER_VALUE] = 0;
                    isNoFilterSelected = true;
                    for (int i = 0; i < alphabetFilter.length; i++) {
                        if (alphabetFilter[i] == 1) {
                            isNoFilterSelected = false;
                            break;
                        }
                    }
                    distributorAdapterList.clear();
                    if (isNoFilterSelected) {
                        distributorAdapterList.addAll(allDistributorList);
                    } else {
                        for (int i = 0; i < allDistributorList.size(); i++) {
                            Distributor distributor = allDistributorList.get(i);
                            if ((distributor.getIsHeader() && alphabetFilter[distributor
                                                                             .getHeader().charAt(0) - LOWEST_CHARACTER_VALUE] != 0)
                                                                             || (!distributor.getIsHeader() && alphabetFilter[distributor
                                                                                                                              .getAgencyName().charAt(0) - LOWEST_CHARACTER_VALUE] != 0)) {
                                distributorAdapterList.add(distributor);
                            }
                        }
                    }
                    distributorAdapter.setTotalgrid(SnapInventoryUtils.getDistributorGrid(distributorAdapterList));
                    distributorAdapter.notifyDataSetChanged();
                }
            } else {
                if (view.isSelected()) {
                    viewIds.add(view);
                    if (companyAdapterList == null)
                        return;
                    companyAdapterList.clear();
                    alphabetFilter[filterChar - LOWEST_CHARACTER_VALUE] = 1;
                    for (int i = 0; i < allCompanyList.size(); i++) {
                        Company company = allCompanyList.get(i);
                        if ((company.isHeader() && alphabetFilter[company
                                                                  .getHeaderValue().charAt(0) - LOWEST_CHARACTER_VALUE] != 0)
                                                                  || (!company.isHeader() && alphabetFilter[company
                                                                                                            .getCompanyName().charAt(0) - LOWEST_CHARACTER_VALUE] != 0)) {
                            companyAdapterList.add(company);
                        }
                    }
                    companyFilterAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(companyAdapterList, 3));
                    companyFilterAdapter.notifyDataSetChanged();
                } else {
                    viewIds.remove(view);
                    if (companyAdapterList == null)
                        return;
                    alphabetFilter[filterChar - LOWEST_CHARACTER_VALUE] = 0;
                    isNoFilterSelected = true;
                    for (int i = 0; i < alphabetFilter.length; i++) {
                        if (alphabetFilter[i] == 1) {
                            isNoFilterSelected = false;
                            break;
                        }
                    }
                    companyAdapterList.clear();
                    if (isNoFilterSelected) {
                        companyAdapterList.addAll(allCompanyList);
                    } else {
                        for (int i = 0; i < allCompanyList.size(); i++) {
                            Company company = allCompanyList.get(i);
                            if ((company.isHeader() && alphabetFilter[company
                                                                      .getHeaderValue().charAt(0) - LOWEST_CHARACTER_VALUE] != 0)
                                                                      || (!company.isHeader() && alphabetFilter[company
                                                                                                                .getCompanyName().charAt(0) - LOWEST_CHARACTER_VALUE] != 0)) {
                                companyAdapterList.add(company);
                            }
                        }
                    }
                    companyFilterAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(companyAdapterList, 3));
                    companyFilterAdapter.notifyDataSetChanged();
                }
            }

        }
    };

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);

        try {
            onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("activity " + activity.toString()
                    + " must implement OnActionbarNavigationListener =");
        }

        try {
            newDistributorListener = (NewDistributorListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("activity " + activity.toString()
                    + " must implement NewDistributorListener =");
        }

        try {
            distributorSelectListener = (DistributorSelectListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("activity " + activity.toString()
                    + " must implement onDistributorClicked =");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        if (getActivity().getActionBar().getCustomView().getId() != R.id.actionbar_distributor_layout) {
            getActivity().getActionBar().setCustomView(
                    R.layout.actionbar_distributorlist_layout);
        }
        productSearchEditText = (EditText) getActivity().getActionBar()
                .getCustomView().findViewById(R.id.search_edittext);
        productSearchEditText.addTextChangedListener(productSearchTextWatcher);
        productSearchLayout= (RelativeLayout) getActivity().getActionBar()
                .getCustomView().findViewById(R.id.search_layout);
        clearSearchButton = (Button) getActivity().getActionBar()
                .getCustomView().findViewById(R.id.clear_search_text_button);
        clearSearchButton.setOnClickListener(clearSearchClickListener);
        /*	View companyFilterView = getActivity().getActionBar().getCustomView()
				.findViewById(R.id.filter_company_button);
		companyFilterView.setOnClickListener(onFilterTypeClickListener);
		View distributorFilterView = getActivity().getActionBar()
				.getCustomView().findViewById(R.id.filter_distributor_button);
		distributorFilterView.setOnClickListener(onFilterTypeClickListener);

		if (distributorFilterType.getDistributorFilterTypeValue().equals(
				"agency_name")
				&& distributorAdapter != null) {
			distributorFilterView.setSelected(true);
			lastSelectedFilterView = distributorFilterView;*/
        if(distributorAdapterList!=null){
            distributorGridView.setVisibility(View.VISIBLE);
            companyGridView.setVisibility(View.GONE);
            distributorAdapterList.clear();
            distributorAdapterList.addAll(allDistributorList);
            distributorAdapter.setTotalgrid(SnapInventoryUtils.getDistributorGrid(distributorAdapterList));
            distributorGridView.setAdapter(distributorAdapter);
        }else{
            distributorFilterType = DistributorFilterType.DISTRIBUTOR_FILTER;
            new GetDistributorListTask(getActivity(),
                    DistributorListFragment.this,
                    GET_ALL_DISTRIBUTORS_TASKCODE, distributorFilterType)
            .execute();
        }
        /*}else if (distributorFilterType.getDistributorFilterTypeValue()
				.equals("company_name") && companyFilterAdapter != null) {
			companyFilterView.setSelected(true);
			lastSelectedFilterView = companyFilterView;
			companyGridView.setVisibility(View.VISIBLE);
			distributorGridView.setVisibility(View.GONE);
			companyAdapterList.clear();
			companyAdapterList.addAll(allCompanyList);
			companyFilterAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(companyAdapterList, 3));
			companyGridView.setAdapter(companyFilterAdapter);
		} else {
			distributorFilterView.setSelected(true);
			lastSelectedFilterView = distributorFilterView;
			if (distributorAdapterList != null) {
				distributorAdapterList.clear();
			}*/

    }


    OnClickListener clearSearchClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            productSearchEditText.setText("");
        }
    };

    TextWatcher productSearchTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub	
            companySearchKeyStrokeTimer.cancel();
            if (s.length() == 0) {
                if(viewIds!=null){
                    for (int i = 0; i < viewIds.size(); i++) {
                        (viewIds.get(i)).setSelected(false);
                    }
                    viewIds.clear();
                }
                Arrays.fill(alphabetFilter, 0);
                getView().findViewById(R.id.filter_linearlayout).setVisibility(View.VISIBLE);
                if(distributorFilterType.getDistributorFilterTypeValue().equals(getString(R.string.filter_distributor_column))){
                    if(distributorAdapterList!=null){
                        distributorAdapterList.clear();
                        distributorAdapterList.addAll(allDistributorList);	
                        distributorAdapter.setTotalgrid(SnapInventoryUtils.getDistributorGrid(allDistributorList));
                        distributorGridView.setAdapter(distributorAdapter);
                        distributorAdapter.notifyDataSetChanged();
                    }
                }else{
                    companyAdapterList.clear();
                    companyAdapterList.addAll(allCompanyList);
                    companyFilterAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(companyAdapterList, 3));
                    companyFilterAdapter.notifyDataSetChanged();
                }
            } else {
                companySearchKeyStrokeTimer.start();
            }
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                int arg3) {
            // TODO Auto-generated method stub

        }
    };

    CountDownTimer companySearchKeyStrokeTimer = new CountDownTimer(
            SnapInventoryConstants.KEY_STROKE_TIMEOUT,
            SnapInventoryConstants.KEY_STROKE_TIMEOUT) {


        @Override
        public void onTick(long arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            String keyword = productSearchEditText.getText().toString();

            if (distributorFilterType.getDistributorFilterTypeValue().equals(getString(R.string.filter_company_column))){
                //	Arrays.fill(alphabetFilter, 0);
                /*getView().findViewById(R.id.filter_linearlayout).setVisibility(View.INVISIBLE);
				if(viewIds!=null){
					for (int i = 0; i < viewIds.size(); i++) {
						(viewIds.get(i)).setSelected(false);
					}
					viewIds.clear();
					companyFilterAdapter.clear();
					companyAdapterList.addAll(allCompanyList);
					companyFilterAdapter.notifyDataSetChanged();
				}*/
                new SearchCompanyFilterTask(getActivity(),
                        DistributorListFragment.this, SEARCH_COMPANY_TASKCODE)
                .execute(keyword);
            }
            else if (distributorFilterType.getDistributorFilterTypeValue().equals(getString(R.string.filter_distributor_column))) {
                //	Arrays.fill(alphabetFilter, 0);
                /*getView().findViewById(R.id.filter_linearlayout).setVisibility(View.INVISIBLE);
				if(viewIds!=null){
					for (int i = 0; i < viewIds.size(); i++) {
						(viewIds.get(i)).setSelected(false);
					}
					viewIds.clear();
					if(distributorAdapter!=null){
						distributorAdapter.clear();
						distributorAdapterList.addAll(allDistributorList);
						distributorAdapter.notifyDataSetChanged();
					}
				}*/
                if (searchDistributorFilterTask != null && !searchDistributorFilterTask.isCancelled())
                    searchDistributorFilterTask.cancel(true);
                searchDistributorFilterTask = new SearchDistributorFilterTask(getActivity(),
                        DistributorListFragment.this,
                        SEARCH_DISTRIBUTORS_TASKCODE).execute(keyword);
                ;
            }
        }
    };

    public interface NewDistributorListener {
        public void onNewDistributorClick();
    }

    @Override
    public void onTaskSuccess(Object responseList, int taskCode) {
        // TODO Auto-generated method stub
        if(getActivity() == null)
            return;
        if (taskCode == GET_ALL_DISTRIBUTORS_TASKCODE) {
            if (companyGridView != null) {
                companyGridView.setVisibility(View.GONE);
            }
            distributorGridView.setVisibility(View.VISIBLE);

            if(allDistributorList==null){
                allDistributorList = new ArrayList<Distributor>();
            }else
                allDistributorList.clear();
            List<Distributor> distributorResponseList = (List<Distributor>) responseList;
            Log.d("TAG","distributorResponseList----->"+distributorResponseList.size());
            char firstCharacter = 0;
            for (int i = 0; i < distributorResponseList.size(); i++) {
            	
            	if (distributorResponseList.get(i).getAgencyName()!=null&&!distributorResponseList.get(i).getAgencyName().trim().equals("")){
            		if (distributorResponseList.get(i).getAgencyName().charAt(0) != firstCharacter) {
            			if(firstCharacter != '#' || Character.isLetter(distributorResponseList.get(i).getAgencyName().charAt(0))) {
            				if(Character.isLetter(distributorResponseList.get(i).getAgencyName().charAt(0))) {
            					firstCharacter = distributorResponseList.get(i).getAgencyName().charAt(0);
            				} else {
            					firstCharacter = '#';
            				}
                        allDistributorList.add(new Distributor(firstCharacter+""));
                    }
                }
            	}
                allDistributorList.add(distributorResponseList.get(i));
            }

            if (distributorAdapterList == null)
                distributorAdapterList = new ArrayList<Distributor>();
            else
                distributorAdapterList.clear();

            distributorAdapterList.addAll(allDistributorList);
            if (distributorAdapter == null) {
                distributorAdapter = new DistributorListAdapter(getActivity(),
                        distributorAdapterList,true,(DistributorSelectListener) getActivity());
            }

            //allDistributorList.addAll(distributorAdapterList);
            distributorAdapter.setTotalgrid(SnapInventoryUtils.getDistributorGrid(distributorAdapterList));
            distributorGridView.setAdapter(distributorAdapter);
            distributorAdapter.notifyDataSetChanged();
        } else if (taskCode == GET_ALL_COMPANIES_TASKCODE) {

            companyGridView.setVisibility(View.VISIBLE);
            distributorGridView.setVisibility(View.GONE);
            if(allCompanyList==null){
                allCompanyList = new ArrayList<Company>();
            }else
                allCompanyList.clear();

            List<Company> companyResponseList = (List<Company>) responseList;
            char firstCharacter = 0;
            for (int i = 0; i < companyResponseList.size(); i++) {
                Log.d("", "company "
                        + companyResponseList.get(i).getCompanyId());
                if (companyResponseList.get(i).getCompanyName().charAt(0) != firstCharacter) {
                    allCompanyList.add(new Company(companyResponseList.get(i)
                            .getCompanyName().charAt(0)
                            + ""));
                    firstCharacter = companyResponseList.get(i)
                            .getCompanyName().charAt(0);
                }
                allCompanyList.add(companyResponseList.get(i));
            }

            if (companyAdapterList == null)
                companyAdapterList = new ArrayList<Company>();
            else
                companyAdapterList.clear();

            companyAdapterList.addAll(allCompanyList);
            if (companyFilterAdapter == null) {
                companyFilterAdapter = new CompanyFilterAdapter(getActivity(),
                        companyAdapterList);
            }

            companyFilterAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(companyAdapterList, 3));
            companyGridView.setAdapter(companyFilterAdapter);
            companyFilterAdapter.notifyDataSetChanged();
        } else if (taskCode == GET_COMPANY_DISTRIBUTORS_TASKCODE) {
            final ArrayList<Distributor> distributorList = (ArrayList<Distributor>) responseList;
            ArrayList<String> arraylist = new ArrayList<String>();
            for (int i = 0; i < distributorList.size(); i++) {
                arraylist.add(distributorList.get(i).getAgencyName());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
            .setTitle("Distributor List").setAdapter(
                    new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1,
                            arraylist),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                            distributorSelectListener
                            .onDistributorClicked(
                                    distributorList.get(which),
                                    isToOrder);
                        }
                    });
            AlertDialog alertDialog = (AlertDialog) builder.create();
            alertDialog.show();
        } else if (taskCode == SEARCH_COMPANY_TASKCODE) {
            if (companyAdapterList == null)
                companyAdapterList = new ArrayList<Company>();
            else
                companyAdapterList.clear();

            /*if (companyList == null)
				companyList = new ArrayList<Company>();
			else
				companyList.clear();*/

            List<Company> companyResponseList = (List<Company>) responseList;
            char firstCharacter = 0;
            for (int i = 0; i < companyResponseList.size(); i++) {
                Log.d("", "company "
                        + companyResponseList.get(i).getCompanyId());
                if (companyResponseList.get(i).getCompanyName().charAt(0) != firstCharacter) {
                    companyAdapterList.add(new Company(companyResponseList.get(i)
                            .getCompanyName().charAt(0)
                            + ""));
                    firstCharacter = companyResponseList.get(i)
                            .getCompanyName().charAt(0);
                }
                companyAdapterList.add(companyResponseList.get(i));
            }

            if (companyFilterAdapter == null) {
                companyFilterAdapter = new CompanyFilterAdapter(getActivity(),
                        companyAdapterList);
            }
            companyFilterAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(companyAdapterList, 3));
            companyGridView.setAdapter(companyFilterAdapter);
            companyFilterAdapter.notifyDataSetChanged();
        } else if(taskCode==SEARCH_DISTRIBUTORS_TASKCODE){
            if (viewIds != null) {
                for (int i = 0; i < viewIds.size(); i++) {
                    (viewIds.get(i)).setSelected(false);
                }
                viewIds.clear();
            }
            Arrays.fill(alphabetFilter, 0);

            if (distributorAdapterList == null)
                distributorAdapterList = new ArrayList<Distributor>();
            else
                distributorAdapterList.clear();
            getView().findViewById(R.id.filter_linearlayout).setVisibility(View.INVISIBLE);

            List<Distributor> distributorResponseList = (List<Distributor>) responseList;
            char firstCharacter = 0;
            for (int i = 0; i < distributorResponseList.size(); i++) {
                if (distributorResponseList.get(i).getAgencyName().charAt(0) != firstCharacter) {
                    if(firstCharacter != '#' || Character.isLetter(distributorResponseList
                            .get(i).getAgencyName().charAt(0))) {
                        if(Character.isLetter(distributorResponseList
                                .get(i).getAgencyName().charAt(0))) {
                            firstCharacter = distributorResponseList.get(i)
                                    .getAgencyName().charAt(0);
                        } else {
                            firstCharacter = '#';
                        }
                        distributorAdapterList.add(new Distributor(firstCharacter+""));
                    }
                }
                distributorAdapterList.add(distributorResponseList.get(i));
            }
            if (distributorAdapter == null) {
                distributorAdapter = new DistributorListAdapter(getActivity(),
                        distributorAdapterList, true, distributorSelectListener);
            }
            distributorAdapter.setTotalgrid(SnapInventoryUtils.getDistributorGrid(distributorAdapterList));
            distributorGridView.setAdapter(distributorAdapter);
            distributorAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onTaskError(String errorMessage, int taskCode) {
        // TODO Auto-generated method stub
        if (taskCode == FILTER_DISTRIBUTOR_TASKCODE) {
            Log.d(TAG, "empty distributor list");
        } else if (taskCode == GET_ALL_DISTRIBUTORS_TASKCODE) {
            if (companyGridView.getVisibility() == View.VISIBLE) {
                companyGridView.setVisibility(View.GONE);
            }
        } else if (taskCode == GET_COMPANY_DISTRIBUTORS_TASKCODE) {
            CustomToast.showCustomToast(getActivity(), "Selected Company does not have any distributors", Toast.LENGTH_SHORT, CustomToast.ERROR);
        } else if (taskCode == SEARCH_COMPANY_TASKCODE) {
            if(companyFilterAdapter!=null){
                companyFilterAdapter.clear();
                companyFilterAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(companyAdapterList, 3));
                companyFilterAdapter.notifyDataSetChanged();
            }
        }
        else if (taskCode == SEARCH_DISTRIBUTORS_TASKCODE) {
            if (viewIds != null) {
                for (int i = 0; i < viewIds.size(); i++) {
                    (viewIds.get(i)).setSelected(false);
                }
                viewIds.clear();
            }
            Arrays.fill(alphabetFilter, 0);
            if(getView()==null)
                return;
            getView().findViewById(R.id.filter_linearlayout).setVisibility(View.INVISIBLE);
            if(distributorAdapter!=null){
                distributorAdapter.clear();
                distributorAdapter.setTotalgrid(SnapInventoryUtils.getDistributorGrid(distributorAdapterList));
                distributorAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.search_meuitem) {
            menuItem.setVisible(false);
            Arrays.fill(alphabetFilter, 0);
            productSearchLayout.setVisibility(View.VISIBLE);
            //	productSearchEditText.setVisibility(View.VISIBLE);
            productSearchEditText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(productSearchEditText,
                    InputMethodManager.SHOW_FORCED);
        } else {
            onActionbarNavigationListener.onActionbarNavigation("",
                    menuItem.getItemId());
        }
        return true;
    }

    TwoWayAdapterView.OnItemClickListener onDistributorClickListener = new TwoWayAdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(TwoWayAdapterView<?> parent, View view,
                int position, long id) {
            // TODO Auto-generated method stub
            if (distributorAdapter.getItem(position) != null
                    && !distributorAdapter.getItem(position).getIsHeader()){
                if (viewIds != null) {
                    for (int i = 0; i < viewIds.size(); i++) {
                        (viewIds.get(i)).setSelected(false);
                    }
                    viewIds.clear();
                }
                Arrays.fill(alphabetFilter, 0);
                distributorSelectListener.onDistributorClicked(
                        distributorAdapter.getItem(position), isToOrder);
            }
        }

    };

    TwoWayAdapterView.OnItemClickListener onCompanyClickListener = new TwoWayAdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(TwoWayAdapterView<?> parent, View view,
                int position, long id) {
            // TODO Auto-generated method stub
            if (companyFilterAdapter.getItem(position) != null
                    && !companyFilterAdapter.getItem(position).isHeader()) {
                new GetDistributorsCompanyTask(getActivity(),
                        DistributorListFragment.this,
                        GET_COMPANY_DISTRIBUTORS_TASKCODE)
                .execute(companyFilterAdapter.getItem(position)
                        .getCompanyId());
            }
        }
    };

    public void addDistributortoList(Distributor distributor) {

        boolean headerPresent = false;
        char firstChar = distributor.getAgencyName().charAt(0);
        if(!Character.isLetter(firstChar))
            firstChar = '#';
        if(allDistributorList==null){
            allDistributorList=new ArrayList<Distributor>();
        }
        if (distributorAdapterList == null) {
            distributorAdapterList = new ArrayList<Distributor>();
        } else {
            distributorAdapterList.clear();
            distributorAdapterList.addAll(allDistributorList);
        }
        for (int i = 0; i < distributorAdapterList.size(); i++) {
            if (distributorAdapterList.get(i) == null) {
                continue;
            } else if (distributorAdapterList.get(i).getIsHeader()
                    && distributorAdapterList.get(i).getHeader()
                    .equals(firstChar + "")) {
                distributorAdapterList.add(i + 1, distributor);
                headerPresent = true;
                break;
            }
        }
        if (!headerPresent) {
            distributorAdapterList.add(new Distributor(firstChar + ""));
            distributorAdapterList.add(distributor);
        }
        /*	if (distributorList == null) {
			distributorList = new ArrayList<Distributor>();
		} else {
			distributorList.clear();
		}

		distributorList.addAll(distributorAdapterList);*/
        allDistributorList.clear();
        allDistributorList.addAll(distributorAdapterList);
        if (distributorAdapter == null)
            distributorAdapter = new DistributorListAdapter(getActivity(),
                    distributorAdapterList,true,(DistributorSelectListener) getActivity());
        distributorAdapter.setTotalgrid(SnapInventoryUtils.getDistributorGrid(distributorAdapterList));
        distributorGridView.setAdapter(distributorAdapter);
        distributorAdapter.notifyDataSetChanged();
    }

    public void refreshDistributorList() {
        distributorAdapter.notifyDataSetChanged();
    }

    public void deleteDistributorList(Distributor deletedDistributor) {
        for(int i = 0; i < allDistributorList.size(); i++) {
            if(allDistributorList.get(i).getDistributorId() == deletedDistributor.getDistributorId()) {
                if(allDistributorList.get(i - 1).getIsHeader() && (allDistributorList.size() == i+1 || allDistributorList.get(i+1).getIsHeader())) {
                    allDistributorList.remove(i - 1);
                    allDistributorList.remove(i - 1);
                } else {
                    allDistributorList.remove(i);
                }
                break;
            }
        }
        for(int i = 0; i < distributorAdapterList.size(); i++) {
            if(distributorAdapterList.get(i).getDistributorId() == deletedDistributor.getDistributorId()) {
                if(distributorAdapterList.get(i - 1).getIsHeader() && (distributorAdapterList.size() == i+1 || distributorAdapterList.get(i+1).getIsHeader())) {
                    distributorAdapterList.remove(i - 1);
                    distributorAdapterList.remove(i - 1);
                } else
                    distributorAdapterList.remove(i);
                break;
            }
        }
        distributorAdapter.setTotalgrid(SnapInventoryUtils.getDistributorGrid(distributorAdapterList));
        distributorAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (searchDistributorFilterTask != null && !searchDistributorFilterTask.isCancelled())
            searchDistributorFilterTask.cancel(true);
        if(companyFilterAdapter!=null){
            /*	companyList.clear();
			if(allCompanyList!=null)
			companyList.addAll(allCompanyList);
			companyFilterAdapter.notifyDataSetChanged();*/
        }
        if (viewIds != null) {
            for (int i = 0; i < viewIds.size(); i++) {
                (viewIds.get(i)).setSelected(false);
            }
            viewIds.clear();
        }
        Arrays.fill(alphabetFilter, 0);
        if(distributorAdapter!=null){
            
            //distributorList.clear();
            /*if(allDistributorList!=null)
			distributorList.addAll(allDistributorList);
			distributorAdapter.notifyDataSetChanged();*/

        }
    }
    public interface DistributorSelectListener {
        public void onDistributorClicked(Distributor distributor,
                boolean isToOrder);
        public void onDistributorEditClicked(Distributor distributor);
    }
}
