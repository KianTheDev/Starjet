package kian.towers.tower.implementation;

import org.bukkit.entity.Player;

import kian.towers.tower.AttackData;
import kian.towers.tower.AttackType;
import kian.towers.tower.Schematic;
import kian.towers.tower.SplashData;
import kian.towers.tower.Tower;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Basic projectile-shooting tower.
 */
public class DragonTower extends Tower
{
	public DragonTower(Schematic schem, Player owner)
	{
		super(schem, new AttackData(1, 1000, 200, 10, 5, AttackType.PARTICLE_SHOOTER, null, ParticleTypeEnum.DRAGON_BREATH), owner);
		this.setUpgradeDescs(new String[]{"The ultimate boss-killer, with slow but powerful attacks.",
				"Gives weapon a small AOE.", "Decreases cooldown by 1 second.", "Ultimate upgrade: Cataclysm, an extremely powerful AOE secondary effect.",
				"Increases damage.", "Increases range by 4 blocks.", "Ultimate upgrade: Increased range and an extra attack."});
		this.setUpgradeNames(new String[]{"Dragonbreath Tower", 
				"Inferno", "Dragon Frenzy", "Cataclysm", 
				"Immolation", "Focused Fire", "Dragon's Fury"});
		this.setMultiplier(20);
	}

	@Override
	public void upgradeP1L1() //Gives weapon a 1 block wide AOE
	{
		int temp = 1000;
		if(this.getUpgradeLevel(2) >= 1) //If damage upgrade researched, increase splash damage
			temp = 1200;
		this.getAttackData().setSplashData(new SplashData(1, temp));
		this.getAttackData().setData(1, 0);
		this.setUpgradeLevel(1, 1);
	}

	@Override
	public void upgradeP1L2() //Increases firing speed
	{
		this.getAttackData().setData(2, 180);
		this.setUpgradeLevel(1, 2);
	}

	@Override
	public void upgradeP1L3() //Ultimate upgrade - Cataclysm: secondary AOE effect that spawns a tertiary AOE effect on all enemies in radius
	{
		AttackData main = new AttackData(1, 30, 1, 3, 5, AttackType.AOE_FIELD, null, ParticleTypeEnum.DRAGON_BREATH);
		main.setSpecial(new AttackData(1, 10, 1, 1, 5, AttackType.AOE_FIELD, null, ParticleTypeEnum.CRIT_MAGIC));
		this.getAttackData().setSpecial(main);
		this.setUpgradeLevel(1, 3);
	}

	@Override
	public void upgradeP2L1() //Increases damage by 200 
	{
		if(this.getUpgradeLevel(1) >= 1) //If AOE upgrade researched, upgrade AOE instead
			this.getAttackData().getSplashData().setDamage(1200);
		else
			this.getAttackData().setData(1, 1200);
		this.setUpgradeLevel(2, 1);
	}

	@Override
	public void upgradeP2L2() //Increases range
	{
		this.getAttackData().setData(3, 14);
		this.setUpgradeLevel(2, 2);
	}

	@Override
	public void upgradeP2L3() //Ultimate upgrade - Dragon's Fury: +5 range and +1 attack
	{
		this.getAttackData().setData(0, 2);
		this.getAttackData().setData(3, 20);
		this.setUpgradeLevel(2, 3);
	}
}
