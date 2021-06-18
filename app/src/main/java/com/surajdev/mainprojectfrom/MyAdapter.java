package com.surajdev.mainprojectfrom;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<ImageViewHOlder> {

    private Context context;
    private List<User> imageList;
    Bundle bundle = new Bundle();

    public MyAdapter(Context context, List<User> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_top_users,parent,false);
        return new ImageViewHOlder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHOlder holder, int position) {
        holder.setIsRecyclable(false);

        Glide.with(context).load(imageList.get(position).getImage()).into(holder.imageView);

        holder.u_name.setText("Name:\n"+imageList.get(position).getName());
        holder.u_score.setText("Scores:\n"+imageList.get(position).getScore());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putDouble("lat", imageList.get(position).latitude);
                bundle.putDouble("longi",imageList.get(position).longitude);
                bundle.putInt("score",imageList.get(position).score);
                bundle.putString("name", imageList.get(position).name);
                replace(new MapsFragment());
            }
        });

    }

    private void replace(Fragment fragment) {

        fragment.setArguments(bundle);
        FragmentTransaction  transaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame,fragment);

        transaction.commit();
    }

    @Override
    public int getItemCount() {

        return imageList.size();
    }
}
class ImageViewHOlder extends RecyclerView.ViewHolder{

    ImageView imageView;
    TextView u_name, u_score;
    public ImageViewHOlder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.userimg);
        u_name = itemView.findViewById(R.id.tbluname);
        u_score = itemView.findViewById(R.id.tblscore);

    }
}