package net.dmulloy2.ultimatearena.events;

import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class UltimateArenaKillEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();
	
	private final ArenaPlayer killer;
	private final ArenaPlayer killed;
	private final Arena arena;
	
	/**
	 * Called when a player kills another player
	 * @param killer - The ArenaPlayer that killed
	 * @param killed - The ArenaPlayer that was killed
	 * @param arena - The Arena the player joined
	 */
	public UltimateArenaKillEvent(final ArenaPlayer killed, final ArenaPlayer killer, final Arena arena)
	{
		this.killed = killed;
		this.killer = killer;
		this.arena = arena;
	}
	
	public ArenaPlayer getKiller()
	{
		return killer;
	}
	
	public ArenaPlayer getKilled()
	{
		return killed;
	}
	
	public Arena getArena()
	{
		return arena;
	}
	
	public String getArenaType()
	{
		return arena.getType();
	}
	
	public Player getKillerPlayer()
	{
		return killer.getPlayer();
	}
	
	public Player getKilledPlayer()
	{
		return killed.getPlayer();
	}
	
	public ItemStack getWeapon()
	{
		return killer.getPlayer().getItemInHand();
	}
	
	public int getHeartsLeft()
	{
		return (int) (killer.getPlayer().getHealth() / 2);
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}