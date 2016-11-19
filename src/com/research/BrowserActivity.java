package com.research;

import com.research.Entity.Login;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class BrowserActivity extends BaseActivity implements OnClickListener {

	private WebView browser;
	private String url,title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState); //实例化WebView对象  
		mContext = this;
		this.setContentView(R.layout.browser);
		url = getIntent().getSerializableExtra("url").toString();
		title = getIntent().getSerializableExtra("title").toString();
		initCompent();
	}

	private void initCompent(){
		setTitleContent(R.drawable.back_btn,0, title);
		mLeftBtn.setOnClickListener(this);
		browser = (WebView)findViewById(R.id.browser);
		browser.getSettings().setJavaScriptEnabled(true);
		//跳转至拼接好的地址
		browser.loadUrl(url);
		browser.setWebViewClient(new WebViewClient());
		browser.setWebChromeClient(new WebChromeClient());
		browser.getSettings().setSavePassword(false);
		//Toast.makeText(mContext,"进入浏览器",Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			this.finish();
			break;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP
				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			this.finish();
			System.exit(0);
		}
		return super.dispatchKeyEvent(event);
	}
	private class myWebViewClient extends WebViewClient {
		
		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) { 
			view.loadUrl(url); 
			return true;
		 } 

	} 
}
