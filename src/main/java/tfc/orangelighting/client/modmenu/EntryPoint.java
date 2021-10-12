package tfc.orangelighting.client.modmenu;

import com.google.gson.JsonObject;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
import tfc.orangelighting.client.MixinHandler;
import tfc.orangelighting.client.OrangeLightingClient;

import java.util.Map;

public class EntryPoint implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
			return (screen) -> {
				ConfigBuilder impl = ConfigBuilder.create()
						.setTitle(new TranslatableText("config.title.orangelighting"));
				
				if (MinecraftClient.getInstance().world != null) impl.transparentBackground();
				
				ConfigCategory current = impl.getOrCreateCategory(new TranslatableText("config.title.orangelighting.current"));
				
				ConfigEntryBuilder entryBuilder = impl.entryBuilder();
				
				current.addEntry(
						entryBuilder.startBooleanToggle(new TranslatableText("config.orangelighting.enable"), getBool("enabled", true))
								.setDefaultValue(true)
								.setSaveConsumer((value) -> {
									if (OrangeLightingClient.config.has("enabled"))
										OrangeLightingClient.config.remove("enabled");
									OrangeLightingClient.config.addProperty("enabled", value);
								})
								.build()
				);
				current.addEntry(
						entryBuilder.startBooleanToggle(new TranslatableText("config.orangelighting.modify_gamma"), getBool("modify_gamma", true))
								.setDefaultValue(true)
								.setSaveConsumer((value) -> {
									if (OrangeLightingClient.config.has("modify_gamma"))
										OrangeLightingClient.config.remove("modify_gamma");
									OrangeLightingClient.config.addProperty("modify_gamma", value);
								})
								.setTooltip(new TranslatableText("config.orangelighting.blo"))
								.build()
				);
				
				current.addEntry(
						entryBuilder.startFloatField(new TranslatableText("config.orangelighting.gamma_divisor"), getFloat("gammaDivisor", 3f))
								.setDefaultValue(3f)
								.setSaveConsumer((value) -> {
									if (OrangeLightingClient.config.has("gammaDivisor"))
										OrangeLightingClient.config.remove("gammaDivisor");
									OrangeLightingClient.config.addProperty("gammaDivisor", value);
								})
								.setTooltip(new TranslatableText("config.orangelighting.blo"))
								.build()
				);
				
				current.addEntry(
						entryBuilder.startColorField(new TranslatableText("config.orangelighting.color_mul_a"), OrangeLightingClient.getColorA())
								.setDefaultValue(() -> new MixinHandler.Color(156, 121, 93, 0).getRGB())
								.setSaveConsumer((value) -> {
									MixinHandler.Color color = new MixinHandler.Color(value);
									if (OrangeLightingClient.config.has("color_a"))
										OrangeLightingClient.config.remove("color_a");
									JsonObject object = new JsonObject();
									// yes
									object.addProperty("r", color.getBlue() / 128d);
									object.addProperty("g", color.getGreen() / 128d);
									object.addProperty("b", color.getRed() / 128d);
									OrangeLightingClient.config.add("color_a", object);
								})
								.setTooltip(new TranslatableText("config.orangelighting.brighter"))
								.build()
				);
				
				current.addEntry(
						entryBuilder.startColorField(new TranslatableText("config.orangelighting.color_mul_b"), OrangeLightingClient.getColorB())
								.setDefaultValue(() -> new MixinHandler.Color(166, 153, 153, 0).getRGB())
								.setSaveConsumer((value) -> {
									MixinHandler.Color color = new MixinHandler.Color(value);
									if (OrangeLightingClient.config.has("color_b"))
										OrangeLightingClient.config.remove("color_b");
									JsonObject object = new JsonObject();
									// yes
									object.addProperty("r", color.getBlue() / 64d);
									object.addProperty("g", color.getGreen() / 64d);
									object.addProperty("b", color.getRed() / 64d);
									OrangeLightingClient.config.add("color_b", object);
								})
								.build()
				);
				
				impl.setSavingRunnable(OrangeLightingClient::saveConfig);
				return impl.setParentScreen(screen).build();
			};
		}
		return ModMenuApi.super.getModConfigScreenFactory();
	}
	
	private static boolean getBool(String name, boolean defaultVal) {
		if (OrangeLightingClient.config.has(name)) return OrangeLightingClient.config.get(name).getAsBoolean();
		return defaultVal;
	}
	
	private static float getFloat(String name, float defaultVal) {
		if (OrangeLightingClient.config.has(name)) return OrangeLightingClient.config.get(name).getAsFloat();
		return defaultVal;
	}
	
	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		return ModMenuApi.super.getProvidedConfigScreenFactories();
	}
}
