package com.ryms.mathagoras.Options;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ryms.mathagoras.Discussion.Discussion;
import com.ryms.mathagoras.R;

import java.text.BreakIterator;
import java.util.ArrayList;

import katex.hourglass.in.mathlib.MathView;

public class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.MyHolder> {
    public ArrayList<OptionsModel> modelArrayList = new ArrayList<>();

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView titleDiss, Type;
        public ImageView imageView;
        MathView messagePost;

        public MyHolder(View view) {
            super(view);
            Type = (TextView) view.findViewById(R.id.Type);
            titleDiss = (TextView) view.findViewById(R.id.titleDiss);
            messagePost = (MathView) view.findViewById(R.id.messagePost);
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
        holder.Type.setText(modelArrayList.get(position).Type);
        holder.titleDiss.setText(modelArrayList.get(position).titleDiss);
        holder.messagePost.setDisplayText(modelArrayList.get(position).messagePost);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelArrayList.get(position).Type.equals("Discussion")) {
                    Intent intent = new Intent(holder.imageView.getContext(), Discussion.class);
                    intent.putExtra("discussionId", modelArrayList.get(position).discussionId);
                    holder.imageView.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }
}

