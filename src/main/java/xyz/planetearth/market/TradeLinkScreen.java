package xyz.planetearth.market;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public final class TradeLinkScreen extends Screen {
    private final TradeApiClient api;
    private final Screen parent;
    private TradeApiClient.LinkSession link;
    private String status = "연결 코드를 준비하는 중...";
    private int ticks;
    private boolean polling;

    public TradeLinkScreen(TradeApiClient api, Screen parent) {
        super(Text.literal("Discord 계정 연결"));
        this.api = api;
        this.parent = parent;
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(Text.literal("포털 다시 열기"), button -> {
            if (link != null) Util.getOperatingSystem().open(link.url());
        }).dimensions(width / 2 - 80, 135, 160, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("취소"), button -> client.setScreen(parent))
                .dimensions(width / 2 - 80, 165, 160, 20).build());
        MinecraftClient minecraft = client;
        if (minecraft != null && minecraft.player != null) {
            api.startLink(minecraft.player.getUuidAsString(), minecraft.player.getName().getString()).thenAccept(session -> minecraft.execute(() -> {
                link = session;
                status = "코드 " + session.code() + "를 포털에서 Discord 로그인 후 연결하세요.";
                Util.getOperatingSystem().open(session.url());
            })).exceptionally(error -> {
                minecraft.execute(() -> status = "연결 코드를 발급하지 못했습니다. 잠시 후 다시 시도하세요.");
                return null;
            });
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (link == null || polling || ++ticks % 20 != 0) return;
        polling = true;
        api.pollLink(link).thenAccept(linked -> client.execute(() -> {
            polling = false;
            if (linked) {
                status = "연결 완료!";
                client.setScreen(new TradeCreateScreen(api, parent));
            }
        })).exceptionally(error -> {
            client.execute(() -> polling = false);
            return null;
        });
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 45, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal(status), width / 2, 85, 0xD0D0D0);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("브라우저에서 Discord 로그인을 완료하면 자동으로 진행됩니다."), width / 2, 105, 0xA0A0A0);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}
