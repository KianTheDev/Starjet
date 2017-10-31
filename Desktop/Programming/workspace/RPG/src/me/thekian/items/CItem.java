package me.thekian.items;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import net.md_5.bungee.api.ChatColor;

public class CItem 
{
	private int id, data, cost, cooldown;
	private double effect;
	private ItemEffect itemEffect;
	private Material material;
	private ItemType type;
	private String name;
	private boolean undroppable;
	
	public CItem(int i, int d, int c, double e, int cd, ItemEffect ie, Material m, ItemType t, String n, boolean u)
	{
		id = i;
		data = d;
		cost = c;
		effect = e;
		cooldown = cd;
		itemEffect = ie;
		material = m;
		type = t;
		name = n;
		undroppable = u;
	}
	
	public ItemStack getItem(boolean b)
	{
		ItemStack is = new ItemStack(material);
		if(material.equals(Material.AIR))
		{
			return null;
		} else if(itemEffect.equals(ItemEffect.MISC))
		{
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.GRAY + name);
			im.setLore(Arrays.asList("Item Type: " + type.toString2(), "Value: " + cost, "Item ID: " + id + ":" + data));
			is.setItemMeta(im);
		} else if(type.equals(ItemType.HEAD))
		{
			SkullMeta im = (SkullMeta) is.getItemMeta();
			im.setDisplayName(ChatColor.BLUE + name);
			im.setOwner("MHF_" + name.substring(0, 3));
			im.setLore(Arrays.asList("Defense: " + effect, "Item ID: " + id + ":" + data, ChatColor.RED + "Unremovable"));
			is.setDurability((short) 3);
			is.setItemMeta(im);
		} else if(!b)
		{
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.GRAY + name);
			im.setLore(Arrays.asList("Item Type: " + type.toString2(), itemEffect.toString2() + ": " + effect, "Value: " + cost, "Item ID: " + id + ":" + data));
			is.setItemMeta(im);
		} else
		{
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "PURCHASE ITEM: " + ChatColor.RESET + ChatColor.GRAY + name);
			im.setLore(Arrays.asList("Item Type: " + type.toString2(), itemEffect.toString2() + ": " + effect, "Value: " + cost, "Item ID: " + id + ":" + data));
			is.setItemMeta(im);
		}
		return is;
	}
	
	public int getID()
	{
		return id;
	}
	
	public int getData()
	{
		return data;
	}
	
	public int getCost()
	{
		return cost;
	}
	
	public ItemType getType()
	{
		return type;
	}
	
	public String getName()
	{
		return name;
	}
	
	public double getEffect()
	{
		return effect;
	}
	
	public ItemEffect getItemEffect()
	{
		return itemEffect;
	}
	
	public boolean getUndroppable()
	{
		return undroppable;
	}
	
	public void setName(String s)
	{
		name = s;
	}
	
	public void setData(int i)
	{
		data = i;
	}
	
	//For saving in files
	public String toStringFormat()
	{
		String s = id + ";" + data + ";" + cost + ";" + cooldown + ";" + effect + ";" + itemEffect.toString() + ";" + material.toString() + ";" + type.toString() + ";" + name + ";";
		return s;
	}
	
	public void fromString(String s)
	{
		int start = 0;
		int i2 = 0;
		for(int i = 0; i < s.length(); i++)
		{
			if(s.charAt(i) == ';')
			{
				if(i2 == 0)
					id = Integer.valueOf(s.substring(start, i));
				if(i2 == 1)
					data = Integer.valueOf(s.substring(start, i));
				if(i2 == 2)
					cost = Integer.valueOf(s.substring(start, i));
				if(i2 == 3)
					cooldown = Integer.valueOf(s.substring(start, i));
				if(i2 == 4)
					effect = Double.valueOf(s.substring(start, i));
				if(i2 == 5)
					itemEffect = ItemEffect.valueOf(s.substring(start, i).toUpperCase());
				if(i2 == 6)
					material = Material.valueOf(s.substring(start, i).toUpperCase());
				if(i2 == 7)
					type = ItemType.valueOf(s.substring(start, i).toUpperCase());
				if(i2 == 8)
					name = s.substring(start, i);
				start = i + 1;
				i2++;
			}
		}
	}
	
	public CItem makeCopy()
	{
		return new CItem(id, data, cost, effect, cooldown, itemEffect, material, type, name, undroppable);
	}
}
