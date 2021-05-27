package me.fragment.customtools;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;

public class SmartColor {

	private Map<String, ChatColor> colorsMap = new HashMap<String, ChatColor>();

	public SmartColor() {
		for (String key : CustomTools.getYMLConfig().getConfigurationSection("smart-colors").getKeys(false)) {
			this.colorsMap.put(key, ChatColor.valueOf(CustomTools.getYMLConfig().getConfigurationSection("smart-colors").getString(key)));
		}
	}

	public ChatColor getColor(String str) {
		return this.colorsMap.entrySet().stream().filter(entry -> str.toLowerCase().contains(entry.getKey().toLowerCase())).map(entry -> entry.getValue()).findFirst().orElse(null);
	}

}
