package com.github.pizzacodr.episodedbcopy;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "file:${user.dir}/episodedbcopy.properties", 
"file:${user.home}/episodedbcopy.properties"})

 interface ConfigFile extends Config {

	@DefaultValue("jdbc:sqlite:${user.home}/podbean.sqlite")
	String dbFileLocation();
}
