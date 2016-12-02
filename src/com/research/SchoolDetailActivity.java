package com.research;

import java.util.ArrayList;
import java.util.List;

import com.research.Entity.Login;
import com.research.Entity.ResearchJiaState;
import com.research.Entity.Room;
import com.research.Entity.RoomList;
import com.research.Entity.SchoolMeeting;
import com.research.Entity.SchoolMeetingList;
import com.research.adapter.SchoolDetailAdapter;
import com.research.global.FeatureFunction;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.map.BMapApiApp;
import com.research.net.ResearchException;
import com.research.sortlist.SideBar;
import com.research.sortlist.SideBar.OnTouchingLetterChangedListener;
import com.research.widget.MyPullToRefreshListView;
import com.research.widget.SelectAddPopupWindow;
import com.research.widget.MyPullToRefreshListView.OnChangeStateListener;
import com.research.widget.SelectCreatPopupWindow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SchoolDetailActivity extends BaseActivity implements
		OnClickListener,OnChangeStateListener {
	/**
	 * 定义全局变量
	 */

//	private SideBar sideBar;
//	private TextView dialog;
	private ArrayList<SchoolMeeting> mSourceDateList = new ArrayList<SchoolMeeting>();
	private SchoolDetailAdapter mAdapter;
	private ListView sortListView;
	private MyPullToRefreshListView mContainer;
	private LinearLayout mCategoryLinear;
	private TextView mRefreshViewLastUpdated;
	
	private boolean mIsRefreshing = false;

	SelectCreatPopupWindow menuWindow2; // 弹出框
	private RelativeLayout mTitleLayout;
	
	private String schoolName; //学校名称
	private String schId; //学校id
	

	public List<Room> mRoomList = new ArrayList<Room>();
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.school_detail_layout);
		mContext = this;
		

		Intent intent = getIntent();
		schoolName = intent.getStringExtra("schoolName");
		schId = intent.getStringExtra("schId");
		
		initComponent();
		getData();
		
		getSchoolDetail(GlobalParam.LIST_LOAD_FIRST);
	}

	/**
	 * 实例化控件
	 */
	private void initComponent() {
		mTitleLayout = (RelativeLayout)findViewById(R.id.title_layout);
		
		setTitleContent(R.drawable.back_btn, R.drawable.add_icon_btn,
				R.string.school_meeting_app_name);
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		/**
		sideBar = (SideBar) findViewById(R.id.school_sidrbar);
		dialog = (TextView) findViewById(R.id.school_dialog);
		sideBar.setTextView(dialog);

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = mAdapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						sideBar.setBackgroundColor(Color
								.parseColor("#00000000"));
						sideBar.setChoose(-1);
						sideBar.invalidate();
						dialog.setVisibility(View.INVISIBLE);
					}
				}, 2000);
			}
		});
	
		*/
		mCategoryLinear = (LinearLayout) findViewById(R.id.school_category_linear);
		mRefreshViewLastUpdated = (TextView) findViewById(R.id.pull_to_refresh_time);
		mContainer = (MyPullToRefreshListView) findViewById(R.id.container);
		sortListView = mContainer.getList();
		sortListView.setDivider(null);
		sortListView.setCacheColorHint(0);
		sortListView.setHeaderDividersEnabled(false);
		
		mContainer.setOnChangeStateListener(this);
	}
	private void addCommonData(ArrayList<SchoolMeeting> list){
		list.add(new SchoolMeeting(1,"校友简章",null));
		list.add(new SchoolMeeting(1,"组织活动",null));
		list.add(new SchoolMeeting(1,"学校动态",null));
	}
	
	private void getData() {
		mSourceDateList.clear();
		
		addCommonData(mSourceDateList);
		
//		mSourceDateList.add(new SchoolMeeting(2,"三大宜昌校友会",null));
//		mSourceDateList.add(new SchoolMeeting(2,"三大北京校友会",null));
//		mSourceDateList.add(new SchoolMeeting(2,"三大武汉校友会",null));
//		mSourceDateList.add(new SchoolMeeting(3,"三大宜昌校友会",null));
//		mSourceDateList.add(new SchoolMeeting(3,"三大北京校友会",null));
//		mSourceDateList.add(new SchoolMeeting(3,"三大武汉校友会",null));
//		for (int i = 0; i < 10; i++) {
//			Login login = new Login("" + (1000 + i), arr[i], arr[i]);
//			mSourceDateList.add(login);
//		}
		

		updateListView();
	}

	private void updateListView() {
		/*
		 * if (mAdapter != null) { mAdapter.notifyDataSetChanged(); }else{
		 */
		mAdapter = new SchoolDetailAdapter(mContext, mSourceDateList);
		sortListView.setAdapter(mAdapter);
		/* } */

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		getSchoolDetail(GlobalParam.LIST_LOAD_FIRST);
	}

	
	/**
	 * 刷新校友录数据
	 */
	private void refreshUpdateListView(){
		if (mAdapter != null) {
			mAdapter.updateListView(mSourceDateList);
//			mAdapter.notifyDataSetChanged();
		}else{
			mAdapter = new SchoolDetailAdapter(mContext, mSourceDateList);
			sortListView.setAdapter(mAdapter);
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
//				updateListView();
				refreshUpdateListView();
				break;

			case GlobalParam.SHOW_SCROLLREFRESH:
				if (mIsRefreshing) {
					mContainer.onRefreshComplete();
					break;
				}
				mIsRefreshing = true;
//				getUserList(GlobalParam.LIST_LOAD_REFERSH);
				getSchoolDetail(GlobalParam.LIST_LOAD_REFERSH);
				break;

			case GlobalParam.HIDE_SCROLLREFRESH:
				mIsRefreshing = false;
				mContainer.onRefreshComplete();
				//updateListView();
				refreshUpdateListView();
				break;
			case GlobalParam.MSG_CLEAR_LISTENER_DATA:

				if(mSourceDateList != null && mSourceDateList.size()>0){
					mSourceDateList.clear();
					if(mAdapter!=null){
						mAdapter.notifyDataSetChanged();
					}
				}

				List<SchoolMeeting> tempList = (List<SchoolMeeting>)msg.obj;
				if(tempList!=null && tempList.size()>0){
					mSourceDateList.addAll(tempList);
				}
				refreshUpdateListView();	//加载
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
	 * @param loadType   /school/api/detail
	 */
	private void getSchoolDetail(final int loadType) {
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
								
								//------------------------------------
								RoomList roomList = ResearchCommon.getResearchInfo().getRoomList(null);
								if(mRoomList!=null && mRoomList.size()>0){
									mRoomList.clear();
								}
								if(roomList.mRoomList!=null && roomList.mRoomList.size()>0){
									mRoomList.addAll(roomList.mRoomList);
								
								}
								//------------------------------------
//								ResearchJiaState mDetailaa = ResearchCommon.getResearchInfo().uploadMakeFriendMessage("强中强","","交友条件",3);
								SchoolMeetingList mDetail = ResearchCommon.getResearchInfo().getDetailInfo(schId);
								mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
								if (mDetail != null) {
									if (mDetail.mState != null && mDetail.mState.code == 0) {

										if (loadType != GlobalParam.LIST_LOAD_MORE) {
											if (mSourceDateList != null) {
												mSourceDateList.clear();
											}
										}

										ArrayList<SchoolMeeting> tempList = new ArrayList<SchoolMeeting>();
										ArrayList<SchoolMeeting> tempList1 = new ArrayList<SchoolMeeting>();
										addCommonData(tempList);
										
										if (mDetail.mSchoolMeetingList != null && mDetail.mSchoolMeetingList.size()>0) {
											
											for (int i = 0; i < mDetail.mSchoolMeetingList.size(); i++) {
												SchoolMeeting item = mDetail.mSchoolMeetingList.get(i);
												if (item.isJoin){
													item.sortType = 2;
													tempList.add(item);
												}else{
													item.sortType = 3;
													tempList1.add(item);
												}
											}
											tempList.addAll(tempList1);
										}
										
										if(mSourceDateList != null && mSourceDateList.size()>0){
											mSourceDateList.clear();
											if(mAdapter!=null){
												mAdapter.notifyDataSetChanged();
											}
										}

										if(tempList!=null && tempList.size()>0){
											mSourceDateList.addAll(tempList);
										}
										
//										ResearchCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA,tempList);
									} else {
										Message msg = new Message();
										msg.what = GlobalParam.MSG_LOAD_ERROR;
										if (mDetail.mState != null && mDetail.mState.errorMsg != null && !mDetail.mState.errorMsg.equals("")) {
											msg.obj = mDetail.mState.errorMsg;
										} else {
											msg.obj = BMapApiApp.getInstance().getResources().getString(R.string.load_error);
										}
										mHandler.sendMessage(msg);
									}
								} else {
									mHandler.sendEmptyMessage(GlobalParam.MSG_LOAD_ERROR);
								}
							} catch (ResearchException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Message msg = new Message();
								msg.what = GlobalParam.MSG_TICE_OUT_EXCEPTION;
								msg.obj = BMapApiApp.getInstance().getResources().getString(R.string.timeout);
								mHandler.sendMessage(msg);
							}
							
							switch (loadType) {
							case GlobalParam.LIST_LOAD_FIRST:
								mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
								break;
							case GlobalParam.LIST_LOAD_MORE:
								mHandler.sendEmptyMessage(GlobalParam.HIDE_LOADINGMORE_INDECATOR);

							case GlobalParam.LIST_LOAD_REFERSH:
								mHandler.sendEmptyMessage(GlobalParam.HIDE_SCROLLREFRESH);
								break;

							default:
								break;
							}
						}
					}.start();
				} else {
					switch (loadType) {
					case GlobalParam.LIST_LOAD_FIRST:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
						break;
					case GlobalParam.LIST_LOAD_MORE:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_LOADINGMORE_INDECATOR);

					case GlobalParam.LIST_LOAD_REFERSH:
						mHandler.sendEmptyMessage(GlobalParam.HIDE_SCROLLREFRESH);
						break;

					default:
						break;
					}
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}
			}

		}.start();
	}
	
	
	/*
	 * 获取分组数据
	 */
	private void getGroupList(final boolean isShowProgress){
		new Thread(){
			public void run() {
				try {
					RoomList roomList = ResearchCommon.getResearchInfo().getRoomList(null);
					if(mRoomList!=null && mRoomList.size()>0){
						mRoomList.clear();
					}
					if(roomList.mRoomList!=null && roomList.mRoomList.size()>0){
						mRoomList.addAll(roomList.mRoomList);
					
					}
				} catch (ResearchException e) {
					e.printStackTrace();
				}catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	};

	@Override
	public void onChangeState(MyPullToRefreshListView container, int state) {
		// TODO Auto-generated method stub
		mRefreshViewLastUpdated.setText(FeatureFunction.getRefreshTime());
		switch (state) {
		case MyPullToRefreshListView.STATE_LOADING:
			mHandler.sendEmptyMessage(GlobalParam.SHOW_SCROLLREFRESH);
			break;
		}
	}
	
	public void uploadImage2(final Activity context,View view) {
		if (menuWindow2 != null && menuWindow2.isShowing()) {
			menuWindow2.dismiss();
			menuWindow2 = null;
		}
		menuWindow2 = new SelectCreatPopupWindow(SchoolDetailActivity.this, itemsOnClick2);
		// 显示窗口

		// 计算坐标的偏移量
		int xoffInPixels = menuWindow2.getWidth() - view.getWidth()+10;
		menuWindow2.showAsDropDown(view, -xoffInPixels, 0);
	}
	
	// 为弹出窗口实现监听类
		private OnClickListener itemsOnClick2 = new OnClickListener() {

			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.create_meeting_layout:
					Intent intent = new Intent();
					intent.putExtra("schId",schId);
					intent.setClass(mContext,SchoolMeetingBuildActivity.class);
					mContext.startActivity(intent);
					break;

				default:
					break;
				}
				if (menuWindow2 != null && menuWindow2.isShowing()) {
					menuWindow2.dismiss();
					menuWindow2 = null;
				}
			}
		};
	
	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			SchoolDetailActivity.this.finish();
			break;
		case R.id.right_btn:
			uploadImage2(SchoolDetailActivity.this,mTitleLayout);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mAdapter = null;
		sortListView = null;
	}

}
