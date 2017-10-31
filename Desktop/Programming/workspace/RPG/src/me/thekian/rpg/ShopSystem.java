package me.thekian.rpg;

import java.util.Arrays;

import me.thekian.data.Players.CPlayer;
import me.thekian.items.Items;
import me.thekian.util.Util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopSystem 
{
	
	Util util = new Util();
	
	Items items;
	
	public void init(Items i)
	{
		items = i;
	}
	
	public void ShopClick(Player p, ItemStack itemStack, Inventory inv, CPlayer cp)
	{
		if(itemStack == null)
			return;
		if(itemStack.getItemMeta() == null)
			return;
		if(itemStack.getItemMeta().getDisplayName() == null)
			return;
		if(itemStack.getItemMeta().getDisplayName().startsWith(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "PURCHASE ITEM: "))
		{
			if(itemStack.getItemMeta().getLore() != null)
			{
				int reqCred = 0, itemID = items.getItemID(itemStack);
				if(itemID != -1)
				{
					reqCred = items.getItems().get(itemID).getCost();
					if(reqCred <= cp.getCredits())
					{
						boolean b = true;
						for(int i = 0; i < 35; i++)
							if(p.getInventory().getItem(i) == null)
								b = false;
							else if(p.getInventory().getItem(i).getType().equals(Material.AIR))
								b = false;
						if(b)
						{
							p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
							p.sendMessage(ChatColor.BLUE + "Shop> " + ChatColor.GRAY + "Your inventory is full.");
						} else
						{
							cp.setCredits(cp.getCredits() - reqCred);
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_PLING, 1F, 1F);
							ItemStack itemToAdd = items.getItems().get(itemID).getItem(false);
							p.getInventory().addItem(itemToAdd);
							p.closeInventory();
							p.sendMessage(ChatColor.BLUE + "Shop> " + ChatColor.GRAY + "You purchased" + ChatColor.YELLOW + items.getItems().get(itemID).getName() + ChatColor.GRAY + " for " + ChatColor.YELLOW + reqCred + ChatColor.GRAY + " credits.");
						}
					}
					else
						p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
				}
			}
		}
	}
}
