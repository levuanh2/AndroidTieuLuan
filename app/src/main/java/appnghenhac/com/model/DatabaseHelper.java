package appnghenhac.com.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import appnghenhac.com.utils.PasswordUtils;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "music_app.db";
    private static final int DATABASE_VERSION = 7; // Tăng version lên 6 để hỗ trợ bảng mới

    public static final String TABLE_USER = "User";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FULLNAME = "fullname";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ROLE = "role";

    // Bảng Artists
    public static final String TABLE_ARTISTS = "Artists";
    public static final String COLUMN_ARTIST_NAME = "name";

    // Bảng Genres
    public static final String TABLE_GENRES = "Genres";
    public static final String COLUMN_GENRE_NAME = "name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    private Context context;
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng User
        String createUserTable = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_FULLNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ROLE + " TEXT)";
        db.execSQL(createUserTable);

        // Tạo bảng UpgradeRequest
        String createUpgradeRequestTable = "CREATE TABLE UpgradeRequest (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "email TEXT, " +
                "status TEXT DEFAULT 'pending')";
        db.execSQL(createUpgradeRequestTable);

        // Tạo bảng Songs
        String createSongsTable = "CREATE TABLE Songs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "artist TEXT," +
                "duration TEXT," +
                "audioUri TEXT," +
                "album TEXT," +
                "genre TEXT," +
                "coverUri TEXT," +
            "playCount INTEGER DEFAULT 0)";
        db.execSQL(createSongsTable);

        // Tạo bảng Artists
        String createArtistsTable = "CREATE TABLE " + TABLE_ARTISTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ARTIST_NAME + " TEXT UNIQUE)";
        db.execSQL(createArtistsTable);

        // Tạo bảng Genres
        String createGenresTable = "CREATE TABLE " + TABLE_GENRES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_GENRE_NAME + " TEXT UNIQUE)";
        db.execSQL(createGenresTable);

        // Tạo tài khoản admin mặc định
        String adminEmail = "admin@musicapp.com";
        String adminFullname = "Admin";
        String adminPassword = PasswordUtils.hashPassword("admin123");
        String adminRole = "admin";

        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_FULLNAME, adminFullname);
        adminValues.put(COLUMN_EMAIL, adminEmail);
        adminValues.put(COLUMN_PASSWORD, adminPassword);
        adminValues.put(COLUMN_ROLE, adminRole);
        db.insert(TABLE_USER, null, adminValues);

        // Thêm dữ liệu mẫu cho Artists và Genres (tùy chọn)
        ContentValues artistValues = new ContentValues();
        artistValues.put(COLUMN_ARTIST_NAME, "Unknown Artist");
        db.insert(TABLE_ARTISTS, null, artistValues);

        ContentValues genreValues = new ContentValues();
        genreValues.put(COLUMN_GENRE_NAME, "Unknown Genre");
        db.insert(TABLE_GENRES, null, genreValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL("DROP TABLE IF EXISTS Songs");
            db.execSQL("DROP TABLE IF EXISTS UpgradeRequest");
            String createSongsTable = "CREATE TABLE Songs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT," +
                    "artist TEXT," +
                    "duration TEXT," +
                    "audioUri TEXT," +
                    "album TEXT," +
                    "genre TEXT," +
                    "coverUri TEXT)";
            db.execSQL(createSongsTable);
            String createUpgradeRequestTable = "CREATE TABLE UpgradeRequest (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "email TEXT, " +
                    "status TEXT DEFAULT 'pending')";
            db.execSQL(createUpgradeRequestTable);
        }
        if (oldVersion < 3) {
            String createUserTable = "CREATE TABLE IF NOT EXISTS " + TABLE_USER + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_FULLNAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_ROLE + " TEXT)";
            db.execSQL(createUserTable);
        }
        if (oldVersion < 6) {
            // Tạo bảng Artists nếu chưa tồn tại
            String createArtistsTable = "CREATE TABLE " + TABLE_ARTISTS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ARTIST_NAME + " TEXT UNIQUE)";
            db.execSQL(createArtistsTable);

            // Tạo bảng Genres nếu chưa tồn tại
            String createGenresTable = "CREATE TABLE " + TABLE_GENRES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_GENRE_NAME + " TEXT UNIQUE)";
            db.execSQL(createGenresTable);

            // Thêm dữ liệu mẫu cho Artists và Genres
            ContentValues artistValues = new ContentValues();
            artistValues.put(COLUMN_ARTIST_NAME, "Unknown Artist");
            db.insert(TABLE_ARTISTS, null, artistValues);

            ContentValues genreValues = new ContentValues();
            genreValues.put(COLUMN_GENRE_NAME, "Unknown Genre");
            db.insert(TABLE_GENRES, null, genreValues);
        }
        if (oldVersion < 7) {
            // Thêm cột playCount cho bảng Songs
            try {
                db.execSQL("ALTER TABLE Songs ADD COLUMN playCount INTEGER DEFAULT 0");
            } catch (SQLiteException e) {
                Log.w("DatabaseHelper", "Column playCount đã tồn tại hoặc lỗi thêm cột", e);
            }
        }
    }

    // Các phương thức hiện có cho User, UpgradeRequest, Songs
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

    public boolean sendUpgradeRequest(String email) {
        if (email == null) {
            Log.e("DatabaseHelper", "Email is null");
            return false;
        }

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
            return false;
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
            return null;
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

    public ArrayList<User> getAllUsersWithUpgradeRequest() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

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
                    int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
                    int fullnameIndex = cursor.getColumnIndex(COLUMN_FULLNAME);
                    int roleIndex = cursor.getColumnIndex(COLUMN_ROLE);

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
                    if (email != null) {
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

    public boolean updateUserRole(String email, String newRole) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROLE, newRole);

        int rowsAffected = db.update(TABLE_USER, values, COLUMN_EMAIL + " = ?", new String[]{email});

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

        if (rowsAffected > 0 && status.equals("rejected")) {
            db.delete("UpgradeRequest", "email = ?", new String[]{email});
        }

        db.close();
        return rowsAffected > 0;
    }

    public boolean insertSong(Context context, Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", song.getTitle());
        values.put("artist", song.getArtist());
        values.put("album", song.getAlbum());
        values.put("genre", song.getGenre());
        values.put("audioUri", song.getAudioUri());
        values.put("coverUri", song.getCoverUri());

        String duration = "";
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, Uri.parse(song.getAudioUri()));
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            mmr.release();
        } catch (Exception e) {
            Log.e("DatabaseError", "Lỗi khi lấy thời lượng: " + e.getMessage());
        }
        values.put("duration", duration != null ? duration : "");

        long result = db.insert("Songs", null, values);
        if (result == -1) {
            Log.e("DatabaseError", "Thêm bài hát thất bại");
            return false;
        }
        db.close();
        return true;
    }

    public List<Song> getAllSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM Songs";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int artistIndex = cursor.getColumnIndex("artist");
                int albumIndex = cursor.getColumnIndex("album");
                int genreIndex = cursor.getColumnIndex("genre");
                int audioUriIndex = cursor.getColumnIndex("audioUri");
                int coverUriIndex = cursor.getColumnIndex("coverUri");
                if (titleIndex != -1 && artistIndex != -1 && albumIndex != -1 &&
                        genreIndex != -1 && audioUriIndex != -1) {
                    long id = idIndex != -1 ? cursor.getLong(idIndex) : -1;
                    String title = cursor.getString(titleIndex);
                    String artist = cursor.getString(artistIndex);
                    String album = cursor.getString(albumIndex);
                    String genre = cursor.getString(genreIndex);
                    String audioUri = cursor.getString(audioUriIndex);
                    String coverUri = coverUriIndex != -1 ? cursor.getString(coverUriIndex) : null;
                    Song song = new Song(id, title, artist, album, genre, audioUri, coverUri);
                    songs.add(song);
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return songs;
    }

    public boolean updateSong(Song song) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", song.getTitle());
        values.put("artist", song.getArtist());
        values.put("album", song.getAlbum());
        values.put("genre", song.getGenre());
        values.put("audioUri", song.getAudioUri());
        values.put("coverUri", song.getCoverUri());

        int result = db.update("Songs", values, "id = ?", new String[]{String.valueOf(song.getId())});
        db.close();
        return result > 0;
    }

    public boolean deleteSong(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete("Songs", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // Các phương thức quản lý Artists
    public List<String> getAllArtists() {
        List<String> artists = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_ARTIST_NAME + " FROM " + TABLE_ARTISTS, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndex(COLUMN_ARTIST_NAME);
                if (nameIndex != -1) {
                    String artistName = cursor.getString(nameIndex);
                    if (artistName != null) {
                        artists.add(artistName);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return artists;
    }

    public boolean insertArtist(String artistName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ARTIST_NAME, artistName);
        long result = db.insert(TABLE_ARTISTS, null, values);
        db.close();
        return result != -1;
    }

    public boolean updateArtist(String oldArtistName, String newArtistName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ARTIST_NAME, newArtistName);
        int result = db.update(TABLE_ARTISTS, values, COLUMN_ARTIST_NAME + " = ?", new String[]{oldArtistName});
        if (result > 0) {
            // Cập nhật artist trong bảng Songs
            ContentValues songValues = new ContentValues();
            songValues.put("artist", newArtistName);
            db.update("Songs", songValues, "artist = ?", new String[]{oldArtistName});
        }
        db.close();
        return result > 0;
    }

    public boolean deleteArtist(String artistName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ARTISTS, COLUMN_ARTIST_NAME + " = ?", new String[]{artistName});
        if (result > 0) {
            // Cập nhật các bài hát có artist này thành "Unknown Artist"
            ContentValues values = new ContentValues();
            values.put("artist", "Unknown Artist");
            db.update("Songs", values, "artist = ?", new String[]{artistName});
        }
        db.close();
        return result > 0;
    }

    // Các phương thức quản lý Genres
    public List<String> getAllGenres() {
        List<String> genres = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_GENRE_NAME + " FROM " + TABLE_GENRES, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int nameIndex = cursor.getColumnIndex(COLUMN_GENRE_NAME);
                if (nameIndex != -1) {
                    String genreName = cursor.getString(nameIndex);
                    if (genreName != null) {
                        genres.add(genreName);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return genres;
    }

    public boolean insertGenre(String genreName) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();

            // Kiểm tra xem tên thể loại đã tồn tại chưa
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_GENRES + " WHERE " + COLUMN_GENRE_NAME + " = ?", new String[]{genreName});
            if (cursor.getCount() > 0) {
                cursor.close();
                Log.w("DatabaseHelper", "Genre already exists: " + genreName);
                return false; // Trả về false nếu tên đã tồn tại
            }
            cursor.close();

            ContentValues values = new ContentValues();
            values.put(COLUMN_GENRE_NAME, genreName);
            long result = db.insert(TABLE_GENRES, null, values);
            if (result == -1) {
                Log.e("DatabaseHelper", "Failed to insert genre: " + genreName + ". Possible reasons: database locked, constraint violation, or disk full.");
                return false;
            }
            Log.d("DatabaseHelper", "Successfully inserted genre: " + genreName);
            return true;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting genre: " + genreName + ", Error: " + e.getMessage());
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }
    public boolean updateGenre(String oldGenreName, String newGenreName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GENRE_NAME, newGenreName);
        int result = db.update(TABLE_GENRES, values, COLUMN_GENRE_NAME + " = ?", new String[]{oldGenreName});
        if (result > 0) {
            // Cập nhật genre trong bảng Songs
            ContentValues songValues = new ContentValues();
            songValues.put("genre", newGenreName);
            db.update("Songs", songValues, "genre = ?", new String[]{oldGenreName});
        }
        db.close();
        return result > 0;
    }

    public boolean deleteGenre(String genreName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_GENRES, COLUMN_GENRE_NAME + " = ?", new String[]{genreName});
        if (result > 0) {
            // Cập nhật các bài hát có genre này thành "Unknown Genre"
            ContentValues values = new ContentValues();
            values.put("genre", "Unknown Genre");
            db.update("Songs", values, "genre = ?", new String[]{genreName});
        }
        db.close();
        return result > 0;
    }

    public void saveCurrentUserEmail(String email) {
        SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("email", email).apply();
    }

    public boolean isUserExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM User WHERE email = ?", new String[]{email});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }
    public List<Song> getSuggestedSongs() {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Truy vấn dữ liệu từ bảng "songs"
        Cursor cursor = db.query("songs", null, null, null, null, null, "id DESC");

        // Kiểm tra nếu cursor không null và có dữ liệu
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Kiểm tra cột có hợp lệ không (giá trị getColumnIndex phải >= 0)
                int idIndex = cursor.getColumnIndex("id");
                int titleIndex = cursor.getColumnIndex("title");
                int artistIndex = cursor.getColumnIndex("artist");
                int albumIndex = cursor.getColumnIndex("album");
                int genreIndex = cursor.getColumnIndex("genre");
                int audioUriIndex = cursor.getColumnIndex("audioUri");
                int coverUriIndex = cursor.getColumnIndex("coverUri");

                if (idIndex >= 0 && titleIndex >= 0 && artistIndex >= 0 && albumIndex >= 0 &&
                        genreIndex >= 0 && audioUriIndex >= 0 && coverUriIndex >= 0) {
                    // Nếu tất cả các chỉ số cột hợp lệ, tạo đối tượng Song
                    Song song = new Song(
                            cursor.getLong(idIndex),
                            cursor.getString(titleIndex),
                            cursor.getString(artistIndex),
                            cursor.getString(albumIndex),
                            cursor.getString(genreIndex),
                            cursor.getString(audioUriIndex),
                            cursor.getString(coverUriIndex)
                    );
                    songs.add(song);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return songs;
    }
    public List<Song> getLatestSongs() {
        return getSuggestedSongs(); // Ví dụ, lấy chung cho mọi loại bài hát.
    }

    public List<Song> getFeaturedSongs() {
        return getSuggestedSongs(); // Cũng tương tự cho "nổi bật".
    }
    public List<Song> getSongsSortedByPlayCount() {
        List<Song> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Songs ORDER BY playCount DESC", null);
        if (c.moveToFirst()) {
            do {
                // đọc dữ liệu vào Song và thêm vào list
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

}