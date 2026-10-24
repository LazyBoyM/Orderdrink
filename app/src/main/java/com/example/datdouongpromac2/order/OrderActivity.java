package com.example.datdouongpromac2.order;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.DatabaseHelper;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {

    private ListView listViewOrders;
    private DatabaseHelper databaseHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        listViewOrders = findViewById(R.id.listViewOrders);
        databaseHelper = new DatabaseHelper(this);

        loadOrders();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh lại lịch sử mỗi khi vào lại activity
        loadOrders();
    }

    private void loadOrders() {
        // Lấy role của user hiện tại
        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", "user");
        String userEmail = sharedPreferences.getString("user_email", "");

        if (userRole.equals("admin")) {
            // Admin xem tất cả đơn hàng
            orders = databaseHelper.getAllOrdersAdmin();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("📋 Đơn Hàng (Admin - Tất Cả)");
            }
        } else {
            // User thường chỉ xem đơn của mình (lọc theo email)
            orders = databaseHelper.getUserOrders(userEmail);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("📋 Đơn Hàng Của Tôi");
            }
        }

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, orders);
        listViewOrders.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
