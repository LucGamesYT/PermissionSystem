package org.jukeboxmc.permissionsystem.config;

import org.jukeboxmc.config.Config;
import org.jukeboxmc.config.ConfigType;
import org.jukeboxmc.permissionsystem.PermissionSystem;

import java.io.File;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class MySqlConfig {

    private final Config config;

    public MySqlConfig( PermissionSystem plugin ) {
        File file = new File( plugin.getDataFolder(), "mysql.yml" );
        this.config = new Config( file, ConfigType.YAML );
    }

    public void createConfig() {
        this.config.addDefault( "mysql.url", "jdbc:mysql://localhost/DATABASE?connectTimeout=5000&socketTimeout=30000&autoConnect=true&autoReconnect=true" );
        this.config.addDefault( "mysql.user", "user" );
        this.config.addDefault( "mysql.password", "password" );
        this.config.save();
    }

    public String getUrl() {
        return this.config.getString( "mysql.url" );
    }

    public String getUser() {
        return this.config.getString( "mysql.user" );
    }

    public String getPassword() {
        return this.config.getString( "mysql.password" );
    }
}
