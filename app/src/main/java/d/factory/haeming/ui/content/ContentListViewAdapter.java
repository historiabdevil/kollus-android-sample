package d.factory.haeming.ui.content;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import d.factory.haeming.R;
import d.factory.haeming.data.ContentItem;
import d.factory.haeming.data.ContentTypes;
import d.factory.haeming.exception.KollusException;

public class ContentListViewAdapter extends BaseAdapter {
    private ArrayList<ContentItem> contentItemArrayList = new ArrayList<ContentItem>();

    public ContentListViewAdapter() {
    }

    @Override
    public int getCount() {
        return this.contentItemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.contentItemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.live_list_item, parent, false);
            ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();

            layoutParams.height = 100;

            convertView.setLayoutParams(layoutParams);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView poster = (ImageView) convertView.findViewById(R.id.poster);
        TextView liveTitle = (TextView) convertView.findViewById(R.id.title);
        TextView contentType = (TextView) convertView.findViewById(R.id.contenttype);
        TextView drmType = (TextView) convertView.findViewById(R.id.drmtype);
        ImageButton btnPlay = (ImageButton) convertView.findViewById(R.id.btnPlay);
        ImageButton btnDownload = (ImageButton) convertView.findViewById(R.id.btnDownload);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ContentItem contentItem = contentItemArrayList.get(position);

        // 아이템 내 각 위젯에 데이터 반영

        try {

            Glide.with(context).load(contentItem.getPoster()).into(poster);

        } catch (KollusException kex) {
            poster.setImageResource(R.drawable.no_image);
        }
        liveTitle.setText(contentItem.getTitle());
        contentType.setText(contentItem.getContentType().getTypeString());
        contentType.setBackgroundColor(contentItem.getContentType().getBackgroundColor().toArgb());
        drmType.setText(contentItem.getEncryptType().getTypeString());
        contentType.setBackgroundColor(contentItem.getEncryptType().getBackgroundColor().toArgb());
        if(contentItem.getContentType() == ContentTypes.LIVE){
            btnDownload.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void add(@NotNull ContentItem item) throws KollusException {
        if (item.getContentType() == null) {
            throw new KollusException("컨텐츠 종류 미지정");
        }
        if (item.getMediaContentKey() == null || item.getMediaContentKey().isEmpty()) {
            throw new KollusException("미디어컨텐츠키 미지정");
        }
        contentItemArrayList.add(item);
    }
}
