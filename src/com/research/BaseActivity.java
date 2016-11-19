package com.research;

import java.util.List;

import com.research.R;
import com.research.Entity.Login;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.map.BMapApiApp;
import com.research.widget.CustomProgressDialog;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 所有页面的父类
 * @author dongli
 *
 */
public class BaseActivity extends FragmentActivity implements OnClickListener{
	
	/*
	 * 定义全局变量
	 */
	public final static String SUB_DETAIL_CHANGE = "com.qiyue.subscription_detail_change";
	public final static String HUANXIN_PWD="1a2b3c456";
	public static boolean isActivity=false;
	public static boolean isVoice=false;
	public static boolean isCall=false;
    public static boolean isActive=false;
	protected final static int CONTROL_SHOW=0;//控件显示
	protected final static int CONTROL_HIDE=8;//控件隐藏
	

	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerFinishCurrentPageWorkMonitor();
	}
	
	
	/**
	 * 注册通知事件
	 */
	public void registerFinishCurrentPageWorkMonitor() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.ACTION_DESTROY_CURRENT_ACTIVITY);
		registerReceiver(mReceiver, filter);
	}
	
	/*
	 * 处理页面公用通知
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent == null){
				return;
			}//销毁整个运用
			if (intent.getAction().equals(GlobalParam.ACTION_DESTROY_CURRENT_ACTIVITY)) {
				String name = ((Activity)mContext).getLocalClassName();
				if(!name.equals("ChatsTab")&& !name.equals("ContactsTab")&& !name.equals("ContactAddrActivity")
					&& !name.equals("ContactPinLiActivity")	&& !name.equals("ContactAddTimeActivity")
					&& !name.equals("ContactProjectActivity") && !name.equals("ContactSubjectActivity")
					&& !name.equals("FindTab") && !name.equals("ProfileTab")
					&& !name.equals("SubTab")){
					((Activity)mContext).finish();
					
				}
				
			}
		}
	};
	
	
	public final static int BASE_SHOW_PROGRESS_DIALOG  = 0x11112;
    public final static int BASE_HIDE_PROGRESS_DIALOG  = 0x11113;
    public final static int BASE_MSG_NETWORK_ERROR = 11114;
    public final static int BASE_MSG_TIMEOUT_ERROR = 11115;
	protected CustomProgressDialog mProgressDialog;
   
	protected Context mContext;
	protected Activity activity;
	
	/*
	 * 处理整个运用公用消息
	 */
	 public Handler mBaseHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            case BASE_SHOW_PROGRESS_DIALOG://显示提示框
                String dialogMsg = (String)msg.obj;
                showProgressDialog(dialogMsg);
                break;
            case BASE_HIDE_PROGRESS_DIALOG://隐藏提示框
                hideProgressDialog();
                String hintMsg = (String)msg.obj;
                if(hintMsg!=null && !hintMsg.equals("")){
                	Toast.makeText(mContext,hintMsg,Toast.LENGTH_LONG).show();
                }
                break;
            case BASE_MSG_NETWORK_ERROR://网络连接错误
            	Toast.makeText(mContext,R.string.network_error,Toast.LENGTH_LONG).show();
            	  hideProgressDialog();
            	break;
            case BASE_MSG_TIMEOUT_ERROR://连接网络超时
            	  hideProgressDialog();
            	  String timeOutMsg = (String)msg.obj;
            	  Toast.makeText(mContext, timeOutMsg, Toast.LENGTH_LONG).show();
            	break;
            }
        }
    };
	    
    public void showProgressDialog(String msg,Context context){
		mProgressDialog = new CustomProgressDialog(mContext);
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}
	public void showProgressDialog(String msg){
		mProgressDialog = new CustomProgressDialog(mContext);
		mProgressDialog.setMessage(msg);
		mProgressDialog.show();
	}
	
	public void hideProgressDialog(){
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	/**+++ for title bar +++*/
    protected ImageView mLeftIcon, mRightBtn,mSearchBtn,mAddBtn,mMoreBtn;
    //protected LinearLayout mRightBtn;
    protected TextView titileTextView,mFristTitlte,mTrowTitle,mRightTextBtn;
    protected RelativeLayout mFirstLayout;
    protected LinearLayout mLeftBtn,mCenterLayout;
    
    protected void setTitleContent(int left_src_id, int right_src_id, int title_id){
        mLeftBtn = (LinearLayout)findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)findViewById(R.id.left_icon);
        mRightBtn = (ImageView)findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)findViewById(R.id.search_btn);
        mAddBtn = (ImageView)findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)findViewById(R.id.more_btn);
        
        titileTextView = (TextView)findViewById(R.id.title);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if (right_src_id != 0) {
        	mRightBtn.setImageResource(right_src_id);
        	mRightBtn.setVisibility(View.VISIBLE);
        }
        
        if (title_id != 0) {
            titileTextView.setText(title_id);
        }
    }
    
    protected void setTrowMenuTitleContent(int left_src_id, int right_src_id, 
    		String firstTitlte,String trowTitle){
        mLeftBtn = (LinearLayout)findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)findViewById(R.id.left_icon);
        mRightBtn = (ImageView)findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)findViewById(R.id.search_btn);
        mAddBtn = (ImageView)findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)findViewById(R.id.more_btn);
        
        titileTextView = (TextView)findViewById(R.id.title);
        titileTextView.setVisibility(View.GONE);
        
        mFristTitlte= (TextView)findViewById(R.id.other_title);
        mTrowTitle= (TextView)findViewById(R.id.child_title);
        if(firstTitlte!=null && !firstTitlte.equals("")){
        	mFristTitlte.setText(firstTitlte);
        	mFristTitlte.setVisibility(View.VISIBLE);
        }
        if(trowTitle!=null && !trowTitle.equals("")){
        	mTrowTitle.setText(trowTitle);
        	mTrowTitle.setVisibility(View.VISIBLE);
        }
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if (right_src_id != 0) {
        	mRightBtn.setImageResource(right_src_id);
        	mRightBtn.setVisibility(View.VISIBLE);
        }
        
       
    }
    
    protected void setTitleContent(int left_src_id,boolean isShowSearch, int right_src_id, int title_id){
        mLeftBtn = (LinearLayout)findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)findViewById(R.id.left_icon);
        mRightBtn = (ImageView)findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)findViewById(R.id.search_btn);
        mAddBtn = (ImageView)findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)findViewById(R.id.more_btn);
        
        titileTextView = (TextView)findViewById(R.id.title);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if(isShowSearch){
        	mSearchBtn.setVisibility(View.VISIBLE);
        }
        
        if (right_src_id != 0) {
        	mRightBtn.setImageResource(right_src_id);
        	mRightBtn.setVisibility(View.VISIBLE);
        }
        
        if (title_id != 0) {
            titileTextView.setText(title_id);
        }
    }
    
    protected void setTitleContent(int left_src_id, boolean showSearchIcon,
    		boolean showAddIcon,boolean showMoreIcon,int title_id){
        mLeftBtn = (LinearLayout)findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)findViewById(R.id.left_icon);
        mRightBtn = (ImageView)findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)findViewById(R.id.search_btn);
        mAddBtn = (ImageView)findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)findViewById(R.id.more_btn);
        titileTextView = (TextView)findViewById(R.id.title);
        mCenterLayout = (LinearLayout)findViewById(R.id.center_layout);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        if(showSearchIcon){
        	mSearchBtn.setVisibility(View.VISIBLE);
        }
       
        if(showAddIcon){
        	mAddBtn.setVisibility(View.VISIBLE);
        }
        if(showMoreIcon){
        	mMoreBtn.setVisibility(View.VISIBLE);
        }
        
        
        if (title_id != 0) {
            titileTextView.setText(title_id);
        }
    }
    
    
    protected void setRightTextTitleContent(int left_src_id, String right_src_id, int title_id){
        mLeftBtn = (LinearLayout)findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)findViewById(R.id.left_icon);
        mRightBtn = (ImageView)findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)findViewById(R.id.search_btn);
        mAddBtn = (ImageView)findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)findViewById(R.id.more_btn);
        
        titileTextView = (TextView)findViewById(R.id.title);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if (right_src_id != null && right_src_id.equals("")) {
        	mRightTextBtn.setText(right_src_id);
        	mRightTextBtn.setVisibility(View.VISIBLE);
        }
        
        if (title_id != 0) {
            titileTextView.setText(title_id);
        }
    }
    
    
    protected void setRightTextTitleContent(int left_src_id, int right_src_id, int title_id){
        mLeftBtn = (LinearLayout)findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)findViewById(R.id.left_icon);
        mRightBtn = (ImageView)findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)findViewById(R.id.right_text_btn);
        //mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        mSearchBtn = (ImageView)findViewById(R.id.search_btn);
        mAddBtn = (ImageView)findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)findViewById(R.id.more_btn);
        
        titileTextView = (TextView)findViewById(R.id.title);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if (right_src_id != 0) {
        	mRightTextBtn.setText(right_src_id);
        	mRightTextBtn.setVisibility(View.VISIBLE);
        }
        
        if (title_id != 0) {
            titileTextView.setText(title_id);
        }
    }
    
    protected void setTitleContent(int left_src_id, int right_src_id, String title_text){
        mLeftBtn = (LinearLayout)findViewById(R.id.left_btn);
        mLeftIcon = (ImageView)findViewById(R.id.left_icon);
        mRightBtn = (ImageView)findViewById(R.id.right_btn);
        mRightTextBtn = (TextView)findViewById(R.id.right_text_btn);
        mSearchBtn = (ImageView)findViewById(R.id.search_btn);
        mAddBtn = (ImageView)findViewById(R.id.add_btn);
        mMoreBtn = (ImageView)findViewById(R.id.more_btn);
        titileTextView = (TextView)findViewById(R.id.title);
       // mRightBtn = (LinearLayout)findViewById(R.id.right_btn);
        
        if (left_src_id != 0) {
        	mLeftIcon.setImageResource(left_src_id);
        }
        
        if (right_src_id != 0) {
        	mRightBtn.setImageResource(right_src_id);
        	mRightBtn.setVisibility(View.VISIBLE);
        }
        
        if (title_text != null && !title_text.equals("")) {
            titileTextView.setText(title_text);
        }
    }
    
    
    /**--- for title bar ---*/
    @Override
    protected void onStop() {
        super.onStop();
        ResearchCommon.appOnStop(mContext);
			isActive = false;// 记录当前已经进入后台
		
		//BaseMethod.showToast("onStop", this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        ResearchCommon.appOnResume(mContext);
		isActive = true;// 记录当前已经进入前台
		/*if(isCall){//判断请求是否开启语音

			Intent callIntent=new Intent();
			if(BaseActivity.isVoice){
				callIntent.setClass(mContext, CallVoiceActivity.class);
			}else{
				callIntent.setClass(mContext, CallVideoActivity.class);
	    	}
			callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//把之前的清除掉
	    	callIntent.putExtra("mydata", ResearchCommon.getLoginResult(mContext));
	    	callIntent.putExtra("todata", MainActivity.fCustomerVo);
	    	callIntent.putExtra("isComingCall", true);
	    	startActivity(callIntent);
		}*/
		

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
   /*
    * 按钮点击事件
    * (non-Javadoc)
    * @see android.view.View.OnClickListener#onClick(android.view.View)
    */
	@Override
	public void onClick(View v) {
		if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
			InputMethodManager manager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
		}
	}


	/*
	 * 页面销毁时释放通知
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
        isActive=false;
		if(mReceiver!=null){
			unregisterReceiver(mReceiver);
		}
	}


	/*@Override
	public void OnAnyChatUserInfoUpdate(int dwUserId, int dwType) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnAnyChatFriendStatus(int dwUserId, int dwStatus) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnAnyChatVideoCallEvent(int dwEventType, int dwUserId,
			int dwErrorCode, int dwFlags, int dwParam, String userStr) {
		String[] str=userStr.split(" ");
		Toast.makeText(mContext,
				String.format("BaseActivity userStr:%s dwEventType:%s",userStr,dwEventType),
				Toast.LENGTH_SHORT).show();
		
		if(str.length==4&&dwEventType==AnyChatDefine.BRAC_VIDEOCALL_EVENT_REQUEST){//呼叫请求
			isCall=true;
			CallSound.startCallSound(mContext);//开启呼叫声音
			Login fCustomerVo=new Login();
			fCustomerVo.uid=str[0];//获取请求方的id
			fCustomerVo.nickname=str[1];//获取请求方的nickname
			fCustomerVo.headsmall=str[2];//获取请求放的小头像
			fCustomerVo.content=str[3];//获取请求放的
			Intent callIntent=new Intent();
			if(dwFlags==AnyChatDefine.BRAC_VIDEOCALL_FLAGS_AUDIO){
				isVoice=true;
				callIntent.setClass(mContext, CallVoiceActivity.class);
			}else{
				isVoice=false;
				callIntent.setClass(mContext, CallVideoActivity.class);
	    	}
			if(!isActive){//判断是否为后台
				callIntent=null;
				return;
			}
			callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);//把之前的清除掉
	    	callIntent.putExtra("mydata", ResearchCommon.getLoginResult(mContext));
	    	callIntent.putExtra("todata", fCustomerVo);
	    	callIntent.putExtra("isComingCall", true);
	    	startActivity(callIntent);
		}else if(str.length==4&&dwEventType==AnyChatDefine.BRAC_VIDEOCALL_EVENT_FINISH){
			isCall=false;
			CallSound.stopCallSound();//关闭声音
		}
		
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnAnyChatConnectMessage(boolean bSuccess) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
		// TODO Auto-generated method stub
		
	}
	*/
	
}
