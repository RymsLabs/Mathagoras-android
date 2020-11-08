package com.ryms.mathagoras.Class;

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

public class CalenderAdapter extends RecyclerView.Adapter<CalenderAdapter.MyHolder> {

    public ArrayList<ClassModel> modelArrayList = new ArrayList<>();


    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView month, date, time, day;
        public ImageView imageView;

        public MyHolder(View view) {
            super(view);
            month = (TextView) view.findViewById(R.id.month);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            day = (TextView) view.findViewById(R.id.day);
            this.imageView = view.findViewById(R.id.backTile);
        }
    }

    public CalenderAdapter(ArrayList<ClassModel> modelArrayList) {
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.calendar_row, null);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        holder.imageView.setImageResource(modelArrayList.get(position).getImage());
        holder.month.setText(modelArrayList.get(position).month);
        holder.date.setText(String.valueOf(modelArrayList.get(position).date));
        holder.time.setText(modelArrayList.get(position).time);
        holder.day.setText(modelArrayList.get(position).day);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(holder.imageView.getContext(), Discussion.class);
                holder.imageView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }
}
