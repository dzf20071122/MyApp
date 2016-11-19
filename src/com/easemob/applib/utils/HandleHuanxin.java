package com.easemob.applib.utils;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.exceptions.EaseMobException;
import com.research.LoginActivity;
import com.research.R;
import com.research.global.GlobalParam;

import date.tool.MD5Util;

public class HandleHuanxin {

	public static void RegisterUser(final String username,final String password){

		new Thread() {
			@Override
			public void run() {
				try {
					// 调用环信sdk注册方法
					Log.i("Runt", "HandleHuanxin注册环信账号");
					EMChatManager.getInstance().createAccountOnServer(username, password);
					Log.i("Runt","HandleHuanxin注册环信账号成功"+username+" "+password);
				} catch (final EaseMobException e) {
					// 注册失败
					int errorCode = e.getErrorCode();
					if (errorCode == EMError.NONETWORK_ERROR) {
						Log.i("Runt", "HandleHuanxin网络异常");
					} else if (errorCode == EMError.USER_ALREADY_EXISTS) {
						Log.i("Runt", "HandleHuanxin用户已存在！");
					} else if (errorCode == EMError.UNAUTHORIZED) {
						Log.i("Runt", "HandleHuanxin注册失败，无权限！");
					} else {
						Log.i("Runt", "HandleHuanxin注册失败 " + e.getMessage());
					}
				}
			}
		}.start();
	}
	
	public static void LoginUser(final String username,final String password){
		Log.i("Runt", String.format("准备登录环信 username:%s password:%s",username,password));
		///登录环信
		EMChatManager.getInstance().login(username,password,new EMCallBack() {//回调
			@Override
			public void onSuccess() {
				// 登陆成功，保存用户名密码
						//EMGroupManager.getInstance().loadAllGroups();
						//EMChatManager.getInstance().loadAllConversations();
						boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(username);
						if (!updatenick) {
							Log.i("Runt", "update current user nick fail"+username);
						}else{
							Log.i("Runt", "update current user nick success"+username);
						}
						Log.i("Runt", "登陆聊天服务器成功！");
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {
				Log.i("Runt", String.format("登陆聊天服务器失败！code:%s message:%s",code,message));
			}
		});
	}
	
	public static void ResetPwd(String username,String password){
		
	}
	
	
}
