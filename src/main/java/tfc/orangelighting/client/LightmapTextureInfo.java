package tfc.orangelighting.client;

import java.util.function.Function;

public interface LightmapTextureInfo {
	void OrangeLight_enable();
	void OrangeLight_setBrightnessGetter(Function<Integer, Float> brightnessGetter);
}
