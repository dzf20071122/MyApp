package com.research.fragment;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.research.BrowserActivity;
import com.research.EditProfileActivity;
import com.research.FeedBackActivity;
import com.research.FriensLoopActivity;
import com.research.MettingActivity;
import com.research.MyAlbumActivity;
import com.research.MyFavoriteActivity;
import com.research.R;
import com.research.SettingTab;
import com.research.addpartActivity;
import com.research.DB.DBHelper;
import com.research.DB.SessionTable;
import com.research.Entity.Login;
import com.research.global.GlobalParam;
import com.research.global.ImageLoader;
import com.research.global.ResearchCommon;

/**
 * 我Fragment的界面
 * @author dl
 */
public class MyFragment extends Fragment implements OnClickListener {
	
	/**
	 * 定义全局变量
	 */
	private View mView;
	public static Login login;
	
	private Context mParentContext;
	private TextView mNewsFriendsLoopIcon,mNewMeetingIcon;
	
	private LinearLayout my_profile,my_photo,my_collection,my_setting,my_feedback,my_vip,add_part;
	private ImageLoader mImageLoader;
	
	
	/**
	 * 导入控件
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParentContext = (Context)MyFragment.this.getActivity();
//		PinYin.main();
	}

	/**
	 * 加载控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.bottomdialog, container, false);   
		return mView;
	}

	
	/**
	 * 初始化界面
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		mFriendsLoopLayout = (RelativeLayout)mView.findViewById(R.id.outlander_content);
//		mMeetingLayout = (RelativeLayout)mView.findViewById(R.id.app_news_content);
//		
//		mNewsFriendsLoopIcon = (TextView)mView.findViewById(R.id.friends_message_count);
//		mNewMeetingIcon = (TextView)mView.findViewById(R.id.app_news_message_count);
//		
//		mFriendsLoopLayout.setOnClickListener(this);
//		mMeetingLayout.setOnClickListener(this);
//		register();

		mImageLoader = new ImageLoader();
		/*btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
		//取消按钮
		btn_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//销毁弹出框
				//SaveDate.saveDate(context, new OAuthV2()); 
				
			}
		});*/
		my_profile = (LinearLayout) mView.findViewById(R.id.my_profile);
		ImageView iv=(ImageView)mView.findViewById(R.id.user_icon);
		TextView userName = (TextView)mView.findViewById(R.id.user_name);
		TextView userSign = (TextView)mView.findViewById(R.id.user_sign);
		login = ResearchCommon.getLoginResult(mParentContext);
		if(login!=null && login.headsmall!=null && !login.headsmall.equals("")){
			mImageLoader.getBitmap(mParentContext, iv,null,login.headsmall,0,false,true);
		}
		userName.setText(login.nickname);
		userSign.setText(login.sign);
//		my_profile.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
		my_profile.setOnClickListener(this);

		my_photo = (LinearLayout) mView.findViewById(R.id.my_photo);
		my_photo.setOnClickListener(this);
//		my_photo.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				
//			}
//		});
		my_collection = (LinearLayout) mView.findViewById(R.id.my_collection);
		my_collection.setOnClickListener(this);
//		my_collection.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
		my_setting = (LinearLayout) mView.findViewById(R.id.my_setting);
		my_setting.setOnClickListener(this);
//		my_setting.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
		my_feedback = (LinearLayout)mView.findViewById(R.id.my_feedback);
		my_feedback.setOnClickListener(this);
//		my_feedback.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
		my_vip = (LinearLayout) mView.findViewById(R.id.my_vip);
		my_vip.setOnClickListener(this);
//		my_vip.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
		add_part = (LinearLayout)mView.findViewById(R.id.add_part);
		add_part.setOnClickListener(this);
//		add_part.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//			}
//		});
	}

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
		case R.id.my_profile:
			/*Intent profileIntent = new Intent();
			profileIntent.setClass(mContext, ProfileActivity.class);
			mContext.startActivity(profileIntent);*/
			Intent intent = new Intent(mParentContext, EditProfileActivity.class);
			/*intent.putExtra("login", mLogin);*/
			startActivity(intent);
			break;
		case R.id.my_photo:
			Intent albumIntent = new Intent();
			albumIntent.setClass(mParentContext, MyAlbumActivity.class);
			mParentContext.startActivity(albumIntent);
			break;
		case R.id.my_collection:
			Intent collectionIntent = new Intent();
			collectionIntent.setClass(mParentContext, MyFavoriteActivity.class);
			mParentContext.startActivity(collectionIntent);
			break;
		case R.id.my_setting:
			Intent settingIntent = new Intent();
			settingIntent.setClass(mParentContext, SettingTab.class);
			mParentContext.startActivity(settingIntent);
			break;
		case R.id.my_feedback:
			Intent feedbackIntent = new Intent();
			feedbackIntent.setClass(mParentContext, FeedBackActivity.class);
			mParentContext.startActivity(feedbackIntent);
			break;


		case R.id.my_vip:
	    	Intent callIntent=new Intent();
	    	callIntent.setClass(mParentContext, BrowserActivity.class);
	    	callIntent.putExtra("url", "http://oa3.xrmg88.com/LoginMobile.aspx?MemberName="+login.phone+"&MemberPwd="+login.password);
	    	callIntent.putExtra("title", "我的会员");
	    	startActivity(callIntent);
			break;

		case R.id.add_part:
	    	Intent addintent=new Intent();
	    	addintent.setClass(mParentContext, addpartActivity.class);
	    	addintent.putExtra("login", login);
	    	addintent.putExtra("title", "广告专区");
	    	startActivity(addintent);
			break;
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
	}
	
	
}
