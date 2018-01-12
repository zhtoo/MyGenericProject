package com.zht.getlocalvideolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * 作者：zhanghaitao on 2018/1/12 09:30
 * 邮箱：820159571@qq.com
 *
 * @describe:列表的Adapter
 */

public class VideoListViewAdapter extends BaseAdapter {

    List<Video> listVideos;
    int local_postion = 0;
    boolean imageChage = false;
    private LayoutInflater mLayoutInflater;
    private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();

    public VideoListViewAdapter(Context context, List<Video> listVideos) {
        mLayoutInflater = LayoutInflater.from(context);
        this.listVideos = listVideos;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    public void addPhoto(LoadedImage image) {
        photos.add(image);
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.item_video_list, null);
            holder.img = (ImageView) convertView.findViewById(R.id.video_img);
            holder.title = (TextView) convertView.findViewById(R.id.video_title);
            holder.time = (TextView) convertView.findViewById(R.id.video_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(listVideos.get(position).getDisplayName());
//        long hours = listVideos.get(position).getDuration() / 1000 / 60 / 60;
//        long min = listVideos.get(position).getDuration() / 1000 / 60 % 60;
//        long sec = listVideos.get(position).getDuration() / 1000 % 60;
        //holder.time.setText(hours + ":" + min + " : " + sec);
        holder.time.setText(getTimeString(listVideos.get(position).getDuration()));
        //holder.time.setText(getTimes(listVideos.get(position).getDuration()));
        holder.img.setImageBitmap(photos.get(position).getBitmap());

        return convertView;
    }

    public final class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView time;
    }

    private String getTimes(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+:08:00"));
        String hms = formatter.format(time);
        return hms;
    }


    private String getTimeString(long time) {
        long day = time / 1000 / 60 / 60 / 24;
        long hours = time / 1000 / 60 / 60;
        long min = time / 1000 / 60 % 60;
        long sec = time / 1000 % 60;
        String hms = new String();
        if (day > 0) {hms += day + "天  ";}
        if (hours < 10) {hms += "0";}
        hms += (hours + ":");
        if (min < 10) {hms += "0";}
        hms += (min + ":");
        if (sec < 10) {hms += "0";}
        hms += sec;
        return hms;
    }


}