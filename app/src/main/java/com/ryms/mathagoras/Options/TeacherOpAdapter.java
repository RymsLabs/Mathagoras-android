package com.ryms.mathagoras.Options;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ryms.mathagoras.Discussion.Discussion;
import com.ryms.mathagoras.Onboard.OnBoardingActivity;
import com.ryms.mathagoras.R;

import java.util.ArrayList;

import katex.hourglass.in.mathlib.MathView;

public class TeacherOpAdapter extends RecyclerView.Adapter<TeacherOpAdapter.MyHolder> {
    public ArrayList<TeacherOpModel> modelArrayList = new ArrayList<>();
    SharedPreferences sp;


    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView TypeT, titleCreate;
        MathView classDate;
        public ImageView imageView;
        public MyHolder(View view) {
            super(view);
            TypeT = (TextView) view.findViewById(R.id.TypeT);
            titleCreate = (TextView) view.findViewById(R.id.titleCreate);
            classDate = (MathView) view.findViewById(R.id.classDate);
            this.imageView = view.findViewById(R.id.dissTile);
        }
    }

    public TeacherOpAdapter(ArrayList<TeacherOpModel> modelArrayList) {
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public TeacherOpAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_discussion, null);
        sp = parent.getContext().getSharedPreferences("SETTING", 0);
        return new TeacherOpAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        holder.imageView.setImageResource(modelArrayList.get(position).getImage());
        holder.TypeT.setText(modelArrayList.get(position).TypeT);
        holder.titleCreate.setText(modelArrayList.get(position).titleCreate);
        if(modelArrayList.get(position).TypeT.equals("Post")){
            holder.classDate.setDisplayText(modelArrayList.get(position).classDate);
        }
        else{
            holder.classDate.setDisplayText("");
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (modelArrayList.get(position).TypeT.equals("Discussion")) {
                    intent = new Intent(holder.imageView.getContext(), Discussion.class);
                    intent.putExtra("discussionId", modelArrayList.get(position).classId);
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

