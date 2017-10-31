package thekian.nms.main;

import java.lang.reflect.Method;

public class NBT 
{
	public class NBTDataSet
	{
		private String attributeName, name, slot;
		private int amount, operation, UUIDLeast, UUIDMost;
		public NBTDataSet(String attributeName, String name, int amount, int operation, int UUIDLeast, int UUIDMost, String slot)
		{
			this.attributeName = attributeName;
			this.name = name;
			this.amount = amount;
			this.operation = operation;
			if(UUIDLeast == -1)
				this.UUIDLeast = ((int) (440000 + Math.random() * 440000));
			else
				this.UUIDLeast = UUIDLeast;
			if(UUIDMost == -1)	
				this.UUIDMost = ((int) (1500 + Math.random() * 1500));
			else
				this.UUIDMost = UUIDMost;
			this.slot = slot;
		}
		
		public String getAttributeName()
		{
			return this.attributeName;
		}
		
		public String getName()
		{
			return this.name;
		}
		
		public String getSlot()
		{
			return this.slot;
		}
		
		public int getAmount()
		{
			return this.amount;
		}
		
		public int getUUIDMost()
		{
			return this.UUIDMost;
		}
		
		public int getUUIDLeast()
		{
			return this.UUIDLeast;
		}
		
		public int getOperation()
		{
			return this.operation;
		}
	}
	
	static public Object createNBTTagString(String s)
	{
		try
		{
			return ReflectionUtils.getNMSClass("NBTTagString").getConstructor(String.class).newInstance(s);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	static public Object createNBTTagInt(int i)
	{
		try
		{
			return ReflectionUtils.getNMSClass("NBTTagInt").getConstructor(int.class).newInstance(i);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	static public Object createNBTTagCompound()
	{
		try
		{
			return ReflectionUtils.getNMSClass("NBTTagCompound").getConstructor().newInstance();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	static public Object createNBTTagList()
	{
		try
		{
			return ReflectionUtils.getNMSClass("NBTTagList").getConstructor().newInstance();
		} catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	//Basic non-reflected NMS code for reference
	//See comments on createNBTAttributes method for details
	
	/*net.minecraft.server.v1_11_R1.ItemStack itemStack = org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asNMSCopy(is);
net.minecraft.server.v1_11_R1.NBTTagCompound compound = (itemStack.hasTag()) ? itemStack.getTag() : new net.minecraft.server.v1_11_R1.NBTTagCompound();
net.minecraft.server.v1_11_R1.NBTTagList modifiers = new net.minecraft.server.v1_11_R1.NBTTagList();
net.minecraft.server.v1_11_R1.NBTTagCompound damage = new net.minecraft.server.v1_11_R1.NBTTagCompound();
damage.set("AttributeName", new net.minecraft.server.v1_11_R1.NBTTagString("generic.attackDamage"));
damage.set("Name", new net.minecraft.server.v1_11_R1.NBTTagString("generic.attackDamage"));
damage.set("Amount", new net.minecraft.server.v1_11_R1.NBTTagInt(attackSpeed)); //Attribute amount
damage.set("Operation", new net.minecraft.server.v1_11_R1.NBTTagInt(0));
damage.set("UUIDLeast", new net.minecraft.server.v1_11_R1.NBTTagInt(894654));
damage.set("UUIDMost", new net.minecraft.server.v1_11_R1.NBTTagInt(2872));
damage.set("Slot", new net.minecraft.server.v1_11_R1.NBTTagString("mainhand"));
modifiers.add(damage);
compound.set("AttributeModifiers", modifiers);
itemStack.setTag(compound);
return org.bukkit.craftbukkit.v1_11_R1.inventory.CraftItemStack.asBukkitCopy(itemStack);*/
	
	static public Object createNBTAttributes(Object compound, String attribute, int amount, String slot)
	{
		try
		{
			Object modifiers = createNBTTagList(); //Object to hold list of modifiers. Modifiers added with NBTTagCompound object.
			Object itemCompound = createNBTTagCompound(); //Object to create item attribute set.
			Method compoundSet = ReflectionUtils.getNMSClass("NBTTagCompound").getMethod("set", String.class, ReflectionUtils.getNMSClass("NBTBase")); //Method object for the NBTTagCompound.set(String, NBTBase) method. Sets 
			compoundSet.invoke(itemCompound, "AttributeName", createNBTTagString(attribute)); //Attribute type
			compoundSet.invoke(itemCompound, "Name", createNBTTagString(attribute)); //Attribute type
			compoundSet.invoke(itemCompound, "Amount", createNBTTagInt(amount)); //Attribute amount
			compoundSet.invoke(itemCompound, "Operation", createNBTTagInt(0)); //Attribute modification type. 0 = additive, 1 = multiplicative (modifiers add), 2 = multiplicative (modifiers multiply by each other)
			compoundSet.invoke(itemCompound, "UUIDLeast", createNBTTagInt((int) (440000 + Math.random() * 440000))); //Not -entirely- understood. Used to create a unique hex ID. Can cause errors if different attributes
			compoundSet.invoke(itemCompound, "UUIDMost", createNBTTagInt((int) (1500 + Math.random() * 1500))); //have the same UUID. Chance of that is negligible with random fields (~1 in 660 million).
			if(slot != null)
				compoundSet.invoke(itemCompound, "Slot", createNBTTagString(slot)); //Item slot in which the NBT modifier applies. Valid values are: mainhand, offhand, head, chest, legs, feet
			ReflectionUtils.getNMSClass("NBTTagList").getMethod("add", ReflectionUtils.getNMSClass("NBTBase")).invoke(modifiers, itemCompound); //Adds the new attribute data to the NBT Tag List.
			compoundSet.invoke(compound, "AttributeModifiers", modifiers); //Set the compound parameter's NBTTagCompound to contain the new data. 
			return compound;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	static public Object createNBTAttributes(Object compound, NBTDataSet nbtData)
	{
		//Alternate method which makes use of an NBT data object
		try
		{
			Object modifiers = createNBTTagList(); //Object to hold list of modifiers. Modifiers added with NBTTagCompound object.
			Object itemCompound = createNBTTagCompound(); //Object to create item attribute set.
			Method compoundSet = ReflectionUtils.getNMSClass("NBTTagCompound").getMethod("set", String.class, ReflectionUtils.getNMSClass("NBTBase")); //Method object for the NBTTagCompound.set(String, NBTBase) method. Sets 
			compoundSet.invoke(itemCompound, "AttributeName", createNBTTagString(nbtData.getAttributeName())); //Attribute type
			compoundSet.invoke(itemCompound, "Name", createNBTTagString(nbtData.getName())); //Attribute type
			compoundSet.invoke(itemCompound, "Amount", createNBTTagInt(nbtData.getAmount())); //Attribute amount
			compoundSet.invoke(itemCompound, "Operation", createNBTTagInt(nbtData.getOperation())); //Attribute modification type. 0 = additive, 1 = multiplicative (modifiers add), 2 = multiplicative (modifiers multiply by each other)
			compoundSet.invoke(itemCompound, "UUIDLeast", createNBTTagInt(nbtData.getUUIDLeast())); //Not -entirely- understood. Used to create a unique hex ID. Can cause errors if different attributes
			compoundSet.invoke(itemCompound, "UUIDMost", createNBTTagInt(nbtData.getUUIDMost())); //have the same UUID. Chance of that is negligible with random fields (~1 in 660 million).
			if(nbtData.getSlot() != null)
				compoundSet.invoke(itemCompound, "Slot", createNBTTagString(nbtData.getSlot())); //Item slot in which the NBT modifier applies. Valid values are: mainhand, offhand, head, chest, legs, feet
			ReflectionUtils.getNMSClass("NBTTagList").getMethod("add", ReflectionUtils.getNMSClass("NBTBase")).invoke(modifiers, itemCompound); //Adds the new attribute data to the NBT Tag List.
			compoundSet.invoke(compound, "AttributeModifiers", modifiers); //Set the compound parameter's NBTTagCompound to contain the new data. 
			return compound;
		} catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
