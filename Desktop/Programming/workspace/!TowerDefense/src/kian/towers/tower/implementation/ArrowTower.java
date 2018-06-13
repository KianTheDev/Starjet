package kian.towers.tower.implementation;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import kian.towers.tower.AttackData;
import kian.towers.tower.AttackType;
import kian.towers.tower.Schematic;
import kian.towers.tower.Tower;

/***
 * Basic projectile-shooting tower.
 */
public class ArrowTower extends Tower
{
	public ArrowTower(Schematic schem, Player owner)
	{
		super(schem, new AttackData(1, 4, 20, 10, 3, AttackType.PROJECTILE_SHOOTER, EntityType.ARROW, null), owner);
		this.setUpgradeDescs(new String[]{"A basic tower. Shoots weak arrows, but is highly upgradable.",
				"Allows arrows to penetrate armor.", "Increases damage and range.", "Damage and range increase; attacks bypass boss damage resistance.",
				"Increases attack speed.", "Fires an additional arrow each attack.", "Fires an additional arrow and doubles attack speed."});
		this.setUpgradeNames(new String[]{"Arrow Tower", 
				"Bodkin Arrows", "Fletching", "Snipers", 
				"Rapid Reload", "Multishot", "Arrow Assault"});
		this.setMultiplier(2);
	}

	@Override
	public void upgradeP1L1() //Allows tower to attack armored enemies
	{
		this.getAttackData().setData(4, 4);
		this.setUpgradeLevel(1, 1);
	}

	@Override
	public void upgradeP1L2() //Increases damage and range
	{
		this.getAttackData().setData(1, 8);
		this.getAttackData().setData(3, 15);
		this.setUpgradeLevel(1, 2);
	}

	@Override
	public void upgradeP1L3() //Ultimate upgrade - sniper
	{
		this.getAttackData().setData(1, 20);
		this.getAttackData().setData(4, 5);
		this.getAttackData().setData(3, 20);
		this.setUpgradeLevel(1, 3);
	}

	@Override
	public void upgradeP2L1() //Upgrades attack speed 
	{
		this.getAttackData().setData(2, 10);
		this.setUpgradeLevel(2, 1);
	}

	@Override
	public void upgradeP2L2() //Adds another attack
	{
		this.getAttackData().setData(0, 2);
		this.setUpgradeLevel(2, 2);
	}

	@Override
	public void upgradeP2L3() //Ultimate upgrade - arrow spray
	{
		this.getAttackData().setData(0, 3);
		this.getAttackData().setData(2, 5);
		this.setUpgradeLevel(2, 3);
	}
}
