package me.fragment.customtools.config;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;

import me.fragment.customtools.CustomTools;
import me.fragment.customtools.utils.Utils;

public class Config {

	private static List<Material> blacklistTools = CustomTools.getInstance().getConfig().getStringList("tools.blacklist").stream().map(Material::valueOf).collect(Collectors.toList());
	private static String maxLevelReached = Utils.color(CustomTools.getInstance().getConfig().getString("messages.max-level-reached"));

	public static List<Material> getBlacklistTools() {
		return blacklistTools;
	}

	public static String getMaxLevelReached() {
		return maxLevelReached;
	}

}
