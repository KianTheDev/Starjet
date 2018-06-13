package kian.towers.tower;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import kian.towers.core.CoordSet;

/***
 * Custom schematic design to load towers.
 */
public class Schematic
{
	private HashMap<CoordSet, BlockData> structure;
	private int xDim, yDim, zDim;
	private int[] minMax;
	
	/***
	 * Generates the schematic automatically from a file. Intended use: One-off loading into memory at plugin enable.
	 * @param f - File containing schematic data
	 */
	public Schematic(File f)
	{
		System.out.println("TRYING TO LOAD " + f.getAbsolutePath());
		try {
			loadSchematicFromFile(f);
		} catch (IOException e) 
		{
			Bukkit.getLogger().severe("Could not load schematic from file " + f.getAbsolutePath() + " -- check your plugins\\TowerData\\ folder");
			e.printStackTrace();
		}
	}
	
	public Schematic()
	{
		structure = new HashMap<CoordSet, BlockData>();
		xDim = 0;
		yDim = 0;
		zDim = 0;
	}
	
	public HashMap<CoordSet, BlockData> getStructure()
	{
		return structure;
	}
	
	/***
	 * Creates a schematic from a selected region of the world.
	 * @param loc - Center alignment of the structure
	 * @param corner1 - Corner of the structure
	 * @param corner2 - Opposite corner of the structure
	 */
	public void generateSchematic(Location loc, CoordSet corner1, CoordSet corner2)
	{
		if(structure == null)
			structure = new HashMap<CoordSet, BlockData>();
		structure.clear(); //Remove any existing data
		//x mins and maxes
		int x1 = (int) (corner1.getX() - loc.getX()), x2 = (int) (corner2.getX() - loc.getX());
		int xmin = Math.min(x1, x2);
		int xmax = Math.max(x1, x2);
		//y mins and maxes
		int y1 = (int) (corner1.getY() - loc.getY()), y2 = (int) (corner2.getY() - loc.getY());
		int ymin = Math.min(y1, y2);
		int ymax = Math.max(y1, y2);
		//z mins and maxes
		int z1 = (int) (corner1.getZ() - loc.getZ()), z2 = (int) (corner2.getZ() - loc.getZ());
		int zmin = Math.min(z1, z2);
		int zmax = Math.max(z1, z2);
		for(int ix = xmin; ix <= xmax; ix++)
			for(int iz = zmin; iz <= zmax; iz++)
				for(int ih = ymin; ih <= ymax; ih++)
				{
					Block b = loc.getWorld().getBlockAt((int) loc.getX() + ix, (int) loc.getY() + ih, (int) loc.getZ() + iz);
					structure.put(new CoordSet(ix, ih, iz), new BlockData(b.getType(), b.getData()));
				}
		System.out.println("Schematic generation complete. Dimensions: " + (xmax-xmin+1) + " x " + (ymax-ymin+1) + " x " + (zmax-zmin+1));
		System.out.println("x1, x2, y1, y2, z1, z2: " + x1 + " " + x2 + " " + y1 + " " + y2 + " " + z1 + " " + z2);
	}
	
	/***
	 * A utility method for server owners to export the tower schematic easily after generation.
	 * @param f - File to contain schematic.
	 * @throws IOException If there is an error in file saving (e.g. permissions)
	 */
	public void saveSchematic(File f) throws IOException
	{
		f.createNewFile();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8));
		System.out.println(structure.size());
		for(CoordSet cs : structure.keySet())
		{
			BlockData bd = structure.get(cs);
			int id = bd.mat.getId(); //Usage of deprecated methods to save file space
			int id1 = id & 255;
			int id2 = id >> 8;
			bw.write((char) id1); //Lower 8 bits
			bw.write((char) id2); //Higher 8 bits
			bw.write((char) bd.dat); //Data value
			//Math.max(0, Math.min(cs.getX() + 127, 255)) -- Gives coordinate a value between -127 and 128 and prevents values below 0 or above 255
			int x = (int) Math.max(0, Math.min(cs.getX() + 127, 255));
			int y = (int) Math.max(0, Math.min(cs.getY() + 127, 255));
			int z = (int) Math.max(0, Math.min(cs.getZ() + 127, 255));
			bw.write((char) x); //Relative coordinates
			bw.write((char) y);
			bw.write((char) z);
			System.out.println("Values: x, y, z = " + x + " " + y + " " + z);
			System.out.println(((char) x) == '?');
			System.out.println(((char) y) == '?');
			System.out.println(((char) z) == '?');
		}
		bw.close();
	}
	
	/***
	 * Loads a schematic for a tower from a file, following a six-byte pattern for each block: a sixteen bit ID value, eight bit data value,
	 * and three signed bytes for relative position.
	 * 
	 * @param f - File containing schematic data to be loaded.
	 * @throws IOException If the file does not exist or if there is an error in file reading.
	 *  
	 */
	public void loadSchematicFromFile(File f) throws IOException
	{
		System.out.println("Attempting to load " + f.getAbsolutePath());

		if(structure == null)
			structure = new HashMap<CoordSet, BlockData>();
		structure.clear(); //Remove any existing data
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8));
		String s, buffer = "";
		while((s = br.readLine()) != null)
		{
			buffer += s;
		}
		br.close();
		s = ""; //Clear s
		for(int index = 0; index < buffer.length(); index += 6)
		{
			if(buffer.length() / 6.0 != Math.floor(buffer.length() / 6.0)) //If the file isn't in six-byte chunks, there's a problem.
			{
				Bukkit.getLogger().log(java.util.logging.Level.SEVERE, "Invalid file structure in schematic " + f.getAbsolutePath());
				break;
			}
			int id1 = (int) buffer.charAt(index);
			int id2 = (int) buffer.charAt(index + 1);
			int dat = (int) buffer.charAt(index + 2);
			int x = ((int) buffer.charAt(index + 3)) - 127;
			int y = ((int) buffer.charAt(index + 4)) - 127;
			int z = ((int) buffer.charAt(index + 5)) - 127;
			structure.put(new CoordSet(x, y, z), new BlockData(Material.getMaterial(id1 + 256 * id2), dat)); //Until Mojang breaks the ID system, it's the most size-efficient way to go.
		}
		buffer = ""; //Clear buffer
		reloadDimensions();
		System.out.println("Dimensions: " + xDim + ", " + yDim + ", " + zDim);
	}
	
	/***
	 * Generates the dimensions of the schematic from its block list.
	 * Useful for determining whether towers overlap when attempting placement. 
	 */
	public void reloadDimensions()
	{
		int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE, zMin = Integer.MAX_VALUE;
		int xMax = Integer.MIN_VALUE, yMax = Integer.MIN_VALUE, zMax = Integer.MIN_VALUE;
		for(CoordSet cs : structure.keySet())
		{
			if(cs.getX() > xMax)
				xMax = (int) cs.getX();
			else if(cs.getX() < xMin)
				xMin = (int) cs.getX();
			if(cs.getY() > yMax)
				yMax = (int) cs.getY();
			else if(cs.getY() < yMin)
				yMin = (int) cs.getY();
			if(cs.getZ() > zMax)
				zMax = (int) cs.getZ();
			else if(cs.getZ() < zMin)
				zMin = (int) cs.getZ();
		}
		minMax = new int[]{xMin, xMax, yMin, yMax, zMin, zMax};
		xDim = xMax - xMin + 1;
		yDim = yMax - yMin + 1;
		zDim = zMax - zMin + 1;
	}
	
	/***
	 * Returns an array with the dimensions of the structure as {x,y,z}.
	 * @return Array with dimensions
	 */
	public int[] getDimensions()
	{
		return new int[]{xDim, yDim, zDim};
	}
	
	/***
	 * Returns an array with the minimum and maximum values of each coordinate.
	 * @return Integer array of pattern {xMin, xMax, yMin, yMax, zMin, zMax}
	 */
	public int[] getMinMax()
	{
		return minMax;
	}
}
