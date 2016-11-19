package com.research;

import java.io.File;
import java.util.UUID;

import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMVideoCallHelper;
import com.easemob.chat.TextMessageBody;
import com.easemob.chatuidemo.Constant;
import com.research.DB.DBHelper;
import com.research.DB.MessageTable;
import com.research.DB.SessionTable;
import com.research.Entity.Login;
import com.research.Entity.MessageInfo;
import com.research.Entity.MessageResult;
import com.research.Entity.MessageType;
import com.research.Entity.Session;
import com.research.fragment.ChatFragment;
import com.research.global.FeatureFunction;
import com.research.global.GlobalParam;
import com.research.global.ImageLoader;
import com.research.global.ResearchCommon;
import com.research.net.ResearchException;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
/**
 * 语音通话页面
 * @author Runt02
 *
 */
public class CallActivity extends BaseActivity implements SensorEventListener {

    protected boolean isInComingCall;
    protected String username;
    protected CallingState callingState = CallingState.CANCED;
    protected String callDruationText;
    protected String msgid;
    protected AudioManager audioManager;
    protected SoundPool soundPool;
    protected Ringtone ringtone;
    protected int outgoing;
    protected EMCallStateChangeListener callStateListener;
    

    //调用距离传感器，控制屏幕
    private SensorManager mManager;//传感器管理对象
    private PowerManager localPowerManager = null;//电源管理对象
    private PowerManager.WakeLock localWakeLock = null;//电源锁

    EMVideoCallHelper callHelper;
    protected ImageLoader mImageLoader = new ImageLoader();
	protected int mRemoteUserid;//通话对象的id
	protected String myStr;//login的string形式
	protected String firendStr;
	protected static boolean isCalling=false;//是否正在呼叫
	protected final static int SEND_SUCCESS = 13454;
	protected final static int SEND_FAILED = 13455;
	protected final static int CHANGE_STATE = 13456;
	protected final static int HIDE_PROGRESS_DIALOG = 15453;
	protected final static int SHOW_KICK_OUT_DIALOG = 15454;
	protected final static int CONTROL_SHOW=0;//控件显示
	protected final static int CONTROL_HIDE=8;//控件隐藏
	protected Login myLogin,firendLogin;//对话双方的login
    protected boolean isMuteState=false;//是否静音
    protected boolean isHandsfreeState;//是否扬声器
    protected boolean isAnswered;//是否接听
    public static boolean exception=true;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        mContext = this;
        firendLogin = (Login)getIntent().getSerializableExtra("todata");
		Log.i("Runt","CallActivity onCreate firendLogin:"+firendLogin);
        mManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        
        //获取系统服务POWER_SERVICE，返回一个PowerManager对象
        localPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag 
        localWakeLock = this.localPowerManager.newWakeLock(32, "MyPower");//第一个参数为电源锁级别，第二个是日志tag
        try {
			mManager.registerListener(this, mManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),// 距离感应器
			        SensorManager.SENSOR_DELAY_NORMAL);//注册传感器，第一个参数为距离监听器，第二个是传感器类型，第三个是延迟类型

			Log.i("Runt","CallActivity onCreate 注册传感器成功:");
		} catch (Exception e) {
			Log.i("Runt","CallActivity onCreate 注册传感器失败:"+e.getMessage());
		}
	}
	
	
   @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Runt","on destroy");
		if(mManager != null){
		    localWakeLock.release();//释放电源锁，如果不释放finish这个acitivity后仍然会有自动锁屏的效果，不信可以试一试
			
		    mManager.unregisterListener(this);//注销传感器监听
		}
        if (soundPool != null)
            soundPool.release();
        if (ringtone != null && ringtone.isPlaying())
            ringtone.stop();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);
        
        if(callStateListener != null)
            EMChatManager.getInstance().removeCallStateChangeListener(callStateListener);
            
    }
	

   /**
    * 播放拨号响铃
    * 
    * @param sound
    * @param number
    */
   protected int playMakeCallSounds() {
       try {
           // 最大音量
           float audioMaxVolumn = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
           // 当前音量
           float audioCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_RING);
           float volumnRatio = audioCurrentVolumn / audioMaxVolumn;

           audioManager.setMode(AudioManager.MODE_RINGTONE);
           audioManager.setSpeakerphoneOn(false);

           // 播放
           int id = soundPool.play(outgoing, // 声音资源
                   0.3f, // 左声道
                   0.3f, // 右声道
                   1, // 优先级，0最低
                   -1, // 循环次数，0是不循环，-1是永远循环
                   1); // 回放速度，0.5-2.0之间。1为正常速度
           return id;
       } catch (Exception e) {
           return -1;
       }
   }
   
   

	/**
	 * 处理消息
	 */
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case CHANGE_STATE:
				MessageInfo messageSend = (MessageInfo) msg.obj;
				SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
				MessageTable table = new MessageTable(db);
				table.update(messageSend);
				break;
			}
		}

	};

	/*
	 * 消息发送成功
	 */
	public void sendBroad2Save(MessageInfo msg,boolean isForward){
		msg.setSendState(1);//设置消息的状态
		sendMessage(msg, 0,isForward);//发送消息
	}

	

	/*
	 * 发送消息
	 */
	private void sendMessage(final MessageInfo msg, final int isResend,final boolean isForward){
		new Thread(){
			@Override
			public void run(){
				if(ResearchCommon.verifyNetwork(mContext)){
					msg.sendState = 2;
					Message stateMessage = new Message();
					stateMessage.obj= msg;
					stateMessage.what = CHANGE_STATE;
					mHandler.sendMessage(stateMessage);
					try {
						MessageResult result = ResearchCommon.getResearchInfo().sendMessage(msg,isForward);
						if(result != null && result.mState != null &&
								(result.mState.code == 0 || result.mState.code == 4)){
							result.mMessageInfo.sendState = 1;
							if(msg.typefile == MessageType.VOICE){
								String voice = FeatureFunction.generator(result.mMessageInfo.voiceUrl);
								FeatureFunction.reNameFile(new File(msg.voiceUrl), voice);
							}
							result.mMessageInfo.readState = 1;
							Message message = new Message();
							message.what = SEND_SUCCESS;
							message.arg1 = isResend;
							if(result.mState.code == 4){
								message.arg2 = 4;
							}
							message.obj = result.mMessageInfo;
							mHandler.sendMessage(message);
							return;
						}else if(result != null && result.mState != null && result.mState.code == 3){
							SQLiteDatabase db = DBHelper.getInstance(mContext).getWritableDatabase();
							SessionTable sessionTable = new SessionTable(db);
							MessageTable messageTable = new MessageTable(db);
							Session session = sessionTable.query(firendLogin.uid, 300);
							if(session != null){
								messageTable.delete(firendLogin.uid, 300);
								sessionTable.delete(firendLogin.uid, 300);

								mContext.sendBroadcast(new Intent(ChatFragment.ACTION_REFRESH_SESSION));
								mContext.sendBroadcast(new Intent(GlobalParam.ACTION_UPDATE_SESSION_COUNT));
							}
							mHandler.sendEmptyMessage(SHOW_KICK_OUT_DIALOG);
							return;
						}
					} catch (ResearchException e) {
						e.printStackTrace();
					}

				}else {
					mHandler.sendEmptyMessage(GlobalParam.MSG_NETWORK_ERROR);
				}

				msg.sendState = 0;
				Message message = new Message();
				message.what = SEND_FAILED;
				message.arg1 = isResend;
				message.obj = msg;
				mHandler.sendMessage(message);
			}
		}.start();
	}
	
		
	public void finishThis(){
		Handler handler = new Handler();
		handler.postDelayed(new Runnable(){
			@Override
			public void run() {
				CallSound.stopCallSound();
				activity.finish();
				isCall=false;
				isCalling=false;
			}
		}, 2000);
	}

	
	public void sendEndMes(){
		MessageInfo msg = new MessageInfo();//创建文本消息
		msg.fromid = myLogin.uid;
		msg.tag = UUID.randomUUID().toString();
		msg.fromname = myLogin.nickname;
		msg.fromurl = myLogin.headsmall;
		msg.toid = firendLogin.uid;
		msg.toname = firendLogin.nickname;
		msg.tourl = firendLogin.headsmall;
		msg.typefile = MessageType.TEXT;
		msg.content = mContext.getString(R.string.session_end);//发送会话结束
		msg.typechat = 100;
		msg.time = System.currentTimeMillis();
		msg.readState = 0;
		sendBroad2Save(msg,true);//把消息发送出去
	}

	  /*
	   * 关闭扬声器
	   */
    protected void closeSpeakerOn() {
        try {
            if (audioManager != null) {
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                	audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     *  打开扬声器
     */
    protected void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            	audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 保存通话消息记录
     * @param type 0：音频，1：视频
     */
    protected void saveCallRecord(int type) {
        EMMessage message = null;
        TextMessageBody txtBody = null;
        if (!isInComingCall) { // 打出去的通话
            message = EMMessage.createSendMessage(EMMessage.Type.TXT);
            message.setReceipt(username);
        } else {
            message = EMMessage.createReceiveMessage(EMMessage.Type.TXT);
            message.setFrom(username);
        }

        String st1 = getResources().getString(R.string.call_duration);
        String st2 = getResources().getString(R.string.Refused);
        String st3 = getResources().getString(R.string.The_other_party_has_refused_to);
        String st4 = getResources().getString(R.string.The_other_is_not_online);
        String st5 = getResources().getString(R.string.The_other_is_on_the_phone);
        String st6 = getResources().getString(R.string.The_other_party_did_not_answer);
        String st7 = getResources().getString(R.string.did_not_answer);
        String st8 = getResources().getString(R.string.Has_been_cancelled);
        switch (callingState) {
        case NORMAL:
            txtBody = new TextMessageBody(st1 + callDruationText);
            break;
        case REFUESD:
            txtBody = new TextMessageBody(st2);
            break;
        case BEREFUESD:
            txtBody = new TextMessageBody(st3);
            break;
        case OFFLINE:
            txtBody = new TextMessageBody(st4);
            break;
        case BUSY:
            txtBody = new TextMessageBody(st5);
            break;
        case NORESPONSE:
            txtBody = new TextMessageBody(st6);
            break;
        case UNANSWERED:
            txtBody = new TextMessageBody(st7);
            break;
        default:
            txtBody = new TextMessageBody(st8);
            break;
        }
        // 设置扩展属性
        if(type == 0)
            message.setAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, true);
        else
            message.setAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, true);

        // 设置消息body
        message.addBody(txtBody);
        message.setMsgId(msgid);

        // 保存
        EMChatManager.getInstance().saveMessage(message, false);
    }

    enum CallingState {
        CANCED, NORMAL, REFUESD, BEREFUESD, UNANSWERED, OFFLINE, NORESPONSE, BUSY
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
        Log.i("Runt","callactivity  onSensorChanged");

        float[] its = event.values;
        //Log.d(TAG,"its array:"+its+"sensor type :"+event.sensor.getType()+" proximity type:"+Sensor.TYPE_PROXIMITY);
        if (its != null && event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
 
            //经过测试，当手贴近距离感应器的时候its[0]返回值为0.0，当手离开时返回1.0
            if (its[0] == 0.0) {// 贴近手机
 
                System.out.println("hands up");
                Log.i("Runt","callactivity  onSensorChanged 贴近手机");
                if (localWakeLock.isHeld()) {
                    return;
                } else{
 
                    localWakeLock.acquire();// 申请设备电源锁
                    Log.i("Runt","callactivity  onSensorChanged 申请设备电源锁");
                }
            } else {// 远离手机
 
                System.out.println("hands moved");
                Log.i("Runt","callactivity  onSensorChanged 远离手机");
                if (localWakeLock.isHeld()) {
                    return;
                } else{
                    localWakeLock.setReferenceCounted(false);
                    localWakeLock.release(); // 释放设备电源锁
                    Log.i("Runt","callactivity  onSensorChanged 释放设备电源锁");
                }
            }
        }
		
	}
}
