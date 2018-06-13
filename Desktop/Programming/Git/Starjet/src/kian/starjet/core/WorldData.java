package kian.starjet.core;

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
	private CoordSet spectatorStartLocation; //Spawnpoint for spectators
	private List<CoordSet> dreadnoughtSpawns; //Locations for dreadnoughts. Alternates between teams to spawn.
	private List<CoordSet> frigateSpawns; //Locations for frigates. Alternates between teams to spawn.
	
	private int[] mapDims; //Set of six values indicated boundaries of the map. Players who cross these boundaries will die.
	
	private int UID; //Map ID for debug purposes
	private String fileName, worldName; //World .zip path and Bukkit world name
	private WorldType worldType; //Kind of world to which data corresponds
	
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
				startLocations = new ArrayList<CoordSet>(); 
				dreadnoughtSpawns = new ArrayList<CoordSet>();
				frigateSpawns = new ArrayList<CoordSet>();
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
					} else if(s.startsWith("dims=")) //Dimensions
					{
						mapDims = new int[6];
						int b = 0, c = 5;
						for(int i = 5; i < s.length(); i++)
						{
							if((s.charAt(i) == ',' || i == s.length() - 1) && b < 6)
							{
								mapDims[b] = Integer.valueOf(s.substring(c, i));
								b++;
								c = i + 1;
							}
						}
					} else if(s.startsWith("dreadloc=")) //Adds a player start location to the list
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
						this.dreadnoughtSpawns.add(startLoc);
					} else if(s.startsWith("frigateloc=")) //Adds a frigate spawn location to the list
					{
						int b = 0, c = 11;
						CoordSet startLoc = new CoordSet(0, 0, 0);
						for(int i = 11; i < s.length(); i++)
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
						this.frigateSpawns.add(startLoc);
					} else if(s.startsWith("uid="))
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
	
	public int[] getMapDims()
	{
		return mapDims;
	}
	
	public WorldType getWorldType()
	{
		return worldType;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public List<CoordSet> getDreadnoughtSpawns()
	{
		return dreadnoughtSpawns;
	}
	
	public List<CoordSet> getFrigateSpawns()
	{
		return frigateSpawns;
	}
	
	public enum WorldType
	{
		HUB, SPAWN, GAMEWORLD;
	}
}
