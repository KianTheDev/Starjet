package kian.towers.tower;

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
}
