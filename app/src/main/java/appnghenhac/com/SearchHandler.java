package appnghenhac.com.util;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.widget.SearchView;
import appnghenhac.com.SearchResultsActivity;

public class SearchHandler {

    /**
     * Thiết lập SearchView để xử lý sự kiện tìm kiếm và điều hướng.
     *
     * @param context Context của Activity gọi SearchHandler
     * @param searchView SearchView từ toolbar_menu
     */
    public static void setupSearchView(Context context, SearchView searchView) {
        searchView.setQueryHint("Tìm kiếm bài hát hoặc nghệ sĩ...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Khi người dùng nhấn nút tìm kiếm (submit) trên bàn phím
                Intent intent = new Intent(context, SearchResultsActivity.class);
                intent.putExtra("search_query", query);
                context.startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Không lọc trực tiếp ở đây, chờ submit
                return false;
            }
        });
    }
}