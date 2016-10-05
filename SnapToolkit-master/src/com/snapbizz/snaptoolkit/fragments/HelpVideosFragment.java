package com.snapbizz.snaptoolkit.fragments;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayAdapterView.OnItemClickListener;
import com.jess.ui.TwoWayGridView;
import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.adapters.HelpVideosAdapter;
import com.snapbizz.snaptoolkit.interfaces.OnActionbarNavigationListener;
import com.snapbizz.snaptoolkit.utils.SnapCommonUtils;
import com.snapbizz.snaptoolkit.utils.SnapToolkitConstants;

import fr.maxcom.libmedia.Licensing;

public class HelpVideosFragment extends Fragment{

    private TwoWayAdapterView helpVideosGrid;
    private HelpVideosAdapter adapter;
    private VideoView mVideo;
    public static int FRAGMENT_TYPE_BILLING=1;
    public static int FRAGMENT_TYPE_INVENTORY=2;
    public static int FRAGMENT_TYPE_PUSH_OFFERS=3;
    private int fragmentType;
	private OnActionbarNavigationListener onActionbarNavigationListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    public void setFragmentType(int fragmentType) {
        this.fragmentType = fragmentType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_help_videos,container, false);
        helpVideosGrid = (TwoWayGridView) view.findViewById(R.id.help_videos_gridview);
        return view;
    }
    
    public boolean isVideoPlaying(){
        if (mVideo!=null && mVideo.isShown())
            return true;
        else
            return false;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar actionbar = getActivity().getActionBar();
        if(!actionbar.isShowing())
            actionbar.show();
        actionbar.setCustomView(R.layout.actionbar_layout);
        ((TextView)getActivity().findViewById(R.id.actionbar_header)).setText("Help Videos");
        Licensing.allow(SnapCommonUtils.getSnapContext(getActivity()));
        ArrayList<String> myList = new ArrayList<String>(); 
        File file=null;
        if(fragmentType==FRAGMENT_TYPE_BILLING)
         file = new File(SnapToolkitConstants.HELP_VIDEO_BILLING_PATH);
        else if(fragmentType==FRAGMENT_TYPE_INVENTORY)
             file = new File(SnapToolkitConstants.HELP_VIDEO_INVENTORY_PATH);
        else if(fragmentType==FRAGMENT_TYPE_PUSH_OFFERS)
             file = new File(SnapToolkitConstants.HELP_VIDEO_PUSHOFFERS_PATH);
        else
            file = new File(SnapToolkitConstants.HELP_VIDEO_PATH);
     
        File list[] = file.listFiles();
    //    getActivity().findViewById(R.id.help_video_relative_layout).setOnClickListener(dismissVideoListener);
        if(list!=null){
        for( int i=0; i< list.length; i++)
        {
                myList.add(list[i].getName());
        }
         adapter = new HelpVideosAdapter(getActivity(),android.R.id.text1 , myList);
        helpVideosGrid.setAdapter(adapter);
        helpVideosGrid.setOnItemClickListener(helpVideoClickListener);
        }else{
            Toast.makeText(getActivity(), "Sorry no videos found", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof OnActionbarNavigationListener))
			throw new ClassCastException(activity.getLocalClassName()
					+ " must implement OnActionbarNavigationListener");
		onActionbarNavigationListener = (OnActionbarNavigationListener) activity;
	}
    
    public OnClickListener dismissVideoListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            getActivity().findViewById(R.id.help_video_relative_layout).setVisibility(View.GONE);
            if(mVideo!=null){
            mVideo.stopPlayback();
            mVideo.destroyDrawingCache();
            mVideo = null;
            }
        }
    };
    

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		//menu.findItem(R.id.help_videos_menuitem).setVisible(false);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		onActionbarNavigationListener.onActionbarNavigation(getTag(),
				menuItem.getItemId());
		return true;
	}
    
    OnItemClickListener helpVideoClickListener = new OnItemClickListener() {


        @Override
        public void onItemClick(TwoWayAdapterView<?> parent, View view,
                int position, long id) {
            getActivity().findViewById(R.id.help_video_relative_layout).setVisibility(View.VISIBLE);
            String path = null;
            if(fragmentType==FRAGMENT_TYPE_BILLING)
            path = SnapCommonUtils.decryptAndPlayVideo(adapter.getItem(position),SnapToolkitConstants.HELP_VIDEO_BILLING_PATH);
            else if(fragmentType==FRAGMENT_TYPE_INVENTORY)
            path = SnapCommonUtils.decryptAndPlayVideo(adapter.getItem(position),SnapToolkitConstants.HELP_VIDEO_INVENTORY_PATH);
            else if(fragmentType==FRAGMENT_TYPE_PUSH_OFFERS)
            path = SnapCommonUtils.decryptAndPlayVideo(adapter.getItem(position),SnapToolkitConstants.HELP_VIDEO_PUSHOFFERS_PATH);

            mVideo = (VideoView) getActivity().findViewById(R.id.video_view1);
            DisplayMetrics metrics = new DisplayMetrics(); 
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) mVideo.getLayoutParams();
            params.width =  metrics.widthPixels;
            params.height = metrics.heightPixels;
            params.leftMargin = 0;
            mVideo.setLayoutParams(params);
            mVideo.setVideoPath(path);
            MediaController ctlr = new MediaController(
                    getActivity());
            ctlr.setMediaPlayer(mVideo);
            mVideo.setMediaController(ctlr);
            // mVideo.setDataSource(TestActivity.this ,)
            mVideo.start();
        }
    };
    
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
