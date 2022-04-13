package com.github.pizzacodr.episodedbcopy;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DatabaseTest {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseTest.class);
	
	@Test
	void testDatabase(){
		assertThrows(SQLException.class,
                ()->{
                new Database("wrongPath");
                });
	}

	@Test
	void testInsertInDBIfNewEpisode(@TempDir Path tempDir) throws IOException, SQLException {
		ClassLoader classLoader = this.getClass().getClassLoader();
        File sourceFile = new File(classLoader.getResource("podbean.sqlite").getFile());
        logger.debug("sourceFile exists: " + sourceFile.exists());
        
        logger.debug("Source path: " + sourceFile.getAbsolutePath());
        
        File destFile = new File(tempDir.toString() + "/podbean.sqlite");
        Files.copy(sourceFile.toPath(), destFile.toPath());
        
        logger.debug("destFile exists: " + destFile.exists());
        logger.debug("destFile path: " + destFile.getAbsolutePath());
        logger.debug("destFile can read: " + destFile.canRead());
        
        String connectionString = "jdbc:sqlite:" + destFile.getAbsolutePath();
        logger.debug("Connection String: " + connectionString);
		
        Database database = new Database(connectionString);
		database.insertInDBIfNewEpisode();
		
		Connection connection = DriverManager.getConnection(connectionString);
    	Statement statement = connection.createStatement();
    	ResultSet rs = statement.executeQuery("SELECT UUID FROM EPISODE WHERE TITLE = 'Dwelleth in Us';");
    	
    	String uuidFromDB = "";
    	while (rs.next()) {
    		uuidFromDB = rs.getString("UUID");
	    }
    	
    	assertTrue(uuidFromDB.contains("4854d0b2-db91-4d7b-a6fc-bd00478e2a95"), "DB Check Failed");
	}
}
