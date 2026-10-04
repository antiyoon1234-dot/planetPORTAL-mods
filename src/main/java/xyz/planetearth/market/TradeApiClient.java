package xyz.planetearth.market;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Portal API boundary. Network code stays outside screens so the UI never blocks
 * the Minecraft client thread. The first UI build uses demo data until the
 * authenticated Edge Function endpoint is configured.
 */
public final class TradeApiClient {
    private static final String SUPABASE_URL = "https://vdndejdmepjigbrssict.supabase.co";
    // This is the public browser anon key, never a service-role key.
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZkbmRlamRtZXBqaWdicnNzaWN0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzAwOTY0OTMsImV4cCI6MjA4NTY3MjQ5M30.etDWkOByafeAbYIof7wbv7rJF-9Z5RpnI-29okNzleI";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String endpoint;
    private volatile String accessToken;

    public TradeApiClient(String endpoint) {
        this.endpoint = endpoint;
    }

    public CompletableFuture<List<TradeListing>> loadListings() {
        HttpRequest request = HttpRequest.newBuilder(URI.create(SUPABASE_URL + "/rest/v1/trade_listings?select=listing_type,item_code,item_name,quantity,unit_price,seller_name,note&status=eq.open&order=created_at.desc&limit=200"))
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Authorization", "Bearer " + SUPABASE_ANON_KEY)
                .GET()
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() / 100 != 2) {
                        throw new IllegalStateException("trade listings request failed: " + response.statusCode());
                    }
                    JsonArray rows = JsonParser.parseString(response.body()).getAsJsonArray();
                    return rows.asList().stream().map(TradeApiClient::toListing).toList();
                });
    }

    private static TradeListing toListing(JsonElement element) {
        JsonObject row = element.getAsJsonObject();
        return new TradeListing(
                row.get("listing_type").getAsString(),
                row.get("item_code").getAsString(),
                row.get("item_name").getAsString(),
                row.get("quantity").getAsInt(),
                row.get("unit_price").getAsLong(),
                row.get("seller_name").getAsString(),
                row.get("note").getAsString()
        );
    }

    public CompletableFuture<LinkSession> startLink(String minecraftUuid, String minecraftName) {
        JsonObject body = new JsonObject();
        body.addProperty("action", "start");
        body.addProperty("minecraftUuid", minecraftUuid);
        body.addProperty("minecraftName", minecraftName);
        return post(body).thenApply(result -> new LinkSession(result.get("code").getAsString(), result.get("token").getAsString(), result.get("url").getAsString()));
    }

    public CompletableFuture<Boolean> pollLink(LinkSession link) {
        JsonObject body = new JsonObject();
        body.addProperty("action", "status");
        body.addProperty("code", link.code());
        body.addProperty("token", link.token());
        return post(body).thenApply(result -> {
            boolean linked = result.get("linked").getAsBoolean();
            if (linked) accessToken = result.get("accessToken").getAsString();
            return linked;
        });
    }

    public boolean isLinked() {
        return accessToken != null;
    }

    public CompletableFuture<Void> createListing(String type, String itemId, String itemName, int quantity, long unitPrice, String note) {
        if (accessToken == null) return CompletableFuture.failedFuture(new IllegalStateException("account linking is required"));
        JsonObject body = new JsonObject();
        body.addProperty("action", "create_listing");
        body.addProperty("accessToken", accessToken);
        body.addProperty("listingType", type);
        body.addProperty("itemId", itemId);
        body.addProperty("itemName", itemName);
        body.addProperty("quantity", quantity);
        body.addProperty("unitPrice", unitPrice);
        body.addProperty("note", note);
        return post(body).thenApply(result -> null);
    }

    private CompletableFuture<JsonObject> post(JsonObject body) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                .header("apikey", SUPABASE_ANON_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
            if (response.statusCode() / 100 != 2) throw new IllegalStateException(response.body());
            return JsonParser.parseString(response.body()).getAsJsonObject();
        });
    }

    public record LinkSession(String code, String token, String url) {
    }

    public String endpoint() {
        return endpoint;
    }
}
