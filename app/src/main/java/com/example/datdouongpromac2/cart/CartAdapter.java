package com.example.datdouongpromac2.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.topping.Topping;

import java.util.ArrayList;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CartItem> cartItems;
    private CartCallback callback;

    public interface CartCallback {
        void onQuantityChanged(int position, int newQuantity);
        void onRemoveItem(int position);
        void onItemClicked(int position);  // ✅ THÊM - để chọn topping
    }

    public CartAdapter(Context context, ArrayList<CartItem> cartItems, CartCallback callback) {
        this.context = context;
        this.cartItems = cartItems;
        this.callback = callback;
    }

    @Override
    public int getCount() { return cartItems.size(); }

    @Override
    public Object getItem(int position) { return cartItems.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_cart, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tvCartItemName);
            holder.tvPrice = convertView.findViewById(R.id.tvCartItemPrice);
            holder.tvQuantity = convertView.findViewById(R.id.tvCartItemQuantity);

            holder.btnMinus = convertView.findViewById(R.id.btnMinus);
            holder.btnPlus = convertView.findViewById(R.id.btnPlus);
            holder.btnRemove = convertView.findViewById(R.id.btnRemove);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem item = cartItems.get(position);


        holder.tvName.setText(item.getName());


        holder.tvPrice.setText(String.format("%,.0f đ", (double) item.getTotalPrice()));


        holder.tvQuantity.setText("x" + item.getQuantity());



        convertView.setOnClickListener(v -> {
            if (callback != null) {
                callback.onItemClicked(position);  // ← GỌI HÀM CHỌN TOPPING
            }
        });


        holder.btnMinus.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity > 0 && callback != null) {
                callback.onQuantityChanged(position, newQuantity);
            }
        });


        holder.btnPlus.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            if (callback != null) {
                callback.onQuantityChanged(position, newQuantity);
            }
        });


        holder.btnRemove.setOnClickListener(v -> {
            if (callback != null) {
                callback.onRemoveItem(position);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvToppings;
        Button btnMinus, btnPlus, btnRemove;
    }
}
