package tfc.orangelighting.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
		saveConfig();
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
	
	@Override
	public void onInitializeClient() {
	}
}
