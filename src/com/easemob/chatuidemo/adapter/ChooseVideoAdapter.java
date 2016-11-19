package com.easemob.chatuidemo.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.research.R;
import com.easemob.chatuidemo.domain.VideoEntity;
import com.easemob.chatuidemo.task.AsyncImageLoader;
import com.easemob.util.DensityUtil;

public class ChooseVideoAdapter extends BaseAdapter {

	private Context mContext;
	private List<VideoEntity> videoList;
	private GridView gridView;
	private AsyncImageLoader asyncImageLoader;

	// LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(
	// (int) (Runtime.getRuntime().maxMemory() / 16)) {
	// @Override
	// protected int sizeOf(String key, Bitmap value) {
	// return value.getRowBytes() * value.getHeight();
	// }
	// };

	public ChooseVideoAdapter(Context context, List<VideoEntity> videoList,
			GridView gridView) {
		this.mContext = context;
		this.videoList = videoList;
		this.gridView = gridView;
		asyncImageLoader = new AsyncImageLoader();
		this.gridView.setOnScrollListener(onScrollListener);
	}

	@Override
	public int getCount() {
		return videoList.size() + 1;
	}

	@Override
	public VideoEntity getItem(int position) {
		return (position == 0) ? null : videoList.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
	public void loadImage(){
		int start=gridView.getFirstVisiblePosition();
		int end=gridView.getLastVisiblePosition();
		
		if(end>=getCount())
		{
			end=getCount()-1;
		}
		asyncImageLoader.setLoadLimit(start, end);
		asyncImageLoader.unlock();
		
	}
	
	
	
	
	AbsListView.OnScrollListener onScrollListener=new AbsListView.OnScrollListener() {
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				asyncImageLoader.lock();
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				loadImage();
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				asyncImageLoader.lock();
				break;
				
			default:
				break;
			}
			
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// TODO Auto-generated method stub
			
		}
	};

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
