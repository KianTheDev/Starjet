package me.thekian.rpg;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.thekian.util.Util;

import me.thekian.data.Mobs.CMob;
import me.thekian.data.ShopType;
import me.thekian.items.Items;

public class Shops 
{
	Util util = new Util();
	Items items;
	
	public void init(Items i)
	{
		items = i;
	}
	
	public void createShop(ShopType st, Player p)
	{
		if(st.equals(ShopType.BATTLE_1))
		{
			Inventory inv = Bukkit.createInventory(null, 63, "Shop");
			ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE);
			is.setDurability((short) 15);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(" ");
			is.setItemMeta(im);
			for(int i = 0; i < 9; i++)
			{
				inv.setItem(i, is);
				inv.setItem(i + 53, is);
			}
			for(int i = 9; i <= 54; i += 9)
			{
				inv.setItem(i, is);
				inv.setItem(i + 8, is);
			}
			inv.setItem(10, items.getItems().get(9).getItem(true));
			inv.setItem(12, items.getItems().get(10).getItem(true));
			inv.setItem(14, items.getItems().get(11).getItem(true));
			inv.setItem(16, items.getItems().get(7).getItem(true));
			inv.setItem(20, items.getItems().get(12).getItem(true));
			inv.setItem(22, items.getItems().get(8).getItem(true));
			inv.setItem(24, items.getItems().get(13).getItem(true));
			p.openInventory(inv);
		} else if(st.equals(ShopType.MAGIC_1))
		{
			Inventory inv = Bukkit.createInventory(null, 27, "Shop");util.storeAddItem(new ItemStack(Material.FIREBALL), ChatColor.BLUE + "Radial Bomb", new String[] {"50 credits.", "6 block range.", "Ignites and damages enemies.", "Level 1"}, inv, 10);
			util.storeAddItem(new ItemStack(Material.EXP_BOTTLE), ChatColor.BLUE + "Regenerative Enzymes", new String[] {"100 credits.", "Removes harmful effects.", "Level 1"}, inv, 12);
			util.storeAddItem(new ItemStack(Material.EYE_OF_ENDER), ChatColor.BLUE + "Explosive Grenade", new String[] {"30 credits.", "5 damage.", "Level 1"}, inv, 14);
			util.storeAddItem(new ItemStack(Material.EXP_BOTTLE), ChatColor.BLUE + "Knockout Gas", new String[] {"20 credits.", "Applies slow for 3 seconds.", "Level 1"}, inv, 16);
			p.openInventory(inv);
		}
	}
}
