package com.example.datdouongpromac2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.datdouongpromac2.drink.Drink;
import com.example.datdouongpromac2.topping.Topping;
import com.example.datdouongpromac2.promo.Promotion;
import com.example.datdouongpromac2.user.User;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "drinkmanagement.db";
    private static final int DATABASE_VERSION = 25;  // ← UPDATED

    // Tables
    public static final String TABLE_DRINKS = "drinks";
    public static final String TABLE_ORDERS = "orders";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PROMOTIONS = "promotions";
    public static final String TABLE_TOPPINGS = "toppings";

    // Columns
    public static final String COLUMN_DRINK_ID = "id";
    public static final String COLUMN_DRINK_NAME = "name";
    public static final String COLUMN_DRINK_PRICE = "price";
    public static final String COLUMN_DRINK_DESCRIPTION = "description";
    public static final String COLUMN_DRINK_CATEGORY = "category";
    public static final String COLUMN_DRINK_IMAGE = "imagePath";

    public static final String COLUMN_ORDER_ID = "id";
    public static final String COLUMN_ORDER_DATE = "date";
    public static final String COLUMN_ORDER_TOTAL = "total";
    public static final String COLUMN_ORDER_STATUS = "status";
    public static final String COLUMN_ORDER_USER_EMAIL = "user_email";
    public static final String COLUMN_ORDER_PHONE = "phone";       // ← NEW
    public static final String COLUMN_ORDER_ADDRESS = "address";   // ← NEW

    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_NAME = "name";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
    public static final String COLUMN_USER_PHONE = "phone";
    public static final String COLUMN_USER_ADDRESS = "address";   // ← NEW

    public static final String COLUMN_PROMO_ID = "id";
    public static final String COLUMN_PROMO_CODE = "code";
    public static final String COLUMN_PROMO_DESCRIPTION = "description";
    public static final String COLUMN_PROMO_DISCOUNT = "discount_percent";
    public static final String COLUMN_PROMO_START_DATE = "start_date";
    public static final String COLUMN_PROMO_END_DATE = "end_date";
    public static final String COLUMN_PROMO_ACTIVE = "is_active";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Drinks
        String createDrinksTable = "CREATE TABLE " + TABLE_DRINKS + "(" +
                COLUMN_DRINK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_DRINK_NAME + " TEXT NOT NULL," +
                COLUMN_DRINK_PRICE + " REAL NOT NULL," +
                COLUMN_DRINK_DESCRIPTION + " TEXT," +
                COLUMN_DRINK_CATEGORY + " TEXT," +
                COLUMN_DRINK_IMAGE + " TEXT," +
                "drawable_id INTEGER DEFAULT 0" +
                ")";
        db.execSQL(createDrinksTable);

        // Orders - UPDATED
        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + "(" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_ORDER_DATE + " TEXT NOT NULL," +
                COLUMN_ORDER_TOTAL + " REAL NOT NULL," +
                COLUMN_ORDER_STATUS + " TEXT," +
                COLUMN_ORDER_USER_EMAIL + " TEXT," +
                COLUMN_ORDER_PHONE + " TEXT," +
                COLUMN_ORDER_ADDRESS + " TEXT" +
                ")";
        db.execSQL(createOrdersTable);

        // Users - UPDATED
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_NAME + " TEXT NOT NULL," +
                COLUMN_USER_EMAIL + " TEXT UNIQUE NOT NULL," +
                COLUMN_USER_PASSWORD + " TEXT NOT NULL," +
                COLUMN_USER_PHONE + " TEXT," +
                COLUMN_USER_ADDRESS + " TEXT," +
                "role TEXT DEFAULT 'user'" +
                ")";
        db.execSQL(createUsersTable);

        // Promotions
        String createPromotionsTable = "CREATE TABLE " + TABLE_PROMOTIONS + "(" +
                COLUMN_PROMO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PROMO_CODE + " TEXT UNIQUE NOT NULL," +
                COLUMN_PROMO_DESCRIPTION + " TEXT," +
                COLUMN_PROMO_DISCOUNT + " INTEGER NOT NULL," +
                COLUMN_PROMO_START_DATE + " TEXT," +
                COLUMN_PROMO_END_DATE + " TEXT," +
                COLUMN_PROMO_ACTIVE + " INTEGER DEFAULT 1" +
                ")";
        db.execSQL(createPromotionsTable);

        // Toppings
        String createToppingsTable = "CREATE TABLE " + TABLE_TOPPINGS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "price REAL NOT NULL" +
                ")";
        db.execSQL(createToppingsTable);

        insertSampleDrinks(db);
        insertSamplePromotions(db);
        insertSampleToppings(db);
        insertDefaultUsers(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 14) {
            try {
                // Add columns nếu chưa có
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COLUMN_USER_ADDRESS + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN " + COLUMN_ORDER_PHONE + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_ORDERS + " ADD COLUMN " + COLUMN_ORDER_ADDRESS + " TEXT");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== USERS ====================
    public boolean registerUser(String name, String email, String password, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_EMAIL, email);
        values.put(COLUMN_USER_PASSWORD, password);
        values.put(COLUMN_USER_PHONE, phone);
        values.put(COLUMN_USER_ADDRESS, "");  // Default empty
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // ← NEW: Cập nhật profile
    public boolean updateUserProfile(String email, String name, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_PHONE, phone);
        values.put(COLUMN_USER_ADDRESS, address);
        int result = db.update(TABLE_USERS, values, COLUMN_USER_EMAIL + "=?", new String[]{email});
        db.close();
        return result > 0;
    }

    // ← NEW: Lấy user info
    public User getUserInfo(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_EMAIL + "=?", new String[]{email});

        User user = null;
        if (cursor.moveToFirst()) {
            try {
                // SELECT * trả về: id, name, email, password, phone, address
                String name = cursor.getString(1);      // name
                String phone = cursor.getString(4);     // phone
                String address = cursor.getString(5);   // address
                user = new User(email, name, phone, address);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        return user;
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                        COLUMN_USER_EMAIL + "=? AND " + COLUMN_USER_PASSWORD + "=?",
                new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean checkEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_USER_EMAIL + "=?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // ==================== ORDERS ====================
    // ← UPDATED: Thêm phone + address
    public boolean addOrder(String date, double total, String status, String userEmail, String phone, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_DATE, date);
        values.put(COLUMN_ORDER_TOTAL, total);
        values.put(COLUMN_ORDER_STATUS, status);
        values.put(COLUMN_ORDER_USER_EMAIL, userEmail);
        values.put(COLUMN_ORDER_PHONE, phone);
        values.put(COLUMN_ORDER_ADDRESS, address);
        long result = db.insert(TABLE_ORDERS, null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<String> getUserOrders(String userEmail) {
        ArrayList<String> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS +
                        " WHERE " + COLUMN_ORDER_USER_EMAIL + "=? ORDER BY " + COLUMN_ORDER_DATE + " DESC",
                new String[]{userEmail});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String date = cursor.getString(1);
                double total = cursor.getDouble(2);
                String status = cursor.getString(3);
                orderList.add(String.format("Đơn #%03d - %,.0f đ - %s", id, total, status));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orderList;
    }

    // ==================== DRINKS ====================
    public boolean addDrink(String name, double price, String description, String category, String imagePath, int drawableId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DRINK_NAME, name);
        values.put(COLUMN_DRINK_PRICE, price);
        values.put(COLUMN_DRINK_DESCRIPTION, description);
        values.put(COLUMN_DRINK_CATEGORY, category);
        values.put(COLUMN_DRINK_IMAGE, imagePath);
        values.put("drawable_id", drawableId);
        long result = db.insert(TABLE_DRINKS, null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<Drink> getAllDrinks() {
        ArrayList<Drink> drinkList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DRINKS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double price = cursor.getDouble(2);
                String description = cursor.getString(3);
                String category = cursor.getString(4);
                String imagePath = cursor.getString(5);
                int drawableId = cursor.getInt(6);

                drinkList.add(new Drink(id, name, price, description, category, imagePath, drawableId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return drinkList;
    }

    public ArrayList<Drink> getDrinksByCategory(String category) {
        ArrayList<Drink> drinkList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DRINKS + " WHERE " + COLUMN_DRINK_CATEGORY + "=?", new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double price = cursor.getDouble(2);
                String description = cursor.getString(3);
                String cat = cursor.getString(4);
                String imagePath = cursor.getString(5);
                int drawableId = cursor.getInt(6);

                drinkList.add(new Drink(id, name, price, description, cat, imagePath, drawableId));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return drinkList;
    }

    public boolean updateDrink(int id, String name, double price, String description, String imagePath, int drawableId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DRINK_NAME, name);
        values.put(COLUMN_DRINK_PRICE, price);
        values.put(COLUMN_DRINK_DESCRIPTION, description);
        values.put(COLUMN_DRINK_IMAGE, imagePath);
        values.put("drawable_id", drawableId);
        int result = db.update(TABLE_DRINKS, values, COLUMN_DRINK_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public boolean deleteDrink(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_DRINKS, COLUMN_DRINK_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ==================== TOPPINGS ====================
    public ArrayList<Topping> getAllToppings() {
        ArrayList<Topping> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TOPPINGS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double price = cursor.getDouble(2);
                list.add(new Topping(id, name, price));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // ==================== PROMOTIONS ====================
    public boolean addPromotion(String code, String description, int discount,
                                String startDate, String endDate, boolean isActive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROMO_CODE, code);
        values.put(COLUMN_PROMO_DESCRIPTION, description);
        values.put(COLUMN_PROMO_DISCOUNT, discount);
        values.put(COLUMN_PROMO_START_DATE, startDate);
        values.put(COLUMN_PROMO_END_DATE, endDate);
        values.put(COLUMN_PROMO_ACTIVE, isActive ? 1 : 0);
        long result = db.insert(TABLE_PROMOTIONS, null, values);
        db.close();
        return result != -1;
    }

    public ArrayList<Promotion> getAllPromotions() {
        ArrayList<Promotion> promotionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROMOTIONS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String code = cursor.getString(1);
                String description = cursor.getString(2);
                int discount = cursor.getInt(3);
                String startDate = cursor.getString(4);
                String endDate = cursor.getString(5);
                boolean isActive = cursor.getInt(6) == 1;

                promotionList.add(new Promotion(id, code, description, discount,
                        startDate, endDate, isActive));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return promotionList;
    }

    public Promotion getPromotionByCode(String code) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROMOTIONS +
                        " WHERE " + COLUMN_PROMO_CODE + "=? AND " + COLUMN_PROMO_ACTIVE + "=1",
                new String[]{code});

        Promotion promotion = null;
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            String promoCode = cursor.getString(1);
            String description = cursor.getString(2);
            int discount = cursor.getInt(3);
            String startDate = cursor.getString(4);
            String endDate = cursor.getString(5);
            boolean isActive = cursor.getInt(6) == 1;

            promotion = new Promotion(id, promoCode, description, discount,
                    startDate, endDate, isActive);
        }
        cursor.close();
        db.close();
        return promotion;
    }

    private void insertSampleDrinks(SQLiteDatabase db) {
        String[][] drinks = {
                {"Cà Phê Đen Đá", "25000", "Cà phê đen đậm đà", "Cà Phê", ""},
                {"Cà Phê Sữa Đá", "30000", "Cà phê sữa ngọt ngào", "Cà Phê", ""},
                {"Matcha Latte", "45000", "Matcha latte Nhật Bản", "Nước Khác", ""},
                {"Trà Sữa Trân Châu", "35000", "Trà sữa thơm ngon với trân châu đen", "Trà Sữa", ""},
                {"Trà Đào Cam Sả", "38000", "Trà đào cam sả mát lạnh", "Trà Sữa", ""},
                {"Sinh Tố Bơ", "40000", "Sinh tố bơ béo ngậy", "Nước Khác", ""},
                {"Nước Ép Cam Tươi", "35000", "Nước ép cam tươi 100%", "Nước Khác", ""},
                {"Soda Chanh Dây", "28000", "Soda chanh dây sảng khoái", "Nước Khác", ""}
        };

        for (String[] drink : drinks) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_DRINK_NAME, drink[0]);     // ← Lấy tên
            values.put(COLUMN_DRINK_PRICE, Double.parseDouble(drink[1]));
            values.put(COLUMN_DRINK_DESCRIPTION, drink[2]);
            values.put(COLUMN_DRINK_CATEGORY, drink[3]);
            values.put(COLUMN_DRINK_IMAGE, drink[4]);
            db.insert(TABLE_DRINKS, null, values);
        }

    }

    private void insertDefaultUsers(SQLiteDatabase db) {
        // Admin user
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USER_NAME, "Admin");
        adminValues.put(COLUMN_USER_EMAIL, "minh@gmail.com");
        adminValues.put(COLUMN_USER_PASSWORD, "1");
        adminValues.put(COLUMN_USER_PHONE, "1234567890");
        adminValues.put(COLUMN_USER_ADDRESS, "HN");
        adminValues.put("role", "admin");  //
        db.insert(TABLE_USERS, null, adminValues);
    }


    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT role FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USER_EMAIL + "=?", new String[]{email});

        String role = "user";  // Default
        if (cursor.moveToFirst()) {
            try {
                role = cursor.getString(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        return role;
    }




    private void insertSamplePromotions(SQLiteDatabase db) {
        String[][] promotions = {
                {"WELCOME20", "Chào mừng khách hàng mới", "20", "2025-10-01", "2025-12-31", "1"},
                {"SUMMER30", "Khuyến mãi mùa hè", "30", "2025-10-15", "2025-11-30", "1"},
                {"FREESHIP", "Miễn phí giao hàng", "10", "2025-10-20", "2025-11-20", "1"},
                {"WEEKEND15", "Giảm giá cuối tuần", "15", "2025-10-24", "2025-10-27", "1"}
        };

        for (String[] promo : promotions) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_PROMO_CODE, promo[0]);
            values.put(COLUMN_PROMO_DESCRIPTION, promo[1]);
            values.put(COLUMN_PROMO_DISCOUNT, Integer.parseInt(promo[2]));
            values.put(COLUMN_PROMO_START_DATE, promo[3]);
            values.put(COLUMN_PROMO_END_DATE, promo[4]);
            values.put(COLUMN_PROMO_ACTIVE, Integer.parseInt(promo[5]));
            db.insert(TABLE_PROMOTIONS, null, values);
        }
    }

    private void insertSampleToppings(SQLiteDatabase db) {
        String[][] toppings = {
                {"Trân châu", "5000"},
                {"Thạch", "3000"},
                {"Sương sáo", "4000"},
                {"Nước cốt dừa", "3000"},
                {"Đậu đỏ", "2000"}
        };

        for (String[] topping : toppings) {
            try {
                ContentValues values = new ContentValues();
                values.put("name", topping[0]);
                values.put("price", Double.parseDouble(topping[1]));
                db.insert(TABLE_TOPPINGS, null, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public double getTotalRevenue() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_ORDER_TOTAL + ") FROM " + TABLE_ORDERS, null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    public int getTotalOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ORDERS, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getTotalUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_USERS, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_NAME + ", " + COLUMN_USER_EMAIL +
                ", " + COLUMN_USER_PHONE + " FROM " + TABLE_USERS, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String email = cursor.getString(1);
                String phone = cursor.getString(2);
                userList.add("👤 " + name + " - " + email + " (" + (phone != null && !phone.isEmpty() ? phone : "Chưa cập nhật") + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userList;
    }



    public ArrayList<String> getAllOrdersAdmin() {
        ArrayList<String> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " ORDER BY " + COLUMN_ORDER_DATE + " DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String date = cursor.getString(1);
                double total = cursor.getDouble(2);
                String status = cursor.getString(3);
                String email = cursor.getString(4);
                orderList.add(String.format("Đơn #%03d - %,.0f đ - %s - %s", id, total, status, email));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orderList;
    }

    // ← THÊM FULL METHOD NÀY
    public boolean updatePromotion(int id, String code, String description, int discount,
                                   String startDate, String endDate, boolean isActive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROMO_CODE, code);
        values.put(COLUMN_PROMO_DESCRIPTION, description);
        values.put(COLUMN_PROMO_DISCOUNT, discount);
        values.put(COLUMN_PROMO_START_DATE, startDate);
        values.put(COLUMN_PROMO_END_DATE, endDate);
        values.put(COLUMN_PROMO_ACTIVE, isActive ? 1 : 0);
        int result = db.update(TABLE_PROMOTIONS, values, COLUMN_PROMO_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ← THÊM METHOD NÀY NỮA
    public boolean deletePromotion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PROMOTIONS, COLUMN_PROMO_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // ← THÊMTHESE METHODS
    public boolean updateUserRole(String email, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("role", role);
        int result = db.update(TABLE_USERS, values, COLUMN_USER_EMAIL + "=?", new String[]{email});
        db.close();
        return result > 0;
    }

    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_USERS, COLUMN_USER_EMAIL + "=?", new String[]{email});
        db.close();
        return result > 0;
    }



}
