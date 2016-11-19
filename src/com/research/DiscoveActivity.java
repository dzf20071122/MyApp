package com.research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.research.R;
import com.research.Entity.Login;
import com.research.Entity.SchoolMeeting;
import com.research.Entity.SchoolMeetingList;
import com.research.adapter.MakeFriendListAdapter;
import com.research.adapter.SchoolMeetingAdapter;
import com.research.adapter.SchoolMeetingMemberAdapter;
import com.research.global.GlobalParam;
import com.research.global.ResearchCommon;
import com.research.map.BMapApiApp;
import com.research.net.ResearchException;

/**
 * 交友墙界面
 * @author dzf
 *
 */
public class DiscoveActivity extends BaseActivity implements OnClickListener{

	private ViewPager mViewPager;  
    private LinearLayout ll_1,ll_2,ll_3;  
    private GridView view1,view2,view3;  
      
    private List<View> viewList;  //把需要滑动的页卡添加到这个list中    
    private ImageView[] imageViews;   
    private int currentPosition = 0; // 当前位置  
      
    /* 
     * 这里我就偷懒啦、三个切换滑动页面引用的是相同的图标和文字、我就省了去找其他的图片啦  
     * 我们可以定义三组数组分别放每个界面的图标和文字内容、 
     * 然后修改下 下面的 setGridViewAdapter() 方法 就行啦... 
     *  
     */  
    // 定义图标数组  
    private int[] imageRes = {R.drawable.w_photo_icon,R.drawable.w_photo_icon,R.drawable.w_photo_icon,  
            R.drawable.w_photo_icon,R.drawable.w_photo_icon,R.drawable.w_photo_icon,  
            R.drawable.w_photo_icon,R.drawable.w_photo_icon,R.drawable.w_photo_icon};  
    //定义标题数组  
    private String[] itemName = {"微信","微音乐","微魅儿",  
            "微通讯","微短信","微烦皂",  
            "微盟主","微爱心","微互粉", 
            "微盟主","微爱心","微互粉" 
            };
    
	/**
	 * 导入控件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchinfo);
		mContext = this;
		initViews();
	}
	
	public void initViews() {  
		setTitleContent(R.drawable.back_btn,0,R.string.blind_date_title_name);
		mLeftBtn.setOnClickListener(this);
		
        mViewPager = (ViewPager) findViewById(R.id.search_viewpager);  
        LayoutInflater lf = getLayoutInflater().from(this);  
        ll_1 = (LinearLayout) lf.inflate(R.layout.ninebox, null);  
        view1 = (GridView) ll_1.findViewById(R.id.myGridView);  
          
        ll_2 = (LinearLayout) lf.inflate(R.layout.ninebox_2, null);  
        view2 = (GridView) ll_2.findViewById(R.id.myGridView2);  
          
        ll_3 = (LinearLayout) lf.inflate(R.layout.ninebox_3, null);  
        view3 = (GridView) ll_3.findViewById(R.id.myGridView3);  
          
        viewList = new ArrayList<View>();  // 将要分页显示的View装入数组中    
        viewList.add(ll_1);  
        viewList.add(ll_2);  
        viewList.add(ll_3);  
          
        setGridViewAdapter(view1);  
        setGridViewAdapter(view2);  
        setGridViewAdapter(view3);  
          
        mViewPager.setAdapter(new MyPagerAdapter(viewList));  
        mViewPager.setOnPageChangeListener(new myOnPageChangeListener());  
          
        LinearLayout llguid = (LinearLayout) findViewById(R.id.llguid);  
        imageViews = new ImageView[viewList.size()];  
        for(int i= 0;i<viewList.size();i++){  
            imageViews[i] = (ImageView) llguid.getChildAt(i);  
            imageViews[i].setEnabled(true);  
            imageViews[i].setTag(i);  
        }  
        imageViews[currentPosition].setEnabled(false);  
    }
	
	/** 
     *  设置GridView适配器 
     */  
    private void setGridViewAdapter(GridView mGridView) {  
        List<Login> data = new ArrayList<Login>();  
        int length = itemName.length;  
        for(int i=0;i<length;i++){  
            Login ml = ResearchCommon.getLoginResult(mContext);
            data.add(ml);  
        }  
          
//        SimpleAdapter simpleAdapter = new SimpleAdapter(this,   
//                data,   
//                R.layout.ninebox_item,   
//                new String[]{"ItemImageView","ItemTextView"},   
//                new int[]{R.id.ItemImageView,R.id.itemTextView});  
//        mGridView.setAdapter(simpleAdapter);  
        
        MakeFriendListAdapter mkAdapter = new MakeFriendListAdapter(mContext,data);
        mGridView.setAdapter(mkAdapter);
    }  
      
      
    class MyPagerAdapter extends PagerAdapter{  
        List<View> viewList;  
          
        public MyPagerAdapter(List<View> viewList) {  
            this.viewList = viewList;  
        }  
  
        @Override  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return viewList.size();  
        }  
  
        @Override  
        public boolean isViewFromObject(View arg0, Object arg1) {  
            // TODO Auto-generated method stub  
            return arg0 == arg1;  
        }  
  
        @Override  
        public void destroyItem(View container, int position, Object object) {  
            // TODO Auto-generated method stub  
            ((ViewPager)container).removeView(viewList.get(position));  
        }  
  
        @Override  
        public Object instantiateItem(ViewGroup container, int position) {  
            // TODO Auto-generated method stub  
            container.addView(viewList.get(position));  
            // Log.d(ConstantS.TAG, "ViewPager--Position:"+position);  
            return viewList.get(position);  
        }  
    }  
    
    class myOnPageChangeListener implements OnPageChangeListener{  
    	  
        /** 
         *  当滑动状态改变时调用    
         */  
        @Override  
        public void onPageScrollStateChanged(int arg0) {  
            // TODO Auto-generated method stub  
              
        }  
  
        /** 
         * 当当前页面被滑动时调用  
         */  
        @Override  
        public void onPageScrolled(int arg0, float arg1, int arg2) {  
            // TODO Auto-generated method stub  
              
        }  
  
        /** 
         * 当新的页面被选中时调用  
         */  
        @Override  
        public void onPageSelected(int arg0) {  
//            Log.d(ConstantS.TAG, "ViewPager--当前是第几页:"+arg0);  
            for(int i=0;i<imageViews.length;i++){  
                if(i == arg0){  
                    imageViews[arg0].setEnabled(true);  
                }else{  
                    imageViews[i].setEnabled(false);  
                }  
            }  
        }  
    }  
	
	/**
	 * 按钮点击事件
	 */
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.left_btn:
			DiscoveActivity.this.finish();
			break;

		default:
			break;
		}
	}
}
