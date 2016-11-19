package com.research.adapter;

import java.util.ArrayList;
import java.util.List;

import com.research.Entity.Login;
import com.research.fragment.GridFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SimplePageAdapter extends FragmentStatePagerAdapter {

	private List<Fragment> fragments;

	public SimplePageAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int arg0) {
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}
	
	public void addFragment(int i,ArrayList<Login> modelList,int lastIndex){
		GridFragment  gf=GridFragment.newInstance(i,modelList,lastIndex);
		fragments.add(gf);
	}
	
	
	

}
