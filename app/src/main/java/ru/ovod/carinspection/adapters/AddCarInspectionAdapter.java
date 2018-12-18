package ru.ovod.carinspection.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import ru.ovod.carinspection.R;
import ru.ovod.carinspection.helpers.PhotoHelper;
import ru.ovod.carinspection.helpers.SysHelper;
import ru.ovod.carinspection.pojo.Photo;

public class AddCarInspectionAdapter extends RecyclerView.Adapter<AddCarInspectionAdapter.ViewHolder> {
    private ArrayList<Photo> galleryList;
    private boolean isDelVisible;
    public DetailsAdapterListener onClickListener;

    public AddCarInspectionAdapter() {
        this.galleryList = new ArrayList<Photo>();
        this.isDelVisible = false;
    }

    public void setGalleryList(ArrayList<Photo> galleryList) {
        this.galleryList = galleryList;
        notifyDataSetChanged();
    }

    public void add(Photo photo) {
        galleryList.add(photo);
        notifyDataSetChanged();
    }

    public void del(int position){
        galleryList.remove(position);
        notifyDataSetChanged();
    }

    public ArrayList<Photo> getGalleryList() {
        return galleryList;
    }

    public void showDelBtn(boolean isVisible){
        isDelVisible = isVisible;
        notifyDataSetChanged();
    }

    @Override
    public AddCarInspectionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_addcarinspection_photo, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddCarInspectionAdapter.ViewHolder viewHolder, final int i) {
        //viewHolder.title.setText(galleryList.get(i).getName());
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File file = new File(galleryList.get(i).getPath());
        final Uri photoURI = SysHelper.getInstance(null).getUri(file);

        float angle = new PhotoHelper().getRotateAngle(galleryList.get(i).getPath());

        Picasso.get()
                .load(photoURI)
                .resize(400, 400)
                .rotate(angle)
                .into(viewHolder.img);

        if (isDelVisible) {
            viewHolder.fab.show();
        } else {
            viewHolder.fab.hide();
        }

        if (galleryList.get(i).getIssync() == 0) {
            viewHolder.imgSynced.setVisibility(View.GONE);
        } else {
            viewHolder.imgSynced.setVisibility(View.VISIBLE);
        }

        viewHolder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.fabOnClick(v, i);
            }
        });

        viewHolder.img.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickListener.imgOnClick(photoURI);
            }
        });


    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public Photo getItem(int position) {
        return galleryList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
       // private TextView title;
        private FloatingActionButton fab;
        private ImageView img;
        private ImageView imgSynced;

        public ViewHolder(View view) {
            super(view);

            fab = (FloatingActionButton) view.findViewById(R.id.fab);
            img = (ImageView) view.findViewById(R.id.img);
            imgSynced = (ImageView) view.findViewById(R.id.imgSynced);
        }
    }

    public interface DetailsAdapterListener {

        void fabOnClick(View v, int position);
        void imgOnClick(Uri uri);
    }

    public void setOnClickListener(DetailsAdapterListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
