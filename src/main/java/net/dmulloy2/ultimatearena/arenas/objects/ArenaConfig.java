package net.dmulloy2.ultimatearena.arenas.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.util.InventoryHelper;

public class ArenaConfig
{
	private int gameTime, lobbyTime, maxDeaths, maxWave, cashReward;
	
	public boolean allowTeamKilling;
	
	public List<ArenaReward> rewards = new ArrayList<ArenaReward>();
	
	public boolean loaded = false;
	
	public String arenaName;
	public File file;
	public UltimateArena plugin;
	
	public ArenaConfig(UltimateArena plugin, String str, File file)
	{
		this.arenaName = str;
		this.file = file;
		this.plugin = plugin;
		
		this.loaded = load();
		if (!loaded)
		{
			plugin.getLogger().warning("Could not load config for " + arenaName + "!");
		}
	}
	
	public boolean load()
	{
		try
		{
			YamlConfiguration fc = YamlConfiguration.loadConfiguration(file);
			if (arenaName.equals("mob"))
			{
				if (fc.get("maxWave") == null)
				{
					fc.set("maxWave", 15);
					fc.save(file);
				}
				
				this.maxWave = fc.getInt("maxWave");
			}
			
			this.gameTime = fc.getInt("gameTime");
			this.lobbyTime = fc.getInt("lobbyTime");
			this.maxDeaths = fc.getInt("maxDeaths");
			this.allowTeamKilling = fc.getBoolean("allowTeamKilling");
			this.cashReward = fc.getInt("cashReward");
					
			List<String> words = fc.getStringList("rewards");
			for (String word : words)
			{
				int id = 0;
				byte dat = 0;
				int amt = 0;
						
				String[] split = word.split(",");
				if (split[0].contains(":"))
				{
					String[] split2 = split[0].split(":");
					id = Integer.parseInt(split2[0]);
					dat = Byte.parseByte(split2[1]);
					amt = Integer.parseInt(split[1]);
				}
				else
				{
					id = Integer.parseInt(split[0]);
					amt = Integer.parseInt(split[1]);
				}
						
				ArenaReward reward = new ArenaReward(id, dat, amt);
				rewards.add(reward);
			}
		}
		catch (Exception e)
		{
			plugin.getLogger().severe("Error loading config: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public void giveRewards(Player player, boolean half) 
	{
		for (ArenaReward a : rewards)
		{
			Material mat = Material.getMaterial(a.getType());
			int amt = a.getAmount();
			byte dat = a.getData();
			PlayerInventory inv = player.getInventory();
			MaterialData data = new MaterialData(mat.getId());
			
			if (amt < 1)
				amt = 1;
			
			if (dat > 0)
			{
				data.setData(dat);
			}
			
			if (half)
				amt = (int)(Math.floor(amt / 2.0));
					
			ItemStack itemStack = new ItemStack(mat, amt);
			if (dat > 0) itemStack.setData(data);
					
			int slot = InventoryHelper.getFirstFreeSlot(inv);
			if (slot > -1)
			{
				inv.setItem(slot, itemStack);
			}
		}
		
		// dmulloy2 new method
		if (plugin.getConfig().getBoolean("moneyrewards"))
		{
			if (plugin.getEconomy() != null)
			{
				if (cashReward > 0)
				{
					plugin.getEconomy().depositPlayer(player.getName(), cashReward);
					String format = plugin.getEconomy().format(cashReward);
					player.sendMessage(ChatColor.GREEN + format + " has been added to your balance!");
				}
			}
		}
	}

	public int getGameTime() 
	{
		return gameTime;
	}

	public int getLobbyTime() 
	{
		return lobbyTime;
	}

	public int getMaxDeaths() 
	{
		return maxDeaths;
	}

	public int getMaxWave()
	{
		return maxWave;
	}

	public int getCashReward()
	{
		return cashReward;
	}
}