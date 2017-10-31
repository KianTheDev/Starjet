/**=============================================================================================**/
/* A data loader for information about objects and items relevant to the modding suite.			**/
/* It buffers the definitions file for each and parses the script for data.						**/
/* This allows for using new, custom items added by a modder to the file.						**/
/**=============================================================================================**/

package kian.geneforge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ObjectItemBuffer
{
	
	private static ItemData[] itemBuffer = new ItemData[512];
	private static ObjectData[] objectBuffer = new ObjectData[256];
	private static ItemData currentItem;
	private static ObjectData currentObject;
			
	public static void initialize(String g5path)
	{
		File itemDefs = new File(g5path + "Geneforge 5 Files" + File.separator + "Scripts" + File.separator + "gf5itemschars.txt");
		File objectDefs = new File(g5path + "Geneforge 5 Files" + File.separator + "Scripts" + File.separator + "gf5objsmisc.txt");
		String definitions = "";
		//Item definitions
		try
		{	
			System.out.println("Buffering item definitions...");
			BufferedReader reader = new BufferedReader(new FileReader(itemDefs));
			String s = "";
			while((s = reader.readLine()) != null)
			{
				definitions = definitions.concat(s);
			}
			reader.close();
			definitions.replaceAll("	", "");
			int id = 0, graphictemplate = 0, graphicsheet = 0, tile = 0, inventile = 0, importDat = 0, coloradj = 0;
			String name = "";
			System.out.println("Loading item definitions...");
			boolean itemdefs = false;
			for(int i = 0; i < definitions.length(); i++)
			{
				if(search(definitions, "begindefineitem", i))
				{
					itemdefs = true;
					for(int i2 = 16; i2 < 40; i2++)
						if(definitions.charAt(i + i2) == ';')
						{
							id = Integer.valueOf(definitions.substring(i + 16, i + i2));
							i = i + i2;
							ItemData itemdata = new ItemData();
							currentItem = itemdata;
							itemBuffer[id] = itemdata;
							if(id == 0)
							{
								name = "Default Item";
								currentItem.setName("Default Item");
								graphicsheet = 0;
								currentItem.setGraphicSheet(0);
								graphictemplate = 55;
								currentItem.setGraphicTemplate(55);
								inventile = 0;
								currentItem.setInvenTile(0);
								tile = 0;
								currentItem.setTile(0);
								coloradj = 0;
								currentItem.setColorAdjust(0);
							}
							break;
						}
				} else if(search(definitions, "it_name =", i))
				{
					int substart = 0;
					boolean b = false;
					for(int i2 = 9; i2 < 100; i2++)
					{
						if(definitions.charAt(i + i2) == '"' && !b)
						{
							substart = i + i2 + 1;
							b = true;
						} else if(definitions.charAt(i + i2) == '"' && b)
						{
							name = definitions.substring(substart, i + i2);
							i = i + i2;
							break;
						}
					}
				}  else if(search(definitions, "import =", i) && itemdefs)
				{
					for(int i2 = 8; i2 < 20; i2++)
					{
						if(definitions.charAt(i + i2) == ';')
						{
							importDat = Integer.valueOf(definitions.substring(i + 9, i + i2));
							name = itemBuffer[importDat].getName();
							graphictemplate = itemBuffer[importDat].getGraphicTemplate();
							graphicsheet = itemBuffer[importDat].getGraphicSheet();
							tile = itemBuffer[importDat].getTile();
							inventile = itemBuffer[importDat].getInvenTile();
							coloradj = itemBuffer[importDat].getColorAdjust();
							i = i + i2;
							break;
						}
					}
				} else if(search(definitions, "it_graphic_template =", i))
				{
					for(int i2 = 21; i2 < 40; i2++)
					{
						if(definitions.charAt(i + i2) == ';')
						{
							graphictemplate = Integer.valueOf(definitions.substring(i + 22, i + i2));
							i = i + i2;
							break;
						}
					}
				} else if(search(definitions, "it_graphic_sheet =", i))
				{
					for(int i2 = 18; i2 < 40; i2++)
					{
						if(definitions.charAt(i + i2) == ';')
						{
							graphicsheet = Integer.valueOf(definitions.substring(i + 19, i + i2));
							i = i + i2;
							break;
						}
					}
				} else if(search(definitions, "it_graphic_coloradj =", i))
				{
					for(int i2 = 21; i2 < 40; i2++)
					{
						if(definitions.charAt(i + i2) == ';')
						{
							coloradj = Integer.valueOf(definitions.substring(i + 22, i + i2));
							i = i + i2;
							break;
						}
					}
				}  else if(search(definitions, "it_which_icon_ground =", i))
				{
					for(int i2 = 22; i2 < 40; i2++)
					{
						if(definitions.charAt(i + i2) == ';')
						{
							tile = Integer.valueOf(definitions.substring(i + 23, i + i2));
							i = i + i2;
							break;
						}
					}
				} else if(search(definitions, "it_which_icon_inven =", i))
				{
					for(int i2 = 21; i2 < 40; i2++)
					{
						if(definitions.charAt(i + i2) == ';')
						{ 
							inventile = Integer.valueOf(definitions.substring(i + 22, i + i2));
							i = i + i2;
							break;
						}
					}
				}

				if(currentItem != null)
				{
					currentItem.setName(name);
					currentItem.setGraphicTemplate(graphictemplate);
					currentItem.setGraphicSheet(graphicsheet);
					currentItem.setTile(tile);
					currentItem.setInvenTile(inventile);
					currentItem.setColorAdjust(coloradj);
				}
			}
			for(ItemData itemdata : itemBuffer)
				if(itemdata != null)
					try
					{
						itemdata.reloadImage(g5path);
					} catch(FileNotFoundException e)
					{
						e.printStackTrace();
					}
			int defnum = 0;
			for(ItemData itemdata : itemBuffer)
				if(itemdata != null)
					defnum++;
			System.out.println("Number of item entries: " + defnum);
			definitions = "";
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		definitions = "";
		//Object definitions
		try
		{	
			System.out.println("Buffering object definitions...");
			BufferedReader reader = new BufferedReader(new FileReader(objectDefs));
			String s = "";
			while((s = reader.readLine()) != null)
			{
				definitions = definitions.concat(s);
			}
			reader.close();
			definitions.replaceAll("	", "");
			int id = 0, graphictemplate = 0, graphicsheet = 0, tile = 0, importDat = 0;
			String name = "";
			System.out.println("Loading object definitions...");
			boolean objectdefs = false;
			for(int i = 0; i < definitions.length(); i++)
			{
				if(search(definitions, "begindefineobject", i))
				{
					objectdefs = true;
					for(int i2 = 18; i2 < 40; i2++)
						if(definitions.charAt(i + i2) == ';')
						{
							id = Integer.valueOf(definitions.substring(i + 18, i + i2));
							i = i + i2;
							ObjectData objectdata = new ObjectData();
							currentObject = objectdata;
							objectBuffer[id] = objectdata;
							if(id == 0)
							{
								name = "Default Object";
								currentObject.setName(name);
								graphicsheet = 0;
								currentObject.setGraphicSheet(0);
								graphictemplate = 33;
								currentObject.setGraphicTemplate(33);
								tile = 0;
								currentObject.setTile(0);
							}
							break;
						}
				} else if(search(definitions, "ob_name =", i))
				{
					int substart = 0;
					boolean b = false;
					for(int i2 = 9; i2 < 100; i2++)
					{
						if(definitions.charAt(i + i2) == '"' && !b)
						{
							substart = i + i2 + 1;
							b = true;
						} else if(definitions.charAt(i + i2) == '"' && b)
						{
							name = definitions.substring(substart, i + i2);
							i = i + i2;
							break;
						}
					}
				}  else if(search(definitions, "import =", i) && objectdefs)
				{
					for(int i2 = 8; i2 < 20; i2++)
					{
						if(definitions.charAt(i + i2) == ';')
						{
							importDat = Integer.valueOf(definitions.substring(i + 9, i + i2));
							name = objectBuffer[importDat].getName();
							graphictemplate = objectBuffer[importDat].getGraphicTemplate();
							graphicsheet = objectBuffer[importDat].getGraphicSheet();
							tile = objectBuffer[importDat].getTile();
							i = i + i2;
							break;
						}
					}
				} else if(search(definitions, "ob_graphic_template =", i))
				{
					for(int i2 = 21; i2 < 40; i2++)
					{
						if(definitions.charAt(i + i2) == ';')
						{
							graphictemplate = Integer.valueOf(definitions.substring(i + 22, i + i2));
							i = i + i2;
							break;
						}
					}
				} else if(search(definitions, "ob_graphic_sheet =", i))
				{
					for(int i2 = 18; i2 < 40; i2++)
					{
						if(definitions.charAt(i + i2) == ';')
						{
							graphicsheet = Integer.valueOf(definitions.substring(i + 19, i + i2));
							i = i + i2;
							break;
						}
					}
				}  else if(search(definitions, "ob_base_icon_num =", i))
				{
					for(int i2 = 18; i2 < 40; i2++)
					{
						if(definitions.charAt(i + i2) == ';')
						{
							tile = Integer.valueOf(definitions.substring(i + 19, i + i2));
							i = i + i2;
							break;
						}
					}
				} else if(search(definitions, "begindefinesfx", i))
				{
					objectdefs = false;
					break;
				} 
				if(currentObject != null)
				{
					currentObject.setName(name);
					currentObject.setGraphicTemplate(graphictemplate);
					currentObject.setGraphicSheet(graphicsheet);
					currentObject.setTile(tile);
					if(name.contains("Door 1") || name.contains("Door 2"))
						currentObject.setUsesOffsets(true);
				}
			}
			for(ObjectData objectdata : objectBuffer)
				if(objectdata != null)
					try
					{
						objectdata.reloadImage(g5path);
					} catch(FileNotFoundException e)
					{
						e.printStackTrace();
					}
			int defnum = 0;
			for(ObjectData objectdata : objectBuffer)
				if(objectdata != null)
					defnum++;
			System.out.println("Number of object entries: " + defnum);
			definitions = "";
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private static boolean search(String string, String tosearch, int index)
	{
		if(index > 1)
			if(string.charAt(index - 1) == '/' && string.charAt(index - 2) == '/')
			{
				return false;
			}
		if(string.length() - index < tosearch.length())
		{
			return false;
		}
		for(int i = 0; i < tosearch.length(); i++)
		{
			if(string.charAt(index + i) != tosearch.charAt(i))
				return false;
		}
		return true;
	}
	
	public static ItemData[] getItemBuffer()
	{
		return itemBuffer;
	}
	
	public static ObjectData[] getObjectBuffer()
	{
		return objectBuffer;
	}
}
