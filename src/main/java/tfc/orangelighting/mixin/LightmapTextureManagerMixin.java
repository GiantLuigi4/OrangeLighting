package tfc.orangelighting.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.orangelighting.client.MixinHandler;

@Mixin(value = LightmapTextureManager.class, priority = 20000)
public abstract class LightmapTextureManagerMixin {
	@Shadow
	protected abstract float getBrightness(World world, int lightLevel);
	
	@Shadow
	@Final
	private MinecraftClient client;
	
	@Shadow
	@Final
	private NativeImage image;
	
	@Inject(at = @At(value = "INVOKE", shift = At.Shift.BEFORE, target = "Lnet/minecraft/client/texture/NativeImageBackedTexture;upload()V"), method = "update")
	public void preUpdate(float delta, CallbackInfo ci) {
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				image.setColor(x, y, MixinHandler.modifyLight(
						x, y, image.getColor(x, y),
						(x1) -> getBrightness(client.world, x1)
				));
			}
		}
	}
}
