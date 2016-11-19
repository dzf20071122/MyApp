package com.research.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.research.R;
import com.research.Entity.Login;
import com.research.Entity.SchoolMeeting;
import com.research.global.FeatureFunction;
import com.research.global.ImageLoader;

public class SchoolDetailAdapter extends BaseAdapter implements SectionIndexer{
	private List<SchoolMeeting> list = null;
	private Context mContext;
	private ImageLoader mImageLoader;
	private int[] mPositions;

	public SchoolDetailAdapter(Context mContext, List<SchoolMeeting> list) {
		this.mContext = mContext;
		this.list = list;
		this.mImageLoader = new ImageLoader();
		
		mPositions = new int[this.list.size()];
		int count = 1;
		int type = 0;
		for (int i = 0; i < this.list.size(); i++) {
//			if (this.list.get(i).sortType == count){
//				mPositions[count - 1] = i;
//				count += 1;
//			}
			
			if (this.list.get(i).sortType != type){
				type = this.list.get(i).sortType;
				mPositions[count-1] = i;
				count += 1;
			}
		}
		
	}

	/**
	 * 获取缓存的图片
	 * @return
	 */
	public HashMap<String, Bitmap> getImageBuffer(){
		return mImageLoader.getImageBuffer();
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SchoolMeeting> list){
		this.list = list;
		
		mPositions = new int[this.list.size()];
		int count = 1;
		int type = 0;
		for (int i = 0; i < this.list.size(); i++) {
//			if (this.list.get(i).sortType == count){
//				mPositions[count - 1] = i;
//				count += 1;
//			}
			
			if (this.list.get(i).sortType != type){
				type = this.list.get(i).sortType;
				mPositions[count-1] = i;
				count += 1;
			}
		}
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		final SchoolMeeting mContent = list.get(position);
		if (convertView == null || ((ViewHolder) convertView.getTag()).mTag != position) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.contact_item, null);
			holder.mHeadrIcon = (ImageView)convertView.findViewById(R.id.headerimage);
			holder.mNameTextView = (TextView)convertView.findViewById(R.id.username);

			holder.index = (TextView) convertView.findViewById(R.id.sortKey);
			holder.indexLayout = (RelativeLayout)convertView.findViewById(R.id.grouplayout);
			holder.mContentSplite = (ImageView)convertView.findViewById(R.id.content_splite);
			holder.mSignTextView = (TextView)convertView.findViewById(R.id.prompt);
			holder.newFriendsIcon= (TextView)convertView.findViewById(R.id.new_notify);
			//holder.contactLayout = (LinearLayout)convertView.findViewById(R.id.contact_layout);//select_contact_splite
			
			holder.joinBtn = (Button)convertView.findViewById(R.id.btn_join);
			
			holder.mTag = position;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		boolean flag = false;
		for (int i = 0; i < mPositions.length; i++) {
			if (mPositions[i] == position){
				flag = true;
				
				break;
			}
		}
		if (flag == true){
			holder.indexLayout.setVisibility(View.VISIBLE);
			holder.index.setVisibility(View.VISIBLE);
			holder.mContentSplite.setVisibility(View.VISIBLE);
			if (this.list.get(position).sortType == 1){
				holder.index.setText("学校信息");
			}else if(this.list.get(position).sortType == 2){
				holder.index.setText("我的校友会");
			}else if(this.list.get(position).sortType == 3){
				holder.index.setText("推荐校友会");
			}
		}else{
			holder.indexLayout.setVisibility(View.GONE);
			holder.index.setVisibility(View.GONE);
			holder.mContentSplite.setVisibility(View.VISIBLE);
		}
		
		String name = this.list.get(position).name;
		
		holder.mNameTextView.setText(name);
		if(this.list.get(position).sec!=null && !this.list.get(position).sec.equals("")){
			holder.mSignTextView.setVisibility(View.VISIBLE);
			holder.mSignTextView.setText(this.list.get(position).sec);
		}
		if(name.equals(mContext.getResources().getString(R.string.school_zhang))){
			//校友简章
			
			holder.newFriendsIcon.setVisibility(View.GONE);
			
			//holder.mNameTextView.getPaint().setFakeBoldText(true);
			holder.mHeadrIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.new_friends_icon));
			//holder.mContentSplite.setVisibility(View.VISIBLE);
		}else if(name.equals(mContext.getResources().getString(R.string.school_activity))){
			//组织活动
			holder.mHeadrIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.service_icon));
		}else if(name.equals(mContext.getResources().getString(R.string.school_dynamic))){
			//学校动态
			holder.mHeadrIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.service_icon));
		}else{
			if(this.list.get(position).icon!=null && !this.list.get(position).icon.equals("")){
				holder.mHeadrIcon.setTag(this.list.get(position).icon);
				mImageLoader.getBitmap(mContext, holder.mHeadrIcon,null,this.list.get(position).icon,0,false,true);
			}else{
				holder.mHeadrIcon.setImageResource(R.drawable.contact_default_header);
			}
		}
		//}
		
		//申请加入校友会按钮是否显示
		holder.joinBtn.setVisibility(View.GONE);
		if (this.list.get(position).sortType == 3){
			if (this.list.get(position).isJoin != null && this.list.get(position).isJoin){
				holder.joinBtn.setVisibility(View.GONE);
			}else{
				holder.joinBtn.setVisibility(View.VISIBLE);
				
			}
		}
		holder.joinBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO 申请加入校友会
				
			}
			
		});


		return convertView;

	}



	final static class ViewHolder {
		public int mTag;
		public ImageView mHeadrIcon,mContentSplite;
		public TextView mNameTextView,mSignTextView;

		public TextView index;
		public RelativeLayout indexLayout,contactLayout;
		public TextView newFriendsIcon;
		public Button joinBtn;
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		//Log.e("SortAdapter", "positon:"+position);
//		if(list.get(position)!=null && list.get(position).sort!=null
//				&& !list.get(position).sort.equals("")){
//			//Log.e("SortAdapter_two", "positon:"+position);
//			return list.get(position).sort.charAt(0);
//		}
		return 0;
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
//		for (int i = 0; i < getCount(); i++) {
//			String sortStr = list.get(i).sort;
//			if(sortStr!=null && !sortStr.equals("")){
//				if(sortStr.toUpperCase()!=null && !sortStr.toUpperCase().equals("")){
//					char firstChar = sortStr.toUpperCase().charAt(0);
//					if (firstChar == section) {
//						return i;
//					}
//				}
//				
//			}
//			
//			
//		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}