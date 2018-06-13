package kian.towers.tower.implementation;

import org.bukkit.entity.Player;

import kian.towers.tower.AttackData;
import kian.towers.tower.AttackType;
import kian.towers.tower.Schematic;
import kian.towers.tower.SplashData;
import kian.towers.tower.Tower;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

/***
 * Zappy tower. Rapidly fires several beams, which do low damage but can harm all targets.
 */
public class TeslaTower extends Tower
{
	public TeslaTower(Schematic schem, Player owner)
	{
		super(schem, new AttackData(6, 1, 5, 7, 4, AttackType.PARTICLE_BEAM, null, ParticleTypeEnum.WATER_BUBBLE), owner);
		this.setUpgradeDescs(new String[]{"Brzzzt. Shoots rapid-fire, low damage lightning bolts.",
				"Increases range by 2.", "Adds another lightning bolt each attack.", "Ultimate upgrade: Doubles damage and bypasses boss damage resistance.",
				"Increases attack speed by 20%.", "Increases attack speed by an additional 25%.", "Ultimate upgrade: Has a small chance per attack to do a powerful AOE hit."});
		this.setUpgradeNames(new String[]{"Tesla Tower", 
				"Enhanced Coils", "High Voltage", "Storm Tower", 
				"Copper Housing", "Superconductors", "Ball Lightning"});
		this.setMultiplier(8);
	}

	@Override
	public void upgradeP1L1() //Increases range
	{
		this.getAttackData().setData(3, 9);	
		this.setUpgradeLevel(1, 1);	
	}

	@Override
	public void upgradeP1L2() //Adds another beam
	{
		this.getAttackData().setData(0, 7);
		this.setUpgradeLevel(1, 2);
	}

	@Override
	public void upgradeP1L3() //Ultimate upgrade - Increases damage and allows tower to bypass boss DR
	{
		this.getAttackData().setData(1, 2);
		this.getAttackData().setData(4, 5);
		this.setUpgradeLevel(1, 3);
	}

	@Override
	public void upgradeP2L1() //Slightly increases attack speed
	{
		this.getAttackData().setData(2, 4);
		this.setUpgradeLevel(2, 1);
	}

	@Override
	public void upgradeP2L2() //Increases attack speed again
	{
		this.getAttackData().setData(2, 3);
		this.setUpgradeLevel(2, 2);
	}

	@Override
	public void upgradeP2L3() //Ultimate upgrade - 1% chance per attack to do a powerful splash
	{
		this.getAttackData().setSpecial(new AttackData(1, 0, 1, 1, 5, AttackType.AOE_FIELD, null, thekian.nms.protocol.Particles.ParticleTypeEnum.CLOUD));
		this.getAttackData().getSpecial().setSplashData(new SplashData(3, 60));
		this.getAttackData().setData(5, 100);
		this.setUpgradeLevel(2, 3);
	}
}
