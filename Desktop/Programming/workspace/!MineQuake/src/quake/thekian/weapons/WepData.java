package quake.thekian.weapons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import quake.thekian.weapons.AttackData.ProjectileData;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

import org.bukkit.inventory.ItemStack;

public class WepData 
{
	static private HashMap<WeaponType, AttackBase> attackDataMap = new HashMap<WeaponType, AttackBase>();
	static private HashMap<WeaponType, WeaponData> weaponDataMap = new HashMap<WeaponType, WeaponData>();
	static private HashMap<String, WeaponType> nameDataMap = new HashMap<String, WeaponType>();
	static AttackData atd = new AttackData();
	static WepData wpd = new WepData();
	
	public class WeaponData
	{
		protected AttackBase weaponData;
		protected double velocity, range;
		protected int cooldown, data;
		protected String name, displayName;
		protected Material materialType;
		protected WeaponType weaponType;
		
		public WeaponData(AttackBase weaponData, double velocity, int cooldown, String name, String displayName, Material materialType, int data, WeaponType weaponType, double range)
		{
			this.weaponData = weaponData;
			this.velocity = velocity;
			this.cooldown = cooldown;
			this.name = name;
			this.displayName = displayName;
			this.materialType = materialType;
			this.data = data;
			this.weaponType = weaponType;
			this.range = range;
		}
		
		public Weapon createInstance()
		{
			return new Weapon(weaponData, velocity, cooldown, name, displayName, materialType, data, weaponType, range);
		}
	}
	
	//private HashMap<String, CWep> weaponDataMap = new HashMap<String, CWep>();
	
	static public void initialize()
	{
		nameDataMap.put(ChatColor.YELLOW + "Gauntlet", WeaponType.GAUNTLET);
		nameDataMap.put(ChatColor.YELLOW + "Machinegun", WeaponType.MACHINEGUN);
		nameDataMap.put(ChatColor.YELLOW + "Shotgun", WeaponType.SHOTGUN);
		nameDataMap.put(ChatColor.YELLOW + "Rocket Launcher", WeaponType.ROCKET_LAUNCHER);
		nameDataMap.put(ChatColor.YELLOW + "Grenade Launcher", WeaponType.GRENADE_LAUNCHER);
		nameDataMap.put(ChatColor.YELLOW + "Railgun", WeaponType.RAILGUN);
		nameDataMap.put(ChatColor.YELLOW + "Lightning Gun", WeaponType.LIGHTNING_GUN);
		nameDataMap.put(ChatColor.YELLOW + "Plasma Gun", WeaponType.PLASMAGUN);
		nameDataMap.put(ChatColor.YELLOW + "BFG 9000", WeaponType.BFG9K);
		
		//attackDataMap.put(<Weapon type>, <Attack Data>(Damage, knockback, burn, projectile type (enum), splash damage boolean, 100% damage radius, 50% damage radius, 25% damage radius));
		attackDataMap.put(WeaponType.GAUNTLET, atd.new InstantHitData(50, 0, 0, ProjectileType.INSTANT_HIT, false, 0, 0, 0, ParticleTypeEnum.CRIT, 0.5, 0, 35, 1, 0));
		attackDataMap.put(WeaponType.MACHINEGUN, atd.new InstantHitData(5, 0, 0, ProjectileType.INSTANT_HIT, false, 0, 0, 0, ParticleTypeEnum.SMOKE_LARGE, 0.1, 0, 5, 1, 0));
		attackDataMap.put(WeaponType.SHOTGUN, atd.new InstantHitData(10, 0, 0, ProjectileType.INSTANT_HIT, false, 0, 0, 0, ParticleTypeEnum.CRIT, 0.1, 0, 5, 11, 0.2));
		attackDataMap.put(WeaponType.ROCKET_LAUNCHER, atd.new ArmorStandData(100, 0, 0, ProjectileType.ARMOR_STAND, true, 0.4, 1.2, 3, Material.INK_SACK, 1, true, false, 0, false));
		attackDataMap.put(WeaponType.GRENADE_LAUNCHER, atd.new ArmorStandData(100, 0, 0, ProjectileType.ARMOR_STAND, true, 0.6, 1.5, 4, Material.TNT, 0, false, true, 3, true));
		attackDataMap.put(WeaponType.RAILGUN, atd.new ParticleData(100, 0, 0, ProjectileType.PARTICLE, false, 1, 2, 3, ParticleTypes.BEAM, ParticleTypeEnum.VILLAGER_HAPPY, Sound.BLOCK_IRON_DOOR_CLOSE));
		attackDataMap.put(WeaponType.LIGHTNING_GUN, atd.new ParticleData(8, 0, 0, ProjectileType.PARTICLE, false, 0, 0, 0, ParticleTypes.BEAM, ParticleTypeEnum.SPELL_INSTANT, Sound.BLOCK_FIRE_EXTINGUISH));
		attackDataMap.put(WeaponType.PLASMAGUN, atd.new ParticleData(20, 0, 0, ProjectileType.PARTICLE, false, 0, 0, 0, ParticleTypes.SIMPLE, ParticleTypeEnum.WATER_BUBBLE, Sound.BLOCK_NOTE_PLING));
		attackDataMap.put(WeaponType.BFG9K, atd.new ParticleData(100, 0, 0, ProjectileType.PARTICLE, true, 0.4, 1.2, 3, ParticleTypes.SPELL, ParticleTypeEnum.CRIT_MAGIC, Sound.ENTITY_ENDERDRAGON_FIREBALL_EXPLODE));
		
		//weaponDataMap.put(<Weapon type>, <Weapon Data>(attackDataMap entry, velocity multiplier, cooldown (2 ticks), internal name, display name, material type, weapon type)
		weaponDataMap.put(WeaponType.GAUNTLET, wpd.new WeaponData(attackDataMap.get(WeaponType.GAUNTLET), 1, 10, "Gauntlet", ChatColor.YELLOW + "Gauntlet", Material.INK_SACK, 2, WeaponType.GAUNTLET, 1));
		weaponDataMap.put(WeaponType.MACHINEGUN, wpd.new WeaponData(attackDataMap.get(WeaponType.MACHINEGUN), 1, 2, "Machinegun", ChatColor.YELLOW + "Machinegun", Material.INK_SACK, 3, WeaponType.MACHINEGUN, 50));
		weaponDataMap.put(WeaponType.SHOTGUN, wpd.new WeaponData(attackDataMap.get(WeaponType.SHOTGUN), 1, 20, "Shotgun", ChatColor.YELLOW + "Shotgun", Material.INK_SACK, 4, WeaponType.SHOTGUN, 50));
		weaponDataMap.put(WeaponType.ROCKET_LAUNCHER, wpd.new WeaponData(attackDataMap.get(WeaponType.ROCKET_LAUNCHER), 15, 25, "Rocket Launcher", ChatColor.YELLOW + "Rocket Launcher", Material.INK_SACK, 5, WeaponType.ROCKET_LAUNCHER, 25));
		weaponDataMap.put(WeaponType.GRENADE_LAUNCHER, wpd.new WeaponData(attackDataMap.get(WeaponType.GRENADE_LAUNCHER), 5, 25, "Grenade Launcher", ChatColor.YELLOW + "Grenade Launcher", Material.INK_SACK, 6, WeaponType.GRENADE_LAUNCHER, 25));
		weaponDataMap.put(WeaponType.RAILGUN, wpd.new WeaponData(attackDataMap.get(WeaponType.RAILGUN), 1, 30, "Railgun", ChatColor.YELLOW + "Railgun", Material.INK_SACK, 7, WeaponType.RAILGUN, 40));
		weaponDataMap.put(WeaponType.LIGHTNING_GUN, wpd.new WeaponData(attackDataMap.get(WeaponType.LIGHTNING_GUN), 1, 1, "Lightning Gun", ChatColor.YELLOW + "Lightning Gun", Material.INK_SACK, 8, WeaponType.LIGHTNING_GUN, 10));
		weaponDataMap.put(WeaponType.PLASMAGUN, wpd.new WeaponData(attackDataMap.get(WeaponType.PLASMAGUN), 5, 2, "Plasma Gun", ChatColor.YELLOW + "Plasma Gun", Material.INK_SACK, 9, WeaponType.PLASMAGUN, 30));
		weaponDataMap.put(WeaponType.BFG9K, wpd.new WeaponData(attackDataMap.get(WeaponType.BFG9K), 1, 4, "BFG 9000", ChatColor.YELLOW + "BFG 9000", Material.INK_SACK, 10, WeaponType.BFG9K, 30));
	}
	
	public static HashMap<WeaponType, AttackBase> getAttackData()
	{
		return attackDataMap;
	}
	
	public static HashMap<String, WeaponType> getNameData()
	{
		return nameDataMap;
	}
	
	public static Weapon getWeaponInstance(WeaponType wt)
	{
		return weaponDataMap.get(wt).createInstance();
	}
	
	//public HashMap<String, CWep> getWeaponData()
	//{
	//	return weaponDataMap;
	//}	
	public enum WeaponType
	{
		GAUNTLET, MACHINEGUN, SHOTGUN, GRENADE_LAUNCHER, ROCKET_LAUNCHER, PLASMAGUN, RAILGUN, LIGHTNING_GUN, BFG9K;
	}
	
	public enum ProjectileType
	{
		ARMOR_STAND, ARROW, BLOCK, DRAGON_FIREBALL, 
		EGG, ENDER_PEARL, FIREBALL, 
		FISH_HOOK, INSTANT_HIT, LLAMA_SPIT, LLAMA_SPIT_2,
		MELEE, PARTICLE, SHULKER_BULLET, 
		SMALL_FIREBALL, SNOWBALL, SPECTRAL_ARROW, 
		EXP_BOTTLE, THROWN_POTION, WITHER_SKULL;
	}
	
	public enum ParticleTypes
	{
		SPELL, SIMPLE, BEAM;
	}
}
