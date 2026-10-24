package com.example.datdouongpromac2.cart;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.DatabaseHelper;
import com.example.datdouongpromac2.MainActivity;
import com.example.datdouongpromac2.promo.Promotion;
import com.example.datdouongpromac2.topping.Topping;
import com.example.datdouongpromac2.topping.ToppingAdapter;
import com.example.datdouongpromac2.user.User;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CartActivity extends AppCompatActivity {

    private ListView listViewCart;
    private TextView tvSubtotal, tvDiscount, tvTotalPrice;
    private EditText etPromoCode, etPhone, etAddress;  // ← UPDATED
    private Button btnApplyPromo, btnCheckout, btnContinue, btnShowPromotions;
    private CartAdapter cartAdapter;
    private ArrayList<CartItem> cartItems;
    private DatabaseHelper databaseHelper;

    private double subtotal = 0;
    private double discountAmount = 0;
    private int discountPercent = 0;
    private double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize views
        listViewCart = findViewById(R.id.listViewCart);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        etPromoCode = findViewById(R.id.etPromoCode);
        etPhone = findViewById(R.id.etPhone);      // ← NEW
        etAddress = findViewById(R.id.etAddress);  // ← NEW
        btnApplyPromo = findViewById(R.id.btnApplyPromo);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnContinue = findViewById(R.id.btnContinue);
        btnShowPromotions = findViewById(R.id.btnShowPromotions);

        databaseHelper = new DatabaseHelper(this);

        // ✅ LẤY CART TỬ STATIC
        cartItems = MainActivity.sharedCart;

        // ← NEW: AUTO-FILL PHONE + ADDRESS
        loadUserInfo();

        // Adapter
        cartAdapter = new CartAdapter(this, cartItems, new CartAdapter.CartCallback() {
            @Override
            public void onQuantityChanged(int position, int newQuantity) {
                if (newQuantity <= 0) {
                    cartItems.remove(position);
                } else {
                    cartItems.get(position).setQuantity(newQuantity);
                }
                cartAdapter.notifyDataSetChanged();
                updatePrices();
            }

            @Override
            public void onRemoveItem(int position) {
                cartItems.remove(position);
                cartAdapter.notifyDataSetChanged();
                updatePrices();
                Toast.makeText(CartActivity.this, "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemClicked(int position) {
                CartItem item = cartItems.get(position);
                showToppingDialog(item);
            }
        });

        listViewCart.setAdapter(cartAdapter);

        // Apply promo
        btnApplyPromo.setOnClickListener(v -> {
            String promoCode = etPromoCode.getText().toString().trim().toUpperCase();
            if (promoCode.isEmpty()) {
                Toast.makeText(CartActivity.this, "Vui lòng nhập mã khuyến mãi", Toast.LENGTH_SHORT).show();
                return;
            }

            Promotion promotion = databaseHelper.getPromotionByCode(promoCode);
            if (promotion != null && promotion.isActive()) {
                discountPercent = promotion.getDiscountPercent();
                updatePrices();
                String msg = "Áp dụng mã giảm " + discountPercent + "% thành công!";
                Toast.makeText(CartActivity.this, msg, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CartActivity.this, "Mã khuyến mãi không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        // Show promotions
        btnShowPromotions.setOnClickListener(v -> showPromotionsDialog());

        // ← UPDATED: Checkout with phone + address
        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(CartActivity.this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            // ← NEW: VALIDATE
            if (phone.isEmpty()) {
                Toast.makeText(CartActivity.this, "Vui lòng nhập số điện thoại!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (address.isEmpty()) {
                Toast.makeText(CartActivity.this, "Vui lòng nhập địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("user_email", "");

            String msg = "Thanh toán thành công: " + String.format("%,.0f đ", totalPrice);
            Toast.makeText(CartActivity.this, msg, Toast.LENGTH_LONG).show();

            DateFormat dateFormat = DateFormat.getDateInstance();
            String date = dateFormat.format(new Date());

            // ← UPDATED: Save with phone + address
            databaseHelper.addOrder(date, totalPrice, "Hoàn thành", userEmail, phone, address);

            // ✅ XÓA CART
            cartItems.clear();
            cartAdapter.notifyDataSetChanged();
            discountPercent = 0;
            etPromoCode.setText("");
            updatePrices();
        });

        // Continue shopping
        btnContinue.setOnClickListener(v -> finish());

        // Setup ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Giỏ Hàng");
        }

        updatePrices();
    }

    // ← NEW: Load user info từ database
    private void loadUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("user_email", "");

        User user = databaseHelper.getUserInfo(userEmail);  // ← Trả về User object
        if (user != null) {
            etPhone.setText(user.getPhone() != null ? user.getPhone() : "");
            etAddress.setText(user.getAddress() != null ? user.getAddress() : "");
        }
    }

    // ✅ HÀM CHỌN TOPPING
    private void showToppingDialog(CartItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn Topping cho " + item.getName());

        ArrayList<Topping> allToppings = databaseHelper.getAllToppings();

        ToppingAdapter adapter = new ToppingAdapter(this, allToppings,
                (topping, isSelected) -> {
                    if (isSelected) {
                        if (!item.getToppings().contains(topping)) {
                            item.addTopping(topping);
                        }
                    } else {
                        item.removeTopping(topping);
                    }
                    updatePrices();
                });

        builder.setAdapter(adapter, null);
        builder.setPositiveButton("OK", (dialog, which) -> {
            cartAdapter.notifyDataSetChanged();
            updatePrices();
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showPromotionsDialog() {
        ArrayList<Promotion> promotions = databaseHelper.getAllPromotions();

        if (promotions.isEmpty()) {
            Toast.makeText(this, "Không có khuyến mãi nào", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> promoDisplay = new ArrayList<>();
        for (Promotion promo : promotions) {
            if (promo.isActive()) {
                String item = promo.getCode() + " - Giảm " + promo.getDiscountPercent() +
                        "% (" + promo.getDescription() + ")";
                promoDisplay.add(item);
            }
        }

        if (promoDisplay.isEmpty()) {
            Toast.makeText(this, "Không có khuyến mãi đang áp dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Khuyến Mãi Đang Có");
        builder.setItems(promoDisplay.toArray(new String[0]), (dialog, which) -> {
            Promotion selectedPromo = promotions.get(which);
            if (selectedPromo.isActive()) {
                discountPercent = selectedPromo.getDiscountPercent();
                etPromoCode.setText(selectedPromo.getCode());
                updatePrices();
                String msg = "Đã áp dụng: " + selectedPromo.getCode();
                Toast.makeText(CartActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("Đóng", null);
        builder.show();
    }

    private void updatePrices() {
        subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }

        discountAmount = subtotal * discountPercent / 100.0;
        totalPrice = subtotal - discountAmount;

        tvSubtotal.setText(String.format("Tạm tính: %,.0f đ", subtotal));
        tvDiscount.setText(String.format("Giảm giá (%d%%): -%,.0f đ", discountPercent, discountAmount));
        tvTotalPrice.setText(String.format("Tổng tiền: %,.0f đ", totalPrice));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
