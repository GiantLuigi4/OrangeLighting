package tfc.orangelighting.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import tfc.orangelighting.client.LightmapTextureInfo;
import tfc.orangelighting.client.MixinHandler;

@Mixin(LightmapTextureManager.class)
public abstract class LightmapTextureManagerMixin {
	@Shadow protected abstract float getBrightness(World world, int lightLevel);
	
	@Shadow @Final private MinecraftClient client;
	
	@Shadow @Final private NativeImageBackedTexture texture;
	
	// TODO: https://github.com/grondag/darkness/blob/1.17/src/main/java/grondag/darkness/mixin/MixinLightTexture.java#L41-L44
	@Inject(at = @At("TAIL"), method = "<init>")
	public void preInit(GameRenderer gameRenderer, MinecraftClient minecraftClient, CallbackInfo ci) {
		((LightmapTextureInfo) texture).OrangeLight_enable();
		((LightmapTextureInfo) texture).OrangeLight_setBrightnessGetter((x)->getBrightness(client.world, x));
	}
}
