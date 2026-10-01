package net.fabricmc.example.mixin;

import com.example.freecam.FreecamMod;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void interceptMouseLook(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
        if (FreecamMod.enabled) {
            // Absorb the mouse movement into the freecam variables instead of the player
            FreecamMod.cameraYaw += (float)(cursorDeltaX * 0.15);
            FreecamMod.cameraPitch += (float)(cursorDeltaY * 0.15);
            
            // Clamp pitch to avoid flipping upside down
            FreecamMod.cameraPitch = Math.max(-90.0f, Math.min(90.0f, FreecamMod.cameraPitch));
            
            // Cancel original method so the physical player body doesn't rotate
            ci.cancel();
        }
    }
}
