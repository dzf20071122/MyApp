package com.research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.research.R;
import com.research.Entity.Login;
import com.research.Entity.SchoolMeeting;
import com.research.Entity.SchoolMeetingList;
import com.research.adapter.SchoolMeetingAdapter;
import com.research.adapter.SchoolMeetingMemberAdapter;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.map.BMapApiApp;
import com.research.net.ResearchException;

/**
 * 关于我们页面
 * @author dzf
 *
 */
public class SchoolMeetingActivity extends BaseActivity implements OnClickListener{

	/**
	 * 定义全局变量
	 */
	
	private ArrayList<String> mNameList;
	private ArrayList<Drawable> mDrawableList;
	private GridView grid;
	private SchoolMeetingAdapter mAdapter;
	private ArrayList<SchoolMeeting> mSourceDateList;
	
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.school_all_menu);
		mContext = this;
		initComponent();
		getData();
	}
	
	/**
	 * 实例化控件
	 */
	private void initComponent(){
		setTitleContent(R.drawable.back_btn,0,R.string.school_meeting_app_name);
		mLeftBtn.setOnClickListener(this);
		
		grid = (GridView) findViewById(R.id.schoo_meeting_grid);  
		mNameList = new ArrayList<String>();
		mNameList.add("三峡大学");
		mNameList.add("武汉大学");
		mNameList.add("三峡大学");
		mNameList.add("三峡大学");
		mNameList.add("三峡大学");
		mNameList.add("三峡大学");
		mNameList.add("三峡大学");
		mNameList.add("三峡大学");
		mNameList.add("三峡大学");
		mNameList.add("三峡大学");
		
		mDrawableList = new ArrayList<Drawable>();
		mDrawableList.add(getResources().getDrawable(R.drawable.xiaobiao_sanda));
		mDrawableList.add(getResources().getDrawable(R.drawable.xiaobiao_sanda));
		mDrawableList.add(getResources().getDrawable(R.drawable.xiaobiao_sanda));
		mDrawableList.add(getResources().getDrawable(R.drawable.xiaobiao_sanda));
		mDrawableList.add(getResources().getDrawable(R.drawable.xiaobiao_sanda));
		mDrawableList.add(getResources().getDrawable(R.drawable.xiaobiao_sanda));
		mDrawableList.add(getResources().getDrawable(R.drawable.xiaobiao_sanda));
		mDrawableList.add(getResources().getDrawable(R.drawable.xiaobiao_sanda));
		mDrawableList.add(getResources().getDrawable(R.drawable.xiaobiao_sanda));
		mDrawableList.add(getResources().getDrawable(R.drawable.xiaobiao_sanda));
		
//		grid.setAdapter(new SchoolMeetingAdapter(this, mNameList, mDrawableList));
		
		mSourceDateList = new ArrayList<SchoolMeeting>();
	
	}
	private void updateListView() {
		/*
		 * if (mAdapter != null) { mAdapter.notifyDataSetChanged(); }else{
		 */
		mAdapter = new SchoolMeetingAdapter(mContext, mSourceDateList);
		grid.setAdapter(mAdapter);
		/* } */

	}
	/**
	 * 刷新校友录数据
	 */
	private void refreshUpdateListView(){
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}else{
			mAdapter = new SchoolMeetingAdapter(mContext, mSourceDateList);
			grid.setAdapter(mAdapter);
		}


	}
	/**
	 * 处理消息
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String dialogMsg = (String) msg.obj;
				showProgressDialog(dialogMsg);
				break;
			case GlobalParam.HIDE_PROGRESS_DIALOG:
				hideProgressDialog();
				
				break;

			case GlobalParam.SHOW_SCROLLREFRESH:
				ArrayList<SchoolMeeting> tempList = (ArrayList<SchoolMeeting>)msg.obj;
				if(tempList!=null && tempList.size()>0){
					for(int i=0;i<tempList.size();i++){
						SchoolMeeting item = tempList.get(i);
						mSourceDateList.add(item);
					}
				}
				refreshUpdateListView();
				break;

			case GlobalParam.HIDE_SCROLLREFRESH:
			
				break;
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:
				
				
				break;

			case GlobalParam.MSG_LOAD_ERROR:
				String error_Detail = (String) msg.obj;
				if (error_Detail != null && !error_Detail.equals("")) {
					Toast.makeText(mContext, error_Detail, Toast.LENGTH_LONG)
					.show();
				} else {
					Toast.makeText(mContext, R.string.load_error,
							Toast.LENGTH_LONG).show();
				}
				break;
			case GlobalParam.MSG_NETWORK_ERROR:
				Toast.makeText(mContext, R.string.network_error,
						Toast.LENGTH_LONG).show();
				break;
			case GlobalParam.MSG_TICE_OUT_EXCEPTION:

				String message = (String) msg.obj;
				if (message == null || message.equals("")) {
					message = BMapApiApp.getInstance().getResources().getString(R.string.timeout);
				}
				Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
				break;

			default:
				break;

			}
		}
	};
	
	/**
	 * 获取校友会列表
	 */
	public void getData(){
		new Thread() {

			@Override
			public void run() {
				//有无网络,有网络请求数据
				if (ResearchCommon.verifyNetwork(mContext)) {
					new Thread() {
						public void run() {
							try {
								ResearchCommon.sendMsg(mBaseHandler,
										BASE_SHOW_PROGRESS_DIALOG, mContext.getResources()
												.getString(R.string.get_school_list));
								SchoolMeetingList mSchoolList = ResearchCommon.getResearchInfo().getSchoolList();
								mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
								if (mSchoolList != null && mSchoolList.mState != null && mSchoolList.mState.code == 0) {
									ResearchCommon.sendMsg(mHandler, GlobalParam.SHOW_SCROLLREFRESH,mSchoolList.mSchoolMeetingList);
								}else{
									Message msg = new Message();
									msg.what = GlobalParam.MSG_LOAD_ERROR;
									if (mSchoolList != null && mSchoolList.mState != null && mSchoolList.mState.errorMsg != null && !mSchoolList.mState.errorMsg.equals("")) {
										msg.obj = mSchoolList.mState.errorMsg;
									} else {
										msg.obj = BMapApiApp.getInstance().getResources().getString(R.string.load_error);
									}
									mHandler.sendMessage(msg);
								}
								
							} catch (ResearchException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
				} else {
					
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}

		}.start();
	}
	
	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			SchoolMeetingActivity.this.finish();
			break;

		default:
			break;
		}
	}
}
