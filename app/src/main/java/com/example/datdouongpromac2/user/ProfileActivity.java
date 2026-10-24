package com.example.datdouongpromac2.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.DatabaseHelper;

public class ProfileActivity extends AppCompatActivity {
    private EditText etName, etPhone, etAddress;
    private Button btnSave;
    private DatabaseHelper databaseHelper;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnSave = findViewById(R.id.btnSave);

        databaseHelper = new DatabaseHelper(this);

        // Lấy email từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_pref", MODE_PRIVATE);
        userEmail = prefs.getString("user_email", "");

        // Load thông tin cũ
        loadUserInfo();

        // Save button
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadUserInfo() {
        User user = databaseHelper.getUserInfo(userEmail);
        if (user != null) {
            etName.setText(user.getName());
            etPhone.setText(user.getPhone());
            etAddress.setText(user.getAddress());
        }
    }

    private void saveProfile() {
        String name = etName.getText().toString();
        String phone = etPhone.getText().toString();
        String address = etAddress.getText().toString();

        if (databaseHelper.updateUserProfile(userEmail, name, phone, address)) {
            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}
