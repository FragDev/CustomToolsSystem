package me.fragment.customtools.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.fragment.customtools.CustomItem;
import me.fragment.customtools.CustomTools;

public class VanillaListeners implements Listener {

	@EventHandler
	public void onPlayerExpChange(PlayerExpChangeEvent event) {
		if (event.getAmount() > 0 && event.getPlayer() != null) {
			PlayerInventory inv = event.getPlayer().getInventory();
			Map<EquipmentSlot, ItemStack> itemsMap = new HashMap<EquipmentSlot, ItemStack>() {
				{
					put(EquipmentSlot.HEAD, inv.getHelmet());
					put(EquipmentSlot.CHEST, inv.getChestplate());
					put(EquipmentSlot.LEGS, inv.getLeggings());
					put(EquipmentSlot.FEET, inv.getBoots());
					put(EquipmentSlot.HAND, inv.getItemInMainHand());
					put(EquipmentSlot.OFF_HAND, inv.getItemInOffHand());
				}
			};
			itemsMap.entrySet().stream().filter(entry -> entry.getValue() != null)
					.filter(entry -> CustomTools.getItemsManager().getMaterials().contains(entry.getValue().getType()) && !CustomItem.fromItemStack(entry.getValue()).isMaxLevel())
					.limit(event.getAmount()).forEach(entry -> {
						CustomItem customItem = CustomItem.fromItemStack(entry.getValue());
						customItem.addExp((int) Math.max(event.getAmount() / itemsMap.size(), 1.0));
						inv.setItem(entry.getKey(), customItem);
					});
		}
	}

}
