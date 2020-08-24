package com.se.kollus.ui.bandwidth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kollus.sdk.media.content.BandwidthItem;



import java.util.ArrayList;
import java.util.List;

import com.se.kollus.R;

public class BandwidthListViewAdapter extends BaseAdapter {
    private ArrayList<BandwidthItem> bandwidthItemArrayList = new ArrayList<BandwidthItem>();
    public BandwidthListViewAdapter(){}

    @Override
    public int getCount() {
        return this.bandwidthItemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.bandwidthItemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.bandwidth_list_item, parent, false);
            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();

            layoutParams.height = 100;
            convertView.setLayoutParams(layoutParams);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득

        TextView bandwidthName = (TextView) convertView.findViewById(R.id.bandwidthName) ;
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        BandwidthItem bandwidthItem = bandwidthItemArrayList.get(position);
        bandwidthName.setText(bandwidthItem.getBandwidthName());
        // 아이템 내 각 위젯에 데이터 반영
        return convertView;
    }

    public void add(BandwidthItem item){
        bandwidthItemArrayList.add(item);
    }
    public void addAll(List<BandwidthItem> list){
        bandwidthItemArrayList.addAll(list);
    }
    public void clear(){
        this.bandwidthItemArrayList.clear();
    }
}
