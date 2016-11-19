package com.research;

import java.util.ArrayList;
import java.util.List;

import com.research.Entity.Login;
import com.research.adapter.AlbumGridViewAdapter;
import com.research.util.AlbumHelper;
import com.research.util.Bimp;
import com.research.util.ImageBucket;
import com.research.util.ImageItem;
import com.research.util.PublicWay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


/**
 * 这个是进入相册显示所有图片的界面
 * 
 * @author king
 * @version 2014年10月18日  下午11:47:15
 */
public class AlbumActivity extends Activity implements OnClickListener {
	//显示手机里的所有图片的列表控件
	private GridView gridView;
	//当手机里没有图片时，提示用户没有图片的控件
	private TextView tv;
	//gridView的adapter
	private AlbumGridViewAdapter gridImageAdapter;
	//完成按钮
	private Button okButton;
	// 返回按钮
	private Button back;
	// 取消按钮
	private Button cancel;
	private Intent intent;
	// 预览按钮
	private Button preview;
	private Context mContext;
	private Login fCustomerVo;
	private ArrayList<ImageItem> dataList;
	private AlbumHelper helper;
	public static List<ImageBucket> contentList;
	public static Bitmap bitmap;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_album);
		PublicWay.activityList.add(this);
		mContext = this;

		fCustomerVo = (Login)getIntent().getSerializableExtra("data");
		Log.i("Runt", "fCustomerVo:"+fCustomerVo);
		//注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		Log.i("Runt", "AlbumActivity 1");
		registerReceiver(broadcastReceiver, filter);  
		Log.i("Runt", String.format("AlbumActivity 2  plugin_camera_no_pictures:%s",R.drawable.plugin_camera_no_pictures));
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.plugin_camera_no_pictures);
		Log.i("Runt", "AlbumActivity 3");
        init();
		Log.i("Runt", "AlbumActivity 4");
		initListener();
		Log.i("Runt", "AlbumActivity 5");
		//这个函数主要用来控制预览和完成按钮的状态
		isShowOkBt();
		Log.i("Runt", "AlbumActivity 6");
	}
	
	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {  
		  
        @Override  
        public void onReceive(Context context, Intent intent) {  
        	//mContext.unregisterReceiver(this);
            // TODO Auto-generated method stub  
        	gridImageAdapter.notifyDataSetChanged();
        }  
    };  

	// 预览按钮的监听
	private class PreviewListener implements OnClickListener {
		public void onClick(View v) {
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", "1");
				intent.setClass(AlbumActivity.this, GalleryActivity.class);
				intent.putExtra("data", fCustomerVo);
				startActivity(intent);
				finish();
			}
		}

	}

	// 完成按钮的监听
	private class AlbumSendListener implements OnClickListener {
		public void onClick(View v) {
			//overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
			/*intent.setClass(mContext, ChatMainActivity.class);
			intent.putExtra("data", fCustomerVo);
			startActivity(intent);*/
			finish();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ok_button:// 完成按钮的监听
			intent.setClass(mContext, ChatMainActivity.class);
			intent.putExtra("data", fCustomerVo);
			startActivity(intent);
			this.finish();
			break;
		case R.id.preview:// 预览按钮的监听
			if (Bimp.tempSelectBitmap.size() > 0) {
				intent.putExtra("position", "1");
				intent.setClass(mContext, GalleryActivity.class);
				intent.putExtra("data", fCustomerVo);
				startActivity(intent);
				this.finish();
			}
			break;
		case R.id.back:// 返回按钮监听
			intent.setClass(mContext, ImageFile.class);
			intent.putExtra("data", fCustomerVo);
			startActivity(intent);
			this.finish();
			break;
		case R.id.cancel:// 取消按钮的监听
			Bimp.tempSelectBitmap.clear();
			intent.setClass(mContext, ChatMainActivity.class);
			intent.putExtra("data", fCustomerVo);
			startActivity(intent);
			this.finish();
			break;
		default:
			break;
		}
		
	}
	// 返回按钮监听
	private class BackListener implements OnClickListener {
		public void onClick(View v) {
			intent.setClass(AlbumActivity.this, ImageFile.class);
			intent.putExtra("data", fCustomerVo);
			startActivity(intent);
			finish();
		}
	}

	// 取消按钮的监听
	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			Bimp.tempSelectBitmap.clear();
			/*intent.setClass(mContext, ChatMainActivity.class);
			intent.putExtra("data", fCustomerVo);
			startActivity(intent);*/
			finish();
		}
	}

	

	// 初始化，给一些对象赋值
	private void init() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());
		
		contentList = helper.getImagesBucketList(false);
		dataList = new ArrayList<ImageItem>();
		for(int i = 0; i<contentList.size(); i++){
			dataList.addAll( contentList.get(i).imageList );
		}
		
		back = (Button) findViewById(R.id.back);
		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new CancelListener());
		back.setOnClickListener(new BackListener());
		preview = (Button) findViewById(R.id.preview);
		preview.setOnClickListener(new PreviewListener());
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this,dataList,Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		tv = (TextView) findViewById(R.id.myText);
		gridView.setEmptyView(tv);
		okButton = (Button) findViewById(R.id.ok_button);
		okButton.setText(R.string.finish);
	}

	private void initListener() {

		gridImageAdapter
				.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked,Button chooseBt) {
						if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
							toggleButton.setChecked(false);
							chooseBt.setVisibility(View.GONE);
							if (!removeOneData(dataList.get(position))) {
								Toast.makeText(AlbumActivity.this, R.string.only_choose_num,
										200).show();
							}
							return;
						}
						if (isChecked) {
							chooseBt.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText(mContext.getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size()
									+ "/"+PublicWay.num+")");
						} else {
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							chooseBt.setVisibility(View.GONE);
							okButton.setText(mContext.getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
						}
						isShowOkBt();
					}
				});

		okButton.setOnClickListener(new AlbumSendListener());

	}

	private boolean removeOneData(ImageItem imageItem) {
			if (Bimp.tempSelectBitmap.contains(imageItem)) {
				Bimp.tempSelectBitmap.remove(imageItem);
				okButton.setText(mContext.getString(R.string.finish)+"(" +Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
				return true;
			}
		return false;
	}
	
	public void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText(mContext.getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			preview.setPressed(true);
			okButton.setPressed(true);
			preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
			preview.setTextColor(Color.WHITE);
		} else {
			okButton.setText(mContext.getString(R.string.finish)+"(" + Bimp.tempSelectBitmap.size() + "/"+PublicWay.num+")");
			preview.setPressed(false);
			preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
			preview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			intent.setClass(AlbumActivity.this, ImageFile.class);
			intent.putExtra("data", fCustomerVo);
			startActivity(intent);
			this.finish();
		}
		return false;

	}
@Override
protected void onRestart() {
	isShowOkBt();
	super.onRestart();
}

}
