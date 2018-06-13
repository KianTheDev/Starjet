package kian.towers.tower;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import kian.towers.attacks.AOEField;
import kian.towers.core.Processor;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Holds information on splash data for attack data. Makes AttackData class simpler by separating different damage components.
 * Splash affects everything within a double radius r, dealing damage relative to distance d according to damage*Math.min(1,2/rd^2).
 * Splash damage has its own separate damage so weapons can do bonus damage to their primary target.
 * Also contains methods for applying splash damage to enemies.
 */
public class SplashData 
{
	private double radius;
	private int damage;

	public SplashData(double radius, int damage)
	{
		this.radius = radius;
		this.damage = damage;
	}
	
	/***
	 * Adds an AOEField to the processor buffer matching data. 
	 * @param loc - Location of the center of splash damage
	 * @param particle - Particle type to be passed when calling the method. Used for generated AOEField.
	 */
	public void applySplash(Location loc, int damageLevel, ParticleTypeEnum particle, Plugin plugin)
	{
		new BukkitRunnable(){
		
			public void run()
			{
				Processor.addAOEField(new AOEField(true, radius, damage, damageLevel, particle, loc, null));
			}
			
		}.runTaskLater(plugin, 1);
		
	}
	
	public double getRadius()
	{
		return radius;
	}
	
	public int getDamage()
	{
		return damage;
	}
	
	public void setRadius(double d)
	{
		radius = d;
	}
	
	public void setDamage(int i)
	{
		damage = i;
	}
}
