package com.snapbizz.snapbilling.adapters;

import java.util.ArrayList;
import java.util.Arrays;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.snapbizz.snapbilling.R;
import com.snapbizz.snapbilling.domains.VisibilityPage;
import com.snapbizz.snapbilling.utils.SnapBillingUtils;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;

public class VisibilityPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<VisibilityPage> pages;
    private static int ITEM_HEIGHT = 150;
    private static int ITEM_WIDTH = 175;
    private LayoutParams layoutParams;
    private int[] forgottenProdPages = new int[3];
    int prevposition;
    private LayoutAnimationController animationController;
    private TranslateAnimation translateAnimation;
    private AnimationListener animationListener;
    private LinearLayout visibilityItem;

    public VisibilityPagerAdapter(Context context,
            ArrayList<VisibilityPage> pages) {
        this.context = context;
        this.pages = pages;
        Arrays.fill(forgottenProdPages, -1);
        translateAnimation = new TranslateAnimation(0, 0, 300, 0); 
        translateAnimation.setDuration(500);
        translateAnimation.setFillAfter(true);
        animationController = new LayoutAnimationController(translateAnimation);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public void destroyItem(View collection, int position, Object o) {
        View view = (View) o;
        ((ViewPager) collection).removeView(view);
        view = null;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final LinearLayout rootLayout = (LinearLayout) inflater.inflate(R.layout.visibility_layout, null);
        final GridLayout grid = (GridLayout) rootLayout.findViewById(R.id.visibility_grid_layout);
        grid.setLayoutAnimation(animationController);
        grid.startLayoutAnimation();
        for (int itemCount = 0, colpos = 0; itemCount < pages.get(position).getProducts().size(); itemCount++) {
             visibilityItem = (LinearLayout) inflater.inflate(R.layout.griditem_visiblity, null);
             visibilityItem.setVisibility(View.VISIBLE);
             ImageView mmageView = (ImageView) visibilityItem.findViewById(R.id.product_imageview);
             TextView prodNameTxt = (TextView) visibilityItem.findViewById(R.id.product_name_textview);
             if (pages.get(position).getPageType() == VisibilityPage.PAGE_TYPE_FORGET) {
            	mmageView.setVisibility(View.VISIBLE);
                prodNameTxt.setVisibility(View.INVISIBLE);
                mmageView.setImageDrawable(SnapCommonUtils.getForgottenProdDrawable(pages.get(position).getProducts().get(itemCount).getProductName(), context));
             } else {
            	mmageView.setImageDrawable(SnapBillingUtils.getProductLargeDrawable(pages.get(position).getProducts().get(itemCount).getImageUrl(), context));
                prodNameTxt.setVisibility(View.VISIBLE);
                prodNameTxt.setText(pages.get(position).getProducts().get(itemCount).getProductName());
            }
            layoutParams = new LayoutParams(GridLayout.spec(0),GridLayout.spec(colpos));
            colpos++;
            if (pages.get(position).getPageType() == VisibilityPage.PAGE_TYPE_OFFERS) {
                if (colpos >= 3)
                    colpos = 0;
                layoutParams.height = ITEM_HEIGHT*2;
                layoutParams.setGravity(Gravity.FILL_VERTICAL);
                layoutParams.width = (int) (ITEM_WIDTH * 2.25);
            } else {
                if (colpos >= 2)
                    colpos = 0;
                layoutParams.height = ITEM_HEIGHT * 2;
                layoutParams.setGravity(Gravity.FILL_VERTICAL);
                layoutParams.width = (int) (ITEM_WIDTH * 3.5);
            }
            visibilityItem.setLayoutParams(layoutParams);
            grid.addView(visibilityItem);
        }
        animationListener = new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                visibilityItem.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {}
        };
        translateAnimation.setAnimationListener(animationListener);
        ((ViewPager) container).addView(rootLayout, 0);
        return rootLayout;
    }

    public boolean isViewFromObject(final View view, final Object object) {
        return view == ((LinearLayout) object);
    }

}
