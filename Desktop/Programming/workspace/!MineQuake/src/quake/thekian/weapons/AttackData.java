package quake.thekian.weapons;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.archenai.sfx.InstantBurstSFX;

import quake.thekian.weapons.AttackData.ArmorStandData;
import quake.thekian.weapons.WepData.ParticleTypes;
import quake.thekian.weapons.WepData.ProjectileType;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

public class AttackData 
{
	public class ProjectileData extends AttackBase
	{
		private Class<? extends Projectile> projectileClass;
		private boolean particleTrail, glows;
		private int trailSize;
		private ParticleTypeEnum trailType;
		
		public ProjectileData(double damage, int knockback, int burn, //Damage data
				ProjectileType projectileType, boolean splash, //Misc data
				double splash100, double splash50, double splash25, //Splash data
				Class<? extends Projectile> projectileClass, boolean particleTrail, boolean glows, int trailSize, ParticleTypeEnum trailType)
		{
			super(damage, knockback, burn, projectileType, splash, splash100, splash50, splash25);
			this.projectileClass = projectileClass;
			this.particleTrail = particleTrail;
			this.trailSize = trailSize;
			this.trailType = trailType;
			this.glows = glows;
		}
		
		public ProjectileData projectileCopy()
		{
			return new ProjectileData(this.getDamageData(0), (int) this.getDamageData(2), (int) this.getDamageData(1), 
					this.getProjectileType(), this.getDoesSplash(), this.getSplashData(0), this.getSplashData(1),
					this.getSplashData(2), projectileClass, particleTrail, glows, trailSize, trailType);
		}
		
		public Class<? extends Projectile> getProjectileClass()
		{
			return projectileClass;
		}
		
		public boolean getParticleTrail()
		{
			return particleTrail;
		}
		
		public boolean getGlows()
		{
			return glows;
		}
		
		public int getTrailSize()
		{
			return trailSize;
		}
		
		public ParticleTypeEnum getTrailType()
		{
			return trailType;
		}
	}
	
	public class ParticleData extends AttackBase
	{
		private ParticleTypes particleType;
		private ParticleTypeEnum effectType;
		private double size;
		private Sound hitSound;
		
		public ParticleData(double damage, int knockback, int burn, //Damage data
				ProjectileType projectileType, boolean splash, //Misc data
				double splash100, double splash50, double splash25, //Splash data
				ParticleTypes particleType, ParticleTypeEnum effectType, Sound hitSound)
		{
			super(damage, knockback, burn, projectileType, splash, splash100, splash50, splash25);
			this.particleType = particleType;
			this.effectType = effectType;
			this.hitSound = hitSound;
		}
		
		public ParticleData particleCopy()
		{
			return new ParticleData(this.getDamageData(0), (int) this.getDamageData(2), (int) this.getDamageData(1), 
					this.getProjectileType(), this.getDoesSplash(), this.getSplashData(0), this.getSplashData(1),
					this.getSplashData(2), particleType, effectType, hitSound);
		}
		
		public ParticleTypes getParticleType()
		{
			return particleType;
		}
		
		public ParticleTypeEnum getEffectType()
		{
			return effectType;
		}
		
		public Sound getHitSound()
		{
			return hitSound;
		}
		
		public double getSize()
		{
			return size;
		}
	}
	
	public class ArmorStandData extends AttackBase
	{
		private Material blockType;
		private boolean onHead;
		private boolean bounces, gravity;
		private int data, bounceNum;
		
		public ArmorStandData(double damage, int knockback, int burn, //Damage data
				ProjectileType projectileType, boolean splash, //Misc data
				double splash100, double splash50, double splash25, //Splash data
				Material blockType, int data, boolean onHead, boolean bounces, int bounceNum, boolean gravity)
		{
			super(damage, knockback, burn, projectileType, splash, splash100, splash50, splash25);
			this.blockType = blockType;
			this.data = data;
			this.onHead = onHead;
			this.bounces = bounces;
			this.gravity = gravity;
			this.bounceNum = bounceNum;
		}
		
		public ArmorStandData armorStandCopy()
		{
			return new ArmorStandData(this.getDamageData(0), (int) this.getDamageData(2), (int) this.getDamageData(1),
				this.getProjectileType(), this.getDoesSplash(), this.getSplashData(0), this.getSplashData(1), 
				this.getSplashData(2), blockType, data, onHead, bounces, bounceNum, gravity);
		}
		
		public boolean getOnHead()
		{
			return onHead;
		}
		
		public int getData()
		{
			return data;
		}
		
		public boolean getGravity()
		{
			return gravity;
		}
		
		public ItemStack getItem()
		{
			ItemStack is = new ItemStack(blockType);
			is.setDurability((short) data);
			return is;
		}
		
		public int getBounces()
		{
			return bounceNum;
		}
		
		public boolean getDoesBounce()
		{
			return bounces;
		}
		
		public Vector bounce(Vector initial)
		{
			if(bounceNum > 0)
			{
				bounceNum--;
				return new Vector(-1 * initial.getX() + (Math.random() * 2 - 1), -1 * initial.getY() + (Math.random() * 2 - 1), -1 * initial.getZ() + (Math.random() * 2 - 1));
			}
			return null;
		}
	}

	public class InstantHitData extends AttackBase
	{
		private ParticleTypeEnum hitEffect;
		private double size, density, radianSpread;
		private int hardNum, hits;
		
		public InstantHitData(double damage, int knockback, int burn, //Damage data
				ProjectileType projectileType, boolean splash, //Misc data
				double splash100, double splash50, double splash25, //Splash data
				ParticleTypeEnum hitEffect, double size, double density, int hardNum, //Hit particle effect data
				int hits, double radianSpread) //For shotgun
		{
			super(damage, knockback, burn, projectileType, splash, splash100, splash50, splash25);
			this.hitEffect = hitEffect;
			this.size = size;
			this.density = density;
			this.hardNum = hardNum;
			this.hits = hits;
			this.radianSpread = radianSpread;
		}
		
		public InstantHitData instantHitCopy()
		{
			return new InstantHitData(this.getDamageData(0), (int) this.getDamageData(2), (int) this.getDamageData(1), 
					this.getProjectileType(), this.getDoesSplash(), this.getSplashData(0), this.getSplashData(1),
					this.getSplashData(2), hitEffect, size, density, hardNum, hits, radianSpread);
		}
		
		public ParticleTypeEnum getHitEffect()
		{
			return hitEffect;
		}
		
		public int getHits()
		{
			return hits;
		}
		
		public double getRadianSpread()
		{
			return radianSpread;
		}
		
		public double getSize()
		{
			return size;
		}
		
		public double getDensity()
		{
			return density;
		}
		
		public int getHardNum()
		{
			return hardNum;
		}
	}
}
