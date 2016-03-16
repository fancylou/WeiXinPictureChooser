package net.muliba.weixinpicturechooser;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.muliba.weixinpicturechooser.util.ImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageListAdapter extends BaseAdapter {

        private static Set<String> mSelectImgs = new HashSet<String>();
        private List<String> mDatas;
        private String dirPath;
        private LayoutInflater inflater;
        private int mScreenWidth;

        public ImageListAdapter(Context context, List<String> mDatas, String dirPath) {
            this.mDatas = mDatas;
            this.dirPath = dirPath;
            this.inflater = LayoutInflater.from(context);

            WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(metrics);
            mScreenWidth = metrics.widthPixels;//获取屏幕宽度
        }

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder ;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_gridview, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.id_item_img);
                viewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.id_item_select);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            //重置状态
            viewHolder.imageView.setImageResource(R.mipmap.icon_default_img);
            viewHolder.imageView.setColorFilter(null);
            viewHolder.imageButton.setImageResource(R.mipmap.icon_file_check_off_grid);
            viewHolder.imageView.setMaxWidth(mScreenWidth / 3);//ImageLoader 进行图片压缩的时候可能会用到 减少内存使用
            ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(dirPath + "/" + mDatas.get(position),
                    viewHolder.imageView);

            final  String filePath = dirPath + "/" + mDatas.get(position);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectImgs.contains(filePath)) {
                        mSelectImgs.remove(filePath);
                        viewHolder.imageView.setColorFilter(null);
                        viewHolder.imageButton.setImageResource(R.mipmap.icon_file_check_off_grid);
                    }else {
                        mSelectImgs.add(filePath);
                        viewHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
                        viewHolder.imageButton.setImageResource(R.mipmap.icon_file_check_on_grid);
                    }
//                    notifyDataSetChanged();
                }
            });
            if (mSelectImgs.contains(filePath)) {
                viewHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
                viewHolder.imageButton.setImageResource(R.mipmap.icon_file_check_on_grid);
            }
            return convertView;
        }


        private class ViewHolder {
            ImageView imageView;
            ImageButton imageButton;
        }

}