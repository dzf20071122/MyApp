package com.research.map;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.harmony.javax.security.auth.login.AppConfigurationEntry;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.receiver.CallReceiver;
import com.research.HuanxinHelperApplication;
import com.research.Entity.CountryList;
import com.research.Entity.ResearchProject;
import com.research.global.ResearchCommon;
import com.research.net.ResearchException;

/**
 * http://lbsyun.baidu.com/apiconsole/key
 * 百度地图key
 * @author dongli
 *
 */
public class BMapApiApp extends Application {
    private Bitmap bitmap;
    private static BMapApiApp mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;
    private static  CountryList mCountryList = null;
   

	public static Context applicationContext;
	
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static DemoHXSDKHelper hxSDKHelper = new DemoHXSDKHelper();
	
	
   //public static final String strKey = "6VINDcwKFAHfjGlVwbqUmVxx"; //调试版
  public static final String strKey = "wiiFPMjNxyuEFlCU8G9UWDSQnFQXWRQg";//"4y96ionUeoxpolcUDSBQYccK";//"hirOm4RsYg3Fkyg79B7GiYmP"; //发布版
    
    @Override
    public void onCreate() {
        super.onCreate();
		Log.i("Runt","BMapApiApp onCreate");
        mInstance = this;
    	ResearchCommon.verifyNetwork(mInstance);
        initEngineManager(this);
        if(ResearchCommon.getUserId(mInstance)!=null && !ResearchCommon.getUserId(mInstance).equals("")){
        	 new Thread(){
             	public void run() {
             		try {
             			mCountryList = ResearchCommon.getResearchInfo()
             					.getCityAndContryUser();
             		} catch (ResearchException e) {
             			e.printStackTrace();
             		}
             	};
             }.start();
        }

        applicationContext = this;

		initSdk();

    }
    
    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }
    }
    
    public static BMapApiApp getInstance() {
        return mInstance;
    }
    
    public static CountryList getContryList(){
    	return mCountryList;
    }
    
    public static void setContryList(CountryList contryList){
    	mCountryList = contryList;
    }
   
    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    public static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }

        @Override
        public void onGetPermissionState(int iError) {
            //非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), 
                        "请在 BMapApiApp.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
                BMapApiApp.getInstance().m_bKeyRight = false;
            }
            else{
                BMapApiApp.getInstance().m_bKeyRight = true;
                //Toast.makeText(BMapApiApp.getInstance().getApplicationContext(), 
                //        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

	/*
	 * 初始化环信SDK
	 */
	private void initSdk() {
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		// 如果app启用了远程的service，此application:onCreate会被调用2次
		// 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
		// 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process name就立即返回

	    Log.i("Runt", "BMapApiApp 准备初始化环信SDK");
		if (processAppName == null ||!processAppName.equalsIgnoreCase("com.research")) {
		    Log.i("Runt", "BMapApiApp enter the service process!初始化失败");
		    //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名
		    
		    // 则此application::onCreate 是被service 调用的，直接返回
		    return;
		}
		EMChat.getInstance().init(applicationContext);
		/**
		 * debugMode == true 时为打开，sdk 会在log里输入调试信息
		 * @param debugMode
		 * 在做代码混淆的时候需要设置成false
		 */
		// 设置sandbox测试环境
        // 建议开发者开发时设置此模式
            //EMChat.getInstance().setEnv(EMEnvMode.EMDevMode);
        
            // set debug mode in development process
		EMChat.getInstance().setDebugMode(false);//在做打包混淆时，要关闭debug模式，如果未被关闭，则会出现程序无法运行问题
		Log.i("Runt", "BMapApiApp initSdk初始化环信SDK完毕");
		IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingCallBroadcastAction());
        CallReceiver   callReceiver = new CallReceiver();
        registerReceiver(callReceiver, callFilter);
		Log.i("Runt","BMapApiApp 注册通知了callReceiver");
		
	}


	 /**
   * check the application process name if process name is not qualified, then we think it is a service process and we will not init SDK
   * @param pID
   * @return
   */
  private String getAppName(int pID) {
      String processName = null;
      ActivityManager am = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
      List l = am.getRunningAppProcesses();
      Iterator i = l.iterator();
      PackageManager pm = applicationContext.getPackageManager();
      while (i.hasNext()) {
          ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
          try {
              if (info.pid == pID) {
                  CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                  // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                  // info.processName +"  Label: "+c.toString());
                  // processName = c.toString();
                  processName = info.processName;
                  return processName;
              }
          } catch (Exception e) {
              // Log.d("Process", "Error>> :"+ e.toString());
          }
      }
      return processName;
  }
  
	
	
}