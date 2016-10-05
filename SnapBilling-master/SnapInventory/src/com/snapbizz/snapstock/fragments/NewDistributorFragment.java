package com.snapbizz.snapstock.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.BrandSelectionAdapter;
import com.snapbizz.snapstock.adapters.CompanyAdapter;
import com.snapbizz.snapstock.adapters.CompanySpinnerAdapter;
import com.snapbizz.snapstock.adapters.DistributorAdapter;
import com.snapbizz.snapstock.adapters.BrandSelectionAdapter.OnBrandEditListener;
import com.snapbizz.snapstock.adapters.CompanyAdapter.OnCompanyEditListener;
import com.snapbizz.snapstock.asynctasks.CreateDistributorTask;
import com.snapbizz.snapstock.asynctasks.CreateOrUpdateBrandTask;
import com.snapbizz.snapstock.asynctasks.CreateOrUpdateCompanyTask;
import com.snapbizz.snapstock.asynctasks.DeleteBrandTask;
import com.snapbizz.snapstock.asynctasks.DeleteCompanyTask;
import com.snapbizz.snapstock.asynctasks.DeleteDistributorTask;
import com.snapbizz.snapstock.asynctasks.GetCompaniesBrandTask;
import com.snapbizz.snapstock.asynctasks.GetCompaniesTask;
import com.snapbizz.snapstock.utils.SnapInventoryConstants;
import com.snapbizz.snapstock.utils.SnapInventoryUtils;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Company;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.Request;
import com.snapbizz.snaptoolkit.domains.ResponseContainer;
import com.snapbizz.snaptoolkit.domains.Store;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.interfaces.OnServiceCompleteListener;
import com.snapbizz.snaptoolkit.utils.CustomToast;
import com.snapbizz.snaptoolkit.utils.RequestCodes;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;
import com.snapbizz.snaptoolkit.utils.ValidationUtils;

public class NewDistributorFragment extends Fragment implements
OnQueryCompleteListener, OnServiceCompleteListener,
OnCompanyEditListener, OnBrandEditListener {

    private final int CREATE_DISTRIBUTOR_TASKCODE = 0;
    private final int GET_COMPANIES_TASKCODE = 1;
    private final int GET_COMPANY_BRANDS_TASKCODE = 2;
    private final int CAMERA_TASKCODE = 4;
    private final int CREATE_COMPANY_TASKCODE = 5;
    private final int CREATE_BRAND_TASKCODE = 6;
    private final int GET_COMPANIES_DISTRIBUTOR_TASKCODE = 7;
    private final int GET_DISTRIBUTOR_TASKCODE = 8;
    private final int DELETE_COMPANY_TASKCODE = 9;
    private final int DELETE_BRAND_TASKCODE = 10;
    private final int DELETE_DISTRIBUTOR_TASKCODE = 11;

    private CompanyAdapter companyAdapter;
    private CompanyAdapter selectedCompanyAdapter;
    private BrandSelectionAdapter brandSelectionAdapter;
    private onNewDistributorAddedListener onnewDistributorAddedListener;
    private OnActionbarNavigationListener onActionbarNavigationListener;
    private EditText agencyEditText;
    private EditText salesmanEditText;
    private EditText tinnumberEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private EditText pincodeEditText;
    private EditText creditLimitEditText;
    private EditText cityEditText;
    private EditText newCompanyEditText;
    private EditText newBrandEditText;
    private View lastSelectedView;

    private List<Company> companyList;
    private List<Company> allCompaniesList;
    private List<Brand> taggedBrandList;
    private List<Brand> allBrandList;
    private List<Company> taggedCompanyList;
    private Integer[] distributorCompanyIdArr;
    private Distributor newDistributor;

    private Bitmap bitmap;
    private GetCompaniesTask getCompaniesTask;
    private GetCompaniesBrandTask getCompaniesBrandTask;

    private Distributor editDistributor;

    private TwoWayGridView distributorGridView;
    private DistributorAdapter distributorAdapter;
    private CompanySpinnerAdapter companySpinnerAdapter;

    private String agencyName;
    private String salesmanName;
    private String tinNumber;
    private String city;
    private String address;
    private String pinCode;
    private String phoneNumber;
    private float creditLimit;

    private Brand editBrand;
    private Company editCompany;

    boolean deleteBrand =false;
    boolean deleteCompany=false;
    boolean deleteDistributor=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_newdistributor, null);
        view.findViewById(R.id.brands_cancel_button).setOnClickListener(
                onNextBackClearClickListener);
        view.findViewById(R.id.add_button).setOnClickListener(
                onAddCancelClickListener);
        agencyEditText = (EditText) view.findViewById(R.id.agencyname_edittext);
        salesmanEditText = (EditText) view
                .findViewById(R.id.salesmanname_edittext);
        addressEditText = (EditText) view.findViewById(R.id.address_edittext);
        tinnumberEditText = (EditText) view
                .findViewById(R.id.tinnumber_edittext);
        phoneEditText = (EditText) view.findViewById(R.id.phonenumber_edittext);
        pincodeEditText = (EditText) view.findViewById(R.id.pincode_edittext);
        cityEditText = (EditText) view.findViewById(R.id.city_edittext);
        creditLimitEditText = (EditText) view
                .findViewById(R.id.creditlimit_edittext);
        distributorGridView = (TwoWayGridView) view
                .findViewById(R.id.distributor_gridview);
        view.findViewById(R.id.distributor_next_button).setOnClickListener(
                onNextBackClearClickListener);
        view.findViewById(R.id.company_next_button).setOnClickListener(
                onNextBackClearClickListener);
        view.findViewById(R.id.company_cancel_button).setOnClickListener(
                onNextBackClearClickListener);
        view.findViewById(R.id.distributor_clear_button).setOnClickListener(
                onNextBackClearClickListener);
        ((TwoWayGridView) view.findViewById(R.id.brand_gridview))
        .setOnItemClickListener(onBrandClickListener);
        distributorGridView.setOnItemClickListener(onDistributorSelectListener);
        view.findViewById(R.id.new_brand_clear_button).setOnClickListener(
                onNextBackClearClickListener);
        view.findViewById(R.id.new_company_clear_button).setOnClickListener(
                onNextBackClearClickListener);
        newBrandEditText = (EditText) view
                .findViewById(R.id.new_brandname_edittext);
        newCompanyEditText = (EditText) view
                .findViewById(R.id.new_companyname_edittext);
        newBrandEditText.addTextChangedListener(brandNameTextWatcher);
        newCompanyEditText.addTextChangedListener(companySearchTextWatcher);
        view.findViewById(R.id.new_company_delete_button).setOnClickListener(
                onAddCancelClickListener);
        view.findViewById(R.id.new_brand_delete_button).setOnClickListener(
                onAddCancelClickListener);
        view.findViewById(R.id.new_distributor_delete_button)
        .setOnClickListener(onAddCancelClickListener);
        return view;
    }

    View.OnClickListener onAddCancelClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.add_button) {
                if (taggedBrandList == null || taggedBrandList.size() == 0) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_tag_brand), Toast.LENGTH_SHORT, CustomToast.INFORMATION);
                } else {
                    newDistributor = new Distributor(
                            SnapToolkitTextFormatter.capitalseText(agencyName),
                            SnapToolkitTextFormatter
                            .capitalseText(salesmanName),
                            SnapToolkitTextFormatter.capitalseText(city),
                            address, pinCode, tinNumber, phoneNumber,
                            creditLimit);
                    if (editDistributor != null) {
                        newDistributor.setDistributorId(editDistributor
                                .getDistributorId());
                        newDistributor.setLastModifiedTimestamp(editDistributor
                                .getLastModifiedTimestamp());
                        if(editDistributor.getCompanyNames()!=null)
                        newDistributor.setCompanyNames(editDistributor.getCompanyNames());
                    }
                    new CreateDistributorTask(getActivity(),
                            NewDistributorFragment.this,
                            CREATE_DISTRIBUTOR_TASKCODE, taggedBrandList,
                            taggedCompanyList).execute(newDistributor);
                }
            } else if (v.getId() == R.id.new_brand_done_button) {
                String brandName = newBrandEditText.getText().toString();
                if (!brandName.equals("")) {
                    if (!ValidationUtils.validateName(brandName)) {
                        CustomToast.showCustomToast(getActivity(), getString(R.string.msg_valid_brand), Toast.LENGTH_SHORT, CustomToast.WARNING);
                        return;
                    }
                    Brand newBrand = new Brand();
                    newBrand.setBrandName(SnapToolkitTextFormatter
                            .capitalseText(brandName));
                    if (bitmap != null)
                        newBrand.setBrandImageUrl(SnapCommonUtils.storeImageBitmap(bitmap, newBrand));
                    newBrand.setCompany((Company) ((Spinner) getView()
                            .findViewById(R.id.company_select_spinner))
                            .getSelectedItem());
                    if (editBrand != null) {
                        newBrand.setBrandId(editBrand.getBrandId());
                        newBrand.setLastModifiedTimestamp(editBrand
                                .getLastModifiedTimestamp());
                        if(bitmap == null)
                            newBrand.setBrandImageUrl(editBrand.getBrandImageUrl());
                    }
                    new CreateOrUpdateBrandTask(getActivity(),
                            NewDistributorFragment.this, CREATE_BRAND_TASKCODE)
                    .execute(newBrand);
                } else {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_brand_name), Toast.LENGTH_SHORT, CustomToast.ERROR);
                }
            } else if (v.getId() == R.id.new_company_done_button) {
                String companyName = newCompanyEditText.getText().toString();
                if (companyName.isEmpty()) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_valid_company), Toast.LENGTH_SHORT, CustomToast.ERROR);
                    return;
                }
                Company newCompany = new Company();
                newCompany.setCompanyName(SnapToolkitTextFormatter
                        .capitalseText(companyName));
                if (editCompany != null) {
                    newCompany.setCompanyId(editCompany.getCompanyId());
                    newCompany.setLastModifiedTimestamp(editCompany
                            .getLastModifiedTimestamp());
                }
                new CreateOrUpdateCompanyTask(getActivity(),
                        NewDistributorFragment.this, CREATE_COMPANY_TASKCODE)
                .execute(newCompany);

            } else if (v.getId() == R.id.new_company_imageview
                    || v.getId() == R.id.new_brand_imageview) {
                Intent cameraIntent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_TASKCODE);
            } else if (v.getId() == R.id.new_company_delete_button) {
                if (editCompany != null) {
                    deleteCompany=true;
                    SnapCommonUtils.showDeleteAlert(getActivity(), "", getString(R.string.msg_confirm_delete), positiveDeleteClickListener, negativeDeleteClickListener, false);
                }
            } else if (v.getId() == R.id.new_brand_delete_button) {
                if (editBrand != null) {
                    deleteBrand=true;
                    SnapCommonUtils.showDeleteAlert(getActivity(), "", getString(R.string.msg_confirm_delete), positiveDeleteClickListener, negativeDeleteClickListener, false);
                }
            } 
            else if (v.getId() == R.id.new_distributor_delete_button) {
                if (editDistributor != null) {
                    deleteDistributor=true;
                    SnapCommonUtils.showDeleteAlert(getActivity(), "", getString(R.string.msg_confirm_delete), positiveDeleteClickListener, negativeDeleteClickListener, false);
                }
            }
        }
    };

    View.OnClickListener onNextBackClearClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (v.getId() == R.id.distributor_next_button) {
                agencyName = agencyEditText.getText().toString().trim();
                salesmanName = salesmanEditText.getText().toString().trim();
                tinNumber = tinnumberEditText.getText().toString().trim();
                city = cityEditText.getText().toString().trim();
                address = addressEditText.getText().toString().trim();
                pinCode = pincodeEditText.getText().toString().trim();
                phoneNumber = phoneEditText.getText().toString().trim();
                try {
                    creditLimit = Float.parseFloat(creditLimitEditText
                            .getText().toString().trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    creditLimit = 0;
                }
                if (agencyName.isEmpty()) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_dist_agency_name), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else if (!salesmanName.isEmpty()
                        && !ValidationUtils.validateName(salesmanName)) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_dist_salesman_name), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else if (city.length() != 0
                        && !ValidationUtils.validateName(city)) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_valid_city), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else if (pinCode.length() > 0 && pinCode.length() != 6) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_valid_pin), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else if (phoneNumber.length() != 0
                        && !ValidationUtils.validateMobileNo(phoneNumber)) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_valid_phone), Toast.LENGTH_SHORT, CustomToast.WARNING);
                } else {
                    hideSoftKeyboard();
//                    getView().findViewById(R.id.distributor_relativelayout)
//                    .setVisibility(View.GONE);
//                    getView().findViewById(R.id.company_relativelayout)
//                    .setVisibility(View.VISIBLE);
                    if (selectedCompanyAdapter != null) {
                        selectedCompanyAdapter.clear();
                        selectedCompanyAdapter.notifyDataSetChanged();
                    }
                    //					if (editDistributor == null) {
                    //						getCompaniesTask = new GetCompaniesTask(getActivity(),
                    //								NewDistributorFragment.this,
                    //								GET_COMPANIES_TASKCODE);
                    //						getCompaniesTask.execute();
                    //					} else {
                    //						new GetCompaniesByDistributorTask(getActivity(),
                    //								NewDistributorFragment.this,
                    //								GET_COMPANIES_TASKCODE).execute(editDistributor
                    //										.getDistributorId());
                    //					}
                    newDistributor = new Distributor(
                            SnapToolkitTextFormatter.capitalseText(agencyName),
                            SnapToolkitTextFormatter
                            .capitalseText(salesmanName),
                            SnapToolkitTextFormatter.capitalseText(city),
                            address, pinCode, tinNumber, phoneNumber,
                            creditLimit);
                    if (editDistributor != null) {
                        newDistributor.setDistributorId(editDistributor
                                .getDistributorId());
                        newDistributor.setLastModifiedTimestamp(editDistributor
                                .getLastModifiedTimestamp());
                        if(editDistributor.getCompanyNames()!=null)
                        newDistributor.setCompanyNames(editDistributor.getCompanyNames());
                    }
                    new CreateDistributorTask(getActivity(),
                            NewDistributorFragment.this,
                            CREATE_DISTRIBUTOR_TASKCODE, taggedBrandList,
                            taggedCompanyList).execute(newDistributor);
                
                    lastSelectedView.setSelected(false);
                    lastSelectedView = getActivity().findViewById(
                            R.id.step2_header);
                    lastSelectedView.setSelected(true);
                    newCompanyEditText.setText("");
                    deleteDistributor = false;
                }
            } else if (v.getId() == R.id.company_next_button) {
                hideSoftKeyboard();
                if (selectedCompanyAdapter == null
                        || selectedCompanyAdapter.getCount() == 0) {
                    CustomToast.showCustomToast(getActivity(), getString(R.string.msg_select_company), Toast.LENGTH_SHORT, CustomToast.WARNING);
                    return;
                }
                Integer[] selectedCompanyIdArray = new Integer[selectedCompanyAdapter
                                                               .getCount()];
                if (taggedCompanyList == null)
                    taggedCompanyList = new ArrayList<Company>();
                else
                    taggedCompanyList.clear();

                for (int i = 0; i < selectedCompanyAdapter.getCount(); i++) {
                    Company selectedCompany = selectedCompanyAdapter.getItem(i);
                    selectedCompanyIdArray[i] = selectedCompany.getCompanyId();
                    taggedCompanyList.add(selectedCompany);
                }
                if (taggedBrandList != null)
                    taggedBrandList.clear();
                new GetCompaniesBrandTask(getActivity(),
                        NewDistributorFragment.this,
                        GET_COMPANY_BRANDS_TASKCODE, editDistributor, distributorCompanyIdArr)
                .execute(selectedCompanyIdArray);
                if (companySpinnerAdapter == null) {
                    companySpinnerAdapter = new CompanySpinnerAdapter(
                            getActivity(), taggedCompanyList);
                    ((Spinner) getView().findViewById(
                            R.id.company_select_spinner))
                            .setAdapter(companySpinnerAdapter);
                } else {
                    companySpinnerAdapter.notifyDataSetChanged();
                }
                getView().findViewById(R.id.company_relativelayout)
                .setVisibility(View.GONE);
                getView().findViewById(R.id.brands_relativelayout)
                .setVisibility(View.VISIBLE);
                lastSelectedView.setSelected(false);
                lastSelectedView = getActivity()
                        .findViewById(R.id.step3_header);
                lastSelectedView.setSelected(true);
                newBrandEditText.setText("");
                ((ImageView) getView().findViewById(R.id.new_brand_imageview))
                .setImageDrawable(getResources().getDrawable(
                        R.drawable.icon_camera));
                deleteCompany=false;
            } else if (v.getId() == R.id.company_cancel_button) {
                getView().findViewById(R.id.company_relativelayout)
                .setVisibility(View.GONE);
                getView().findViewById(R.id.distributor_relativelayout)
                .setVisibility(View.VISIBLE);
                lastSelectedView.setSelected(false);
                lastSelectedView = getActivity()
                        .findViewById(R.id.step1_header);
                lastSelectedView.setSelected(true);
                deleteCompany=false;
            } else if (v.getId() == R.id.brands_cancel_button) {
                getView().findViewById(R.id.company_relativelayout)
                .setVisibility(View.VISIBLE);
                getView().findViewById(R.id.brands_relativelayout)
                .setVisibility(View.GONE);
                lastSelectedView.setSelected(false);
                lastSelectedView = getActivity()
                        .findViewById(R.id.step2_header);
                lastSelectedView.setSelected(true);
                newCompanyEditText.setText("");
                deleteBrand=false;
            } else if (v.getId() == R.id.distributor_clear_button) {
                editDistributor = null;
                resetEditText();
            } else if (v.getId() == R.id.new_brand_clear_button) {
                newBrandEditText.setText("");
                ((ImageView) getView().findViewById(R.id.new_brand_imageview))
                .setImageDrawable(getResources().getDrawable(
                        R.drawable.icon_camera));
                bitmap = null;
                editBrand = null;
            } else if (v.getId() == R.id.new_company_clear_button) {
                newCompanyEditText.setText("");
                editCompany = null;
            }
        }
    };

    TwoWayAdapterView.OnItemClickListener onDistributorSelectListener = new TwoWayAdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(TwoWayAdapterView<?> parent, View view,
                int position, long id) {
            // TODO Auto-generated method stub
            editDistributor = distributorAdapter.getItem(position);
            agencyEditText.setText(editDistributor.getAgencyName());
            if (editDistributor.getSalesmanName() != null)
                salesmanEditText.setText(editDistributor.getSalesmanName());
            if (editDistributor.getTinNumber() != null)
                tinnumberEditText.setText(editDistributor.getTinNumber());
            if (editDistributor.getCreditLimit() != 0)
                creditLimitEditText.setText(editDistributor.getCreditLimit()
                        + "");
            if (editDistributor.getPhoneNumber() != null)
                phoneEditText.setText(editDistributor.getPhoneNumber());
            if (editDistributor.getAddress() != null)
                addressEditText.setText(editDistributor.getAddress());
            if (editDistributor.getCity() != null)
                cityEditText.setText(editDistributor.getCity());
            if (editDistributor.getPincode() != null)
                pincodeEditText.setText(editDistributor.getPincode());
        }

    };

    public Distributor getEditDistributor() {
        return editDistributor;
    }

    public void setEditDistributor(Distributor editDistributor) {
        this.editDistributor = editDistributor;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_TASKCODE && resultCode == Activity.RESULT_OK) {
            if (bitmap != null) {
                bitmap.recycle();
            }
            bitmap = (Bitmap) data.getExtras().get("data");
            Bitmap.createScaledBitmap(bitmap, 100, 100, false);
            ((ImageView) getView().findViewById(R.id.new_brand_imageview))
            .setImageBitmap(bitmap);

        }
        super.onActivityResult(requestCode, resultCode, data);
    };

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        try {
            onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("activity " + activity.toString()
                    + " must implement OnActionbarNavigationListener");
        }
        try {
            onnewDistributorAddedListener = (onNewDistributorAddedListener) activity;
        } catch (Exception e) {
            throw new ClassCastException("activity " + activity.toString()
                    + " must implement onNewDistributorAddedListener");
        }
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            if (companyList != null) {
                companyList.clear();
                companyList.addAll(allCompaniesList);
            }
        }
        onActionbarNavigationListener.onActionbarNavigation("",
                menuItem.getItemId());
        return true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getActionBar().setCustomView(
                R.layout.actionbar_adddistributor_layout);
        lastSelectedView = getActivity().findViewById(R.id.step1_header);
        lastSelectedView.setSelected(true);
        ((TwoWayGridView) getView().findViewById(R.id.company_gridview))
        .setOnItemClickListener(onCompanyClickListener);
        getView().findViewById(R.id.new_brand_done_button).setOnClickListener(
                onAddCancelClickListener);
        getView().findViewById(R.id.new_company_done_button)
        .setOnClickListener(onAddCancelClickListener);
        getView().findViewById(R.id.new_brand_imageview).setOnClickListener(
                onAddCancelClickListener);
        if (distributorAdapter == null) {
            Request request = new Request();
            HashMap<String, String> requestParamMap = new HashMap<String, String>();
            Store store = SnapSharedUtils.getStoreDetails(SnapCommonUtils
                    .getSnapContext(getActivity()));
            requestParamMap.put(SnapToolkitConstants.PINCODE_KEY,
                    store.getZipCode());
            requestParamMap.put(SnapToolkitConstants.STORE_ID,
                    store.getStoreId());
            requestParamMap.put(SnapToolkitConstants.STORE_AUTH_KEY,
                    store.getStoreAuthToken());
            requestParamMap
            .put(SnapToolkitConstants.DEVICE_ID, SnapSharedUtils
                    .getDeviceId(SnapCommonUtils
                            .getSnapContext(getActivity())));
            request.setRequestParams(requestParamMap);
            // ServiceRequest serviceRequest = new ServiceRequest(request,
            // getActivity());
            // serviceRequest.setPath(SnapToolkitConstants.DISTIBUTORS_PATH);
            // serviceRequest.setMethod(SnapToolkitConstants.LOCAL_DISTRIBUTORS_METHOD);
            // serviceRequest.setResponsibleClass(responsibleClass)
            // ServiceThread serviceThread = new ServiceThread(getActivity(),
            // this, false);
            // serviceThread.execute(serviceRequest);
        } else {
            distributorGridView.setAdapter(distributorAdapter);
        }
        if (editDistributor != null) {
            agencyEditText.setText(editDistributor.getAgencyName());
            if (editDistributor.getSalesmanName() != null)
                salesmanEditText.setText(editDistributor.getSalesmanName());
            if (editDistributor.getTinNumber() != null)
                tinnumberEditText.setText(editDistributor.getTinNumber());
            if (editDistributor.getCreditLimit() != 0)
                creditLimitEditText.setText(editDistributor.getCreditLimit()
                        + "");
            if (editDistributor.getPhoneNumber() != null)
                phoneEditText.setText(editDistributor.getPhoneNumber());
            if (editDistributor.getAddress() != null)
                addressEditText.setText(editDistributor.getAddress());
            if (editDistributor.getCity() != null)
                cityEditText.setText(editDistributor.getCity());
            if (editDistributor.getPincode() != null)
                pincodeEditText.setText(editDistributor.getPincode());
        }
        if (companyAdapter != null) {
            ((TwoWayGridView) getView().findViewById(R.id.company_gridview))
            .setAdapter(companyAdapter);
        }
        if (selectedCompanyAdapter != null) {
            ((ListView) getView().findViewById(R.id.selected_company_listview))
            .setAdapter(selectedCompanyAdapter);
        }
        ((ListView) getView().findViewById(R.id.selected_company_listview))
        .setOnItemClickListener(onSelectedCompanyClickListener);
        if (companySpinnerAdapter != null) {
            ((Spinner) getView().findViewById(R.id.company_select_spinner))
            .setAdapter(companySpinnerAdapter);
        }
        if(brandSelectionAdapter != null) {
            ((TwoWayGridView) getView().findViewById(R.id.brand_gridview))
            .setAdapter(brandSelectionAdapter);
        }

        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(agencyEditText, InputMethodManager.SHOW_FORCED);
    }

    TwoWayAdapterView.OnItemClickListener onCompanyClickListener = new TwoWayAdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(TwoWayAdapterView<?> parent, View view,
                int position, long id) {
            // TODO Auto-generated method stub

            Company company = companyAdapter.getItem(position);
            if (company == null || company.isHeader())
                return;
            if (!company.isSelected()) {
                company.setSelected(true);
                if (selectedCompanyAdapter == null) {
                    selectedCompanyAdapter = new CompanyAdapter(getActivity(),
                            new ArrayList<Company>(), true);
                    ListView listView = (ListView) getView().findViewById(
                            R.id.selected_company_listview);
                    listView.setVisibility(View.VISIBLE);
                    listView.setAdapter(selectedCompanyAdapter);
                }
                ListView listView = (ListView) getView().findViewById(
                        R.id.selected_company_listview);
                if (listView.getVisibility() == View.GONE)
                    listView.setVisibility(View.VISIBLE);
                selectedCompanyAdapter.add(company);
                selectedCompanyAdapter.notifyDataSetChanged();
                for (int i = 0; i < allCompaniesList.size(); i++) {
                    if (company.getCompanyId() == allCompaniesList.get(i)
                            .getCompanyId()) {
                        allCompaniesList.get(i).setSelected(
                                company.isSelected());
                        break;
                    }
                }
                if(newCompanyEditText.length() > 0)
                    newCompanyEditText.setText("");
            }
        }

    };

    AdapterView.OnItemClickListener onSelectedCompanyClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view,
                int position, long id) {
            Company company = selectedCompanyAdapter.getItem(position);
            company.setSelected(false);
            companyAdapter.notifyDataSetChanged();
            selectedCompanyAdapter.remove(selectedCompanyAdapter
                    .getItem(position));
            selectedCompanyAdapter.notifyDataSetChanged();
            if (selectedCompanyAdapter.getCount() == 0)
                getView().findViewById(R.id.selected_company_listview)
                .setVisibility(View.GONE);
        }
    };

    TwoWayAdapterView.OnItemClickListener onBrandClickListener = new TwoWayAdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(TwoWayAdapterView<?> parent, View view,
                int position, long id) {
            Brand brand = brandSelectionAdapter.getItem(position);
            brand.setSelected(!brand.isSelected());
            if (brand.isSelected()) {
                if (taggedBrandList == null)
                    taggedBrandList = new ArrayList<Brand>();
                taggedBrandList.add(brand);
            } else {
                taggedBrandList.remove(brand);
            }
            brandSelectionAdapter.notifyDataSetChanged();
        }
    };

    OnClickListener onDismissAddViewListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            hideSoftKeyboard();
            v.setVisibility(View.GONE);
        }
    };

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (brandSelectionAdapter != null) {
            brandSelectionAdapter.notifyDataSetChanged();
        }
        if (companyAdapter != null) {
            for (Company company : companyList) {
                company.setSelected(false);
            }
            companyAdapter.notifyDataSetChanged();
            if (getCompaniesBrandTask != null
                    && !getCompaniesBrandTask.isCancelled()) {
                getCompaniesBrandTask.cancel(true);
            }
        }
        if (getCompaniesTask != null && !getCompaniesTask.isCancelled()) {
            getCompaniesTask.cancel(true);
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    public void resetEditText() {
        if (agencyEditText != null) {
            agencyEditText.setText("");
            tinnumberEditText.setText("");
            salesmanEditText.setText("");
            creditLimitEditText.setText("");
            addressEditText.setText("");
            phoneEditText.setText("");
            cityEditText.setText("");
            pincodeEditText.setText("");
        }
    }

    TextWatcher brandNameTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            // TODO Auto-generated method stub
            if (brandSelectionAdapter == null || allBrandList == null)
                return;
            brandKeyStrokeTimer.cancel();
            if (s.length() == 0) {
                brandSelectionAdapter.clear();
                brandSelectionAdapter.addAll(allBrandList);
                brandSelectionAdapter.notifyDataSetChanged();
            } else {
                brandKeyStrokeTimer.start();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    CountDownTimer brandKeyStrokeTimer = new CountDownTimer(
            SnapToolkitConstants.KEY_STROKE_TIMEOUT,
            SnapToolkitConstants.KEY_STROKE_TIMEOUT) {

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish() {
            // TODO Auto-generated method stub
            brandSelectionAdapter.clear();
            String brandSearchString = newBrandEditText.getText().toString()
                    .toLowerCase();
            for (Brand brand : allBrandList) {
                if (brand.getBrandName().toLowerCase()
                        .contains(brandSearchString)) {
                    brandSelectionAdapter.add(brand);
                }
            }
            brandSelectionAdapter.notifyDataSetChanged();
        }
    };

    TextWatcher companySearchTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            if (companyList == null)
                return;
            companySearchKeyStrokeTimer.cancel();
            if (s.length() == 0) {
                companyList.clear();
                companyList.addAll(allCompaniesList);
                companyAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(
                        companyList, 7));
                companyAdapter.notifyDataSetChanged();
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
            companyList.clear();
            String companySearchString = newCompanyEditText.getText()
                    .toString().toLowerCase();
            for (Company company : allCompaniesList) {
                if (!company.isHeader()
                        && company.getCompanyName().toLowerCase()
                        .contains(companySearchString)) {
                    companyList.add(company);
                }
            }
            companyAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(
                    companyList, 7));
            companyAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onTaskSuccess(Object responseList, int taskCode) {
        // TODO Auto-generated method stub
        if (taskCode == GET_COMPANIES_TASKCODE) {
            if (companyList == null)
                companyList = new ArrayList<Company>();
            else
                companyList.clear();
            List<Company> companyResponseList = (List<Company>) responseList;
            char firstCharacter = 0;
            for (int i = 0; i < companyResponseList.size(); i++) {
                if (companyResponseList.get(i).getCompanyName().charAt(0) != firstCharacter) {
                    companyList.add(new Company(companyResponseList.get(i)
                            .getCompanyName().charAt(0)
                            + ""));
                    firstCharacter = companyResponseList.get(i)
                            .getCompanyName().charAt(0);
                }
                companyList.add(companyResponseList.get(i));
            }

            if (allCompaniesList == null) {
                allCompaniesList = new ArrayList<Company>();
                allCompaniesList.addAll(companyList);
            } else {
                allCompaniesList.clear();
                allCompaniesList.addAll(companyList);
            }

            if (companyAdapter == null) {
                companyAdapter = new CompanyAdapter(getActivity(), companyList,
                        this);
                companyAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(
                        companyList, 7));
                ((TwoWayGridView) getView().findViewById(R.id.company_gridview))
                .setAdapter(companyAdapter);
            } else {
                companyAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(
                        companyList, 7));
                companyAdapter.notifyDataSetChanged();
            }
            if (selectedCompanyAdapter == null) {
                selectedCompanyAdapter = new CompanyAdapter(getActivity(),
                        new ArrayList<Company>(), true);
                ListView listView = (ListView) getView().findViewById(
                        R.id.selected_company_listview);
                listView.setAdapter(selectedCompanyAdapter);
            } else {
                selectedCompanyAdapter.clear();
            }
            boolean isSelected = false;

            for (Company company : companyList) {
                if (company.isSelected()) {
                    isSelected = true;
                    selectedCompanyAdapter.add(company);
                }
            }

            distributorCompanyIdArr = new Integer[selectedCompanyAdapter.getCount()];
            for(int i = 0; i < selectedCompanyAdapter.getCount(); i++) {
                distributorCompanyIdArr[i] = selectedCompanyAdapter.getItem(i).getCompanyId();
            }
            selectedCompanyAdapter.notifyDataSetChanged();
            if (!isSelected)
                getView().findViewById(R.id.selected_company_listview)
                .setVisibility(View.GONE);
            else
                getView().findViewById(R.id.selected_company_listview)
                .setVisibility(View.VISIBLE);
            newCompanyEditText.setText("");
        } else if (taskCode == CREATE_DISTRIBUTOR_TASKCODE) {
            resetEditText();
            if (editDistributor == null)
                onnewDistributorAddedListener
                .addDistributortoList((Distributor) responseList);
            else {
                editDistributor.setAgencyName(agencyName);
                editDistributor.setAddress(address);
                editDistributor.setCity(city);
                editDistributor.setCreditLimit(creditLimit);
                editDistributor.setLastModifiedTimestamp(newDistributor
                        .getLastModifiedTimestamp());
                editDistributor.setPhoneNumber(phoneNumber);
                editDistributor.setSalesmanName(salesmanName);
                editDistributor.setPincode(pinCode);
                editDistributor.setTinNumber(tinNumber);
                editDistributor.setCompanyNames(newDistributor
                        .getCompanyNames());
                editDistributor
                .setLastModifiedTimestamp(((Distributor) responseList)
                        .getLastModifiedTimestamp());
                onnewDistributorAddedListener.onEditDistributorList((Distributor) responseList);
            }
            editDistributor = null;
//            companyList.clear();
//            companyList.addAll(allCompaniesList);
        } else if (taskCode == GET_COMPANY_BRANDS_TASKCODE) {
            List<Brand> brandList = (List<Brand>) responseList;
            if (taggedBrandList == null)
                taggedBrandList = new ArrayList<Brand>();
            else
                taggedBrandList.clear();
            if (allBrandList == null)
                allBrandList = new ArrayList<Brand>();
            else
                allBrandList.clear();
            allBrandList.addAll(brandList);
            for (Brand brand : brandList) {
                if (brand.isSelected())
                    taggedBrandList.add(brand);
            }
            if (brandSelectionAdapter == null) {
                brandSelectionAdapter = new BrandSelectionAdapter(
                        getActivity(), brandList, this);
            } else {
                brandSelectionAdapter.clear();
                brandSelectionAdapter.addAll(brandList);
                brandSelectionAdapter.notifyDataSetChanged();
            }
            editBrand = null;
            ((TwoWayGridView) getView().findViewById(R.id.brand_gridview))
            .setAdapter(brandSelectionAdapter);
            newBrandEditText.setText("");
        } else if (taskCode == CREATE_COMPANY_TASKCODE) {
            hideSoftKeyboard();
            Company company = ((Company) responseList);
            newCompanyEditText.setText("");
            if (editCompany == null) {
                company.setSelected(true);
                allCompaniesList.add(company);
                if (selectedCompanyAdapter == null) {
                    selectedCompanyAdapter = new CompanyAdapter(getActivity(),
                            new ArrayList<Company>(), true);
                }
                View view = getView().findViewById(
                        R.id.selected_company_listview);
                if (view.getVisibility() == View.GONE)
                    view.setVisibility(View.VISIBLE);
                selectedCompanyAdapter.add(company);
            } else {
                editCompany.setCompanyName(company.getCompanyName());
                editCompany.setLastModifiedTimestamp(company.getLastModifiedTimestamp());
                editCompany = null;
            }
            selectedCompanyAdapter.notifyDataSetChanged();
            companyAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(
                    companyList, 7));
            companyAdapter.notifyDataSetChanged();
        } else if (taskCode == CREATE_BRAND_TASKCODE) {
            hideSoftKeyboard();
            Brand companyBrand = (Brand) responseList;
            newBrandEditText.setText("");
            ((ImageView) getView().findViewById(R.id.new_brand_imageview))
            .setImageDrawable(getResources().getDrawable(
                    R.drawable.icon_camera));
            bitmap = null;
            if (brandSelectionAdapter == null) {
                brandSelectionAdapter = new BrandSelectionAdapter(
                        getActivity(), new ArrayList<Brand>(), this);
                ((TwoWayGridView) getView().findViewById(R.id.brand_gridview))
                .setAdapter(brandSelectionAdapter);
            }
            companyBrand.setSelected(true);
            if (editBrand == null) {
                if (taggedBrandList == null)
                    taggedBrandList = new ArrayList<Brand>();
                if(allBrandList == null)
                    allBrandList = new ArrayList<Brand>();
                allBrandList.add(companyBrand);
                brandSelectionAdapter.add(companyBrand);
                taggedBrandList.add(companyBrand);
            } else {
                editBrand.setBrandImageUrl(companyBrand.getBrandImageUrl());
                editBrand.setBrandName(companyBrand.getBrandName());
                editBrand.setCompany(companyBrand.getCompany());
                editBrand.setLastModifiedTimestamp(companyBrand.getLastModifiedTimestamp());
                editBrand = null;
            }
            brandSelectionAdapter.notifyDataSetChanged();
        } else if (taskCode == GET_DISTRIBUTOR_TASKCODE) {
            if (distributorAdapter == null) {
                distributorAdapter = new DistributorAdapter(getActivity(),
                        (List<Distributor>) responseList);
                distributorGridView.setAdapter(distributorAdapter);
            } else {
                distributorAdapter.clear();
                distributorAdapter
                .addAll((Collection<? extends Distributor>) responseList);
            }
        } else if (taskCode == DELETE_COMPANY_TASKCODE) {
            deleteCompany=false;
            hideSoftKeyboard();
            newCompanyEditText.setText("");
            for (int i = 0; i < allCompaniesList.size(); i++) {
                if (allCompaniesList.get(i).getCompanyId() == editCompany
                        .getCompanyId()) {
                    companyList.remove(i);
                    allCompaniesList.remove(i);
                    break;
                }
            }
            for (int i = 0; i < selectedCompanyAdapter.getCount(); i++) {
                if (selectedCompanyAdapter.getItem(i).getCompanyId() == editCompany
                        .getCompanyId()) {
                    selectedCompanyAdapter.remove(selectedCompanyAdapter
                            .getItem(i));
                }
            }
            if(selectedCompanyAdapter.getCount() == 0) {
                getView().findViewById(R.id.selected_company_listview).setVisibility(View.GONE);
            }
            companyAdapter.setTotalgrid(SnapInventoryUtils.getCompanyGrid(
                    companyList, 7));
            companyAdapter.notifyDataSetChanged();
            selectedCompanyAdapter.notifyDataSetChanged();
            editCompany = null;
        } else if (taskCode == DELETE_BRAND_TASKCODE) {
            deleteBrand=false;
            hideSoftKeyboard();
            newBrandEditText.setText("");
            ((ImageView) getView().findViewById(R.id.new_brand_imageview))
            .setImageDrawable(getResources().getDrawable(
                    R.drawable.icon_camera));
            bitmap = null;
            for (int i = 0; i < allBrandList.size(); i++) {
                if (allBrandList.get(i).getBrandId() == editBrand.getBrandId()) {
                    brandSelectionAdapter.remove(allBrandList.get(i));
                    allBrandList.remove(i);
                }
            }
            if(taggedBrandList != null) {
                for(int i = 0; i < taggedBrandList.size(); i++) {
                    if(taggedBrandList.get(i).getBrandId() == editBrand.getBrandId()) {
                        taggedBrandList.remove(i);
                    }
                }
            }
            brandSelectionAdapter.notifyDataSetChanged();
            editBrand = null;
        } else if (taskCode == DELETE_DISTRIBUTOR_TASKCODE) {
            deleteDistributor=false;
            onnewDistributorAddedListener.onDeleteDistributor(editDistributor);
        }
    }

    @Override
    public void onTaskError(String errorMessage, int taskCode) {
        // TODO Auto-generated method stub
        if (taskCode == CREATE_BRAND_TASKCODE) {
            CustomToast.showCustomToast(getActivity(), getString(R.string.msg_brand_creation), Toast.LENGTH_SHORT, CustomToast.ERROR);
        } else if (taskCode == GET_COMPANIES_DISTRIBUTOR_TASKCODE) {
            //Toast.makeText(getActivity(), " ", 0).show();
        } else if(taskCode == GET_COMPANY_BRANDS_TASKCODE) {
            if(brandSelectionAdapter != null)
                brandSelectionAdapter.clear();
            if(allBrandList != null)
                allBrandList.clear();
        } else {
            CustomToast.showCustomToast(getActivity(), errorMessage, Toast.LENGTH_SHORT, CustomToast.ERROR);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.findItem(R.id.search_meuitem).setVisible(false);
    }

    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            SnapCommonUtils.hideSoftKeyboard(getActivity(), getActivity().getCurrentFocus().getWindowToken());
        }
    }

    public interface onNewDistributorAddedListener {
        public void addDistributortoList(Distributor distributor);

        public void onEditDistributorList(Distributor distributor);

        public void onDeleteDistributor(Distributor distributor);
    }

    @Override
    public void onSuccess(ResponseContainer response) {
        // TODO Auto-generated method stub
        if (distributorAdapter == null) {
            distributorAdapter = new DistributorAdapter(getActivity(),
                    (List<Distributor>) response);
            distributorGridView.setAdapter(distributorAdapter);
        } else {
            distributorAdapter.clear();
            distributorAdapter
            .addAll((Collection<? extends Distributor>) response);
        }
    }

    @Override
    public void onError(ResponseContainer response, RequestCodes requestCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onCompanyEdit(int position) {
        // TODO Auto-generated method stub
        editCompany = companyAdapter.getItem(position);
        newCompanyEditText.setText(editCompany.getCompanyName());
    }

    @Override
    public void onBrandEdit(int position) {
        // TODO Auto-generated method stub
        editBrand = brandSelectionAdapter.getItem(position);
        newBrandEditText.setText(editBrand.getBrandName());
        ((ImageView) getView().findViewById(R.id.new_brand_imageview))
        .setImageDrawable(SnapCommonUtils.getBrandDrawable(
                editBrand, getActivity()));
    }

    OnClickListener positiveDeleteClickListener =  new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(editCompany!=null && deleteCompany)
                new DeleteCompanyTask(getActivity(),
                        NewDistributorFragment.this,
                        DELETE_COMPANY_TASKCODE).execute(editCompany);
            else if(editBrand!=null && deleteBrand)
                new DeleteBrandTask(getActivity(),
                        NewDistributorFragment.this, DELETE_BRAND_TASKCODE)
            .execute(editBrand);
            else if (editDistributor != null && deleteDistributor) {
                new DeleteDistributorTask(getActivity(),
                        NewDistributorFragment.this,
                        DELETE_DISTRIBUTOR_TASKCODE)
                .execute(editDistributor);
            }
            SnapCommonUtils.dismissAlert();
        }
    };

    OnClickListener negativeDeleteClickListener =  new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            SnapCommonUtils.dismissAlert();
            deleteBrand=false;
            deleteCompany=false;
            deleteDistributor=false;
        }
    };
}
