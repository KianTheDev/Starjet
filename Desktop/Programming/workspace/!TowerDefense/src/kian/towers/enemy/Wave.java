package kian.towers.enemy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

/***
 * This class stores information for a given wave. It contains three arrays, each corresponding to a different set of data.
 * The first array contains the MobData information, for health, damage resistance, damage dealt, and essence dropped.
 * The second array contains a set of integers for the number of each mob to be released.
 * The third array contains the corresponding mob type. The indices of each array correspond to each other, and they should consequently
 * all be the same length.
 * @author TheKian
 */
public class Wave
{
	private MobData[] mobData; //Mob data for each kind of enemy
	private int[] numbers; //Number of mobs in each wave
	private EntityType[] mobTypes; //Entity type of mob to spawn

	/***
	 * Loads the wave information from a data file.
	 * @param f - File from which to load.
	 */
	public Wave(File f)
	{
		loadData(f);
	}

	/***
	 * Constructs the wave from data passed into it.
	 * @param mobData - MobData array to pass
	 * @param numbers - Integer array to pass
	 * @param mobTypes - EntityType array to pass
	 */
	public Wave(MobData[] mobData, int[] numbers, EntityType[] mobTypes)
	{
		this.mobData = mobData;
		this.numbers = numbers;
		this.mobTypes = mobTypes;
	}
	
	public MobData[] getMobData()
	{
		return mobData;
	}
	
	public int[] getNumbers()
	{
		return numbers;
	}
	
	public EntityType[] getMobTypes()
	{
		return mobTypes;
	}

	/***
	 * Returns the number of sections of each wave.
	 */
	public int getSize()
	{
		return Math.min(numbers.length, Math.min(mobTypes.length, mobData.length)); //All parts of the wave are the same length
		//But in case they aren't, this will avoid breaking anything
	}
	
	/***
	 * Parses wave information from a file. Follows this format: for MobData, "md=int;int;int;int;"
	 * - for number, "num=int;" - for EntityType, "et=ENTITY_STRING;"
	 * @param f - File containing wave data to load
	 */
	public void loadData(File f)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(f.getAbsolutePath()));
			String s = ""; //File reader
			List<MobData> temp1 = new ArrayList<MobData>(); //Temporarily holds MobData
			List<Integer> temp2 = new ArrayList<Integer>(); //Temporarily holds numbers
			List<EntityType> temp3 = new ArrayList<EntityType>(); //Temporarily holds entity data
			try
			{
				while((s = reader.readLine()) != null)
				{
					if(s.startsWith("#")) //Comment data
						continue;
					else if(s.startsWith("md=")) //MobData loader
					{
						int tmp1 = 3, tmp2 = 0, tmp3 = 0, tmp4 = 0, tmp5 = 0, tmp6 = 0; float tmp7 = 0; //Temp data to cycle through line and load data
						for(int i = 3; i < s.length(); i++)
						{
							if(s.charAt(i) == ';') //Data is semicolon-separated data, e.g. md=10;3;2;10;
							{
								switch(tmp2)
								{
								case 0: //Health
									tmp3 = Integer.valueOf(s.substring(tmp1, i));
									tmp2 = 1;
									break;
								case 1: //Damage level
									tmp4 = Integer.valueOf(s.substring(tmp1, i));
									tmp2 = 2;
									break;
								case 2: //Damage
									tmp5 = Integer.valueOf(s.substring(tmp1, i));
									tmp2 = 3;
									break;
								case 3: //Essence
									tmp6 = Integer.valueOf(s.substring(tmp1, i));
									tmp2 = 4;
									break;
								case 4: //Speed
									tmp7 = Float.valueOf(s.substring(tmp1, i));
									break;
								default:
									break;
								}
								tmp1 = i + 1;
							}
						}
						temp1.add(new MobData(tmp3, tmp4, tmp5, tmp6, tmp7)); //Adds a new instance to temp list
					} else if(s.startsWith("num=")) //Loads wave sizes
					{
						for(int i = 4; i < s.length(); i++)
							if(s.charAt(i) == ';')
								temp2.add(new Integer(s.substring(4, i)));
					} else if(s.startsWith("et=")) //Loads entity types
					{
						for(int i = 3; i < s.length(); i++)
							if(s.charAt(i) == ';')
								temp3.add(EntityType.valueOf(s.substring(3, i)));
					}
				}
			} catch (IOException e)
			{
				Bukkit.getLogger().severe("There was an exception while reading file " + f.getAbsolutePath());
				e.printStackTrace();
			}

			//Assimilate temp data into instance
			mobData = new MobData[temp1.size()];
			for(int i = 0; i < temp1.size(); i++)
				mobData[i] = temp1.get(i);
			numbers = new int[temp2.size()];
			for(int i = 0; i < temp2.size(); i++)
				numbers[i] = temp2.get(i).intValue();
			mobTypes = new EntityType[temp3.size()];
			for(int i = 0; i < temp3.size(); i++)
				mobTypes[i] = temp3.get(i);
		} catch (FileNotFoundException e) 
		{
			Bukkit.getLogger().severe("Attempted to load wave file that didn't exist: " + f.getAbsolutePath());
			e.printStackTrace();
		}		
	}
}
