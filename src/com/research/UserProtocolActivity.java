package com.research;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

import com.research.R;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.net.ResearchException;

/**
 * 用户协议
 * @author dongli
 *
 */
public class UserProtocolActivity extends BaseActivity implements OnClickListener{

	private boolean mIsAgree = true;
	
	private boolean mIsHideAgreeBtn; 
	
	WebView mWebView;
	private String mUserProtocol;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg); 
			switch (msg.what) {
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				mWebView.loadData(mUserProtocol,"text/html; charset=UTF-8", null);
				break;

			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_protocol);
		mIsHideAgreeBtn = getIntent().getBooleanExtra("hide_btn",false);
		mContext = this;
		getProtocol();
		initCompnet();
	}
	
	private void initCompnet(){
	    setTitleContent(R.drawable.back_btn, 0, R.string.user_protocol);
	    mLeftBtn.setOnClickListener(this);
	    
		
		
		mWebView = (WebView)findViewById(R.id.webview);
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
		mWebView.setPadding(5,5, 5, 5);
	}
	
	/*
	 * 获取用协议
	 */
	private void getProtocol(){
		if(!ResearchCommon.getNetWorkState()){
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					ResearchCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, "数据加载中,请稍后...");
					mUserProtocol = ResearchCommon.getResearchInfo().getProtocol();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					mHandler.sendEmptyMessage(GlobalParam.MSG_SHOW_LOAD_DATA);
				} catch (ResearchException e) {
					e.printStackTrace();
					ResearchCommon.sendMsg(mBaseHandler, BASE_MSG_NETWORK_ERROR,
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			UserProtocolActivity.this.finish();
			break;

		default:
			break;
		}
	}

}
