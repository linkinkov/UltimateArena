package net.dmulloy2.ultimatearena.listeners;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaSign;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaZone;
import net.dmulloy2.ultimatearena.permissions.PermissionType;
import net.dmulloy2.ultimatearena.util.FormatUtil;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author dmulloy2
 */

public class BlockListener implements Listener 
{
	private final UltimateArena plugin;
	public BlockListener(final UltimateArena plugin) 
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) 
	{
		Block block = event.getBlock();
		if (block == null)
			return;

		Player player = event.getPlayer();
		if (player == null)
			return;
		
		if (event.isCancelled())
			return;
		
		if (plugin.isInArena(block))
		{
			/** The player is in an arena **/
			if (plugin.isInArena(player) && plugin.getArena(player) != null)
			{
				Arena arena = plugin.getArena(player);
				if (!arena.getType().equalsIgnoreCase("Hunger"))
				{
					player.sendMessage(ChatColor.RED + "You cannot break this!");
					event.setCancelled(true);
				}
				// TODO: Hunger games block logging?
			}
			else
			{
				/** The player is at the site of the arena, but not in it **/
				if (!plugin.getPermissionHandler().hasPermission(player, PermissionType.BUILD.permission))
				{
					player.sendMessage(ChatColor.RED + "You cannot break this!");
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) 
	{
		Block block = event.getBlock();
		if (block == null)
			return;

		Player player = event.getPlayer();
		if (player == null)
			return;
		
		if (event.isCancelled())
			return;
		
		if (plugin.isInArena(block))
		{
			/** The player is in an arena **/
			if (plugin.isInArena(player) && plugin.getArena(player) != null)
			{
				Arena arena = plugin.getArena(player);
				if (!arena.getType().equalsIgnoreCase("Hunger"))
				{
					player.sendMessage(ChatColor.RED + "You cannot place this!");
					event.setCancelled(true);
				}
				// TODO: Hunger games block logging?
			}
			else
			{
				/** The player is at the site of the arena, but not in it **/
				if (!plugin.getPermissionHandler().hasPermission(player, PermissionType.BUILD.permission))
				{
					player.sendMessage(ChatColor.RED + "You cannot place this!");
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignChange(SignChangeEvent event)
	{
		if (event.getLine(0).equalsIgnoreCase("[UltimateArena]"))
		{
			if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), PermissionType.BUILD.permission))
			{
				if (event.getLine(1).equalsIgnoreCase("Click to join"))
				{
					if (event.getLine(2).equalsIgnoreCase("Auto assign"))
					{
						return;
					}
					
					ArenaZone az = plugin.getArenaZone(event.getLine(2));
					if (az != null)
					{
						ArenaSign sign = new ArenaSign(plugin, event.getBlock().getLocation(), az, true, plugin.arenaSigns.size());
						plugin.arenaSigns.add(sign);
						sign.update();
						
						event.getPlayer().sendMessage(FormatUtil.format("&eCreated new Join Sign!"));
					}
					else
					{
						event.setLine(0, FormatUtil.format("[UltimateArena]"));
						event.setLine(1, FormatUtil.format("&4Invalid Arena"));
						event.setLine(2, "");
						event.setLine(3, "");
					}
				}
			}
			else
			{
				event.setLine(0, FormatUtil.format("[UltimateArena]"));
				event.setLine(1, FormatUtil.format("&4No permission"));
				event.setLine(2, "");
				event.setLine(3, "");
			}
		}
		else if (event.getLine(0).equalsIgnoreCase("[Arena Stats]"))
		{
			if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), PermissionType.BUILD.permission))
			{
				ArenaZone az = plugin.getArenaZone(event.getLine(1));
				if (az != null)
				{
					ArenaSign sign = new ArenaSign(plugin, event.getBlock().getLocation(), az, false, plugin.arenaSigns.size());
					plugin.arenaSigns.add(sign);
					sign.update();
					
					event.getPlayer().sendMessage(FormatUtil.format("&eCreated new Stats sign!"));
				}
				else
				{
					event.setLine(0, FormatUtil.format("[UltimateArena]"));
					event.setLine(1, FormatUtil.format("&4Invalid Arena"));
					event.setLine(2, "");
					event.setLine(3, "");
				}
			}
			else
			{
				event.setLine(0, FormatUtil.format("[UltimateArena]"));
				event.setLine(1, FormatUtil.format("&4No permission"));
				event.setLine(2, "");
				event.setLine(3, "");
			}
			
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignBreak(BlockBreakEvent event)
	{
		Block block = event.getBlock();
		Player player = event.getPlayer();
		if (block.getState() instanceof Sign)
		{
			Sign s = (Sign)block.getState();
			if (s.getLine(0).equalsIgnoreCase("[UltimateArena]"))
			{
				ArenaSign sign = plugin.getArenaSign(block.getLocation());
				if (sign != null)
				{
					if (plugin.getPermissionHandler().hasPermission(player, PermissionType.BUILD.permission))
					{
						plugin.deleteSign(sign);
						player.sendMessage(FormatUtil.format("&eDeleted Join sign!"));
					}
					else
					{
						event.setCancelled(true);
						player.sendMessage(FormatUtil.format("&cPermission denied!"));
					}
				}
			}
			
			if (s.getLine(0).equalsIgnoreCase("[Arena Stats]"))
			{
				ArenaSign sign = plugin.getArenaSign(block.getLocation());
				if (sign != null)
				{
					if (plugin.getPermissionHandler().hasPermission(player, PermissionType.BUILD.permission))
					{
						plugin.deleteSign(sign);
						player.sendMessage(FormatUtil.format("&eDeleted Stats sign!"));
					}
					else
					{
						event.setCancelled(true);
						player.sendMessage(FormatUtil.format("&cPermission denied!"));
					}
				}
			}
		}
	}
}