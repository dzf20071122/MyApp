package com.research.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.research.R;

public class SelectMakeFriendPopupWindow extends PopupWindow {

	private View mMenuView;
	private LinearLayout mSchoolFriend, mCountyFriend, mFujinFriend,
			mEveryFriend;

	public SelectMakeFriendPopupWindow(final Activity context,
			final OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.make_friend_select_xml, null);
		mSchoolFriend = (LinearLayout) mMenuView
				.findViewById(R.id.school_friend_layout);
		mCountyFriend = (LinearLayout) mMenuView
				.findViewById(R.id.county_friend_layout);
		mFujinFriend = (LinearLayout) mMenuView
				.findViewById(R.id.fujin_friend_layout);
		mEveryFriend = (LinearLayout) mMenuView
				.findViewById(R.id.every_friend_layout);

		
		OnClickListener listen = new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemsOnClick.onClick(v);
				dismiss();
			}
		};
		mSchoolFriend.setOnClickListener(listen);
		mCountyFriend.setOnClickListener(listen);
		mFujinFriend.setOnClickListener(listen);
		mEveryFriend.setOnClickListener(listen);

		int h = context.getWindowManager().getDefaultDisplay().getHeight();
		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		// 设置按钮监听
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth((w / 2/* +20 */));
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.mystyle);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		// this.setBackgroundDrawable(dw);
		this.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.no_top_arrow_bg));
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout3).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}
}
