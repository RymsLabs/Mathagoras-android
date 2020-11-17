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

import java.util.ArrayList;

public class TeacherOpAdapter extends RecyclerView.Adapter<TeacherOpAdapter.MyHolder> {
    public ArrayList<TeacherOpModel> modelArrayList = new ArrayList<>();


    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView classId, titleCreate, classDate;
        public ImageView imageView;
        public MyHolder(View view) {
            super(view);
            classId = (TextView) view.findViewById(R.id.classId);
            titleCreate = (TextView) view.findViewById(R.id.titleCreate);
            classDate = (TextView) view.findViewById(R.id.classDate);
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
        return new TeacherOpAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        holder.imageView.setImageResource(modelArrayList.get(position).getImage());
        holder.classId.setText(modelArrayList.get(position).classId);
        holder.titleCreate.setText(modelArrayList.get(position).titleCreate);
        holder.classDate.setText(modelArrayList.get(position).classDate);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(holder.imageView.getContext(), Discussion.class);
                intent.putExtra("discussionId", modelArrayList.get(position).classId);
                holder.imageView.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }
}

