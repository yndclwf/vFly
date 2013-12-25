package com.ryantang.pulllistview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.ryantang.pulllistview.RTPullListView.OnRefreshListener;

/**
 * PullListView
 * @author Ryan
 *
 */
public class RTPullListViewActivity extends Activity {
	private static final int INTERNET_FAILURE = -1;
	private static final int LOAD_SUCCESS = 1;
	private static final int LOAD_MORE_SUCCESS = 3;
	private static final int NO_MORE_INFO = 4;
	private static final int LOAD_NEW_INFO = 5;
	
	private RTPullListView pullListView;
	private ProgressBar moreProgressBar;
	
	private List<String> dataList;
	private ArrayAdapter<String> adapter;
	private int pullNum=0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        pullListView = (RTPullListView) this.findViewById(R.id.pullListView);
        dataList=new LinkedList<String>();
      //  dataList = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			dataList.add("Item data "+i);
		}
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		pullListView.setAdapter(adapter);
		
		//添加listview底部获取更多按钮（可自定义）
        LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.list_footview, null);
		RelativeLayout footerView =(RelativeLayout) view.findViewById(R.id.list_footview);
		moreProgressBar = (ProgressBar) view.findViewById(R.id.footer_progress);
		pullListView.addFooterView(footerView);
		
		//下拉刷新监听器
		pullListView.setonRefreshListener(new OnRefreshListener() {			
			@Override
			public void onRefresh() {
				Log.v("onRefresh", "0");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Thread.sleep(1000);
							//dataList.clear();
							List newList=new ArrayList<String>();
							for (int i = 0; i < 10; i++) {
								newList.add(pullNum+"Item data "+i);
							}
							dataList.addAll(0, newList);
							pullNum++;
							myHandler.sendEmptyMessage(LOAD_NEW_INFO);Log.v("onRefresh", "4");
						} catch (InterruptedException e) {
							Log.v("InterruptedException", e.toString());
							e.printStackTrace();
						}
					}
				}).start();
			}

			@Override
			public void onFootRefresh() {
				Log.v("onFootRefresh","onFootRefresh");
				footRefresh();
			}
		});
		
		//获取跟多监听器
		footerView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				footRefresh();
			}
		});
    }
    //加载更多
   private void footRefresh(){
	   moreProgressBar.setVisibility(View.VISIBLE);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					for (int i = 0; i < 10; i++) {
						dataList.add("New item data "+i);
					}
					myHandler.sendEmptyMessage(LOAD_MORE_SUCCESS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
   }
    //结果处理
    private Handler myHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_MORE_SUCCESS:	
				moreProgressBar.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				pullListView.setSelectionfoot();
				break;

			case LOAD_NEW_INFO:
				//Log.v("Handler", "2");
				adapter.notifyDataSetChanged();
				pullListView.onRefreshComplete();
				break;
			default:
				break;
			}
		}
    	
    };
}