package net.muliba.weixinpicturechooser;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.muliba.weixinpicturechooser.bean.FolderBean;
import net.muliba.weixinpicturechooser.util.ListImageDirPopupWindow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private GridView mGridView;

    private RelativeLayout mBottomLy;
    private TextView mDirName;
    private TextView mDirCount;

    private List<String> mImgs ;
    private File mCurrentDir;
    private int mMaxCount;

    private List<FolderBean> folderBeans = new ArrayList<>();

    private ProgressDialog progressDialog;
    private ImageListAdapter adapter ;

    private ListImageDirPopupWindow popupWindow;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x110) {
                progressDialog.dismiss();
                data2View();
                initDirPopupWindow();
            }
        }
    };

    private void initDirPopupWindow() {
        popupWindow = new ListImageDirPopupWindow(this, folderBeans);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                lightOn();
            }
        });
        popupWindow.setOnDirSelectedListener(new ListImageDirPopupWindow.OnDirSelectedListener() {
            @Override
            public void onSelected(FolderBean folderBean) {
                //todo 更新adapter
                mCurrentDir = new File(folderBean.getDir());
                mImgs = Arrays.asList(mCurrentDir.list(filter));
                setGridViewAdapter();
                popupWindow.dismiss();
            }
        });
    }

    /**
     * 关闭popupWindow后内容区域变亮
     */
    private void lightOn() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        getWindow().setAttributes(lp);
    }

    private void lightOff() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.3f;
        getWindow().setAttributes(lp);
    }
    private void data2View() {
        if (mCurrentDir == null) {
            Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
            return;
        }
        mImgs = Arrays.asList(mCurrentDir.list(filter));
        setGridViewAdapter();
    }

    private void setGridViewAdapter() {
        adapter = new ImageListAdapter(this, mImgs, mCurrentDir.getAbsolutePath());
        mGridView.setAdapter(adapter);
        mDirCount.setText(mMaxCount + "");
        mDirName.setText(mCurrentDir.getName());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDatas();
        initEvent();
    }

    private void initEvent() {
        mBottomLy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.setAnimationStyle(R.style.dir_popupwindow_anim);
                popupWindow.showAsDropDown(mBottomLy, 0, 0);
                lightOff();
            }
        });
    }



    /**
     * 利用ContentProvider扫描手机中的所有图片
     */
    private void initDatas() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(getApplicationContext(), "当前存储卡不可用！", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = ProgressDialog.show(this, null, "正在加载...");
        new Thread(){
            @Override
            public void run() {
                Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = MainActivity.this.getContentResolver();
                String select = MediaStore.Images.Media.MIME_TYPE+" = ? or "+MediaStore.Images.Media.MIME_TYPE+" = ?";
                Cursor query = contentResolver.query(uri, null, select, new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
                Set<String> mDirPaths = new HashSet<String>();
                while (query.moveToNext()){
                    String path = query.getString(query.getColumnIndex(MediaStore.Images.Media.DATA));
                    File parent = new File(path).getParentFile();
                    if (parent == null) {
                        continue;
                    }
                    String dirPath = parent.getAbsolutePath();
                    FolderBean bean = null;
                    if (mDirPaths.contains(dirPath)){
                        continue;
                    }else {
                        mDirPaths.add(dirPath);
                        bean = new FolderBean();
                        bean.setDir(dirPath);
                        bean.setFirstImgPath(path);
                    }
                    if (parent.list() == null) {
                        continue;
                    }
                    int picSize = parent.list(filter).length;
                    bean.setCount(picSize);
                    folderBeans.add(bean);
                    if (picSize > mMaxCount) {
                        mMaxCount = picSize;
                        mCurrentDir = parent;
                    }
                }
                query.close();
                //通知 扫描完成
                handler.sendEmptyMessage(0x110);
            }
        }.start();

    }

    private void initView() {
        mGridView = (GridView) findViewById(R.id.id_gridview);
        mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_rl);
        mDirName = (TextView) findViewById(R.id.id_dir_name);
        mDirCount = (TextView) findViewById(R.id.id_dir_count);
    }


    private FilenameFilter filter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String filename) {
            if (filename.endsWith(".jpg") || filename.endsWith(".png") ||
                    filename.endsWith(".jepg")){
                return true;
            }
            return false;
        }
    };

}
