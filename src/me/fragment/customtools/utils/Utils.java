package me.fragment.customtools.utils;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class Utils {

	public static String color(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	public static List<String> color(List<String> input) {
		for (int i = 0; i < input.size(); i++) {
			input.set(i, color(input.get(i)));
		}

		return input;
	}

	public static ItemStack applyPlaceholders(ItemStack itemstack, List<DoublePair<String, String>> placeholders) {
		List<String> lores = itemstack.getItemMeta().getLore();
		String displayName = itemstack.getItemMeta().getDisplayName();

		for (int i = 0; i < lores.size(); i++) {
			for (DoublePair<String, String> placeholder : placeholders) {
				lores.set(i, lores.get(i).replace(placeholder.getFirst(), placeholder.getSecond()));
			}
		}

		for (DoublePair<String, String> placeholder : placeholders) {
			displayName = displayName.replace(placeholder.getFirst(), placeholder.getSecond());
		}

		return new ItemBuilder(itemstack).clone().setName(displayName).setLore(lores).toItemStack();
	}

	public static ItemStack applyPlaceholders(ItemStack itemstack, List<DoublePair<String, List<String>>> multiLinesPlaceholders, List<DoublePair<String, String>> placeholders) {
		List<String> lores = itemstack.getItemMeta().getLore();
		String displayName = itemstack.getItemMeta().getDisplayName();

		for (int i = 0; i < lores.size(); i++) {
			for (DoublePair<String, List<String>> placeholder : multiLinesPlaceholders) {
				if (lores.contains(placeholder.getFirst())) {
					lores.remove(i);
					lores.addAll(i, placeholder.getSecond());
				}
			}
		}

		for (int i = 0; i < lores.size(); i++) {
			for (DoublePair<String, String> placeholder : placeholders) {
				lores.set(i, lores.get(i).replace(placeholder.getFirst(), placeholder.getSecond()));
			}
		}

		for (DoublePair<String, String> placeholder : placeholders) {
			displayName = displayName.replace(placeholder.getFirst(), placeholder.getSecond());
		}

		return new ItemBuilder(itemstack).clone().setName(displayName).setLore(lores).toItemStack();
	}

	public static List<String> applyPlaceholders(List<String> lines, List<DoublePair<String, String>> placeholders) {
		for (int i = 0; i < lines.size(); i++) {
			for (DoublePair<String, String> placeholder : placeholders) {
				lines.set(i, lines.get(i).replace(placeholder.getFirst(), placeholder.getSecond()));
			}
		}

		return lines;
	}

	public static List<String> applyPlaceholders(List<String> lines, List<DoublePair<String, List<String>>> multiLinesPlaceholders, List<DoublePair<String, String>> placeholders) {
		for (int i = 0; i < lines.size(); i++) {
			for (DoublePair<String, List<String>> placeholder : multiLinesPlaceholders) {
				if (lines.get(i).contains(placeholder.getFirst())) {
					lines.remove(i);
					lines.addAll(i, placeholder.getSecond());
				}
			}
		}

		for (int i = 0; i < lines.size(); i++) {
			for (DoublePair<String, String> placeholder : placeholders) {
				lines.set(i, lines.get(i).replace(placeholder.getFirst(), placeholder.getSecond()));
			}
		}

		return lines;
	}

	public static String timeFormatter(long time) {
		String format = "";

		// int days = 0;
		int hours = 0;
		int minutes = 0;
		int secondes = 0;

		/*
		 * while (time / 86400 >= 1) { time -= 86400; days++; }
		 */
		while (time / 3600 >= 1) {
			time -= 3600;
			hours++;
		}
		while (time / 60 >= 1) {
			time -= 60;
			minutes++;
		}
		while (time / 1 >= 1) {
			time -= 1;
			secondes++;
		}

		format = /*
					 * (days > 0 ? String.valueOf(days) + " d " : "") +
					 */(hours > 0 ? String.valueOf(hours) + "h " : "") + (minutes > 0 ? String.valueOf(minutes) + "m " : "") + (secondes > 0 ? String.valueOf(secondes) + "s " : "");

		if (format.equals("")) {
			format = "0s ";
		}

		return format.substring(0, format.length() - 1);
	}

	private static final NavigableMap<Double, String> suffixes = new TreeMap<>();
	static {
		suffixes.put(1000.0, "k");
		suffixes.put(1000000.0, "M");
		suffixes.put(1000000000.0, "G");
		suffixes.put(1000000000000.0, "T");
		suffixes.put(1000000000000000.0, "P");
		suffixes.put(1000000000000000000.0, "E");
	}

	private static final NavigableMap<Long, String> intSuffixes = new TreeMap<>();
	static {
		intSuffixes.put(1000L, "k");
		intSuffixes.put(1000000L, "M");
		intSuffixes.put(1000000000L, "G");
		intSuffixes.put(1000000000000L, "T");
		intSuffixes.put(1000000000000000L, "P");
		intSuffixes.put(1000000000000000000L, "E");
	}

	public static String compactNumber(double value) {
		// Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
		if (value == Double.MIN_VALUE)
			return compactNumber(Double.MIN_VALUE + 1);
		if (value < 0)
			return "-" + compactNumber(-value);
		if (value < 1000)
			return Double.toString(value); // deal with easy case

		Entry<Double, String> e = suffixes.floorEntry(value);
		Double divideBy = e.getKey();
		String suffix = e.getValue();

		double truncated = value / (divideBy / 10); // the number part of the output times 10
		boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
		return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
	}

	public static String compactNumber(long value) {
		// Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
		if (value == Long.MIN_VALUE)
			return compactNumber(Long.MIN_VALUE + 1);
		if (value < 0)
			return "-" + compactNumber(-value);
		if (value < 1000)
			return Long.toString(value); // deal with easy case

		Entry<Long, String> e = intSuffixes.floorEntry(value);
		Long divideBy = e.getKey();
		String suffix = e.getValue();

		double truncated = value / (divideBy / 10); // the number part of the output times 10
		boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
		return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
	}

	public static String IntegerToRomanNumeral(int input) {
		if (input < 1 || input > 3999)
			return "Invalid Roman Number Value";
		String s = "";
		while (input >= 1000) {
			s += "M";
			input -= 1000;
		}
		while (input >= 900) {
			s += "CM";
			input -= 900;
		}
		while (input >= 500) {
			s += "D";
			input -= 500;
		}
		while (input >= 400) {
			s += "CD";
			input -= 400;
		}
		while (input >= 100) {
			s += "C";
			input -= 100;
		}
		while (input >= 90) {
			s += "XC";
			input -= 90;
		}
		while (input >= 50) {
			s += "L";
			input -= 50;
		}
		while (input >= 40) {
			s += "XL";
			input -= 40;
		}
		while (input >= 10) {
			s += "X";
			input -= 10;
		}
		while (input >= 9) {
			s += "IX";
			input -= 9;
		}
		while (input >= 5) {
			s += "V";
			input -= 5;
		}
		while (input >= 4) {
			s += "IV";
			input -= 4;
		}
		while (input >= 1) {
			s += "I";
			input -= 1;
		}
		return s;
	}

	public static String makePrettyStringFromEnum(final String enumString, boolean allCaps) {
		String[] split = enumString.split("_");

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < split.length; i++) {
			final String s = split[i];

			if (!allCaps) {
				sb.append(s.substring(0, 1) + s.substring(1).toLowerCase());
			} else {
				sb.append(s);
			}

			if ((i + 1) < split.length) {
				sb.append(" ");
			}
		}

		return sb.toString();
	}

}
