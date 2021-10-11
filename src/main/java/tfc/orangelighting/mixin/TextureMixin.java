package tfc.orangelighting.mixin;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tfc.orangelighting.client.LightmapTextureInfo;
import tfc.orangelighting.client.MixinHandler;

import java.util.function.Function;

@Mixin(value = NativeImageBackedTexture.class, priority = 20000)
public class TextureMixin implements LightmapTextureInfo {
	@Shadow @Nullable private NativeImage image;
	
	@Unique public boolean isEnabled = false;
	@Unique public Function<Integer, Float> brightnessGetter;
	
	// TODO: https://github.com/grondag/darkness/blob/121ee7bc399b4fbd243d044e429a74275c9ee691/src/main/java/grondag/darkness/mixin/MixinDynamicTexture.java#L35-L52
	@Inject(at = @At("HEAD"), method = "upload")
	public void preUpload(CallbackInfo ci) {
		if (image == null || brightnessGetter == null) return;
		if (isEnabled) {
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					image.setColor(x, y, MixinHandler.modifyLight(
							x, y, image.getColor(x, y),
							brightnessGetter
					));
				}
			}
		}
	}
	
	@Override
	public void OrangeLight_enable() {
		isEnabled = true;
	}
	
	@Override
	public void OrangeLight_setBrightnessGetter(Function<Integer, Float> brightnessGetter) {
		this.brightnessGetter = brightnessGetter;
	}
}
