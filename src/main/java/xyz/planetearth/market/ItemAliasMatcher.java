package xyz.planetearth.market;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ItemAliasMatcher {
    private static final Map<String, Set<String>> GROUPS = Map.of(
            "gold", Set.of("금", "골드", "gold"),
            "gold_ingot", Set.of("금괴", "금주괴", "goldingot", "gold_ingot"),
            "gold_block", Set.of("금블럭", "금블록", "금 블럭", "금 블록", "goldblock", "gold_block")
    );

    private ItemAliasMatcher() {
    }

    public static boolean matches(String query, TradeListing listing) {
        String normalizedQuery = normalize(query);
        if (normalizedQuery.isBlank()) {
            return true;
        }
        String itemId = normalize(listing.itemId());
        String itemName = normalize(listing.itemName());
        if (itemId.contains(normalizedQuery) || itemName.contains(normalizedQuery)) {
            return true;
        }
        String queryGroup = groupFor(normalizedQuery);
        if (queryGroup == null) {
            return false;
        }
        if ("gold".equals(queryGroup)) {
            return itemId.contains("gold_ingot") || itemId.contains("gold_block")
                    || itemName.contains("금괴") || itemName.contains("금블럭") || itemName.contains("금블록");
        }
        return queryGroup.equals(groupFor(itemId)) || queryGroup.equals(groupFor(itemName));
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return Normalizer.normalize(value, Normalizer.Form.NFKC)
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\s_\\-]+", "")
                .trim();
    }

    private static String groupFor(String value) {
        String normalized = normalize(value);
        return GROUPS.entrySet().stream()
                .filter(entry -> entry.getValue().stream().map(ItemAliasMatcher::normalize).anyMatch(normalized::equals))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
