package xyz.planetearth.market;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class TradeCreateScreen extends Screen {
    private final TradeApiClient api;
    private final Screen parent;
    private TextFieldWidget itemName;
    private TextFieldWidget quantity;
    private TextFieldWidget price;
    private TextFieldWidget note;
    private String type = "sell";
    private String error = "";

    public TradeCreateScreen(TradeApiClient api, Screen parent) {
        super(Text.literal("거래글 등록"));
        this.api = api;
        this.parent = parent;
    }

    @Override
    protected void init() {
        itemName = field("아이템 이름", 40);
        quantity = field("수량", 70);
        price = field("개당 가격", 100);
        note = field("거래 조건", 130);
        addDrawableChild(ButtonWidget.builder(Text.literal("판매"), button -> type = "sell").dimensions(width / 2 - 155, 165, 75, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("구매"), button -> type = "buy").dimensions(width / 2 - 75, 165, 75, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("등록"), button -> submit()).dimensions(width / 2 + 5, 165, 75, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("취소"), button -> client.setScreen(parent)).dimensions(width / 2 + 85, 165, 75, 20).build());
    }

    private TextFieldWidget field(String placeholder, int y) {
        TextFieldWidget field = new TextFieldWidget(textRenderer, width / 2 - 150, y, 300, 20, Text.literal(placeholder));
        field.setPlaceholder(Text.literal(placeholder));
        addDrawableChild(field);
        return field;
    }

    private void submit() {
        if (itemName.getText().trim().isEmpty()) {
            error = "아이템 이름을 입력해 주세요.";
            return;
        }
        api.createListing(type, "custom:" + ItemAliasMatcher.normalize(itemName.getText()), itemName.getText().trim(),
                parsePositive(quantity.getText(), 1), parsePositiveLong(price.getText(), 1), note.getText().trim())
                .whenComplete((ignored, failure) -> MinecraftClient.getInstance().execute(() -> {
                    if (failure != null) {
                        error = "먼저 웹 포털에서 Discord 계정을 연결해야 등록할 수 있습니다.";
                    } else {
                        MinecraftClient.getInstance().setScreen(parent);
                    }
                }));
    }

    private static int parsePositive(String value, int fallback) {
        try { return Math.max(1, Integer.parseInt(value)); } catch (NumberFormatException ignored) { return fallback; }
    }

    private static long parsePositiveLong(String value, long fallback) {
        try { return Math.max(1, Long.parseLong(value)); } catch (NumberFormatException ignored) { return fallback; }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 18, 0xFFFFFF);
        if (!error.isEmpty()) context.drawCenteredTextWithShadow(textRenderer, Text.literal(error), width / 2, 195, 0xFF7777);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}
