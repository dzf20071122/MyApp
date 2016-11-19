package com.research.receiver;

import java.util.List;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.research.DB.DBHelper;
import com.research.DB.MessageTable;
import com.research.DB.SessionTable;
import com.research.Entity.Login;
import com.research.Entity.MessageInfo;
import com.research.Entity.MessageType;
import com.research.Entity.Session;
import com.research.Entity.SessionList;
import com.research.global.GlobleType;
import com.research.global.ResearchCommon;
import com.research.org.json.JSONObject;
import com.research.service.XmppManager;

/**
 * 
 * 功能： 接收到发送的消息.通过广播发送出去 <br />
 * 日期：2013-5-6<br />
 * 地点：风搜科技<br />
 * 版本：ver 1.0<br />
 * 
 * @author fighter
 * @since
 */
public class NotifyChatMessage implements NotifyMessage {
	private static final String TAG = "NotifyChatMessage";

	/**
	 * 聊天服务发来聊天信息, 广播包<br/>
	 * 附加参数: {@link NotifyChatMessage#EXTRAS_NOTIFY_CHAT_MESSAGE}
	 */
	public static final String ACTION_NOTIFY_CHAT_MESSAGE = "com.research.sns.notify.ACTION_NOTIFY_CHAT_MESSAGE";
	/**
	 * 某消息列表有更新，注意查收
	 * 附加参数: {@link NotifyChatMessage#EXTRAS_NOTIFY_SESSION_MESSAGE}
	 */
	public static final String ACTION_NOTIFY_SESSION_MESSAGE = "com.research.sns.notify.ACTION_NOTIFY_SESSION_MESSAGE";

	/**
	 * 更新语音转文字成功之后语音消息对应的文本信息通知
	 */
	public static final String ACTION_CHANGE_VOICE_CONTENT = "com.teamchat.chat.intent.action.ACTION_CHANGE_VOICE_CONTENT";

	
	/**
	 * 附加信息<br/> {@link MessageInfo}
	 */
	public static final String EXTRAS_NOTIFY_CHAT_MESSAGE = "extras_message";
	/**
	 * 附加信息<br/> {@link SessionList}
	 */
	public static final String EXTRAS_NOTIFY_SESSION_MESSAGE = "extras_session";

	private ChatMessageNotifiy chatMessageNotifiy;
	public XmppManager xmppManager;
	public Login userInfoVo;
	

	public NotifyChatMessage(XmppManager xmppManager) {
		super();
		this.xmppManager = xmppManager;
		this.userInfoVo = xmppManager.getSnsService().getUserInfoVo();
		chatMessageNotifiy = new ChatMessageNotifiy(xmppManager.getSnsService());
	}

	@Override
	public void notifyMessage(String msg) {
		Log.e("NotifyChatMessage", msg);
		try {
			
			if(msg == null || msg.equals("" )
					|| msg.equals("This room is not anonymous.")){
				return;
			}
			
			MessageInfo info = new MessageInfo(new JSONObject(msg));
			
			if(info.typechat != 100 && info.getFromId().equals(ResearchCommon.getUserId(xmppManager.getSnsService()))){
				return;
			}
			info.sendState = 1;
			/*if(MessageType.MAP == info.type){
				info.setContent(json.getString("content"));
			}*/
			if (info != null) {
				saveMessageInfo(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveMessageInfo(MessageInfo info) {
		
		if(info.typefile == MessageType.VOICE){
			info.setSendState(4);
		}
		
		SQLiteDatabase dbDatabase = DBHelper.getInstance(xmppManager.getSnsService()).getWritableDatabase();
		MessageTable table = new MessageTable(dbDatabase);
		table.insert(info);
		
		Session session = new Session();
		if(info.typechat == 100){
			session.setFromId(info.fromid);
			session.name = info.fromname;
			session.heading = info.fromurl;
			session.lastMessageTime = info.time;
		}else {
			session.setFromId(info.toid);
			session.name = info.toname;
			session.heading = info.tourl;
			session.lastMessageTime = info.time;
		}
		
		session.type = info.typechat;
		SessionTable sessionTable = new SessionTable(dbDatabase);
		Session existSession = sessionTable.query(session.getFromId(), info.typechat);
		if(existSession != null){
			if(existSession.isTop!=0){
				List<Session> exitsSesList = sessionTable.getTopSessionList();
				if(exitsSesList!=null && exitsSesList.size()>0){
					for (int i = 0; i < exitsSesList.size(); i++) {
						Session ses = exitsSesList.get(i);
						if(ses.isTop>1){
							ses.isTop = ses.isTop-1;
							sessionTable.update(ses, ses.type);
						}
					}
				}
				session.isTop = sessionTable.getTopSize();
			}
			sessionTable.update(session, info.typechat);
		}else {
			sessionTable.insert(session);
		}
		
			sendBroad(info);
		
	}

	private void sendBroad(MessageInfo info) {
		Log.d(TAG, "sendBroad()");
		
		/*Intent refreshIntent = new Intent(ChatsTab.ACTION_REFRESH_SESSION);
		refreshIntent.putExtra("message", info);
		xmppManager.getSnsService().sendBroadcast(refreshIntent);*/
		
		Intent intent = new Intent(ACTION_NOTIFY_CHAT_MESSAGE);
		intent.putExtra(EXTRAS_NOTIFY_CHAT_MESSAGE, info);
		//intent.putExtra(EXTRAS_NOTIFY_SESSION_MESSAGE, sessionList);
		chatMessageNotifiy.notifiy(info);
		if (xmppManager != null && xmppManager.getSnsService() != null) {
			xmppManager.getSnsService().sendBroadcast(intent);
		}
	}
}
