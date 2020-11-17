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
        public TextView user, dateDiss, timeDiss, userType, mess_age, nameDiss;
        public ImageView imageView;

        public MyHolder(View view) {
            super(view);
            nameDiss = (TextView) view.findViewById(R.id.nameDiss);
            user = (TextView) view.findViewById(R.id.user);
            dateDiss = (TextView) view.findViewById(R.id.dateDiss);
            timeDiss = (TextView) view.findViewById(R.id.timeDiss);
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
        holder.nameDiss.setText(modelArrayList.get(position).nameDiss);
        holder.user.setText(modelArrayList.get(position).user);
        holder.dateDiss.setText(modelArrayList.get(position).dateDiss);
        holder.timeDiss.setText(modelArrayList.get(position).timeDiss);
        holder.userType.setText(modelArrayList.get(position).userType);
        holder.mess_age.setText(modelArrayList.get(position).mess_age);
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }
}
