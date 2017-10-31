package me.thekian.util;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Util 
{
	public void makeAddItem(ItemStack is, String displayName, Inventory inv, boolean unbreakable)
	{
		ItemMeta im = is.getItemMeta();
		im.spigot().setUnbreakable(unbreakable);
		im.setDisplayName(displayName);
		is.setItemMeta(im);
		inv.addItem(is);
	}
	
	public void makeAddItem(ItemStack is, String displayName, String[] lore, Inventory inv, boolean unbreakable)
	{
		ItemMeta im = is.getItemMeta();
		im.spigot().setUnbreakable(unbreakable);
		im.setDisplayName(displayName);
		im.setLore(Arrays.asList(lore));
		is.setItemMeta(im);
		inv.addItem(is);
	}
	
	public void storeAddItem(ItemStack is, String displayName, String[] lore, Inventory inv, int i)
	{
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayName);
		im.setLore(Arrays.asList(lore));
		is.setItemMeta(im);
		inv.setItem(i, is);
	}
}
