package com.oasis.onebox;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by tehaoye on 2019/3/27.
 */

public class CloudFileAdapter extends RecyclerView.Adapter<CloudFileAdapter.ViewHolder> {

    private Context context;
    private List<CloundFile> cloundFileList;
    private View view;
    private String tempFatherPath;
    CloudFileAdapter(Context context, List<CloundFile> memberList) {
        this.context = context;
        this.cloundFileList = memberList;
    }

    @Override
    public CloudFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.cardview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CloudFileAdapter.ViewHolder holder, int position) {
        final CloundFile cloundFile = cloundFileList.get(position);
        holder.imageId.setImageResource(cloundFile.getImage());
        holder.txfilename.setText(cloundFile.getFileName());
        holder.txfiletime.setText(cloundFile.getLastModifiedTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ImageView imageView = new ImageView(context);
//                imageView.setImageResource(cloundFile.getImage());
//                Toast toast = new Toast(context);
//                toast.setView(imageView);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.show();
//
                if (cloundFile.getFileType().equals("folder")){
                    if (context.getClass().equals(MainActivity.class)){
                        ((MainActivity) context).refreshView(cloundFile.getBase64FilePath());
                    }
                }
                else
                {
                    holder.getPopupMenu().show();
                    holder.getPopupMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId())
                            {
                                case R.id.item_download:
                                    if (context.getClass().equals(MainActivity.class)){
                                        ((MainActivity) context).downloadFile(cloundFile.getBase64FilePath(),cloundFile.getFileName());
                                        return true;
                                    }
                            }
                            return false;
                        }
                    });
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return cloundFileList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageId;
        TextView txfilename, txfiletime;
        PopupMenu popupMenu;
        ViewHolder(View itemView) {
            super(itemView);
            imageId = (ImageView) itemView.findViewById(R.id.imageId);
            txfilename = (TextView) itemView.findViewById(R.id.tx_filename);
            txfiletime = (TextView) itemView.findViewById(R.id.tx_filetime);
            popupMenu = new PopupMenu(context,view, Gravity.START);
            popupMenu.getMenuInflater().inflate(R.menu.popmenu, popupMenu.getMenu());

        }

        public ImageView getImageId() {
            return imageId;
        }

        public TextView getTxfilename() {
            return txfilename;
        }

        public TextView getTxfiletime() {
            return txfiletime;
        }

        public PopupMenu getPopupMenu() {
            return popupMenu;
        }
    }
}
