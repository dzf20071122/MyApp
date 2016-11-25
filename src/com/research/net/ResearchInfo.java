package com.research.net;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.research.ChatMainActivity;
import com.research.Entity.AddGroup;
import com.research.Entity.AppNews;
import com.research.Entity.ChatImg;
import com.research.Entity.CheckFriends;
import com.research.Entity.CommentWeibo;
import com.research.Entity.CountryList;
import com.research.Entity.Favorite;
import com.research.Entity.FriendsLoop;
import com.research.Entity.FriendsLoopItem;
import com.research.Entity.GroupList;
import com.research.Entity.LoginResult;
import com.research.Entity.Meeting;
import com.research.Entity.MeetingItem;
import com.research.Entity.MessageInfo;
import com.research.Entity.MessageResult;
import com.research.Entity.MessageType;
import com.research.Entity.MorePicture;
import com.research.Entity.ResearchJiaState;
import com.research.Entity.Room;
import com.research.Entity.RoomList;
import com.research.Entity.RoomUsrList;
import com.research.Entity.SchoolMeeting;
import com.research.Entity.SchoolMeetingList;
import com.research.Entity.Topic;
import com.research.Entity.UserList;
import com.research.Entity.VersionInfo;
import com.research.global.FeatureFunction;
import com.research.global.Paging;
import com.research.global.ResearchCommon;
import com.research.map.BMapApiApp;
import com.research.org.json.JSONException;
import com.research.org.json.JSONObject;

public class ResearchInfo implements Serializable {
	private static final long serialVersionUID = 1651654562644564L;

	public static final String IP = "123.56.142.185";// "123.56.154.168";//"58.96.179.194";//"123.56.154.168";//120.26.54.221
														// 27.54.249.122
	public static final String PORT = "";// ":8080";
	public static final String SERVER_PREFIX = "http://" + IP + PORT
			+ "/lediao/index.php";
	// public static final String SERVER_PREFIX =
	// "http://www.spermatium.com/lediao/index.php";
	// "http://yuliao.zgcom.cn/index.php";
	public static final String CODE_URL = "http://" + IP + PORT + "/lediao";
	public static final String HEAD_URL = "http://" + IP + PORT
			+ "/lediao/index.php";
	public static final int PAGESIZE = 20;

	public static final String APPKEY = "0e93f53b5b02e29ca3eb6f37da3b05b9";

	public String request(String url, ResearchParameters params,
			String httpMethod, int loginType) throws ResearchException {
		String rlt = null;
		rlt = Utility.openUrl(url, httpMethod, params, loginType);
		if (rlt != null && rlt.length() != 0) {
			int c = rlt.indexOf("{");
			if (c != 0) {
				rlt = rlt.subSequence(c, rlt.length()).toString();
			}
		}
		return rlt;
	}

	public String requestProtocol(String url, ResearchParameters params,
			String httpMethod) throws ResearchException {
		String rlt = null;
		rlt = Utility.openUrl(url, httpMethod, params, 0);
		return rlt;

	}

	/**
	 * 鐢ㄦ埛娉ㄥ唽鍗忚 /user/apiother/regist
	 * 
	 * @throws ResearchException
	 */
	public String getProtocol() throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		String url = SERVER_PREFIX + "/user/apiother/regist";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if (reString != null && !reString.equals("")) {
			Log.e("reString", reString);
			return reString;
		}
		return null;

	}

	/**
	 * 鑾峰彇楠岃瘉鐮� /user/apiother/getCode
	 * /user/apiother/getCode?act=getcode&tel=13808172548
	 * 
	 * @param isGetCode
	 *            true=getcode false=-clean
	 * @throws WeiboException
	 */
	public ResearchJiaState getVerCode(String tel, int type)
			throws ResearchException {
		if (tel == null || tel.equals("")) {
			return null;
		}
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("phone", tel);
		if (type != 0) {
			bundle.add("type", String.valueOf(type));
		}
		String url = SERVER_PREFIX + "/user/apiother/getCode";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 0);
		try {
			if (reString != null && !reString.equals("null")
					&& !reString.equals("")) {
				return new ResearchJiaState(new JSONObject(reString));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 3. 楠岃瘉楠岃瘉鐮�(/user/apiother/checkCode)
	 * 
	 * @param phone
	 *            true string 鎵嬫満鍙�
	 * @param code
	 *            true string 楠岃瘉鐮�
	 */
	public ResearchJiaState checkVerCode(String phone, String code)
			throws ResearchException {
		if (phone == null || phone.equals("")) {
			return null;
		}

		ResearchParameters bundle = new ResearchParameters();
		bundle.add("phone", phone);
		bundle.add("code", String.valueOf(code));
		String url = SERVER_PREFIX + "/user/apiother/getCode";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 0);
		try {
			if (reString != null && !reString.equals("null")
					&& !reString.equals("")) {
				return new ResearchJiaState(new JSONObject(reString));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 浜屻�佺櫥闄嗘敞鍐�
	/**
	 * 
	 * 1銆佹敞鍐� 鈶犮��闈炲鍛樻敞鍐�(/user/api/regist) 1銆丠TTP璇锋眰鏂瑰紡 GET/POST 2銆佹槸鍚﹂渶瑕佺櫥褰�
	 * false 3銆佹敮鎸佹牸寮� JSON 鍙傛暟 蹇呴�� 绫诲瀷 璇存槑 phone true string 鐢ㄦ埛鐨勬墜鏈哄彿
	 * password true string 瀵嗙爜 name true string 鐢ㄦ埛濮撳悕 validCode true string
	 * 閭�璇风爜楠岃瘉鐮�
	 */
	public LoginResult register(String phone, String password, String validCode)
			throws ResearchException {
		LoginResult register = null;
		ResearchParameters bundle = new ResearchParameters();
		if ((phone == null || phone.equals(""))
				|| (password == null || password.equals(""))) {
			return null;
		}
		bundle.add("phone", phone);
		bundle.add("password", password);
		String url = SERVER_PREFIX + "/user/api/regist";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 0);
		Log.i("regist", reString + "");
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new LoginResult(reString);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return register;
	}

	/**
	 * 
	 * 鐢ㄦ埛鐧诲綍 /user/api/login
	 * 
	 * @param phone
	 *            true string 鐢ㄦ埛鐨勬墜鏈哄彿
	 * @param password
	 *            true string 瀵嗙爜
	 * @param appkey
	 * @return
	 * @throws ResearchException
	 *             http://192.168.1.12/research/index.php/user/api/login?phone=
	 *             13689084790&password=123456
	 */
	public LoginResult getLogin(String phone, String password)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("phone", phone);
		bundle.add("password", password);
		String url = SERVER_PREFIX + "/user/api/login";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 0);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			return new LoginResult(reString.trim());
		}

		return null;

	}

	/**
	 * 鈶犮��蹇樿瀵嗙爜锛岃幏鍙栨柊瀵嗙爜(/api/index/forgetpwd)
	 * 
	 * @param phone
	 *            true int
	 * @throws ResearchException
	 */
	public ResearchJiaState findPwd(String phone) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("phone", phone);
		String url = SERVER_PREFIX + "/api/index/forgetpwd";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 0);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * 鏇存敼鐢ㄦ埛璧勬枡 post鏂瑰紡璇锋眰 /user/api/edit
	 * 
	 * @param picture
	 *            true file 涓婁紶鍥剧墖
	 * @param nickname
	 *            true String 鏄电О
	 * @param gender
	 *            false string 0-鐢� 1-濂� 2-鏈煡 鏈～鍐�
	 * @param sign
	 *            false string 绛惧悕
	 * @param province
	 *            false int 鐪�
	 * @param city
	 *            false int 甯�
	 * @throws ResearchException
	 * 
	 */
	public LoginResult modifyUserInfo(String file, String nickname, int gender,
			String sign, String provinceid, String city, String friendXuanyan,
			String friendRequire, String realName, String blindDateXuanyan,
			String blindDateRequir, int age) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();

		// 蹇呭～閫夐」
		bundle.add("appkey", APPKEY);
		if (file != null && !file.equals("") && file.length() > 0) {
			List<MorePicture> listpic = new ArrayList<MorePicture>();
			listpic.add(new MorePicture("picture", file));
			bundle.addPicture("pic", listpic);
		}

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (nickname != null && !nickname.equals("")) {
			bundle.add("nickname", nickname);
		}
		bundle.add("gender", String.valueOf(gender));

		bundle.add("sign", sign);

		if (provinceid != null && !provinceid.equals("")) {
			bundle.add("province", provinceid);
		}
		if (city != null && !city.equals("")) {
			bundle.add("city", city);
		}

		bundle.add("friendXuanyan", friendXuanyan);
		bundle.add("friendRequire", friendRequire);
		bundle.add("realName", realName);
		bundle.add("blindDateXuanyan", blindDateXuanyan);
		bundle.add("blindDateRequir", blindDateRequir);
		bundle.add("age", age + "");

		String url = SERVER_PREFIX + "/user/api/edit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			return new LoginResult(reString);
		}
		return null;
	}

	/**
	 * 鏍规嵁id鑾峰彇鐢ㄦ埛璧勬枡
	 * 
	 * @param uid
	 * @return
	 * @throws ResearchException
	 */
	public LoginResult getUserInfo(String uid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("fuid", uid);
		bundle.add("uid", String.valueOf(ResearchCommon.getUserId(BMapApiApp
				.getInstance())));
		String url = SERVER_PREFIX + "/user/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			return new LoginResult(reString);
		}

		return null;
	}

	/**
	 * 16. 璁剧疆鏄熸爣鏈嬪弸(/user/api/setStar) fuid true int 鐢ㄦ埛id
	 */
	public LoginResult setStar(String fuid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		if (fuid == null || fuid.equals("")) {
			return null;
		}
		bundle.add("fuid", fuid);
		bundle.add("uid", String.valueOf(ResearchCommon.getUserId(BMapApiApp
				.getInstance())));
		String url = SERVER_PREFIX + "/user/api/setStar";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			return new LoginResult(reString);
		}

		return null;
	}

	/**
	 * 
	 * 涓婁紶鏂囦欢
	 * 
	 * @param f_upload
	 * @param type
	 *            1-鍥剧墖 2-澹伴煶
	 * @return
	 * @throws ResearchException
	 */
	public ChatImg uploadFile(String f_upload, int type)
			throws ResearchException {
		ChatImg chatImg = null;
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		List<MorePicture> listpic = new ArrayList<MorePicture>();
		listpic.add(new MorePicture("f_upload", f_upload));
		bundle.addPicture("pic", listpic);

		bundle.add("type", String.valueOf(type));
		String url = SERVER_PREFIX + "/api/index/upload";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("data")) {
					String s = json.getString("data");
					if (s != null && !s.equals("")) {
						chatImg = ChatImg.getInfo(s);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return chatImg;
	}

	// 鍥涖�侀�氳褰�
	// 1.鏈嬪弸鍒楄〃
	/**
	 * 鈶犮��鏈嬪弸鍒楄〃(/user/api/friendList) 鑾峰彇濂藉弸鍒楄〃
	 * 
	 * @param page
	 * @return
	 * @throws ResearchException
	 */
	public GroupList getUserList() throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", String.valueOf(ResearchCommon.getUserId(BMapApiApp
				.getInstance())));
		String url = SERVER_PREFIX + "/user/api/friendList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			return new GroupList(reString);
		}

		return null;
	}

	// 2銆佹坊鍔犳湅鍙�
	// 1.1 娣诲姞鏈嬪弸
	/**
	 * 鈶犲ソ鍙嬬敵璇�(/user/api/applyAddFriend) /api/user/to_friend
	 * 
	 * @param action
	 *            to_friend
	 * @param uid
	 * @param fuid
	 *            http://www.deedkey.com/friend/Index/action?action=to_friend&
	 *            uid=200269&fuid=53
	 */
	public ResearchJiaState applyFriends(String userID, String fuid,
			String reason) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		if ((userID == null || userID.equals(""))
				|| (fuid == null || fuid.equals(""))
		/* || (reason == null || reason.equals("")) */) {
			return null;
		}
		bundle.add("uid", userID);
		bundle.add("fuid", fuid);
		bundle.add("content", reason);
		bundle.add("appkey", APPKEY);
		String url = SERVER_PREFIX + "/user/api/applyAddFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 鈶″悓鎰忓姞涓哄ソ鍙�(/user/api/agreeAddFriend)
	 * 
	 * @param action
	 *            be_friend
	 * @param uid
	 * @param fuid
	 */
	public ResearchJiaState agreeFriends(String fuid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		bundle.add("appkey", APPKEY);
		String url = SERVER_PREFIX + "/user/api/agreeAddFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 鈶� 鎷掔粷鍔犱负濂藉弸(/user/api/refuseAddFriend)
	 * 
	 * @param action
	 *            refuse_f
	 * @param uid
	 * @param toUid
	 *            琚嫆缁濈殑uid
	 */
	public ResearchJiaState denyFriends(String toUid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", toUid);
		bundle.add("appkey", APPKEY);
		String url = SERVER_PREFIX + "/user/api/refuseAddFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 鈶� 鍒犻櫎濂藉弸(/user/api/deleteFriend)
	 * 
	 * @param uid
	 * @param fuid
	 *            濂藉弸uid
	 */
	public ResearchJiaState cancleFriends(String fuid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		bundle.add("appkey", APPKEY);
		String url = SERVER_PREFIX + "/user/api/deleteFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	// 1.2鎼滃彿鐮�
	/**
	 * 
	 * 鈶� 閫氳繃鎵嬫満鍙锋垨鏄电О鎼滅储(/user/api/search)
	 * 
	 * @param userName
	 * @param page
	 * @return
	 * @throws ResearchException
	 */
	int id = 0;

	public UserList search_number(String search, int page)
			throws ResearchException {
		id = id + 1;
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("search", search);

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("page", String.valueOf(page));
		String url = SERVER_PREFIX + "/user/api/search";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		Log.e("search_number", "id:" + id);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new UserList(reString, 0);
		}

		return null;
	}

	// 1.3 浠庢墜鏈洪�氳褰曞垪琛ㄦ坊鍔�
	/**
	 * 鈶� 瀵煎叆鎵嬫満閫氳褰�(/user/api/importContact)
	 */
	public CheckFriends getContactUserList(String phone)
			throws ResearchException {
		if (phone == null || phone.equals("") || phone.contains("null")) {
			return null;
		}
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("phone", phone);
		bundle.add("uid", String.valueOf(ResearchCommon.getUserId(BMapApiApp
				.getInstance())));
		String url = SERVER_PREFIX + "/user/api/importContact";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			return new CheckFriends(reString);
		}

		return null;
	}

	// 3銆佹柊鐨勬湅鍙�
	/**
	 * 鈶犮��鏂扮殑鏈嬪弸(/api/user/newfriend)
	 * 
	 * @param phone
	 *            true string 鏍煎紡 鎵嬫満1,鎵嬫満2,鎵嬫満3,鎵嬫満4
	 * @param uid
	 *            鐧诲綍鐢ㄦ埛id
	 * @throws ResearchException
	 */
	public UserList getNewFriend(String phone) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		if (phone == null || phone.equals("") || phone.startsWith(",")) {
			return null;
		}
		bundle.add("phone", phone);

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/newFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			// Log.e("addFriend", reString);
			return new UserList(reString, 1);

		}

		return null;
	}

	/**
	 * 鈶犮��娣诲姞鍏虫敞涓庡彇娑堝叧娉�(/api/publics/follow)
	 * 
	 * @param publics_id
	 *            true int 鍏紬鍙风殑ID
	 * @param uid
	 *            鐧诲綍鐢ㄦ埛id
	 */
	public ResearchJiaState addFocus(String subUserID) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("publics_id", subUserID);
		bundle.add("appkey", APPKEY);
		String url = SERVER_PREFIX + "/api/publics/follow";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 鈶� 鏈嬪弸鍒嗙粍(/api/user/group)
	 * 
	 * @param uid
	 *            鐧诲綍鐢ㄦ埛id
	 * @param type
	 *            true int 0-鍚嶅瓧 1-鍦板尯 2-棰戠巼 3-娣诲姞鏃堕棿 4-璇剧▼ 5-琛屼笟
	 * @throws ResearchException
	 */
	public UserList getContactGroupList(int type) throws ResearchException {

		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("type", String.valueOf(type));
		String url = SERVER_PREFIX + "/api/user/group";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			// Log.e("getContactGroupList", reString);
			try {
				return new UserList(reString, 0);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 娣诲姞鍏虫敞涓庡彇娑堝叧娉�(/api/user/follow)
	 * 
	 * @param uid
	 *            鐧诲綍鐢ㄦ埛id
	 * @param fuid
	 *            瑕佸叧娉ㄧ殑鐢ㄦ埛ID
	 * @param type
	 *            0-鍙栨秷鍏虫敞 1-娣诲姞鍏虫敞
	 * @param appkey
	 * @throws ResearchException
	 * 
	 */
	public ResearchJiaState addfocus(String fuid/* ,int type */)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		/* bundle.add("type",String.valueOf(type)); */
		String url = SERVER_PREFIX + "/api/user/follow";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			// Log.e("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// dalong

	/**
	 * 
	 * 鍔犲叆榛戝悕鍗� /user/api/black
	 * 
	 * @param blackUid
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState addBlock(String blackUid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", blackUid);
		String url = SERVER_PREFIX + "/user/api/black";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			// Log.e("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 
	 * 涓炬姤濂藉弸 /api/user/jubao
	 * 
	 * @param fuid
	 * @param content
	 * @param type
	 *            1-鐢ㄦ埛 2-璁㈤槄鍙�
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState reportedFriend(String fuid, String content, int type)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		bundle.add("type", String.valueOf(type));
		bundle.add("content", content);
		String url = SERVER_PREFIX + "/api/user/jubao";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			// Log.e("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 
	 * 鑾峰彇榛戝悕鍗曞垪琛�/user/api/blackList
	 * 
	 * @param page
	 * @return
	 * @throws ResearchException
	 */
	public UserList getBlockList(/* int page */) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		/*
		 * bundle.add("page", String.valueOf(page)); bundle.add("pageSize",
		 * String.valueOf(Common.LOAD_SIZE));
		 */
		String url = SERVER_PREFIX + "/user/api/blackList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);

		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			// Log.e("reString", reString);

			return new UserList(reString, 0);
		}

		return null;
	}

	/**
	 * 
	 * 鍙栨秷榛戝悕鍗� /user/api/black
	 * 
	 * @param fuid
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState cancelBlock(String fuid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		String url = SERVER_PREFIX + "/user/api/black";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			// Log.e("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 娣诲姞鏀惰棌(/user/api/favorite)
	 * 
	 * @param uid
	 *            true 鐧婚檰鐢ㄦ埛id
	 * @throws ResearchException
	 * @param fuid
	 *            true int 琚敹钘忎汉鐨剈id
	 * @param otherid
	 *            false int 濡傛灉鏄敹钘忕殑缇ょ粍鐨勬秷鎭紝灏变紶鍏ユid
	 * @param content
	 *            true string 鏀惰棌鐨勫唴瀹�
	 */
	public ResearchJiaState favoreiteMoving(String fuid, String groupId,
			String content) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if ((content == null || content.equals(""))
				&& (fuid == null || fuid.equals(""))
				&& (groupId == null || groupId.equals(""))) {
			return null;
		}
		bundle.add("content", content);
		if (fuid != null && !fuid.equals("")) {
			bundle.add("fuid", fuid);
		}

		if (groupId != null && !groupId.equals("")) {
			bundle.add("otherid", groupId);
		}

		String url = SERVER_PREFIX + "/user/api/favorite";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			Log.e("favoreiteMoving", reString);
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * http://117.78.2.70/index.php/user/api/favoriteList?uid=200000
	 * 鏀惰棌鍒楄〃(/user/api/favoriteList)
	 * 
	 * @parem uid true 鐧婚檰鐢ㄦ埛id
	 *        http://117.78.2.70/index.php/user/api/favoriteList
	 *        ?appkey=0e93f53b5b02e29ca3eb6f37da3b05b9&uid=200018&page=1,
	 *        count=20
	 */
	public Favorite favoriteList(int page) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (page != 0) {
			bundle.add("page", String.valueOf(page));
		}
		bundle.add("count", "20");
		String url = SERVER_PREFIX + "/user/api/favoriteList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			Log.e("favoriteList", reString);
			return new Favorite(reString);
		}
		return null;
	}

	/**
	 * 鍒犻櫎鏀惰棌(/user/api/deleteFavorite)
	 * 
	 * @param uid
	 *            true 鐧婚檰鐢ㄦ埛id
	 * @parem favoriteid true int 鏀惰棌璁板綍鐨刬d
	 */
	public ResearchJiaState canclefavMoving(int favoriteid)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (favoriteid == 0) {
			return null;
		}
		bundle.add("favoriteid", String.valueOf(favoriteid));

		String url = SERVER_PREFIX + "/user/api/deleteFavorite";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			Log.e("favoreiteMoving", reString);
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 鍒涘缓缁�
	 * 
	 * @param name
	 *            缁勫悕
	 * @return
	 * @throws ResearchException
	 */
	public AddGroup AddGroup(String name) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("teamName", name);
		bundle.add("action", "addTeam");
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "friend/Index/action";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new AddGroup(reString);
		}

		return null;
	}

	/**
	 * 妫�娴嬫洿鏂� /version/api/update
	 * 
	 * @param version
	 *            鐗堟湰鍙�
	 * @return
	 * @throws ResearchException
	 */
	public VersionInfo checkUpgrade(String version) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		if (version == null || version.equals("")) {
			return null;
		}
		bundle.add("os", "android");
		bundle.add("version", version.substring(1));
		String url = SERVER_PREFIX + "/version/api/update";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new VersionInfo(reString);
		}

		return null;
	}

	/**
	 * /session/api/add 鈶犮��1. 鍒涘缓涓存椂浼氳瘽骞舵坊鍔犵敤鎴�
	 * 
	 * @param name
	 *            true 浼氳瘽鍚嶇О
	 * @param uids
	 *            true 鎵�閭�璇风敤鎴稩D涓� 鏍煎紡锛歩d1,id2,id3,id4
	 * @return
	 * @throws ResearchException
	 */
	public Room createRoom(String groupname, String uids)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		Log.e("createRoom", "groupName:" + groupname);
		bundle.add("name", groupname);
		bundle.add("uids", uids);
		String url = SERVER_PREFIX + "/session/api/add";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new Room(reString);
		}

		return null;
	}

	/**
	 * 鈶°�� 娣诲姞鐢ㄦ埛鍒颁竴涓細璇�(/session/api/addUserToSession)
	 * 
	 * @param groupid
	 *            true int 缇ょ粍id
	 * @param inviteduids
	 *            true string 鍙傛暟鏍煎紡: uid1,uid2,uid3
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState inviteUsers(String sessionid, String uids)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		bundle.add("uids", uids);
		String url = SERVER_PREFIX + "/session/api/addUserToSession";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 鎵竴鎵�/session/api/join
	 * 
	 * @param sessionid
	 *            true int 缇ょ粍id
	 * @return
	 * @throws ResearchException
	 */
	public Room join(String sessionid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		String url = SERVER_PREFIX + "/session/api/join";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new Room(reString);
		}

		return null;
	}

	/**
	 * 鈶€��鎶婄敤鎴蜂粠鏌愪釜缇よ涪鍑�(/session/api/remove)
	 * 
	 * @param groupid
	 *            鎴块棿ID
	 * @param fuid
	 *            琚涪鐢ㄦ埛ID
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState kickParticipant(String sessionid, String fuid)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", String.valueOf(sessionid));
		bundle.add("fuid", fuid);
		String url = SERVER_PREFIX + "/session/api/remove";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 鑾峰彇鏌愪釜鐢ㄦ埛鐨勬墍鍦ㄧ殑缇わ紙鎴块棿鍒楄〃锛� /session/api/userSessionList
	 * 
	 * @param fuid
	 *            false String 涓嶄紶鍒欐煡鐪嬭嚜宸辩殑銆備紶浜嗗垯鏌ョ湅鍒汉鐨�
	 * @return
	 * @throws ResearchException
	 */
	public RoomList getRoomList(String fuid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/session/api/userSessionList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new RoomList(reString);
		}

		return null;
	}

	/**
	 * 鑾峰彇鏌愪釜鎴块棿鐨勭敤鎴峰垪琛�(/api/group/getGroupUserList)
	 * 
	 * @param groupid
	 *            鎴块棿ID
	 * @return
	 * @throws ResearchException
	 */
	public RoomUsrList getRoomUserList(String groupid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("groupid", groupid);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/api/group/getGroupUserList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new RoomUsrList(reString);
		}

		return null;
	}

	/**
	 * 鈶ｃ��鍒犻櫎缇�(/session/api/delete)
	 * 
	 * @param sessionid
	 *            缇ょ粍id
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState deleteRoom(String sessionid)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", String.valueOf(sessionid));
		String url = SERVER_PREFIX + "/session/api/delete";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 閫�鍑烘埧闂�(/session/api/quit)
	 * 
	 * @param sessionid
	 *            鎴块棿ID
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState exitRoom(String sessionid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		String url = SERVER_PREFIX + "/session/api/quit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 7.缇ょ粍鑱婂ぉ 鈶ㄣ��淇敼缇よ祫鏂�(/session/api/edit)
	 * 
	 * @param uid
	 *            true string 鐧婚檰鐢ㄦ埛id
	 * @param sessionid
	 *            true int 缇d
	 * @param name
	 *            false string 缇ゅ悕绉�
	 * @param groupnickname
	 *            false string 缇ゆ樀绉�
	 */
	public ResearchJiaState modifyGroupNickName(String sessionid, String name)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		bundle.add("name", name);
		String url = SERVER_PREFIX + "/session/api/edit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}

	/**
	 * 淇敼鎴戠殑缇ゆ樀绉� /session/api/setNickname
	 * 
	 * @param uid
	 *            true string 鐧婚檰鐢ㄦ埛id
	 * @param mynickname
	 *            true string 璁剧疆鐨勭兢鏄电О
	 * @param sessionid
	 *            true int 缇ょ粍id
	 */
	public ResearchJiaState modifyMyNickName(String sessionid,
			String groupnickname) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		bundle.add("mynickname", groupnickname);
		String url = SERVER_PREFIX + "/session/api/setNickname";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}

	/**
	 * 7.缇ょ粍鑱婂ぉ 鈶┿��璁剧疆缇ょ被鍨�(/api/group/ispublic)
	 * 
	 * @param uid
	 *            true string 鐧婚檰鐢ㄦ埛id
	 * @param groupid
	 *            true int 缇d
	 * @param ispublic
	 *            true int 0-鍏紑缇� 1-绉佸瘑缇�
	 * 
	 */
	public ResearchJiaState isPublicGroup(String groupid, int ispublic)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("groupid", groupid);
		bundle.add("ispublic", String.valueOf(ispublic));
		String url = SERVER_PREFIX + "/api/group/ispublic";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 7.缇ょ粍鑱婂ぉ 11銆�璁剧疆鏄惁鎺ユ敹娑堟伅(/session/api/getmsg)
	 * 
	 * @param uid
	 *            true string 鐧婚檰鐢ㄦ埛id
	 * @param groupid
	 *            true int 缇d
	 * @param isgetmsg
	 *            true int 0-涓嶆帴鏀� 1-鎺ユ敹
	 * 
	 */
	public ResearchJiaState isGetGroupMsg(String groupid, int isgetmsg)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", groupid);
		bundle.add("isgetmsg", String.valueOf(isgetmsg));
		String url = SERVER_PREFIX + "/session/api/getmsg";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 17. 璁剧疆鏄惁鎺ユ敹鍙︿竴鐢ㄦ埛鐨勬秷鎭�(/user/api/setGetmsg)
	 * 
	 * @param fuid
	 *            true int 鐢ㄦ埛id
	 */
	public ResearchJiaState setMsg(String fuid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		String url = SERVER_PREFIX + "/user/api/setGetmsg";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 缇よ亰 4. 浼氳瘽璇︾粏(/session/api/detail)
	 * 
	 * @param uid
	 *            true string 鐧婚檰鐢ㄦ埛id
	 * @param groupid
	 *            true int 缇d
	 */
	public Room getRommInfoById(String sessionid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		String url = SERVER_PREFIX + "/session/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new Room(reString);
		}

		return null;
	}

	// 鍏�佹垜
	// 1.鑾峰彇鐪佸競 琛屼笟 璇剧▼
	/**
	 * 鈶犮��鐪佸競(/user/apiother/areaList)
	 * 
	 * @param uid
	 * @throws ResearchException
	 */
	public CountryList getCityAndContryUser() throws ResearchException {
		String reString = FeatureFunction.getAssestsFile("AreaCode");
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new CountryList(reString);
		}
		return null;
	}

	/**
	 * 
	 * 6 淇敼澶囨敞鍚�(/user/api/remark )
	 * 
	 * @param fuid
	 * @param remark
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState remarkFriend(String fuid, String remark)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		bundle.add("remark", remark);
		String url = SERVER_PREFIX + "/user/api/remark";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	// 5. 鏈嬪弸鍦�
	/**
	 * 璁剧疆鐢ㄦ埛灏侀潰鍥� post鏂瑰紡璇锋眰 /friend/api/setCover
	 * 
	 * @param uid
	 *            true string 褰撳墠鐧婚檰鐢ㄦ埛ID
	 * @param action
	 *            true frontCover
	 * @param f_upload
	 *            true 涓婁紶鍥剧墖
	 * @throws QiyueException
	 */
	public ResearchJiaState uploadUserBg(String userID,
			List<MorePicture> listpic) throws ResearchException {
		ResearchJiaState status = null;
		ResearchParameters bundle = new ResearchParameters();
		if (listpic != null && listpic.size() > 0) {
			bundle.addPicture("pic", listpic);
		}
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/friend/api/setCover";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				JSONObject jsonObj = new JSONObject(reString);
				if (jsonObj != null && !jsonObj.equals("")
						&& !jsonObj.equals("null")) {
					status = new ResearchJiaState(jsonObj);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// return null;
			}
		}
		return status;
	}

	/**
	 * 1.鍙戝竷鍒嗕韩(/friend/api/add)
	 * 
	 * @param uid
	 *            true 鐧婚檰鐢ㄦ埛id
	 * @param picList
	 *            涓婁紶鍥剧墖 false string 鏈�澶氫笂浼�6寮狅紝鍛藉悕picture1 picture2.....
	 * @param content
	 *            true string 鍒嗕韩鏂囧瓧鍐呭
	 * @param lng
	 *            false string 缁忓害
	 * @param lat
	 *            false string 绾害
	 * @param address
	 *            false string 缁忕含搴︽墍鍦ㄧ殑鍦板潃
	 * @param visible
	 *            false string 涓嶄紶琛ㄧず鏄叕寮�鐨勶紝浼犲叆鏍煎紡锛歩d1,id2,id3
	 */
	public ResearchJiaState addShare(List<MorePicture> picList, String content,
			String lng, String lat, String address, String visible)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if ((picList == null || picList.size() <= 0)
				&& (content == null || content.equals(""))) {
			return null;
		}

		if (picList != null && picList.size() > 0) {
			bundle.addPicture("pic", picList);
		}

		if (content != null && !content.equals("")) {
			bundle.add("content", content);
		}

		if (lng != null && !lng.equals("")) {
			bundle.add("lng", lng);
		}

		if (lat != null && !lat.equals("")) {
			bundle.add("lat", lat);
		}

		if (address != null && !address.equals("")) {
			bundle.add("address", address);
		}

		if (visible != null && !visible.equals("") && !visible.startsWith(",")) {
			bundle.add("visible", visible);
		}

		String url = SERVER_PREFIX + "/friend/api/add";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}

	/**
	 * 2. 鍒犻櫎鍒嗕韩(/friend/api/delete)
	 * 
	 * @param uid
	 *            string true 鐧婚檰鐢ㄦ埛id
	 * @param fsid
	 *            int true 鍒嗕韩id
	 */
	public ResearchJiaState deleteShare(int fsid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (fsid == 0) {
			return null;
		}

		bundle.add("fsid", String.valueOf(fsid));

		String url = SERVER_PREFIX + "/friend/api/delete";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {// //
				return new ResearchJiaState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 3.鍒嗕韩璇︾粏(/friend/api/detail)
	 * 
	 * @param uid
	 *            string true 鐧婚檰鐢ㄦ埛id
	 * @param fsid
	 *            int true 鍒嗕韩id
	 */
	public FriendsLoopItem shareDetail(int fsid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (fsid == 0) {
			return null;
		}

		bundle.add("fsid", String.valueOf(fsid));

		String url = SERVER_PREFIX + "/friend/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new FriendsLoopItem(reString);

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 4. 鏈嬪弸鍦堝垪琛�(/friend/api/shareList)
	 * 
	 * @param uid
	 *            true 鐧诲綍鐢ㄦ埛id
	 * @param page
	 *            int 璇锋眰鐨勯〉鏁�
	 */
	public FriendsLoop shareList(int page) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);

		if (page != 0) {
			bundle.add("page", String.valueOf(page));
		}
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/friend/api/shareList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			return new FriendsLoop(reString);
		}
		return null;
	}

	/**
	 * 5.鏈嬪弸鐩稿唽(/friend/api/userAlbum) fuid
	 */
	public FriendsLoop myHomeList(int page, String fuid)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		if (fuid != null
				&& !fuid.equals(ResearchCommon.getUserId(BMapApiApp
						.getInstance()))) {
			bundle.add("fuid", fuid);
		}
		if (page != 0) {
			bundle.add("page", String.valueOf(page));
		}
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/friend/api/userAlbum";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			return new FriendsLoop(reString);
		}
		return null;
	}

	/**
	 * 6. 娣诲姞 鍙栨秷璧�(/friend/api/sharePraise)
	 * 
	 * @param uid
	 *            true 鐧婚檰鐢ㄦ埛id
	 * @param fsid
	 *            true int 鍒嗕韩id
	 */
	public ResearchJiaState sharePraise(int fsid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (fsid == 0) {
			return null;
		}

		bundle.add("fsid", String.valueOf(fsid));

		String url = SERVER_PREFIX + "/friend/api/sharePraise";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 7.鍥炲(/friend/api/shareReply)
	 * 
	 * @param uid
	 *            true 鐧婚檰鐢ㄦ埛id
	 * @param fsid
	 *            true int 鍒嗕韩id
	 * @param fuid
	 *            true int 鍥炲鍝釜浜�
	 */
	public ResearchJiaState shareReply(int fsid, String toUid, String content)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (fsid == 0 || (toUid == null || toUid.equals(""))) {
			return null;
		}

		bundle.add("content", content);
		bundle.add("fsid", String.valueOf(fsid));
		bundle.add("fuid", toUid);

		String url = SERVER_PREFIX + "/friend/api/shareReply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 8. 鍒犻櫎鍥炲(/friend/api/deleteReply)
	 * 
	 * @param uid
	 *            true 鐧婚檰鐢ㄦ埛id
	 * @param replyid
	 *            true int 鏌愭潯鍥炲鐨刬d
	 */
	public ResearchJiaState deleteReply(int replyid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (replyid == 0) {
			return null;
		}

		bundle.add("replyid", String.valueOf(replyid));

		String url = SERVER_PREFIX + "/friend/api/deleteReply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 9. 璁剧疆鏈嬪弸鍦堟潈闄�(/friend/api/setFriendCircleAuth)
	 * 
	 * @param uid
	 *            true 鐧婚檰鐢ㄦ埛id
	 * @param fuid
	 *            true 瑕佽缃殑鐢ㄦ埛id
	 * @param type
	 *            true int 1-涓嶇湅浠栵紙濂癸級鐨勬湅鍙嬪湀 2-涓嶈浠栵紙濂癸級鐪嬫垜鐨勬湅鍙嬪湀
	 */
	public ResearchJiaState setFriendCircleAuth(int type, String fuid)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (type == 0 || (fuid == null || fuid.equals(""))) {
			return null;
		}

		bundle.add("type", String.valueOf(type));
		bundle.add("fuid", fuid);

		String url = SERVER_PREFIX + "/friend/api/setFriendCircleAuth";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	// 7.璁剧疆
	/**
	 * 
	 * 鎰忚鍙嶉 /user/api/feedback
	 * 
	 * @param content
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState feedback(String content) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("content", content);
		String url = SERVER_PREFIX + "/user/api/feedback";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				JSONObject json = new JSONObject(reString);
				if (!json.isNull("state")) {
					return new ResearchJiaState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 7.1 淇敼瀵嗙爜(/user/api/editPassword)
	 * 
	 * @param uid
	 *            true string 鐧婚檰鐢ㄦ埛id
	 * @param oldpassword
	 *            true string 鏃у瘑鐮�
	 * @param newpassword
	 *            true string 鏂板瘑鐮�
	 */
	public ResearchJiaState editPasswd(String oldpassword, String newpassword)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		if ((oldpassword == null || oldpassword.equals(""))
				|| (newpassword == null || newpassword.equals(""))) {
			return null;
		}
		bundle.add("oldpassword", oldpassword);
		bundle.add("newpassword", newpassword);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/editPassword";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 7.2 甯姪涓績(/user/apiother/help) 杩斿洖鐨勬槸涓�涓猦tml鐨勯〉闈�
	 * 
	 * @throws ResearchException
	 */
	public String getHelpHtml() throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		String url = SERVER_PREFIX + "/user/apiother/help";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return reString;
		}
		return null;
	}

	/**
	 * 鏍规嵁濮撳悕鑾峰彇鐢ㄦ埛璇︾粏(/api/user/getUserByName)
	 * 
	 * @param uid
	 *            鐧婚檰鐢ㄦ埛id
	 * @param name
	 *            true string 鐢ㄦ埛濮撳悕
	 */
	public LoginResult getUserByName(String name) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		if (name == null || name.equals("")) {
			return null;
		}
		bundle.add("name", name);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/api/user/getUserByName";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			return new LoginResult(reString.trim());
		}

		return null;

	}

	/**
	 * 璁剧疆鍔犲ソ鍙嬫槸鍚﹂渶瑕侀獙璇�(/user/api/setVerify) verify int true 0-涓嶉獙璇� 1-楠岃瘉
	 * 
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState setVerify(int verify) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/setVerify";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}

	/**
	 * 鍙戦�佹秷鎭帴鍙�
	 * 
	 * @param messageInfo
	 * @return
	 * @throws BridgeException
	 *             http://117.78.2.70/index.php/user/api/sendMessage?uid=200000
	 *             &content=鍏卞拰鍥戒箹涔�&fromurl=http://117.78.2.70/Uploads/Picture/
	 *             avatar/200000/s_f9f0399347f63dc71c8880d057403f97.jpg
	 *             &voicetime=0&tag=0814a62b-5cf3-432d-a878-3da9c99af257
	 *             &fromid=
	 *             200000&fromname=钀岃悓鍝�&typechat=200&toname=灏忛粍楦�,娴锋磱澶╁爞
	 *             ,婕╂丁楦ｄ汉&typefile=1&toid=17
	 */
	public MessageResult sendMessage(MessageInfo messageInfo, boolean isForward)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		if (messageInfo == null) {
			return null;
		}
		bundle.add("typechat", String.valueOf(messageInfo.typechat));
		bundle.add("tag", messageInfo.tag);
		if (!TextUtils.isEmpty(messageInfo.fromname)) {
			bundle.add("fromname", messageInfo.fromname);
		}
		if (!TextUtils.isEmpty(messageInfo.fromid)) {
			bundle.add("fromid", messageInfo.fromid);
		}

		if (!TextUtils.isEmpty(messageInfo.fromurl)) {
			bundle.add("fromurl", messageInfo.fromurl);
		}
		bundle.add("toid", messageInfo.toid);
		if (!TextUtils.isEmpty(messageInfo.toname)) {
			bundle.add("toname", messageInfo.toname);
		}

		if (!TextUtils.isEmpty(messageInfo.tourl)) {
			bundle.add("tourl", messageInfo.tourl);
		}
		bundle.add("typefile", String.valueOf(messageInfo.typefile));

		if (!TextUtils.isEmpty(messageInfo.content)) {
			bundle.add("content", messageInfo.content);
		}

		if (messageInfo.typefile == MessageType.PICTURE) {

			if (isForward && !TextUtils.isEmpty(messageInfo.imageString)) {
				bundle.add("image", messageInfo.imageString);
			} else {
				if (!TextUtils.isEmpty(messageInfo.imgUrlS)) {
					List<MorePicture> fileList = new ArrayList<MorePicture>();
					fileList.add(new MorePicture("file_upload",
							messageInfo.imgUrlS));
					bundle.addPicture("pic", fileList);
				}
			}
			if (messageInfo.imgWidth != 0) {
				bundle.add("width", String.valueOf(messageInfo.imgWidth));
			}

			if (messageInfo.imgHeight != 0) {
				bundle.add("height", String.valueOf(messageInfo.imgHeight));
			}

		} else if (messageInfo.typefile == MessageType.VOICE) {
			if (isForward && !TextUtils.isEmpty(messageInfo.voiceString)) {
				bundle.add("voice", messageInfo.voiceString);
			} else if (!TextUtils.isEmpty(messageInfo.voiceUrl)) {
				List<MorePicture> fileList = new ArrayList<MorePicture>();
				fileList.add(new MorePicture("file_upload",
						messageInfo.voiceUrl));
				bundle.addPicture("pic", fileList);

			}
		}

		if (messageInfo.mLat != 0) {
			bundle.add("lat", String.valueOf(messageInfo.mLat));
		}

		if (messageInfo.mLng != 0) {
			bundle.add("lng", String.valueOf(messageInfo.mLng));
		}

		if (!TextUtils.isEmpty(messageInfo.mAddress)) {
			bundle.add("address", messageInfo.mAddress);
		}

		bundle.add("voicetime", String.valueOf(messageInfo.voicetime));
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/sendMessage";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		ChatMainActivity.json = reString;
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") && reString.startsWith("{")) {
			return new MessageResult(reString);
		}

		return null;
	}

	// 浼氳
	/**
	 * 1.鍒涘缓浼氳(/meeting/api/add)
	 * 
	 * @param uid
	 *            true String 鐧婚檰鐢ㄦ埛id
	 * @param picture
	 *            false string 涓婁紶logo鍥剧墖
	 * @param name
	 *            true string 浼氳鏍囬
	 * @param content
	 *            true string 浼氳涓婚
	 * @param start
	 *            true int 寮�濮嬫椂闂存埑
	 * @param end
	 *            true int 缁撴潫鏃堕棿鎴�
	 * @throws ResearchException
	 */
	public ResearchJiaState createMetting(String picture, String name,
			String content, long start, long end) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		if (picture != null && !picture.equals("")) {
			List<MorePicture> listPic = new ArrayList<MorePicture>();
			listPic.add(new MorePicture("picture", picture));
			bundle.addPicture("pic", listPic);
		}
		if ((name == null || name.equals(""))
				|| (content == null || content.equals("")) || start == 0
				|| end == 0) {
			return null;
		}
		bundle.add("name", name);
		bundle.add("content", content);
		bundle.add("start", String.valueOf(start));
		bundle.add("end", String.valueOf(end));

		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/meeting/api/add";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}

	/**
	 * 2.浼氳璇︾粏(/meeting/api/detail)
	 * 
	 * @param meetingid
	 *            true string 浼氳id
	 * @throws ResearchException
	 */
	public MeetingItem mettingDetail(int meetingid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (meetingid == 0) {
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		String url = SERVER_PREFIX + "/meeting/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			return new MeetingItem(reString);
		}
		return null;
	}

	/**
	 * 3. 浼氳鍒楄〃(/meeting/api/meetingList)
	 * 
	 * @param uid
	 *            true String 鐧婚檰鐢ㄦ埛id
	 * @param type
	 *            true string type 1-姝ｅ湪杩涜涓� 2-寰�鏈� 3-鎴戠殑
	 * @param page
	 *            int
	 * @throws ResearchException
	 */
	public Meeting meetingList(int type, int page) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (type == 0) {
			return null;
		}
		bundle.add("type", String.valueOf(type));
		bundle.add("page", String.valueOf(page));
		String url = SERVER_PREFIX + "/meeting/api/meetingList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new Meeting(reString);

		}
		return null;
	}

	/**
	 * 4. 鐢宠鍔犲叆浼氳(/meeting/api/apply)
	 * 
	 * @param uid
	 *            true String 鐧婚檰鐢ㄦ埛id
	 * @param meetingid
	 *            true string 浼氳id
	 * @throws ResearchException
	 */
	public ResearchJiaState applyMeeting(int meetingid, String reasion)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		if (meetingid == 0 || (reasion == null || reasion.equals(""))) {
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("content", reasion);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/meeting/api/apply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 5. 鍚屾剰鐢宠鍔犲叆浼氳(/meeting/api/agreeApply) 33
	 * 
	 * @param uid
	 *            true String 鐧婚檰鐢ㄦ埛id
	 * @param meetingid
	 *            true string 浼氳id
	 * @param fuid
	 *            true int 鐢宠鐢ㄦ埛id
	 */
	public ResearchJiaState agreeApplyMeeting(int meetingid, String fuid)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		if (meetingid == 0 || (fuid == null || fuid.equals(""))) {
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("fuid", fuid);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/meeting/api/agreeApply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 6. 涓嶅悓鎰忕敵璇峰姞鍏ヤ細璁�(/meeting/api/disagreeApply) 34
	 * 
	 * @param uid
	 *            true String 鐧婚檰鐢ㄦ埛id
	 * @param meetingid
	 *            true string 浼氳id
	 * @paramfuid true int 鐢宠鐢ㄦ埛id
	 */
	public ResearchJiaState disagreeApplyMeeting(int meetingid, String fuid)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		if (meetingid == 0 || (fuid == null || fuid.equals(""))) {
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("fuid", fuid);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/meeting/api/disagreeApply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 7. 閭�璇峰姞鍏ヤ細璁�(/meeting/api/invite) 34
	 * 
	 * @param uid
	 *            true String 鐧婚檰鐢ㄦ埛id
	 * @param meetingid
	 *            true string 浼氳id
	 * @param uids
	 *            true int 琚個璇风敤鎴穒d
	 */
	public ResearchJiaState inviteMeeting(int meetingid, String uids)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (meetingid == 0
				|| (uids == null || uids.equals("") || uids.startsWith(",") || uids
						.endsWith(","))) {
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("uids", uids);
		String url = SERVER_PREFIX + "/meeting/api/invite";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}

	/**
	 * 10. 浼氳鐨勭敤鎴风敵璇峰垪琛�(/meeting/api/meetingApplyList) 36
	 * 
	 * @param uid
	 *            true String 鐧婚檰鐢ㄦ埛id
	 * @param meetingid
	 *            true string 浼氳id
	 * @throws ResearchException
	 */
	public UserList meetingApplyList(int page, int meetingid)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		if (meetingid == 0) {
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("page", String.valueOf(page));
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/meeting/api/meetingApplyList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			return new UserList(reString, 0);
		}
		return null;
	}

	/**
	 * 11. 鐢ㄦ埛娲昏穬搴︽帓琛�(/meeting/api/huoyue) 37
	 * 
	 * @param uid
	 *            true String 鐧婚檰鐢ㄦ埛id
	 * @param meetingid
	 *            true string 浼氳id
	 */
	public UserList huoyueList(int page, int meetingid)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		if (meetingid == 0) {
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("page", String.valueOf(page));
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/meeting/api/huoyue";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			return new UserList(reString, 0);
		}
		return null;
	}

	/**
	 * 12. 绉婚櫎鐢ㄦ埛(/meeting/api/remove) 37
	 * 
	 * @param uid
	 *            true String 鐧婚檰鐢ㄦ埛id
	 * @param meetingid
	 *            true string 浼氳id
	 * @param fuid
	 *            true int 瑕佺Щ闄ょ殑鐢ㄦ埛
	 */
	public ResearchJiaState removeMetUser(int meetingid, String fuid)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		if (meetingid == 0 || (fuid == null || fuid.equals(""))) {
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("fuid", fuid);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/meeting/api/remove";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null") /* && reString.startsWith("{") */) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 获取校友会列表
	 * 
	 * @return
	 * @throws ResearchException
	 */
	public SchoolMeetingList getSchoolList() throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/School/Api/getList";
		String uid = ResearchCommon.getUserId(BMapApiApp.getInstance());
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new SchoolMeetingList(reString, 1);
		}
		return null;
	}

	/**
	 * 获取分会或群的消息
	 * 
	 * @return
	 * @throws ResearchException
	 *             sid 目标分会或者群id
	 */
	public SchoolMeetingList getDetailInfo(String sid) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sid", sid);
		String url = SERVER_PREFIX + "/school/api/detail";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			return new SchoolMeetingList(reString, 2);
		}
		return null;
	}

	/**
	 * 创建校友会
	 * 
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState createSchoolMeeting(String name, String pid,
			String type) throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("name", name);
		bundle.add("pid", pid);
		bundle.add("type", type);
		String url = SERVER_PREFIX + "/school/api/add";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 交友墙，上传交友信息 friend/api/addSocially
	 * 
	 * @return
	 * @throws ResearchException
	 */
	public ResearchJiaState uploadMakeFriendMessage(String brief,
			String image_urls, String require_condition, int click_good_count)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("brief", brief); // 交友宣言
		bundle.add("image_urls", image_urls); // 图片
		bundle.add("require_condition", require_condition); // 交友条件
		bundle.add("click_good_count", click_good_count + ""); // 点赞数
		String url = SERVER_PREFIX + "/friend/api/addSocially";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			try {
				return new ResearchJiaState(new JSONObject(reString));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 交友墙，获取墙上数据
	 * 
	 * @return
	 * @throws ResearchException
	 */
	public UserList getMKFriendDateList(String page, String rows, String type)
			throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		Log.i("dzf", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("page", page);
		bundle.add("rows", rows);
		bundle.add("type", type);

		String url = SERVER_PREFIX + "/friend/api/friendWall";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			// Log.e("reString", reString);

			return new UserList(reString);
		}
		return null;
	}

	/**
	 * 相亲墙，获取墙上数据
	 * 
	 * @return
	 * @throws ResearchException
	 */
	public UserList getBlindDateList() throws ResearchException {
		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		Log.i("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/getList";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			// Log.e("reString", reString);

			return new UserList(reString, 0);
		}
		return null;
	}

	/**
	 * 查看附近人，上传经纬度
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws ResearchException
	 */
	public UserList getgetNearbyUser(String latitude, String longitude,String page, String rows)
			throws ResearchException {

		ResearchParameters bundle = new ResearchParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("latitude", latitude);
		bundle.add("longitude", longitude);
		bundle.add("page", page);
		bundle.add("rows", rows);
		
		String url = SERVER_PREFIX + "/user/api/getNearbyUser";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		Log.i("uid", ResearchCommon.getUserId(BMapApiApp.getInstance()));
		if (reString != null && !reString.equals("")
				&& !reString.equals("null")) {
			// Log.e("reString", reString);

			return new UserList(reString, 0);
		}
		return null;
	}

}
