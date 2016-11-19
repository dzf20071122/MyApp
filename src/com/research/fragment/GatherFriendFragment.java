package com.research.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.research.BlindDateActivity;
import com.research.BrowserActivity;
import com.research.CompleteUserInfoActvity;
import com.research.DiscoveActivity;
import com.research.EditProfileActivity;
import com.research.FeedBackActivity;
import com.research.MakeFriendActivity;
import com.research.MyAlbumActivity;
import com.research.MyFavoriteActivity;
import com.research.R;
import com.research.SchoolMeetingActivity;
import com.research.SettingTab;
import com.research.addpartActivity;
import com.research.Entity.Login;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;

/**
 * 我Fragment的界面
 * @author dl
 */
public class GatherFriendFragment extends Fragment implements OnClickListener {
	
	/**
	 * 定义全局变量
	 */
	private View mView;
	public static Login login;
	
	private Context mParentContext;
	
	private RelativeLayout school_fellow_menu,countrymen_menu,makeFriendLayout,blindDateLayout;
	
	
	/**
	 * 导入控件
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParentContext = (Context)GatherFriendFragment.this.getActivity();
//		PinYin.main();
	}

	/**
	 * 加载控件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.gather_friend_menu, container, false);   
		return mView;
	}

	
	/**
	 * 初始化界面
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		school_fellow_menu = (RelativeLayout) mView.findViewById(R.id.school_fellow_menu);
		school_fellow_menu.setOnClickListener(this);

		countrymen_menu = (RelativeLayout) mView.findViewById(R.id.countrymen_menu);
		countrymen_menu.setOnClickListener(this);

		makeFriendLayout = (RelativeLayout) mView.findViewById(R.id.make_friend_layout);
		makeFriendLayout.setOnClickListener(this);
		
		blindDateLayout = (RelativeLayout) mView.findViewById(R.id.blind_date_layout);
		blindDateLayout.setOnClickListener(this);
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
		case R.id.school_fellow_menu:
//			Intent intent = new Intent(mParentContext, EditProfileActivity.class);
//			startActivity(intent);
			Intent intent = new Intent(mParentContext, SchoolMeetingActivity.class);
			startActivity(intent);
			break;
		case R.id.countrymen_menu:
			Intent albumIntent = new Intent();
			albumIntent.setClass(mParentContext, MyAlbumActivity.class);
			mParentContext.startActivity(albumIntent);
			break;
		case R.id.make_friend_layout:
			//交友墙
			
			Login mLogin = ResearchCommon.getLoginResult(mParentContext);
//			mLogin.makeFriendXuanyan = "交友宣言"; //测试用
//			mLogin.makeFriendRequre = "交友要求";	//测试用
			if ((mLogin.makeFriendXuanyan == null || "".equals(mLogin.makeFriendXuanyan)) || (mLogin.makeFriendRequre == null || "".equals(mLogin.makeFriendRequre))){
				new AlertDialog.Builder(mParentContext).setTitle(getResources().getString(R.string.str_dialog_title_protip)).setMessage("交友信息不完全，是否去完善？").setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						Intent intent = new Intent(mParentContext, EditProfileActivity.class);
						startActivity(intent);
						
						dialog.dismiss();

					}
				}).setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
			}else{
				Intent toIntent = new Intent();
				toIntent.setClass(mParentContext, MakeFriendActivity.class);
				mParentContext.startActivity(toIntent);
			}
			
			break;
		case R.id.blind_date_layout:
			//相亲墙
			Login xlogin = ResearchCommon.getLoginResult(mParentContext);
			
			
			if ((xlogin.blindDate == null || "".equals(xlogin.blindDate)) || (xlogin.blindDateRequre == null || "".equals(xlogin.blindDateRequre))
					|| (xlogin.trueName == null || "".equals(xlogin.trueName))){
				new AlertDialog.Builder(mParentContext).setTitle(getResources().getString(R.string.str_dialog_title_protip)).setMessage("相亲信息不完全，是否去完善？").setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						Intent intent = new Intent(mParentContext, EditProfileActivity.class);
						startActivity(intent);
						
						dialog.dismiss();

					}
				}).setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
			}else{
				Intent toIntent = new Intent();
				toIntent.setClass(mParentContext, BlindDateActivity.class);
				mParentContext.startActivity(toIntent);
			}
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
