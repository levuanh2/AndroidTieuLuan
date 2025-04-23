package appnghenhac.com.utils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import appnghenhac.com.model.Song;

public class SearchUtil {

    /**
     * Lọc danh sách bài hát dựa trên từ khóa (hỗ trợ không dấu và tìm kiếm gần đúng).
     *
     * @param songs Danh sách bài hát gốc
     * @param query Từ khóa tìm kiếm
     * @return Danh sách bài hát đã lọc
     */
    public static List<Song> filterSongs(List<Song> songs, String query) {
        List<Song> filteredSongs = new ArrayList<>();

        // Nếu không có từ khóa, trả về danh sách gốc
        if (query == null || query.trim().isEmpty()) {
            return songs != null ? new ArrayList<>(songs) : new ArrayList<>();
        }

        // Nếu danh sách gốc rỗng hoặc null, trả về danh sách rỗng
        if (songs == null || songs.isEmpty()) {
            return filteredSongs;
        }

        // Chuyển từ khóa thành không dấu và chữ thường
        String lowerQuery = removeDiacritics(query).toLowerCase();
        int maxDistance = Math.max(2, query.length() / 3); // Giới hạn khoảng cách Levenshtein

        // Lọc bài hát
        for (Song song : songs) {
            String title = song.getTitle() != null ? song.getTitle() : "";
            String artist = song.getArtist() != null ? song.getArtist() : "";

            // Chuyển tiêu đề và nghệ sĩ thành không dấu và chữ thường
            String lowerTitle = removeDiacritics(title).toLowerCase();
            String lowerArtist = removeDiacritics(artist).toLowerCase();

            // Kiểm tra tìm kiếm không dấu
            boolean matchesTitle = lowerTitle.contains(lowerQuery);
            boolean matchesArtist = lowerArtist.contains(lowerQuery);

            // Nếu không khớp chính xác, kiểm tra tìm kiếm gần đúng
            if (!matchesTitle && !matchesArtist) {
                matchesTitle = isApproximateMatch(lowerTitle, lowerQuery, maxDistance);
                matchesArtist = isApproximateMatch(lowerArtist, lowerQuery, maxDistance);
            }

            // Nếu khớp với tiêu đề hoặc nghệ sĩ, thêm vào danh sách kết quả
            if (matchesTitle || matchesArtist) {
                filteredSongs.add(song);
            }
        }

        return filteredSongs;
    }

    /**
     * Chuyển đổi chuỗi có dấu thành không dấu.
     *
     * @param text Chuỗi đầu vào
     * @return Chuỗi không dấu
     */
    private static String removeDiacritics(String text) {
        if (text == null) return "";
        // Chuẩn hóa chuỗi về dạng phân tách (decomposed)
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        // Loại bỏ các ký tự dấu (diacritical marks)
        return normalized.replaceAll("\\p{M}", "");
    }

    /**
     * Kiểm tra tìm kiếm gần đúng bằng khoảng cách Levenshtein.
     *
     * @param source Chuỗi nguồn (tiêu đề hoặc nghệ sĩ không dấu)
     * @param target Chuỗi đích (từ khóa không dấu)
     * @param maxDistance Khoảng cách tối đa cho phép
     * @return True nếu hai chuỗi gần giống nhau
     */
    private static boolean isApproximateMatch(String source, String target, int maxDistance) {
        if (source == null || target == null) return false;

        // Chia chuỗi thành các từ
        String[] sourceWords = source.split("\\s+");
        String[] targetWords = target.split("\\s+");

        // So sánh từng từ trong từ khóa với từng từ trong chuỗi nguồn
        for (String targetWord : targetWords) {
            if (targetWord.isEmpty()) continue;
            for (String sourceWord : sourceWords) {
                if (sourceWord.isEmpty()) continue;
                int distance = levenshteinDistance(sourceWord, targetWord);
                if (distance <= maxDistance) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Tính khoảng cách Levenshtein giữa hai chuỗi.
     *
     * @param s1 Chuỗi 1
     * @param s2 Chuỗi 2
     * @return Khoảng cách Levenshtein
     */
    private static int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        // Tạo ma trận để tính khoảng cách
        int[][] dp = new int[len1 + 1][len2 + 1];

        // Khởi tạo hàng và cột đầu tiên
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        // Tính khoảng cách
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[len1][len2];
    }
}