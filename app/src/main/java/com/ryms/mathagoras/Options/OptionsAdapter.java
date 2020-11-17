package com.ryms.mathagoras.Options;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ryms.mathagoras.Class.ClassRoom;
import com.ryms.mathagoras.Dashb.Model;
import com.ryms.mathagoras.Dashb.MyAdapter;
import com.ryms.mathagoras.Discussion.Discussion;
import com.ryms.mathagoras.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.MyHolder> {
    public ArrayList<OptionsModel> modelArrayList = new ArrayList<>();


    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView discussionId, titleDiss, messagePost;
        public ImageView imageView;
        public MyHolder(View view) {
            super(view);
            discussionId = (TextView) view.findViewById(R.id.discussionId);
            titleDiss = (TextView) view.findViewById(R.id.titleDiss);
            messagePost = (TextView) view.findViewById(R.id.messagePost);
            this.imageView = view.findViewById(R.id.dissTile);
        }
    }

    public OptionsAdapter(ArrayList<OptionsModel> modelArrayList) {
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.get_discussions, null);
        return new OptionsAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        holder.imageView.setImageResource(modelArrayList.get(position).getImage());
        holder.discussionId.setText(modelArrayList.get(position).discussionId);
        holder.titleDiss.setText(modelArrayList.get(position).titleDiss);
        holder.messagePost.setText(modelArrayList.get(position).messagePost);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(holder.imageView.getContext(), Discussion.class);
                intent.putExtra("discussionId", modelArrayList.get(position).discussionId);
                holder.imageView.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }
}

