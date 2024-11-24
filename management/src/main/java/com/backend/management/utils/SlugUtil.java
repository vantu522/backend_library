package com.backend.management.utils;

import java.text.Normalizer;

public class SlugUtil {
    private SlugUtil() {
    }

    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "") // Xóa dấu
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("[^a-z0-9\\s-]", "") // Chỉ giữ lại ký tự chữ, số, khoảng trắng và dấu "-"
                .trim()
                .replaceAll("\\s+", "-"); // Thay khoảng trắng bằng dấu "-"
    }
}


