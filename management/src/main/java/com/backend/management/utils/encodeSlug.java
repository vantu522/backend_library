import java.text.Normalizer;

private String encodeSlug(String original) {
    // Chuyển đổi chữ hoa thành chữ thường
    String slug = original.toLowerCase();
    // Loại bỏ dấu tiếng Việt
    slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
    slug = slug.replaceAll("\\p{M}", "");
    // Thay thế dấu cách và dấu '/' bằng dấu '-'
    slug = slug.replace(" ", "-").replace("/", "-");
    return slug;
}

public void main() {
}
