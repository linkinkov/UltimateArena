package net.dmulloy2.ultimatearena.listeners;

import net.dmulloy2.ultimatearena.UltimateArena;
import net.dmulloy2.ultimatearena.arenas.Arena;
import net.dmulloy2.ultimatearena.arenas.objects.ArenaPlayer;
import net.dmulloy2.ultimatearena.events.UltimateArenaKillEvent;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

public class EntityListener implements Listener
{
	private final UltimateArena plugin;
	public EntityListener(final UltimateArena plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) 
	{
		if (plugin.isInArena(event.getLocation()))
		{
			if (!event.blockList().isEmpty())
			{
				event.setCancelled(true);
			}
		}
	}

	// TODO: Rewrite this
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) 
	{
		Entity att = event.getDamager();
		if (att instanceof Player)
		{
			Player attacker = (Player)att;
			if (plugin.isInArena(attacker)) 
			{
				ItemStack itm = attacker.getItemInHand();
				itm.setDurability((short) 0);
			}
		}
		
		if (!(event.getEntity() instanceof Player)) 
				return;
			
		Player defender = (Player)event.getEntity();
		if (!(att instanceof Player)) 
		{
			if (att instanceof LivingEntity)
			{
				LivingEntity attacker = (LivingEntity)att;
				if (plugin.isInArena(attacker.getLocation()))
				{
					if (plugin.isInArena(defender.getLocation()))
					{
						event.setCancelled(false);
					}
				}
			}
				
			if (att instanceof Arrow)
			{
				Arrow attacker = (Arrow)att;
				if (attacker.getShooter() instanceof Player)
				{
					Player shooter = (Player) attacker.getShooter();
					if ((plugin.isInArena(defender)) && (plugin.isInArena(shooter)))
					{
						event.setCancelled(false);
						Arena a = plugin.getArena(defender);
						if (shooter.getItemInHand().getType().equals(Material.BOW))
						{
							shooter.getItemInHand().setDurability((short) 0);
						}
						if (plugin.getArenaPlayer(defender).getTeam() == plugin.getArenaPlayer(shooter).getTeam()) 
						{
							if (!a.isAllowTeamKilling()) 
							{
								event.setCancelled(true);
								shooter.sendMessage(ChatColor.RED + "You cannot hurt your teammate!");
							}
						}
					}
				}
				
				return;
			}
				
			if (att instanceof Snowball)
			{
				Snowball attacker = (Snowball)att;
				if (attacker.getShooter() instanceof Player)
				{
					Player shooter = (Player) attacker.getShooter();
					if ((plugin.isInArena(defender)) && (plugin.isInArena(shooter))) 
					{
						event.setCancelled(false);
						Arena a = plugin.getArena(defender);
						if (plugin.getArenaPlayer(defender).getTeam() == plugin.getArenaPlayer(shooter).getTeam())
						{
							if (!a.isAllowTeamKilling()) 
							{
								event.setCancelled(true);
								shooter.sendMessage(ChatColor.RED + "You cannot hurt your teammate!");
							}
						}
					}
				}
					
				return;
			}
			
			return;
		}
		else
		{
			Player attacker = (Player)att;
			if ((plugin.isInArena(defender)) && (plugin.isInArena(attacker)))
			{
				event.setCancelled(false);
				Arena a = plugin.getArena(defender);
				if (a.getStarttimer() >= 0)
				{
					event.setCancelled(true);
					return;
				}
				if (plugin.getArenaPlayer(defender).getTeam() == plugin.getArenaPlayer(attacker).getTeam()) 
				{
					if (!a.isAllowTeamKilling())
					{
						event.setCancelled(true);
						if (attacker.getItemInHand().getType().equals(Material.GOLD_AXE))
						{
							if ((defender.getHealth() + 2) <= 20)
							{
								defender.setHealth(defender.getHealth() + 2);
								attacker.sendMessage(ChatColor.GRAY + "You have healed " + ChatColor.GOLD + defender.getName() + ChatColor.GRAY + " for 1 heart");
							}
						}
						else
						{
							attacker.sendMessage(ChatColor.RED + "You cannot hurt your teammate!");
						}
					}
				}
			}
		}
	}
	
	// TODO: Rewrite this
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event)
	{
		Entity died = event.getEntity();
		if (died instanceof Player)
		{
			Player pdied = (Player)died;
			if (plugin.isInArena(pdied)) 
			{
				event.getDrops().clear();
				plugin.getArena(pdied).onPlayerDeath(plugin.getArenaPlayer(pdied));
			}
		}
		
		EntityDamageEvent dev = event.getEntity().getLastDamageCause();
		if (dev != null)
		{
			if (dev.getEntity() != null)
			{
				if (dev.getEntity() instanceof Player)
				{
					Player dead = (Player)event.getEntity();
					if (dev.getCause() != null) 
					{
						DamageCause dc = dev.getCause();
						if (dc.equals(DamageCause.ENTITY_ATTACK)) 
						{
							Entity damager = ((EntityDamageByEntityEvent)dev).getDamager();
							if (plugin.isInArena(dead))
							{
								String attackerName = damager.getType().getName();
								if (damager instanceof Player) 
								{
									attackerName = ((Player)damager).getName();
								}
								event.getDrops().clear();
								
								ArenaPlayer dp = plugin.getArenaPlayer(dead);
								dp.setKillstreak(0);
								dp.setDeaths(dp.getDeaths() + 1);
									
								String line1 = ChatColor.GREEN + attackerName + ChatColor.WHITE + " killed " + ChatColor.RED + dead.getName();
								String line2 = ChatColor.RED + dead.getName() + " You have been killed by " + attackerName;
								String line3 = ChatColor.RED + "----------------------------";
								String line4 = ChatColor.RED + "Kills: " + dp.getKills();
								String line5 = ChatColor.RED + "Deaths: " + dp.getDeaths();
								String line6 = ChatColor.RED + "----------------------------";
									
								dead.sendMessage(line1);
								dead.sendMessage(line2);
								dead.sendMessage(line3);
								dead.sendMessage(line4);
								dead.sendMessage(line5);
								dead.sendMessage(line6);
									
								if (damager instanceof Player) 
								{
									Player attacker = (Player)((EntityDamageByEntityEvent)dev).getDamager();
									if (plugin.isInArena(attacker))
									{
										ArenaPlayer ap = plugin.getArenaPlayer(attacker);
										ap.setKills(ap.getKills() + 1);
										ap.setKillstreak(ap
												.getKillstreak() + 1);
										ap.setGameXP(ap.getGameXP() + 100);
										
										line2  = ChatColor.RED + "killed " + dead.getName() + " +100 XP";
										line4 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(attacker).getKills();
										line5 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(attacker).getDeaths();
										
										attacker.sendMessage(line1);
										attacker.sendMessage(line2);
										attacker.sendMessage(line3);
										attacker.sendMessage(line4);
										attacker.sendMessage(line5);
										attacker.sendMessage(line6);
										Arena ar = plugin.getArena(attacker);
										ar.doKillStreak(plugin.getArenaPlayer(attacker));
											
										// Call kill event
										UltimateArenaKillEvent killEvent = new UltimateArenaKillEvent(dp, ap, ar);
										plugin.getServer().getPluginManager().callEvent(killEvent);
									}
								}
							}
							
							return;
						}
						else if (dc.equals(DamageCause.PROJECTILE))
						{
							if (((EntityDamageByEntityEvent)dev).getDamager().getType().toString().toLowerCase().equals("snowball"))
							{
								Entity bullet = ((EntityDamageByEntityEvent)dev).getDamager();
								if (bullet instanceof Snowball) 
								{
									if (((Snowball)((EntityDamageByEntityEvent)dev).getDamager()).getShooter() instanceof Player) 
									{
										Player gunner = (Player) ((Snowball)((EntityDamageByEntityEvent)dev).getDamager()).getShooter();
										if (gunner != null && plugin.isInArena(gunner))
										{
											String gunnerp = gunner.getName();
											event.getDrops().clear();
												
											//Dead player
											if (dead instanceof Player)
											{
												if (plugin.isInArena(dead.getLocation()))
												{
													Player deadplayer = (Player)dead;
													ArenaPlayer dp = plugin.getArenaPlayer(deadplayer);
													
													dp.setKillstreak(0);
													dp.setDeaths(dp
															.getDeaths() + 1);
														
													String line1 = ChatColor.GREEN + deadplayer.getName() + ChatColor.WHITE + " has been killed by " + ChatColor.RED + gunnerp;
													String line2 = ChatColor.RED + "----------------------------";
													String line3 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(deadplayer).getKills();
													String line4 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(deadplayer).getDeaths();
													String line5 = ChatColor.RED + "----------------------------";
												
													deadplayer.sendMessage(line1);
													deadplayer.sendMessage(line2);
													deadplayer.sendMessage(line3);
													deadplayer.sendMessage(line4);
													deadplayer.sendMessage(line5);
												}
											}
												
											//Killer
											if (gunner instanceof Player)
											{
												if (plugin.isInArena(gunner))
												{
													plugin.getArenaPlayer(gunner)
															.setKillstreak(
																	plugin.getArenaPlayer(gunner)
																			.getKillstreak() + 1);
													plugin.getArenaPlayer(gunner)
															.setKills(
																	plugin.getArenaPlayer(gunner)
																			.getKills() + 1);
													plugin.getArenaPlayer(gunner)
															.setGameXP(
																	plugin.getArenaPlayer(gunner)
																			.getGameXP() + 25);
														
													String line1 = ChatColor.GREEN + gunnerp + ChatColor.WHITE + " killed " + ChatColor.RED + dead.getType().getName();
													String line2 = ChatColor.RED + "----------------------------";
													String line3 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(gunner).getKills();
													String line4 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(gunner).getDeaths();
													String line5 = ChatColor.RED + "----------------------------";
													
													gunner.sendMessage(line1);
													gunner.sendMessage(line2);
													gunner.sendMessage(line3);
													gunner.sendMessage(line4);
													gunner.sendMessage(line5);
												}
											}
										}
									}
								}
							}
							
							return;
						}
						else
						{
							if (plugin.isInArena(dead))
							{
								event.getDrops().clear();
								plugin.getArenaPlayer(dead).setKillstreak(0);
								
								plugin.getArenaPlayer(dead).setDeaths(plugin.getArenaPlayer(dead).getDeaths() + 1);
								String line2 = ChatColor.RED + dead.getName() + " You have been killed by " + dc.toString();
								String line3 = ChatColor.RED + "----------------------------";
								String line4 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(dead).getKills();
								String line5 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(dead).getDeaths();
								String line6 = ChatColor.RED + "----------------------------";
									
								dead.sendMessage(line2);
								dead.sendMessage(line3);
								dead.sendMessage(line4);
								dead.sendMessage(line5);
								dead.sendMessage(line6);
							}
							
							return;
						}
					}
				}
				else
				{
					LivingEntity dead = (LivingEntity) event.getEntity();
					if (dev.getCause() != null) 
					{
						DamageCause dc = dev.getCause();
						if (dc.equals(DamageCause.PROJECTILE))
						{
							if (((EntityDamageByEntityEvent)dev).getDamager().getType().toString().equalsIgnoreCase("snowball"))
							{
								Entity bullet = ((EntityDamageByEntityEvent)dev).getDamager();
								if (bullet instanceof Snowball) 
								{
									if (((Snowball)((EntityDamageByEntityEvent)dev).getDamager()).getShooter() instanceof Player) 
									{
										Player gunner = (Player) ((Snowball)((EntityDamageByEntityEvent)dev).getDamager()).getShooter();
										if (gunner != null)
										{
											String gunnerp = gunner.getName();
											event.getDrops().clear();
											
											//Dead player
											if (dead instanceof Player)
											{
												if (plugin.isInArena(dead.getLocation()))
												{
													Player deadplayer = (Player)dead;
													plugin.getArenaPlayer(deadplayer).setKillstreak(0);
													plugin.getArenaPlayer(deadplayer)
															.setDeaths(
																	plugin.getArenaPlayer(deadplayer)
																			.getDeaths() + 1);
													String line1 = ChatColor.GREEN + deadplayer.getName() + ChatColor.WHITE + " has been killed by " + ChatColor.RED + gunnerp;
													String line2 = ChatColor.RED + "----------------------------";
													String line3 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(deadplayer).getKills();
													String line4 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(deadplayer).getDeaths();
													String line5 = ChatColor.RED + "----------------------------";
												
													deadplayer.sendMessage(line1);
													deadplayer.sendMessage(line2);
													deadplayer.sendMessage(line3);
													deadplayer.sendMessage(line4);
													deadplayer.sendMessage(line5);
												}
											}
												
											//Killer
											if (gunner instanceof Player)
											{
												if (plugin.isInArena(gunner))
												{
													plugin.getArenaPlayer(gunner)
															.setKillstreak(
																	plugin.getArenaPlayer(gunner)
																			.getKillstreak() + 1);
													plugin.getArenaPlayer(gunner)
															.setKills(
																	plugin.getArenaPlayer(gunner)
																			.getKills() + 1);
													plugin.getArenaPlayer(gunner)
															.setGameXP(
																	plugin.getArenaPlayer(gunner)
																			.getGameXP() + 25);
														
													String line1 = ChatColor.GREEN + gunnerp + ChatColor.WHITE + " killed " + ChatColor.RED + dead.getType().getName();
													String line2 = ChatColor.RED + "----------------------------";
													String line3 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(gunner).getKills();
													String line4 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(gunner).getDeaths();
													String line5 = ChatColor.RED + "----------------------------";
													
													gunner.sendMessage(line1);
													gunner.sendMessage(line2);
													gunner.sendMessage(line3);
													gunner.sendMessage(line4);
													gunner.sendMessage(line5);
												}
											}
											
											if ((gunner instanceof Player) && (dead instanceof Player))
											{
												if ((gunner != null) && (dead != null))
												{
													ArenaPlayer ag = plugin.getArenaPlayer(gunner);
													ArenaPlayer ad = plugin.getArenaPlayer((Player)dead);
													Arena ar = plugin.getArena(gunner);
														
													UltimateArenaKillEvent killEvent = new UltimateArenaKillEvent(ad, ag, ar);
													plugin.getServer().getPluginManager().callEvent(killEvent);
												}
											}
										}
									}
								}
							}
							
							return;
						}
						else if (dc.equals(DamageCause.ENTITY_ATTACK)) 
						{
							Entity damager = ((EntityDamageByEntityEvent)dev).getDamager();
							if (damager instanceof Player) 
							{
								Player attacker = (Player)((EntityDamageByEntityEvent)dev).getDamager();
								if (plugin.isInArena(attacker)) 
								{
									event.getDrops().clear();
									if (plugin.isInArena(attacker.getLocation()))
									{
										plugin.getArenaPlayer(attacker).setKills(
												plugin.getArenaPlayer(attacker).getKills() + 1);
										plugin.getArenaPlayer(attacker).setKillstreak(
												plugin.getArenaPlayer(attacker).getKillstreak() + 1);
										plugin.getArenaPlayer(attacker).setGameXP(
												plugin.getArenaPlayer(attacker).getGameXP() + 25);
										
										String attstr = dead.getType().getName();
										
										String line1 = ChatColor.GREEN + attacker.getName() + ChatColor.WHITE + " killed " + ChatColor.RED + attstr;
										String line2 = ChatColor.RED + "killed " + attstr + " +25 XP";
										String line3 = ChatColor.RED + "----------------------------";
										String line4 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(attacker).getKills();
										String line5 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(attacker).getDeaths();
										String line6 = ChatColor.RED + "----------------------------";
											
										attacker.sendMessage(line1);
										attacker.sendMessage(line2);
										attacker.sendMessage(line3);
										attacker.sendMessage(line4);
										attacker.sendMessage(line5);
										attacker.sendMessage(line6);
											
										Arena ar = plugin.getArena(attacker);
										ar.doKillStreak(plugin.getArenaPlayer(attacker));
									}
								}
							}
							
							return;
						}
						else if (dc.equals(DamageCause.PROJECTILE)) 
						{
							Entity damager = ((EntityDamageByEntityEvent)dev).getDamager();
							if (damager instanceof Arrow) 
							{
								if (((Arrow)((EntityDamageByEntityEvent)dev).getDamager()).getShooter() instanceof Player) 
								{
									Player attacker = (Player) ((Arrow)((EntityDamageByEntityEvent)dev).getDamager()).getShooter();
									if (plugin.isInArena(attacker))
									{
										event.getDrops().clear();
										if (plugin.isInArena(attacker.getLocation())) 
										{
											plugin.getArenaPlayer(attacker).setKills(
													plugin.getArenaPlayer(attacker).getKills() + 1);
											plugin.getArenaPlayer(attacker)
													.setKillstreak(
															plugin.getArenaPlayer(attacker)
																	.getKillstreak() + 1);
											plugin.getArenaPlayer(attacker).setGameXP(
													plugin.getArenaPlayer(attacker).getGameXP() + 25);
											
											String attstr = dead.getType().getName();
												
											String line1 = ChatColor.GREEN + attacker.getName() + ChatColor.WHITE + " killed " + ChatColor.RED + attstr;
											String line2 = ChatColor.RED + "killed " + attstr + " +25 XP";
											String line3 = ChatColor.RED + "----------------------------";
											String line4 = ChatColor.RED + "Kills: " + plugin.getArenaPlayer(attacker).getKills();
											String line5 = ChatColor.RED + "Deaths: " + plugin.getArenaPlayer(attacker).getDeaths();
											String line6 = ChatColor.RED + "----------------------------";
											
											attacker.sendMessage(line1);
											attacker.sendMessage(line2);
											attacker.sendMessage(line3);
											attacker.sendMessage(line4);
											attacker.sendMessage(line5);
											attacker.sendMessage(line6);
												
											Arena ar = plugin.getArena(attacker);
											ar.doKillStreak(plugin.getArenaPlayer(attacker));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}