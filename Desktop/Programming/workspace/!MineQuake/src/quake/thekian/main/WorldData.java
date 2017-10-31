package quake.thekian.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Material;

import quake.thekian.weapons.WepData;
import quake.thekian.weapons.WepData.WeaponType;

public class WorldData 
{
	private String name, authors;
	private List<CoordSet> startLocations;
	private HashMap<CoordSet, WeaponType> ammoSpawns;
	private HashMap<CoordSet, WeaponType> weaponSpawns;
	private HashMap<CoordSet, Integer> healthSpawns;
	private CoordSet spectatorStartLocation;
	private Material collapseBlock;
	private int UID;
	private String fileName, worldName;
	private WorldType worldType;
	private CollapseType collapseType;
	
	public WorldData(String name, String worldName, String authors, List<CoordSet> startLocations, Material collapseBlock, int UID, 
			String fileName, WorldType worldType, CollapseType collapseType)
	{
		this.name = name;
		this.worldName = worldName;
		this.authors = authors;
		this.startLocations = startLocations;
		this.collapseBlock = collapseBlock;
		this.UID = UID;
		this.fileName = fileName;
		this.worldType = worldType;
		this.collapseType = collapseType;
	}
	
	public WorldData(File f, boolean spawn)
	{
		if(f.exists())
		{
			try {
				startLocations = new ArrayList<CoordSet>();
				weaponSpawns = new HashMap<CoordSet, WeaponType>();
				ammoSpawns = new HashMap<CoordSet, WeaponType>();
				healthSpawns = new HashMap<CoordSet, Integer>();
				FileReader fileReader = new FileReader(f);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String s = "";
				while(spawn && (s = bufferedReader.readLine()) != null)
				{
					if(s.startsWith("name="))
						this.name = s.substring(5);
					else if(s.startsWith("worldname="))
						this.worldName = s.substring(10);
					else if(s.startsWith("authors="))
						this.authors = s.substring(8);
					else if(s.startsWith("startloc="))
					{
						int b = 0, c = 9;
						CoordSet startLoc = new CoordSet(0, 0, 0);
						for(int i = 9; i < s.length(); i++)
						{
							if(s.charAt(i) == ',' || i == s.length() - 1)
								switch(b)
								{
								case 0:
									startLoc.setX(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 1:
									startLoc.setY(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 2:
									startLoc.setZ(Double.valueOf(s.substring(c)));
									break;
								}
						}
						this.startLocations.add(startLoc); //s.substring(5);
					} else if(s.startsWith("spectatorstart="))
					{
						int b = 0, c = 15;
						CoordSet startLoc = new CoordSet(0, 0, 0);
						for(int i = 15; i < s.length(); i++)
						{
							if(s.charAt(i) == ',' || i == s.length() - 1)
								switch(b)
								{
								case 0:
									startLoc.setX(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 1:
									startLoc.setY(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 2:
									startLoc.setZ(Double.valueOf(s.substring(c)));
									break;
								}
						}
						spectatorStartLocation = startLoc;
					} else if(s.startsWith("filename="))
						this.fileName = s.substring(9);
					else if(s.startsWith("uid="))
						this.UID = Integer.valueOf(s.substring(4));
					else if(s.startsWith("worldtype="))
						this.worldType = WorldType.valueOf(s.substring(10).toUpperCase());
					this.collapseType = null;
					this.collapseBlock = null;
				}
				while(!spawn && (s = bufferedReader.readLine()) != null)
				{
					if(s.startsWith("name="))
						this.name = s.substring(5);
					else if(s.startsWith("worldname="))
						this.worldName = s.substring(10);
					else if(s.startsWith("authors="))
						this.authors = s.substring(8);
					else if(s.startsWith("startloc="))
					{
						int b = 0, c = 9;
						CoordSet startLoc = new CoordSet(0, 0, 0);
						for(int i = 9; i < s.length(); i++)
						{
							if(s.charAt(i) == ',' || i == s.length() - 1)
								switch(b)
								{
								case 0:
									startLoc.setX(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 1:
									startLoc.setY(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 2:
									startLoc.setZ(Double.valueOf(s.substring(c)));
									break;
								}
						}
						this.startLocations.add(startLoc); //s.substring(5);
					} else if(s.startsWith("wepspawn="))
					{
						System.out.println("Weapon spawn");
						int b = 0, c = 9;
						CoordSet spawnLoc = new CoordSet(0, 0, 0);
						WeaponType weaponType = WeaponType.MACHINEGUN;
						for(int i = 9; i < s.length(); i++)
						{
							if(s.charAt(i) == ',' || i == s.length() - 1)
								switch(b)
								{
								case 0:
									weaponType = WeaponType.valueOf(s.substring(c, i));
									c = i + 1;
									b++;
									break;
								case 1:
									spawnLoc.setX(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 2:
									spawnLoc.setY(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 3:
									spawnLoc.setZ(Double.valueOf(s.substring(c)));
									break;
								}
						}	
						weaponSpawns.put(spawnLoc, weaponType);
					} else if(s.startsWith("ammospawn="))
					{
						System.out.println("Ammo spawn");
						int b = 0, c = 10;
						CoordSet spawnLoc = new CoordSet(0, 0, 0);
						WeaponType weaponType = WeaponType.MACHINEGUN;
						for(int i = 9; i < s.length(); i++)
						{
							if(s.charAt(i) == ',' || i == s.length() - 1)
								switch(b)
								{
								case 0:
									weaponType = WeaponType.valueOf(s.substring(c, i));
									c = i + 1;
									b++;
									break;
								case 1:
									spawnLoc.setX(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 2:
									spawnLoc.setY(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 3:
									spawnLoc.setZ(Double.valueOf(s.substring(c)));
									break;
								}
						}	
						ammoSpawns.put(spawnLoc, weaponType);
					} else if(s.startsWith("healthspawn="))
					{
						System.out.println("Health spawn");
						int b = 0, c = 12;
						CoordSet spawnLoc = new CoordSet(0, 0, 0);
						int val = 5;
						for(int i = 9; i < s.length(); i++)
						{
							if(s.charAt(i) == ',' || i == s.length() - 1)
								switch(b)
								{
								case 0:
									val = Integer.valueOf(s.substring(c, i));
									c = i + 1;
									b++;
									break;
								case 1:
									spawnLoc.setX(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 2:
									spawnLoc.setY(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 3:
									spawnLoc.setZ(Double.valueOf(s.substring(c)));
									break;
								}
						}	
						healthSpawns.put(spawnLoc, new Integer(val));	
					}
					else if(s.startsWith("uid="))
						this.UID = Integer.valueOf(s.substring(4));
					else if(s.startsWith("filename="))
						this.fileName = s.substring(9);
					else if(s.startsWith("worldtype="))
						this.worldType = WorldType.valueOf(s.substring(10).toUpperCase());
				}
				bufferedReader.close();
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		} else
			throw new IllegalArgumentException();
	}
	
	public String getWorldName()
	{
		return this.worldName;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getAuthors()
	{
		return this.authors;
	}
	
	public List<CoordSet> getStartLocations()
	{
		return this.startLocations;
	}
	
	public CoordSet getSpectatorStartLocation()
	{
		return spectatorStartLocation;
	}
	
	public int getUID()
	{
		return this.UID;
	}
	
	public Material getDeathBlock()
	{
		return collapseBlock;
	}
	
	public CollapseType getDeathType()
	{
		return collapseType;
	}
	
	public WorldType getWorldType()
	{
		return worldType;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public HashMap<CoordSet, WeaponType> getWeaponSpawns()
	{
		return weaponSpawns;
	}
	
	public HashMap<CoordSet, WeaponType> getAmmoSpawns()
	{
		return ammoSpawns;
	}
	
	public HashMap<CoordSet, Integer> getHealthSpawns()
	{
		return healthSpawns;
	}
	
	public enum WorldType
	{
		HUB, SPAWN, GAMEWORLD;
	}
	
	public enum CollapseType
	{
		COLLAPSE, FLOOD, FIELD;
	}
}
