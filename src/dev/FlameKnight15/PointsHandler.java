package dev.FlameKnight15;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;

public class PointsHandler implements Listener {

    Cardinal c;

    public PointsHandler(Cardinal c) {
        this.c = c;
    }

    @EventHandler
    public void killMob(EntityDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player) { //Check if the killer of the entity is a player
            Entity killedMob = e.getEntity(); //Mob killed by the player
            String mobName = killedMob.getCustomName(); //Get the custom name of the mob killed

            //Make sure that the config has mobs entered into it
            if (c.config.getConfigurationSection("Mobs").getKeys(false).isEmpty()) {
                c.getLogger().info("Config section 'Mobs' is missing entries! Please enter mobs here!");
                return;
            }
            //Run through all the mobs in the config
            for (String a : c.config.getConfigurationSection("Mobs").getKeys(false)) {
                if (a.equals(mobName)) { //Check that the mob killed is a mob with a custom name and in the config
                    int points = c.config.getInt("Mobs." + a);
                    if (e.getEntity().getKiller() instanceof Player) {
                        Player p = e.getEntity().getKiller();
                        for (User u : c.allOnlinePlayers) { //Sort through all the players as users
                            if (u.getPlayer().getName().equals(p.getName())) {//Check if the player matches a user already in the database
                                u.setPoints(u.getPoints() + points);//Assign the users their points for killing the mob
                                System.out.println(u.getPoints());
                                if (!c.config.getString("Messages.MobKillMsg").isEmpty()) { //Make sure there is a message to send to the player

                                    //Replace all msg variables
                                    String msg = ChatColor.translateAlternateColorCodes('&', c.config.getString("Messages.MobKillMsg"));
                                    if (msg.contains("{MOB}"))
                                        msg = msg.replace("{MOB}", mobName);
                                    if (msg.contains("{PLAYER}"))
                                        msg = msg.replace("{PLAYER}", p.getName());
                                    if (msg.contains("{POINTS}"))
                                        msg = msg.replace("{POINTS}", points + "");
                                    if (msg.contains("{RANK}"))
                                        msg = msg.replace("{RANK}", u.getRank() + "");
                                    msg = c.prefix + msg;


                                    p.sendMessage(msg);
                                    c.displayPlayerRanks();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setPointsFromFile(){
        for(String str : c.points){
            String[] info = str.split(":");
            for (User u : c.allOnlinePlayers){
                if (info[0].equalsIgnoreCase(u.getPlayer().getName())){
                    u.setPoints(Integer.parseInt(info[1]));
                }
            }
        }
    }


}
