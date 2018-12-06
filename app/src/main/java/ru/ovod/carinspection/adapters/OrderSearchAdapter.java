package ru.ovod.carinspection.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import ru.ovod.carinspection.R;
import ru.ovod.carinspection.pojo.Order;


public class OrderSearchAdapter extends ArrayAdapter<Order> {
    private Context context;

    static class ViewHolderItem {
        TextView number;
        TextView date;
        TextView model;
        TextView vin;
    }

    public OrderSearchAdapter(Context context) {
        super(context, R.layout.activity_ordersearch_item);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolderItem viewHolder;

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.activity_ordersearch_item, parent, false);
            // well set up the ViewHolder
            viewHolder = new ViewHolderItem();
            viewHolder.number = (TextView) convertView.findViewById(R.id.viewNumber);
            viewHolder.date = (TextView) convertView.findViewById(R.id.viewDate);
            viewHolder.model = (TextView) convertView.findViewById(R.id.viewModel);
            viewHolder.vin = (TextView) convertView.findViewById(R.id.viewVIN);
            // store the holder with the view.
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        final Order item = getItem(position);
        if(item != null) {
            viewHolder.number.setText(String.valueOf(item.getNumber()));
            if (item.getDate().getTime() == 0) {
                viewHolder.date.setText("");
            }else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.mm.yyyy");
                viewHolder.date.setText(dateFormat.format(item.getDate()));
            }
            viewHolder.model.setText(item.getModel());
            viewHolder.vin.setText(item.getVin());

            convertView.setTag(viewHolder);
        }
        return convertView;
    }

}