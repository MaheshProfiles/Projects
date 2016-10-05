package com.snapbizz.snapbilling.activity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.asynctasks.QueryVisibilityOffersTask;
import com.snapbizz.snapbilling.domains.VisibilityItem;
import com.snapbizz.snapbilling.domains.VisibilityPage;
import com.snapbizz.snaptoolkit.domains.InventorySku;
import com.snapbizz.snaptoolkit.interfaces.OnQueryCompleteListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapSharedUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;


public class SlideshowService extends PresentationService implements
		OnQueryCompleteListener {
	private Random randomGenerator = null;
	private final int GET_PRODUCT_OFFERS_TASKCODE = 3;
	
	private ArrayList<VisibilityPage> pages = null;
	private static int pageGap = 0;
	
	private Handler switcherHandler = null;
	private int pageNumber = 0;
	private AnimatorSet animatorSet;
	private LinearLayout visiblity_layout = null;
	private RelativeLayout layout_campaign = null;
	private RelativeLayout visibility_item1_relativelayout = null;
	private RelativeLayout visibility_item2_relativelayout = null;
	private RelativeLayout visibility_item3_relativelayout = null;
    private VisibilityItem visibilityItem;
	@Override
	public void onCreate() {
		super.onCreate();
		new QueryVisibilityOffersTask(this, this, GET_PRODUCT_OFFERS_TASKCODE)
				.execute();
	}

	@Override
	protected int getThemeId() {
		return (R.style.AppTheme);
	}

	@Override
	protected View buildPresoView(Context ctxt, LayoutInflater inflater) {
		View parentLayout=null;
		
		try {
			parentLayout = inflater.inflate(R.layout.presentation_visibility,null);
			visiblity_layout = (LinearLayout) parentLayout
					.findViewById(R.id.visiblity_layout);
			layout_campaign = (RelativeLayout) parentLayout
					.findViewById(R.id.layout_campaign);
			visibility_item1_relativelayout = (RelativeLayout) parentLayout
					.findViewById(R.id.visibility_item1_relativelayout);
			visibility_item2_relativelayout = (RelativeLayout) parentLayout
					.findViewById(R.id.visibility_item2_relativelayout);
			visibility_item3_relativelayout = (RelativeLayout) parentLayout
					.findViewById(R.id.visibility_item3_relativelayout);
			switcherHandler = new Handler();
			animatorSet = new AnimatorSet();
			randomGenerator = new Random();
			pages = new ArrayList<VisibilityPage>();
		} catch (IllegalStateException e) {}
		
		return (parentLayout);
	}

	@Override
	public void onDestroy() {
		switcherHandler.removeCallbacks(pageSwitchRunnable);
		super.onDestroy();
	}

	public void randomAnimateViewLeft(View view) {
		String translation = getString(R.string.translation_x);
		int rand = randomGenerator.nextInt(2);
		if (rand == 1)
			translation = getString(R.string.translation_y);
		ObjectAnimator mover = ObjectAnimator.ofFloat(view, translation, -500f,
				0f);
		mover.setDuration(m_slideShowDuration);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, getString(R.string.alpha), 0f, 1f);
		fadeIn.setDuration(m_slideShowDuration);
		animatorSet.play(mover).with(fadeIn).after(0);
		animatorSet.start();
	}

	public void randomAnimateViewRight(View view) {
		String translation = getString(R.string.translation_x);
		int rand = randomGenerator.nextInt(2);
		if (rand == 1)
			translation = getString(R.string.translation_y);
		ObjectAnimator mover = ObjectAnimator.ofFloat(view, translation, 500f,
				0f);
		mover.setDuration(m_slideShowDuration);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, getString(R.string.alpha), 0f, 1f);
		fadeIn.setDuration(m_slideShowDuration);
		animatorSet.play(mover).with(fadeIn).after(0);
		animatorSet.start();
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		try {
			@SuppressWarnings("unchecked")
			
			ArrayList<ArrayList<InventorySku>> totalItemsList = (ArrayList<ArrayList<InventorySku>>) responseList;
			Log.d("Capmaign", "Capmaign---totalItemsList->"+totalItemsList.size());
			HashMap<String, String> prodIds = new HashMap<String, String>();
			for (int i = 0; i < totalItemsList.size(); i++) {
				ArrayList<InventorySku> inventoryList = totalItemsList.get(i);
				if (inventoryList.size()>0&&inventoryList.get(0).getProductSku() != null) {
					ArrayList<VisibilityItem> products = new ArrayList<VisibilityItem>();
					for (int j = 0; j < inventoryList.size(); j++) {
						prodIds.put(inventoryList.get(j).getProductSku()
								.getProductSkuCode(), "");
						products.add(new VisibilityItem(inventoryList.get(j)
								.getProductSku().getProductSkuCode(),
								inventoryList.get(j).getProductSku()
										.getProductSkuName(), inventoryList
										.get(j).getProductSku().getImageUrl(),
								inventoryList.get(j).getProductSku()
										.getProductSkuMrp(), inventoryList
										.get(j).getProductSku()
										.getProductSkuSalePrice(),
								inventoryList.get(j).isOffer()));
					}

					pages.add(new VisibilityPage(products,
							VisibilityPage.PAGE_TYPE_OFFERS));
				}
			}
			SnapSharedUtils.storeVisibilityOfferProducts(this, prodIds);
			switcherHandler.post(pageSwitchRunnable);
			pageGap = pageGap + totalItemsList.size();

			long seed = System.nanoTime();
			Collections.shuffle(pages, new Random(seed));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {

	}

	Runnable pageSwitchRunnable = new Runnable() {

		@Override
		public void run() {
			try {			
				android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
				java.util.Date date= new java.util.Date();
				Timestamp startTimestamp =new Timestamp(date.getTime());
				animatorSet = new AnimatorSet();
				visiblity_layout.setVisibility(View.VISIBLE);
				VisibilityPage page = pages.get(pageNumber);
				if (page.getPageType() == VisibilityPage.PAGE_TYPE_OFFERS) {
					visiblity_layout
							.setBackgroundResource(R.color.default_background_color);
				} else if (page.getPageType() == VisibilityPage.PAGE_TYPE_SUGGESTIONS) {
					visiblity_layout
							.setBackgroundResource(R.color.default_background_color);
				} else if (page.getPageType() == VisibilityPage.PAGE_TYPE_FORGET) {
					visiblity_layout
							.setBackgroundResource(R.drawable.forgotten_products_bg);
				} else if (page.getPageType() == VisibilityPage.PAGE_TYPE_STORE) {
					Log.d(" page gap store change ", pageGap + "");
					visiblity_layout
							.setBackgroundResource(R.color.default_background_color);
				}

				switch (page.getProducts().size()) {
				case 3:
					visibility_item3_relativelayout.setVisibility(View.VISIBLE);
					visibility_item2_relativelayout.setVisibility(View.VISIBLE);
					visiblity_layout.setWeightSum(3);
					animateView(visibility_item1_relativelayout, page, 0);
					animateView(visibility_item2_relativelayout, page, 2);
					animateView(visibility_item3_relativelayout, page, 1);
					break;
				case 2:
					visibility_item3_relativelayout.setVisibility(View.VISIBLE);
					visibility_item2_relativelayout.setVisibility(View.GONE);
					visiblity_layout.setWeightSum(2);
					animateView(visibility_item1_relativelayout, page, 0);
					animateView(visibility_item3_relativelayout, page, 1);
					break;
				case 1:
					visibility_item3_relativelayout.setVisibility(View.GONE);
					visibility_item2_relativelayout.setVisibility(View.GONE);
					visiblity_layout.setWeightSum(2);
					animateView(visibility_item1_relativelayout, page, 0);
					break;
				}

				pageNumber++;
				if (pageNumber >= pages.size()) {
					pageNumber = 0;
					long seed = System.nanoTime();
					Collections.shuffle(pages, new Random(seed));
				}
				date= new java.util.Date();
				Timestamp endTimestamp =new Timestamp(date.getTime());
				long diff = endTimestamp.getTime() - startTimestamp.getTime();
				Log.e("VISIBILITY", "VISIBILITY ---->"+diff);
				switcherHandler.removeCallbacks(pageSwitchRunnable);
				
				switcherHandler.postDelayed(pageSwitchRunnable, 5000);
			} catch (Exception e) {
				 visiblity_layout.setVisibility(View.GONE);
				 if(switcherHandler!=null){
                 switcherHandler.removeCallbacks(pageSwitchRunnable);
				 }
				 
			}
		}
	};

	@SuppressWarnings("deprecation")
	public void animateView(View view, VisibilityPage page, int position) {
	    visibilityItem = page.getProducts().get(position);
	    Log.d("TAG","getSmartStoreList------> inside store block--"+visibilityItem.getProductCode());
	    if (visibilityItem.getProductCode().toLowerCase().contains(
				SnapToolkitConstants.SNAP_MYSCREEN_PREFIX_KEY.toLowerCase())) {
	    	Log.d("TAG","getSmartStoreList------> inside store block");
			visiblity_layout.setVisibility(View.GONE);
			layout_campaign.setVisibility(View.VISIBLE);
			view = layout_campaign;
			view.setBackgroundDrawable(SnapCommonUtils.getProductLargeDrawable(SnapToolkitConstants.VISIBILITY_IMAGE_PATH+"mystore/"+"storeimage.png", this));
		
		} else if (visibilityItem.getProductCode().toLowerCase().contains(
				SnapToolkitConstants.SNAP_LOCAL_CAMPAIGN_PREFIX_KEY.toLowerCase())||visibilityItem.getProductCode().toLowerCase().contains(
						SnapToolkitConstants.SNAP_CAMPAIGN_PREFIX_KEY.toLowerCase())) {
			visiblity_layout.setVisibility(View.GONE);
			layout_campaign.setVisibility(View.VISIBLE);
			view = layout_campaign;
			visibilityItem = page.getProducts().get(0);
			if (visibilityItem.getProductCode().contains(
					SnapToolkitConstants.SNAP_LOCAL_CAMPAIGN_PREFIX_KEY)){view.setBackgroundDrawable(SnapCommonUtils.getProductLargeDrawable(
					visibilityItem.getImageUrl(), this));
			}else{
				view.setBackgroundDrawable(SnapCommonUtils.getProductLargeDrawable(SnapToolkitConstants.VISIBILITY_IMAGE_PATH+"campaign/"+
						visibilityItem.getImageUrl(), this));
			}
		} else {
			visiblity_layout.setVisibility(View.VISIBLE);
			layout_campaign.setVisibility(View.GONE);
			if (page.getPageType() == VisibilityPage.PAGE_TYPE_FORGET)
				view.setBackgroundDrawable(SnapCommonUtils
						.getForgottenProdDrawable(
								visibilityItem.getProductName(), this));
			else
				view.setBackgroundDrawable(SnapCommonUtils
						.getProductLargeDrawable(visibilityItem.getImageUrl(),
								this));
			float priceDiff = visibilityItem.getProductMrp()
					- visibilityItem.getProductSalePrice();
			((TextView) view.findViewById(R.id.pricesp_textview))
					.setText(visibilityItem.getProductSalePrice() + "");
			((TextView) view
					.findViewById(R.id.product_name_visibility_textview))
					.setText(visibilityItem.getProductName());
			if (visibilityItem.isOffer()) {
				view.findViewById(R.id.price_layout)
						.setVisibility(View.VISIBLE);
				TextView offerTextView = (TextView) view
						.findViewById(R.id.offer_textview);
				offerTextView.setVisibility(View.VISIBLE);
				if (priceDiff > 0) {
					float discountPercentage = priceDiff * 100
							/ visibilityItem.getProductMrp();
					if (discountPercentage <= 5) {
						offerTextView.setVisibility(View.GONE);
						view.findViewById(R.id.price_layout)
								.setBackgroundResource(
										R.drawable.price_tag_yellow);
					} else if (discountPercentage <= 10) {
						offerTextView.setVisibility(View.VISIBLE);
						offerTextView
								.setBackgroundResource(R.drawable.offer_badge_yellow);
						view.findViewById(R.id.price_layout)
								.setBackgroundResource(
										R.drawable.price_tag_yellow);
					} else {
						offerTextView.setVisibility(View.VISIBLE);
						offerTextView
								.setBackgroundResource(R.drawable.offer_badge_red);
						view.findViewById(R.id.price_layout)
								.setBackgroundResource(
										R.drawable.price_tag_grey);
					}
					((TextView) view.findViewById(R.id.pricemrp_textview))
							.setText(visibilityItem.getProductMrp() + "");
					((TextView) view.findViewById(R.id.pricemrp_textview))
							.setPaintFlags(((TextView) view
									.findViewById(R.id.pricemrp_textview))
									.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);
					if (visibilityItem.getProductSalePrice() == 0)
						offerTextView.setText(getString(R.string.offer));
					else
						offerTextView.setText(getString(R.string.offer) + "\n"
								+ ((int) discountPercentage) + getString(R.string.percentage));
				} else {
					((TextView) view.findViewById(R.id.pricemrp_textview))
							.setText("");
					offerTextView.setText(getString(R.string.offer));
					view.findViewById(R.id.price_layout).setBackgroundResource(
							R.drawable.price_tag_green);
				}
				((TextView) view.findViewById(R.id.save_textview))
						.setVisibility(View.GONE);
				ObjectAnimator scaleX = ObjectAnimator.ofFloat(offerTextView,
						getString(R.string.scale_x), 1, 1.15f);
				scaleX.setDuration(700);
				scaleX.setRepeatCount(5);
				ObjectAnimator scaleY = ObjectAnimator.ofFloat(offerTextView,
						getString(R.string.scale_y), 1, 1.15f);
				scaleY.setDuration(700);
				scaleY.setRepeatCount(5);
				animatorSet.play(scaleY).with(scaleX).after(0);
			} else {
				if (page.getPageType() == VisibilityPage.PAGE_TYPE_FORGET)
					view.findViewById(R.id.price_layout).setVisibility(
							View.GONE);
				else
					view.findViewById(R.id.price_layout).setVisibility(
							View.VISIBLE);
				((TextView) view.findViewById(R.id.offer_textview))
						.setVisibility(View.GONE);
				float discountPercentage = priceDiff * 100
						/ visibilityItem.getProductMrp();
				if (priceDiff > 0) {
					TextView saveTextView = ((TextView) view
							.findViewById(R.id.save_textview));
					if (visibilityItem.getProductSalePrice() == 0)
						saveTextView.setVisibility(View.GONE);
					else
						saveTextView.setVisibility(View.VISIBLE);
					saveTextView.setText(((int) discountPercentage) + getString(R.string.percentage));
					if (discountPercentage <= 10) {
						saveTextView.setVisibility(View.GONE);
						view.findViewById(R.id.price_layout)
								.setBackgroundResource(
										R.drawable.price_tag_yellow);
					} else {
						saveTextView.setVisibility(View.VISIBLE);
						view.findViewById(R.id.price_layout)
								.setBackgroundResource(
										R.drawable.price_tag_grey);
					}
					((TextView) view.findViewById(R.id.pricemrp_textview))
							.setText(visibilityItem.getProductMrp() + "");
					((TextView) view.findViewById(R.id.pricemrp_textview))
							.setPaintFlags(((TextView) view
									.findViewById(R.id.pricemrp_textview))
									.getPaintFlags()
									| Paint.STRIKE_THRU_TEXT_FLAG);

					ObjectAnimator scaleX = ObjectAnimator.ofFloat(
							saveTextView, getString(R.string.scale_x), 1, 1.15f);
					scaleX.setDuration(700);
					scaleX.setRepeatCount(5);
					ObjectAnimator scaleY = ObjectAnimator.ofFloat(
							saveTextView, getString(R.string.scale_y), 1, 1.15f);
					scaleY.setDuration(700);
					scaleY.setRepeatCount(5);
					animatorSet.play(scaleY).with(scaleX).after(0);
				} else {
					view.findViewById(R.id.price_layout).setBackgroundResource(
							R.drawable.price_tag_green);
					((TextView) view.findViewById(R.id.pricemrp_textview))
							.setText("");
					((TextView) view.findViewById(R.id.save_textview))
							.setVisibility(View.GONE);
				}
			}
		}
		if (position == 0)
			randomAnimateViewLeft(view);
		else if (position == 1)
			randomAnimateViewRight(view);
		else
			randomAnimateViewCentre(view);
	}

	public void randomAnimateViewCentre(View view) {
		String translation = getString(R.string.translation_y);
		int rand = randomGenerator.nextInt(2);
		float val = 500f;
		if (rand == 1)
			val = -500f;
		ObjectAnimator mover = ObjectAnimator.ofFloat(view, translation, val,
				0f);
		mover.setDuration(1000);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, getString(R.string.alpha), 0f, 1f);
		fadeIn.setDuration(1000);
		animatorSet.play(mover).with(fadeIn).after(0);
		animatorSet.start();
	}
}
