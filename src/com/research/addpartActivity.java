package com.research;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import com.research.Entity.Login;
import com.research.Entity.LoginResult;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.http.post.HttpRequestServer;
import com.research.io.file.AndroidFolder;
import com.research.io.file.UserFolder;
import com.research.net.ResearchInfo;

import date.tool.handleDate;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * 广告专区
 * 
 * @author Runt02
 * 
 */
public class addpartActivity extends BaseActivity implements OnClickListener {

	private String title;
	private Login mLogin;
	private ScrollView scrollview;
	private LinearLayout addLinear;
	private final static int SETPIC = 131;
	List<Map<String, String>> list;
	View add;
	/*
	 * 处理消息
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SETPIC:
				File[] fils = new File(AndroidFolder.getSdCardPath()
						+ UserFolder.datapath + "add").listFiles();
				String str = "";
				int imgWidth=0;
				int imgHeight=0;
				try {
					int i = 0;
					WindowManager wm = (WindowManager) getBaseContext()
			                    .getSystemService(Context.WINDOW_SERVICE);
			     int width = wm.getDefaultDisplay().getWidth();
			     int height = wm.getDefaultDisplay().getHeight();
					for (; i < list.size(); i++) {
						str = "1 "+i;
						LinearLayout linear = new LinearLayout(mContext);// 线性布局方式
						str = "2 "+i;
						linear.setOrientation(LinearLayout.VERTICAL);// 控件对其方式为垂直排列
						str = "3 "+i;
						LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						lParams.setMargins(5, 5, 5, 5);
						str = "4 "+i;
						linear.setId(Integer.parseInt(list.get(i).get("id")));//设置控件的id
						linear.setOnClickListener((OnClickListener) mContext);
						linear.setLayoutParams(lParams);
						str = "5 "+i;
						ImageView image = new ImageView(mContext);// 创建图片控件
						BitmapFactory.Options option = new BitmapFactory.Options();
						option.inSampleSize = 4;
						//Bitmap bm = BitmapFactory.decodeFile(fils[i].getPath(),option);
						String path=AndroidFolder.getSdCardPath()+ UserFolder.datapath + "add";//设置sd卡路径
						String imageurl=list.get(i).get("image");//获取网络图片的地址
						String filename=imageurl.substring(imageurl.lastIndexOf('/'));//将地址拆分为文件名
						Bitmap bm = BitmapFactory.decodeFile(path+"/"+filename,option);//
						imgHeight=bm.getHeight();
						imgWidth=bm.getWidth();
						double height_=width*((double)imgHeight/(double)imgWidth);
						image.setLayoutParams(new LayoutParams(width, (int)height_));
						str = "7"+i;
						image.setImageBitmap(bm);
						str = "10";
						linear.addView(image);
						addLinear.addView(linear);// 将linear加入控件中
					}
					//Toast.makeText(mContext, "屏幕:" + imgWidth+" * "+imgHeight+" ", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					Toast.makeText(
							mContext,
							"错误" + e.getMessage() + " "
									+ e.getLocalizedMessage() + " str" + str
									+ " " + e.toString(), Toast.LENGTH_SHORT)
							.show();
				}
				mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				break;
			
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		this.setContentView(R.layout.add_part);
		mLogin = ResearchCommon.getLoginResult(mContext);
		title = getIntent().getSerializableExtra("title").toString();
		initCompent();
		ResearchCommon.sendMsg(
				mBaseHandler,
				BASE_SHOW_PROGRESS_DIALOG,
				mContext.getResources().getString(
						R.string.add_more_loading));
		
		doPostDownloadPic();//下载图片
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	/**
	 * 初始化控件
	 */
	private void initCompent() {
		setTitleContent(R.drawable.back_btn, 0, title);
		mLeftBtn.setOnClickListener(this);
		scrollview = (ScrollView) findViewById(R.id.add_scroll);
		addLinear = (LinearLayout) findViewById(R.id.add_linear);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			addpartActivity.this.finish();
			break;

		default:
			int index=HttpRequestServer.chatList.toString().indexOf(v.getId());//判断你好友id是否存在已经聊天的list集合
	        if(index>-1){
	        	Toast.makeText(mContext,"该广告积分已经送过了！",Toast.LENGTH_SHORT).show();
	        	break;
	        }
			for(int i=0;i<list.size();i++){
				String id=list.get(i).get("id");
				if(v.getId()==Integer.parseInt(id)){
					this.add=v;
					doPost();
				}
			}
			break;
		}
	}

	/**
	 * 向服务器发送请求，获赠每日看广告积分
	 */
	public void doPost() {

		new Thread() {
			@Override
			public void run() {
				Looper.prepare();// 用来在线程中支持显示Toast
				Object flag = HttpRequestServer.toGetTodayAdd(mLogin.uid,add.getId()+"");
				Map<String, String> success=null;
				try {
					success = (Map<String, String>) flag;
				} catch (Exception e) {
					/*Toast.makeText(mContext,
							"获取积分失败",
							Toast.LENGTH_SHORT).show();*/
				}
				if (success != null) {
					if (success.get("success").equals("true")) {
						int integral = Integer.parseInt(success.get("integral")
								.toString());
						mBaseHandler
								.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
						if (integral > 0) {
							Toast.makeText(mContext,
									"每日看广告赠送" + integral + "积分",
									Toast.LENGTH_SHORT).show();
							HttpRequestServer.chatList.add(add.getId());//添加到已经赠送的广告list集合中
							try {
								UserFolder uf = new UserFolder(mLogin.uid);// 打开用户的文件夹
								File file=uf.EditFile(uf.folders[2]+"/"+handleDate.getDateToInt(handleDate.getDateToInt()));//添加今天的日期文件
								FileWriter fw=new FileWriter(file);
								BufferedWriter  bw=new BufferedWriter(fw);
								bw.write(add.getId()+"\t\n");
								bw.close();
								fw.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else {
						/*
						 * Toast.makeText(mContext, flag + "没有赠送成功",
						 * Toast.LENGTH_SHORT).show();
						 */
					}
				}
				// Toast.makeText(mContext,
				// flag+" doPost Click isCheck:"+isCheck+" "+MainActivity.todayAdd,
				// Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}.start();
	}

	/**
	 * 下载广告图片
	 */
	public void doPostDownloadPic() {
		new Thread() {
			@Override
			public void run() {
				ResearchCommon.sendMsg(
						mBaseHandler,
						BASE_SHOW_PROGRESS_DIALOG,
						mContext.getResources().getString(
								R.string.add_more_loading));
				String str = "";
				Looper.prepare();// 用来在线程中支持显示Toast
				// Object flag =
				// HttpRequestServer.getPHPRequest("http://"+ResearchInfo.ip+"/lediao/index.php/home/index/ad",null);//获取后台服务的数据
				Object flag = HttpRequestServer
						.getPHPMultiRequest(
								"http://"+ResearchInfo.IP+ResearchInfo.PORT+"/lediao/index.php/ads/apiad/ad",
								null);// 获取后台服务的数据
				// Object flag = HttpRequestServer.getRequest();//获取后台服务的数据
				// Toast.makeText(mContext,
				// flag.toString()+"NMB doPostToGetPic ",Toast.LENGTH_SHORT).show();
				if (flag != null) {
					try {
						str = "1";
						list = (List<Map<String, String>>) flag;
						// Toast.makeText(mContext,
						// flag.toString()+" doPostToGetPic ",Toast.LENGTH_SHORT).show();
						str = "2";

						str = "3";
						//if(!MainActivity.updateAdd){
						if(true){
							/**
							 * 下载图片
							 */
							new Thread() {
								@Override
								public void run() {
									Looper.prepare();// 用来在线程中支持显示Toast
									String foldername = AndroidFolder.getSdCardPath()+ UserFolder.datapath + "add";//指定广告文件存放的文件夹
									UserFolder.delFolder(foldername);//清空文件夹中的文件
									boolean downloaded=true;
									for (int i = 0; i < list.size(); i++) {
										try {
											// Toast.makeText(mContext,
											// "正在下载图片 picUrl:"+picUrl,Toast.LENGTH_SHORT).show();
											String filename = AndroidFolder
													.getSdCardPath()
													+ UserFolder.datapath + "add/";
											String image = list.get(i).get("image");
											int last = image.lastIndexOf("/");
											filename += image.substring(last);
											File file = new File(filename);
											URL url = new URL(image);
											URLConnection con = url
													.openConnection();// 打开连接
											InputStream is = con.getInputStream();// 输入流
											File folder = new File(foldername);
											if (!folder.exists()) { // 如果目标文件夹不存在，创建文件夹
												folder.mkdirs();
											}
											if (file.exists()) { // 如果目标文件已经存在则删除
												file.delete();
											}
											// Toast.makeText(mContext,
											// "正在下载图片 picUrl:"+picUrl,Toast.LENGTH_SHORT).show();
											int contentLength = con
													.getContentLength();// 获得文件的长度
											byte[] bs = new byte[1024];// 1K的数据缓冲
											int len;// 读取到的数据长度
											FileOutputStream os = new FileOutputStream(
													file);// 输出的文件流
											// 开始读取
											while ((len = is.read(bs)) != -1) {
												os.write(bs, 0, len);
											}
											// 完毕，关闭所有链接
											os.close();
											is.close();
										} catch (Exception e) {
											Toast.makeText(mContext,
													"错误2:" + e.getMessage(),
													Toast.LENGTH_SHORT).show();
											downloaded=false;
											break;
										}
									}
									if(downloaded){
										Message msg = new Message();// 创建message信息
										msg.what = SETPIC;
										mHandler.sendMessage(msg);// 发送到主线程用来更改控件属性
										MainActivity.updateAdd=true;
										UserFolder uf = new UserFolder(mLogin.uid);//打开用户的文件夹
										uf.EditFile(uf.folders[3]+"/"+handleDate.getDateToInt(handleDate.getDateToInt()));//添加今天的日期文件
									}else{
										Toast.makeText(mContext,"图片下载失败",Toast.LENGTH_SHORT).show();
										mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
									}
									Looper.loop();
								}
							}.start();
						}else{
							Message msg = new Message();// 创建message信息
							msg.what = SETPIC;
							mHandler.sendMessage(msg);
						}
						//Toast.makeText(mContext, "list:" + list.size(),Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						Toast.makeText(
								mContext,
								"错误1: str:" + str + " " + e.getMessage() + " "
										+ e.toString(), Toast.LENGTH_SHORT)
								.show();
					}
					// Toast.makeText(mContext,
					// flag.toString()+" doPostToGetPic "+str2,Toast.LENGTH_SHORT).show();
				}
				Looper.loop();
			}
		}.start();
	}

}
