package kian.starjet.ship;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import kian.starjet.core.CoordSet;
import kian.starjet.core.Processor;
import kian.starjet.core.Team;
import thekian.nms.protocol.Particles.ParticleTypeEnum;

public class Turret extends Ship
{
	private Ship target;
	private int pointValue;
	private Vector lookAngle;
	private boolean targetType; //Dictates whether the turret attacks players or only capital ships.
	
	public Turret(double health, List<CoordSet> firingLocs, AttackData weapon, Material mat, short data, Team team, int pointValue, boolean b)
	{
		super(health, 1, firingLocs, null, weapon, null, 0, 0, mat, data, team);
		this.pointValue = pointValue;
		targetType = b;
		this.setDimensions(new double[]{1.5, -1.5, 1.5, -1.5, 1.5, -1.5});
	}
	
	public Turret clone()
	{
		return new Turret(this.getHealth(), this.getPrimaryFiringLocs(), this.getPrimaryWeapon(), this.getMat(), this.getData(), this.getTeam(), this.pointValue, this.targetType);
	}
	
	public int getPointValue()
	{
		return pointValue;
	}
	
	@Override
	public Ship killShip()
	{
		Ship s = super.killShip();
		if(s != null)
			s.getTeam().addPoints(s instanceof Turret ? pointValue / 2 : pointValue);
		return s;
	}
	
	/***
	 * Selects a target within range of the turret to attack.
	 * @param targets - List of ships which can potentially be targeted.
	 * @return Ship within range if any exist, otherwise null.
	 */
	public Ship acquireTarget(List<Ship> targets)
	{
		if(target != null && target.getShipModel() != null && target.isAlive() && this.getShipModel() != null && this.getPrimaryWeapon() != null && target.getShipModel().getEyeLocation().distance(this.getShipModel().getEyeLocation()) <= this.getPrimaryWeapon().getRange())
			return target;
		for(Ship s : targets)
			if(!s.getTeam().equals(this.getTeam()))
			{
				//Ship center is most closely the eye location of the armor stand
				if(s.getLocation().distance(this.getLocation()) <= this.getPrimaryWeapon().getRange())
				{
					target = s; //Turret continues to target single ship until it's out of range
					return s; //Return first ship it notices in range
				}
			}
		return null;
	}
	
	public void attackEnemy(List<Ship> targets)
	{
		Ship s = acquireTarget(targets);
		if(s == null)
			return;
		Location loc = s.getShipModel().getEyeLocation();
		Location loc2 = this.getShipModel().getEyeLocation();
		lookAngle = new Vector(loc.getX() - loc2.getX(), loc.getY() - loc2.getY(), loc.getZ() - loc2.getZ()).normalize();
		//A bunch of trig to pose the model
		this.getShipModel().setHeadPose(new EulerAngle(Math.toDegrees(Math.atan(lookAngle.getZ() / lookAngle.getX())),
				Math.toDegrees(Math.atan(lookAngle.getY() / Math.sqrt(Math.pow(lookAngle.getX(), 2) + Math.pow(lookAngle.getZ(), 2)))),
				0));//Math.toDegrees(Math.atan(lookAngle.getX() / lookAngle.getZ()))));
		this.attackPrimary();
	}
	
	//Version of attackEnemy() which targets capital ships
	public void attackCapitalShip(CapitalShip cs)
	{
		if(this.getCoolOne() > 0)
			return;
		Location loc = null;
		//Random low chance for the turret to select another anti-capital ship turret. Otherwise targets capital ship.
		if(Math.random() > 0.3)
			loc = cs.getLocation();
		else
			for(Turret t : cs.getTurrets())
				if(t.getTargeting())
				{
					loc = t.getLocation();
					break;
				}
		if(loc == null)
			loc = cs.getLocation();
		Location loc2 = this.getShipModel().getEyeLocation();
		lookAngle = new Vector(loc.getX() - loc2.getX(), loc.getY() - loc2.getY(), loc.getZ() - loc2.getZ()).normalize();
		this.getShipModel().setHeadPose(new EulerAngle(Math.toDegrees(Math.atan(lookAngle.getZ() / lookAngle.getX())),
				Math.toDegrees(Math.atan(lookAngle.getY() / Math.sqrt(Math.pow(lookAngle.getX(), 2) + Math.pow(lookAngle.getZ(), 2)))),
				0));
		this.setCoolOne(this.getPrimaryWeapon().getCooldown());
		Processor.addAttack(loc2, this.getPrimaryWeapon(), this);
	}
	
	public Ship getTarget()
	{
		return target;
	}
	
	public Vector getLookAngle()
	{
		return lookAngle;
	}
	
	public boolean getTargeting()
	{
		return targetType;
	}
	
	public enum TurretPresets
	{
		//Basic anti-fighter plasma turret
		PLASMA(200, new CoordSet(1, 1, 0),
				new AttackData(AttackType.PROJECTILE, 10, 15, 0.1, 10, ParticleTypeEnum.CRIT_MAGIC, null, null, (byte) 0, 5),
			Material.WOOD_AXE, (short) 1, 5, false),
		//Heavier but slower turret, shoots faster projectiles
		LANCE(350, new CoordSet(3, 1, 0),
				new AttackData(AttackType.PROJECTILE, 50, 25, 0.05, 15, ParticleTypeEnum.VILLAGER_HAPPY, null, null, (byte) 0, 20),
			Material.WOOD_AXE, (short) 2, 5, false),
		//Massive anti capital ship cannon, only targets capital ships.
		NOVA(5000, new CoordSet(3, 1, 0),
				new AttackData(AttackType.BEAM, 2000, 400, 2, 5, ParticleTypeEnum.END_ROD, null, null, (byte) 0, 300),
			Material.WOOD_AXE, (short) 3, 40, true);
		
		private Turret turret;
		
		private TurretPresets(double health, CoordSet firingLoc, AttackData weapon, Material mat, short data, int pointValue, boolean b)
		{
			turret = new Turret(health, Arrays.asList(firingLoc), weapon, mat, data, null, pointValue, b);
		}
		
		public Turret getTurret()
		{
			return turret;
		}
	}
}
