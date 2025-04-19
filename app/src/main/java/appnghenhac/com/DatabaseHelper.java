package appnghenhac.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "music_app.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_USER = "User";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULLNAME = "fullname";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FULLNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ROLE + " TEXT)";

        db.execSQL(createUserTable);
        String createUpgradeRequestTable = "CREATE TABLE UpgradeRequest (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "status TEXT DEFAULT 'pending')"; // status: pending, approved, rejected

        db.execSQL(createUpgradeRequestTable);
        // Tạo tài khoản admin mặc định
        String adminEmail = "admin@musicapp.com";
        String adminFullname = "Admin";
        String adminPassword = PasswordUtils.hashPassword("admin123");
        String adminRole = "admin";

        ContentValues values = new ContentValues();
        values.put(COLUMN_FULLNAME, adminFullname);
        values.put(COLUMN_EMAIL, adminEmail);
        values.put(COLUMN_PASSWORD, adminPassword);
        values.put(COLUMN_ROLE, adminRole);

        db.insert(TABLE_USER, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS UpgradeRequest");
        onCreate(db);
    }

    public boolean insertUser(String fullname, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULLNAME, fullname);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);
        long result = db.insert(TABLE_USER, null, values);
        db.close();
        return result != -1;
    }

    public String checkLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER,
                new String[]{COLUMN_ROLE},
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password},
                null, null, null);

        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        } else {
            cursor.close();
            return null;
        }
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{COLUMN_ROLE}, COLUMN_EMAIL + "=?",
                new String[]{email}, null, null, null);
        String role = "user"; // Mặc định
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
            }
            cursor.close();
        }
        db.close();
        return role;
    }

    public boolean checkUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, null, COLUMN_EMAIL + "=?",
                new String[]{email}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Gửi yêu cầu nâng cấp
    public boolean sendUpgradeRequest(String email) {
        if (email == null) {
            Log.e("DatabaseHelper", "Email is null");
            return false;
        }

        // Kiểm tra xem email có tồn tại trong bảng User không
        if (!checkUserExists(email)) {
            Log.e("DatabaseHelper", "Email " + email + " does not exist in User table");
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM UpgradeRequest WHERE email = ?", new String[]{email});
        if (cursor.getCount() > 0) {
            cursor.close();
            db.close();
            Log.w("DatabaseHelper", "Upgrade request already exists for email: " + email);
            return false; // Đã có yêu cầu, không thể gửi lại
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("status", "pending");

        long result = db.insert("UpgradeRequest", null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert upgrade request for email: " + email);
        } else {
            Log.d("DatabaseHelper", "Successfully inserted upgrade request for email: " + email);
        }
        db.close();
        return result != -1;
    }

    // Lấy trạng thái yêu cầu nâng cấp của người dùng
    public String getUpgradeRequestStatus(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT status FROM UpgradeRequest WHERE email = ?", new String[]{email});
            if (cursor.moveToFirst()) {
                String status = cursor.getString(0);
                cursor.close();
                return status;
            }
            cursor.close();
            return null; // Nếu không có yêu cầu nào
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null; // Trả về null nếu có lỗi
        } finally {
            db.close();
        }
    }

    // Lấy danh sách người dùng có yêu cầu nâng cấp
    public ArrayList<User> getAllUsersWithUpgradeRequest() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Kiểm tra dữ liệu trong bảng UpgradeRequest
        Cursor checkCursor = db.rawQuery("SELECT * FROM UpgradeRequest WHERE status = 'pending'", null);
        Log.d("DatabaseHelper", "Number of pending upgrade requests: " + checkCursor.getCount());
        if (checkCursor.moveToFirst()) {
            do {
                int emailIndex = checkCursor.getColumnIndex("email");
                int statusIndex = checkCursor.getColumnIndex("status");
                if (emailIndex != -1 && statusIndex != -1) {
                    String email = checkCursor.getString(emailIndex);
                    String status = checkCursor.getString(statusIndex);
                    Log.d("DatabaseHelper", "UpgradeRequest - Email: " + email + ", Status: " + status);
                }
            } while (checkCursor.moveToNext());
        }
        checkCursor.close();

        // Sử dụng bí danh để đảm bảo tên cột trong kết quả
        String query = "SELECT u." + COLUMN_EMAIL + " AS " + COLUMN_EMAIL + "," +
                " u." + COLUMN_FULLNAME + " AS " + COLUMN_FULLNAME + "," +
                " u." + COLUMN_ROLE + " AS " + COLUMN_ROLE +
                " FROM " + TABLE_USER + " u" +
                " LEFT JOIN UpgradeRequest ur ON u." + COLUMN_EMAIL + " = ur.email" +
                " WHERE ur.status = 'pending'";

        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    // Kiểm tra các cột trước khi lấy dữ liệu
                    int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
                    int fullnameIndex = cursor.getColumnIndex(COLUMN_FULLNAME);
                    int roleIndex = cursor.getColumnIndex(COLUMN_ROLE);

                    // Log nếu cột không tồn tại
                    if (emailIndex == -1) {
                        Log.e("DatabaseHelper", "Column " + COLUMN_EMAIL + " not found in query result");
                        continue;
                    }
                    if (fullnameIndex == -1) {
                        Log.e("DatabaseHelper", "Column " + COLUMN_FULLNAME + " not found in query result");
                        continue;
                    }
                    if (roleIndex == -1) {
                        Log.e("DatabaseHelper", "Column " + COLUMN_ROLE + " not found in query result");
                        continue;
                    }

                    String email = cursor.getString(emailIndex);
                    String fullname = cursor.getString(fullnameIndex);
                    String role = cursor.getString(roleIndex);
                    if (email != null) { // Chỉ thêm nếu email không null
                        users.add(new User(email, fullname, role));
                        Log.d("DatabaseHelper", "Added user with upgrade request: " + email);
                    }
                } while (cursor.moveToNext());
            } else {
                Log.d("DatabaseHelper", "No users with pending upgrade requests found");
            }
            cursor.close();
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e("DatabaseHelper", "Error fetching users with upgrade requests: " + e.getMessage());
        } finally {
            db.close();
        }
        return users;
    }

    // Cập nhật vai trò người dùng
    public boolean updateUserRole(String email, String newRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROLE, newRole);

        int rowsAffected = db.update(TABLE_USER, values, COLUMN_EMAIL + " = ?", new String[]{email});

        // Xóa yêu cầu nâng cấp sau khi duyệt
        if (rowsAffected > 0) {
            db.delete("UpgradeRequest", "email = ?", new String[]{email});
        }

        db.close();
        return rowsAffected > 0;
    }

    public boolean updateUpgradeRequestStatus(String email, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);

        int rowsAffected = db.update("UpgradeRequest", values, "email = ?", new String[]{email});

        // Xóa yêu cầu nếu bị từ chối
        if (rowsAffected > 0 && status.equals("rejected")) {
            db.delete("UpgradeRequest", "email = ?", new String[]{email});
        }

        db.close();
        return rowsAffected > 0;
    }
}