package kian.starjet.util;

import org.bukkit.Material;
/***
 * Used for simple schematic utility.
 */
public class BlockData
{
	protected Material mat;
	protected int dat;
	
	public BlockData(Material mat, int dat)
	{
		this.mat = mat;
		this.dat = dat;
	}
	
	public BlockData(){}
	
	public Material getMaterial()
	{
		return mat;
	}
	
	public int getData()
	{
		return dat;
	}
	
	public void setData(int i)
	{
		dat = i;
	}
}
