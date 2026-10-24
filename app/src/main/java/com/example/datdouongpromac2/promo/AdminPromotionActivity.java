package com.example.datdouongpromac2.promo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.DatabaseHelper;

import java.util.ArrayList;

public class AdminPromotionActivity extends AppCompatActivity {

    private EditText etPromoCode, etPromoDescription, etPromoDiscount, etPromoStartDate, etPromoEndDate;
    private Switch switchPromoActive;
    private Button btnAddPromo, btnUpdatePromo, btnDeletePromo;
    private ListView listViewPromos;
    private DatabaseHelper databaseHelper;
    private ArrayList<Promotion> promotionList;
    private PromotionAdapter adapter;
    private int selectedPromoId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_promotion);

        etPromoCode = findViewById(R.id.etPromoCode);
        etPromoDescription = findViewById(R.id.etPromoDescription);
        etPromoDiscount = findViewById(R.id.etPromoDiscount);
        etPromoStartDate = findViewById(R.id.etPromoStartDate);
        etPromoEndDate = findViewById(R.id.etPromoEndDate);
        switchPromoActive = findViewById(R.id.switchPromoActive);
        btnAddPromo = findViewById(R.id.btnAddPromo);
        btnUpdatePromo = findViewById(R.id.btnUpdatePromo);
        btnDeletePromo = findViewById(R.id.btnDeletePromo);
        listViewPromos = findViewById(R.id.listViewAdminPromos);

        databaseHelper = new DatabaseHelper(this);
        loadPromotions();

        btnAddPromo.setOnClickListener(v -> handleAddPromo());
        btnUpdatePromo.setOnClickListener(v -> handleUpdatePromo());
        btnDeletePromo.setOnClickListener(v -> handleDeletePromo());

        listViewPromos.setOnItemClickListener((parent, view, position, id) -> {
            Promotion promo = promotionList.get(position);
            selectedPromoId = promo.getId();
            etPromoCode.setText(promo.getCode());
            etPromoDescription.setText(promo.getDescription());
            etPromoDiscount.setText(String.valueOf(promo.getDiscountPercent()));
            etPromoStartDate.setText(promo.getStartDate());
            etPromoEndDate.setText(promo.getEndDate());
            switchPromoActive.setChecked(promo.isActive());
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Quản Lý Khuyến Mãi");
        }
    }

    private void handleAddPromo() {
        String code = etPromoCode.getText().toString().trim();
        String description = etPromoDescription.getText().toString().trim();
        String discountStr = etPromoDiscount.getText().toString().trim();
        String startDate = etPromoStartDate.getText().toString().trim();
        String endDate = etPromoEndDate.getText().toString().trim();
        boolean isActive = switchPromoActive.isChecked();

        if (code.isEmpty() || description.isEmpty() || discountStr.isEmpty() ||
                startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int discount = Integer.parseInt(discountStr);
            if (discount < 0 || discount > 100) {
                Toast.makeText(this, "Giảm giá từ 0-100%", Toast.LENGTH_SHORT).show();
                return;
            }

            if (databaseHelper.addPromotion(code, description, discount, startDate, endDate, isActive)) {
                Toast.makeText(this, "Thêm khuyến mãi thành công", Toast.LENGTH_SHORT).show();
                clearFields();
                loadPromotions();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giảm giá phải là số", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleUpdatePromo() {
        if (selectedPromoId == -1) {
            Toast.makeText(this, "Vui lòng chọn khuyến mãi cần sửa", Toast.LENGTH_SHORT).show();
            return;
        }

        String code = etPromoCode.getText().toString().trim();
        String description = etPromoDescription.getText().toString().trim();
        String discountStr = etPromoDiscount.getText().toString().trim();
        String startDate = etPromoStartDate.getText().toString().trim();
        String endDate = etPromoEndDate.getText().toString().trim();
        boolean isActive = switchPromoActive.isChecked();

        if (code.isEmpty() || description.isEmpty() || discountStr.isEmpty() ||
                startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int discount = Integer.parseInt(discountStr);
            if (databaseHelper.updatePromotion(selectedPromoId, code, description, discount,
                    startDate, endDate, true)) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                clearFields();
                loadPromotions();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giảm giá phải là số", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleDeletePromo() {
        if (selectedPromoId == -1) {
            Toast.makeText(this, "Vui lòng chọn khuyến mãi cần xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.deletePromotion(selectedPromoId)) {
            Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
            clearFields();
            loadPromotions();
        }
    }

    private void loadPromotions() {
        promotionList = databaseHelper.getAllPromotions();
        adapter = new PromotionAdapter(this, promotionList);
        listViewPromos.setAdapter(adapter);
    }

    private void clearFields() {
        etPromoCode.setText("");
        etPromoDescription.setText("");
        etPromoDiscount.setText("");
        etPromoStartDate.setText("");
        etPromoEndDate.setText("");
        switchPromoActive.setChecked(true);
        selectedPromoId = -1;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
