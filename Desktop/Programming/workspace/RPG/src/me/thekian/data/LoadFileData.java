package me.thekian.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.thekian.data.Players.CAccount;
import me.thekian.data.Players.CCharacter;
import me.thekian.data.Players.CPlayer;
import me.thekian.items.CItem;
import me.thekian.items.Items;

public class LoadFileData 
{
	Players players = new Players();
	Items items = new Items();
	
	public void init()
	{
		items.initialize();
	}
	
	public void savePlayerInventory(CCharacter cc, String name, String path, PlayerInventory inv)
	{
		try {
			CPlayer cp = cc.getPlayerData();
			FileWriter fileWriter = new FileWriter(path + "inventories" + File.separator + name + "-" + cc.getCharNum() + ".dat");
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			int i = 0, i2 = 0;
			boolean b = false;
			CItem ci = null;
			if(inv.getBoots() != null)
			{
				if(inv.getBoots().getItemMeta().getLore() != null)
				{
					for(String s : inv.getBoots().getItemMeta().getLore())
					{
						if(s.startsWith("Item ID: "))
						{
							b = true;
							for(int i3 = 8; i3 < s.length(); i3++)
							{
								if(s.charAt(i3) == ':')
								{
									i = Integer.valueOf(s.substring(9, i3));
									i2 = Integer.valueOf(s.substring(i3 + 1));
								}
							}
							ci = items.getItems().get(i).makeCopy();
							ci.setData(i2);
							bufferedWriter.write(ci.toStringFormat());
							bufferedWriter.newLine();
						}
					}
					if(!b)
					{
						bufferedWriter.write(items.getItems().get(0).toStringFormat());
						bufferedWriter.newLine();					
					}
				} else
				{
					bufferedWriter.write(items.getItems().get(0).toStringFormat());
					bufferedWriter.newLine();					
				}
			} else
			{
				bufferedWriter.write(items.getItems().get(0).toStringFormat());
				bufferedWriter.newLine();					
			}
			b = false;
			if(inv.getLeggings() != null)
			{
				if(inv.getLeggings().getItemMeta().getLore() != null)
				{
					for(String s : inv.getLeggings().getItemMeta().getLore())
					{
						if(s.startsWith("Item ID: "))
						{
							b = true;
							for(int i3 = 8; i3 < s.length(); i3++)
							{
								if(s.charAt(i3) == ':')
								{
									i = Integer.valueOf(s.substring(9, i3));
									i2 = Integer.valueOf(s.substring(i3 + 1));
								}
							}
							ci = items.getItems().get(i).makeCopy();
							ci.setData(i2);
							bufferedWriter.write(ci.toStringFormat());
							bufferedWriter.newLine();
						}
					}
					if(!b)
					{
						bufferedWriter.write(items.getItems().get(0).toStringFormat());
						bufferedWriter.newLine();					
					}
				} else
				{
					bufferedWriter.write(items.getItems().get(0).toStringFormat());
					bufferedWriter.newLine();					
				}
			} else
			{
				bufferedWriter.write(items.getItems().get(0).toStringFormat());
				bufferedWriter.newLine();					
			}
			b = false;
			if(inv.getChestplate() != null)
			{
				if(inv.getChestplate().getItemMeta().getLore() != null)
				{
					for(String s : inv.getChestplate().getItemMeta().getLore())
					{
						if(s.startsWith("Item ID: "))
						{
							b = true;
							for(int i3 = 8; i3 < s.length(); i3++)
							{
								if(s.charAt(i3) == ':')
								{
									i = Integer.valueOf(s.substring(9, i3));
									i2 = Integer.valueOf(s.substring(i3 + 1));
								}
							}
							ci = items.getItems().get(i).makeCopy();
							ci.setData(i2);
							bufferedWriter.write(ci.toStringFormat());
							bufferedWriter.newLine();
						}
					}
					if(!b)
					{
						bufferedWriter.write(items.getItems().get(0).toStringFormat());
						bufferedWriter.newLine();					
					}
				} else
				{
					bufferedWriter.write(items.getItems().get(0).toStringFormat());
					bufferedWriter.newLine();					
				}
			} else
			{
				bufferedWriter.write(items.getItems().get(0).toStringFormat());
				bufferedWriter.newLine();					
			}
			b = false;
			if(inv.getHelmet() != null)
			{
				if(inv.getHelmet().getItemMeta().getLore() != null)
				{
					for(String s : inv.getHelmet().getItemMeta().getLore())
					{
						if(s.startsWith("Item ID: "))
						{
							b = true;
							for(int i3 = 8; i3 < s.length(); i3++)
							{
								if(s.charAt(i3) == ':')
								{
									i = Integer.valueOf(s.substring(9, i3));
									i2 = Integer.valueOf(s.substring(i3 + 1));
								}
							}
							ci = items.getItems().get(i).makeCopy();
							ci.setData(i2);
							bufferedWriter.write(ci.toStringFormat());
						}
					}
					if(!b)
					{
						bufferedWriter.write(items.getItems().get(0).toStringFormat());
					}
				} else
				{
					bufferedWriter.write(items.getItems().get(0).toStringFormat());		
				}
			} else
			{
				bufferedWriter.write(items.getItems().get(0).toStringFormat());
			}
			//Inventory
			for(int i3 = 0; i3 < 36; i3++)
			{
				ItemStack is = inv.getItem(i3);
				if(is != null)
				{
					if(is.getItemMeta().getLore() != null && !is.getType().equals(Material.AIR))
					{
						for(String s : is.getItemMeta().getLore())
						{
							if(s.startsWith("Item ID: "))
							{
								b = true;
								for(int i4 = 8; i4 < s.length(); i4++)
								{
									if(s.charAt(i4) == ':')
									{
										i = Integer.valueOf(s.substring(9, i4));
										i2 = Integer.valueOf(s.substring(i4 + 1));
									}
								}
								ci = items.getItems().get(i).makeCopy();
								ci.setData(i2);
							}
						}
						if(!b)
						{
							ci = items.getItems().get(0).makeCopy();	
						}
					} else
					{
						ci = items.getItems().get(0).makeCopy();		
					}
				} else
				{
					ci = items.getItems().get(0).makeCopy();		
				}
				bufferedWriter.newLine();
				bufferedWriter.write(ci.toStringFormat());
			}
			bufferedWriter.close();
		} catch (IOException e)
		{
			System.out.println("Problem?");
		}
	}
	
	public void savePlayerData(CCharacter cc, String name, String path)
	{
		try {
			CPlayer cp = cc.getPlayerData();
			FileWriter fileWriter = new FileWriter(path + "playerdata" + File.separator + name + "-" + cc.getCharNum() + ".dat");
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(cp.getUUID().toString());
			bufferedWriter.newLine();
			//Coords
			bufferedWriter.write(String.valueOf(cc.getCoords()[0]));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cc.getCoords()[1]));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cc.getCoords()[2]));
			bufferedWriter.newLine();
			//End coords
			bufferedWriter.write(cp.getPlayerClass().toString());
			bufferedWriter.newLine();
			bufferedWriter.write(cp.getRace().toString());
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getCredits()));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getLevel()));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getXP()));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getHealth()));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getMaxHealth()));
			//Stats
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getStatPoints()));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getStrength()));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getLuck()));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getLogic()));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getIntelligence()));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getAgility()));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(cp.getVitality()));
			bufferedWriter.close();
		} catch (IOException e)
		{
			System.out.println("Problem?");
		}
	}
	
	public ArrayList<CCharacter> loadAccountData(UUID uuid, String path)
	{
		ArrayList<CCharacter> list = new ArrayList<CCharacter>();
		if(new File(path + "playerdata" + File.separator + uuid.toString() + "-1.dat").exists() && new File(path + "inventories" + File.separator + uuid.toString() + "-1.dat").exists())
		{
			int i = 1;
			while(new File(path + "playerdata" + File.separator + uuid.toString() + "-" + i + ".dat").exists() && new File(path + "inventories" + File.separator + uuid.toString() + "-" + i + ".dat").exists())
			{
				CPlayer cp = loadPlayerData(uuid, path, i);
				ArrayList<CItem> itemList = loadPlayerInventory(uuid, path, i);
				ArrayList<CItem> armorList = loadPlayerArmor(uuid, path, i);
				CCharacter cc = players.new CCharacter(cp, itemList, armorList, i);
				cc.setCoords(loadCoords(uuid, path, i, 0), loadCoords(uuid, path, i, 1), loadCoords(uuid, path, i, 2));
				list.add(cc);
				i++;
			}
		}
		return list;
	}
	
	public ArrayList<CItem> loadPlayerArmor(UUID uuid, String path, int i)
	{
		ArrayList<CItem> list2 = new ArrayList<CItem>();
		try {
			FileReader fileReader = new FileReader(path + "inventories" + File.separator + uuid.toString() + "-" + i + ".dat");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String s = "";
			CItem ci = items.getItems().get(0).makeCopy();
			s = bufferedReader.readLine();
			ci.fromString(s);
			list2.add(ci.makeCopy());
			s = bufferedReader.readLine();
			ci.fromString(s);
			list2.add(ci.makeCopy());
			s = bufferedReader.readLine();
			ci.fromString(s);
			list2.add(ci.makeCopy());
			s = bufferedReader.readLine();
			ci.fromString(s);
			list2.add(ci.makeCopy());
			/*String s;
			if((s = bufferedReader.readLine()).equals(""))
			{
				cp.setName(bufferedReader.readLine());
			} else
			{
				cp.setName(s);
			} */
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e)
		{
			return null;
		}
		return list2;
	}
	
	public ArrayList<CItem> loadPlayerInventory(UUID uuid, String path, int i)
	{
		ArrayList<CItem> list1 = new ArrayList<CItem>(); 
		try {
			FileReader fileReader = new FileReader(path + "inventories" + File.separator + uuid.toString() + "-" + i + ".dat");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String s = "";
			CItem ci = items.getItems().get(0).makeCopy();
			bufferedReader.readLine();
			bufferedReader.readLine();
			bufferedReader.readLine();
			bufferedReader.readLine();
			while((s = bufferedReader.readLine()) != null)
			{
				ci.fromString(s);
				list1.add(ci.makeCopy());
			}
			/*String s;
			if((s = bufferedReader.readLine()).equals(""))
			{
				cp.setName(bufferedReader.readLine());
			} else
			{
				cp.setName(s);
			} */
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e)
		{
			return null;
		}
		return list1;
	}
	
	public CPlayer loadPlayerData(UUID uuid, String path, int i)
	{
		double[] coords = new double[3];
		CPlayer cp = players.new CPlayer(uuid);
		try {
			FileReader fileReader = new FileReader(path + "playerdata" + File.separator + uuid.toString() + "-" + i + ".dat");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			/*String s;
			if((s = bufferedReader.readLine()).equals(""))
			{
				cp.setName(bufferedReader.readLine());
			} else
			{
				cp.setName(s);
			} */
			cp.setUUID(UUID.fromString(bufferedReader.readLine()));
			coords[0] = Double.valueOf(bufferedReader.readLine());
			coords[1] = Double.valueOf(bufferedReader.readLine());
			coords[2] = Double.valueOf(bufferedReader.readLine());
			cp.setClass(PlayerClass.valueOf(bufferedReader.readLine().toUpperCase()));
			cp.setRace(Race.valueOf(bufferedReader.readLine().toUpperCase()));
			cp.setCredits(Integer.valueOf(bufferedReader.readLine()));
			cp.setXPLevel(Integer.valueOf(bufferedReader.readLine()), Integer.valueOf(bufferedReader.readLine()));
			cp.setHealth(Integer.valueOf(bufferedReader.readLine()));
			cp.setMaxHealth();
			bufferedReader.readLine();
			//Stats
			cp.setStatPoints(Integer.valueOf(bufferedReader.readLine()));
			cp.setStrength(Integer.valueOf(bufferedReader.readLine()));
			cp.setLuck(Integer.valueOf(bufferedReader.readLine()));
			cp.setLogic(Integer.valueOf(bufferedReader.readLine()));
			cp.setIntelligence(Integer.valueOf(bufferedReader.readLine()));
			cp.setAgility(Integer.valueOf(bufferedReader.readLine()));
			cp.changeVitality(Integer.valueOf(bufferedReader.readLine()));
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e)
		{
			return null;
		}
		return cp;
	}
	
	public double loadCoords(UUID uuid, String path, int i, int i2)
	{
		double[] coords = new double[3];
		try {
			FileReader fileReader = new FileReader(path + "playerdata" + File.separator + uuid.toString() + "-" + i + ".dat");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			bufferedReader.readLine();
			coords[0] = Double.valueOf(bufferedReader.readLine());
			coords[1] = Double.valueOf(bufferedReader.readLine());
			coords[2] = Double.valueOf(bufferedReader.readLine());
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			return 0;
		} catch (IOException e)
		{
			return 0;
		}
		return coords[i2];
	}
}
