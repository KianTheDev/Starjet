package quake.thekian.main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import com.archenai.sfx.ParticleSFX;

import quake.thekian.weapons.ArmorStandProjectile;

public class ArmorStandProcessor 
{
	public static void process(List<ArmorStandProjectile> processed, List<LivingEntity> affected, List<UUID> immunePlayers)
	{
		List<ArmorStandProjectile> toRemove = new ArrayList<ArmorStandProjectile>();
		for(ArmorStandProjectile asp : processed)
		{
			boolean b = false;
			if(asp.incrementAge())
			{
				asp.kill();
				toRemove.add(asp);
				continue;
			}
			for(LivingEntity le : affected)
			{
				if(customDistance(asp.getProjectile().getLocation(), le) < 1 && !(le instanceof Player && immunePlayers.contains(((Player) le).getUniqueId())))
				{
					if(asp.getAttackData().getDoesSplash())
					{
						for(LivingEntity le2 : affected)
						{				
							if(le2 instanceof Player && immunePlayers.contains((((Player) le).getUniqueId())))
								continue;
							if(customDistance(asp.getProjectile().getLocation(), le2) <= asp.getAttackData().getSplashData(0))
							{
								if(asp.getAttackData().getDamageData(0) >= le.getHealth())
									le2.damage(9001, asp.getOwner());
								else
								{
									le2.setHealth(le2.getHealth() - (asp.getAttackData().getDamageData(0)));
									le2.setLastDamage(-1);
									le2.damage(0, asp.getOwner());
								}
							} else if(customDistance(asp.getProjectile().getLocation(), le2) <= asp.getAttackData().getSplashData(1))
							{
								if(asp.getAttackData().getDamageData(0) >= le.getHealth())
									le2.damage(9001, asp.getOwner());
								else
								{
									le2.setHealth(le2.getHealth() - (asp.getAttackData().getDamageData(0) / 0.5));
									le2.setLastDamage(-1);
									le2.damage(0, asp.getOwner());
								}
							} else if(customDistance(asp.getProjectile().getLocation(), le2) <= asp.getAttackData().getSplashData(2))
							{
								if(asp.getAttackData().getDamageData(0) >= le.getHealth())
									le2.damage(9001, asp.getOwner());
								else
								{
									le2.setHealth(le2.getHealth() - (asp.getAttackData().getDamageData(0) / 0.25));
									le2.setLastDamage(-1);
									le2.damage(0, asp.getOwner());
								}
							}
						}
					}
					else
					{
						if(asp.getAttackData().getDamageData(0) >= le.getHealth())
							le.damage(9001, asp.getOwner());
						else
						{
							le.setHealth(le.getHealth() - asp.getAttackData().getDamageData(0));
							le.setLastDamage(-1);
							le.damage(0, asp.getOwner());
						}
					}
					asp.kill();
					toRemove.add(asp);
					b = true;
					break;
				}	
			}
			if(!b)
			{
				try {
					Location l2 = asp.getProjectile().getLocation();
					BlockIterator iterator = new BlockIterator(asp.getProjectile().getWorld(), new Location(asp.getProjectile().getWorld(), l2.getX(), l2.getY() + 1, l2.getZ()).toVector(), asp.getVelocity().normalize(), 0.0D, 1);
					Block bl = null;
					if(b == false)
						while (iterator.hasNext()) 
						{
							bl = iterator.next();
							if(!bl.getType().equals(Material.AIR) && bl.getType().isSolid())
							{
								if(asp.getAttackData().getBounces() <= 0)
								{
									if(asp.getAttackData().getDoesSplash())
										for(LivingEntity le2 : affected)
										{				
											if(le2 instanceof Player && immunePlayers.contains((((Player) le2).getUniqueId())))
												continue;
											if(customDistance(asp.getProjectile().getLocation(), le2) <= asp.getAttackData().getSplashData(0))
											{
												le2.setHealth(le2.getHealth() - (asp.getAttackData().getDamageData(0)));
												le2.damage(0, asp.getOwner());
											} else if(customDistance(asp.getProjectile().getLocation(), le2) <= asp.getAttackData().getSplashData(1))
											{
												le2.setHealth(le2.getHealth() - (asp.getAttackData().getDamageData(0) / 0.5));
												le2.damage(0, asp.getOwner());
											} else if(customDistance(asp.getProjectile().getLocation(), le2) <= asp.getAttackData().getSplashData(2))
											{
												le2.setHealth(le2.getHealth() - (asp.getAttackData().getDamageData(0) / 0.25));
												le2.damage(0, asp.getOwner());
											}
										}
									asp.kill();
									toRemove.add(asp);
								}
								else
								{
									asp.setVelocity(asp.getAttackData().bounce(asp.getVelocity()));
									asp.getProjectile().teleport(asp.getProjectile().getLocation().add(asp.getVelocity().normalize()));
								}
								break;
							} 
						}
				} catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			asp.getProjectile().teleport(asp.getProjectile().getLocation().add(asp.getVelocity()));
		}
		for(ArmorStandProjectile asp : toRemove)
		{
			if(processed.contains(asp))
				processed.remove(asp);
		}
	}

	private static double customDistance(Location loc, LivingEntity le)
	{
		if(le != null)
		{
			return loc.distance(new Location(le.getWorld(), le.getLocation().getX(), le.getEyeHeight() / 2 + le.getLocation().getY() + 0.2, le.getLocation().getZ()));
		}
		return 0;
	}
}
