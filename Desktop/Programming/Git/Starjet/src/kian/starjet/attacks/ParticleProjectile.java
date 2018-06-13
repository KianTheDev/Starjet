package kian.starjet.attacks;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import kian.starjet.ship.Ship;
import thekian.nms.protocol.Packets;
import thekian.nms.protocol.Particles;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Basic projectile made from particles. Is not affected by gravity and expires after a certain amount of time (age >= maxAge).
 */
public class ParticleProjectile 
{
	private Location location;
	private ParticleTypeEnum effect;
	private Vector vector;
	private int age, maxAge, data;
	private double damage;
	private float size;
	private Ship owner;
	private Sound sound;
	private boolean collision;
	
	public ParticleProjectile(double xp, double yp, double zp, ParticleTypeEnum effect, Vector vector, World w, int maxAge, float size, double damage, Ship owner, Sound sound, int data, boolean collision)
	{
		location = new Location(w, xp, yp, zp);
		this.effect = effect;
		this.vector = vector.normalize().multiply(0.05);
		this.maxAge = maxAge;
		age = 0;
		this.size = size;
		this.damage = damage;
		this.owner = owner;
		this.sound = sound;
		this.data = data;
		this.collision = collision;
	}
	
	public boolean getCollision()
	{
		return collision;
	}
	
	public Sound getSound()
	{
		return sound;
	}
	
	public Ship getOwner()
	{
		return owner;
	}
	
	public void setSize(float f)
	{
		size = f;
	}
	
	public void setX(double d)
	{
		location.setX(d);
	}
	
	public void setY(double d)
	{
		location.setY(d);
	}
	
	public void setZ(double d)
	{
		location.setZ(d);
	}
	
	public void setEffect(ParticleTypeEnum e)
	{
		effect = e;
	}
	
	public void setVector(Vector v)
	{
		vector = v;
	}
	
	public void incrementAge()
	{
		age += 1;
	}
	
	public float getSize()
	{
		return size;
	}

	public Location getLocation()
	{
		return location;
	}
	
	public ParticleTypeEnum getEffect()
	{
		return effect;
	}
	
	public Vector getVector()
	{
		return vector;
	}
	
	public int getMaxAge()
	{
		return maxAge;
	}
	
	public int getAge()
	{
		return age;
	}
	
	public void setAge(int i)
	{
		age = i;
	}
	
	public int getData()
	{
		return data;
	}
	
	public double getDamage()
	{
		return damage;
	}
	
	public void setDamage(int i)
	{
		damage = i;
	}
	
	public void drawToPlayer(Player p)
	{
		Packets.sendPacket(p, Particles.createParticle(true, effect, (float) location.getX(), (float) location.getY(), (float) location.getZ(), size, size, size, data, (int) Math.ceil(size * 50)));
	}
	
	public void move()
	{
		location.add(vector);
		incrementAge();
	}
	
	public static Vector generateVector(Location start, Location target, double speed)
	{
		if(start != null && target != null)
			return new Vector(target.getX() - start.getX(), target.getY() - start.getY(), target.getZ() - start.getZ()).normalize().multiply(speed);
		return null;
	}
}