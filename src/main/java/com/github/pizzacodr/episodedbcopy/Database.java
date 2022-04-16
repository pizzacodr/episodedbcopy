package com.github.pizzacodr.episodedbcopy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Database {
	
	private static Logger logger = LoggerFactory.getLogger(Database.class);
	private Connection connection;
	private Statement statement;
	
	Database(String dbFileLocation) throws SQLException {
		connection = DriverManager.getConnection(dbFileLocation);
		statement = connection.createStatement();
	}
	
	boolean insertInDBIfNewEpisode() throws SQLException {
		
		ResultSet resultSetFromNewEpisode = statement.executeQuery("SELECT * FROM EPISODE ORDER BY ID DESC LIMIT 0, 1;");
		
		while (resultSetFromNewEpisode.next()) {
			
			Episode episode = new Episode();
			
			int idEpisode = resultSetFromNewEpisode.getInt(1);
			logger.debug("idEpisode: {}", idEpisode);
			episode.setId(idEpisode);
			
			String uuidEpisode = resultSetFromNewEpisode.getString(2);
			logger.debug("uuidEpisode: {}", uuidEpisode);
			episode.setUuid(uuidEpisode);
			
			String titleEpisode = resultSetFromNewEpisode.getString(3);
			logger.debug("titleEpisode: {}", titleEpisode);
			episode.setTitle(titleEpisode);
			
			String linkEpisode = resultSetFromNewEpisode.getString(4);
			logger.debug("linkEpisode: {}", linkEpisode);
			episode.setLink(linkEpisode);
			
			String contentEpisode = resultSetFromNewEpisode.getString(5);
			logger.debug("contentEpisode: {}", contentEpisode);
			episode.setContent(contentEpisode);
			
			String sharelinkEpisode = resultSetFromNewEpisode.getString(6);
			logger.debug("sharelinkEpisode: {}", sharelinkEpisode);
			episode.setShareLink(sharelinkEpisode);
			
			String dateEpisode = resultSetFromNewEpisode.getString(7);
			logger.debug("dateEpisode: {}", dateEpisode);
			episode.setDate(dateEpisode);
			
			boolean isItOnTable = isEpisodeOnTable(uuidEpisode);
			
			if (!isItOnTable) {
			
				insertIntoTable(episode);
				logger.debug("New content inserted");
				
				return true;
			}
        }
		
		logger.debug("No new content");
		return false;
	}

	private void insertIntoTable(Episode episode) throws SQLException {
		
		try (PreparedStatement prepStm = connection.prepareStatement("INSERT INTO MASTODON VALUES(NULL, ?, ?, ?, ?);")){
			prepStm.setString(1, episode.getUuid());
			prepStm.setString(2, episode.getTitle() + "\n" + episode.getContent());
			prepStm.setString(3, episode.getShareLink());
			prepStm.setInt(4, 0);
			prepStm.executeUpdate();
		}
	}
	
	private boolean isEpisodeOnTable(String uuidEpisode) throws SQLException {
		
		ResultSet rsMastodon = statement.executeQuery("SELECT COUNT(*) FROM MASTODON WHERE UUID = '" + uuidEpisode + "';");
		
		int fetchSize = 0;
		while (rsMastodon.next()) {
			fetchSize = rsMastodon.getInt(1);
        }
		
		logger.debug("fetchSize: {}", fetchSize);
		return fetchSize != 0;
	}
}
