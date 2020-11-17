package com.ryms.mathagoras.Dashb;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ryms.mathagoras.Class.ClassRoom;
import com.ryms.mathagoras.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyHolder> {
    public ArrayList<Model> modelArrayList = new ArrayList<>();


    public class MyHolder extends RecyclerView.ViewHolder {
        public TextView cname, tname, description;
        public ImageView imageView;
        public MyHolder(View view) {
            super(view);
            cname = (TextView) view.findViewById(R.id.cname);
            tname = (TextView) view.findViewById(R.id.tname);
            description = (TextView) view.findViewById(R.id.description);
            this.imageView = view.findViewById(R.id.backTile);
        }
    }

    public MyAdapter(ArrayList<Model> modelArrayList) {
        this.modelArrayList = modelArrayList;
    }

    @NotNull
    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, null);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        holder.imageView.setImageResource(modelArrayList.get(position).getImage());
        holder.cname.setText(modelArrayList.get(position).cname);
        holder.tname.setText(modelArrayList.get(position).tname);
        holder.description.setText(modelArrayList.get(position).description);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(holder.imageView.getContext(), ClassRoom.class);
                intent.putExtra("cid", modelArrayList.get(position).cid);
                holder.imageView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }
}

