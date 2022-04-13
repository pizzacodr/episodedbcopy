package com.github.pizzacodr.episodedbcopy;

import java.sql.SQLException;

import org.aeonbits.owner.ConfigFactory;

public class App {

	public static void main(String[] args) throws SQLException {	
		
		ConfigFile configFile = ConfigFactory.create(ConfigFile.class, System.getProperties());
		Database database = new Database(configFile.dbFileLocation());
		database.insertInDBIfNewEpisode();
	}
}
