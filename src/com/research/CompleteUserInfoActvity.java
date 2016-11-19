package com.research;

import java.io.File;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.research.R;
import com.research.Entity.ChildCity;
import com.research.Entity.Country;
import com.research.Entity.CountryList;
import com.research.Entity.ResearchProjectItem;
import com.research.Entity.ResearchJiaState;
import com.research.Entity.Login;
import com.research.Entity.LoginResult;
import com.research.dialog.MMAlert;
import com.research.dialog.MMAlert.OnAlertSelectId;
import com.research.global.FeatureFunction;
import com.research.global.GlobalParam;
import com.research.global.GlobleType;
import com.research.global.ImageLoader;
import com.research.global.ResearchCommon;
import com.research.http.post.HttpRequestServer;
import com.research.map.BMapApiApp;
import com.research.net.ResearchException;
import com.research.scrollerpicker.SelectContryhday;
import com.research.scrollerpicker.SelectContryhday.OnButtonClickListener;

/**
 * 完善用户资料
 * 
 * @author dongli
 * 
 */
public class CompleteUserInfoActvity extends BaseActivity implements
		OnClickListener {

	private static final String TEMP_FILE_NAME = "header.jpg";
	private RelativeLayout mHeaderLayout, mAddrLayout, mSexLayout, mSignLayout,
			mNickNameLayout,makeFriendXuanyanLayout,makeFriendRequireLayout,xiangqinNameLayout,xiangqinXuanyanLayout,xiangqinRequireLayout;

	private TextView mSexTextView, mAddrTextView, mSiTextView,
			mHangYueTextView, mSubjectTextView, mNickNameTextView,mMKXuanyanTextView,mMKRequireTextView,mTrueNameTextView,mXqXuanyanTextView,mXqRequireTextView;
	private ImageView mImageView;

	private String mInputNickName, mInputAddr, mInputSign,mInputMKXuanyan,mInputMKRequire,mInputTrueName,mInputXqXuanyan,mInputXqRequire;
	private int mInputSex = 2;
	
	private int age = 0;
	/**
	 * // 省id
	 */
	private String mProvinceid;
	private String mProvice;

	/**
	 * //市id
	 */
	private String mCityid;
	private String mCity;
	/**
	 * // 课程id
	 */
	private String mCourseid;

	private int mType;

	private Login mLogin;
	private Bitmap mBitmap;
	private String mImageFilePath;
	private SelectContryhday contry;
	private ImageLoader mImageLoader;
	private String mHeadUrl;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case GlobalParam.MSG_CHECK_STATE:
				LoginResult loginResult = (LoginResult) msg.obj;
				if (loginResult == null) {
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.commit_data_error),
							Toast.LENGTH_LONG).show();
					return;
				}
				if (loginResult.mState.code != 0) {
					Toast.makeText(mContext, loginResult.mState.errorMsg,
							Toast.LENGTH_LONG).show();
					return;
				}
				Login login = loginResult.mLogin;
				login.password = ResearchCommon.getLoginResult(mContext).password;
				ResearchCommon.saveLoginResult(mContext, login);
				setResult(GlobalParam.SHOW_COMPLETE_RESULT);
				CompleteUserInfoActvity.this.finish();
				doPost();
				break;
			case GlobalParam.MSG_SHOW_LOAD_DATA:
				if (mInputAddr != null && !mInputAddr.equals("")) {
					mAddrTextView.setText(mInputAddr);
				}

				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_user_info);
		mContext = this;
		mLogin = (Login) getIntent().getSerializableExtra("login");
		mImageLoader = new ImageLoader();
		initCompent();

		// CountryList contryList = BMapApiApp.getContryList();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initCompent() {
		setTitleContent(0, R.drawable.ok_btn, R.string.complete_user_info);
		// mLeftBtn.setOnClickListener(this);
		mRightBtn.setOnClickListener(this);

		mHeaderLayout = (RelativeLayout) findViewById(R.id.new_header_layout);
		mAddrLayout = (RelativeLayout) findViewById(R.id.addr_layout);
		mSexLayout = (RelativeLayout) findViewById(R.id.sex_layout);
		mNickNameLayout = (RelativeLayout) findViewById(R.id.nickname_layout);
		mSignLayout = (RelativeLayout) findViewById(R.id.sign_layout);
		mHeaderLayout.setOnClickListener(this);
		mNickNameLayout.setOnClickListener(this);
		mAddrLayout.setOnClickListener(this);
		mSexLayout.setOnClickListener(this);
		mSignLayout.setOnClickListener(this);

		mNickNameTextView = (TextView) findViewById(R.id.nickname_content);
		mSexTextView = (TextView) findViewById(R.id.sex_content);
		mAddrTextView = (TextView) findViewById(R.id.addr_content);
		mSiTextView = (TextView) findViewById(R.id.sign_content);
		mHangYueTextView = (TextView) findViewById(R.id.hangyue_content);
		mSubjectTextView = (TextView) findViewById(R.id.subject_content);

		mImageView = (ImageView) findViewById(R.id.new_header_icon);
		
		
		//dzf_add
		makeFriendXuanyanLayout = (RelativeLayout) findViewById(R.id.make_friend_xuanyan_layout);	//交友宣言
		makeFriendRequireLayout = (RelativeLayout) findViewById(R.id.make_friend_require_layout);	//交友要求
		
		xiangqinNameLayout = (RelativeLayout) findViewById(R.id.xiangqin_layout);	//相亲所需姓名
		xiangqinXuanyanLayout = (RelativeLayout) findViewById(R.id.xiangqin_xuanyan_layout);	//相亲宣言
		xiangqinRequireLayout = (RelativeLayout) findViewById(R.id.xiangqin_require_layout);	//相亲要求
		
		makeFriendXuanyanLayout.setOnClickListener(this);
		makeFriendRequireLayout.setOnClickListener(this);
		xiangqinNameLayout.setOnClickListener(this);
		xiangqinXuanyanLayout.setOnClickListener(this);
		xiangqinRequireLayout.setOnClickListener(this);
		
		mMKXuanyanTextView = (TextView)findViewById(R.id.xuanyan_content);
		mMKRequireTextView = (TextView)findViewById(R.id.require_content);
		mTrueNameTextView = (TextView)findViewById(R.id.true_name__content);
		mXqXuanyanTextView = (TextView)findViewById(R.id.xiangqin_xuanyan_content);
		mXqRequireTextView = (TextView)findViewById(R.id.xiangqin_require_content);
				//--------
		// getMenuTitle();
		setTexts();

	}

	private void setTexts() {
		if (mLogin == null || mLogin.equals("")) {
			return;
		}
		mHeadUrl = mLogin.headsmall;
		if (mLogin.headsmall != null && !mLogin.headsmall.equals("")) {
			mImageView.setTag(mLogin.headsmall);
			mImageLoader.getBitmap(mContext, mImageView, null,
					mLogin.headsmall, 0, false, true);
		}
		mInputSex = mLogin.gender;
		if (mLogin.gender == 0) {
			mSexTextView.setText(mContext.getResources()
					.getString(R.string.man));
		} else if (mLogin.gender == 1) {
			mSexTextView.setText(mContext.getResources().getString(
					R.string.femal));
		} else if (mLogin.gender == 2) {
			mSexTextView.setText(mContext.getResources().getString(
					R.string.no_limit));
		}

		mProvice = mLogin.provinceid;
		mCity = mLogin.cityid;
		if ((mProvice != null && !mProvice.equals(""))
				&& (mCity != null && !mCity.equals(""))) {
			mAddrTextView.setText(mProvice + "  " + mCity + " ");
		}

		mInputNickName = mLogin.nickname;
		if (mInputNickName != null && !mInputNickName.equals("")) {
			mNickNameTextView.setText(mInputNickName + " ");
		}

		mInputSign = mLogin.sign;
		if (mInputSign != null && !mInputSign.equals("")) {
			mSiTextView.setText(mInputSign + " ");
		}
		
		mInputMKXuanyan = mLogin.makeFriendXuanyan;
		if (mInputMKXuanyan != null && !mInputMKXuanyan.equals("")) {
			mMKXuanyanTextView.setText(mInputMKXuanyan+" ");
		}
		
		mInputMKRequire = mLogin.makeFriendRequre;
		if (mInputMKRequire != null && !mInputMKRequire.equals("")) {
			mMKRequireTextView.setText(mInputMKRequire+" ");
		}
		
		mInputTrueName = mLogin.trueName;
		if (mInputTrueName != null && !mInputTrueName.equals("")) {
			mTrueNameTextView.setText(mInputTrueName+" ");
		}
		
		mInputXqXuanyan = mLogin.blindDate;
		if (mInputXqXuanyan != null && !mInputXqXuanyan.equals("")) {
			mXqXuanyanTextView.setText(mInputXqXuanyan+" ");
		}
		
		mInputXqRequire = mLogin.blindDateRequre;
		if (mInputXqRequire != null && !mInputXqRequire.equals("")) {
			mXqRequireTextView.setText(mInputXqRequire+" ");
		}
		
		age = mLogin.old;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			/*
			 * sendBroadcast(new
			 * Intent(GlobalParam.CANCLE_COMPLETE_USERINFO_ACTION));
			 * this.finish();
			 */
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	private void completeUserInfo() {
		if (!ResearchCommon.getNetWorkState()) {
			mBaseHandler.sendEmptyMessage(BASE_MSG_NETWORK_ERROR);
		}
		new Thread() {
			public void run() {

				try {
					ResearchCommon.sendMsg(mBaseHandler,
							BASE_SHOW_PROGRESS_DIALOG, R.string.commit_dataing);
					/*
					 * String file,String nickname,int gender, String
					 * sign,String provinceid,String city
					 */
					LoginResult login = ResearchCommon.getResearchInfo()
							.modifyUserInfo(mImageFilePath, mInputNickName,
									mInputSex, mInputSign, mProvice, mCity,
									mInputMKXuanyan,mInputMKRequire,mInputTrueName,mInputXqXuanyan,mInputXqRequire,age);
					ResearchCommon.sendMsg(mHandler,
							GlobalParam.MSG_CHECK_STATE, login);
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				} catch (ResearchException e) {
					e.printStackTrace();
					ResearchCommon.sendMsg(mBaseHandler,
							BASE_MSG_TIMEOUT_ERROR, mContext.getResources()
									.getString(e.getStatusCode()));
				} catch (Exception e) {
					e.printStackTrace();
					mBaseHandler.sendEmptyMessage(BASE_HIDE_PROGRESS_DIALOG);
				}
			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {

		case R.id.right_btn:
			if (mInputNickName == null || mInputNickName.equals("")) {
				Toast.makeText(mContext, "昵称为必填项!", Toast.LENGTH_LONG).show();
				return;
			} else if (mSexTextView == null || mSexTextView.equals("")) {
				Toast.makeText(mContext, "性别为必填项!", Toast.LENGTH_LONG).show();
				return;
			}
			completeUserInfo();
			break;
		case R.id.new_header_layout:
			selectImg();
			break;
		case R.id.addr_layout:

			Intent intent = new Intent();
			intent.setClass(mContext, TreeViewActivity.class);
			intent.putExtra("type", GlobleType.TreeViewActivity_City_TYPE);
			mType = GlobleType.COMPLETE_ADDR;
			startActivityForResult(intent, 1);
			break;
		case R.id.nickname_layout:
			Intent nickNameIntent = new Intent();
			nickNameIntent.setClass(mContext, WriteUserInfoActivity.class);
			nickNameIntent.putExtra("content", mInputNickName);
			nickNameIntent.putExtra("type", GlobleType.COMPLETE_NICKNAME);
			mType = GlobleType.COMPLETE_NICKNAME;
			startActivityForResult(nickNameIntent, 1);
			break;
		case R.id.sex_layout:
			MMAlert.showAlert(mContext, "", mContext.getResources()
					.getStringArray(R.array.sex_array), null,
					new OnAlertSelectId() {

						@Override
						public void onClick(int whichButton) {
							switch (whichButton) {
							case 0:
								mInputSex = 0;
								mSexTextView.setText(mContext.getResources()
										.getString(R.string.man));
								break;
							case 1:
								mInputSex = 1;
								mSexTextView.setText(mContext.getResources()
										.getString(R.string.femal));
								break;
							default:
								break;
							}
						}
					});
			break;

		case R.id.sign_layout:
			Intent signIntent = new Intent();
			signIntent.setClass(mContext, WriteUserInfoActivity.class);
			signIntent.putExtra("content", mInputSign);
			signIntent.putExtra("type", GlobleType.COMPLETE_SIGN);
			mType = GlobleType.COMPLETE_SIGN;
			startActivityForResult(signIntent, 1);
			break;
		case R.id.hangyue_layout:
			Intent hangyueIntent = new Intent();
			hangyueIntent.setClass(mContext, TreeViewActivity.class);
			hangyueIntent.putExtra("type",
					GlobleType.TreeViewActivity_Subject_TYPE);
			mType = GlobleType.COMPLETE_HANGYUE;
			startActivityForResult(hangyueIntent, 1);
			break;
		case R.id.subject_layout:
			Intent subjectIntent = new Intent();
			subjectIntent.setClass(mContext, TreeViewActivity.class);
			subjectIntent.putExtra("type",
					GlobleType.TreeViewActivity_Project_TYPE);
			mType = GlobleType.COMPLETE_SUBJECT;
			startActivityForResult(subjectIntent, 1);
			break;
		case R.id.make_friend_xuanyan_layout:
			//交友宣言
			Intent signIntent1 = new Intent();
			signIntent1.setClass(mContext, WriteUserInfoActivity.class);
			signIntent1.putExtra("content", mInputMKXuanyan);
			signIntent1.putExtra("type",GlobleType.COMPLETE_MKFRIEND_XUANYAN);
			mType = GlobleType.COMPLETE_MKFRIEND_XUANYAN;
			startActivityForResult(signIntent1, 1);
			break;
		case R.id.make_friend_require_layout:
			//交友要求
			Intent signIntent2 = new Intent();
			signIntent2.setClass(mContext, WriteUserInfoActivity.class);
			signIntent2.putExtra("content", mInputMKRequire);
			signIntent2.putExtra("type",GlobleType.COMPLETE_MKFRIEND_REQUIRE);
			mType = GlobleType.COMPLETE_MKFRIEND_REQUIRE;
			startActivityForResult(signIntent2, 1);
			break;
		case R.id.xiangqin_layout:
			Intent signIntent3 = new Intent();
			signIntent3.setClass(mContext, WriteUserInfoActivity.class);
			signIntent3.putExtra("content", mInputTrueName);
			signIntent3.putExtra("type",GlobleType.COMPLETE_XIANGQIN_NAME);
			mType = GlobleType.COMPLETE_XIANGQIN_NAME;
			startActivityForResult(signIntent3, 1);
			break;
		case R.id.xiangqin_xuanyan_layout:
			Intent signIntent4 = new Intent();
			signIntent4.setClass(mContext, WriteUserInfoActivity.class);
			signIntent4.putExtra("content", mInputXqXuanyan);
			signIntent4.putExtra("type",GlobleType.COMPLETE_XIANGQIN_XUANYAN);
			mType = GlobleType.COMPLETE_XIANGQIN_XUANYAN;
			startActivityForResult(signIntent4, 1);
			break;
		case R.id.xiangqin_require_layout:
			Intent signIntent5 = new Intent();
			signIntent5.setClass(mContext, WriteUserInfoActivity.class);
			signIntent5.putExtra("content", mInputXqRequire);
			signIntent5.putExtra("type",GlobleType.COMPLETE_XIANGQIN_REQUIRE);
			mType = GlobleType.COMPLETE_XIANGQIN_REQUIRE;
			startActivityForResult(signIntent5, 1);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mImageLoader.getImageBuffer() != null
				&& !mImageLoader.getImageBuffer().isEmpty()) {
			if (mImageLoader.getImageBuffer().containsKey(mLogin.headsmall)) {
				mImageView.setImageDrawable(null);
				if (mImageLoader.getImageBuffer().get(mLogin.headsmall) != null) {
					mImageLoader.getImageBuffer().get(mLogin.headsmall)
							.recycle();
				}
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			if (mType == GlobleType.COMPLETE_SEX) {
				mInputSex = data.getIntExtra("sex", 0);
				if (mInputSex == 0) {
					mSexTextView.setText("男 ");
				} else if (mInputSex == 1) {
					mSexTextView.setText("女 ");
				} else if (mInputSex == 2) {
					mSexTextView.setText("不限 ");
				}

			} else if (mType == GlobleType.COMPLETE_NICKNAME) {
				mInputNickName = data.getStringExtra("nickname");
				mNickNameTextView.setText(mInputNickName + " ");
			} else if (mType == GlobleType.COMPLETE_ADDR) {
				mInputAddr = data.getStringExtra("addr");
				// 省id
				mProvinceid = data.getStringExtra("shengid");
				mProvice = data.getStringExtra("provice");
				mCity = data.getStringExtra("city");
				// 市id
				mCityid = data.getStringExtra("shiid");
				mAddrTextView.setText(mInputAddr + " ");
			} else if (mType == GlobleType.COMPLETE_EMAIL) {

			} else if (mType == GlobleType.COMPLETE_COMPANY) {

			} else if (mType == GlobleType.COMPLETE_SIGN) {
				mInputSign = data.getStringExtra("sign");
				mSiTextView.setText(mInputSign + " ");
			}else if(mType == GlobleType.COMPLETE_MKFRIEND_XUANYAN){
				mInputMKXuanyan = data.getStringExtra("mk_xuanyan");
				mMKXuanyanTextView.setText(mInputMKXuanyan+" ");
			}else if(mType == GlobleType.COMPLETE_MKFRIEND_REQUIRE){
				mInputMKRequire = data.getStringExtra("mk_require");
				mMKRequireTextView.setText(mInputMKRequire+" ");
			}else if(mType == GlobleType.COMPLETE_XIANGQIN_NAME){
				mInputTrueName = data.getStringExtra("true_name");
				mTrueNameTextView.setText(mInputTrueName+" ");
			}else if(mType == GlobleType.COMPLETE_XIANGQIN_XUANYAN){
				mInputXqXuanyan = data.getStringExtra("xq_xuanyan");
				mXqXuanyanTextView.setText(mInputXqXuanyan+" ");
			}else if(mType == GlobleType.COMPLETE_XIANGQIN_REQUIRE){
				mInputXqRequire = data.getStringExtra("xq_require");
				mXqRequireTextView.setText(mInputXqRequire+" ");
			}
		}

		switch (requestCode) {
		case GlobalParam.REQUEST_GET_URI:
			if (resultCode == RESULT_OK) {
				doChoose(true, data);
			}

			break;

		case GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA:
			if (resultCode == RESULT_OK) {
				doChoose(false, data);
			}
			break;
		case GlobalParam.REQUEST_GET_BITMAP:
			if (resultCode == RESULT_OK) {

				Bundle extras = data.getExtras();
				if (extras != null) {

					mImageView.setImageBitmap(null);
					if (mBitmap != null && !mBitmap.isRecycled()) {
						mBitmap.recycle();
						mBitmap = null;
					}

					mBitmap = extras.getParcelable("data");
					mImageView.setImageBitmap(mBitmap);
					File file = new File(
							Environment.getExternalStorageDirectory()
									+ FeatureFunction.PUB_TEMP_DIRECTORY
									+ TEMP_FILE_NAME);
					if (file != null && file.exists()) {
						file.delete();
						file = null;
					}

					mImageFilePath = FeatureFunction.saveTempBitmap(mBitmap,
							"header.jpg");
				}

			}
			break;
		default:
			break;
		}
	}

	/**
	 * 选择头像对话框
	 */
	private void selectImg() {
		MMAlert.showAlert(mContext, "",
				mContext.getResources().getStringArray(R.array.camer_item),
				null, new OnAlertSelectId() {

					@Override
					public void onClick(int whichButton) {
						switch (whichButton) {
						case 0:
							getImageFromGallery();
							break;
						case 1:
							getImageFromCamera();
							break;
						default:
							break;
						}
					}
				});
	}

	/**
	 * 调用系统相机拍摄照片
	 */
	private void getImageFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (FeatureFunction.newFolder(Environment.getExternalStorageDirectory()
				+ FeatureFunction.PUB_TEMP_DIRECTORY)) {
			File out = new File(Environment.getExternalStorageDirectory()
					+ FeatureFunction.PUB_TEMP_DIRECTORY, TEMP_FILE_NAME);
			Uri uri = Uri.fromFile(out);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

			startActivityForResult(intent,
					GlobalParam.REQUEST_GET_IMAGE_BY_CAMERA);
		}

	}

	/**
	 * 选择手机相册中的图片
	 */
	private void getImageFromGallery() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");

		startActivityForResult(intent, GlobalParam.REQUEST_GET_URI);
	}

	/**
	 * 处理选择的图片
	 * 
	 * @param isGallery
	 * @param data
	 */
	private void doChoose(final boolean isGallery, final Intent data) {
		if (isGallery) {
			originalImage(data);
		} else {
			if (data != null) {
				originalImage(data);
			} else {
				// Here if we give the uri, we need to read it
				String path = Environment.getExternalStorageDirectory()
						+ FeatureFunction.PUB_TEMP_DIRECTORY + TEMP_FILE_NAME;
				String extension = path.substring(path.indexOf("."),
						path.length());
				if (FeatureFunction.isPic(extension)) {
					startPhotoZoom(Uri.fromFile(new File(path)));
				} else {
					// Toast.makeText(mContext, R.string.please_choose_pic,
					// Toast.LENGTH_SHORT).show();
				}
				// mImageFilePath =
				// FeatureFunction.PUB_TEMP_DIRECTORY+TEMP_FILE_NAME;
				// ShowBitmap(false);
			}
		}
	}

	private void originalImage(Intent data) {
		/*
		 * switch (requestCode) {
		 */
		// case FLAG_CHOOSE:
		Uri uri = data.getData();
		// Log.d("may", "uri=" + uri + ", authority=" + uri.getAuthority());
		if (!TextUtils.isEmpty(uri.getAuthority())) {
			Cursor cursor = getContentResolver().query(uri,
					new String[] { MediaStore.Images.Media.DATA }, null, null,
					null);
			if (null == cursor) {
				// Toast.makeText(mContext, R.string.no_found,
				// Toast.LENGTH_SHORT).show();
				return;
			}
			cursor.moveToFirst();
			String path = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			Log.d("may", "path=" + path);
			String extension = path.substring(path.lastIndexOf("."),
					path.length());
			if (FeatureFunction.isPic(extension)) {
				/*
				 * Intent intent = new Intent(mContext,
				 * ChooseHeaderActivity.class); intent.putExtra("path", path);
				 * startActivityForResult(intent, REQUEST_GET_BITMAP);
				 */

				startPhotoZoom(data.getData());

			} else {
				// Toast.makeText(mContext, R.string.please_choose_pic,
				// Toast.LENGTH_SHORT).show();
			}
			// ShowBitmap(false);

		} else {
			Log.d("may", "path=" + uri.getPath());
			String path = uri.getPath();
			String extension = path.substring(path.lastIndexOf("."),
					path.length());
			if (FeatureFunction.isPic(extension)) {
				startPhotoZoom(uri);
			} else {
				// Toast.makeText(mContext, R.string.please_choose_pic,
				// Toast.LENGTH_SHORT).show();
			}
			// mImageFilePath = uri.getPath();
			// ShowBitmap(false);
		}
	}

	/**
	 * 裁剪图片
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 180);
		intent.putExtra("outputY", 180);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, GlobalParam.REQUEST_GET_BITMAP);
	}

	/**
	 * 向服务器发送请求，获赠完善资料积分
	 */
	public void doPost() {
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();// 用来在线程中支持显示Toast
				Object flag = HttpRequestServer.toGetComplete(mLogin.uid);
				Map<String, String> success = null;
				try {
					success = (Map<String, String>) flag;
				} catch (Exception e) {
					/*Toast.makeText(mContext, "获取积分失败", Toast.LENGTH_SHORT)
							.show();*/
				}
				if (success != null && success.get("success").equals("true")) {
					Toast.makeText(mContext,
							"完善资料赠送" + success.get("integral") + "积分",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, flag + "没有赠送成功",
							Toast.LENGTH_SHORT).show();
				}
				Looper.loop();
			}
		}.start();
	}

}
