package com.example.datdouongpromac2.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.DatabaseHelper;
import com.example.datdouongpromac2.user.User;

import java.util.ArrayList;

public class UserManagementActivity extends AppCompatActivity {

    private EditText etUserName, etUserEmail, etUserPassword;
    private Button btnAddAdmin, btnViewUsers;
    private ListView listViewUsers;
    private DatabaseHelper databaseHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> userList;
    private ArrayList<User> userObjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        etUserName = findViewById(R.id.etUserName);
        etUserEmail = findViewById(R.id.etUserEmail);
        etUserPassword = findViewById(R.id.etUserPassword);
        btnAddAdmin = findViewById(R.id.btnAddAdmin);
        btnViewUsers = findViewById(R.id.btnViewUsers);
        listViewUsers = findViewById(R.id.listViewUsers);

        databaseHelper = new DatabaseHelper(this);
        userList = new ArrayList<>();

        // ← THÊM USER
        btnAddAdmin.setOnClickListener(v -> addNewAdmin());

        // ← XEM DANH SÁCH
        btnViewUsers.setOnClickListener(v -> loadAllUsers());

        // ← CLICK ITEM ĐỂ CHỈNH SỬA
        listViewUsers.setOnItemClickListener((parent, view, position, id) -> {
            showEditUserDialog(userObjects.get(position), position);
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản Lý Người Dùng");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    // ← THÊM ADMIN MỚI
    private void addNewAdmin() {
        String name = etUserName.getText().toString().trim();
        String email = etUserEmail.getText().toString().trim();
        String password = etUserPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.checkEmailExists(email)) {
            Toast.makeText(this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        // ← THÊM VÀO DATABASE VỚI ROLE = "admin"
        if (databaseHelper.registerUser(name, email, password, "")) {
            // Cập nhật role = admin
            databaseHelper.updateUserRole(email, "admin");

            Toast.makeText(this, "✅ Bổ nhiệm admin thành công!", Toast.LENGTH_SHORT).show();

            etUserName.setText("");
            etUserEmail.setText("");
            etUserPassword.setText("");

            loadAllUsers();
        } else {
            Toast.makeText(this, "❌ Lỗi khi bổ nhiệm", Toast.LENGTH_SHORT).show();
        }
    }

    // ← HIỂN THỊ DANH SÁCH TẤT CẢ USER
    private void loadAllUsers() {
        userList.clear();
        userObjects.clear();

        // Lấy từ database
        ArrayList<String> allUsersDisplay = databaseHelper.getAllUsers();

        if (allUsersDisplay.isEmpty()) {
            Toast.makeText(this, "Chưa có người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        userList.addAll(allUsersDisplay);

        // ← LẤY USER OBJECTS ĐỂ CHỈNH SỬA
        for (String userDisplay : userList) {
            // Parse email từ display string
            String email = extractEmail(userDisplay);
            User user = databaseHelper.getUserInfo(email);
            if (user != null) {
                userObjects.add(user);
            }
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listViewUsers.setAdapter(adapter);
    }

    // ← CHỈNH SỬA / XÓA / ĐỔI ROLE
    private void showEditUserDialog(User user, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh Sửa - " + user.getName());

        // Layout input
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        EditText etName = new EditText(this);
        etName.setHint("Tên");
        etName.setText(user.getName());
        layout.addView(etName);

        EditText etEmail = new EditText(this);
        etEmail.setHint("Email");
        etEmail.setText(user.getEmail());
        etEmail.setEnabled(false);  // ← EMAIL KHÔNG SỬA ĐƯỢC
        layout.addView(etEmail);

        EditText etPhone = new EditText(this);
        etPhone.setHint("Điện thoại");
        etPhone.setText(user.getPhone());
        layout.addView(etPhone);

        builder.setView(layout);

        builder.setPositiveButton("💾", (dialog, which) -> {
            String newName = etName.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(this, "Nhập tên", Toast.LENGTH_SHORT).show();
                return;
            }

            // ← CẬP NHẬT PROFILE
            if (databaseHelper.updateUserProfile(user.getEmail(), newName, newPhone, user.getAddress())) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                loadAllUsers();
            }
        });

        builder.setNeutralButton("Đổi Quyền", (dialog, which) -> {
            showChangeRoleDialog(user);
        });

        builder.setNegativeButton("🗑️", (dialog, which) -> {
            deleteUser(user.getEmail());
        });

        builder.show();
    }

    // ← ĐỔI QUYỀN USER ↔ ADMIN
    private void showChangeRoleDialog(User user) {
        String currentRole = databaseHelper.getUserRole(user.getEmail());
        String newRole = "admin".equals(currentRole) ? "user" : "admin";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Đổi Quyền");
        builder.setMessage("Thay đổi từ " + currentRole + " thành " + newRole + "?");

        builder.setPositiveButton("Đồng Ý", (dialog, which) -> {
            databaseHelper.updateUserRole(user.getEmail(), newRole);
            Toast.makeText(this, "Đã thay đổi quyền thành " + newRole, Toast.LENGTH_SHORT).show();
            loadAllUsers();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    // ← XÓA USER
    private void deleteUser(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xóa Người Dùng");
        builder.setMessage("Bạn chắc chắn xóa người dùng này?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            if (databaseHelper.deleteUser(email)) {
                Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                loadAllUsers();
            } else {
                Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }


    private String extractEmail(String displayString) {
        int start = displayString.indexOf("- ") + 2;
        int end = displayString.indexOf(" (");
        if (end < 0) end = displayString.length();
        return displayString.substring(start, end);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
