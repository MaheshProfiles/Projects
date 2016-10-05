/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.snapbizz.snapbilling.fragments;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Presentation;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
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

/**
 * <p>
 * A {@link android.app.Presentation} used to demonstrate interaction between
 * primary and secondary screens.
 * </p>
 * <p>
 * It displays the name of the display in which it has been embedded (see
 * {@link android.app.Presentation#getDisplay()}) and exposes a facility to
 * change its background color and display its text.
 * </p>
 */
@SuppressLint("NewApi")
public class VisibilityPresentation extends Presentation implements
		OnQueryCompleteListener {

	private final int GET_PRODUCT_OFFERS_TASKCODE = 3;
	private ArrayList<VisibilityPage> pages;
	private static int pageGap = 0;
	private Context context;
	private Handler switcherHandler = new Handler();
	private int pageNumber = 0;
	private AnimatorSet animatorSet=null;
	private Random randomGenerator = new Random();
	private VisibilityItem visibilityItem=null;
	private LinearLayout visiblity_layout = null;
	private RelativeLayout layout_campaign = null;
	@Override
	public void onStop() {
		switcherHandler.removeCallbacks(pageSwitchRunnable);
		visiblity_layout = null;
		layout_campaign = null;
		animatorSet=null;
		super.onStop();
	};

	Runnable pageSwitchRunnable = new Runnable() {

		@Override
		public void run() {
			animatorSet = new AnimatorSet();
			visiblity_layout = (LinearLayout)findViewById(R.id.visiblity_layout);
			layout_campaign = (RelativeLayout)findViewById(R.id.layout_campaign);
			View view1 = findViewById(R.id.visibility_item1_relativelayout);
			View view2 = findViewById(R.id.visibility_item2_relativelayout);
			View view3 = findViewById(R.id.visibility_item3_relativelayout);
			if (pages.size() > 0) {
				VisibilityPage page = pages.get(pageNumber);
				if (page.getPageType() == VisibilityPage.PAGE_TYPE_OFFERS) {
					findViewById(R.id.visiblity_layout).setBackgroundResource(
							R.color.default_background_color);
				} else if (page.getPageType() == VisibilityPage.PAGE_TYPE_SUGGESTIONS) {

					findViewById(R.id.visiblity_layout).setBackgroundResource(
							R.color.default_background_color);
				} else if (page.getPageType() == VisibilityPage.PAGE_TYPE_FORGET) {

					findViewById(R.id.visiblity_layout).setBackgroundResource(
							R.drawable.forgotten_products_bg);
				} else if (page.getPageType() == VisibilityPage.PAGE_TYPE_STORE) {
					Log.d(" page gap store change ", pageGap + "");
					findViewById(R.id.visiblity_layout).setBackgroundResource(
							R.color.default_background_color);
				}

				switch (page.getProducts().size()) {
				case 3:
					view3.setVisibility(View.VISIBLE);
					view2.setVisibility(View.VISIBLE);
					((LinearLayout) findViewById(R.id.visiblity_layout))
							.setWeightSum(3);
					animateView(view1, page, 0);
					animateView(view2, page, 2);
					animateView(view3, page, 1);
					break;
				case 2:
					view3.setVisibility(View.VISIBLE);
					view2.setVisibility(View.GONE);
					((LinearLayout) findViewById(R.id.visiblity_layout))
							.setWeightSum(2);
					animateView(view1, page, 0);
					animateView(view3, page, 1);
					break;
				case 1:
					view3.setVisibility(View.GONE);
					view2.setVisibility(View.GONE);
					((LinearLayout) findViewById(R.id.visiblity_layout))
							.setWeightSum(2);
					animateView(view1, page, 0);
					break;
				}

				pageNumber++;
				if (pageNumber >= pages.size()) {
					pageNumber = 0;
				}
			}
			switcherHandler.removeCallbacks(pageSwitchRunnable);
			switcherHandler.postDelayed(pageSwitchRunnable, 5000);
		}
	};

	@SuppressWarnings("deprecation")
	public void animateView(View view, VisibilityPage page, int position) {

	    visibilityItem = page.getProducts().get(position);
	    NumberFormat formatter = new DecimalFormat(SnapToolkitConstants.PRICE_FORMAT);
	    if (visibilityItem.getProductCode().toLowerCase().contains(
				SnapToolkitConstants.SNAP_MYSCREEN_PREFIX_KEY.toLowerCase())) {
	    	Log.d("TAG","getSmartStoreList------> inside store block");
			visiblity_layout.setVisibility(View.GONE);
			layout_campaign.setVisibility(View.VISIBLE);
			view = layout_campaign;
			view.setBackgroundDrawable(SnapCommonUtils.getProductLargeDrawable(SnapToolkitConstants.VISIBILITY_IMAGE_PATH+"mystore/"+"storeimage.png", context));
		
		} else if (visibilityItem.getProductCode().toLowerCase().contains(
				SnapToolkitConstants.SNAP_LOCAL_CAMPAIGN_PREFIX_KEY.toLowerCase())||visibilityItem.getProductCode().toLowerCase().contains(
						SnapToolkitConstants.SNAP_CAMPAIGN_PREFIX_KEY.toLowerCase())) {
			visiblity_layout.setVisibility(View.GONE);
			layout_campaign.setVisibility(View.VISIBLE);
			view = layout_campaign;
			visibilityItem = page.getProducts().get(0);
			if (visibilityItem.getProductCode().contains(
					SnapToolkitConstants.SNAP_LOCAL_CAMPAIGN_PREFIX_KEY)){view.setBackgroundDrawable(SnapCommonUtils.getProductLargeDrawable(
					visibilityItem.getImageUrl(), context));
			}else{
				view.setBackgroundDrawable(SnapCommonUtils.getProductLargeDrawable(SnapToolkitConstants.VISIBILITY_IMAGE_PATH+"campaign/"+
						visibilityItem.getImageUrl(), context));
			}
		} else {
			visiblity_layout.setVisibility(View.VISIBLE);
			layout_campaign.setVisibility(View.GONE);
			if (page.getPageType() == VisibilityPage.PAGE_TYPE_FORGET)
				view.setBackgroundDrawable(SnapCommonUtils
						.getForgottenProdDrawable(
								visibilityItem.getProductName(), context));
			else
				view.setBackgroundDrawable(SnapCommonUtils
						.getProductLargeDrawable(visibilityItem.getImageUrl(),
								context));
			float priceDiff = visibilityItem.getProductMrp()
					- visibilityItem.getProductSalePrice();
			((TextView) view.findViewById(R.id.product_name_visibility_textview))
							.setText(visibilityItem.getProductName());
			if (SnapSharedUtils.getOfferVisible(context)) {
				((TextView) view.findViewById(R.id.pricemrp_textview)).setVisibility(View.VISIBLE);
				((TextView) view.findViewById(R.id.pricesp_textview)).setVisibility(View.VISIBLE);
				((TextView) view.findViewById(R.id.pricesp_textview))
							.setText(formatter.format(visibilityItem.getProductSalePrice()));
			} else {
				((TextView) view.findViewById(R.id.pricesp_textview)).setVisibility(View.GONE);
				((TextView) view.findViewById(R.id.offer_textview)).setVisibility(View.GONE);
				view.findViewById(R.id.price_layout).setVisibility(View.GONE);
				((TextView) view.findViewById(R.id.pricemrp_textview)).setVisibility(View.GONE);
			}
			if (visibilityItem.isOffer() && SnapSharedUtils.getOfferVisible(context)) {
				view.findViewById(R.id.price_layout).setVisibility(View.VISIBLE);
				displayOfferTags(priceDiff, view);
			} else {
				if (page.getPageType() == VisibilityPage.PAGE_TYPE_FORGET) {
					view.findViewById(R.id.price_layout).setVisibility(View.GONE);
				} else {
					if (SnapSharedUtils.getOfferVisible(context)) {
						view.findViewById(R.id.price_layout).setVisibility(View.VISIBLE);
						displayOfferTags(priceDiff, view);
					}
				}
			}
		}
		if (position == 0)
			randomAnimateViewLeft(view);
		else if (position == 1)
			randomAnimateViewRight(view);
		else
			randomAnimateViewCentre(view);
		
		visibilityItem=null;
	}

	public void randomAnimateViewLeft(View view) {
		String translation = context.getString(R.string.translation_x);
		int rand = randomGenerator.nextInt(2);
		if (rand == 1)
			translation = context.getString(R.string.translation_y);
		ObjectAnimator mover = ObjectAnimator.ofFloat(view, translation, -500f,
				0f);
		mover.setDuration(1000);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, context.getString(R.string.alpha), 0f, 1f);
		fadeIn.setDuration(1000);
		animatorSet.play(mover).with(fadeIn).after(0);
		animatorSet.start();
	}

	public void randomAnimateViewRight(View view) {
		String translation = context.getString(R.string.translation_x);
		int rand = randomGenerator.nextInt(2);
		if (rand == 1)
			translation = context.getString(R.string.translation_y);
		ObjectAnimator mover = ObjectAnimator.ofFloat(view, translation, 500f,
				0f);
		mover.setDuration(1000);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, context.getString(R.string.alpha), 0f, 1f);
		fadeIn.setDuration(1000);
		animatorSet.play(mover).with(fadeIn).after(0);
		animatorSet.start();
	}

	public void randomAnimateViewCentre(View view) {
		String translation = context.getString(R.string.translation_y);
		int rand = randomGenerator.nextInt(2);
		float val = 500f;
		if (rand == 1)
			val = -500f;
		ObjectAnimator mover = ObjectAnimator.ofFloat(view, translation, val,
				0f);
		mover.setDuration(1000);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, context.getString(R.string.alpha), 0f, 1f);
		fadeIn.setDuration(1000);
		animatorSet.play(mover).with(fadeIn).after(0);
		animatorSet.start();
	}

	public void displayOfferTags(float priceDiff, View view) {
		NumberFormat formatter = new DecimalFormat(SnapToolkitConstants.PRICE_FORMAT);
		TextView offerTextView = ((TextView) view.findViewById(R.id.offer_textview));
		offerTextView.setVisibility(View.VISIBLE);
		float discountPercentage = priceDiff * 100 / visibilityItem.getProductMrp();
		if (priceDiff > 0) {
			if (visibilityItem.getProductSalePrice() == 0)
				offerTextView.setVisibility(View.GONE);
			else
				offerTextView.setVisibility(View.VISIBLE);
			offerTextView.setText(((int) discountPercentage) + "%");
			if (discountPercentage <= 5) {
				offerTextView.setVisibility(View.VISIBLE);
				offerTextView.setBackgroundResource(R.drawable.offer_badge_green);
				view.findViewById(R.id.price_layout)
						.setBackgroundResource(
								R.drawable.price_tag_yellow);
			} else if (discountPercentage <= 10) {
				offerTextView.setVisibility(View.VISIBLE);
				offerTextView.setBackgroundResource(R.drawable.offer_badge_yellow);
				view.findViewById(R.id.price_layout)
						.setBackgroundResource(
								R.drawable.price_tag_yellow);
			} else {
				offerTextView.setVisibility(View.VISIBLE);
				offerTextView.setBackgroundResource(R.drawable.offer_badge_red);
				view.findViewById(R.id.price_layout)
						.setBackgroundResource(
								R.drawable.price_tag_grey);
			}
			((TextView) view.findViewById(R.id.pricemrp_textview))
					.setText(formatter.format(visibilityItem.getProductMrp()) + "");
			((TextView) view.findViewById(R.id.pricemrp_textview))
					.setPaintFlags(((TextView) view
							.findViewById(R.id.pricemrp_textview))
							.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
			if(discountPercentage < 5) {
				offerTextView.setText("Save\n"
						+ context.getString(R.string.rupee_symbol) + formatter.format(priceDiff));
			} else {
				String offer = context.getString(R.string.offer);
				if (visibilityItem.getProductSalePrice() == 0)
					offerTextView.setText(offer);
				else
					offerTextView.setText(offer + "\n"
							+ ((int) discountPercentage) + "%");
			}
		} else {
			view.findViewById(R.id.price_layout).setBackgroundResource(
					R.drawable.price_tag_green);
			((TextView) view.findViewById(R.id.pricemrp_textview))
					.setText("");
			offerTextView.setVisibility(View.GONE);
		}
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(
				offerTextView, context.getString(R.string.scale_x), 1, 1.15f);
		scaleX.setDuration(700);
		scaleX.setRepeatCount(5);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(
				offerTextView, context.getString(R.string.scale_y), 1, 1.15f);
		scaleY.setDuration(700);
		scaleY.setRepeatCount(5);
		animatorSet.play(scaleY).with(scaleX).after(0);
	}
	
	public VisibilityPresentation(Context outerContext, Display display) {
		super(outerContext, display);
		this.context = outerContext;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the content view to the custom layout
		setContentView(R.layout.presentation_visibility);
		pages = new ArrayList<VisibilityPage>();
		pageGap = 0;
		pageNumber = 0;
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

		new QueryVisibilityOffersTask(context, this,
				GET_PRODUCT_OFFERS_TASKCODE).execute();
	}

	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		return true;
	}

	@Override
	public void onTaskSuccess(Object responseList, int taskCode) {
		try {
			@SuppressWarnings("unchecked")
			ArrayList<ArrayList<InventorySku>> totalItemsList = (ArrayList<ArrayList<InventorySku>>) responseList;
			Log.d("Capmaign",
					"Capmaign---totalItemsList->" + totalItemsList.size());
			HashMap<String, String> prodIds = new HashMap<String, String>();
			for (int i = 0; i < totalItemsList.size(); i++) {
				ArrayList<InventorySku> inventoryList = totalItemsList.get(i);
				if (inventoryList.size() > 0
						&& inventoryList.get(0).getProductSku() != null) {
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
					products=null;
				}
			}
			SnapSharedUtils.storeVisibilityOfferProducts(context, prodIds);
			switcherHandler.post(pageSwitchRunnable);
			pageGap = pageGap + totalItemsList.size();

			totalItemsList=null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTaskError(String errorMessage, int taskCode) {
	}

}
