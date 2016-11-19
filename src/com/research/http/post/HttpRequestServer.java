package com.research.http.post;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.research.R;
import com.research.global.GlobalParam;
import com.research.net.ResearchInfo;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.widget.Toast;

public class HttpRequestServer {
	
    //private static String url = "http://192.168.1.102:8080/vipManager/";//局域网地址
    private static final String URL = "http://"+ResearchInfo.IP+":1010/vipManager/";//服务器地址 118.193.195.216:1010
    private static final int REQUEST_TIMEOUT = 1 * 1000;// 设置请求超时10秒钟 
    private static final int SO_TIMEOUT = 5 * 1000; // 设置等待数据超时时间10秒钟 
    private static final int type=-1;//请求Type 
    public static List chatList=new ArrayList();
    public static List addList=new ArrayList();
    public static Object obj;
    
    public HttpRequestServer() {
    	
    }

    /**
     * 向服务器发送servlet请求
     * @return
     */
    public static Object toGetUserInfo(String uid){
    	 String path=URL+"servlet/getUserInfo";//声明服务器的地址和servlet  
         //将用户名和密码放入HashMap中  
         Map<String,String> params=new HashMap<String,String>();  
         params.put("id", uid);//编辑要发送的数据  
         try {  
             return sendGETRequest(path,params,"UTF-8");  
         } catch (Exception e) {  
             // TODO Auto-generated catch block  
             return e.getMessage();  
         }  
    }
    
    /**
     * 向服务器发送servlet请求 , 用户注册登录后赠送积分
     * @return
     */
    public static Object toGetRegister(String phone){
    	 String path=URL+"servlet/RegIntgral";//声明服务器的地址和servlet  
         //将用户名和密码放入HashMap中  
         Map<String,String> params=new HashMap<String,String>();  
         params.put("phone", phone);//编辑要发送的数据  
         try {  
             return sendGETRequest(path,params,"UTF-8");
         } catch (Exception e) {  
             // TODO Auto-generated catch block  
             return e.getMessage();
         }  
    }
    
    /**
     * 向服务器发送servlet请求,获取vip数据
     * @return
     */
    public static Object toGetVipList(){
    	 String path=URL+"servlet/getVipListServlet";//声明服务器的地址和servlet  
         //将用户名和密码放入HashMap中  
         Map<String,String> params=new HashMap<String,String>();  
         try {  
             return sendGETRequest(path,params,"UTF-8");  
         } catch (Exception e) {  
             // TODO Auto-generated catch block  
             return e.getMessage();  
         }  
    }

    /**
     * 向服务器发送servlet请求,获取完善资料赠送的积分
     * @return
     */
    public static Object toGetComplete(String id){
    	 String path=URL+"servlet/CompleteIntegral";//声明服务器的地址和servlet  
         Map<String,String> params=new HashMap<String,String>();  
         params.put("id", id);//存入用户的id
         try {  
             return sendGETRequest(path,params,"UTF-8");  
         } catch (Exception e) {  
             // TODO Auto-generated catch block  
             return e.getMessage();  
         }  
    }

    /**
     * 向服务器发送servlet请求,获取当天登录赠送的积分
     * @return
     */
    public static Object toGetTodayLogin(String id){
    	 String path=URL+"servlet/TodayLogin";//声明服务器的地址和servlet  
         Map<String,String> params=new HashMap<String,String>();  
         params.put("id", id);//存入用户的id
         try {  
             return sendGETRequest(path,params,"UTF-8");  
         } catch (Exception e) {  
             // TODO Auto-generated catch block  
             return e.getMessage();  
         }  
    }

    /**
     * 向服务器发送servlet请求,获取用户的总积分
     * @return
     */
    public static Object toGetUserIntegral(String id){
    	 String path=URL+"servlet/UserIntegral";//声明服务器的地址和servlet  
         Map<String,String> params=new HashMap<String,String>();  
         params.put("id", id);//存入用户的id
         try {  
             return sendGETRequest(path,params,"UTF-8");  
         } catch (Exception e) {  
             // TODO Auto-generated catch block  
             return e.getMessage();  
         }  
    }

    /**
     * 向服务器发送servlet请求,获取广告图片的地址
     * @return
     */
    public static Object toGetAddPath(){
    	 String path="http://"+ResearchInfo.IP+ResearchInfo.PORT+"/lediao/index.php/home/index/ad";//声明服务器的地址和servlet  
         Map<String,String> params=new HashMap<String,String>();
         try {  
             return sendGETRequest(path,params,"UTF-8");  
         } catch (Exception e) {  
             // TODO Auto-generated catch block  
             return e.getMessage();  
         }  
    }
 
    /**
     * 向服务器发送servlet请求,获取当天聊天赠送的积分
     * @return
     */
    public static Object toGetTodayChat(String send_id,String re_id){
    	 String path=URL+"servlet/TodayChat";//声明服务器的地址和servlet  
         Map<String,String> params=new HashMap<String,String>();  
         params.put("send_id", send_id);//存入用户的id
         params.put("re_id", re_id);//存入用户的id
         try {  
             return sendGETRequest(path,params,"UTF-8");  
         } catch (Exception e) {  
             // TODO Auto-generated catch block  
             return e.getMessage();  
         }  
    }
    
    /**
     * 向服务器发送servlet请求,获取当天看广告赠送的积分
     * @return
     */
    public static Object toGetTodayAdd(String userid,String addid){
    	 String path=URL+"servlet/TodayAdd";//声明服务器的地址和servlet  
         Map<String,String> params=new HashMap<String,String>();  
         params.put("userid", userid);//存入用户的id
         params.put("addid", addid);//存入广告的id
         try {  
             return sendGETRequest(path,params,"UTF-8");  
         } catch (Exception e) {  
             // TODO Auto-generated catch block  
             return e.getMessage();  
         }  
    }
   
    /*public static Object toRegister(String phone,String id){
    	Object o=null;
    	String path=serverCategoryStr+"servlet/getVipListServlet";//声明服务器的地址和servlet  
        //将用户名和密码放入HashMap中  
        Map<String,String> params=new HashMap<String,String>();  
        params.put("mess", "username");  
        params.put("passWord", "username");//编辑要发送的数据  
        try {  
            o= sendGETRequest(path,params,"UTF-8");  
            
        } catch (Exception e) {  
        	o = e.getMessage();
        }  
    	return o;
    }*/
    
    /**
     * 请求服务器的servlet并获取数据
     * @param path  服务器的路径，和指定的响应地址
     * @param params 向服务器发送的数据
     * @param encode 
     * @return
     */
	private static Object sendGETRequest(String path,Map<String, String> params, String encode){
		StringBuilder url = new StringBuilder(path);
		url.append("?");
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				url.append(entry.getKey()).append("=");
				url.append(URLEncoder.encode(entry.getValue(), encode));
				url.append("&");
			}
			// 删掉最后一个&
			url.deleteCharAt(url.length() - 1);
			HttpURLConnection conn = (HttpURLConnection) new URL(url.toString()).openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("POST");
			if (conn.getResponseCode() == 200) {
			    InputStream is=conn.getInputStream();
			    ObjectInputStream ois = new ObjectInputStream(is);
			    Object obj = ois.readObject();
			    while(obj==null){
			    	obj = ois.readObject();
			    }
				return obj;
			}
		} catch (Exception e) {
			return e.getMessage();
		}
		return url;
	}

	/**
	 * 获取json数据,并解析单条数据
	 * @return
	 */
	public static Object getPHPRequest(String URL,Map<String, String> params){

        String result = "haha";
        Map<String, String> map=new HashMap<String, String>();
        try {
        	if(params!=null&&params.size()>0){
	        	StringBuilder url = new StringBuilder(URL);
	    		url.append("?");
	    		for (Map.Entry<String, String> entry : params.entrySet()) {
					url.append(entry.getKey()).append("=");
					url.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
					url.append("&");
				}
				// 删掉最后一个&
				url.deleteCharAt(url.length() - 1);
				URL=url.toString();
        	}
            //请求数据  
            HttpClient hc = new DefaultHttpClient();  
            HttpPost hp = new HttpPost(URL);  
            //请求json报文  
            JSONObject jo = new JSONObject();  
            //resultEdit.setText(jo.toString());  
            hp.setEntity(new StringEntity(jo.toString()));  
            HttpResponse hr = hc.execute(hp);  
            //获取返回json报文  
            if(hr.getStatusLine().getStatusCode() == 200){  
                result = EntityUtils.toString(hr.getEntity());   
            }  
            map=analysisJson(result);
            //关闭连接  
            if (hc != null) {  
                hc.getConnectionManager().shutdown();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        return map;
	}
	
	/**	
	 * 获取json数据,并解析多条记录
	 * @return
	 */
	public static Object getPHPMultiRequest(String URL,Map<String, String> params){

        String result = "haha";
        
        List<Map<String, String>> list=new ArrayList<Map<String,String>>();//创建list集合用来保存解析出来的json
    	Map<String, String> jsonmap=new HashMap<String, String>();//创建map用来保存第一次解析的json
    		StringBuilder sb=new StringBuilder();
    		String jsons="";
        try {
        	if(params!=null&&params.size()>0){
	        	StringBuilder url = new StringBuilder(URL);//创建stringbuilder对象用来创建响应接口地址
	    		url.append("?");
	    		for (Map.Entry<String, String> entry : params.entrySet()) {//添加要发送的数据
					url.append(entry.getKey()).append("=");
					url.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
					url.append("&");
				}
				url.deleteCharAt(url.length() - 1);// 删掉最后一个&
				URL=url.toString();
        	}
            HttpClient hc = new DefaultHttpClient();//请求数据  
            HttpPost hp = new HttpPost(URL);  
            //请求json报文  
            JSONObject jo = new JSONObject();  
            //resultEdit.setText(jo.toString());  
            hp.setEntity(new StringEntity(jo.toString()));  
            HttpResponse hr = hc.execute(hp);  
            //获取返回json报文  
            if(hr.getStatusLine().getStatusCode() == 200){  
                result = EntityUtils.toString(hr.getEntity());   
            }  
            JSONObject jsonObject=new JSONObject(result);//解析字符串
            Iterator iter = jsonObject.keys(); 
            while(iter.hasNext()){ 
                String key = (String) iter.next(); 
                String value = jsonObject.get(key).toString(); 
                jsonmap.put(key, value); 
            }
            jsons=jsonmap.get("data").toString();//获取data中的值，即多条数据记录
           
            result="2";
            sb=new StringBuilder(jsons);//实例化StringBuilder
    		sb.deleteCharAt(0);//去掉第一个字符，即“[”
    		sb.deleteCharAt(sb.length()-1);//去掉最后一个字符，即“]”
            result="3";
    		String[] strs=sb.toString().split("\\}");//拆分成json字符串数组
    		for(int i=0;i<strs.length;i++){//遍历数组
    			StringBuilder sbtemp=new StringBuilder(strs[i]);
    			int index=sbtemp.indexOf(",");//判断json是否多出字符“,”
    			if(index<2&&index>-1){//判断该字符的位置是否在第一位
    				sbtemp.deleteCharAt(0);//去掉“,”
    			}
                result="4";
    			if(sbtemp.indexOf("}")<0){//判断json字符串是否完整
    				sbtemp.append("}");//修正成完整的json字符串
    			}
    			Map<String, String> tempmap=analysisJson(sbtemp.toString());//解析字符串，并返回Map集合
    			list.add(tempmap);//将map集合添加到list集合中
    		}
    		
            //关闭连接  
            if (hc != null) {  
                hc.getConnectionManager().shutdown();  
            }  
        } catch (Exception e) {  
            return e.getMessage()+" "+e.toString()+" "+ result;
            
        }  
        return list;
	}
	
	/**
	 * 解析json字符串
	 * @param json
	 * @return
	 */
	public static Map<String, String> analysisJson(String json){
		Map<String , String> map=new HashMap<String, String>();
        try {
			JSONObject jsonObject=new JSONObject(json);//解析字符串
			Iterator iter = jsonObject.keys(); 
			while(iter.hasNext()){ 
			    String key = (String) iter.next(); 
			    String value = jsonObject.get(key).toString(); 
			    map.put(key, value); 
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return map;
	}
	
}
