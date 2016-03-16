package net.muliba.weixinpicturechooser.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.muliba.weixinpicturechooser.R;
import net.muliba.weixinpicturechooser.bean.FolderBean;

import java.util.List;

/**
 * Created by FancyLou on 2016/3/16.
 */
public class ListImageDirPopupWindow extends PopupWindow {

    private int width;
    private int height;
    private View mConvertView;
    private ListView mListView;
    private List<FolderBean> mDatas;

    public interface OnDirSelectedListener {
        void onSelected(FolderBean folderBean);
    }


    private OnDirSelectedListener listener;

    public void setOnDirSelectedListener(OnDirSelectedListener listener) {
        this.listener = listener;
    }

    public ListImageDirPopupWindow(Context context, List<FolderBean> mDatas) {
        super(context);
        this.mDatas = mDatas;
        calWidthAndHeight(context);
        mConvertView = LayoutInflater.from(context).inflate(R.layout.popup_main, null);

        setContentView(mConvertView);
        setWidth(width);
        setHeight(height);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initViews(context);
        initEvent();
    }

    private void initViews(Context context) {
        mListView = (ListView) mConvertView.findViewById(R.id.id_dir_list);
        mListView.setAdapter(new ListDirAdapter(context, mDatas));
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listener!=null) {
                    listener.onSelected(mDatas.get(position));
                }
            }
        });

    }

    /**
     * 计算高度和宽度
     * 宽度和屏幕一样
     * 高度是70%的屏幕
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = (int) (metrics.heightPixels * 0.7 );
    }


    private class ListDirAdapter extends ArrayAdapter<FolderBean> {

        private LayoutInflater inflater;

        public ListDirAdapter(Context context, List<FolderBean> objects) {
            super(context, 0, objects);
            inflater = LayoutInflater.from(context);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder =null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_popup, parent, false);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.id_dir_item_image);
                viewHolder.nameTv = (TextView) convertView.findViewById(R.id.id_dir_item_name);
                viewHolder.countTv = (TextView) convertView.findViewById(R.id.id_dir_item_count);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            FolderBean bean = getItem(position);
            viewHolder.imageView.setImageResource(R.mipmap.icon_default_img);
            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(bean.getFirstImgPath(), viewHolder.imageView);
            viewHolder.nameTv.setText(bean.getName());
            viewHolder.countTv.setText(bean.getCount() + "");
            return convertView;
        }

        private class ViewHolder {
            ImageView imageView;
            TextView nameTv;
            TextView countTv;
        }
    }

}
