package tfc.orangelighting.client;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.*;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class OrangeLightingClient implements ClientModInitializer {
	public static final JsonObject config;
	
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
	
	public static void saveConfig() {
		writeJson(config, new File("config/orangelighting.json"));
	}
	
	private static void writeJson(JsonObject object, File file) {
		try {
			if (!file.exists()) file.createNewFile();
			OutputStreamWriter streamWriter = new OutputStreamWriter(new FileOutputStream(file));
			JsonWriter writer = gson.newJsonWriter(streamWriter);
			writeElement(object, writer);
			writer.close();
			writer.flush();
		} catch (Throwable ignored) {
		}
	}
	
	private static void writeElement(JsonElement element, JsonWriter writer) throws IOException {
		if (element.isJsonPrimitive()) {
			if (element.getAsJsonPrimitive().isBoolean()) writer.value(element.getAsBoolean());
			else if (element.getAsJsonPrimitive().isString()) writer.value(element.getAsString());
			else if (element.getAsJsonPrimitive().isNumber()) writer.value(element.getAsString());
			else {
				Number n = element.getAsNumber();
				if (n instanceof Long) writer.value(element.getAsLong());
				else if (n instanceof Integer) writer.value(element.getAsInt());
				else if (n instanceof Short) writer.value(element.getAsShort());
				else if (n instanceof Float) writer.value(element.getAsFloat());
				else if (n instanceof Double) writer.value(element.getAsDouble());
				else if (n instanceof Byte) writer.value(element.getAsByte());
			}
		} else if (element.isJsonArray()) {
			writer.beginArray();
			for (JsonElement jsonElement : element.getAsJsonArray()) writeElement(jsonElement, writer);
			writer.endArray();
		} else if (element.isJsonObject()) {
			writer.beginObject();
			for (Map.Entry<String, JsonElement> stringJsonElementEntry : element.getAsJsonObject().entrySet()) {
				writer.name(stringJsonElementEntry.getKey());
				writeElement(stringJsonElementEntry.getValue(), writer);
			}
			writer.endObject();
		} else if (element.isJsonNull()) writer.nullValue();
	}
	
	static {
		config = loadConfig();
		if (!config.has("enabled")) config.addProperty("enabled", true);
		if (!config.has("modify_gamma")) config.addProperty("modify_gamma", false);
		if (!config.has("mn_skyBrightnessDivisorMultiplier"))
			config.addProperty("mn_skyBrightnessDivisorMultiplier", 5);
		if (!config.has("gammaDivisor")) config.addProperty("gammaDivisor", 3);
		if (!new File("config/orangelighting.json").exists()) saveConfig();
		// these are not saved on first boot for precision reasons
		if (!config.has("color_a")) {
			JsonObject object = new JsonObject();
//			object.addProperty("r", 0.9);
//			object.addProperty("g", 1.1);
//			object.addProperty("b", 1.35);

//			object.addProperty("r", 0.9);
//			object.addProperty("g", 1.1);
//			object.addProperty("b", 1.4);

//			object.addProperty("r", 0.609375);
//			object.addProperty("g", 0.78125);
//			object.addProperty("b", 1.0);
			
			object.addProperty("r", 0.7265625);
			object.addProperty("g", 0.9453125);
			object.addProperty("b", 1.21875);
			if (!config.has("color_a")) config.add("color_a", object);
		}
		if (!config.has("color_b")) {
			JsonObject object = new JsonObject();
			object.addProperty("r", 2.4);
//			object.addProperty("r", 0.890625);
			object.addProperty("g", 2.4);
//			object.addProperty("g", 0.890625);
			object.addProperty("b", 2.6);
//			object.addProperty("b", 1.0);
			if (!config.has("color_b")) config.add("color_b", object);
		}
	}
	
	public static JsonObject loadConfig() {
		File file = new File("config/orangelighting.json");
		if (file.exists()) {
			try {
				FileInputStream stream = new FileInputStream(file);
				ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
				int b;
				while ((b = stream.read()) != -1) stream1.write(b);
				stream.close();
				byte[] bytes = stream1.toByteArray();
				stream1.close();
				stream1.flush();
				return gson.fromJson(new String(bytes), JsonObject.class);
			} catch (Throwable ignored) {
			}
		}
		
		JsonObject object = new JsonObject();
		object.addProperty("enabled", true);
		object.addProperty("modify_gamma", true);
		return object;
	}
	
	public static int getColorA() {
		double r = getColorValue("a", "r", 0.9);
		double g = getColorValue("a", "g", 1.1);
		double b = getColorValue("a", "b", 1.35);
		
		return new MixinHandler.Color((int) (b * 128), (int) (g * 128), (int) (r * 128), 0).getRGB();
	}
	
	public static int getColorB() {
		double r = getColorValue("b", "r", 2.4);
		double g = getColorValue("b", "g", 2.4);
		double b = getColorValue("b", "b", 2.6);
		
		return new MixinHandler.Color((int) (b * 64), (int) (g * 64), (int) (r * 64), 0).getRGB();
	}
	
	private static double getColorValue(String element, String channel, double defaultVale) {
		JsonElement element1 = OrangeLightingClient.config.get("color_" + element);
		if (element1 instanceof JsonObject) {
			if (((JsonObject) element1).has(channel)) {
				JsonPrimitive r = ((JsonObject) element1).getAsJsonPrimitive(channel);
				return r.getAsDouble();
			}
		}
		return defaultVale;
	}
	
	@Override
	public void onInitializeClient() {
	}
}
