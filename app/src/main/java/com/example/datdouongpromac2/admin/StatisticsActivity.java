package com.example.datdouongpromac2.admin;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.DatabaseHelper;

public class StatisticsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        databaseHelper = new DatabaseHelper(this);

        // Kiểm tra quyền Admin
        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", "user");

        if (!userRole.equals("admin")) {
            Toast.makeText(this, "⛔ Bạn không có quyền xem thống kê!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TextView tvRevenue = findViewById(R.id.tvRevenue);
        TextView tvTotalOrders = findViewById(R.id.tvTotalOrders);
        TextView tvTotalUsers = findViewById(R.id.tvTotalUsers);

        // Lấy dữ liệu từ database
        double totalRevenue = databaseHelper.getTotalRevenue();
        int totalOrders = databaseHelper.getTotalOrders();
        int totalUsers = databaseHelper.getTotalUsers();

        // Hiển thị dữ liệu
        tvRevenue.setText(String.format("💰 Tổng Doanh Thu: %,.0f đ", totalRevenue));
        tvTotalOrders.setText(String.format("📦 Tổng Đơn Hàng: %d đơn", totalOrders));
        tvTotalUsers.setText(String.format("👥 Tổng Người Dùng: %d người", totalUsers));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("📊 Thống Kê");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
