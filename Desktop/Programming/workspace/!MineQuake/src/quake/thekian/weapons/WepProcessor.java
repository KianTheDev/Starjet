package quake.thekian.weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.archenai.sfx.InstantBurstSFX;
import com.archenai.sfx.ParticleSFX;

import me.kian.particles.ParticleBeam;
import me.kian.particles.ParticleProjectile;
import me.kian.particles.ParticleSpell;
import me.kian.particles.ParticlesMain;
import quake.thekian.weapons.AttackData.ArmorStandData;
import quake.thekian.weapons.AttackData.InstantHitData;
import quake.thekian.weapons.AttackData.ParticleData;
import quake.thekian.weapons.AttackData.ProjectileData;
import quake.thekian.weapons.WepData.ParticleTypes;
import quake.thekian.weapons.WepData.ProjectileType;

public class WepProcessor 
{
	static List<ParticleSFX> bufferSFXList = new ArrayList<ParticleSFX>();
	static List<AttackBase> bufferAttackDataList = new ArrayList<AttackBase>();
	static HashMap<Projectile, ProjectileData> bufferProjectileData = new HashMap<Projectile, ProjectileData>();
	static ArrayList<ArmorStandProjectile> bufferArmorStandData = new ArrayList<ArmorStandProjectile>();
	static AttackData atd;
	
	public static List<ParticleSFX> retrieveSFXBuffer()
	{
		return bufferSFXList;
	}
	
	public static HashMap<Projectile, ProjectileData> retrieveProjectileDataBuffer()
	{
		return bufferProjectileData;
	}
	
	public static ArrayList<ArmorStandProjectile> retrieveArmorStandDataBuffer()
	{
		return bufferArmorStandData;
	}
	
	public static void purgeLists()
	{
		bufferSFXList.clear();
		bufferAttackDataList.clear();
		bufferProjectileData.clear();
		bufferArmorStandData.clear();
	}
	
	public static void processWeaponUsage(Weapon weapon, Player p, List<LivingEntity> affected)
	{
		System.out.println("Affected: " + affected.size());
		if(weapon.getWeaponData() instanceof ArmorStandData)
		{
			ArmorStandData asd = (ArmorStandData) weapon.getWeaponData();
			Vector v = p.getLocation().getDirection().normalize().multiply(0.05 * weapon.getVelocity());
			Location loc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()).add(v);
			ArmorStandProjectile asp = new ArmorStandProjectile(loc, asd.armorStandCopy(), v, (int) (weapon.getRange() * 20 / weapon.getVelocity()), p);
			bufferArmorStandData.add(asp);
		} else if(weapon.getWeaponData() instanceof InstantHitData)
		{
			InstantHitData ihd = (InstantHitData) weapon.getWeaponData();
			for(int i = 0; i < ihd.getHits(); i++)
			{
				Location loc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + p.getEyeHeight() - 0.2, p.getLocation().getZ());
				Vector v = p.getLocation().getDirection().normalize();
				if(ihd.getRadianSpread() > 0)
				{
					v.setX(v.getX() + (Math.random() * (ihd.getRadianSpread() * 2)) - ihd.getRadianSpread());
					v.setY(v.getY() + (Math.random() * (ihd.getRadianSpread() * 2)) - ihd.getRadianSpread());
					v.setZ(v.getZ() + (Math.random() * (ihd.getRadianSpread() * 2)) - ihd.getRadianSpread());
				}
				v = v.normalize().multiply(0.2);
				boolean b2 = false;
				for(double i2 = 0; i2 <= weapon.getRange(); i2 += 0.2)
				{
					loc.add(v);
					boolean b = false;
					for(LivingEntity le : affected)
					{
						if(le.equals(p))
							continue;
						Location loc2 = new Location(le.getLocation().getWorld(), le.getLocation().getX(), le.getLocation().getY(), le.getLocation().getZ());
						//if(loc3.distance(loc) <= Math.max(1.2 + d1, le.getEyeHeight() / 1.3) && !e.equals(pb.getOwner()))
						//Checks if X is within desired radius		//Checks if Z is within desired radius	//Checks if hit is above feet	//Checks if hit is below head approximation height			
						if(Math.abs(loc2.getX() - loc.getX()) < 0.8 && Math.abs(loc2.getZ() - loc.getZ()) < 0.8 && loc.getY() >= loc2.getY() && loc.getY() <= loc2.getY() + le.getEyeHeight() + 0.2)
						{
							le.setLastDamage(-1);
							le.damage(ihd.getDamageData(0));
							le.setNoDamageTicks(0);
							ParticlesMain.addParticleSFX(new InstantBurstSFX(ihd.getHitEffect(), (float) ihd.getSize(), loc, ihd.getDensity(), ihd.getHardNum()));
							b = true;
							b2 = true;
							break;
						}
					}
					if(b)
						break;
					BlockIterator iterator = new BlockIterator(loc.getWorld(), loc.toVector(), v.normalize(), 0.0D, 1);
					Block bl = null;
					while (iterator.hasNext()) 
					{
						bl = iterator.next();
						if(!bl.getType().equals(Material.AIR) && bl.getType().isSolid())
						{
							ParticlesMain.addParticleSFX(new InstantBurstSFX(ihd.getHitEffect(), (float) ihd.getSize(), loc, ihd.getDensity(), ihd.getHardNum()));
							b = true;
							b2 = true;
							break;
						}
					}
					if(b || b2)
						break;
				}
				if(!b2)
					ParticlesMain.addParticleSFX(new InstantBurstSFX(ihd.getHitEffect(), (float) ihd.getSize(), loc, ihd.getDensity(), ihd.getHardNum()));
			}
		} else if(weapon.getWeaponData() instanceof ProjectileData)
		{
			ProjectileData pd = (ProjectileData) weapon.getWeaponData();
			Location loc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + p.getEyeHeight() - 0.2, p.getLocation().getZ());
			loc.add(p.getLocation().getDirection().normalize());
			Projectile proj = p.launchProjectile(pd.getProjectileClass(), p.getLocation().getDirection().multiply(weapon.getVelocity()));
			proj.setGravity(false);
			proj.setGlowing(((ProjectileData) weapon.getWeaponData()).getGlows());
			bufferProjectileData.put(proj, ((ProjectileData) WepData.getAttackData().get(weapon.getWeaponType())).projectileCopy()); 
		} else if(weapon.getWeaponData() instanceof ParticleData)
		{
			ParticleData pd = (ParticleData) weapon.getWeaponData();
			if(pd.getParticleType().equals(ParticleTypes.BEAM))
			{
				Location loc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + p.getEyeHeight() - 0.2, p.getLocation().getZ());
				loc.add(p.getLocation().getDirection().normalize().multiply(0.4));
				ParticlesMain.addBeam(new ParticleBeam(loc, pd.getEffectType(), p.getLocation().getDirection().normalize().multiply(0.05), weapon.getRange(), (float) pd.getDamageData(0), (float) pd.getSize(), 0, pd.getHitSound(), p, 0.01, null, pd.getDoesSplash(), pd.getSplashData(0), pd.getSplashData(1), pd.getSplashData(2)));
			} else if(pd.getParticleType().equals(ParticleTypes.SPELL))
			{
				Location loc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + p.getEyeHeight() - 0.2, p.getLocation().getZ());
				loc.add(p.getLocation().getDirection().normalize().multiply(0.4));
				ParticlesMain.addProjectile(new ParticleSpell(loc.getX(), loc.getY(), loc.getZ(), pd.getEffectType(), pd.getEffectType(), p.getLocation().getDirection().multiply(0.05 * weapon.getVelocity()), loc.getWorld(), (int) (weapon.getRange() * 20 * weapon.getVelocity()), (float) pd.getSize(), (float) pd.getSize(), (int) pd.getDamageData(0), p, pd.getHitSound(), 0, 0, null, pd.getDoesSplash(), pd.getSplashData(0), pd.getSplashData(1), pd.getSplashData(2)));
			} else if(pd.getParticleType().equals(ParticleTypes.SIMPLE))
			{
				Location loc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + p.getEyeHeight() - 0.2, p.getLocation().getZ());
				loc.add(p.getLocation().getDirection().normalize().multiply(0.4));
				ParticlesMain.addProjectile(new ParticleProjectile(loc.getX(), loc.getY(), loc.getZ(), pd.getEffectType(), p.getLocation().getDirection().multiply(0.05 * weapon.getVelocity()), loc.getWorld(), (int) (weapon.getRange() * 20 * weapon.getVelocity()), (float) pd.getSize(), (int) pd.getDamageData(0), p, pd.getHitSound(), 0, null, pd.getDoesSplash(), pd.getSplashData(0), pd.getSplashData(1), pd.getSplashData(2)));
			}
		} else 
		{
			
		}
	}
}
