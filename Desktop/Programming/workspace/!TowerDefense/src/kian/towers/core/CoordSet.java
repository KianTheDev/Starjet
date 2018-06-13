package kian.towers.core;

/***
 * More memory-efficient class for coordinate storage than Bukkit's Location. Stores non-world-specific locations.
 * @author TheKian
 *
 */
public class CoordSet 
{
	private double x, y, z;
	
	/***
	 * Basic coordset constructor. Initializes data for xyz location.
	 * @param x
	 * @param y
	 * @param z
	 */
	public CoordSet(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX()
	{
		return this.x;
	}
	
	public double getY()
	{
		return this.y;
	}
	
	public double getZ()
	{
		return this.z;
	}
	
	public void setX(double d)
	{
		this.x = d;
	}
	
	public void setY(double d)
	{
		this.y = d;
	}
	
	public void setZ(double d)
	{
		this.z = d;
	}
}
