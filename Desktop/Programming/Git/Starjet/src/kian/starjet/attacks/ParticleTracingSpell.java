package kian.starjet.attacks;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import kian.starjet.ship.Ship;
import thekian.nms.protocol.Packets;
import thekian.nms.protocol.Particles;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Tracing particle projectile with secondary particle effect for cosmetic purposes.
 */
public class ParticleTracingSpell extends ParticleTracing 
{
	private ParticleTypeEnum effect2;
	private int data2;
	private float size2;
	
	public ParticleTracingSpell(double xp, double yp, double zp, ParticleTypeEnum effect1, ParticleTypeEnum effect2, Vector vector, World world, int maxAge, float size1, float size2, double damage, int damageLevel, Ship owner, Sound sound, int data1, int data2, Ship target, Location targetLocation, boolean collision)
	{
		super(xp, yp, zp, effect1, vector, world, maxAge, size1, damage, owner, sound, data1, target, targetLocation, collision);
		this.effect2 = effect2;
		this.data2 = data2;
		this.size2 = size2;
	}
	
	@Override
	public void drawToPlayer(Player p) //Draws the normal particles and adds the second type of particle as well.
	{
		super.drawToPlayer(p);
		Packets.sendPacket(p, Particles.createParticle(true, effect2, (float) this.getLocation().getX(), (float) this.getLocation().getY(), (float) this.getLocation().getZ(), size2, size2, size2, data2, (int) Math.ceil(size2 * 50)));
	}
}