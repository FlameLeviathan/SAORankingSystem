package dev.FlameKnight15;

import org.bukkit.entity.Player;

public class User {
	//Variables
	int rank; //Rank of player
	int points; //Points that the player has
	Player player; //Player variable
	//static Cardinal c = new Cardinal(); //Creates new cardinal system IF DISCONNECT IN CODE CHANGE THIS PART!
	//String prefix = c.getConfig().get("rankPrefix").toString();


	//Create what a User is and how to call it
	User(Player p){
		this.player = p;
	}
	
	//Set the rank for the player
	void setRank(int rank){
		this.rank = rank;
	}
	
	//Set the points of the player
	void setPoints(int points){
	    this.points = points;
	    System.out.println("Points: " + getPoints());
		/*for(int i = 0; i < Cardinal.points.size(); i++){
			String[] info = Cardinal.points.get(i).split(":");
			if(info[0].equals(player.getName())){
				Cardinal.points.add(player.getDisplayName() + ":" + getPoints() + points);
				Cardinal.points.remove(i);
			}
		}*/
	}
	
	//Get the rank of the player
	int getRank(){
		return rank;
	}
	
	//Get the points of the player
	int getPoints(){
	    return points;
/*		for(String string : Cardinal.points){
			String[] info = string.split(":");
			if(info[0].equals(player.getName())){
				Integer.getInteger(info[1]);
			}
		}
		return 0;*/
	}

	Player getPlayer(){
		return player;
	}
}
