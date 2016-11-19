package com.research.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.research.ChatMainActivity;
import com.research.R;
import com.research.RoomDetailActivity;
import com.research.SchoolDetailActivity;
import com.research.UserInfoActivity;
import com.research.Entity.Login;
import com.research.Entity.Session;
import com.research.global.FeatureFunction;
import com.research.global.ImageLoader;
import com.research.global.ResearchCommon;

/**
 * 交友墙适配器
 * @author dzf
 *
 */
public class MakeFriendAdapter extends BaseAdapter{

	private final LayoutInflater mInflater;
	private List<Login> mData;
	private Context mContext;
	private int mFromType;
	
	private ImageLoader mImageLoader;
	public MakeFriendAdapter(Context context, List<Login> data,int fromType){
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mData = data;
		mFromType = fromType;
		this.mImageLoader = new ImageLoader();
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;  
		final int index = position;
		if (convertView==null) {  
			convertView=mInflater.inflate(R.layout.heard_name_item, null);   
			holder=new ViewHolder();  

			holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.user_name);
//			holder.mSexTextView = (TextView) convertView.findViewById(R.id.sex);
			holder.mSexImageView = (ImageView) convertView.findViewById(R.id.sex_image);
			holder.mOldTextView = (TextView) convertView.findViewById(R.id.old);
			holder.mHeaderView = (ImageView) convertView.findViewById(R.id.head_img);
			holder.mXuanyanTextView = (TextView) convertView.findViewById(R.id.mk_xy_tv);
			
			convertView.setTag(holder);  
//			hashMap.put(position, convertView);
		}else {
			holder=(ViewHolder) convertView.getTag();  
		}
		
		Login ml = mData.get(index);
		holder.mUserNameTextView.setText(ml.nickname);
//		if (ml.gender == 0){
//			holder.mSexTextView.setText("男");
//		}else if (ml.gender == 1){
//			holder.mSexTextView.setText("女");
//		}
		if(ml.gender == 0 || ml.gender == 2){
			holder.mSexImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.boy));
		}else if(ml.gender == 1){
			holder.mSexImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.girl));
		}
		holder.mOldTextView.setText(ml.old + "岁");
		holder.mXuanyanTextView.setText(ml.makeFriendXuanyan);
		
		if(ml.headsmall != null && !ml.headsmall.equals("")){
			mImageLoader.getBitmap(mContext, holder.mHeaderView, null, ml.headsmall, 0, false, true);
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, UserInfoActivity.class);
				intent.putExtra("user", mData.get(index));
				intent.putExtra("from", "MakeFriendActivity");
				mContext.startActivity(intent);

			}
		});
		return convertView;
	}


	final static class ViewHolder {  
		TextView mUserNameTextView;  
		TextView mOldTextView;
//		TextView mSexTextView;
		ImageView mSexImageView;
		ImageView mHeaderView;
		TextView mXuanyanTextView;
	} 

}
