package com.research.fragment;

import java.util.ArrayList;
import java.util.List;

import com.research.R;
import com.research.Entity.Login;
import com.research.adapter.MakeFriendListAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class GridFragment extends Fragment {

	private View view;
	private GridView gv;
	private int index = -1;
	private FragmentActivity context;
	private List<Login> modelList;
	private TextView no;

	public static GridFragment newInstance(int index, ArrayList<Login> modelList,int lastIndex) {
		GridFragment gf = new GridFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("index", index);
		bundle.putInt("last", lastIndex);
		bundle.putSerializable("user", modelList);
//		bundle.putParcelableArrayList("model", modelList);
		gf.setArguments(bundle);
		return gf;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (view == null) {
			context = getActivity();

			Bundle bundle = getArguments();
			index = bundle.getInt("index");
//			modelList = bundle.getParcelableArrayList("model");
			modelList = (List<Login>) bundle.getSerializable("user");
			List<Login> newModels;
//			int last = 9 * index + 9;
			int beginIndex = 0;
			int endIndex = 0;
			int last = bundle.getInt("last");
			
			if (last == -1){
				last = 9 * index + 9;
				beginIndex = 9 * index;
			}else{
				beginIndex = last;
			}
			if(beginIndex+9 >= modelList.size()){
				endIndex = modelList.size();
			}else{
				endIndex = beginIndex+9;
			}
			
//			if (last >= modelList.size()) {
//				newModels = modelList.subList((9 * index), (modelList.size()));
//
//			} else {
//				newModels = modelList.subList((9 * index), (last));
//			}
			newModels = modelList.subList(beginIndex, endIndex);

			Log.i("tag", "当前页数是" + index + "view是null哦");
			view = LayoutInflater.from(context).inflate(R.layout.fragment_grid,
					container, false);
			gv = (GridView) view.findViewById(R.id.gridview);
			no = (TextView) view.findViewById(R.id.no);
			no.setText("这个是第" + (index + 1) + "页");

			// 这里重新开辟一个地址空间，来保存list，否则会报ConcurrentModificationException错误
			final ArrayList<Login> text = new ArrayList<Login>();
			text.addAll(newModels);
			gv.setAdapter(new MakeFriendListAdapter(context,text));//new GridBaseAdapter(getActivity(), text));

			gv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					Toast.makeText(context,
							"点击的item是" + text.get(position).nickname,
							Toast.LENGTH_SHORT).show();
				}
			});

		} else {
			Log.i("tag", "当前页数是" + index + "view不不不是null哦");
			ViewGroup root = (ViewGroup) view.getParent();
			if (root != null) {
				root.removeView(view);
			}
		}

		Log.i("tag", "当前页数是" + index);

		return view;
	}

}
