package com.research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.research.DB.DBHelper;
import com.research.DB.GroupTable;
import com.research.Entity.GroupList;
import com.research.Entity.Login;
import com.research.Entity.SchoolMeeting;
import com.research.Entity.SchoolMeetingList;
import com.research.adapter.SchoolMeetingMemberAdapter;
import com.research.global.FeatureFunction;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.map.BMapApiApp;
import com.research.net.ResearchException;
import com.research.sortlist.CharacterParser;
import com.research.sortlist.PinYin;
import com.research.sortlist.PinyinComparator;
import com.research.sortlist.SideBar;
import com.research.sortlist.SortAdapter;
import com.research.sortlist.SideBar.OnTouchingLetterChangedListener;
import com.research.widget.MyPullToRefreshListView;
import com.research.widget.MyPullToRefreshListView.OnChangeStateListener;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SchoolMeetingDetailActivity extends BaseActivity implements
		OnClickListener,OnChangeStateListener {
	/**
	 * 定义全局变量
	 */

	private SideBar sideBar;
	private TextView dialog;
	List<Login> mSourceDateList = new ArrayList<Login>();
	private SchoolMeetingMemberAdapter mAdapter;
	private ListView sortListView;
	private PinyinComparator pinyinComparator;
	private MyPullToRefreshListView mContainer;
	private LinearLayout mCategoryLinear;
	private TextView mRefreshViewLastUpdated;
	private CharacterParser characterParser;
	
	private boolean mIsRefreshing = false;

	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.school_meeting_detail);
		mContext = this;
		initComponent();
		pinyinComparator = new PinyinComparator();
		
		characterParser = CharacterParser.getInstance();
		
	}

	/**
	 * 实例化控件
	 */
	private void initComponent() {
		setTitleContent(R.drawable.back_btn, 0,
				R.string.school_meeting_app_name);
		mLeftBtn.setOnClickListener(this);

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

		mCategoryLinear = (LinearLayout) findViewById(R.id.school_category_linear);
		mRefreshViewLastUpdated = (TextView) findViewById(R.id.pull_to_refresh_time);
		mContainer = (MyPullToRefreshListView) findViewById(R.id.container);
		sortListView = mContainer.getList();
		sortListView.setDivider(null);
		sortListView.setCacheColorHint(0);
		sortListView.setHeaderDividersEnabled(false);
		
		mContainer.setOnChangeStateListener(this);
	}

	private void getData() {
		String[] arr = { "张三", "李四", "王五", "zhangsan", "liwu", "dongsi", "董宗峰",
				"花花", "小梁", "陈烨" };
		mSourceDateList.clear();
		
		mSourceDateList.add(new Login("↑","","校友简章","校友简章",1));
		mSourceDateList.add(new Login("↑","","组织活动","组织活动",1));
		mSourceDateList.add(new Login("↑","","学校动态","学校动态",1));
		
		for (int i = 0; i < 10; i++) {
			Login login = new Login("" + (1000 + i), arr[i], arr[i]);
			mSourceDateList.add(login);
		}

		updateListView();
	}

	private void updateListView() {
		filledData();
		// 根据a-z排序
		Collections.sort(mSourceDateList, pinyinComparator);
		/*
		 * if (mAdapter != null) { mAdapter.notifyDataSetChanged(); }else{
		 */
		mAdapter = new SchoolMeetingMemberAdapter(mContext, mSourceDateList);
		sortListView.setAdapter(mAdapter);
		/* } */

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getData();
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private void filledData() {

		try {
			for (int i = 0; i < mSourceDateList.size(); i++) {
				String name = "";
				name = mSourceDateList.get(i).name;
				if (name == null || name.equals("")) {
					name = mSourceDateList.get(i).nickname;
				}
				// 汉字转换成拼音

				String pinyin;
				pinyin = characterParser.getSelling(name);
				String sortString = pinyin.substring(0, 1).toUpperCase();
				Log.i("dzf..pinyin", sortString);
				String sortString1 = mSourceDateList.get(i).sort;
				if (sortString1.matches("↑")) {
					sortString = sortString1;
				}
				// String sName = mSourceDateList.get(i).sortName;

				if (sortString.matches("↑")) {
					mSourceDateList.get(i).sort = "↑";
					mSourceDateList.get(i).sortName = "";
					mSourceDateList.get(i).remark = name.substring(1,
							name.length());
				} else if (sortString.matches("[A-Z]")
						|| sortString.matches("[a-z]")) {
					String sort = PinYin.getPingYin(name.trim());
					if (sort == null || sort.length() <= 0) {
						sort = "#";
					} else {
						sort = sort.substring(0, 1).toUpperCase();
					}
					mSourceDateList.get(i).sort = sort;
					mSourceDateList.get(i).sortName = sort;
				} else {
					mSourceDateList.get(i).sortName = "#";
					mSourceDateList.get(i).sort = "#";
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 刷新校友录数据
	 */
	private void refreshUpdateListView(){
		filledData();
		//根据a-z排序
		Collections.sort(mSourceDateList, pinyinComparator);
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}else{
			mAdapter = new SchoolMeetingMemberAdapter(mContext, mSourceDateList);
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
				updateListView();
				break;

			case GlobalParam.SHOW_SCROLLREFRESH:
				if (mIsRefreshing) {
					mContainer.onRefreshComplete();
					break;
				}
				mIsRefreshing = true;
				getUserList(GlobalParam.LIST_LOAD_REFERSH);
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

				List<Login> tempList = (List<Login>)msg.obj;
				if(tempList!=null && tempList.size()>0){
					mSourceDateList.addAll(tempList);
				}
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
	 * 获取通讯录人员列表
	 * @param loadType
	 */
	private void getUserList(final int loadType) {
		new Thread() {

			@Override
			public void run() {
				//有无网络,有网络请求数据
				if (ResearchCommon.verifyNetwork(mContext)) {
					new Thread() {
						public void run() {
							List<Login> tempList = new ArrayList<Login>();
							String[] arr = { "安邦 ", "安福", "安歌", "安国", "才捷", "飞昂", "刚豪",
									"花花", "gangzai", "陈烨","guang","大发","yi" };
							tempList.add(new Login("↑","","校友简章","校友简章",1));
							tempList.add(new Login("↑","","组织活动","组织活动",1));
							tempList.add(new Login("↑","","学校动态","学校动态",1));
							for (int i = 0; i < arr.length; i++) {
								Login login = new Login("" + (1000 + i), arr[i], arr[i]);
								tempList.add(login);
							}
							ResearchCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA,tempList);
							try {
								SchoolMeetingList mGroup = ResearchCommon.getResearchInfo().getSchoolList();
							} catch (ResearchException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							/**
							try {
								
								mGroup = ResearchCommon.getResearchInfo().getUserList();

								if (mGroup != null) {
									if (mGroup.mState != null && mGroup.mState.code == 0) {

										if (loadType != GlobalParam.LIST_LOAD_MORE) {
											if (mGroupList != null) {
												mGroupList.clear();
											}
										}

										List<Login> tempList = new ArrayList<Login>();
										tempList.add(new Login("↑","",mNewFriendsString,mNewFriendsString,1,ResearchCommon.getContactTip(mParentContext)));
										tempList.add(new Login("↑","",mChatString,mChatString,1));
										if (mGroup.mGroupList != null) {
											mGroupList.addAll(mGroup.mGroupList);
											SQLiteDatabase db = DBHelper.getInstance(mParentContext).getWritableDatabase();
											GroupTable table = new GroupTable(db);
											table.insert(mGroup.mGroupList);

											for (int i = 0; i < mGroup.mGroupList.size(); i++) {
												if(mGroupList.get(i).mStarList!=null
														&& mGroupList.get(i).mStarList.size()>0){
													tempList.addAll(mGroupList.get(i).mStarList);
												}
											}
											for (int j = 0; j < mGroup.mGroupList.size(); j++) {
												if(mGroupList.get(j).mUserList != null){
													tempList.addAll(mGroupList.get(j).mUserList);
												}
											}
										}

										ResearchCommon.sendMsg(mHandler, GlobalParam.MSG_CLEAR_LISTENER_DATA,tempList);
									} else {
										Message msg = new Message();
										msg.what = GlobalParam.MSG_LOAD_ERROR;
										if (mGroup.mState != null && mGroup.mState.errorMsg != null && !mGroup.mState.errorMsg.equals("")) {
											msg.obj = mGroup.mState.errorMsg;
										} else {
											msg.obj = BMapApiApp.getInstance().getResources().getString(R.string.load_error);
										}
										mHandler.sendMessage(msg);
									}
								} else {
									mHandler.sendEmptyMessage(GlobalParam.MSG_LOAD_ERROR);
								}
								
							} catch (ResearchException e) {
								e.printStackTrace();
								Message msg = new Message();
								msg.what = GlobalParam.MSG_TICE_OUT_EXCEPTION;
								msg.obj = BMapApiApp.getInstance().getResources().getString(R.string.timeout);
								mHandler.sendMessage(msg);
							}
							*/
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
	
	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			SchoolMeetingDetailActivity.this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
