package me.fragment.customtools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.fragment.customtools.config.Config;
import me.fragment.customtools.serializer.xseries.XEnchantment;
import me.fragment.customtools.utils.DoublePair;
import me.fragment.customtools.utils.TriPair;
import me.fragment.customtools.utils.Utils;

public class CustomItem extends ItemStack {

	private static Map<Material, TriPair<Level, Enchants, DoublePair<String, List<String>>>> settings = new HashMap<Material, TriPair<Level, Enchants, DoublePair<String, List<String>>>>();

	private static TriPair<Level, Enchants, DoublePair<String, List<String>>> defaultSettings = new TriPair<CustomItem.Level, CustomItem.Enchants, DoublePair<String, List<String>>>(
			new Level(CustomTools.getYMLConfig().getConfigurationSection("tools.settings.default.levels")),
			new Enchants(CustomTools.getYMLConfig().getConfigurationSection("tools.settings.default.enchantements")), new DoublePair<String, List<String>>(
					CustomTools.getYMLConfig().getString("tools.settings.default.item.displayname"), CustomTools.getYMLConfig().getStringList("tools.settings.default.item.lores")));

	public CustomItem(ItemStack item) {
		super(item);

		if (!this.isCustomItem()) {
			initItem();
		}
	}

	// Item Section
	public void initItem() {
		ItemMeta meta = this.getItemMeta();

		meta.getPersistentDataContainer().set(new NamespacedKey(CustomTools.getInstance(), "customTools"), PersistentDataType.STRING, "true");
		meta.getPersistentDataContainer().set(new NamespacedKey(CustomTools.getInstance(), "exp"), PersistentDataType.INTEGER, 0);
		this.setItemMeta(meta);

		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		meta.setDisplayName((getDisplayname().contains("%smart-color%") ? CustomTools.getSmartColor().getColor(this.getType().toString()) : "") + getDisplayname()
				.replace("%smart-color%", "").replace("%item-displayname%", WordUtils.capitalize(this.getType().toString().replace('_', ' ').toLowerCase(Locale.ENGLISH))));

		List<String> enchantsLines = meta.getEnchants().entrySet().stream()
				.map(entry -> getLores().stream().filter(line -> line.contains("%repeat-enchantements%")).findFirst().get()
						.replace("%enchant-displayname%", XEnchantment.matchXEnchantment(entry.getKey()).getDisplayName())
						.replace("%enchant-level%", Utils.IntegerToRomanNumeral(entry.getValue())).replace("%repeat-enchantements%", ""))
				.collect(Collectors.toList());

		meta.setLore(Utils.applyPlaceholders(getLores(),
				Arrays.asList(new DoublePair<String, List<String>>("%repeat-enchantements%", enchantsLines),
						new DoublePair<String, List<String>>("%xp-line%",
								(isMaxLevel() ? Arrays.asList(Config.getMaxLevelReached())
										: getLores().stream().filter(line -> line.contains("%xp-line%")).map(line -> line.replace("%xp-line%", "")).collect(Collectors.toList())))),
				Arrays.asList(new DoublePair<String, String>("%require-exp%", Utils.compactNumber(getSettings().getFirst().getXP(getLevel() + 1) - this.getExp())),
						new DoublePair<String, String>("%next-lvl%", String.valueOf(getLevel() + 1)), new DoublePair<String, String>("%gems-displayname%", ""),
						new DoublePair<String, String>("%repeat-gems%", ""))));

		this.setItemMeta(meta);
	}

	public void updateLore() {
		ItemMeta meta = this.getItemMeta();

		List<String> enchantsLines = meta.getEnchants().entrySet().stream()
				.map(entry -> getLores().stream().filter(line -> line.contains("%repeat-enchantements%")).findFirst().get()
						.replace("%enchant-displayname%", XEnchantment.matchXEnchantment(entry.getKey()).getDisplayName())
						.replace("%enchant-level%", Utils.IntegerToRomanNumeral(entry.getValue())).replace("%repeat-enchantements%", ""))
				.collect(Collectors.toList());

		meta.setLore(Utils.applyPlaceholders(getLores(),
				Arrays.asList(new DoublePair<String, List<String>>("%repeat-enchantements%", enchantsLines),
						new DoublePair<String, List<String>>("%xp-line%",
								(isMaxLevel() ? Arrays.asList(Config.getMaxLevelReached())
										: getLores().stream().filter(line -> line.contains("%xp-line%")).map(line -> line.replace("%xp-line%", "")).collect(Collectors.toList())))),
				Arrays.asList(new DoublePair<String, String>("%require-exp%", Utils.compactNumber(getSettings().getFirst().getXP(getLevel() + 1) - this.getExp())),
						new DoublePair<String, String>("%next-lvl%", String.valueOf(getLevel() + 1)), new DoublePair<String, String>("%gems-displayname%", ""),
						new DoublePair<String, String>("%repeat-gems%", ""))));

		this.setItemMeta(meta);
	}

	// Settings Section
	public TriPair<Level, Enchants, DoublePair<String, List<String>>> getSettings() {
		if (!settings.containsKey(this.getType())) {
			CustomTools.getYMLConfig().getConfigurationSection("tools.settings").getKeys(false).stream().filter(key -> key.equalsIgnoreCase(this.getType().toString())).findFirst()
					.ifPresentOrElse(key -> {
						boolean hasLevelSection = CustomTools.getYMLConfig().getConfigurationSection("tools.settings." + key).getKeys(false).stream()
								.anyMatch(section -> section.equalsIgnoreCase("levels"));
						boolean hasEnchantSection = CustomTools.getYMLConfig().getConfigurationSection("tools.settings." + key).getKeys(false).stream()
								.anyMatch(section -> section.equalsIgnoreCase("enchantements"));
						boolean hasDisplaynameSection = CustomTools.getYMLConfig().getConfigurationSection("tools.settings." + key + ".item").getKeys(false).stream()
								.anyMatch(section -> section.equalsIgnoreCase("displayname"));
						boolean hasLoreSection = CustomTools.getYMLConfig().getConfigurationSection("tools.settings." + key + ".item").getKeys(false).stream()
								.anyMatch(section -> section.equalsIgnoreCase("lores"));

						settings.put(this.getType(), new TriPair<Level, Enchants, DoublePair<String, List<String>>>(
								hasLevelSection ? new Level(CustomTools.getYMLConfig().getConfigurationSection("tools.settings." + key + ".levels")) : defaultSettings.getFirst(),
								hasEnchantSection ? new Enchants(CustomTools.getYMLConfig().getConfigurationSection("tools.settings." + key + ".enchantements"))
										: defaultSettings.getSecond(),
								new DoublePair<String, List<String>>(
										hasDisplaynameSection ? CustomTools.getYMLConfig().getString("tools.settings." + key + ".item.displayname")
												: defaultSettings.getThird().getFirst(),
										hasLoreSection ? CustomTools.getYMLConfig().getStringList("tools.settings." + key + ".item.lores") : defaultSettings.getThird().getSecond())));
					}, () -> {
						settings.put(this.getType(), defaultSettings);
					});
		}

		return settings.get(this.getType());
	}

	public String getDisplayname() {
		return Utils.color(getSettings().getThird().getFirst());
	}

	public List<String> getLores() {
		return Utils.color(new ArrayList<String>(getSettings().getThird().getSecond()));
	}

	// Exp Section
	public int getLevel() {
		return (int) Math.cbrt(getExp() * getSettings().getFirst().getDivider() / 4.0);
	}

	public int getExp() {
		return this.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(CustomTools.getInstance(), "exp"), PersistentDataType.INTEGER);
	}

	public boolean isMaxLevel() {
		return this.getLevel() >= getSettings().getFirst().getMaxLevel();
	}

	public void setExp(int exp) {
		ItemMeta meta = this.getItemMeta();
		meta.getPersistentDataContainer().set(new NamespacedKey(CustomTools.getInstance(), "exp"), PersistentDataType.INTEGER, exp);
		this.setItemMeta(meta);

		this.applyEnchants();
		this.updateLore();
	}

	public void addExp(int exp) {
		this.setExp(exp + this.getExp());
	}

	// Enchants Section
	public void applyEnchants() {
		ItemMeta meta = this.getItemMeta();

		getSettings().getSecond().getEnchants().entrySet().stream().filter(entry -> entry.getKey().parseEnchantment().canEnchantItem(this)).forEach(entry -> {
			Enchantment enchantment = entry.getKey().parseEnchantment();
			DoublePair<Integer, Integer> settings = entry.getValue();

			if (getSettings().getFirst().getMaxLevel() / settings.getSecond() > 0) {
				meta.addEnchant(enchantment, this.getLevel() / (getSettings().getFirst().getMaxLevel() / settings.getSecond()), true);
			}
		});

		this.setItemMeta(meta);
	}

	// Check Section
	public boolean isCustomItem() {
		return this.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(CustomTools.getInstance(), "customTools"), PersistentDataType.STRING);
	}

	// Static Section
	public static CustomItem fromItemStack(ItemStack item) {
		return new CustomItem(item);
	}

	public static class Level {

		private int max;
		private int maxExp;

		public Level(int max, int maxExp) {
			this.max = max;
			this.maxExp = maxExp;
		}

		public Level(ConfigurationSection section) {
			this.max = section.getInt("max");
			this.maxExp = section.getInt("max-exp");
		}

		public int getXP(int level) {
			return (int) Math.round((4.0 * Math.pow(level, 3)) / getDivider());
		}

		public double getDivider() {
			return (4.0 * Math.pow(getMaxLevel(), 3)) / getMaxExp();
		}

		public int getMaxLevel() {
			return max;
		}

		public void setMaxLevel(int max) {
			this.max = max;
		}

		public int getMaxExp() {
			return maxExp;
		}

		public void setMaxExp(int maxExp) {
			this.maxExp = maxExp;
		}

	}

	public static class Enchants {

		private Map<XEnchantment, DoublePair<Integer, Integer>> enchants = new HashMap<XEnchantment, DoublePair<Integer, Integer>>();

		public Enchants(Map<XEnchantment, DoublePair<Integer, Integer>> enchants) {
			this.enchants = enchants;
		}

		public Enchants(ConfigurationSection section) {
			for (String key : section.getKeys(false)) {
				if (!XEnchantment.matchXEnchantment(key).isPresent()) {
					CustomTools.getInstance().getLogger().log(java.util.logging.Level.WARNING, "Enchantment " + key + " is not valid and has been ignored");
					continue;
				}

				this.enchants.put(XEnchantment.matchXEnchantment(key).get(), new DoublePair<Integer, Integer>(section.getInt(key + ".min-lvl"), section.getInt(key + ".max-lvl")));
			}
		}

		public Map<XEnchantment, DoublePair<Integer, Integer>> getEnchants() {
			return enchants;
		}

		public void setEnchants(Map<XEnchantment, DoublePair<Integer, Integer>> enchants) {
			this.enchants = enchants;
		}

		public DoublePair<Integer, Integer> getEnchant(XEnchantment xEnchant) {
			return this.enchants.getOrDefault(xEnchant, null);
		}

		public boolean hasEnchant(XEnchantment xEnchant) {
			return this.enchants.containsKey(xEnchant);
		}

	}

}
