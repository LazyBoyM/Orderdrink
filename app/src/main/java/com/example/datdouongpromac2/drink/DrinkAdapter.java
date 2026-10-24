package com.example.datdouongpromac2.drink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.datdouongpromac2.R;

import java.util.ArrayList;

public class DrinkAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Drink> drinkList;

    public DrinkAdapter(Context context, ArrayList<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @Override
    public int getCount() {
        return drinkList.size();
    }

    @Override
    public Object getItem(int position) {
        return drinkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_drink, parent, false);
            holder = new ViewHolder();
            holder.tvDrinkName = convertView.findViewById(R.id.tvDrinkName);
            holder.tvDrinkPrice = convertView.findViewById(R.id.tvDrinkPrice);
            holder.tvDrinkDescription = convertView.findViewById(R.id.tvDrinkDescription);
            holder.ivDrinkIcon = convertView.findViewById(R.id.ivDrinkIcon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Drink drink = drinkList.get(position);
        holder.tvDrinkName.setText(drink.getName());
        holder.tvDrinkPrice.setText(String.format("%,.0f đ", drink.getPrice()));
        holder.tvDrinkDescription.setText(drink.getDescription());


        String imageName = drink.getImage();  // VD: "coffee", "tra_sua"

        if (imageName != null && !imageName.isEmpty()) {
            // Tìm drawable theo tên (VD: R.drawable.coffee)
            int resourceId = context.getResources().getIdentifier(
                    imageName, "drawable", context.getPackageName());

            if (resourceId != 0) {
                // Tìm thấy drawable
                holder.ivDrinkIcon.setImageResource(resourceId);
            } else {
                // Không tìm thấy → dùng default
                holder.ivDrinkIcon.setImageResource(R.drawable.ic_drink_default);
            }
        } else {
            // Không có tên drawable → dùng default
            holder.ivDrinkIcon.setImageResource(R.drawable.ic_drink_default);
        }


        return convertView;
    }


    public static class ViewHolder {
        public TextView tvDrinkName;
        public TextView tvDrinkPrice;
        public TextView tvDrinkDescription;
        public ImageView ivDrinkIcon;
    }
}
