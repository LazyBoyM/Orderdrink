package com.example.datdouongpromac2.promo;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.DatabaseHelper;

import java.util.ArrayList;

public class PromotionActivity extends AppCompatActivity {

    private ListView listViewPromotions;
    private PromotionAdapter promotionAdapter;
    private DatabaseHelper databaseHelper;
    private ArrayList<Promotion> promotionList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);

        listViewPromotions = findViewById(R.id.listViewPromotions);
        databaseHelper = new DatabaseHelper(this);

        loadPromotions();

        listViewPromotions.setOnItemClickListener((parent, view, position, id) -> {
            Promotion promotion = promotionList.get(position);
            if (promotion.isActive()) {
                Toast.makeText(this,
                        "Mã: " + promotion.getCode() + " - Giảm " + promotion.getDiscountPercent() + "%",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Mã khuyến mãi đã hết hạn", Toast.LENGTH_SHORT).show();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("🎁 Khuyến Mãi");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh lại danh sách mỗi khi vào lại activity
        loadPromotions();
    }

    private void loadPromotions() {
        promotionList = databaseHelper.getAllPromotions();
        promotionAdapter = new PromotionAdapter(this, promotionList);
        listViewPromotions.setAdapter(promotionAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
