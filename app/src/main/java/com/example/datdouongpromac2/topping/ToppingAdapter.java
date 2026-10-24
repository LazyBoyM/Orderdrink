package com.example.datdouongpromac2.topping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.datdouongpromac2.R;

import java.util.ArrayList;

public class ToppingAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Topping> toppingList;
    private ToppingCallback callback;

    public interface ToppingCallback {
        void onToppingSelected(Topping topping, boolean isSelected);
    }

    public ToppingAdapter(Context context, ArrayList<Topping> toppingList, ToppingCallback callback) {
        this.context = context;
        this.toppingList = toppingList;
        this.callback = callback;
    }

    @Override
    public int getCount() { return toppingList.size(); }

    @Override
    public Object getItem(int position) { return toppingList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_topping, parent, false);
            holder = new ViewHolder();
            holder.cbTopping = convertView.findViewById(R.id.cbTopping);
            holder.tvToppingName = convertView.findViewById(R.id.tvToppingName);
            holder.tvToppingPrice = convertView.findViewById(R.id.tvToppingPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Topping topping = toppingList.get(position);
        holder.tvToppingName.setText(topping.getName());
        holder.tvToppingPrice.setText(String.format("+%,.0f đ", topping.getPrice()));
        holder.cbTopping.setChecked(topping.isSelected());

        // ✅ CHECKBOX LISTENER
        holder.cbTopping.setOnCheckedChangeListener((buttonView, isChecked) -> {
            topping.setSelected(isChecked);
            if (callback != null) {
                callback.onToppingSelected(topping, isChecked);
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        CheckBox cbTopping;
        TextView tvToppingName, tvToppingPrice;
    }
}
