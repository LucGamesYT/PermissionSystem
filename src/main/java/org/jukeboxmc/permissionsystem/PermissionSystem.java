package org.jukeboxmc.permissionsystem;

import lombok.Getter;
import org.jukeboxmc.JukeboxMC;
import org.jukeboxmc.command.CommandManager;
import org.jukeboxmc.permissionsystem.command.MyPermsCommand;
import org.jukeboxmc.permissionsystem.config.MySqlConfig;
import org.jukeboxmc.permissionsystem.listener.PlayerJoinListener;
import org.jukeboxmc.permissionsystem.listener.PlayerQuitListener;
import org.jukeboxmc.permissionsystem.service.GroupService;
import org.jukeboxmc.permissionsystem.service.PlayerGroupService;
import org.jukeboxmc.permissionsystem.service.PlayerPermissionService;
import org.jukeboxmc.plugin.Plugin;
import org.jukeboxmc.plugin.PluginManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * @author LucGamesYT
 * @version 1.0
 */
@Getter
public class PermissionSystem extends Plugin {

    private final Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    private Connection connection;

    private MySqlConfig mySqlConfig;
    private GroupService groupService;
    private PlayerGroupService playerGroupService;
    private PlayerPermissionService playerPermissionService;

    @Override
    public void onEnable() {
        this.mySqlConfig = new MySqlConfig( this );
        this.mySqlConfig.createConfig();

        this.initMySQL();

        this.groupService = new GroupService( this );
        this.playerGroupService = new PlayerGroupService( this );
        this.playerPermissionService = new PlayerPermissionService( this );

        PluginManager pluginManager = JukeboxMC.getPluginManager();
        pluginManager.registerListener( new PlayerJoinListener( this ) );
        pluginManager.registerListener( new PlayerQuitListener( this ) );

        CommandManager commandManager = pluginManager.getCommandManager();
        commandManager.registerCommand( new MyPermsCommand( this ) );
    }

    private void initMySQL() {
        try {
            this.connection = DriverManager.getConnection( this.mySqlConfig.getUrl(), this.mySqlConfig.getUser(), this.mySqlConfig.getPassword() );

            CompletableFuture.runAsync( () -> {
                try ( PreparedStatement statement = this.connection.prepareStatement( "CREATE TABLE IF NOT EXISTS groups(" +
                        "id INT AUTO_INCREMENT NOT NULL," +
                        " groupName TEXT," +
                        " primary key(id));" ) ) {
                    statement.executeUpdate();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
                try ( PreparedStatement statement = this.connection.prepareStatement( "CREATE TABLE IF NOT EXISTS groups_permission(" +
                        "id INT AUTO_INCREMENT NOT NULL," +
                        " groupName TEXT," +
                        " permission TEXT," +
                        " primary key(id));" ) ) {
                    statement.executeUpdate();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
                try ( PreparedStatement statement = this.connection.prepareStatement( "CREATE TABLE IF NOT EXISTS player_group(" +
                        "id INT AUTO_INCREMENT NOT NULL," +
                        " uuid VARCHAR(36)," +
                        " groupName TEXT," +
                        " primary key(id));" ) ) {
                    statement.executeUpdate();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }

                try ( PreparedStatement statement = this.connection.prepareStatement( "CREATE TABLE IF NOT EXISTS player_permissions(" +
                        "id INT AUTO_INCREMENT NOT NULL," +
                        " uuid VARCHAR(36)," +
                        " permission TEXT," +
                        " primary key(id));" ) ) {
                    statement.executeUpdate();
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            } );
            this.getLogger().info( "§aDie Verbindung zu MySQL wurde erfolgreich hergestellt." );
        } catch ( SQLException e ) {
            e.printStackTrace();
            this.getLogger().info( "§cEs konnte keine Verbindung zu MySQL hergestellt werden." );
        }
    }

    public boolean isValidUUID(String str) {
        if (str == null) {
            return false;
        }
        return this.UUID_REGEX_PATTERN.matcher(str).matches();
    }

}
