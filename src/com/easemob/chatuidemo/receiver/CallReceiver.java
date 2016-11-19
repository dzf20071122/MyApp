/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.easemob.chatuidemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.VideoCallActivity;
import com.easemob.chatuidemo.activity.VoiceCallActivity;
import com.easemob.util.EMLog;
import com.research.CallVideoActivity;
import com.research.CallVoiceActivity;
import com.research.Entity.Login;
import com.research.Entity.UserList;
import com.research.global.ResearchCommon;
import com.research.net.ResearchException;

public class CallReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.i("Runt",String.format("CallReceiver环信来通知了 onReceive"));
		if(!DemoHXSDKHelper.getInstance().isLogined())
		    return;
		//拨打方username
		final String from = intent.getStringExtra("from");
		//call type
		final String type = intent.getStringExtra("type");
		Log.i("Runt",String.format("CallReceiver环信来通知了 from:%s type:%s",from,type));
		new Thread(){
			@Override
			public void run(){
						
				Login login=null;
				try {
					UserList loginResult = ResearchCommon.getResearchInfo().search_number(from,1);
					login=loginResult.mUserList.get(0);
					Log.i("Runt",String.format("CallReceiver 获取用户信息  id:%s",login.uid));
				} catch (ResearchException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i("Runt",String.format("CallReceiver 获取用户信息失败 "));
				}
				if("video".equals(type)){ //视频通话
				    context.startActivity(new Intent(context, CallVideoActivity.class).
		                    putExtra("username", from).putExtra("isComingCall", true).putExtra("todata", login).
		                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}else{ //音频通话
				    context.startActivity(new Intent(context, CallVoiceActivity.class).
				            putExtra("username", from).putExtra("isComingCall", true).putExtra("todata", login).
				            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				}
				EMLog.d("CallReceiver", "app received a incoming call");
			}
		}.start();
	}

}
