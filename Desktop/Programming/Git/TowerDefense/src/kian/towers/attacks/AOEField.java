package kian.towers.attacks;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import kian.towers.tower.SplashData;
import thekian.nms.protocol.Packets;
import thekian.nms.protocol.Particles;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Emanates damage in a certain radius. Can do constant damage throughout the field or drop off over distance.
 */
public class AOEField
{
	private boolean dropOff;
	private double radius;
	private double damage;
	private int damageLevel;
	private ParticleTypeEnum particleType;
	private Location center;
	private SplashData splashData;
	
	public AOEField(boolean dropOff, double radius, double damage, int damageLevel, ParticleTypeEnum particleType, Location center, SplashData splashData)
	{
		this.dropOff = dropOff;
		this.radius = radius;
		this.damage = damage;
		this.damageLevel = damageLevel;
		this.particleType = particleType;
		this.center = center;
		this.splashData = splashData;
	}
	
	/***
	 * Applies the AOEField's damage effect to a LivingEntity.
	 * @param le - LivingEntity to damage.
	 */
	public void damage(LivingEntity le)
	{
		if(dropOff) //If dropOff is enabled, uses alternate formula to deal decreased damage at a distance
			le.damage(damage * Math.min(1, 2 / (radius * Math.pow(damage, 2))));
		else
			le.damage(damage);
		//Makes entity able to immediately take damage again
		le.setLastDamage(0);
		le.setNoDamageTicks(0);
	}
	
	public boolean getDropOff()
	{
		return dropOff;
	}
	
	public double getRadius()
	{
		return radius;
	}
	
	public double getDamage()
	{
		return damage;
	}
	
	public int getDamageLevel()
	{
		return damageLevel;
	}
	
	public ParticleTypeEnum getEffect()
	{
		return particleType;
	}

	public Location getCenter()
	{
		return center;
	}
	
	public SplashData getSplashData()
	{
		return splashData;
	}
	
	public void setDropOff(boolean b)
	{
		dropOff = b;
	}
	
	public void setRadius(double d)
	{
		radius = d;
	}
	
	public void setDamage(int i)
	{
		damage = i;
	}
	
	public void setSplashData(SplashData sd)
	{
		splashData = sd;
	}
	
	//Draws a circle of particles around the center
	public void drawToPlayer(Player p)
	{
		for(double d = 0.5; d <= radius; d += 0.5)
			for(double d2 = 0; d2 < Math.PI * 2; d2 += Math.PI / (16 * d))
				Packets.sendPacket(p, Particles.createParticle(true, particleType, (float) (d * Math.sin(d2) + center.getX()), (float) center.getY(), (float) (d * Math.cos(d2) + center.getZ()), 1, 1, 1, 0, 2));
	}
}
