package tfc.orangelighting.client.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.TranslatableText;
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
				
				ConfigCategory root = impl.getOrCreateCategory(new TranslatableText("config.title.orangelighting.root"));
				
				ConfigEntryBuilder entryBuilder = impl.entryBuilder();
				
				root.addEntry(
						entryBuilder.startBooleanToggle(new TranslatableText("config.orangelighting.enable"), getBool("enabled", true))
								.setDefaultValue(true)
								.setSaveConsumer((value) -> {
									if (OrangeLightingClient.config.has("enabled"))
										OrangeLightingClient.config.remove("enabled");
									OrangeLightingClient.config.addProperty("enabled", value);
								})
								.build()
				);
				root.addEntry(
						entryBuilder.startBooleanToggle(new TranslatableText("config.orangelighting.modify_gamma"), getBool("modify_gamma", true))
								.setDefaultValue(true)
								.setSaveConsumer((value) -> {
									if (OrangeLightingClient.config.has("modify_gamma"))
										OrangeLightingClient.config.remove("modify_gamma");
									OrangeLightingClient.config.addProperty("modify_gamma", value);
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
	
	@Override
	public Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
		return ModMenuApi.super.getProvidedConfigScreenFactories();
	}
}
