package com.research;

import java.util.ArrayList;
import java.util.List;

import com.research.DB.DBHelper;
import com.research.DB.RoomTable;
import com.research.DB.SessionTable;
import com.research.DB.UserTable;
import com.research.Entity.Login;
import com.research.Entity.ResearchJiaState;
import com.research.Entity.Room;
import com.research.Entity.SchoolMeetingList;
import com.research.Entity.Session;
import com.research.fragment.ChatFragment;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.map.BMapApiApp;
import com.research.net.ResearchException;
import com.research.sortlist.ClearEditText;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SchoolMeetingBuildActivity extends BaseActivity implements
		OnClickListener {

	private Button okBtn;
	private EditText schoolMeetingName;
	private ClearEditText schoolSignName;
	private String schId; // 上一级校友会ID

	private String mName;

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

		okBtn = (Button) findViewById(R.id.create_to);
		okBtn.setOnClickListener(this);

		schoolMeetingName = (EditText) findViewById(R.id.nick_name);
		schoolSignName = (com.research.sortlist.ClearEditText) findViewById(R.id.school_sign_name);
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
			case 1000:
				Room room = (Room)msg.obj;
				if(room == null){
					Toast.makeText(mContext, mContext.getResources().
							getString(R.string.create_group_failed),Toast.LENGTH_LONG).show();
					return;
				}
				SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
				String roomId = room.groupId;
				List<Login> roomUsrList = room.mUserList;
				RoomTable roomTab = new RoomTable(db);
				roomTab.insert(room);
				String groupHeadUrl="";
				if (roomUsrList != null ) {
					//RoomUserTable roomUserTable = new RoomUserTable(db);
					UserTable userTable = new UserTable(db);

					for (int j = 0; j < roomUsrList.size(); j++) {
						if(room.groupCount-1 == j){
							groupHeadUrl += roomUsrList.get(j).headsmall;
						}else{
							groupHeadUrl += roomUsrList.get(j).headsmall+",";
						}

						/*if(!roomUsrList.get(j).uid.equals(ResearchCommon.getUserId(mContext))){
							roomUserTable.insert(roomId, roomUsrList.get(j).uid);
						}*/
						Login user = userTable.query(roomUsrList.get(j).uid);
						if(user == null){
							userTable.insert(roomUsrList.get(j), -999);
						}
					}
				}


				Session session = new Session();
				session.setFromId(room.groupId/*ResearchCommon.getUserId(mContext)*/);
				session.name = room.groupName;
				session.heading = groupHeadUrl;
				session.type = 300;
				session.lastMessageTime = System.currentTimeMillis();

				SessionTable sessionTable = new SessionTable(db);
				sessionTable.insert(session);

				sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));

				
				
				SchoolMeetingBuildActivity.this.finish();
				break;
			case GlobalParam.SHOW_PROGRESS_DIALOG:
				String dialogMsg = (String) msg.obj;
				showProgressDialog(dialogMsg);
				break;
			case GlobalParam.MSG_CHECK_STATE:
				ResearchJiaState status = (ResearchJiaState) msg.obj;
				if (status != null) {
					if (status.code == 0) {
						// 创建校友会成功
						// ------------------创建群
						createRoom();
//						SchoolMeetingBuildActivity.this.finish();
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
					message = BMapApiApp.getInstance().getResources()
							.getString(R.string.timeout);
				}
				Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
				break;

			default:
				break;

			}
		}
	};

	/**
	 * 创建校友会
	 * 
	 * @param loadType
	 */
	private void buildSchoolMeeting(final int loadType, final String name,
			final String pid, final String type) {
		new Thread() {

			@Override
			public void run() {
				// 有无网络,有网络请求数据
				if (ResearchCommon.verifyNetwork(mContext)) {
					new Thread() {
						public void run() {

							try {
								ResearchCommon
										.sendMsg(
												mBaseHandler,
												BASE_SHOW_PROGRESS_DIALOG,
												mContext.getResources()
														.getString(
																R.string.create_school_meeting));

								ResearchJiaState status = ResearchCommon
										.getResearchInfo().createSchoolMeeting(
												name, pid, type);

								ResearchCommon.sendMsg(mHandler,
										GlobalParam.MSG_CHECK_STATE, status);
								mBaseHandler
										.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);

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
	public void checkMessage() {
		String sname = schoolMeetingName.getText().toString().trim();
		String signName = schoolSignName.getText().toString().trim();

		if (sname.equals("")) {
			Toast.makeText(mContext, R.string.please_input_schoolname,
					Toast.LENGTH_SHORT).show();
			return;
		}
		// if(signName.equals("")){
		// Toast.makeText(mContext, R.string.please_input_password,
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		if (!ResearchCommon.verifyNetwork(mContext)) {
			Toast.makeText(mContext, R.string.network_error, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Message message = new Message();
		// message.what = GlobalParam.SHOW_PROGRESS_DIALOG;
		// message.obj =
		// mContext.getResources().getString(R.string.loading_login);
		// mHandler.sendMessage(message);
		mName = sname;
		buildSchoolMeeting(1, sname, schId, "1");
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

	/**
	 * 
	 * @param list
	 * @param type
	 *            0-创建群组 1-邀请参会
	 */
	private void createRoom() {
		if (!ResearchCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_TIMEOUT_ERROR);
			return;
		}
		new Thread() {
			public void run() {
				try {
					String mUids = ResearchCommon.getUserId(BMapApiApp
							.getInstance());

					Room createRoom = ResearchCommon.getResearchInfo()
							.createRoom(mName, mUids);
					if (createRoom != null && createRoom.state != null
							&& createRoom.state.code == 0) {
						ResearchCommon.sendMsg(mHandler,
								1000, createRoom);
					} else {
						Message msg = new Message();
						msg.what = GlobalParam.MSG_LOAD_ERROR;
						if (createRoom != null && createRoom.state != null
								&& !createRoom.state.errorMsg.equals("")) {
							msg.obj = createRoom.state.errorMsg;
						} else {
							msg.obj = mContext
									.getString(R.string.create_group_failed);
						}
						mHandler.sendMessage(msg);
					}

				} catch (NotFoundException e) {
					e.printStackTrace();
				} catch (ResearchException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
