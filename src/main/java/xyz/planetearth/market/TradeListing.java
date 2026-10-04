package xyz.planetearth.market;

public record TradeListing(
        String type,
        String itemId,
        String itemName,
        int quantity,
        long unitPrice,
        String sellerName,
        String note
) {
    public boolean isSelling() {
        return "sell".equals(type);
    }
}
