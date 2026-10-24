package com.example.datdouongpromac2.admin;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.datdouongpromac2.R;
import com.example.datdouongpromac2.DatabaseHelper;
import com.example.datdouongpromac2.drink.Drink;
import com.example.datdouongpromac2.drink.DrinkAdapter;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    private EditText etDrinkName, etDrinkPrice, etDrinkDescription;
    private Spinner spinnerCategory, spinnerDrawable;
    private Button btnAddDrink, btnUpdateDrink, btnDeleteDrink;
    private ListView listViewAdminDrinks;
    private DatabaseHelper databaseHelper;
    private ArrayList<Drink> drinkList;
    private ArrayList<String> drawableNames;  // ← LIST ẢNH
    private Drink selectedDrink = null;
    private String selectedImage = "";  // ← ẢNH ĐƯỢC CHỌN

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        etDrinkName = findViewById(R.id.etDrinkName);
        etDrinkPrice = findViewById(R.id.etDrinkPrice);
        etDrinkDescription = findViewById(R.id.etDrinkDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerDrawable = findViewById(R.id.spinnerDrawable);  // ← THÊM
        btnAddDrink = findViewById(R.id.btnAddDrink);
        btnUpdateDrink = findViewById(R.id.btnUpdateDrink);
        btnDeleteDrink = findViewById(R.id.btnDeleteDrink);
        listViewAdminDrinks = findViewById(R.id.listViewAdminDrinks);

        databaseHelper = new DatabaseHelper(this);
        loadDrinks();

        // ← SETUP CATEGORY SPINNER
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Cà Phê");
        categories.add("Trà Sữa");
        categories.add("Nước Khác");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(categoryAdapter);

        // ← SETUP DRAWABLE SPINNER
        drawableNames = new ArrayList<>();
        drawableNames.add("(Không chọn)");
        drawableNames.add("ic_coffee_black");
        drawableNames.add("ic_coffee_milk");
        drawableNames.add("ic_drink_default");
        drawableNames.add("ic_juice_orange");
        drawableNames.add("ic_smoothie_avocado");
        drawableNames.add("ic_soda_lemon");
        drawableNames.add("ic_tea_boba");
        drawableNames.add("ic_tea_matcha");
        drawableNames.add("ic_tea_peach");

        ArrayAdapter<String> drawableAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, drawableNames);
        spinnerDrawable.setAdapter(drawableAdapter);

        // ← LẮNG NGHE CHỌN ẢNH
        spinnerDrawable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position == 0) {
                    selectedImage = "";
                } else {
                    selectedImage = drawableNames.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ← BUTTON LISTENERS
        btnAddDrink.setOnClickListener(v -> addDrink());
        btnUpdateDrink.setOnClickListener(v -> updateDrink());
        btnDeleteDrink.setOnClickListener(v -> deleteDrink());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quản Lý Đồ Uống");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void loadDrinks() {
        drinkList = databaseHelper.getAllDrinks();

        // ← DÙNG DrinkAdapter
        DrinkAdapter adapter = new DrinkAdapter(this, drinkList);
        listViewAdminDrinks.setAdapter(adapter);

        listViewAdminDrinks.setOnItemClickListener((parent, view, position, id) -> {
            selectedDrink = drinkList.get(position);
            loadDrinkToFields(selectedDrink);
            Toast.makeText(this, "Chọn: " + selectedDrink.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    private void loadDrinkToFields(Drink drink) {
        etDrinkName.setText(drink.getName());
        etDrinkPrice.setText(String.valueOf((int)drink.getPrice()));
        etDrinkDescription.setText(drink.getDescription());
        spinnerCategory.setSelection(getSpinnerIndex(spinnerCategory, drink.getCategory()));

        // ← LOAD ẢNH
        String imageName = drink.getImage();
        if (imageName != null && !imageName.isEmpty()) {
            int position = drawableNames.indexOf(imageName);
            if (position >= 0) {
                spinnerDrawable.setSelection(position);
            }
        } else {
            spinnerDrawable.setSelection(0);
        }

        selectedImage = imageName != null ? imageName : "";
    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                return i;
            }
        }
        return 0;
    }

    private void addDrink() {
        String name = etDrinkName.getText().toString().trim();
        String priceStr = etDrinkPrice.getText().toString().trim();
        String description = etDrinkDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String image = selectedImage;  // ← ẢNH

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Nhập đủ tên và giá!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        databaseHelper.addDrink(name, price, description, category, image, 0);  // ← THÊM image

        clearFields();
        loadDrinks();
        Toast.makeText(this, "Thêm thành công!", Toast.LENGTH_SHORT).show();
    }

    private void updateDrink() {
        if (selectedDrink == null) {
            Toast.makeText(this, "Chọn đồ uống để sửa!", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etDrinkName.getText().toString().trim();
        String priceStr = etDrinkPrice.getText().toString().trim();
        String description = etDrinkDescription.getText().toString().trim();
        String image = selectedImage;  // ← ẢNH

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Nhập đủ tên và giá!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        databaseHelper.updateDrink(selectedDrink.getId(), name, price, description, image, 0);  // ← THÊM image

        clearFields();
        loadDrinks();
        Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
    }

    private void deleteDrink() {
        if (selectedDrink == null) {
            Toast.makeText(this, "Chọn đồ uống để xóa!", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseHelper.deleteDrink(selectedDrink.getId());
        clearFields();
        loadDrinks();
        Toast.makeText(this, "Xóa thành công!", Toast.LENGTH_SHORT).show();
    }

    private void clearFields() {
        etDrinkName.setText("");
        etDrinkPrice.setText("");
        etDrinkDescription.setText("");
        spinnerCategory.setSelection(0);
        spinnerDrawable.setSelection(0);  // ← CLEAR SPINNER ẢNH
        selectedDrink = null;
        selectedImage = "";  // ← CLEAR SELECTED IMAGE
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
