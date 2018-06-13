package kian.towers.tower.implementation;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Career;

import kian.towers.tower.AttackData;
import kian.towers.tower.AttackType;
import kian.towers.tower.Schematic;
import kian.towers.tower.SplashData;
import kian.towers.tower.Tower;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * AOE field tower. Damages and slows enemies. Can damage armored enemies but not flying. Overrides build and destroy methods to add a decorative villager on top.
 */
public class PriestTower extends Tower
{
	private Villager priest;
	
	public PriestTower(Schematic schem, Player owner)
	{
		super(schem, new AttackData(1, 10, 60, 6, 2, AttackType.AOE_FIELD, null, ParticleTypeEnum.DAMAGE_INDICATOR), owner);
		this.setUpgradeDescs(new String[]{"Hrrr. Unleashes an area of effect attack.",
				"Increases range.", "Further increases range.", "Additional AOE attack on each hit.",
				"Increases attack speed.", "Increases attack damage.", "Attacks faster and releases energy waves."});
		this.setUpgradeNames(new String[]{"Priest Tower", 
				"Energy Focus", "Warp Field", "Energy Burst", 
				"Magic Focus", "Intense Power", "Divine Nova"});
		this.setMultiplier(4);
	}
	
	@Override
	public void upgradeP1L1() //Increases range
	{
		this.getAttackData().setData(3, 8);
		this.setUpgradeLevel(1, 1);
	}
	
	@Override
	public void upgradeP1L2() //Further increases range
	{
		this.getAttackData().setData(3, 10);
		this.setUpgradeLevel(1, 2);
	}
	
	@Override
	public void upgradeP1L3() //Ultimate upgrade - adds a secondary splash effect to every hit
	{
		this.getAttackData().setSplashData(new SplashData(2, 5));
		this.setUpgradeLevel(1, 3);
	}
	
	@Override
	public void upgradeP2L1() //Decreases cooldown
	{
		this.getAttackData().setData(2, 50);
		this.setUpgradeLevel(2, 1);
	}
	
	@Override
	public void upgradeP2L2() //Increases damage
	{
		this.getAttackData().setData(1, 15);
		this.setUpgradeLevel(2, 2);
	}
	
	@Override
	public void upgradeP2L3() //Ultimate upgrade - decreases cooldown and attacks twice
	{
		this.getAttackData().setData(2, 40);
		this.getAttackData().setData(0, 2);
		this.setUpgradeLevel(2, 3);
	}
	
	@Override
	public void buildTower(Location loc)
	{
		super.buildTower(loc);
		//Make an immobile, invulnerable priest when the tower spawns
		priest = (Villager) loc.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getBlockX() + 0.5, loc.getBlockY() + 6.5, loc.getBlockZ() + 0.5), EntityType.VILLAGER);
		priest.setRemoveWhenFarAway(false);
		priest.setAI(false);
		priest.setInvulnerable(true);
		priest.setCareer(Career.CLERIC);
	}
	
	@Override
	public void destroyTower()
	{
		super.destroyTower();
		priest.remove(); //Get rid of decorative priest
	}
	
}
