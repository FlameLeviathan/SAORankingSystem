package dev.FlameKnight15;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

public class Cardinal extends JavaPlugin implements Listener{
	
	File cFile = new File(getDataFolder(), "config.yml");
	public FileConfiguration config = YamlConfiguration.loadConfiguration(cFile);
	String prefix;
	String rankPrefix;
	PointsHandler pointsHandler = new PointsHandler(this);
	ArrayList<User> allOnlinePlayers = new ArrayList<>();
	static ArrayList<String> points = new ArrayList<String>();

	
	@Override
	public void onEnable(){
		//Add all players to an array as Users
        for(Player p : Bukkit.getOnlinePlayers()){
            if(!allOnlinePlayers.contains(p)){
                allOnlinePlayers.add(new User(p));
            }
        }

/*		for(OfflinePlayer p : Bukkit.getOfflinePlayers()){
			if(!allPlayers.contains(p)){
			    Player player = (Player) p;
				allPlayers.add(new User(player));
			}
		}*/


/*		for(User user : allPlayers){
		    System.out.println("User: " + user.getPlayer().getName());
        }*/
		System.out.println(allOnlinePlayers.size() + " Online Players Loaded!");
		
		//Register placeholder
		PlaceholderAPI.registerPlaceholder(this, "SAORANK",
                new PlaceholderReplacer() {
        			@Override
        			public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
                        Player p = e.getPlayer();
                        User[] players = organizeUsers();
                        String placeHolder = null;
    					for(int i = 0; i < players.length; i++){
    						//Displays the rank above the player's head
    						if(Bukkit.getOnlinePlayers().contains(players[i].getPlayer())){
    							if(players[i].getPlayer().equals(p)){
    							//System.out.println(players[i].getPlayer().getName() + " | " + (i+1));
    								placeHolder = rankPrefix + players[i].getRank() + " ";
    								
    							}
    						}
    					}
                        return placeHolder;
                    }
                });
				
		getLogger().info("onEnable has been invoked!");
		saveDefaultConfig();
		config.options().copyDefaults(true);
        config = YamlConfiguration.loadConfiguration(cFile);
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("chat-prefix"));
        rankPrefix = config.getString("rankPrefix");
        registerEvents(this, new PointsHandler(this), this);
        
		//Check if the MVDWPlaceholderAPI plugin is loaded
		if(!Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI")){
			this.getLogger().info("Please install the MVdWPlaceholderAPI! It is required in order to run the SAO Ranking System Plugin!");
			//this.onDisable();
		}

		try {
			points = (ArrayList<String>) SLAPI.load("plugins/SAORankingSystem/points.bin");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        pointsHandler.setPointsFromFile();
		System.out.println(points.size());
	}

	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
		Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}
	
	@Override
	public void onDisable(){
		getLogger().info("onDisable has been invoked!");
		config = YamlConfiguration.loadConfiguration(cFile);
		config.options().copyDefaults(true);
        System.out.println(points.size());
		try {
		    for(String s: points) {
                SLAPI.save(s, "plugins/SAORankingSystem/points.bin");
            }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * On player join refresh all user's ranks 
	 * @param e
	 */
	@EventHandler
	public void addPlayerToFile(PlayerJoinEvent e){
		//Refresh the rankings when a player joins
		displayPlayerRanks();
		
		//
		
	}
	
	/**
	 * Refresh all player rankings and go through all players and display their rank above their head
	 */
	public void displayPlayerRanks(){
		User[] players = organizeUsers();
		
		for(int i = 0; i < players.length; i++){
			//Sees if the player is online and adds them to the onlinePlayers 

			
			//Displays the rank above the player's head
			if(Bukkit.getOnlinePlayers().contains(players[i].getPlayer())){
				String placeHolder = rankPrefix + players[i].getRank() + " ";
				PlaceholderAPI.replacePlaceholders(players[i].getPlayer(), placeHolder);
				players[i].getPlayer().setCustomName(placeHolder + players[i].getPlayer().getName());
			}
		}
	}
	
	//Organize the players from greatest to least based on their points and set the player's rank
	public User[] organizeUsers(){
		// Variables
		// Get all players
        ArrayList<User> allPlayers = new ArrayList<>();
/*		for(OfflinePlayer p : Bukkit.getOfflinePlayers()){
			if(!allPlayers.contains(p)){ //May have an error here where it is searching for a user but we give it a player instead. May have to create a new User with player
				allPlayers.add(new User(p.getPlayer()));
			}
		}*/
		for(Player p : Bukkit.getOnlinePlayers()){
			if(!allPlayers.contains(p)){
				allPlayers.add(new User(p));
			}
		}

		for (String s : points) {
            Bukkit.getPlayer("FlameKnight15").sendMessage(s);
        }
		
		// Sort the users
		User[] sorted = new User[allPlayers.size()];
		for(int i = 0; i < allPlayers.size(); i++){
			sorted[i] = allPlayers.get(i);
		}
		        for (int i = 0; i < sorted.length; i++) {
		            for (int j = i+1; j < sorted.length; j++) {
		                if ( (sorted[i].getPoints() > sorted[j].getPoints()) && (i != j) ) {
		                    User temp = sorted[j];
		                    sorted[j] = sorted[i];
		                    sorted[i] = temp;
		                }
		            }
		        }		
		for(int i = 0; i < sorted.length; i++){
			sorted[i].setRank(i+1);
		}
		return sorted;
	}
	
    /**
     * Handles the use of commands
     */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("cranks") || cmd.getName().equalsIgnoreCase("cardinalranks")|| cmd.getName().equalsIgnoreCase("cr")){
			if(args.length == 1){
				if (args[0].equalsIgnoreCase("reload")) {
					if(sender.hasPermission("cardinalranks.reload")) {
						if (YamlConfiguration.loadConfiguration(cFile) != null) {
							reloadConfig();
							config = YamlConfiguration.loadConfiguration(cFile);
							sender.sendMessage(ChatColor.GREEN + "SAORankingSystem has been reloaded!");
						} else {
							saveDefaultConfig();
							config.options().copyDefaults(true);
							sender.sendMessage(ChatColor.GREEN + "SAORankingSystem has been reloaded!");
						}


						return true;
					}
				}
				if (args[0].equalsIgnoreCase("rerank")) {
					if(sender.hasPermission("cardinalranks.rerank")) {
						displayPlayerRanks();

						sender.sendMessage(ChatColor.GREEN + "Re-ranking complete!");
						return true;
					}
				}
			}else if(args.length == 0){
				sender.sendMessage(ChatColor.AQUA + "Available commands:");
				if(sender.hasPermission("cardinalranks.reload"))
				sender.sendMessage(ChatColor.AQUA + "/cr reload" + ChatColor.GRAY + " - Reloads the config and files");
				if(sender.hasPermission("cardinalranks.rerank"))
				sender.sendMessage(ChatColor.AQUA + "/cr rerank" + ChatColor.GRAY + " - Reranks all players");
			}
		}
		return true;
	}
  
}
