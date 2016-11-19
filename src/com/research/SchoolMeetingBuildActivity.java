package com.research;

import java.util.ArrayList;
import java.util.List;

import com.research.Entity.Login;
import com.research.Entity.ResearchJiaState;
import com.research.Entity.SchoolMeetingList;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.map.BMapApiApp;
import com.research.net.ResearchException;
import com.research.sortlist.ClearEditText;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SchoolMeetingBuildActivity extends BaseActivity implements
		OnClickListener{

	private Button okBtn;
	private EditText schoolMeetingName;
	private ClearEditText schoolSignName;
	private String schId; //上一级校友会ID

	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		schId = intent.getStringExtra("schId");
		
		setContentView(R.layout.school_meeting_new);
		mContext = this;
		initComponent();
		
	}

	/**
	 * 实例化控件
	 */
	private void initComponent() {
		
		setTitleContent(R.drawable.back_btn, R.drawable.add_icon_btn,
				R.string.school_meeting_app_name);
		mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);
		
		okBtn = (Button)findViewById(R.id.create_to);
		okBtn.setOnClickListener(this);
		
		schoolMeetingName = (EditText)findViewById(R.id.nick_name);
		schoolSignName = (com.research.sortlist.ClearEditText)findViewById(R.id.school_sign_name);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
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
			case GlobalParam.MSG_CHECK_STATE:
				ResearchJiaState status = (ResearchJiaState) msg.obj;
				if (status != null) {
					if (status.code == 0) {
						//创建校友会成功
						SchoolMeetingBuildActivity.this.finish();
					} else {
						Toast.makeText(mContext, status.errorMsg,
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(mContext, R.string.create_school_error,
							Toast.LENGTH_LONG).show();
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
	private void buildSchoolMeeting(final int loadType,final String name, final String pid,final String type) {
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
												.getString(R.string.create_school_meeting));

								ResearchJiaState status = ResearchCommon.getResearchInfo().createSchoolMeeting(name,pid,type);

								ResearchCommon.sendMsg(mHandler,
										GlobalParam.MSG_CHECK_STATE, status);
								mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
								
								
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
	 * 检测输入校友会是否合理
	 */
	public void checkMessage(){
		String sname = schoolMeetingName.getText().toString().trim();
		String signName = schoolSignName.getText().toString().trim();


		if(sname.equals("")){
			Toast.makeText(mContext, R.string.please_input_schoolname, Toast.LENGTH_SHORT).show();
			return;
		}
//		if(signName.equals("")){
//			Toast.makeText(mContext, R.string.please_input_password, Toast.LENGTH_SHORT).show();
//			return;
//		}
		if(!ResearchCommon.verifyNetwork(mContext)){
			Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT).show();
			return;
		}

//		Message message = new Message();
//		message.what = GlobalParam.SHOW_PROGRESS_DIALOG;
//		message.obj = mContext.getResources().getString(R.string.loading_login);
//		mHandler.sendMessage(message);

		buildSchoolMeeting(1,sname,schId,"1");
	}
	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			SchoolMeetingBuildActivity.this.finish();
			break;
		case R.id.right_btn:
			
			break;
		case R.id.create_to:
			checkMessage();
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
