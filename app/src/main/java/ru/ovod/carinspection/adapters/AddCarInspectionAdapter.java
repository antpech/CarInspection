package ru.ovod.carinspection.adapters;

import android.net.Uri;
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

    public AddCarInspectionAdapter() {
        this.galleryList = new ArrayList<Photo>();
    }

    public void setGalleryList(ArrayList<Photo> galleryList) {
        this.galleryList = galleryList;
    }

    public void add(Photo photo) {
        galleryList.add(photo);
        notifyDataSetChanged();
    }

    @Override
    public AddCarInspectionAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_addcarinspection_photo, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddCarInspectionAdapter.ViewHolder viewHolder, int i) {
        viewHolder.title.setText(galleryList.get(i).getName());
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);

        File file = new File(galleryList.get(i).getPath());
        Uri photoURI = SysHelper.getInstance(null).getUri(file);

        float angle = new PhotoHelper().getRotateAngle(galleryList.get(i).getPath());

        Picasso.get()
                .load(photoURI)
                .resize(400, 400)
                .rotate(angle)
                .into(viewHolder.img);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView img;
        public ViewHolder(View view) {
            super(view);

            title = (TextView)view.findViewById(R.id.title);
            img = (ImageView) view.findViewById(R.id.img);
        }
    }
}
