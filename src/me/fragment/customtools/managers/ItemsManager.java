package me.fragment.customtools.managers;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.SmithingRecipe;

import me.fragment.customtools.CustomItem;
import me.fragment.customtools.CustomTools;
import me.fragment.customtools.config.Config;

public class ItemsManager {

	private Set<Material> materials = new HashSet<Material>();

	public ItemsManager() {
		this.modifyRecipes();
	}

	public void modifyRecipes() {
		Stream.of(Material.values())
				.filter(material -> (material.toString().contains("HELMET") || material.toString().contains("PICKAXE") || material.toString().contains("LEGGINGS")
						|| material.toString().contains("CHESTPLATE") || material.toString().contains("BOOTS") || material.toString().contains("AXE")
						|| material.toString().contains("HOE") || material.toString().contains("BOW") || material.toString().contains("SHOVEL") || material.toString().contains("SWORD")
						|| material.toString().contains("SHIEL") || material.toString().contains("TRIDENT") || material.toString().contains("CROSSBOW")
						|| material.toString().contains("FISHING")) && !Config.getBlacklistTools().contains(material))
				.forEach(material -> {
					this.materials.add(material);

					Bukkit.getRecipesFor(new ItemStack(material)).forEach(recipe -> {
						if (recipe instanceof ShapedRecipe) {
							Bukkit.getScheduler().runTask(CustomTools.getInstance(), () -> {
								ShapedRecipe replaceRecipe = new ShapedRecipe(((ShapedRecipe) recipe).getKey(), CustomItem.fromItemStack(recipe.getResult()));
								replaceRecipe.shape(((ShapedRecipe) recipe).getShape());
								replaceRecipe.setGroup(((ShapedRecipe) recipe).getGroup());

								((ShapedRecipe) recipe).getIngredientMap().forEach((key, itemstack) -> {
									if (itemstack != null) {
										replaceRecipe.setIngredient(key, itemstack.getType());
									}
								});

								Bukkit.removeRecipe(((ShapedRecipe) recipe).getKey());
								Bukkit.addRecipe(replaceRecipe);
							});
						} else if (recipe instanceof ShapelessRecipe) {
							Bukkit.getScheduler().runTask(CustomTools.getInstance(), () -> {
								ShapelessRecipe replaceRecipe = new ShapelessRecipe(((ShapelessRecipe) recipe).getKey(), CustomItem.fromItemStack(recipe.getResult()));
								replaceRecipe.setGroup(((ShapelessRecipe) recipe).getGroup());

								((ShapelessRecipe) recipe).getIngredientList().forEach(ingredient -> {
									replaceRecipe.addIngredient(ingredient.getAmount(), ingredient.getType());
								});

								Bukkit.removeRecipe(((ShapelessRecipe) recipe).getKey());
								Bukkit.addRecipe(replaceRecipe);
							});
						} else if (recipe instanceof SmithingRecipe) {
							Bukkit.getScheduler().runTask(CustomTools.getInstance(), () -> {
								SmithingRecipe replaceRecipe = new SmithingRecipe(((SmithingRecipe) recipe).getKey(), CustomItem.fromItemStack(recipe.getResult()),
										((SmithingRecipe) recipe).getBase(), ((SmithingRecipe) recipe).getAddition());

								Bukkit.removeRecipe(((SmithingRecipe) recipe).getKey());
								Bukkit.addRecipe(replaceRecipe);
							});
						}
					});
				});

		CustomTools.getInstance().getLogger().log(java.util.logging.Level.INFO, "The crafts have been modified and injected");
	}

	public Set<Material> getMaterials() {
		return materials;
	}

}
