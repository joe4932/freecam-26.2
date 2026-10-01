package com.example.freecam;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class FreecamMod implements ClientModInitializer {
    public static boolean enabled = false;
    
    // Virtual camera properties
    public static Vec3d cameraPos = Vec3d.ZERO;
    public static float cameraYaw = 0.0f;
    public static float cameraPitch = 0.0f;

    private static KeyBinding toggleKey;
    private static final double SPEED = 0.5;

    @Override
    public void onInitializeClient() {
        // Bind to F1 key (Note: F1 normally hides GUI, you may want GLFW.GLFW_KEY_X instead)
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.freecam.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F1,
                "category.freecam"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Handle Toggling
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (enabled) {
                    // Initialize camera at the player's current position
                    cameraPos = client.player.getPos().add(0, client.player.getEyeHeight(client.player.getPose()), 0);
                    cameraYaw = client.player.getYaw();
                    cameraPitch = client.player.getPitch();
                }
            }

            // Handle Camera Movement when active
            if (enabled) {
                handleMovement(client);
            }
        });
    }

    private void handleMovement(MinecraftClient client) {
        // Calculate forward/backward vectors based on camera angle
        double radYaw = Math.toRadians(cameraYaw);
        Vec3d forward = new Vec3d(-Math.sin(radYaw), 0, Math.cos(radYaw));
        Vec3d sideways = new Vec3d(forward.z, 0, -forward.x);

        if (client.options.forwardKey.isPressed()) cameraPos = cameraPos.add(forward.multiply(SPEED));
        if (client.options.backKey.isPressed()) cameraPos = cameraPos.add(forward.multiply(-SPEED));
        if (client.options.leftKey.isPressed()) cameraPos = cameraPos.add(sideways.multiply(-SPEED));
        if (client.options.rightKey.isPressed()) cameraPos = cameraPos.add(sideways.multiply(SPEED));
        
        // Up and Down using Jump/Sneak keys
        if (client.options.jumpKey.isPressed()) cameraPos = cameraPos.add(0, SPEED, 0);
        if (client.options.sneakKey.isPressed()) cameraPos = cameraPos.add(0, -SPEED, 0);
    }
}
