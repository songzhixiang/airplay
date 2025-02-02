package com.coocaa.tvpi.module.local;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coocaa.publib.base.BaseAppletActivity;
import com.coocaa.publib.data.local.ImageData;
import com.coocaa.publib.data.local.VideoData;
import com.coocaa.publib.utils.DimensUtils;
import com.coocaa.tvpi.module.local.adapter.AlbumAdapter;
import com.coocaa.tvpi.module.local.adapter.AlbumAdapter2;
import com.coocaa.tvpi.module.local.utils.LocalMediaHelp;
import com.coocaa.tvpi.module.local.utils.MediaStoreHelper;
import com.coocaa.tvpi.module.local.utils.VideoBrowseAsyncTask;
import com.coocaa.tvpi.module.local.view.LocalResStatesView;
import com.coocaa.tvpi.util.StatusBarHelper;
import com.coocaa.tvpi.view.CommonTitleBar;
import com.coocaa.tvpi.view.decoration.PictureItemDecoration;
import com.coocaa.tvpilib.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @ClassName DLNAAlbumActivity
 * @Description TODO (write something)
 * @User WHY
 * @Date 2018/7/24
 * @Version TODO (write something)
 */
public class AlbumActivity2 extends BaseAppletActivity {

    private static final String TAG = AlbumActivity2.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private AlbumAdapter2 mAdapter;

    private List<String> mImageCovers;
    private HashMap<String, ArrayList<ImageData>> mImageMap;
    private List<VideoData> mVideoDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarHelper.translucent(this);
        StatusBarHelper.setStatusBarLightMode(this);
        setContentView(R.layout.local_activity_album);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG); // 统计页面
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG); // 统计页面
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        mImageCovers = LocalMediaHelp.getImageGroup();
        mImageMap = LocalMediaHelp.getImageCacheMap();
        mVideoDataList = LocalMediaHelp.getVideoList();
    }

    private void initView() {
        if(mNPAppletInfo != null) {
            ViewGroup content = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.local_activity_album, null);
            mRecyclerView = content.findViewById(R.id.activity_album_recyclerview);
            content.removeView(mRecyclerView);
            setContentView(mRecyclerView);
        } else {
            mRecyclerView = findViewById(R.id.activity_album_recyclerview);
        }

        LinearLayoutManager layoutManager = new GridLayoutManager(this,3);
        PictureItemDecoration decoration = new PictureItemDecoration(3,
                DimensUtils.dp2Px(this,10),DimensUtils.dp2Px(this,15));
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(decoration);
        mAdapter = new AlbumAdapter2(this, mImageCovers, mImageMap,mVideoDataList);
        mRecyclerView.setAdapter(mAdapter);
        if(mNPAppletInfo != null && mHeaderHandler != null) {
            mHeaderHandler.setHeaderVisible(true);
            mHeaderHandler.setBackButtonVisible(true);
            mHeaderHandler.setTitle("相册");
        } else {
            CommonTitleBar titleBar = findViewById(R.id.titleBar);
            titleBar.setOnClickListener(new CommonTitleBar.OnClickListener() {
                @Override
                public void onClick(CommonTitleBar.ClickPosition position) {
                    if(position == CommonTitleBar.ClickPosition.LEFT){
                        finish();
                    }
                }
            });
        }
    }

}
