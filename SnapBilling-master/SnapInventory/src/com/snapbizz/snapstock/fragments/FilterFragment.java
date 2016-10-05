package com.snapbizz.snapstock.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.snapbizz.snapstock.R;
import com.snapbizz.snapstock.adapters.BrandSelectionAdapter;
import com.snapbizz.snapstock.adapters.CategoryAdapter;
import com.snapbizz.snapstock.asynctasks.GetSubCategoriesBrandsTask;
import com.snapbizz.snapstock.utils.ProductFilterType;
import com.snapbizz.snaptoolkit.asynctasks.QueryProductCategories;
import com.snapbizz.snaptoolkit.asynctasks.QueryProductSubCategories;
import com.snapbizz.snaptoolkit.domains.Brand;
import com.snapbizz.snaptoolkit.domains.Distributor;
import com.snapbizz.snaptoolkit.domains.ProductCategory;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.ToolkitV2;

public class FilterFragment extends Fragment implements OnQueryCompleteListener {

    private CategoryAdapter categoryAdapter;
    private CategoryAdapter subCategoryAdapter;
    private BrandSelectionAdapter brandSelectionAdapter;
    private ListView categoryListView;
    private TwoWayGridView subcategoryGridView;
    private TwoWayGridView brandGridView;
    private ProductFilterType productFilterType = ProductFilterType.CATEGORY_FILTER;
    private int GET_CATEGORIES_TASKCODE = 0;
    private int GET_SUBCATEGORIES_TASKCODE = 1;
    private int GET_BRANDS_TASKCODE = 2;
    private OnFilterSelectedListener onFilterSelectedListener;
    private Distributor distributor;
    private boolean showAllCategories;
    private ProductCategory selectedProductSubCategory;
    
    public void setSelectedProductSubCategory(
            ProductCategory selectedProductSubCategory) {
        this.selectedProductSubCategory = selectedProductSubCategory;
    }

    private boolean isBrandFilteredBySubcategory;

    public boolean isBrandFilteredBySubcategory() {
		return isBrandFilteredBySubcategory;
	}

	public void setBrandFilteredBySubcategory(boolean isBrandFilteredBySubcategory) {
		this.isBrandFilteredBySubcategory = isBrandFilteredBySubcategory;
	}

	public ProductCategory getSelectedProductSubCategory() {
        return selectedProductSubCategory;
    }

    public void setShowAllCategories(boolean showAllCategories) {
        this.showAllCategories = showAllCategories;
    }

    public void setDistributor(Distributor distributor) {
        this.distributor = distributor;
    }
    
    

    public void setProductFilterType(ProductFilterType productFilterType) {
        this.productFilterType = productFilterType;
        if(isVisible()) {
            if(productFilterType.ordinal() == ProductFilterType.CATEGORY_FILTER.ordinal()) {
                getView().findViewById(R.id.category_filter_layout).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.brand_filter_layout).setVisibility(View.GONE);
                if(categoryAdapter != null) {
                    categoryListView.setAdapter(categoryAdapter);
                } else {
                    new QueryProductCategories(getActivity(), this, GET_CATEGORIES_TASKCODE).execute();
                }
                if(subCategoryAdapter != null) {
                    subcategoryGridView.setAdapter(subCategoryAdapter);
                }
            } else if(productFilterType.ordinal() == ProductFilterType.BRAND_FILTER.ordinal()) {
                getView().findViewById(R.id.category_filter_layout).setVisibility(View.GONE);
                getView().findViewById(R.id.brand_filter_layout).setVisibility(View.VISIBLE);
                if(!isBrandFilteredBySubcategory) {
                    if(selectedProductSubCategory == null)
                        new GetSubCategoriesBrandsTask(getActivity(), this, GET_BRANDS_TASKCODE, distributor).execute();
                    else
                        new GetSubCategoriesBrandsTask(getActivity(), this, GET_BRANDS_TASKCODE, distributor).execute(selectedProductSubCategory.getCategoryId());
                }
            }
        }
    }

    public void setOnFilterSelectedListener(OnFilterSelectedListener onFilterSelectedListener) {
        this.onFilterSelectedListener = onFilterSelectedListener;
    }

    public ProductFilterType getProductFilterType() {
        return this.productFilterType;
    }
    
    public void resetFilters() {
        if(brandSelectionAdapter != null) {
            for(int i = 0; i < brandSelectionAdapter.getCount(); i++) {
                brandSelectionAdapter.getItem(i).setSelected(false);
            }
            brandSelectionAdapter.notifyDataSetChanged();
        }
    }

    public List<Brand> getSelectedBrand() {
        if(brandSelectionAdapter == null)
            return null;
        List<Brand> selectedBrandList = new ArrayList<Brand>();
        for(int i = 0; i < brandSelectionAdapter.getCount(); i++)
            if(brandSelectionAdapter.getItem(i).isSelected())
                selectedBrandList.add(brandSelectionAdapter.getItem(i));
        
        if(selectedBrandList.isEmpty()){
        	  for(int i = 0; i < brandSelectionAdapter.getCount(); i++)
                      selectedBrandList.add(brandSelectionAdapter.getItem(i));
        }
        return selectedBrandList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_filter, null);
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        if(onFilterSelectedListener == null)
            throw new ClassCastException(getActivity().toString()+" must set OnFilterSelectedListener");
        categoryListView = (ListView) getView().findViewById(R.id.category_listview);
        subcategoryGridView = (TwoWayGridView) getView().findViewById(R.id.subcategory_gridview);
        brandGridView = (TwoWayGridView) getView().findViewById(R.id.brand_gridview);
        categoryListView.setOnItemClickListener(onCategoryClickListener);
        subcategoryGridView.setOnItemClickListener(onSubCategoryClickListener);
        brandGridView.setOnItemClickListener(onBrandClickListener);
        getView().findViewById(R.id.select_all_button).setOnClickListener(onSelectAllBrandsClickListener);
        if(brandSelectionAdapter != null)
            brandGridView.setAdapter(brandSelectionAdapter);
        if(productFilterType.ordinal() == ProductFilterType.CATEGORY_FILTER.ordinal()) {
            getView().findViewById(R.id.category_filter_layout).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.brand_filter_layout).setVisibility(View.GONE);
            if(categoryAdapter != null) {
                categoryListView.setAdapter(categoryAdapter);
            } else {
                new QueryProductCategories(getActivity(), this, GET_CATEGORIES_TASKCODE).execute();
            }
            if(subCategoryAdapter != null) {
                subcategoryGridView.setAdapter(subCategoryAdapter);
            }
        } else if(productFilterType.ordinal() == ProductFilterType.BRAND_FILTER.ordinal()) {
            getView().findViewById(R.id.category_filter_layout).setVisibility(View.GONE);
            getView().findViewById(R.id.brand_filter_layout).setVisibility(View.VISIBLE);
            if(!isBrandFilteredBySubcategory) {    
                if(selectedProductSubCategory == null && !showAllCategories) {
                    selectedProductSubCategory = new ProductCategory();
                    selectedProductSubCategory.setCategoryId(2);
                }
                if(selectedProductSubCategory == null)
                    new GetSubCategoriesBrandsTask(getActivity(), this, GET_BRANDS_TASKCODE, distributor).execute();
                else
                    new GetSubCategoriesBrandsTask(getActivity(), this, GET_BRANDS_TASKCODE, distributor).execute(selectedProductSubCategory.getCategoryId());
            }
        }
    }

    View.OnClickListener onSelectAllBrandsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(brandSelectionAdapter != null) {
                v.setSelected(!v.isSelected());
                for(int i = 0; i < brandSelectionAdapter.getCount(); i++) {
                    Brand selectedBrand = brandSelectionAdapter.getItem(i);
                    selectedBrand.setSelected(v.isSelected());
                }
                brandSelectionAdapter.notifyDataSetChanged();
            }
        }
    };

    TwoWayAdapterView.OnItemClickListener onSubCategoryClickListener = new TwoWayAdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(TwoWayAdapterView<?> parent, View view,
                int position, long id) {
            // TODO Auto-generated method stub
            isBrandFilteredBySubcategory = false;
            selectedProductSubCategory = subCategoryAdapter.getItem(position);
            if(brandSelectionAdapter != null)
                brandSelectionAdapter.clear();
            Log.d("TAG", "selectedProductSubCategory----->"+selectedProductSubCategory.getCategoryId());
            onFilterSelectedListener.onProductSubCategorySelected(selectedProductSubCategory);
        }
    };

    TwoWayAdapterView.OnItemClickListener onBrandClickListener = new TwoWayAdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(TwoWayAdapterView<?> parent, View view,
                int position, long id) {
            // TODO Auto-generated method stub
            Brand selectedBrand = brandSelectionAdapter.getItem(position);
            selectedBrand.setSelected(!selectedBrand.isSelected());
            if(!selectedBrand.isSelected() && getView().findViewById(R.id.select_all_button).isSelected()) {
                getView().findViewById(R.id.select_all_button).setSelected(false);
            }
            brandSelectionAdapter.notifyDataSetChanged();
        }
    };

    AdapterView.OnItemClickListener onCategoryClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position,
                long id) {
            // TODO Auto-generated method stub
            if(categoryAdapter != null) {
                categoryAdapter.setLastSelectedPosition(position);
                categoryAdapter.notifyDataSetChanged();
            }
            ProductCategory productCategory = categoryAdapter.getItem(position);
            if(productCategory != null)
                new QueryProductSubCategories(getActivity(), FilterFragment.this, GET_SUBCATEGORIES_TASKCODE).execute(categoryAdapter.getItem(position).getCategoryId());
            else {
                isBrandFilteredBySubcategory = false;
                if(subCategoryAdapter != null) {
                    subCategoryAdapter.clear();
                    subCategoryAdapter.notifyDataSetChanged();
                }
                selectedProductSubCategory = null;
                onFilterSelectedListener.onProductSubCategorySelected(null);
            }
        }
    };

    @Override
    public void onTaskSuccess(Object responseList, int taskCode) {
        // TODO Auto-generated method stub
        if(getActivity() == null || getView() == null)
            return;
        if(taskCode == GET_CATEGORIES_TASKCODE) {
            if(categoryAdapter == null) {
                categoryAdapter = new CategoryAdapter(getActivity(), (List<ProductCategory>) responseList, R.layout.listitem_category, showAllCategories);
                categoryListView = (ListView) getView().findViewById(R.id.category_listview);
                categoryListView.setAdapter(categoryAdapter);
            } else {
                categoryAdapter.clear();
                categoryAdapter.addAll((List<ProductCategory>) responseList);
                categoryAdapter.notifyDataSetChanged();
            }
        } else if(taskCode == GET_SUBCATEGORIES_TASKCODE) {
        	List<ProductCategory> subproductCategoryList = (List<ProductCategory>) responseList;
        	ToolkitV2.hidingXtraProduct(subproductCategoryList, this.getActivity());
            if(subCategoryAdapter == null) {
                subCategoryAdapter = new CategoryAdapter(getActivity(), subproductCategoryList, R.layout.listitem_subcategory, showAllCategories);
                subcategoryGridView = (TwoWayGridView) getView().findViewById(R.id.subcategory_gridview);
                subcategoryGridView.setAdapter(subCategoryAdapter);
            } else {
                subCategoryAdapter.clear();
                subCategoryAdapter.addAll((List<ProductCategory>) responseList);
                subCategoryAdapter.notifyDataSetChanged();
            }
        } else if(taskCode == GET_BRANDS_TASKCODE) {
            isBrandFilteredBySubcategory = true;
            if(brandSelectionAdapter == null) {
                brandSelectionAdapter = new BrandSelectionAdapter(getActivity(), (List<Brand>) responseList, null);
                brandGridView.setAdapter(brandSelectionAdapter);
            } else {
                brandSelectionAdapter.clear();
                brandSelectionAdapter.addAll((List<Brand>) responseList);
                brandSelectionAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onTaskError(String errorMessage, int taskCode) {
        // TODO Auto-generated method stub
        if(taskCode == GET_CATEGORIES_TASKCODE) {
            if(categoryAdapter != null)
                categoryAdapter.clear();
        } else if(taskCode == GET_SUBCATEGORIES_TASKCODE) {
            if(subCategoryAdapter != null)
                subCategoryAdapter.clear();
        } else if(taskCode == GET_BRANDS_TASKCODE) {
            if(brandSelectionAdapter != null)
                brandSelectionAdapter.clear();
        }
    }

    public interface OnFilterSelectedListener {
        public void onProductSubCategorySelected(ProductCategory selectedProductSubCategory);
    }

}
