package kian.towers.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

/***
 * Holds important data about invididual game worlds, such as start locations.
 * @author TheKian
 *
 */
public class WorldData 
{
	private String name, authors; //Map and author names
	private List<CoordSet> startLocations; //Starting locations for players
	private List<CoordSet> mobGates; //Mob spawnpoints, corresponding to each player
	private List<List<CoordSet>> nodes; //Nodes for each player
	private CoordSet spectatorStartLocation; //Spawnpoint for spectators
	private int buildHeight; //Height of the plane on which players can make towers
	private int UID; //Map ID for debug purposes
	private String fileName, worldName; //World .zip path and Bukkit world name
	private WorldType worldType; //Kind of world to which data corresponds
	
	public WorldData(String name, String worldName, String authors, List<CoordSet> startLocations, Material collapseBlock, int UID, 
			String fileName, WorldType worldType)
	{
		this.name = name;
		this.worldName = worldName;
		this.authors = authors;
		this.startLocations = startLocations;
		this.UID = UID;
		this.fileName = fileName;
		this.worldType = worldType;
	}
	
	/***
	 * Initializes the world data from a file path.
	 * @param f - Data file.
	 * @param spawn - Whether the world is a spawn world.
	 */
	public WorldData(File f, boolean spawn)
	{
		if(f.exists())
		{
			try 
			{
				nodes = new ArrayList<List<CoordSet>>();
				for(int i = 0; i < 4; i++)
					nodes.add(new ArrayList<CoordSet>());
				startLocations = new ArrayList<CoordSet>(); 
				mobGates = new ArrayList<CoordSet>();
				FileReader fileReader = new FileReader(f);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String s = "";
				//World data loading for spawn worlds
				while(spawn && (s = bufferedReader.readLine()) != null)
				{
					if(s.startsWith("name=")) //Map name
						this.name = s.substring(5);
					else if(s.startsWith("worldname=")) //Bukkit world name
						this.worldName = s.substring(10);
					else if(s.startsWith("authors=")) //Author list
						this.authors = s.substring(8);
					else if(s.startsWith("startloc=")) //Adds a player start location to the list
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
						this.startLocations.add(startLoc);
					} else if(s.startsWith("spectatorstart=")) //Spectator start location
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
					} else if(s.startsWith("filename=")) //World .zip file
						this.fileName = s.substring(9);
					else if(s.startsWith("uid=")) //Unique ID
						this.UID = Integer.valueOf(s.substring(4));
					else if(s.startsWith("worldtype=")) //World type
						this.worldType = WorldType.valueOf(s.substring(10).toUpperCase());
				}
				while(!spawn && (s = bufferedReader.readLine()) != null)
				{
					if(s.startsWith("name="))
						this.name = s.substring(5);
					else if(s.startsWith("worldname="))
						this.worldName = s.substring(10);
					else if(s.startsWith("authors="))
						this.authors = s.substring(8);
					else if(s.startsWith("startloc=")) //Adds a player start location to the list
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
						this.startLocations.add(startLoc);
					} else if(s.startsWith("mobspawn=")) //Adds a mob spawning start location to the list
					{
						int b = 0, c = 9;
						CoordSet mobLoc = new CoordSet(0, 0, 0);
						for(int i = 9; i < s.length(); i++)
						{
							if(s.charAt(i) == ',' || i == s.length() - 1)
								switch(b)
								{
								case 0:
									mobLoc.setX(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 1:
									mobLoc.setY(Double.valueOf(s.substring(c, i)));
									c = i + 1;
									b++;
									break;
								case 2:
									mobLoc.setZ(Double.valueOf(s.substring(c)));
									break;
								}
						}
						this.mobGates.add(mobLoc);
					} 
					else if(s.startsWith("uid="))
						this.UID = Integer.valueOf(s.substring(4));
					else if(s.startsWith("filename="))
						this.fileName = s.substring(9);
					else if(s.startsWith("worldtype="))
						this.worldType = WorldType.valueOf(s.substring(10).toUpperCase());
					else
						for(int n = 0; n < 4; n++)
							if(s.startsWith("node" + (n + 1) + "=")) //Adds a node to the i node list
							{
								int b = 0, c = 6;
								CoordSet node = new CoordSet(0, 0, 0);
								for(int i = 6; i < s.length(); i++)
								{
									if(s.charAt(i) == ',' || i == s.length() - 1)
										switch(b)
										{
										case 0:
											node.setX(Double.valueOf(s.substring(c, i)));
											c = i + 1;
											b++;
											break;
										case 1:
											node.setY(Double.valueOf(s.substring(c, i)));
											c = i + 1;
											b++;
											break;
										case 2:
											node.setZ(Double.valueOf(s.substring(c)));
											break;
										}
								}
								nodes.get(n).add(node);
							} 
				}
				bufferedReader.close();
				System.out.println("World loaded name: " + name);
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
	
	public List<CoordSet> getNodes(int i)
	{
		return nodes.get(i);
	}
	
	public List<List<CoordSet>> getAllNodes()
	{
		return nodes;
	}
	
	public CoordSet getSpectatorStartLocation()
	{
		return spectatorStartLocation;
	}
	
	public int getUID()
	{
		return this.UID;
	}
	
	public int getBuildHeight()
	{
		return buildHeight;
	}
	
	public WorldType getWorldType()
	{
		return worldType;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public enum WorldType
	{
		HUB, SPAWN, GAMEWORLD;
	}
}
