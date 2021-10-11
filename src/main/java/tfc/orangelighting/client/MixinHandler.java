package tfc.orangelighting.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Function;

public class MixinHandler {
	private static class Color {
		public int value;
		
		public Color(int r, int g, int b) {
			this(r, g, b, 255);
		}
		
		public Color(int r, int g, int b, int a) {
			value = ((a & 0xFF) << 24) |
					((r & 0xFF) << 16) |
					((g & 0xFF) << 8) |
					((b & 0xFF));
		}
		
		public Color(int rgba, boolean hasalpha) {
			if (hasalpha) value = rgba;
			else value = 0xff000000 | rgba;
		}
		
		public Color(int rgb) {
			this.value = rgb;
		}
		
		public int getRed() {
			return (getRGB() >> 16) & 0xFF;
		}
		
		public int getGreen() {
			return (getRGB() >> 8) & 0xFF;
		}
		
		public int getBlue() {
			return (getRGB()) & 0xFF;
		}
		
		public int getAlpha() {
			return (getRGB() >> 24) & 0xff;
		}
		
		public int getRGB() {
			return value;
		}
	}
	
	public static int modifyLight(int x, int y, int color, Function<Integer, Float> brightnessGetter) {
		if (x == 15) return color;
		
		int v = color;
		Color lightColor = new Color(v);
//		float mul = 1 - ((Integer) x / 15f);
		float mul;
		
		ClientWorld clientWorld = MinecraftClient.getInstance().world;
		
		float sky = brightnessGetter.apply(x);
//		float block = brightnessGetter.apply(x);
		float f = clientWorld.method_23783(1.0F);
//		if (((Integer) x).intValue() == 0) return;
		if (clientWorld.getLightningTicksLeft() > 0) f = 1f;
		else f = f * 0.95F + 0.05F;
//		float actualSky = sky;
		sky = 1 - sky;
		sky *= f;
//		actualSky *= f;

//		mul = 1 - f;
		mul = sky;
		mul -= 0.23;
		mul = Math.max(mul, 0);
		mul += 0.23;
		
		float gamma = (float) MinecraftClient.getInstance().options.gamma / 3f;
//		{
//			float f1 = 1.0F - mul;
//			mul *= 1.0F - f1 * f1 * f1 * f1;
//		}
		
		mul = ((1 - ((mul + gamma) / 2f)) + (0.5f)) / 2f;
		mul /= 1.1;
//		mul += (gamma / 1);
//		mul /= 2;
//		mul = 1 - mul;
////		mul += 0.25f * f;
//		mul += 0.5;
//		mul /= 2;
		
		Color newColor = new Color(
				Math.min((int) (lightColor.getRed() * (0.9 / (mul * 2.4))), lightColor.getRed()),
				Math.min((int) (lightColor.getGreen() * (1.1 / (mul * 2.4))), lightColor.getGreen()),
//				Math.min((int) (lightColor.getBlue() * (1.5 / (mul * 2.6))), lightColor.getBlue()),
				Math.min((int) (lightColor.getBlue() * (1.35 / (mul * 2.6))), lightColor.getBlue()),
				lightColor.getAlpha()
		);

//		Color skyColor = new Color(
//				(int)(actualSky * 255),
//				(int)(actualSky * 255),
//				(int)(actualSky * 255),
//				255
//		);

//		System.out.println(skyColor.getRed());

//		newColor = new Color(
//				Math.max(newColor.getRed(), skyColor.getRed()),
//				Math.max(newColor.getGreen(), skyColor.getRed()),
//				Math.max(newColor.getBlue(), skyColor.getRed()),
//				lightColor.getAlpha()
//		);
		
		return newColor.getRGB();
//		return v;
	}
}
