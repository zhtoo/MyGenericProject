package com.zht.getlocalvideolist;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.List;

/**
 * 作者：zhanghaitao on 2018/1/12 09:28
 * 邮箱：820159571@qq.com
 *
 * @describe:
 */

public class VideoListActivity extends Activity {

    public VideoListActivity instance = null;
    ListView mVideoListView;
    VideoListViewAdapter mVideoListViewAdapter;
    List<Video> listVideos;
    int videoSize;
    private RecyclerView mRecycler;
    private VideoRecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video_list);

        LinearLayout mContainer = (LinearLayout) findViewById(R.id.video_container);
        StatusBarUtils.setColor(this, Color.parseColor("#44000000"));
        StatusBarUtils.measureTitleBarHeight(mContainer, this);
        instance = this;
        AbstructProvider provider = new VideoProvider(instance);
        listVideos = provider.getList();
        videoSize = listVideos.size();

        initRecycler();

//        mVideoListViewAdapter = new VideoListViewAdapter(this, listVideos);
//        mVideoListView = (ListView) findViewById(R.id.video_list_file);
//        mVideoListView.setAdapter(mVideoListViewAdapter);
//        mVideoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position,
//                                    long id) {
//                Video video = listVideos.get(position);
//                String path = video.getPath();
//                if (!TextUtils.isEmpty(path)) {
//                    Uri uri = Uri.parse(path);
//                    //调用系统自带的播放器
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    Log.v("URI--------", uri.toString());
//                    intent.setDataAndType(uri, "video/*");
//                    try {
//                        //调用系统的播放器
//                        startActivity(intent);
//                    } catch (Exception e) {
//                        Toast.makeText(VideoListActivity.this, "没有默认的播放器", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
        loadImages();
    }

    private void initRecycler() {
        mRecycler = (RecyclerView) findViewById(R.id.video_recycler);
        GridLayoutManager lm = new GridLayoutManager(this, 2);
        lm.setOrientation(GridLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(lm);
        mAdapter = new VideoRecyclerAdapter(this, listVideos);
        mRecycler.setAdapter(mAdapter);

    }

    public void back(View view) {
        finish();
    }

    /**
     * 加载视频预览图片
     */
    private void loadImages() {
        final Object data = getLastNonConfigurationInstance();
        if (data == null) {
            new LoadImagesFromSDCard().execute();
        } else {
            final LoadedImage[] photos = (LoadedImage[]) data;
            if (photos.length == 0) {
                new LoadImagesFromSDCard().execute();
            }
            for (LoadedImage photo : photos) {
                addImage(photo);
            }
        }
    }

    //添加图片
    private void addImage(LoadedImage... value) {
        for (LoadedImage image : value) {
//            mVideoListViewAdapter.addPhoto(image);
//            mVideoListViewAdapter.notifyDataSetChanged();
            mAdapter.addPhoto(image);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
//        final ListView grid = mVideoListView;
//        final int count = grid.getChildCount();


//
//        final LoadedImage[] list = new LoadedImage[count];
//
//        for (int i = 0; i < count; i++) {
//            final ImageView v = (ImageView) grid.getChildAt(i);
//            list[i] = new LoadedImage(
//                    ((BitmapDrawable) v.getDrawable()).getBitmap());
//        }
//
//        return list;

        return super.onRetainNonConfigurationInstance();
    }

    class LoadImagesFromSDCard extends AsyncTask<Object, LoadedImage, Object> {
        @Override
        protected Object doInBackground(Object... params) {
            Bitmap bitmap = null;
            for (int i = 0; i < videoSize; i++) {
                bitmap = getVideoThumbnail(listVideos.get(i).getPath(), 120, 120, Thumbnails.MINI_KIND);
                if (bitmap != null) {
                    publishProgress(new LoadedImage(bitmap));
                }
            }
            return null;
        }

        /**
         * 获取视频缩略图
         */
        private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
            Bitmap bitmap = null;
            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            return bitmap;
        }

        @Override
        public void onProgressUpdate(LoadedImage... value) {
            addImage(value);
        }

    }
}