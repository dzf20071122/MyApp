package com.research;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMChatConfig.EMEnvMode;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.VideoCallActivity;
import com.easemob.chatuidemo.activity.VoiceCallActivity;
import com.easemob.chatuidemo.receiver.CallReceiver;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;
import com.research.DB.DBHelper;
import com.research.DB.SessionTable;
import com.research.Entity.Login;
import com.research.Entity.MessageInfo;
import com.research.Entity.Session;
import com.research.Entity.Version;
import com.research.Entity.VersionInfo;
import com.research.dialog.MMAlert;
import com.research.dialog.MMAlert.OnAlertSelectId;
import com.research.exception.ExceptionHandler;
import com.research.fragment.ChatFragment;
import com.research.fragment.ContactsFragment;
import com.research.fragment.FoundFragment;
import com.research.fragment.GatherFriendFragment;
import com.research.fragment.MyFragment;
import com.research.global.FeatureFunction;
import com.research.global.GlobalParam;
import com.research.global.GlobleType;
import com.research.global.ResearchCommon;
import com.research.http.post.HttpRequestServer;
import com.research.io.file.AndroidFolder;
import com.research.io.file.UserFolder;
import com.research.net.ResearchException;
import com.research.net.ResearchInfo;
import com.research.receiver.NotifyChatMessage;
import com.research.receiver.NotifySystemMessage;
import com.research.service.SnsService;
import com.research.widget.MainSearchDialog;
import com.research.widget.PagerSlidingTabStrip;
import com.research.widget.SelectAddPopupWindow;
import com.research.widget.SelectPicPopupWindow;

import date.tool.MD5Util;
import date.tool.handleDate;

/**
 * 高仿微信的主界面
 * 
 * http://blog.csdn.net/guolin_blog/article/details/26365683
 * 
 * @author guolin
 */
public class MainActivity extends FragmentActivity implements OnClickListener,OnItemClickListener{

	/**
	 * 定义全局变量
	 */

	public static Login mLogin,fCustomerVo;
	public static boolean isActive = false;//本界面是否显示在前台
	public static boolean todayLogin = false;
	public static boolean updateAdd = false;
    List<Map<String, String>> list;
	private static final int REQUEST_GET_URI = 101;
	public static final int REQUEST_GET_BITMAP = 102;
	public static final int REQUEST_GET_IMAGE_BY_CAMERA = 103;

	private boolean mIsRegisterReceiver = false;
	
	
	protected ImageView mLogIcon, mSearchBtn,mAddBtn,mMoreBtn;
	private TextView mTitleView;
	private RelativeLayout mTitleLayout;
	
	protected AlertDialog mUpgradeNotifyDialog;
	private Version mVersion;
	protected ClientUpgrade mClientUpgrade;


	/**
	 * 聊天界面的Fragment
	 */
	private ChatFragment chatFragment;

	/**
	 * 发现界面的Fragment
	 */
	private FoundFragment foundFragment;
	
	/**
	 * 聚友会的Fragment
	 */
	private GatherFriendFragment gatherFriendFragment;
	
	/**
	 * 通讯录界面的Fragment
	 */
	private ContactsFragment contactsFragment;
	
	/**
	 * 我的Fragment
	 */
	private MyFragment myFragment;
	/**
	 * PagerSlidingTabStrip的实例
	 */
	private PagerSlidingTabStrip tabs;

	/**
	 * 获取当前屏幕的密度
	 */
	private DisplayMetrics dm;

	// 自定义的弹出框类
	SelectPicPopupWindow menuWindow; // 弹出框
	SelectAddPopupWindow menuWindow2; // 弹出框



	private Timer mTimer;
	private StartServiceTask mServiceTask;

	private ViewPager mPager;
	private Context mContext;

	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("Runt","MainActivity onCreate");
		Thread.currentThread().setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY);
		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		mContext = this;
		setContentView(R.layout.activity_main);
		//initSdk();
		registerNetWorkMonitor();
		setActionBarLayout(/*R.layout.title_layout*/);


		
		//setOverflowShowingAlways();

		dm = getResources().getDisplayMetrics();
		mPager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

		if(ResearchCommon.getLoginResult(mContext) == null){
			Intent intent = new Intent(mContext, LoginActivity.class);
			startActivityForResult(intent, GlobalParam.LOGIN_REQUEST);
		}else {
			loginXMPP();
			mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
			tabs.setViewPager(mPager);
			setTabsValue();
			sessionPromptUpdate();
			Intent intent = new Intent(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP);
			intent.putExtra("found_type", 1);
			mContext.sendBroadcast(intent);
			if(ResearchCommon.getFriendsLoopTip(mContext)!=0){
				tabs.setNewMsgTip(1, 1);
				mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP));
			}


			loginHuanxin();
		}
		//注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		Log.i("Runt", "Mainactivity 注册一个监听连接状态的 MyConnectionListener ");
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
		checkToday();// 查看今天是否已经获取登录积分了
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.research.BaseActivity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		
		mLogin = ResearchCommon.getLoginResult(mContext);
	}
	
	
	/**
	 * 自定义titlebar
	 */
	public void setActionBarLayout( /*int layoutId */){

	   /* ActionBar actionBar = getActionBar( );

	    if( null != actionBar ){

	        actionBar.setDisplayShowHomeEnabled( false );

	        actionBar.setDisplayShowCustomEnabled(true);
*/

	   /*     LayoutInflater inflator = (LayoutInflater)   this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	        View v = inflator.inflate(layoutId, null);*/

	      /*  ActionBar.LayoutParams layout = new     ActionBar.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	        actionBar.setCustomView(v,layout);
	        */
			mTitleLayout = (RelativeLayout)findViewById(R.id.title_layout);
		
	        mLogIcon = (ImageView)findViewById(R.id.logo_icon);
	        mLogIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.actionbar_icon));
	        mLogIcon.setVisibility(View.VISIBLE);
	        
	        mTitleView = (TextView)findViewById(R.id.title);
	        mTitleView.setText(mContext.getResources().getString(R.string.ochat_app_name));
	        
	        mSearchBtn = (ImageView)findViewById(R.id.search_btn);
	        mAddBtn = (ImageView)findViewById(R.id.add_btn);
	        mMoreBtn = (ImageView)findViewById(R.id.more_btn);
	        
	        mSearchBtn.setVisibility(View.VISIBLE);
	        mAddBtn.setVisibility(View.VISIBLE);
	        mMoreBtn.setVisibility(View.VISIBLE);
	        
	        mSearchBtn.setOnClickListener(this);
	        mAddBtn.setOnClickListener(this);
	        mMoreBtn.setOnClickListener(this);
	        

	  /*  }*/

	}

	
	

	/**
	 * 检测用户是否输入昵称
	 * @param login
	 * @return
	 */
	private boolean checkValue(Login login){
		boolean ischeck = true;
		if(login == null || login.equals("") ){
			ischeck = false;
		}else{
			if (/*(login.headsmall == null || login.headsmall.equals(""))
					|| */(login.nickname== null || login.nickname.equals(""))
					) {
				ischeck = false;
			}
		}
		return ischeck;

	}


	/**
	 * 连接到xmpp
	 */
	private void loginXMPP(){

		startGuidePage();

		mServiceTask = new StartServiceTask(mContext);
		mTimer = new Timer("starting");
		mTimer.scheduleAtFixedRate(mServiceTask, 0, 5000);
		

	}

	/**
	 * 检测用是否填写昵称，如果没有则跳转到完善资料页进行填写
	 */
	private void startGuidePage(){
		Login login = ResearchCommon.getLoginResult(mContext);
		mLogin = login;
		if(checkValue(login)){
			/*SharedPreferences preferences = this.getSharedPreferences(reSearchCommon.SHOWGUDIEVERSION, 0);
				int version = preferences.getInt("app_version", 0);
				//version = 0;
				if (version != FeatureFunction.getAppVersion(mContext)) {
					Intent intent = new Intent();
					intent.setClass(MainActivity.this, GuideActivity.class);
					startActivityForResult(intent, GlobalParam.SHOW_GUIDE_REQUEST);
					//isShowGudie = true;
				}else {*/
			checkUpgrade();//检测新版本
			/*}*/
		}else{//跳转到完善资料页
			Intent completeIntent = new Intent();
			completeIntent.setClass(mContext, CompleteUserInfoActvity.class);
			completeIntent.putExtra("login", login);
			startActivityForResult(completeIntent, GlobalParam.SHOW_COMPLETE_REQUEST);
		}
		//return isShowGudie;
	}
	
	/**
	 * 开启聊天服务
	 * @author dongli
	 *
	 */
	private final class StartServiceTask extends TimerTask{
		private Context context;
		StartServiceTask(Context context){
			this.context = context;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(), SnsService.class);
			this.context.startService(intent);
		}
	}

	/**
	 * 注册通知
	 */
	private void registerNetWorkMonitor() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.ACTION_NETWORK_CHANGE);
		filter.addAction(GlobalParam.EXIT_ACTION);
		filter.addAction(GlobalParam.ACTION_REFRESH_NOTIFIY);
		filter.addAction(GlobalParam.ACTION_UPDATE_SESSION_COUNT);
		filter.addAction(GlobalParam.ACTION_CALLBACK);
		filter.addAction(GlobalParam.ACTION_REFRESH_FRIEND);
		filter.addAction(GlobalParam.ACTION_LOGIN_OUT);
		filter.addAction(NotifySystemMessage.ACTION_VIP_STATE);
		filter.addAction(GlobalParam.CANCLE_COMPLETE_USERINFO_ACTION);
		filter.addAction(GlobalParam.ACTION_SHOW_TOAST);
		filter.addAction(GlobalParam.ACTION_SHOW_REGISTER_REQUEST);
		filter.addAction(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP);
		filter.addAction(GlobalParam.ACTION_HIDE_FOUND_NEW_TIP);
		filter.addAction(GlobalParam.ACTION_SHOW_CONTACT_NEW_TIP);
		filter.addAction(GlobalParam.ACTION_HIDE_CONTACT_NEW_TIP);
		//filter.addAction(GlobalParam.ACTION_UPDATE_MEETING_SESSION_COUNT);
		
		registerReceiver(mReceiver, filter);
		
		/*IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
        CallReceiver   callReceiver = new CallReceiver();
        registerReceiver(callReceiver, callFilter);
		Log.i("Runt","MainActivity注册通知了callReceiver");*/
		mIsRegisterReceiver = true;
	}

	/**
	 * 检测通知类型，进行不同的操作
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent){
			String action = intent.getAction();
			if (action.equals(GlobalParam.ACTION_NETWORK_CHANGE)) {//网络通知
				boolean isNetConnect = false;
				ConnectivityManager connectivityManager = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetInfo = connectivityManager
						.getActiveNetworkInfo();
				if (activeNetInfo != null) {
					if (activeNetInfo.isConnected()) {
						isNetConnect = true;
						/*Toast.makeText(
								context,
								"xxxxxxxxxxxxxxxxxxxxxxxxx"
										+ activeNetInfo.getTypeName(),
								Toast.LENGTH_SHORT).show();*/
					} else {
						/*Toast.makeText(
								context,
								"xxxxxxxxxxxxxxxxxxxxxxxxx" + " "
										+ activeNetInfo.getTypeName(),
								Toast.LENGTH_SHORT).show();*/
					}
				} else {
					/*Toast.makeText(context, mContext.getResources().getString(R.string.network_error),
							Toast.LENGTH_SHORT).show();*/
				}
				ResearchCommon.setNetWorkState(isNetConnect);
			} else if (action.equals(GlobalParam.SWITCH_TAB)){//却换到第一个标签
				mPager.setCurrentItem(0);
				//tabs.set
				//mTabHost.setCurrentTabByTag(CHATS);
			}else if(action.equals(GlobalParam.EXIT_ACTION)){//退出登录
				//reSearchCommon.CancelNotifyAlarm(mContext);
				moveTaskToBack(true);
				System.exit(0);
				//moveTaskToBack(true);
				//System.exit(0);
			}else if(GlobalParam.CANCLE_COMPLETE_USERINFO_ACTION.equals(action)){//跳转到登陆界面
				Intent loginIntent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(loginIntent, GlobalParam.LOGIN_REQUEST);
			}else if(GlobalParam.ACTION_UPDATE_SESSION_COUNT.equals(action)){//消息未读消息数
				sessionPromptUpdate();
			}
			else if(GlobalParam.ACTION_LOGIN_OUT.equals(action)){//却换用户登陆
				mPager.setCurrentItem(0);
				try {
					mTimer.cancel();
				} catch (Exception e) {
				}
				Intent loginIntent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(loginIntent, GlobalParam.LOGIN_REQUEST);

			}else if(GlobalParam.ACTION_SHOW_TOAST.equals(action)){//显示账号在其他设备登陆的通知
				String hintMsg = intent.getStringExtra("toast_msg");
				if(hintMsg!=null && !hintMsg.equals("")){
					Toast.makeText(mContext, hintMsg,Toast.LENGTH_LONG).show();
				}
			}else if(GlobalParam.ACTION_SHOW_REGISTER_REQUEST.equals(action)){//注册账号成功后，登陆到xmpp
				//Log.e("MainActivity-onActivityResult", "注册成功,完善资料！+++++++");
				loginXMPP();
				mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
				tabs.setViewPager(mPager);
				setTabsValue();
				sessionPromptUpdate();
			}else if(action.equals(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP)){//显示朋友圈和会议有新的消息
				int type = intent.getIntExtra("found_type", 0);
				if(type ==1){
					meetingPromptUpdate();
				}else if(type  == 2){//有新的会议通知
					tabs.setNewMsgTip(1, 1);
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_MEETING));
				}else{//朋友圈有新的动态
					int tipCount = ResearchCommon.getFriendsLoopTip(mContext);
					tipCount = tipCount+1;
					ResearchCommon.saveFriendsLoopTip(mContext, tipCount);
					tabs.setNewMsgTip(1, 1);
					/*Intent fondIntent = new Intent(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP);
					fondIntent.putExtra("count",tipCount);*/
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP));
				}
				
			}else if(action.equals(GlobalParam.ACTION_HIDE_FOUND_NEW_TIP)){//隐藏发现按钮旁边的小红点
				
				int type = intent.getIntExtra("found_type",0);
				if(type !=0){
					ResearchCommon.saveFriendsLoopTip(mContext, 0);
				}
				if(ResearchCommon.getIsReadFoundTip(mContext)){
					tabs.hideMsgTip(1);
				}
			}else if(action.equals(GlobalParam.ACTION_SHOW_CONTACT_NEW_TIP)){//显示有新的联系人
				ResearchCommon.saveContactTip(mContext, 1);
				tabs.setNewMsgTip(1, 3);
			}else if(action.equals(GlobalParam.ACTION_HIDE_CONTACT_NEW_TIP)){//隐藏有新的联系人小红点
				ResearchCommon.saveContactTip(mContext, 0);
				tabs.hideMsgTip(3);
			}
			
		}
	};


	//显示未读显示数
	public void sessionPromptUpdate(){
		SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
		SessionTable table = new SessionTable(db);
		int count = table.querySessionCount(GlobleType.MEETING_CHAT);
		tabs.setNewMsgTip(count, 0);
	}
	
	/**
	 * 查询未读会议数据
	 */
	public boolean meetingPromptUpdate(){
		boolean isExits = false;
		SQLiteDatabase db = DBHelper.getInstance(mContext).getReadableDatabase();
		SessionTable table = new SessionTable(db);
		int count = table.queryMeetingSessionCount();

		tabs.setNewMsgTip(count, 1);
		if(count!=0){
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_MEETING));
				}
			}, 1000);
			
			isExits = true;
		}
		return isExits;
	}


	/**
	 * 对PagerSlidingTabStrip的各项属性进行赋值。
	 */
	private void setTabsValue() {
		// 设置Tab是自动填充满屏幕的
		tabs.setShouldExpand(true);
		// 设置Tab的分割线是透明的
		tabs.setDividerColor(Color.TRANSPARENT);
		// 设置Tab底部线的高度
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		// 设置Tab Indicator的高度
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,0, dm));
		// 设置Tab标题文字的大小
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 12, dm));
		// 设置Tab Indicator的颜色
		tabs.setIndicatorColor(Color.parseColor("#ff0000"));
		// 设置选中Tab文字的颜色 (这是我自定义的一个方法)
		tabs.setSelectedTextColor(Color.parseColor("#00bfff"));
		// 取消点击Tab时的背景色
		tabs.setTabBackground(0);
	}

	/**
	 * fragment 适配器
	 * @author dongli
	 *
	 */
	public class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] titles = mContext.getResources().getStringArray(R.array.main_fragment_array);
		
		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return titles.length;
		}
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if (chatFragment == null) {
					chatFragment = new ChatFragment();
				}
				return chatFragment;
			case 1:
				if (foundFragment == null) {
					foundFragment = new FoundFragment();
				}
				return foundFragment;
			case 2:
				if (gatherFriendFragment == null) {
					gatherFriendFragment = new GatherFriendFragment();
				}
				return gatherFriendFragment;
			case 3:
				if (contactsFragment == null) {
					contactsFragment = new ContactsFragment();
				}
				return contactsFragment;
			case 4:
				if (myFragment == null) {
					myFragment = new MyFragment();
				}
				return myFragment;
			default:
				return null;
			}
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			Log.e("destroyItem", "destroyItem");
			super.destroyItem(container, position, object);
		}

		@Override
		public long getItemId(int position) {
			//mCurrentTabIndex = position;
			return super.getItemId(position);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			Log.e("instantiateItem", "instantiateItem");
			return super.instantiateItem(container, position);
		}



	}

	public void uploadImage(final Activity context,View view) {
		if (menuWindow != null && menuWindow.isShowing()) {
			menuWindow.dismiss();
			menuWindow = null;
		}
		menuWindow = new SelectPicPopupWindow(MainActivity.this, itemsOnClick);
		// 显示窗口
		/*	View view = MainActivity.this.findViewById(R.id.set);*/
		// 计算坐标的偏移量
		int xoffInPixels = menuWindow.getWidth() - view.getWidth() + 10;
		menuWindow.showAsDropDown(view, -xoffInPixels, 0);
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.my_profile:
				/*Intent profileIntent = new Intent();
				profileIntent.setClass(mContext, ProfileActivity.class);
				mContext.startActivity(profileIntent);*/
				Intent intent = new Intent(mContext, EditProfileActivity.class);
				/*intent.putExtra("login", mLogin);*/
				startActivity(intent);
				break;
			case R.id.my_photo:
				Intent albumIntent = new Intent();
				albumIntent.setClass(mContext, MyAlbumActivity.class);
				mContext.startActivity(albumIntent);
				break;
			case R.id.my_collection:
				Intent collectionIntent = new Intent();
				collectionIntent.setClass(mContext, MyFavoriteActivity.class);
				mContext.startActivity(collectionIntent);
				break;
			case R.id.my_setting:
				Intent settingIntent = new Intent();
				settingIntent.setClass(mContext, SettingTab.class);
				mContext.startActivity(settingIntent);
				break;
			case R.id.my_feedback:
				Intent feedbackIntent = new Intent();
				feedbackIntent.setClass(mContext, FeedBackActivity.class);
				mContext.startActivity(feedbackIntent);
				break;


			case R.id.my_vip:
		    	Intent callIntent=new Intent();
		    	callIntent.setClass(mContext, BrowserActivity.class);
		    	callIntent.putExtra("url", "http://oa3.xrmg88.com/LoginMobile.aspx?MemberName="+mLogin.phone+"&MemberPwd="+mLogin.password);
		    	callIntent.putExtra("title", "我的会员");
		    	startActivity(callIntent);
				break;

			case R.id.add_part:
		    	Intent addintent=new Intent();
		    	addintent.setClass(mContext, addpartActivity.class);
		    	addintent.putExtra("login", mLogin);
		    	addintent.putExtra("title", "广告专区");
		    	startActivity(addintent);
				break;
			default:
				break;
			}
			if (menuWindow != null && menuWindow.isShowing()) {
				menuWindow.dismiss();
				menuWindow = null;
			}
			
		}
	};

	public void uploadImage2(final Activity context,View view) {
		if (menuWindow2 != null && menuWindow2.isShowing()) {
			menuWindow2.dismiss();
			menuWindow2 = null;
		}
		menuWindow2 = new SelectAddPopupWindow(MainActivity.this, itemsOnClick2);
		// 显示窗口

		// 计算坐标的偏移量
		int xoffInPixels = menuWindow2.getWidth() - view.getWidth()+10;
		menuWindow2.showAsDropDown(view, -xoffInPixels, 0);
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick2 = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.chat_layout:
				Intent intent = new Intent();
				intent.setClass(mContext,ChooseUserActivity.class);
				intent.putExtra("jumpfrom",1);
				mContext.startActivity(intent);
				break;
			case R.id.add_friend:
				Intent addIntent = new Intent();
				addIntent.setClass(mContext, AddActivity.class);
				startActivity(addIntent);
				break;
			case R.id.shao_layout:
				Intent scanIntent = new Intent(mContext, CaptureActivity.class);
				startActivity(scanIntent);
				break;
			case R.id.photo_share:
				selectImg();
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
	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			//exitDialog();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	

	/**
	 * 销毁页面
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mIsRegisterReceiver){
			mIsRegisterReceiver = false;
			unregisterReceiver(mReceiver);
		}
		// Verify picture cache files whose created date more than Fifteen days.
		System.exit(0);
	}

	/**
	 * 页面返回结果
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.e("MainActivity-onActivityResult", "insert+++++++");
		switch (requestCode) {
		case GlobalParam.LOGIN_REQUEST:
			if (resultCode == GlobalParam.RESULT_EXIT) {// dl repair
				//moveTaskToBack(true);
				MainActivity.this.finish();
				return;
			}else if(resultCode == RESULT_OK){
				
				new Handler().postDelayed(new Runnable(){
					@Override
					public void run() {
						int mode = Context.MODE_WORLD_WRITEABLE;
						if(Build.VERSION.SDK_INT >= 11){
							mode = Context.MODE_MULTI_PROCESS;
						}
						SharedPreferences sharePreferences = mContext.getSharedPreferences("LAST_TIME", mode);
						String lastTime = sharePreferences.getString("last_time","");
						int contactCount = sharePreferences.getInt("contact_count",0);
						String currentTime = FeatureFunction.formartTime(System.currentTimeMillis()/1000, "yyyy-MM-dd HH:mm:ss");
						try {
							if((lastTime==null || lastTime.equals("")) || !(FeatureFunction.jisuan(lastTime, currentTime))){
								//发送检测新的朋友通知
								Intent checkIntent = new Intent(ChatFragment.ACTION_CHECK_NEW_FRIENDS);
								checkIntent.putExtra("count",contactCount);
								mContext.sendBroadcast(checkIntent);
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 2000);
				
				/**
				 * 连接到xmpp、初始化页面
				 */
				loginXMPP();
				mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
				tabs.setViewPager(mPager);
				setTabsValue();
				sendBroadcast(new Intent(GlobalParam.SWITCH_TAB));
				sessionPromptUpdate();
				Intent sintent = new Intent(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP);
				sintent.putExtra("found_type", 1);
				mContext.sendBroadcast(sintent);
				if(ResearchCommon.getFriendsLoopTip(mContext)!=0){
					tabs.setNewMsgTip(1, 1);
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP));
				}
			}
			break;
		case GlobalParam.SHOW_GUIDE_REQUEST:
			if(resultCode == RESULT_OK){

				checkUpgrade();

			}
			break;
		case GlobalParam.SHOW_COMPLETE_REQUEST:
			if(resultCode == GlobalParam.SHOW_COMPLETE_RESULT){
				loginXMPP();
				mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
				tabs.setViewPager(mPager);
				setTabsValue();
				sessionPromptUpdate();
				Intent sintent = new Intent(GlobalParam.ACTION_SHOW_FOUND_NEW_TIP);
				sintent.putExtra("found_type", 1);
				mContext.sendBroadcast(sintent);
				if(ResearchCommon.getFriendsLoopTip(mContext)!=0){
					tabs.setNewMsgTip(1, 1);
					mContext.sendBroadcast(new Intent(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP));
				}
			}
			break;
		case REQUEST_GET_URI: 
			if (resultCode == RESULT_OK) {
				doChoose(true, intent);
			}

			break;

		case REQUEST_GET_IMAGE_BY_CAMERA:
			if(resultCode == RESULT_OK){
				doChoose(false, intent);
			}
			break;

		case REQUEST_GET_BITMAP:
			if(resultCode == RESULT_OK){
				String path = intent.getStringExtra("path");
				if(!TextUtils.isEmpty(path)){
					Intent sendMovingIntent = new Intent();
					sendMovingIntent.setClass(mContext, SendMovingActivity.class);
					sendMovingIntent.putExtra("moving_url",path);
					mContext.startActivity(sendMovingIntent);
				}
			}

			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if(intent == null){
			return ;
		}
		
		boolean isChatNotify = intent.getBooleanExtra("chatnotify", false);
		if(BaseActivity.isCall){//判断请求是否开启语音
			Toast.makeText(mContext,"通知栏计入"+BaseActivity.isCall,	Toast.LENGTH_SHORT).show();
			isChatNotify=false;
		}
		boolean isNotify = intent.getBooleanExtra("notify", false);
		if(isChatNotify){
			Login user = (Login)intent.getSerializableExtra("data");
			user.mIsRoom = intent.getIntExtra("type", 100);
			Intent chatIntent = new Intent(mContext, ChatMainActivity.class);
			chatIntent.putExtra("data", user);
			chatIntent.putExtra("isback", true);
			startActivity(chatIntent);
		}else if(isNotify){
			Intent chatIntent = new Intent(mContext, NewFriendsActivity.class);
			startActivity(chatIntent);
		}else {
			sendBroadcast(new Intent(GlobalParam.SWITCH_TAB));
		}

		super.onNewIntent(intent);
	}

	/**
	 * 底部弹出框
	 */
	private void selectImg(){
		MMAlert.showAlert(mContext,mContext.getResources().getString(R.string.select_image),
				mContext.getResources().getStringArray(R.array.camer_item), 
				null, new OnAlertSelectId() {

			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
				case 0:
					getImageFromGallery();
					break;
				case 1:
					getImageFromCamera();
					break;
				default:
					break;
				}
			}
		});
	}


	/**
	 * 拍一张照片
	 */
	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if(FeatureFunction.newFolder(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY)){
			File out = new File(Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY, "moving.jpg");
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, REQUEST_GET_IMAGE_BY_CAMERA);
		}

	}

	/**
	 * 从相册中选择图片
	 */
	private void getImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");

		startActivityForResult(intent, REQUEST_GET_URI);
	}

	/**
	 * 选择图片
	 * @param isGallery
	 * @param data
	 */
	private void doChoose(final boolean isGallery, final Intent data) {
		if(isGallery){
			originalImage(data);
		}else {
			if(data != null){
				originalImage(data);
			}else{
				// Here if we give the uri, we need to read it

				String path = Environment.getExternalStorageDirectory() + FeatureFunction.PUB_TEMP_DIRECTORY+"moving.jpg";
				String extension = path.substring(path.indexOf("."), path.length());
				if(FeatureFunction.isPic(extension)){
					//startPhotoZoom(Uri.fromFile(new File(path)));
					Intent intent = new Intent(mContext, RotateImageActivity.class);
					intent.putExtra("path", path);
					intent.putExtra("type", 0);
					startActivityForResult(intent, REQUEST_GET_BITMAP);
				}
				//mImageFilePath = FeatureFunction.PUB_TEMP_DIRECTORY+TEMP_FILE_NAME;
				//ShowBitmap(false);
			}
		}
	}

	private void originalImage(Intent data) {
		/*
		 * switch (requestCode) {
		 */
		// case FLAG_CHOOSE:
		Uri uri = data.getData();
		//Log.d("may", "uri=" + uri + ", authority=" + uri.getAuthority());
		if (!TextUtils.isEmpty(uri.getAuthority())) {
			Cursor cursor = getContentResolver().query(uri,
					new String[] { MediaStore.Images.Media.DATA }, null, null,
					null);
			if (null == cursor) {
				//Toast.makeText(mContext, R.string.no_found, Toast.LENGTH_SHORT).show();
				return;
			}
			cursor.moveToFirst();
			String path = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			Log.d("may", "path=" + path);
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if(FeatureFunction.isPic(extension)){
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, REQUEST_GET_BITMAP);

				//startPhotoZoom(data.getData());

			}else {
				//Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
			}
			//ShowBitmap(false);


		} else {
			Log.d("may", "path=" + uri.getPath());
			String path = uri.getPath();
			String extension = path.substring(path.lastIndexOf("."), path.length());
			if(FeatureFunction.isPic(extension)){
				Intent intent = new Intent(mContext, RotateImageActivity.class);
				intent.putExtra("path", path);
				startActivityForResult(intent, REQUEST_GET_BITMAP);
			}else {
				//Toast.makeText(mContext, R.string.please_choose_pic, Toast.LENGTH_SHORT).show();
			}
			//mImageFilePath = uri.getPath();
			//ShowBitmap(false);
		}
	}

	/**
	 * 处理消息
	 */
	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.SHOW_UPGRADE_DIALOG:
				showUpgradeDialog();
				break;
			case GlobalParam.NO_NEW_VERSION:
				Toast.makeText(getApplicationContext(), mContext.getResources().getString(R.string.no_version), Toast.LENGTH_LONG).show();
				break;
			case GlobalParam.MSG_NETWORK_ERROR:
				Toast.makeText(mContext,R.string.network_error,Toast.LENGTH_LONG).show();
				return;

			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				String message=(String)msg.obj;
				if (message==null || message.equals("")) {
					message=mContext.getResources().getString(R.string.timeout);
				}
				Toast.makeText(mContext,message, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}

	};

	/**
	 * 检测更新
	 */
	private void checkUpgrade(){
		new Thread(){
			@Override
			public void run() {
				if(ResearchCommon.verifyNetwork(mContext)){
					try {

						VersionInfo versionInfo = ResearchCommon.getResearchInfo().checkUpgrade(FeatureFunction.getAppVersionName(mContext));
						if(versionInfo != null && versionInfo.mVersion!=null && versionInfo.mState != null && versionInfo.mState.code == 0){
							mClientUpgrade = new ClientUpgrade();
							mVersion = versionInfo.mVersion;
							if(mClientUpgrade.compareVersion(FeatureFunction.getAppVersionName(mContext), mVersion.version)){
								mHandler.sendEmptyMessage(GlobalParam.SHOW_UPGRADE_DIALOG);
							}else{
								//mHandler.sendEmptyMessage(NO_NEW_VERSION);
							}
						}
					} catch (ResearchException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}


	/**
	 * 初始化版本更新对话框
	 */
	private void showUpgradeDialog() {
		LayoutInflater factor = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View serviceView = factor.inflate(R.layout.client_dialog, null);
		TextView titleTextView = (TextView) serviceView.findViewById(R.id.title);
		titleTextView.setText(mContext.getResources().getString(R.string.check_new_version));
		TextView contentView = (TextView) serviceView.findViewById(R.id.updatelog);
		contentView.setText(mVersion.discription);
		Button okBtn = (Button)serviceView.findViewById(R.id.okbtn);
		okBtn.setText(mContext.getResources().getString(R.string.upgrade));
		okBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {

				showDownloadApkDilog();//下载新的版本

				if (mUpgradeNotifyDialog != null){
					mUpgradeNotifyDialog.dismiss();
					mUpgradeNotifyDialog = null;
				}
			}
		});

		Button cancelBtn = (Button)serviceView.findViewById(R.id.cancelbtn);
		cancelBtn.setText(mContext.getResources().getString(R.string.cancel));
		cancelBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {//隐藏版本更新对话框
				if (mUpgradeNotifyDialog != null){
					mUpgradeNotifyDialog.dismiss();
					mUpgradeNotifyDialog = null;
				}
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		mUpgradeNotifyDialog = builder.create();
		mUpgradeNotifyDialog.show();
		mUpgradeNotifyDialog.setContentView(serviceView);
		FrameLayout.LayoutParams layout = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		layout.setMargins(FeatureFunction.dip2px(mContext, 10), 0, FeatureFunction.dip2px(mContext, 10), 0);
		serviceView.setLayoutParams(layout);
	}

	private void showDownloadApkDilog() {
		if (mVersion != null) {
			try {
				Uri uri = Uri.parse(mVersion.downloadUrl);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			} catch (Exception e) {
				/*Toast.makeText(mContext, R.string.upgradfail,
						Toast.LENGTH_LONG).show();*/
			}

		}
	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_btn:
			MainSearchDialog dialog = new MainSearchDialog(mContext,0);
			dialog.show();
			break;
		case R.id.add_btn:
			uploadImage2(MainActivity.this,mTitleLayout);
			break;
		case R.id.more_btn:
			uploadImage(MainActivity.this,mTitleLayout);
			break;
		default:
			break;
		}
	}

	/**
	 * 向服务器发送请求，获赠每日登录积分
	 */
	public void doPost() {
		if (!ResearchCommon.getNetWorkState()) {
			mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
			return;
		}

		new Thread() {
			@Override
			public void run() {
				Looper.prepare();// 用来在线程中支持显示Toast
				Object flag = HttpRequestServer.toGetTodayLogin(mLogin.uid);
				Map<String, String> success=null;
				try {
					success = (Map<String, String>) flag;
				} catch (Exception e) {
					/*Toast.makeText(mContext,
							"获取积分失败",
							Toast.LENGTH_SHORT).show();*/
				}
				if (success != null) {
					if (success.get("success").equals("true")) {
						int integral = Integer.parseInt(success.get("integral")
								.toString());
						if (integral > 0) {
							Toast.makeText(mContext,
									"每日登录赠送" + integral + "积分",
									Toast.LENGTH_SHORT).show();
						}
						UserFolder uf = new UserFolder(mLogin.uid);// 打开用户的文件夹
						uf.EditFile(uf.folders[0]
								+ "/"
								+ handleDate.getDateToInt(handleDate
										.getDateToInt()));// 获取今天的日期
						todayLogin = true;
					} else {
						/*
						 * Toast.makeText(mContext, flag + "没有赠送成功",
						 * Toast.LENGTH_SHORT).show();
						 */
					}
				}
				Looper.loop();
			}
		}.start();
	}
	public String picUrl = "";


	/**
	 * 向服务器发送请求，获取广告图片
	 */
	public void doPostToGetPic() {
		new Thread() {
			@Override
			public void run() {
				String str="";
				Looper.prepare();// 用来在线程中支持显示Toast
				Object flag = HttpRequestServer.getPHPMultiRequest("http://"+ResearchInfo.IP+ResearchInfo.PORT+"/lediao/index.php/ads/apiad/ad",null);//获取后台服务的数据
				//Object flag = HttpRequestServer.getRequest();//获取后台服务的数据
				//Toast.makeText(mContext, flag.toString()+"NMB doPostToGetPic ",Toast.LENGTH_SHORT).show();
				if (flag != null) {
					try {
						str="1";
				        list=(List<Map<String, String>>) flag;
						Toast.makeText(mContext, flag.toString()+" doPostToGetPic ",Toast.LENGTH_SHORT).show();
						str="2";
						//Map<String, String> success = (Map<String, String>) flag;
						//String date = success.get("time").toString();// 获取传来的date值
						//picUrl = success.get("image").toString();// 获取传来的date值
						//Toast.makeText(mContext, "doPostToGetPic picUrl:"+picUrl,Toast.LENGTH_SHORT).show();
						//int intdate = handleDate.getDateToInt(date);// 将传来的date值转换为只有日期的int类型
						//int today = handleDate.getDateToInt(handleDate.getDateToInt());// 获取今天的日期int
						
						str="3";
						new Thread() {
							@Override
							public void run() {
									Looper.prepare();// 用来在线程中支持显示Toast
								try {
									//Toast.makeText(mContext, "正在下载图片 picUrl:"+picUrl,Toast.LENGTH_SHORT).show();
									String filename = AndroidFolder.getSdCardPath()+ UserFolder.datapath + "add/addfile";
									File file = new File(filename);
									URL url = new URL(picUrl);
									URLConnection con = url.openConnection();// 打开连接
									InputStream is = con.getInputStream();// 输入流
									File folder = new File(AndroidFolder.getSdCardPath()+ UserFolder.datapath + "add");
									if (!folder.exists()) { // 如果目标文件夹不存在，创建文件夹
										folder.mkdirs();
									}
									if (file.exists()) { // 如果目标文件已经存在则删除
										file.delete();
									}
									//Toast.makeText(mContext, "正在下载图片 picUrl:"+picUrl,Toast.LENGTH_SHORT).show();
									int contentLength = con.getContentLength();// 获得文件的长度
									byte[] bs = new byte[1024];// 1K的数据缓冲
									int len;// 读取到的数据长度
									FileOutputStream os = new FileOutputStream(file);// 输出的文件流
									// 开始读取
									while ((len = is.read(bs)) != -1) {
										os.write(bs, 0, len);
									}
									// 完毕，关闭所有链接
									os.close();
									is.close();
									UserFolder uf = new UserFolder(mLogin.uid);//打开用户的文件夹
									uf.EditFile(uf.folders[3]+"/"+handleDate.getDateToInt(handleDate.getDateToInt()));//添加今天的日期文件 
									updateAdd=true;
								} catch (Exception e) {
									Toast.makeText(mContext, "错误2:"+e.getMessage(),Toast.LENGTH_SHORT).show();
								}
									Looper.loop();
							}
						}.start();
					} catch (Exception e) {
						Toast.makeText(mContext, "错误1: str:"+str+" "+e.getMessage()+" "+e.toString(),Toast.LENGTH_SHORT).show();
					}
					// Toast.makeText(mContext,
					// flag.toString()+" doPostToGetPic "+str2,Toast.LENGTH_SHORT).show();
				}
				Looper.loop();
			}
		}.start();
	}

	/**
	 * 查看今天是否登录
	 */
	public void checkToday() {
		String str = "";
		String str2 = "";
		try {
			if (mLogin == null) {
				return;
			}
			str2 = "1";
			UserFolder uf = new UserFolder(mLogin.uid);// 打开用户的文件夹
			str2 = "2";
			if (!todayLogin) {
				File[] list = uf.getFiles(uf.folders[0]);// 获取todayLogin文件夹内的文件
				str2 = "0";
				str = list.length + "";
				int today = handleDate.getDateToInt(handleDate.getDateToInt());// 获取今天的日期
				for (int i = 0; i < list.length; i++) {// 便利文件
					String fileName = list[i].getName();// 获取文件名称
					// str=fileName+" "+today;
					if (fileName.equals(today + "")) {// 判断是否是当前日期
						todayLogin = true;
					}
				}
				if (!todayLogin) {// 如果还是没有当前日期的文件则发送获赠积分请求
					doPost();
				}
			}
			str2 = "3";
			File[] list = uf.getFiles(uf.folders[1]);// 获取todayChat文件夹内的文件
			str2 = "4";
			int today = handleDate.getDateToInt(handleDate.getDateToInt());// 获取今天的日期
			for (int i = 0; i < list.length; i++) {// 便利文件
				String fileName = list[i].getName();// 获取文件名称
				// str=fileName+" "+today;
				if (fileName.equals(today + "")) {// 判断是否是当前日期
					FileReader fr=new FileReader(list[i]);
			        BufferedReader br=new BufferedReader(fr);
			        String line="";
			        while ((line=br.readLine())!=null) {
			        	HttpRequestServer.chatList.add(line);//添加到已经聊过天的list集合中
			        }
			        br.close();
			        fr.close();
			        break;
				}
			}
		
			list = uf.getFiles(uf.folders[2]);// 获取todayadd文件夹内的文件
			str2 = "6";
			for (int i = 0; i < list.length; i++) {// 便利文件
				String fileName = list[i].getName();// 获取文件名称
				str = fileName + " " + today;
				if (fileName.equals(today + "")) {// 判断是否是当前日期

					FileReader fr=new FileReader(list[i]);
			        BufferedReader br=new BufferedReader(fr);
			        String line="";
			        while ((line=br.readLine())!=null) {
			        	HttpRequestServer.addList.add(line);//添加到点过广告的list集合中
			        }
			        br.close();
			        fr.close();
					break;
				}
			}
			str2 = "7";
			if (!updateAdd) {// 判断今天是否检查广告更新
				list = uf.getFiles(uf.folders[3]);// 获取updateAdd文件夹内的文件
				str2 = "8";
				for (int i = 0; i < list.length; i++) {// 便利文件
					String fileName = list[i].getName();// 获取文件名称
					str = fileName + " " + today;
					if (fileName.equals(today + "")) {// 判断是否是当前日期
						updateAdd = true;
						break;
					}
				}
				if (!updateAdd) {// 如果广告图片没有更新
					//doPostToGetPic();
				}
			}
			/*Toast.makeText(
					mContext,
					" checktoday" + str2 + " todayAdd:" + todayAdd
							+ " todayLogin:" + todayLogin + " updateAdd:"
							+ updateAdd, Toast.LENGTH_SHORT).show();*/
		} catch (Exception e) {
			//Toast.makeText(mContext,e.getMessage() + " 错误 " + str2 + "" + e.toString(),Toast.LENGTH_SHORT).show();
		}
	}

	/**
　　         * 程序是否在前台运行
　　         *
　　         * @return
　　         */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device
		ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getApplicationContext().getPackageName();
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
            }
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}



	/*
	 * 初始化环信SDK
	 */
	private void initSdk() {
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		// 如果app启用了远程的service，此application:onCreate会被调用2次
		// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
		// 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

		if (processAppName == null ||!processAppName.equalsIgnoreCase("com.research")) {
		    Log.e("Runt", "enter the service process!");
		    //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
		    
		    // 则此application::onCreate 是被service 调用的，直接返回
		    return;
		}
		EMChat.getInstance().init(mContext);
		/**
		 * debugMode == true 时为打开，sdk 会在log里输入调试信息
		 * @param debugMode
		 * 在做代码混淆的时候需要设置成false
		 */
		// 设置sandbox测试环境
        // 建议开发者开发时设置此模式
            //EMChat.getInstance().setEnv(EMEnvMode.EMDevMode);
        
            // set debug mode in development process
		EMChat.getInstance().setDebugMode(false);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
		Log.i("Runt", "Mainactivity initSdk");

		
	}
	
	
	 /**
     * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = mContext.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }
    
    /**
     * 登录环信
     * @return
     */
    public boolean loginHuanxin(){

				//登录anychat
					int tip=0;
				try {
					while(mLogin==null||mLogin.phone==null){
						mLogin = ResearchCommon.getLoginResult(mContext);
					}
					/*if(EMChat.getInstance().isLoggedIn()){
						Log.i("Runt", String.format("已经登陆了环信"));
						return;
					}*/
					
					String pwdMD5=MD5Util.MD5(/*mLogin.password*/BaseActivity.HUANXIN_PWD);

					Log.i("Runt", String.format("准备登录环信 username:%s pwdMD5:%s",mLogin.phone,pwdMD5));

					///登录环信
					EMChatManager.getInstance().login(mLogin.phone,pwdMD5,new EMCallBack() {//回调
						@Override
						public void onSuccess() {
							// 登陆成功，保存用户名密码
							/*DemoApplication.getInstance().setUserName(username);
							DemoApplication.getInstance().setPassword(pwdMD5);*/
							runOnUiThread(new Runnable() {
								public void run() {
									//EMGroupManager.getInstance().loadAllGroups();
									//EMChatManager.getInstance().loadAllConversations();
									boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(mLogin.phone);
									if (!updatenick) {
										Log.i("Runt", "update current user nick fail"+mLogin.phone);
									}else{
										Log.i("Runt", "update current user nick success"+mLogin.phone);
									}
									Log.i("Runt", "MainActivity登陆聊天服务器成功！");
								}
							});
						}

						@Override
						public void onProgress(int progress, String status) {

						}

						@Override
						public void onError(int code, String message) {
							Log.i("Runt", String.format("MainActivity登陆聊天服务器失败！code:%s message:%s",code,message));
						}
					});
					/*DemoApplication.getInstance().setUserName(mLogin.phone);
					DemoApplication.getInstance().setPassword(pwdMD5);*/
				} catch (Exception e) {
					Log.i("Runt", e.getMessage()+" MainActivity "+tip+" "+mLogin);
				}
			
    	return true;
    }
    
    //实现ConnectionListener接口
    private class MyConnectionListener implements EMConnectionListener {
        @Override
    	public void onConnected() {
        	//已连接到服务器
			Log.i("Runt", "MainActivity MyConnectionListener 已连接到服务器");
    	}
    	@Override
    	public void onDisconnected(final int error) {
    		runOnUiThread(new Runnable() {

    			@Override
    			public void run() {
    				if(error == EMError.USER_REMOVED){
    					// 显示帐号已经被移除
    					Log.i("Runt", "MainActivity MyConnectionListener 显示帐号已经被移除");
    				}else if (error == EMError.CONNECTION_CONFLICT) {
    					// 显示帐号在其他设备登陆
    					Log.i("Runt", "MainActivity MyConnectionListener 显示帐号在其他设备登陆");
    				} else {
	    				if (NetUtils.hasNetwork(MainActivity.this)){
	    					//连接不到聊天服务器
	    					Log.i("Runt", "MainActivity MyConnectionListener 连接不到聊天服务器");
	    				}
	    				//当前网络不可用，请检查网络设置
    					Log.i("Runt", "MainActivity MyConnectionListener 当前网络不可用，请检查网络设置");
    				}
    			}
    		});
    	}
    }
	
}