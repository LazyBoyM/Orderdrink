package com.example.datdouongpromac2.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.DatabaseHelper;
import com.example.datdouongpromac2.MainActivity;
import com.example.datdouongpromac2.admin.AdminActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etName, etPhone;
    private Button btnLogin;  // ← CHỈ CÓ btnLogin
    private TextView tvToggleText;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        btnLogin = findViewById(R.id.btnLogin);  // ← CHỈ CÓ CÁI NÀY
        tvToggleText = findViewById(R.id.tvToggleText);

        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences("user_pref", MODE_PRIVATE);

        updateFormUI();

        // Nút Login xử lý cả Login lẫn Signup
        btnLogin.setOnClickListener(v -> {
            if (isLoginMode) {
                handleLogin();
            } else {
                handleSignup();
            }
        });

        // Chuyển đổi giữa Login và Signup
        tvToggleText.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            updateFormUI();
        });
    }

    private void updateFormUI() {
        if (isLoginMode) {
            btnLogin.setText("Đăng Nhập");
            etName.setVisibility(android.view.View.GONE);
            etPhone.setVisibility(android.view.View.GONE);
            tvToggleText.setText("Chưa có tài khoản? Đăng ký");
        } else {
            btnLogin.setText("Đăng Ký");
            etName.setVisibility(android.view.View.VISIBLE);
            etPhone.setVisibility(android.view.View.VISIBLE);
            tvToggleText.setText("Đã có tài khoản? Đăng nhập");
        }
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkUser(email, password)) {
            try {
                // ← LẤY ROLE
                String role = databaseHelper.getUserRole(email);

                // Lưu vào SharedPreferences
                SharedPreferences.Editor editor = getSharedPreferences("user_pref", MODE_PRIVATE).edit();
                editor.putString("user_email", email);
                editor.putString("user_role", role);
                editor.apply();

                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                // ← CHECK ROLE → NAVIGATE
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Email hoặc mật khẩu sai", Toast.LENGTH_SHORT).show();
        }
    }



    private void handleSignup() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkEmailExists(email)) {
            Toast.makeText(this, "Email đã được đăng ký", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.registerUser(name, email, password, phone)) {
            Toast.makeText(this, "Đăng ký thành công. Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            isLoginMode = true;
            updateFormUI();
            clearFields();
        } else {
            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
        }
    }


    private void clearFields() {
        etEmail.setText("");
        etPassword.setText("");
        etName.setText("");
        etPhone.setText("");
    }
}
