package me.fragment.customtools;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.fragment.customtools.listeners.VanillaListeners;
import me.fragment.customtools.managers.ItemsManager;

public class CustomTools extends JavaPlugin {

	private static CustomTools instance;
	private static FileConfiguration ymlConfig;

	private static SmartColor smartColor;

	private static ItemsManager itemsManager;

	@Override
	public void onEnable() {
		instance = this;
		ymlConfig = this.getConfig();
		this.saveDefaultConfig();

		smartColor = new SmartColor();
		itemsManager = new ItemsManager();

		getServer().getPluginManager().registerEvents(new VanillaListeners(), this);
	}

	@Override
	public void onDisable() {

	}

	public static CustomTools getInstance() {
		return instance;
	}

	public static FileConfiguration getYMLConfig() {
		return ymlConfig;
	}

	public static SmartColor getSmartColor() {
		return smartColor;
	}

	public static ItemsManager getItemsManager() {
		return itemsManager;
	}

}
