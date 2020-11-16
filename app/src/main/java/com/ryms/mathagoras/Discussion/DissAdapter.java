package com.ryms.mathagoras.Discussion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ryms.mathagoras.R;

import java.util.ArrayList;

public class DissAdapter extends RecyclerView.Adapter<DissAdapter.MyHolder> {

    public ArrayList<DissModel> modelArrayList = new ArrayList<>();

    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView user, date_time, userType, mess_age;
        public ImageView imageView;

        public MyHolder(View view) {
            super(view);
            user = (TextView) view.findViewById(R.id.user);
            date_time = (TextView) view.findViewById(R.id.date_time);
            userType = (TextView) view.findViewById(R.id.userType);
            mess_age = (TextView) view.findViewById(R.id.mess_age);
            this.imageView = view.findViewById(R.id.backTile);
        }

    }

    public DissAdapter(ArrayList<DissModel> modelArrayList) {
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public DissAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.senders_box, null);
        return new DissAdapter.MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        holder.imageView.setImageResource(modelArrayList.get(position).getImage());
        holder.user.setText(modelArrayList.get(position).user);
        holder.date_time.setText(modelArrayList.get(position).date_time);
        holder.userType.setText(modelArrayList.get(position).userType);
        holder.mess_age.setText(modelArrayList.get(position).mess_age);
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }
}
