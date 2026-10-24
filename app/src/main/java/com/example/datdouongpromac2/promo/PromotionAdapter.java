package com.example.datdouongpromac2.promo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.datdouongpromac2.R;

import java.util.ArrayList;

public class PromotionAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Promotion> promotionList;

    public PromotionAdapter(Context context, ArrayList<Promotion> promotionList) {
        this.context = context;
        this.promotionList = promotionList;
    }

    @Override
    public int getCount() { return promotionList.size(); }

    @Override
    public Object getItem(int position) { return promotionList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_promotion, parent, false);
            holder = new ViewHolder();
            holder.tvPromoCode = convertView.findViewById(R.id.tvPromoCode);
            holder.tvPromoDescription = convertView.findViewById(R.id.tvPromoDescription);
            holder.tvPromoDiscount = convertView.findViewById(R.id.tvPromoDiscount);
            holder.tvPromoDate = convertView.findViewById(R.id.tvPromoDate);
            holder.tvPromoStatus = convertView.findViewById(R.id.tvPromoStatus);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Promotion promotion = promotionList.get(position);
        holder.tvPromoCode.setText(promotion.getCode());
        holder.tvPromoDescription.setText(promotion.getDescription());
        holder.tvPromoDiscount.setText("Giảm " + promotion.getDiscountPercent() + "%");
        holder.tvPromoDate.setText(promotion.getStartDate() + " - " + promotion.getEndDate());
        holder.tvPromoStatus.setText(promotion.isActive() ? "Đang áp dụng" : "Hết hạn");
        holder.tvPromoStatus.setTextColor(
                promotion.isActive() ?
                        context.getResources().getColor(android.R.color.holo_green_dark) :
                        context.getResources().getColor(android.R.color.holo_red_dark)
        );

        return convertView;
    }

    static class ViewHolder {
        TextView tvPromoCode, tvPromoDescription, tvPromoDiscount, tvPromoDate, tvPromoStatus;
    }
}
