package tfc.orangelighting.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

import java.util.function.Function;

public class MixinHandler {
	public static class Color {
		public int value;
		
		public Color(int r, int g, int b, int a) {
			value = ((a & 0xFF) << 24) |
					((r & 0xFF) << 16) |
					((g & 0xFF) << 8) |
					((b & 0xFF));
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
		if (x == 0) return color;
		
		int v = color;
		Color lightColor = new Color(v);
//		float mul = 1 - ((Integer) x / 15f);
		float mul;
		
		ClientWorld clientWorld = MinecraftClient.getInstance().world;
		
		float sky = brightnessGetter.apply(y);
//		float block = brightnessGetter.apply(x);
		float f = clientWorld.method_23783(1.0F);
		if (clientWorld.getLightningTicksLeft() > 0) f = 1f;
		else f = f * 0.95F + 0.05F;
//		if (block < (sky * f)) return v;
		sky *= f;
		float actualSky = sky;
		sky = 1 - sky;

//		mul = 1 - f;
		mul = sky;
		mul -= 0.23;
		mul = Math.max(mul, 0);
		mul += 0.23;
		
		
		boolean vanillaGamma = (OrangeLightingClient.config.getAsJsonPrimitive("modify_gamma").getAsBoolean());
		float gamma = vanillaGamma ? ((float) MinecraftClient.getInstance().options.gamma / OrangeLightingClient.config.getAsJsonPrimitive("gammaDivisor").getAsFloat()) : (float) (MinecraftClient.getInstance().options.gamma);
//		{
//			float f1 = 1.0F - mul;
//			mul *= 1.0F - f1 * f1 * f1 * f1;
//		}
		
		mul = ((1 - ((mul + gamma) / 2f)) + (0.5f)) / 2f;
		mul /= 1.1;
		
		if (y != 0)
//			mul /= (0.5 * OrangeLightingClient.config.getAsJsonPrimitive("mn_skyBrightnessDivisorMultiplier").getAsDouble());
			mul /= OrangeLightingClient.config.getAsJsonPrimitive("mn_skyBrightnessDivisorMultiplier").getAsDouble() * (((int) ((actualSky + 0.2) * 255)) / 255d);
//		mul += (gamma / 1);
//		mul /= 2;
//		mul = 1 - mul;
////		mul += 0.25f * f;
//		mul += 0.5;
//		mul /= 2;
		
		// TODO: options for color multipliers
		Color newColor = new Color(
				Math.min((int) (lightColor.getRed() * (getColorValueR("a", 0.9) / (mul * getColorValueR("b", 2.4)))), lightColor.getRed()),
				Math.min((int) (lightColor.getGreen() * (getColorValueG("a", 1.1) / (mul * getColorValueG("b", 2.4)))), lightColor.getGreen()),
//				Math.min((int) (lightColor.getBlue() * (1.5 / (mul * 2.6))), lightColor.getBlue()),
				Math.min((int) (lightColor.getBlue() * (getColorValueB("a", 1.35) / (mul * getColorValueB("b", 2.6)))), lightColor.getBlue()),
				lightColor.getAlpha()
		);

//		Color skyColor = new Color(
//				(int)(actualSky),
//				(int)(actualSky),
//				(int)(actualSky),
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
	
	private static double getColorValueR(String element, double defaultVale) {
		JsonElement element1 = OrangeLightingClient.config.get("color_" + element);
		if (element1 instanceof JsonObject) {
			if (((JsonObject) element1).has("r")) {
				JsonPrimitive r = ((JsonObject) element1).getAsJsonPrimitive("r");
				return r.getAsDouble();
			}
		}
		return defaultVale;
	}
	
	private static double getColorValueG(String element, double defaultVale) {
		JsonElement element1 = OrangeLightingClient.config.get("color_" + element);
		if (element1 instanceof JsonObject) {
			if (((JsonObject) element1).has("g")) {
				JsonPrimitive r = ((JsonObject) element1).getAsJsonPrimitive("g");
				return r.getAsDouble();
			}
		}
		return defaultVale;
	}
	
	private static double getColorValueB(String element, double defaultVale) {
		JsonElement element1 = OrangeLightingClient.config.get("color_" + element);
		if (element1 instanceof JsonObject) {
			if (((JsonObject) element1).has("b")) {
				JsonPrimitive r = ((JsonObject) element1).getAsJsonPrimitive("b");
				return r.getAsDouble();
			}
		}
		return defaultVale;
	}
	
	// 	public static void modifyLight(Args args, Function<Integer, Float> brightnessGetter) {
	//		int v = args.get(2);
	//		Color lightColor = new Color(v);
	////		float mul = 1 - ((Integer) args.get(0) / 15f);
	//		float mul;
	//
	//		ClientWorld clientWorld = MinecraftClient.getInstance().world;
	//
	//		float sky = brightnessGetter.apply(args.get(0));
	////		float block = brightnessGetter.apply(args.get(0));
	//		float f = clientWorld.method_23783(1.0F);
	//		if (((Integer) args.get(0)).intValue() == 15) return;
	////		if (((Integer) args.get(0)).intValue() == 0) return;
	//		if (clientWorld.getLightningTicksLeft() > 0) f = 1f;
	//		else f = f * 0.95F + 0.05F;
	////		float actualSky = sky;
	//		sky = 1 - sky;
	//		sky *= f;
	////		actualSky *= f;
	//
	////		mul = 1 - f;
	//		mul = sky;
	//		mul -= 0.23;
	//		mul = Math.max(mul, 0);
	//		mul += 0.23;
	//
	//		float gamma = (float) MinecraftClient.getInstance().options.gamma / 3f;
	////		{
	////			float f1 = 1.0F - mul;
	////			mul *= 1.0F - f1 * f1 * f1 * f1;
	////		}
	//
	//		mul = ((1 - ((mul + gamma) / 2f)) + (0.5f)) / 2f;
	//		mul /= 1.1;
	////		mul += (gamma / 1);
	////		mul /= 2;
	////		mul = 1 - mul;
	//////		mul += 0.25f * f;
	////		mul += 0.5;
	////		mul /= 2;
	//
	//		Color newColor = new Color(
	//				Math.min((int) (lightColor.getRed() * (1.1 / (mul * 2.4))), lightColor.getRed()),
	//				Math.min((int) (lightColor.getGreen() * (1.2 / (mul * 2.5))), lightColor.getGreen()),
	////				Math.min((int) (lightColor.getBlue() * (1.5 / (mul * 2.6))), lightColor.getBlue()),
	//				Math.min((int) (lightColor.getBlue() * (1.35 / (mul * 2.6))), lightColor.getBlue()),
	//				lightColor.getAlpha()
	//		);
	//
	////		Color skyColor = new Color(
	////				(int)(actualSky * 255),
	////				(int)(actualSky * 255),
	////				(int)(actualSky * 255),
	////				255
	////		);
	//
	////		System.out.println(skyColor.getRed());
	//
	////		newColor = new Color(
	////				Math.max(newColor.getRed(), skyColor.getRed()),
	////				Math.max(newColor.getGreen(), skyColor.getRed()),
	////				Math.max(newColor.getBlue(), skyColor.getRed()),
	////				lightColor.getAlpha()
	////		);
	//
	////		args.set(2, newColor.getRGB());
	//	}
}
