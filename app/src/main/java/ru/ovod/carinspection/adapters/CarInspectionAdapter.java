package ru.ovod.carinspection.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;

import ru.ovod.carinspection.CarInspectionsActivity;
import ru.ovod.carinspection.R;
import ru.ovod.carinspection.helpers.PhotoHelper;
import ru.ovod.carinspection.helpers.SysHelper;
import ru.ovod.carinspection.pojo.Inspection;


public class CarInspectionAdapter extends ArrayAdapter<Inspection> {
    private Context context;
    private boolean isDelVisible;

    static class ViewHolderItem {
        private TextView photoCo;
        private TextView number;
        private TextView date;
        private TextView model;
        private TextView vin;
        private CheckBox isSynced;
        private ImageView img;
        private FloatingActionButton fab;
    }

    public CarInspectionAdapter(Context context) {
        super(context, R.layout.activity_carinspections_item);
        this.context = context;
        isDelVisible = false;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolderItem viewHolder;

        final Inspection item = getItem(position);

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.activity_carinspections_item, parent, false);
            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.photoCo = (TextView) convertView.findViewById(R.id.viewPhotoCo);
            viewHolder.number = (TextView) convertView.findViewById(R.id.viewNumber);
            viewHolder.date = (TextView) convertView.findViewById(R.id.viewDate);
            viewHolder.model = (TextView) convertView.findViewById(R.id.viewModel);
            viewHolder.vin = (TextView) convertView.findViewById(R.id.viewVIN);
            viewHolder.isSynced = (CheckBox) convertView.findViewById(R.id.chbIsSynced);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img);
            viewHolder.fab = (FloatingActionButton) convertView.findViewById(R.id.fab);
            viewHolder.fab.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        ((CarInspectionsActivity) context).delInspection(item);
                        CarInspectionAdapter.this.remove(item);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                    }

                }
            });

            // store the holder with the view.
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        if(item != null) {
            if (item.getPhotoCo() > 0) {
                viewHolder.photoCo.setText(String.valueOf(item.getPhotoCo()));
            } else {
                viewHolder.photoCo.setText("");
            }

            viewHolder.number.setText(String.valueOf(item.getNumber()));
            if (item.getDate().getTime() == 0) {
                viewHolder.date.setText("");
            }else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
                viewHolder.date.setText(dateFormat.format(item.getDate()));
            }
            viewHolder.model.setText(item.getModel());
            viewHolder.vin.setText(item.getVin());
            viewHolder.isSynced.setChecked(item.getIssync() == 1);

            if (item.getPath() != null) {
                float angle = new PhotoHelper().getRotateAngle(item.getPath());

                File file = new File(item.getPath());
                Uri photoURI = SysHelper.getInstance(null).getUri(file);
                Picasso.get()
                        .load(photoURI)
                        .resize(100, 100)
                        .rotate(angle)
                        .into(viewHolder.img);
            }

            if (isDelVisible) {
                viewHolder.fab.show();
            } else {
                viewHolder.fab.hide();
            }

            convertView.setTag(viewHolder);
        }

        return convertView;
    }

    public void showDelBtn(boolean isVisible){
        isDelVisible = isVisible;
        notifyDataSetChanged();
    }


}
