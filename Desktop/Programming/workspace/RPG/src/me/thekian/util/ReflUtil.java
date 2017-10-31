package me.thekian.util;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;


public class ReflUtil
{
	public static Class<?> getCraftBukkitClass(String name)
	{
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try 
		{
			return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static Class<?> getNMSClass(String name)
	{
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try 
		{
			return Class.forName("net.minecraft.server." + version + "." + name);
		} catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static Object getPrivateField(String fieldName, Class cls, Object object)
    {
        Field field;
        Object obj = null;
        try
        {
            field = cls.getDeclaredField(fieldName);
            field.setAccessible(true);
            obj = field.get(object);
        } catch(Exception e)
        {
        	System.out.println("Reflection error.");
            e.printStackTrace();
        }
        return obj;
    }
}