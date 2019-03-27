package com.oasis.onebox;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

    CloudFileAdapter(Context context, List<CloundFile> memberList) {
        this.context = context;
        this.cloundFileList = memberList;
    }

    @Override
    public CloudFileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CloudFileAdapter.ViewHolder holder, int position) {
        final CloundFile cloundFile = cloundFileList.get(position);
        holder.imageId.setImageResource(cloundFile.getImage());
        holder.textId.setText(String.valueOf(cloundFile.getFileType()));
        holder.textName.setText(cloundFile.getFileName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(cloundFile.getImage());
                Toast toast = new Toast(context);
                toast.setView(imageView);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cloundFileList.size();
    }


    //Adapter 需要一個 ViewHolder，只要實作它的 constructor 就好，保存起來的view會放在itemView裡面
    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageId;
        TextView textId, textName;
        ViewHolder(View itemView) {
            super(itemView);
            imageId = (ImageView) itemView.findViewById(R.id.imageId);
            textId = (TextView) itemView.findViewById(R.id.textId);
            textName = (TextView) itemView.findViewById(R.id.textName);
        }
    }
}
