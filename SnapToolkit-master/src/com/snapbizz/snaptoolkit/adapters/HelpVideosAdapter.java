package com.snapbizz.snaptoolkit.adapters;

import java.util.List;

import android.app.DownloadManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapbizz.snaptoolkit.R;
import com.snapbizz.snaptoolkit.utils.SnapToolkitTextFormatter;

public class HelpVideosAdapter extends ArrayAdapter<String>{


    private LayoutInflater layoutInflater;
    private Context context;
    DownloadManager dm;
    
    public HelpVideosAdapter(Context context, int textViewResourceId,
            List<String> objects) {
        super(context, textViewResourceId, objects);
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HelpVideosAdapterWrapper helpVideosAdapterWrapper;
        if(convertView == null) {
            convertView = layoutInflater.inflate(R.layout.griditem_help_video, parent,false);
            helpVideosAdapterWrapper = new HelpVideosAdapterWrapper();
            helpVideosAdapterWrapper.videoNameTextView = (TextView) convertView.findViewById(R.id.product_name_textview);
            helpVideosAdapterWrapper.videoImageView = (ImageView) convertView.findViewById(R.id.product_imageview);
            convertView.setTag(helpVideosAdapterWrapper);
        } else {
            helpVideosAdapterWrapper = (HelpVideosAdapterWrapper) convertView.getTag();
        }
      //  Bitmap bitmap=ThumbnailUtils.createVideoThumbnail(SnapToolkitConstants.VIDEO_PATH+getItem(position), Thumbnails.MINI_KIND);
        String name=getItem(position).replace("_", " ");
         name=name.replace("enc.mp4", "");
        helpVideosAdapterWrapper.videoNameTextView.setText(SnapToolkitTextFormatter.capitalseText(name));
        return convertView;   
        }
    
    private static class HelpVideosAdapterWrapper {
        public ImageView videoImageView;
        public TextView videoNameTextView;
    }

}
