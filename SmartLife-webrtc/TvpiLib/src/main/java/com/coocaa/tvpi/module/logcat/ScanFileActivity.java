package com.coocaa.tvpi.module.logcat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coocaa.publib.base.BaseAppletActivity;
import com.coocaa.tvpi.util.permission.PermissionsUtil;
import com.coocaa.tvpilib.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Describe:扫描文件Activity
 * Created by AwenZeng on 2021/03/02
 */
public class ScanFileActivity extends BaseAppletActivity {
    private TextView scanTv;
    private ProgressBar scanProgressbar;
    private RecyclerView mRecylerView;
    private Button startMonitorBtn,stopMonitorBtn;
    private ArrayList<File> scanedFiles = new ArrayList<>();
    /*扫描线程*/
    private Thread scanThread;
    /*定时器  用于定时检测扫描线程的状态*/
    private Timer scanTimer;
    /*检测扫描线程的任务*/
    private TimerTask scanTask;
    private ScanFileAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanfile);
        initData();
        initUI();
        startScan();
        LogcatHelper.getInstance(ScanFileActivity.this).start();
    }


    private void initUI() {
        scanTv = findViewById(R.id.scanTv);
        scanProgressbar = findViewById(R.id.scanProgressbar);
        mRecylerView = findViewById(R.id.recyclerView);
        startMonitorBtn = findViewById(R.id.startMonitorBtn);
        stopMonitorBtn = findViewById(R.id.stopMonitorBtn);
        mRecylerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ScanFileAdapter(this);
        startMonitorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               LogcatHelper.getInstance(ScanFileActivity.this).start();
            }
        });

        stopMonitorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Runtime.getRuntime().exec("logcat -c");
                }catch (Exception e){
                    e.printStackTrace();
                }
                LogcatHelper.getInstance(ScanFileActivity.this).stop();
            }
        });
    }


    /**
     * 开始清理日志
     */
    private void initData(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
       LogcatHelper.getInstance(this);

    }


    /*开始扫描*/
    private void startScan() {
        /*要扫描的文件后缀名*/
        final String endFilter = ".log";
        final File dir = new File(LogcatHelper.LOG_PATH);
        scanThread = new Thread(new Runnable() {
            @Override
            public void run() {
                scanFile(dir, endFilter);
            }
        });
        /*判断扫描是否完成 其实就是个定时任务 时间可以自己设置  每2s获取一下扫描线程的状态  如果线程状态为结束就说明扫描完成*/
        scanTimer = new Timer();
        scanTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("线程状态",scanThread.getState().toString());
                if (scanThread.getState() == Thread.State.TERMINATED) {
                    /*说明扫描线程结束 扫描完成  更新ui*/
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("线程结束","扫描完成");
//                            scanTv.setText("扫描完成，共扫描出" + scanedFiles.size() + "个txt文件");
                            scanTv.setText("扫描完成，历史日志记录列表");
                            mAdapter.setDataList(reverseList(scanedFiles));
                            mRecylerView.setAdapter(mAdapter);
                            scanProgressbar.setVisibility(View.GONE);
                            cancelTask();
                        }
                    });
                }
            }
        };

        scanTimer.schedule(scanTask, 0,1000);
        /*开始扫描*/
        scanThread.start();
    }

    private ArrayList<File> reverseList(ArrayList<File> list){
        Collections.sort(list,Collections.reverseOrder());
        return list;
    }


    /**
     * 扫描文件
     * @param dir
     * @param endFilter
     */
    private void scanFile(File dir, String endFilter) {
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (final File file : files) {
                if (file.getName().toUpperCase().endsWith(endFilter.toUpperCase())) {
                    scanedFiles.add(file);
                }
                if (file.isDirectory()) {//文件是否存在
                    scanFile(file, endFilter); /*递归扫描*/
                }
            }

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTask();
    }

    private void cancelTask() {
        Log.i("cancelTask","结束任务");
        if (scanTask!=null){
            scanTask.cancel();
        }
        if (scanTimer!=null){
            scanTimer.purge();
            scanTimer.cancel();;
        }
    }
}