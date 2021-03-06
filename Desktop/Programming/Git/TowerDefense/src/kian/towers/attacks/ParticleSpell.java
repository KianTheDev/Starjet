package kian.towers.attacks;

import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import kian.towers.tower.SplashData;
import thekian.nms.protocol.Packets;
import thekian.nms.protocol.Particles;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

public class ParticleSpell extends ParticleProjectile
{
	private ParticleTypeEnum effect2;
	private int data2;
	private float size2;

	public ParticleSpell(double xp, double yp, double zp, ParticleTypeEnum effect, Vector vector, World world, int maxAge, float size, double damage, int damageLevel, LivingEntity owner, Sound sound, int data, SplashData splashData, ParticleTypeEnum effect2, int data2, float size2, boolean collision)
	{
		super(xp, yp, zp, effect, vector, world, maxAge, size, damage, damageLevel, owner, sound, data, splashData, collision);
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
