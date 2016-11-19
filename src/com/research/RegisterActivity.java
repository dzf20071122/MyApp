package com.research;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.research.Entity.LoginResult;
import com.research.Entity.ResearchJiaState;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.http.post.HttpRequestServer;
import com.research.net.ResearchException;

import date.tool.MD5Util;

/**
 * 新用户注册
 * 
 * @author dongli
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {

	/**
	 * 定义全局变量
	 */
	private Button mBuyCodeBtn;
	private CheckBox mProtrol;
	private EditText mPhoneNumberEdit, mPwdEdit, mConfirmpwdEdit, mValidEdit,
			mCommendEdit;
	private TextView mWatchProtrolText;
	private String mInputNumber, mInputPwd, mInputConfirmName, mInputValidCode;
	Map<String, String> params = new HashMap<String, String>();

	private int mTotalTime = 60;
	private Button mReSendCode;
	private boolean mIsCheck = true;

	/**
	 * 处理消息
	 */
	private Handler mHandler = new Handler() {
		@SuppressLint("NewApi")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.i("dzf..handle..",msg.what+"msg.what");
			switch (msg.what) {
			case GlobalParam.REGISTER_REQUEST:// 检测注册结果
				LoginResult register = (LoginResult) msg.obj;
				
				if (register == null) {
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.register_faile), Toast.LENGTH_LONG)
							.show();
					return;
				}
				if (register.mState != null) {
					Log.i("dzf..mState..",register.mState.code + "");
					if (register.mState.code == 0) {
						if (register.mLogin != null) {
							register.mLogin.password = mInputPwd;
							ResearchCommon.saveLoginResult(mContext,
									register.mLogin);
							ResearchCommon.setUid(register.mLogin.uid);
						}

						Editor editor = mContext.getSharedPreferences(
								ResearchCommon.REMENBER_SHARED, 0).edit();
						editor.putBoolean(ResearchCommon.ISREMENBER, true);
						editor.putString(ResearchCommon.USERNAME, mInputNumber);
						editor.putString(ResearchCommon.PASSWORD, mInputPwd);
						editor.commit();
						setResult(1);// 注册成功释放登陆界面
						mContext.sendBroadcast(new Intent(
								GlobalParam.ACTION_SHOW_REGISTER_REQUEST));
						///登录环信
						final String pwdMD5=MD5Util.MD5(HUANXIN_PWD);
						EMChatManager.getInstance().login(mInputNumber,pwdMD5,new EMCallBack() {//回调
							@Override
							public void onSuccess() {
								runOnUiThread(new Runnable() {
									public void run() {
										EMGroupManager.getInstance().loadAllGroups();
										EMChatManager.getInstance().loadAllConversations();
										Log.d("Runt", "登陆聊天服务器成功！");
									}
								});
							}

							@Override
							public void onProgress(int progress, String status) {

							}

							@Override
							public void onError(int code, String message) {
								Log.i("Runt", "登陆聊天服务器失败！");
							}
						});
						RegisterActivity.this.finish();
					} else {
						Toast.makeText(mContext, register.mState.errorMsg,
								Toast.LENGTH_LONG).show();
					}
				}
				break;
			case GlobalParam.MSG_CHECK_VALID_ERROR:
				ResearchJiaState validState = (ResearchJiaState) msg.obj;
				if (validState == null) {
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.commit_data_error),
							Toast.LENGTH_LONG).show();
					return;
				}
				String validErrorMsg = validState.errorMsg;
				if (validErrorMsg == null || validErrorMsg.equals("")) {
					validErrorMsg = mContext.getResources().getString(
							R.string.commit_data_error);
				}
				Toast.makeText(mContext, validErrorMsg, Toast.LENGTH_LONG)
						.show();
				break;
			case GlobalParam.MSG_CHECK_STATE:
				ResearchJiaState status = (ResearchJiaState) msg.obj;
				if (status != null) {
					mTotalTime = 60;
					if (status.code == 0) {
						mReSendCode.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gray_btn));
						mValidEdit.setText(status.validCode);
						mReSendCode.setEnabled(false);
						mReSendCode.setText(mContext.getResources().getString(
								R.string.Countdown)
								+ mTotalTime + ")");
						Message timeMessage = mHandler
								.obtainMessage(GlobalParam.MSG_UPDATEA_TIP_TIME);
						mHandler.sendMessageDelayed(timeMessage, 1000);
					} else {
						Toast.makeText(mContext, status.errorMsg,
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(mContext, R.string.send_veri_code,
							Toast.LENGTH_LONG).show();
				}
				break;
			case GlobalParam.MSG_UPDATEA_TIP_TIME:
				mTotalTime--;
				mReSendCode.setText(mContext.getResources().getString(
						R.string.Countdown)
						+ mTotalTime + ")");

				if (mTotalTime > 0) {
					Message backMessage = mHandler
							.obtainMessage(GlobalParam.MSG_UPDATEA_TIP_TIME);
					mHandler.sendMessageDelayed(backMessage, 1000); // send
																	// message
				} else {
					mTotalTime = 90;
					mReSendCode.setText(mContext.getResources().getString(
							R.string.get_valid_code));
					mReSendCode.setBackground(mContext.getResources()
							.getDrawable(R.drawable.login_btn));
					mReSendCode.setEnabled(true);
				}

				break;
			default:
				break;
			}
		}
	};

	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user);
		mContext = this;
		initCompent();
	}

	/**
	 * 初始化控件
	 */
	private void initCompent() {
		setTitleContent(R.drawable.back_btn, 0, R.string.register);
		mLeftBtn.setOnClickListener(this);

		mBuyCodeBtn = (Button) findViewById(R.id.buy_code);
		mReSendCode = (Button) findViewById(R.id.get_valid_code_btn);
		mBuyCodeBtn.setVisibility(View.VISIBLE);

		mBuyCodeBtn.setOnClickListener(this);
		mReSendCode.setOnClickListener(this);

		mProtrol = (CheckBox) findViewById(R.id.disagree_protrol);
		mProtrol.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				mIsCheck = isChecked;
			}
		});

		mPhoneNumberEdit = (EditText) findViewById(R.id.phone_number);
		mConfirmpwdEdit = (EditText) findViewById(R.id.confirm_password);
		mPwdEdit = (EditText) findViewById(R.id.password);
		mValidEdit = (EditText) findViewById(R.id.valid_code);
		mCommendEdit = (EditText) findViewById(R.id.commend_number);
		mWatchProtrolText = (TextView) findViewById(R.id.watch_protrol);
		mWatchProtrolText.setOnClickListener(this);
	}

	/**
	 * 检测输入内容是否合法
	 */
	private void checkNumber() {
		boolean isCheck = true;
		String hintMsg = "";
		mInputNumber = mPhoneNumberEdit.getText().toString();
		mInputConfirmName = mConfirmpwdEdit.getText().toString();
		mInputPwd = mPwdEdit.getText().toString();
		mInputValidCode = mValidEdit.getText().toString();
		if (mInputNumber == null || mInputNumber.equals("")) {
			isCheck = false;
			hintMsg = mContext.getResources().getString(
					R.string.please_input_phone_number);
		} else if (!ResearchCommon.isMobileNum(mInputNumber)) {
			isCheck = false;
			hintMsg = mContext.getResources().getString(
					R.string.check_phone_number_hint);
		} else if (mInputPwd == null || mInputPwd.equals("")) {
			isCheck = false;
			hintMsg = mContext.getResources().getString(
					R.string.please_input_pwd);
		} else if (mInputConfirmName == null || mInputConfirmName.equals("")) {
			isCheck = false;
			hintMsg = mContext.getResources().getString(
					R.string.please_input_confim_pwd);
		} else if (!mInputConfirmName.equals(mInputPwd)) {
			isCheck = false;
			hintMsg = mContext.getResources()
					.getString(R.string.check_pwd_hint);
		} else if (mInputValidCode == null || mInputValidCode.equals("")) {
			isCheck = false;
			hintMsg = mContext.getResources().getString(
					R.string.please_input_valid_hint);
		}
		if (!mIsCheck) {
			isCheck = false;
			hintMsg = mContext.getResources().getString(
					R.string.check_protrol_hint);
		}
		if (!isCheck && hintMsg != null && !hintMsg.equals("")) {
			Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
		}else{
			getTigerRegister();
		}
//		getTigerRegister();
		/*
		 * if (/isCheck) { register(); }
		 */
	}

	/**
	 * 注册
	 */
	private void register() {
		if (!ResearchCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread() {
			public void run() {
				try {
					ResearchCommon.sendMsg(mBaseHandler,
							BASE_SHOW_PROGRESS_DIALOG, R.string.commit_dataing);
					// ++检测验证码++//
//					ResearchJiaState validState = ResearchCommon
//							.getResearchInfo().checkVerCode(mInputNumber,
//									mInputValidCode);
//					// -检测验证码-//
//					Log.i("dzf..", validState.code + "...1");
//					if (validState != null && validState.code == 0) {// 注册
//						LoginResult status = ResearchCommon.getResearchInfo()
//								.register(mInputNumber, mInputPwd,
//										mInputValidCode);
//						Log.i("dzf..",status + "");
//						ResearchCommon.sendMsg(mHandler,
//								GlobalParam.REGISTER_REQUEST, status);
//					} else {
//						ResearchCommon.sendMsg(mHandler,
//								GlobalParam.MSG_CHECK_VALID_ERROR, validState);
//					}
					
					LoginResult status = ResearchCommon.getResearchInfo()
							.register(mInputNumber, mInputPwd,
									mInputValidCode);
					Log.i("dzf..",status + "");
					ResearchCommon.sendMsg(mHandler,
							GlobalParam.REGISTER_REQUEST, status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);

				} catch (ResearchException e) {
					e.printStackTrace();
					ResearchCommon.sendMsg(mBaseHandler,
							BASE_MSG_TIMEOUT_ERROR, mContext.getResources()
									.getString(e.getStatusCode()));
				} catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	/**
	 * 获取验证码
	 */
	private void getVeriCode() {

		mInputNumber = mPhoneNumberEdit.getText().toString();
		if (!ResearchCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		if (mInputNumber == null || mInputNumber.equals("")) {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.please_input_phone_number),
					Toast.LENGTH_LONG).show();
			return;
		} else if (!ResearchCommon.isMobileNum(mInputNumber)) {
			Toast.makeText(
					mContext,
					mContext.getResources().getString(
							R.string.check_phone_number_hint),
					Toast.LENGTH_LONG).show();
			return;
		}
		new Thread() {
			public void run() {
				try {
					ResearchCommon.sendMsg(mBaseHandler,
							BASE_SHOW_PROGRESS_DIALOG, mContext.getResources()
									.getString(R.string.get_code));
					int type = 0;

					ResearchJiaState status = ResearchCommon.getResearchInfo()
							.getVerCode(mInputNumber, type);

					ResearchCommon.sendMsg(mHandler,
							GlobalParam.MSG_CHECK_STATE, status);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (ResearchException e) {
					e.printStackTrace();
					ResearchCommon.sendMsg(mBaseHandler,
							BASE_MSG_TIMEOUT_ERROR, mContext.getResources()
									.getString(e.getStatusCode()));
				} catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
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
			RegisterActivity.this.finish();
			break;
		case R.id.get_valid_code_btn:
			getVeriCode();
			break;
		case R.id.buy_code:
			checkNumber();
			break;
		case R.id.watch_protrol:
			Intent forgetPwdIntent = new Intent();
			forgetPwdIntent.setClass(mContext, UserProtocolActivity.class);
			startActivity(forgetPwdIntent);
			break;
		default:
			break;
		}
	}

	public void getTigerRegister() {
		params.put("OnePassword", mInputPwd);
		params.put("OnePasswords", mInputConfirmName);
		params.put("Name", mInputNumber);
		params.put("CardNo", "99999999999");
		params.put("Mail", "yiliao@qq.com");
		params.put("Mobile", mInputNumber);
		params.put("username", mInputNumber);
		new Thread() {
			@Override
			public void run() {
				ResearchCommon.sendMsg(
						mBaseHandler,
						BASE_SHOW_PROGRESS_DIALOG,
						mContext.getResources().getString(
								R.string.add_more_loading));
				Looper.prepare();// 用来在线程中支持显示Toast
				try {
					// 调用环信sdk注册方法
					Log.i("Runt", "注册环信账号");
					final String pwdMD5=MD5Util.MD5(HUANXIN_PWD);
					EMChatManager.getInstance().createAccountOnServer(mInputNumber, pwdMD5);
					Log.i("Runt","注册环信账号成功"+mInputNumber+" "+pwdMD5);
					register();
				} catch (final EaseMobException e) {
					// 注册失败
					int errorCode = e.getErrorCode();
					if (errorCode == EMError.NONETWORK_ERROR) {
						Toast.makeText(
								getApplicationContext(),
								"网络异常，请检查网络！",
								Toast.LENGTH_SHORT).show();
					} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
						/*Toast.makeText(
								getApplicationContext(),
								"环信用户已存在！",
								Toast.LENGTH_SHORT).show();*/
						register();
					} else if (errorCode == EMError.UNAUTHORIZED) {
						Toast.makeText(
								getApplicationContext(),
								"注册失败，无权限！",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(
								getApplicationContext(),
								"注册失败: " + e.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				}
				mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				Looper.loop();
			}
		}.start();
	}

}