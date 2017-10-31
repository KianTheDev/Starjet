package me.thekian.items;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Items 
{
	ArrayList<CItem> masterItemList = new ArrayList<CItem>();
	
	public void initialize()
	{
		//id, data, cost, effect, cooldown, ItemEffect, Material, ItemType, name
		masterItemList.add(new CItem(0, 0, 0, 0, 0, ItemEffect.MISC, Material.AIR, ItemType.MISC, "Empty", false));
		masterItemList.add(new CItem(1, 0, 0, 1, 0, ItemEffect.DEFENSE, Material.SKULL_ITEM, ItemType.HEAD, "Cow Head", true));
		masterItemList.add(new CItem(2, 0, 0, 1, 0, ItemEffect.DEFENSE, Material.SKULL_ITEM, ItemType.HEAD, "Pig Head", true));
		masterItemList.add(new CItem(3, 0, 0, 3, 0, ItemEffect.MELEE, Material.STONE_SWORD, ItemType.WEAPON_MELEE, "Chipped Shortsword", false));
		masterItemList.add(new CItem(4, 0, 0, 2, 20, ItemEffect.RANGED, Material.BOW, ItemType.WEAPON_RANGED, "Cracked Shortbow", false));
		masterItemList.add(new CItem(5, 0, 0, 1, 20, ItemEffect.MAGIC, Material.STICK, ItemType.WEAPON_MAGIC, "Brittle Wand", false));
		masterItemList.add(new CItem(6, 0, 0, 5, 40, ItemEffect.RANGED, Material.EYE_OF_ENDER, ItemType.WEAPON_RANGED, "Bolt Arm", false));
		masterItemList.add(new CItem(7, 0, 10, 5, 0, ItemEffect.DEFENSE, Material.LEATHER_CHESTPLATE, ItemType.ARMOR, "Scratched Leather Cuirass", false));
		masterItemList.add(new CItem(8, 0, 6, 2, 0, ItemEffect.DEFENSE, Material.LEATHER_HELMET, ItemType.ARMOR, "Scratched Leather Cap", false));
		masterItemList.add(new CItem(9, 0, 5, 4, 0, ItemEffect.MELEE, Material.STONE_SWORD, ItemType.WEAPON_MELEE, "Shortsword", false));
		masterItemList.add(new CItem(10, 0, 5, 4, 20, ItemEffect.RANGED, Material.BOW, ItemType.WEAPON_RANGED, "Shortbow", false));
		masterItemList.add(new CItem(11, 0, 5, 1.2, 20, ItemEffect.MAGIC, Material.STICK, ItemType.WEAPON_MAGIC, "Charred Wand", false));
		masterItemList.add(new CItem(12, 0, 8, 3, 0, ItemEffect.DEFENSE, Material.LEATHER_LEGGINGS, ItemType.ARMOR, "Scratched Leather Leggings", false));
		masterItemList.add(new CItem(13, 0, 6, 2, 0, ItemEffect.DEFENSE, Material.LEATHER_BOOTS, ItemType.ARMOR, "Scratched Leather Boots", false));
	}
	
	public ArrayList<CItem> getItems()
	{
		return masterItemList;
	}
	
	public int getItemData(ItemStack is)
	{
		if(is != null)
		{
			if(is.getItemMeta().getLore() == null)
				return -1;
			for(String s : is.getItemMeta().getLore())
			{
				if(s.startsWith("Item ID: "))
				{
					for(int i3 = 8; i3 < s.length(); i3++)
					{
						if(s.charAt(i3) == ':')
						{
							return Integer.valueOf(s.substring(i3 + 1));
						}
					}
				}
			}
		}
		return -1;
	}
	
	public int getItemID(ItemStack is)
	{
		if(is != null && is.getItemMeta() != null && is.getItemMeta().getLore() != null)
		{
			for(String s : is.getItemMeta().getLore())
			{
				if(s.startsWith("Item ID: "))
				{
					for(int i3 = 8; i3 < s.length(); i3++)
					{
						if(s.charAt(i3) == ':')
						{
							return Integer.valueOf(s.substring(9, i3));
						}
					}
				}
			}
		}
		return -1;
	}
	
	public double getItemDamage(ItemStack is)
	{
		if(is != null)
		{
			int i = getItemID(is);
			if(i == -1)
				return -1;
			CItem ci = masterItemList.get(i);
			if(ci.getItemEffect().equals(ItemEffect.MAGIC) || ci.getItemEffect().equals(ItemEffect.MELEE) || ci.getItemEffect().equals(ItemEffect.RANGED))
				for(String s : is.getItemMeta().getLore())
				{
					if(s.startsWith(ci.getItemEffect().toString2() + ": "))
					{
						return(Double.valueOf(s.substring(ci.getItemEffect().toString2().length() + 2)));
					}
				}
		}
		return -1;
	}
}
