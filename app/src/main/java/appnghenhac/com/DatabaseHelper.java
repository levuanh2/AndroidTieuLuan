package appnghenhac.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "music_app.db";
    private static final int DATABASE_VERSION = 1;

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
    }// Gửi yêu cầu nâng cấp
    public boolean sendUpgradeRequest(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra nếu người dùng đã có yêu cầu chưa
        Cursor cursor = db.rawQuery("SELECT * FROM UpgradeRequest WHERE email = ?", new String[]{email});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false; // Đã có yêu cầu, không thể gửi lại
        }

        // Thêm yêu cầu mới
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("status", "pending"); // Trạng thái ban đầu là 'pending'

        long result = db.insert("UpgradeRequest", null, values);
        cursor.close();
        return result != -1;
    }

    // Lấy trạng thái yêu cầu nâng cấp của người dùng
    public String getUpgradeRequestStatus(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT status FROM UpgradeRequest WHERE email = ?", new String[]{email});

        if (cursor.moveToFirst()) {
            String status = cursor.getString(0);
            cursor.close();
            return status;
        }
        cursor.close();
        return null; // Nếu không có yêu cầu nào
    }
    // Lấy danh sách người dùng có yêu cầu nâng cấp
    public ArrayList<User> getAllUsersWithUpgradeRequest() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn lấy người dùng từ bảng User có yêu cầu nâng cấp trạng thái 'pending'
        String query = "SELECT u." + COLUMN_EMAIL + ", u." + COLUMN_FULLNAME + ", u." + COLUMN_ROLE +
                " FROM " + TABLE_USER + " u" +
                " INNER JOIN UpgradeRequest ur ON u." + COLUMN_EMAIL + " = ur.email" +
                " WHERE ur.status = 'pending'";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
                String fullname = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FULLNAME));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLE));
                users.add(new User(email, fullname, role));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
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
