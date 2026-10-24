package com.example.datdouongpromac2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.example.datdouongpromac2.user.ProfileActivity;
import androidx.appcompat.app.AlertDialog;
import com.example.datdouongpromac2.admin.AdminActivity;
import com.example.datdouongpromac2.admin.StatisticsActivity;
import com.example.datdouongpromac2.admin.UserManagementActivity;
import com.example.datdouongpromac2.cart.CartActivity;
import com.example.datdouongpromac2.cart.CartItem;
import com.example.datdouongpromac2.fragments.DrinkTabAdapter;
import com.example.datdouongpromac2.order.OrderActivity;
import com.example.datdouongpromac2.promo.AdminPromotionActivity;
import com.example.datdouongpromac2.promo.PromotionActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private ViewPager2 viewPager;
    private double totalPrice = 0;
    private DrinkTabAdapter adapter;


    public static ArrayList<CartItem> sharedCart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Views
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Setup adapter
        adapter = new DrinkTabAdapter(this);
        viewPager.setAdapter(adapter);

        // Setup tabs
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Cà Phê");
                    break;
                case 1:
                    tab.setText("Trà Sữa");
                    break;
                case 2:
                    tab.setText("Nước Khác");
                    break;
            }
        }).attach();


        updateTotalPrice();

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        checkAdminPermissions(navigationView);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Nước Siêu Ngon");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Refresh khi quay lại
        updateTotalPrice();
    }

    private void checkAdminPermissions(NavigationView navigationView) {
        SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        String userRole = sharedPreferences.getString("user_role", "user");

        android.view.Menu menu = navigationView.getMenu();

        // ← Simple: Chỉ show khi admin
        boolean isAdmin = "admin".equals(userRole);

        if (menu.findItem(R.id.nav_admin) != null)
            menu.findItem(R.id.nav_admin).setVisible(isAdmin);
        if (menu.findItem(R.id.nav_admin_promo) != null)
            menu.findItem(R.id.nav_admin_promo).setVisible(isAdmin);
        if (menu.findItem(R.id.nav_users) != null)
            menu.findItem(R.id.nav_users).setVisible(isAdmin);
        if (menu.findItem(R.id.nav_statistics) != null)
            menu.findItem(R.id.nav_statistics).setVisible(isAdmin);
    }

    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đăng Xuất");
        builder.setMessage("Bạn chắc chắn muốn đăng xuất?");

        builder.setPositiveButton("Có", (dialog, which) -> {
            // Clear SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            // Clear cart
            sharedCart.clear();

            Toast.makeText(MainActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            // Back to LoginActivity
            startActivity(new Intent(MainActivity.this, com.example.datdouongpromac2.auth.LoginActivity.class));
            finish();
        });

        builder.setNegativeButton("Không", null);
        builder.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            Toast.makeText(this, "Menu đồ uống", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_orders) {
            startActivity(new Intent(MainActivity.this, OrderActivity.class));
        } else if (id == R.id.nav_statistics) {
            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
        } else if (id == R.id.nav_promotions) {
            startActivity(new Intent(MainActivity.this, PromotionActivity.class));
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            return true;
        } else if (id == R.id.nav_admin) {
            startActivity(new Intent(MainActivity.this, AdminActivity.class));
        } else if (id == R.id.nav_admin_promo) {
            startActivity(new Intent(MainActivity.this, AdminPromotionActivity.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(MainActivity.this, UserManagementActivity.class));
        }
        else if (id == R.id.nav_logout) {
            logout();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    public void updateTotalPrice() {
        totalPrice = 0;
        for (CartItem item : sharedCart) {
            totalPrice += item.getTotalPrice();
        }
        tvTotalPrice.setText(String.format("Tổng tiền: %,.0f đ", totalPrice));
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
