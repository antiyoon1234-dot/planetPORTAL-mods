package xyz.planetearth.market;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class PlanetEarthMarketClient implements ClientModInitializer {
    private static KeyBinding openMarketKey;
    private static TradeApiClient api;

    @Override
    public void onInitializeClient() {
        api = new TradeApiClient("https://vdndejdmepjigbrssict.supabase.co/functions/v1/trade-link");
        openMarketKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.planetearth_market.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                "category.planetearth_market"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMarketKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new TradeMarketScreen(api));
                }
            }
        });
    }

    public static void sendStatusMessage(net.minecraft.client.MinecraftClient client, String message) {
        if (client.player != null) {
            client.player.sendMessage(Text.literal(message), true);
        }
    }
}
