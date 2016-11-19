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
 * 相亲墙适配器
 * @author dzf
 *
 */
public class MakeFriendListAdapter extends BaseAdapter{

	private final LayoutInflater mInflater;
	private List<Login> mData;
	private Context mContext;
	public MakeFriendListAdapter(Context context, List<Login> data){
		mInflater = (LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		mContext = context;
		mData = data;
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
//		convertView = hashMap.get(position);
		ViewHolder holder;  
		final int index = position;
		if (convertView==null) {  
			convertView=mInflater.inflate(R.layout.makefriend_item, null);   
			holder=new ViewHolder();  

			holder.mUserNameTextView = (TextView) convertView.findViewById(R.id.user_name_text_view);
			holder.mSexTextView = (TextView) convertView.findViewById(R.id.user_sex_text_view);
			holder.mOldTextView = (TextView) convertView.findViewById(R.id.user_old_text_view);
			holder.mHeaderView = (ImageView) convertView.findViewById(R.id.heardImageView);
			holder.mAddressView = (TextView) convertView.findViewById(R.id.user_address_text_view);
			
			holder.mSign = (TextView) convertView.findViewById(R.id.qm_content);
			holder.mXqXuanyan = (TextView) convertView.findViewById(R.id.xy_content);
			holder.mXqRequire = (TextView) convertView.findViewById(R.id.yq_content);
			
			convertView.setTag(holder);  
//			hashMap.put(position, convertView);
		}else {
			holder=(ViewHolder) convertView.getTag();  
		}
		
		Login ml = mData.get(index);
		holder.mUserNameTextView.setText(ml.nickname);
		if (ml.gender == 0){
			holder.mSexTextView.setText("男");
		}else if (ml.gender == 1){
			holder.mSexTextView.setText("女");
		}
		holder.mOldTextView.setText(ml.old + "");
		holder.mAddressView.setText(ml.provinceid+"  "+ml.cityid+" ");
		
		holder.mSign.setText(ml.sign);
		holder.mXqXuanyan.setText(ml.blindDate);
		holder.mXqRequire.setText(ml.blindDateRequre);
		
		ImageLoader mImageLoader = new ImageLoader();
		mImageLoader.getBitmap(mContext, holder.mHeaderView, null,ml.headsmall, 0,true,false);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, UserInfoActivity.class);
				intent.putExtra("user", mData.get(index));
				intent.putExtra("from", "BlindDateActivity");
				mContext.startActivity(intent);

			}
		});
		return convertView;
	}


	final static class ViewHolder {  
		TextView mUserNameTextView;  
		TextView mOldTextView;
		TextView mSexTextView;
		ImageView mHeaderView;
		TextView mAddressView;
		
		TextView mSign;
		TextView mXqXuanyan;
		TextView mXqRequire;
	} 

}
