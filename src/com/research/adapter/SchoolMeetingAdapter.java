package com.research.adapter;  
  
import java.util.ArrayList;  

import com.research.R;
import com.research.RoomDetailActivity;
import com.research.SchoolDetailActivity;
import com.research.SchoolMeetingDetailActivity;
import com.research.UserInfoActivity;
import com.research.Entity.SchoolMeeting;
import com.research.global.ImageLoader;
  
import android.content.Context;  
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;  
import android.view.Gravity;  
import android.view.LayoutInflater;  
import android.view.View;  
import android.view.ViewGroup;  
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;  
import android.widget.ImageView;  
import android.widget.LinearLayout;  
import android.widget.TextView;  
  
public class SchoolMeetingAdapter extends BaseAdapter {  
    private ArrayList<String> mNameList = new ArrayList<String>();  
    private ArrayList<Drawable> mDrawableList = new ArrayList<Drawable>();  
    private LayoutInflater mInflater;  
    private Context mContext;  
    LinearLayout.LayoutParams params;
	private ArrayList<SchoolMeeting> mSchoolMeetingList;  
	
	private ImageLoader mImageLoader;
  
    public SchoolMeetingAdapter(Context context, ArrayList<SchoolMeeting> schoolMeetingList) {  
//        mNameList = nameList;  
//        mDrawableList = drawableList;  
        this.mSchoolMeetingList = schoolMeetingList;
        this.mContext = context;  
        this.mInflater = LayoutInflater.from(context);  
          
        this.params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
        params.gravity = Gravity.CENTER;  
        
        this.mImageLoader = new ImageLoader();
    }  
  
    public int getCount() {  
        return this.mSchoolMeetingList.size();  
    }  
  
    public Object getItem(int position) {  
        return this.mSchoolMeetingList.get(position);  
    }  
  
    public long getItemId(int position) {  
        return position;  
    }  
  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ItemViewTag viewTag;  
          
        if (convertView == null)  
        {  
            convertView = mInflater.inflate(R.layout.school_all_item, null);  
              
            // construct an item tag   
            viewTag = new ItemViewTag((ImageView) convertView.findViewById(R.id.grid_icon), (TextView) convertView.findViewById(R.id.grid_name));  
            convertView.setTag(viewTag);  
        } else  
        {  
            viewTag = (ItemViewTag) convertView.getTag();  
        }  
          
        // set name   
        viewTag.mName.setText(this.mSchoolMeetingList.get(position).name);  
          
        viewTag.mIcon.setTag(this.mSchoolMeetingList.get(position).icon);
        Resources res = this.mContext.getResources();  
        Drawable drawable = res.getDrawable(R.drawable.xiaobiao_sanda);
        BitmapDrawable bd = (BitmapDrawable) drawable;
        Bitmap bitmap = bd.getBitmap();
        viewTag.mIcon.setImageBitmap(bitmap);
        
		mImageLoader.getBitmap(mContext, viewTag.mIcon,null,this.mSchoolMeetingList.get(position).icon,0,false,true);
        // set icon   
        //viewTag.mIcon.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.xiaobiao_sanda)); 
        
        //viewTag.mIcon.setLayoutParams(params); 
        
        final String schId = this.mSchoolMeetingList.get(position).id;
        convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent userIntent = new Intent();
				userIntent.setClass(mContext, SchoolDetailActivity.class);
				userIntent.putExtra("type",2);
				userIntent.putExtra("schId",schId);
				userIntent.putExtra("schoolName", "三峡大学");
				if(userIntent!=null){
					mContext.startActivity(userIntent);
				}

			}
		});
        return convertView;  
    }  
      
    class ItemViewTag  
    {  
        protected ImageView mIcon;  
        protected TextView mName;  
          
        /** 
         * The constructor to construct a navigation view tag 
         *  
         * @param name 
         *            the name view of the item 
         * @param size 
         *            the size view of the item 
         * @param icon 
         *            the icon view of the item 
         */  
        public ItemViewTag(ImageView icon, TextView name)  
        {  
            this.mName = name;  
            this.mIcon = icon;  
        }  
    }  
  
}  