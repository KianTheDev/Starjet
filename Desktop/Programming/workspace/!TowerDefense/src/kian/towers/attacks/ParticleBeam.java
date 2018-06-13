package kian.towers.attacks;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import kian.towers.tower.SplashData;
import thekian.nms.protocol.Packets;
import thekian.nms.protocol.Particles;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Particle beam attack. Holds all data necessary for damage processing and rendering.
 * @author TheKian
 */
public class ParticleBeam 
{

	private Location startPoint;
	private ParticleTypeEnum effect;
	private Sound sound;
	private Vector direction;
	private double damage;
	private double length, maxLength, effectChance;
	private float size, data;
	private int damageLevel;
	private LivingEntity owner;
	//private ArrayList<ParticleSFX> effects = new ArrayList<ParticleSFX>();
	private boolean collision;
	private SplashData splashData;
	
	public ParticleBeam(Location loc, ParticleTypeEnum effect, Vector direction, double maxLength, double damage, int damageLevel, float size, float data, Sound sound, LivingEntity owner, double effectChance, SplashData splashData, boolean collision)
	{
		startPoint = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		this.effect = effect;
		this.direction = direction;
		this.maxLength = maxLength;
		length = 0;
		this.damage = damage;
		this.sound = sound;
		this.owner = owner;
		this.size = size;
		this.data = data;
		this.effectChance = effectChance;
		this.damageLevel = damageLevel;
		this.splashData = splashData;
		this.collision = collision;
	}
	
	/***
	 * Alternate constructor which automatically calculates vector toward a given target.
	 */
	public ParticleBeam(Location loc, ParticleTypeEnum effect, double maxLength, double damage, float size, float data, Sound sound, LivingEntity owner, double effectChance, SplashData splashData, LivingEntity target, boolean collision)
	{
		startPoint = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		this.effect = effect;
		this.maxLength = maxLength;
		length = 0;
		this.damage = damage;
		this.sound = sound;
		this.owner = owner;
		this.size = size;
		this.data = data;
		this.effectChance = effectChance;
		direction = targetVector(loc, new Location(target.getWorld(), target.getLocation().getX(), target.getLocation().getY(), target.getLocation().getZ())).multiply(0.1);
		this.splashData = splashData;
		this.collision = collision;
	}
	
	public boolean getCollision()
	{
		return collision;
	}
	
	public double getEffectChance()
	{
		return effectChance;
	}
	
	public Entity getOwner()
	{
		return owner;
	}
	
	public Sound getSound()
	{
		return sound;
	}
	
	public SplashData getSplashData()
	{
		return splashData;
	}
	
	public Location getStartPoint()
	{
		return startPoint;
	}
	
	public ParticleTypeEnum getEffect()
	{
		return effect;
	}
	
	public Vector getDirecton()
	{
		return direction;
	}
	
	public double getLength()
	{
		return length;
	}
	
	public double getMaxLength()
	{
		return maxLength;
	}
	
	public double getDamage()
	{
		return damage;
	}
	
	public int getDamageLevel()
	{
		return damageLevel;
	}
	
	public float getData()
	{
		return data;
	}
	
	/***
	 * Returns vector in the direction of a given target location.
	 */
	public Vector targetVector(Location loc1, Location loc2)
	{
		return new Vector(loc2.getX() - loc1.getX(), loc2.getY() - loc1.getY(), loc2.getZ() - loc1.getZ()).normalize();
	}
	
	public void addLength(double i)
	{
		length += i;
	}
	
	public void setDamage(int i)
	{
		damage = i;
	}
	
	public void drawToPlayer(Player p, Location location)
	{
		Packets.sendPacket(p, Particles.createParticle(true, effect, (float) location.getX(), (float) location.getY(), (float) location.getZ(), size, size, size, data, (int) Math.ceil(size * 50)));
	}
}