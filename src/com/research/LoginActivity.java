package com.research;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.DemoApplication;
import com.research.R;
import com.research.Entity.Login;
import com.research.Entity.ResearchProject;
import com.research.Entity.LoginResult;
import com.research.fragment.ChatFragment;
import com.research.global.FeatureFunction;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.map.BMapApiApp;
import com.research.net.ResearchException;

import date.tool.MD5Util;

/**
 * 登录
 * @author dongli
 *
 */
public class LoginActivity extends BaseActivity implements OnClickListener{

	/**
	 * 定义全局变量
	 */

	private EditText mUserNameText, mPasswordText;
	private Button mLoginBtn,mRegisterBtn,mForgetPwdBtn;
	public SharedPreferences mPreferences;


	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.login);
		initComponent();
	}

	/**
	 * 初始化控件
	 */
	private void initComponent(){
		setTitleContent(0, 0, R.string.login);
		mPreferences = this.getSharedPreferences(ResearchCommon.REMENBER_SHARED, 0);
		mUserNameText = (EditText) findViewById(R.id.username);
		mPasswordText = (EditText) findViewById(R.id.password);

		mLoginBtn = (Button) findViewById(R.id.login_btn);
		mRegisterBtn = (Button) findViewById(R.id.register);
		mForgetPwdBtn = (Button) findViewById(R.id.forget_pwd);


		mLoginBtn.setOnClickListener(this);
		mRegisterBtn.setOnClickListener(this);
		mForgetPwdBtn.setOnClickListener(this);

		String username = mPreferences.getString(ResearchCommon.USERNAME, "");
		String password = mPreferences.getString(ResearchCommon.PASSWORD, "");

		mUserNameText.setText(username);
		mPasswordText.setText(password);

		setUIValue();
	}

	/**
	 * 给控件设置值
	 */
	private void setUIValue(){
		mUserNameText.setHint(mContext.getResources().getString(R.string.username));
		mPasswordText.setHint(mContext.getResources().getString(R.string.password));
		mLoginBtn.setText(mContext.getResources().getString(R.string.login));
	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.login_btn:
			checkLogin();
			break;
		case R.id.register:
			Intent intent = new Intent();
			intent.setClass(mContext, RegisterActivity.class);
			startActivityForResult(intent, 0);
			break;
		case R.id.forget_pwd:
			Intent forgetIntent = new Intent();
			forgetIntent.setClass(mContext, ForgetPwdActity.class);
			startActivity(forgetIntent);
			break;
		default:
			break;
		}
	}

	/**
	 * 检测用户名和密码是否合法
	 */
	private void checkLogin(){
		String username = mUserNameText.getText().toString().trim();
		String password = mPasswordText.getText().toString().trim();


		if(username.equals("")){
			Toast.makeText(mContext, R.string.please_input_username, Toast.LENGTH_SHORT).show();
			return;
		}
		if(password.equals("")){
			Toast.makeText(mContext, R.string.please_input_password, Toast.LENGTH_SHORT).show();
			return;
		}
		if(!ResearchCommon.verifyNetwork(mContext)){
			Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
			return;
		}

		Message message = new Message();
		message.what = GlobalParam.SHOW_PROGRESS_DIALOG;
		message.obj = mContext.getResources().getString(R.string.loading_login);
		mHandler.sendMessage(message);

		getLogin(username, password);
	}

	/**
	 * 处理消息
	 */
	private Handler mHandler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String dialogMsg = (String)msg.obj;
				showProgressDialog(dialogMsg);
				break;
			case GlobalParam.HIDE_PROGRESS_DIALOG:
				hideProgressDialog();
				break;

			case GlobalParam.MSG_LOAD_ERROR:
				hideProgressDialog();
				String error_Detail = (String)msg.obj;
				if(error_Detail != null && !error_Detail.equals("")){
					Toast.makeText(mContext,error_Detail,Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(mContext,R.string.load_error,Toast.LENGTH_LONG).show();
				}
				break;
			case GlobalParam.MSG_NETWORK_ERROR:
				hideProgressDialog();
				Toast.makeText(mContext,R.string.network_error,Toast.LENGTH_LONG).show();
				break;
			case GlobalParam.MSG_TICE_OUT_EXCEPTION:
				hideProgressDialog();
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

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onResume();
	}

	/**
	 * 登录
	 * @param username
	 * @param password
	 */
	private void getLogin(final String username, final String password) {
		new Thread() {
			@Override
			public void run() {
				try {
					LoginResult result = ResearchCommon.getResearchInfo().getLogin(username, password);
					if (result != null) {
						if (result.mState != null && result.mState.code == 0) {
							if (result.mLogin != null) {
								result.mLogin.password = password;
								ResearchCommon.saveLoginResult(mContext, result.mLogin);
								ResearchCommon.setUid(result.mLogin.uid);
							}
							Editor editor = mPreferences.edit();
							editor.putBoolean(ResearchCommon.ISREMENBER, true);
							editor.putString(ResearchCommon.USERNAME, username);
							editor.putString(ResearchCommon.PASSWORD, password);
							editor.commit();

							if(BMapApiApp.getContryList() == null
									||BMapApiApp.getContryList().mCountryList == null
									|| BMapApiApp.getContryList().mCountryList.size()<=0){
								BMapApiApp.setContryList(ResearchCommon.getResearchInfo().getCityAndContryUser());
							}
							setResult(RESULT_OK);
							final String pwdMD5=MD5Util.MD5(/*password*/HUANXIN_PWD);
							Log.i("Runt", String.format("准备登录环信 username:%s pwdMD5:%s",username,pwdMD5));

							///登录环信
							EMChatManager.getInstance().login(username,pwdMD5,new EMCallBack() {//回调
								@Override
								public void onSuccess() {
									// 登陆成功，保存用户名密码
									/*DemoApplication.getInstance().setUserName(username);
									DemoApplication.getInstance().setPassword(pwdMD5);*/
									runOnUiThread(new Runnable() {
										public void run() {
											//EMGroupManager.getInstance().loadAllGroups();
											//EMChatManager.getInstance().loadAllConversations();
											boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(username);
											if (!updatenick) {
												Log.i("Runt", "update current user nick fail"+username);
											}else{
												Log.i("Runt", "update current user nick success"+username);
											}
											Log.i("Runt", "登陆聊天服务器成功！");
											mHandler.sendEmptyMessage(GlobalParam.HIDE_PROGRESS_DIALOG);
											LoginActivity.this.finish();	
										}
									});
								}

								@Override
								public void onProgress(int progress, String status) {

								}

								@Override
								public void onError(int code, String message) {
									Message message1 = new Message();
									message1.what = GlobalParam.MSG_TICE_OUT_EXCEPTION;
									message1.obj = mContext.getResources().getString(R.string.login_error);
									mHandler.sendMessage(message1);
									Log.i("Runt", String.format("登陆聊天服务器失败！code:%s message:%s",code,message));
								}
							});

						}else {
							Message message = new Message();
							message.what = GlobalParam.MSG_TICE_OUT_EXCEPTION;
							if(result.mState != null && result.mState.errorMsg != null && !result.mState.errorMsg.equals("")){
								message.obj = result.mState.errorMsg;
							}else {
								message.obj = mContext.getResources().getString(R.string.login_error);
							}
							mHandler.sendMessage(message);
						}
					} else {
						Message message = new Message();
						message.what = GlobalParam.MSG_LOAD_ERROR;
						message.obj = mContext.getResources().getString(R.string.login_error);
						mHandler.sendMessage(message);
					}
				} catch (ResearchException e) {
					Message msg = new Message();
					msg.what = GlobalParam.MSG_TICE_OUT_EXCEPTION;
					msg.obj = mContext.getResources().getString(R.string.timeout);
					mHandler.sendMessage(msg);
				}
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 键盘返回事件
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			/*setResult(GlobalParam.RESULT_EXIT);*/
			Intent intent = new Intent();
			intent.setAction(GlobalParam.EXIT_ACTION);
			sendBroadcast(intent);
			LoginActivity.this.finish();
			this.finish();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 注册成功时销毁登陆界面
	 * @param arg0 requestCode 请求值
	 * @param arg1 resultCode 请求值
	 */
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		//super.onActivityResult(arg0, arg1, arg2);
		if(arg0 == 0 && arg1 == 1){
			Log.e("LoginActivity_onActivityResult","销毁登陆界面");
			LoginActivity.this.finish();
		}
	}


}
