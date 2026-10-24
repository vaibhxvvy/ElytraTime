package yes.mediumdifficulty.elytratime;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ElytraTimeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerEvents();
        registerKeybindings();

        ElytraTime.LOGGER.info("[ElytraTime] Initialised on client");
    }

    private static final KeyMapping.Category ELYTRA_TIME_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("elytratime", "controls"));

    private void registerKeybindings() {
        KeyMapping printTime = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.elytratime.show",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_F4,
                ELYTRA_TIME_CATEGORY
        ));

        KeyMapping openConfig = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.elytratime.config",
                InputConstants.Type.KEYBOARD,
                InputConstants.KEY_O,
                ELYTRA_TIME_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (printTime.consumeClick() && client.player != null) {
                var found = Util.findElytra(client.player);

                if (found.isPresent())
                    client.player.sendSystemMessage(Component.literal(
                            Util.formatTimePercent(found.get(),ClientTextUtils.getTimeReportFormat(), ClientTextUtils.getTimeFormat(), Minecraft.getInstance().level))
                            .withStyle(ChatFormatting.GREEN));
                else
                    client.player.sendSystemMessage(Component.translatable("message.elytratime.no_elytra").withStyle(ChatFormatting.RED));
            }

            if (openConfig.consumeClick()) {
                client.setScreenAndShow(ConfigMenu.build(client.gui.screen()));
            }
        });
    }

    private static void registerEvents() {
        ItemTooltipCallback.EVENT.register((itemStack, context, type, lines) -> {
            try {
                if (FlightEquipment.isFlightEquipment(itemStack) && ElytraTime.config.tooltipEnabled) {
                    var text = Component.literal(
                            Util.formatTimePercent(itemStack, ClientTextUtils.getTooltipFormat(), ClientTextUtils.getTimeFormat(), Minecraft.getInstance().level)
                        ).withStyle(ChatFormatting.GREEN);

                    lines.add(1, text);
                }
            } catch (Throwable t) {
                // This callback runs inside the render path: a failure here (e.g. a
                // corrupted jar or unexpected equipment shape) must never crash the
                // game. Log it and leave the tooltip untouched.
                ElytraTime.LOGGER.error("Elytra Time tooltip failed; skipping it", t);
            }
        });
    }
}
