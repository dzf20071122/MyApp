package com.research;

import java.util.ArrayList;
import java.util.List;

import com.llc.xlistview.XListView;
import com.research.Entity.Login;
import com.research.Entity.UserList;
import com.research.adapter.MakeFriendAdapter;
import com.research.adapter.SimplePageAdapter;
import com.research.fragment.GridFragment;
import com.research.global.GlobalParam;
import com.research.global.GlobleType;
import com.research.global.ResearchCommon;
import com.research.map.BMapApiApp;
import com.research.net.ResearchException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 相亲墙页面
 * 
 * @author dzf
 * 
 */
public class BlindDateActivity extends BaseActivity implements OnClickListener {

	private XListView listView;
	private MakeFriendAdapter mAdapter;
	private ArrayList<Login> mArrayList = new ArrayList<Login>(); 
	private UserList mUser;
	
	/**
	 * 一共有多少页
	 */
	private int pageCount;
	private int totalCount = 0;
	
	private ViewPager vp;
	private List<Fragment> fragments;
	private SimplePageAdapter pageAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blind_date_layout);
		mContext = this;
		initComponent();
		getDataList(GlobalParam.LIST_LOAD_FIRST);
		
//		initDatas();
//		setLayout();
	}

	/**
	 * 实例化控件
	 */
	private void initComponent(){
		setTitleContent(R.drawable.back_btn,0,R.string.blind_date_title_name);
		mLeftBtn.setOnClickListener(this);
		
		vp = (ViewPager) findViewById(R.id.viewpager);
		fragments = new ArrayList<Fragment>();
//		listView = (XListView) findViewById(R.id.blind_date_user_list); 
	}
	
	/**
	 * 刷新
	 */
	private void refreshUpdateListView(){
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}else{
			mAdapter = new MakeFriendAdapter(mContext, mArrayList,GlobleType.ADAPTER_FROM_XIANGQIN_TYPE);
			listView.setAdapter(mAdapter);
		}

	}
	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1000:
				//切换条件数据
				setLayout();
				break;
			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String dialogMsg = (String)msg.obj;
				showProgressDialog(dialogMsg);
				break;
			}
		}
	};
	
	/**
	 * 初始化数据，刚开始有18个
	 */
	private void initDatas() {
		for (int i = 0; i < 22; i++) {
			Login mLogin = ResearchCommon.getLoginResult(mContext);
			mLogin.nickname = "dzf"+i;
			mArrayList.add(mLogin);
		}
	}
	
	private void setLayout(){
		int size = mArrayList.size();
		if (size % 9 == 0) {
			pageCount = size / 9;
		} else {
			pageCount = size / 9 + 1;
		}
		totalCount = size;
		for (int i = 0; i < pageCount; i++) {
			//初始化每一个fragment
			GridFragment gf = GridFragment.newInstance(i, mArrayList,-1);

			fragments.add(gf);
		}

		//初始化pageAdapter
		pageAdapter = new SimplePageAdapter(getSupportFragmentManager(),
				fragments);

		vp.setAdapter(pageAdapter);

		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				if (arg0 == pageCount - 1) {
					// 在最后一页，开始加载后一页的数据
					for (int i = totalCount; i < totalCount + 9; i++) {
						Login mLogin = ResearchCommon.getLoginResult(mContext);
						mLogin.nickname = "dzf"+i;
						mArrayList.add(mLogin);
					}
					totalCount += 9;
					pageAdapter.addFragment(pageCount++, mArrayList,totalCount-9);
					pageAdapter.notifyDataSetChanged();

				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	} 
	/*
	 * 获取相亲墙数据
	 */
	private void getDataList(final int loadType){
		new Thread(){

			@Override
			public void run(){
				if(ResearchCommon.verifyNetwork(mContext)){
					new Thread(){
						public void run() {
							try {
//								if(type == GlobleType.BLOCKLISTACTIVITY_BLOCK_TYPE){
									mUser = ResearchCommon.getResearchInfo().getBlindDateList();
//								}

								if(mUser != null){
									if( mUser.code == 0){
//										mNoMore = true;

										if (mArrayList != null) {
											mArrayList.clear();
										}
										if (mUser.mUserList != null) {
											mArrayList.addAll(mUser.mUserList);
											mArrayList.addAll(mUser.mUserList);
											mArrayList.addAll(mUser.mUserList);
										}
										mHandler.sendEmptyMessage(1000);
									}else {
										Message msg=new Message();
										msg.what=GlobalParam.MSG_LOAD_ERROR;
										if(mUser.mState != null && mUser.mState.errorMsg != null && !mUser.mState.errorMsg.equals("")){
											msg.obj = mUser.mState.errorMsg;
										}else {
											msg.obj = BMapApiApp.getInstance().getResources().getString(R.string.load_error);
										}
										mHandler.sendMessage(msg);
									}
								}else {
									mHandler.sendEmptyMessage(GlobalParam.MSG_LOAD_ERROR);
								}


							} catch (ResearchException e) {
								e.printStackTrace();
								Message msg=new Message();
								msg.what=GlobalParam.MSG_TICE_OUT_EXCEPTION;
								msg.obj=BMapApiApp.getInstance().getResources().getString(R.string.timeout);
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
				}else {
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
	
	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			BlindDateActivity.this.finish();
			break;
		default:
			break;
		}
	}
}
