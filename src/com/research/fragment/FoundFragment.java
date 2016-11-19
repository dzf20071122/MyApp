package com.research.fragment;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.research.CaptureActivity;
import com.research.FriensLoopActivity;
import com.research.MettingActivity;
import com.research.R;
import com.research.DB.DBHelper;
import com.research.DB.SessionTable;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.sortlist.PinYin;

/**
 * 发现Fragment的界面
 * @author dl
 */
public class FoundFragment extends Fragment implements OnClickListener {
	
	/**
	 * 定义全局变量
	 */
	private View mView;
	
	private RelativeLayout mFriendsLoopLayout,mNearActivityLayout,mSaoyisaoLayout,mMakeFriendLayout,mBlindDateLayout;
	private Context mParentContext;
	private TextView mNewsFriendsLoopIcon,mNewMeetingIcon;
	
	
	
	/**
	 * 导入控件
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParentContext = (Context)FoundFragment.this.getActivity();
		PinYin.main();
	}

	/**
	 * 加载控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.chat_tab_header, container, false);   
		return mView;
	}

	
	/**
	 * 初始化界面
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mFriendsLoopLayout = (RelativeLayout)mView.findViewById(R.id.outlander_content);
		mNearActivityLayout = (RelativeLayout)mView.findViewById(R.id.near_activity_layout);
		
		mSaoyisaoLayout = (RelativeLayout)mView.findViewById(R.id.saoyisao_layout);
//		mMakeFriendLayout = (RelativeLayout)mView.findViewById(R.id.make_friend_layout);
//		mBlindDateLayout = (RelativeLayout)mView.findViewById(R.id.blind_date_layout);
		
		mNewsFriendsLoopIcon = (TextView)mView.findViewById(R.id.friends_message_count);
		mNewMeetingIcon = (TextView)mView.findViewById(R.id.app_news_message_count);
		
		mFriendsLoopLayout.setOnClickListener(this);
		mNearActivityLayout.setOnClickListener(this);
		mSaoyisaoLayout.setOnClickListener(this);
//		mMakeFriendLayout.setOnClickListener(this);
		register();
	}

	/**
	 * 注册界面通知
	 */
	private void register(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP);
		filter.addAction(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP);
		filter.addAction(GlobalParam.ACTION_SHOW_NEW_MEETING);
		filter.addAction(GlobalParam.ACTION_HIDE_NEW_MEETING);
		mParentContext.registerReceiver(mReBoradCast, filter);
	}
	
	/**
	 * 处理通知
	 */
	BroadcastReceiver mReBoradCast = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent!=null){
				String action = intent.getAction();
				if(action.equals(GlobalParam.ACTION_SHOW_NEW_FRIENDS_LOOP)){
					if(mNewsFriendsLoopIcon!=null){
						int count = ResearchCommon.getFriendsLoopTip(mParentContext);
						if(count!=0){
							mNewsFriendsLoopIcon.setVisibility(View.VISIBLE);
							mNewsFriendsLoopIcon.setText(count+"");
						}
					}
				}else if(action.equals(GlobalParam.ACTION_HIDE_NEW_FRIENDS_LOOP)){
					if(mNewsFriendsLoopIcon!=null){
						mNewsFriendsLoopIcon.setVisibility(View.GONE);
					}
				}else if(action.equals(GlobalParam.ACTION_SHOW_NEW_MEETING)){
					if(mNewMeetingIcon!=null){
						SQLiteDatabase db = DBHelper.getInstance(mParentContext).getReadableDatabase();
						SessionTable table = new SessionTable(db);
						int count = table.queryMeetingSessionCount();
						mNewMeetingIcon.setVisibility(View.VISIBLE);
						/*if(count!=0){
							mNewMeetingIcon.setVisibility(View.VISIBLE);
							//mNewMeetingIcon.setText(count+"");
						}*/
					}
				}else if(action.equals(GlobalParam.ACTION_HIDE_NEW_MEETING)){
					if(mNewMeetingIcon!=null){
						mNewMeetingIcon.setVisibility(View.GONE);
					}
				}
			}
		}
	};
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.outlander_content:
			Intent intent = new Intent();
			intent.setClass(mParentContext, FriensLoopActivity.class);
			startActivity(intent);
			break;
		case R.id.near_activity_layout:
			//附近活动
			Intent meeting = new Intent();
			meeting.setClass(mParentContext, MettingActivity.class);
			startActivity(meeting);
			break;
		case R.id.saoyisao_layout:
			//扫一扫
			Intent scanIntent = new Intent(mParentContext, CaptureActivity.class);
			startActivity(scanIntent);
			break;
//		case R.id.make_friend_layout:
//			//交友墙
//			
//			break;
//		case R.id.blind_date_layout:
//			
//			break;
//		case R.id.app_news_content: //正在进行的会议
//			Intent meeting = new Intent();
//			meeting.setClass(mParentContext, MettingActivity.class);
//			startActivity(meeting);
//			break;
		default:
			break;
		}
	}

	/**
	 * 销毁页面
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mReBoradCast!=null){
			mParentContext.unregisterReceiver(mReBoradCast);
		}
	}
	
	
}
