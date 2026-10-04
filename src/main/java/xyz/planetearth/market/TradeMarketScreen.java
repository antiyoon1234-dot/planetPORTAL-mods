package xyz.planetearth.market;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class TradeMarketScreen extends Screen {
    private final TradeApiClient api;
    private final List<TradeListing> listings = new ArrayList<>();
    private TextFieldWidget search;
    private String status = "거래글을 불러오는 중...";

    public TradeMarketScreen(TradeApiClient api) {
        super(Text.literal("플래닛어스 거래소"));
        this.api = api;
    }

    @Override
    protected void init() {
        search = new TextFieldWidget(textRenderer, width / 2 - 150, 38, 300, 20, Text.literal("아이템 검색"));
        search.setPlaceholder(Text.literal("금, 금괴, 금 블럭 등"));
        addDrawableChild(search);
        addDrawableChild(ButtonWidget.builder(Text.literal("거래 등록"), button -> client.setScreen(api.isLinked() ? new TradeCreateScreen(api, this) : new TradeLinkScreen(api, this)))
                .dimensions(width / 2 + 160, 38, 90, 20).build());
        api.loadListings().thenAccept(result -> client.execute(() -> {
            listings.clear();
            listings.addAll(result);
            status = listings.size() + "개의 거래글";
        })).exceptionally(error -> {
            client.execute(() -> status = "거래글을 불러오지 못했습니다. 포털 연결을 확인하세요.");
            return null;
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 14, 0xFFFFFF);
        context.drawTextWithShadow(textRenderer, Text.literal(status), 20, 72, 0xA0A0A0);
        int y = 92;
        String query = search == null ? "" : search.getText();
        for (TradeListing listing : listings) {
            if (!ItemAliasMatcher.matches(query, listing)) {
                continue;
            }
            int color = listing.isSelling() ? 0x8BC34A : 0x64B5F6;
            context.fill(20, y - 4, width - 20, y + 38, 0xCC1E1E1E);
            context.drawTextWithShadow(textRenderer, Text.literal((listing.isSelling() ? "판매 " : "구매 ") + listing.itemName()), 30, y, color);
            context.drawTextWithShadow(textRenderer, Text.literal(listing.quantity() + "개 · " + listing.unitPrice() + " G · " + listing.sellerName()), 30, y + 16, 0xD0D0D0);
            y += 50;
            if (y > height - 35) {
                break;
            }
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
