package com.research;

import java.util.Map;

import com.research.R;
import com.research.Entity.ResearchJiaState;
import com.research.Entity.Login;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.http.post.HttpRequestServer;
import com.research.net.ResearchException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 修改密码
 * @author dongli
 *
 */
public class ModifyPwdActivity extends BaseActivity{
	
	private Button mOkBtn;
	private EditText mOldPwdEdit,mNewPwdEdit,mConfirmPwdEdit;
	private String mInputOldPwd,mInputNewPwd,mInputConfirmPwd;

	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				ResearchJiaState state = (ResearchJiaState)msg.obj;
				if(state == null ){
					Toast.makeText(mContext,R.string.commit_data_error, Toast.LENGTH_LONG).show();
				}
				if(state.code == 0){
					Login login = ResearchCommon.getLoginResult(mContext);
					if(login !=null){
						login.password = mInputNewPwd;
						ResearchCommon.saveLoginResult(mContext, login);
					}
					String hintMsg =state.errorMsg;
					if(hintMsg == null || hintMsg.equals("") ){
						hintMsg =mContext.getResources().getString(R.string.modity_success) ;
					}
					Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
					ModifyPwdActivity.this.finish();
				}else{
					String hintMsg =state.errorMsg;
					if(hintMsg == null || hintMsg.equals("") ){
						hintMsg = mContext.getResources().getString(R.string.commit_data_error);
					}
					Toast.makeText(mContext, hintMsg, Toast.LENGTH_LONG).show();
				}
				break;

			default:
				break;
			}
		}
		
	};
	
	
	/*
	 * 导入控件
	 * (non-Javadoc)
	 * @see com.research.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.modify_pwd_view);
		mContext = this;
		initCompent();
	}
	
	/*
	 * 实例化控件
	 */
	private void initCompent(){
		setTitleContent(R.drawable.back_btn,0,R.string.modify_pwd);
		mLeftBtn.setOnClickListener(this);
		
		mOkBtn = (Button)findViewById(R.id.ok);
		mOkBtn.setOnClickListener(this);
		
		mOldPwdEdit = (EditText)findViewById(R.id.old_pwd);
		mNewPwdEdit = (EditText)findViewById(R.id.new_pwd);
		mConfirmPwdEdit = (EditText)findViewById(R.id.confirm_pwd);
	}

	
	/*
	 * 修改密码
	 */
	private void modifyPwd(){
		if (!ResearchCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}
		new Thread(){
			public void run() {
				try {
					ResearchJiaState state = ResearchCommon.getResearchInfo().editPasswd(mInputOldPwd, mInputNewPwd);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
					ResearchCommon.sendMsg(mHandler, GlobalParam.MSG_CHECK_STATE,state);
				} catch (ResearchException e) {
					e.printStackTrace();
					ResearchCommon.sendMsg(mBaseHandler, BASE_MSG_TIMEOUT_ERROR, 
							mContext.getResources().getString(e.getStatusCode()));
				}catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}
	
	/**
	 * 修改另一个系统的密码
	 */
	public void modifyPwdOther(){

		if (!ResearchCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
			return;
		}

		new Thread(){
			@Override
			public void run() {
				ResearchCommon.sendMsg(mBaseHandler, BASE_SHOW_PROGRESS_DIALOG, 
						mContext.getResources().getString(R.string.send_request));
				Login login=ResearchCommon.getLoginResult(mContext);
				Object flag=HttpRequestServer.getPHPRequest("http://oa3.xrmg88.com/PwdUpdMobile.aspx?MemberName="+login.phone+"&MemberPwd="+mInputNewPwd, null);
				Map<String, String> map=(Map<String, String>) flag;
				int code=Integer.parseInt(map.get("code"));
				if(code>0){
					modifyPwd();
				}
			}
		}.start();
		
	}
	/*
	 * 按钮点击事件
	 * (non-Javadoc)
	 * @see com.research.BaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;
		case R.id.ok:
			mInputNewPwd = mNewPwdEdit.getText().toString();
			mInputOldPwd = mOldPwdEdit.getText().toString();
			mInputConfirmPwd = mConfirmPwdEdit.getText().toString();
			if((mInputNewPwd == null || mInputNewPwd.equals(""))
					|| (mInputOldPwd == null || mInputOldPwd.equals(""))
					|| (mInputConfirmPwd == null || mInputConfirmPwd.equals(""))){
				Toast.makeText(mContext, R.string.please_input_old_new_confirm,Toast.LENGTH_LONG).show();
				return;
			}
			if(mInputNewPwd.equals(mInputOldPwd)){
				Toast.makeText(mContext,R.string.new_old_pwd_not_equalse,Toast.LENGTH_LONG).show();
				return;
			}
			if(!mInputNewPwd.equals(mInputConfirmPwd)){
				Toast.makeText(mContext,R.string.check_pwd_hint,Toast.LENGTH_LONG).show();
				return;
			}
			modifyPwdOther();
			break;
		default:
			break;
		}
		
	}
	
	

}
