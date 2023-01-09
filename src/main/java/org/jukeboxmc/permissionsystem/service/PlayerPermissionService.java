package org.jukeboxmc.permissionsystem.service;

import org.jukeboxmc.permissionsystem.PermissionSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class PlayerPermissionService {

    private final Connection connection;
    private final Map<UUID, Set<String>> playerPermissionList = new HashMap<>();

    public PlayerPermissionService( PermissionSystem plugin ) {
        this.connection = plugin.getConnection();
    }

    public CompletableFuture<Set<String>> getPermissionsByUUID( UUID uuid ) {
        return CompletableFuture.supplyAsync( () -> {
            if ( this.playerPermissionList.containsKey( uuid ) ) {
                return this.playerPermissionList.get( uuid );
            }
            Set<String> permissions = new HashSet<>();
            try ( PreparedStatement statement = this.connection.prepareStatement( "SELECT * FROM player_permission WHERE uuid= ?" ) ) {
                statement.setString( 1, uuid.toString() );
                ResultSet resultSet = statement.executeQuery();
                while ( resultSet.next() ) {
                    permissions.add( resultSet.getString( "permission" ).toLowerCase() );
                }
                resultSet.close();
                this.playerPermissionList.put( uuid, permissions );
                return permissions;
            } catch ( SQLException e ) {
                return new HashSet<>();
            }
        } );
    }

    public CompletableFuture<Boolean> playerHasPermission( UUID uuid, String permission ) {
        return CompletableFuture.supplyAsync( () -> {
            try ( PreparedStatement statement = this.connection.prepareStatement( "SELECT * FROM player_permission WHERE uuid= ? AND permission= ?" ) ) {
                statement.setString( 1, uuid.toString() );
                statement.setString( 2, permission.toLowerCase() );
                ResultSet resultSet = statement.executeQuery();
                boolean exists = resultSet.next();
                resultSet.close();
                return exists;
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
            return false;
        } );
    }

    public void addPlayerPermission( UUID uuid, String permission ) {
        CompletableFuture.runAsync( () -> {
            try ( PreparedStatement statement = this.connection.prepareStatement( "INSERT INTO player_permission (uuid, permission) VALUES (?, ?)" ) ) {
                statement.setString( 1, uuid.toString() );
                statement.setString( 2, permission.toLowerCase() );
                statement.executeUpdate();
                if ( this.playerPermissionList.containsKey( uuid ) ) {
                    this.playerPermissionList.get( uuid ).add( permission.toLowerCase() );
                } else {
                    this.playerPermissionList.put( uuid, Collections.singleton( permission.toLowerCase() ) );
                }
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        } );
    }

    public void removePlayerPermission( UUID uuid, String permission ) {
        CompletableFuture.runAsync( () -> {
            try ( PreparedStatement statement = this.connection.prepareStatement( "DELETE FROM player_permission WHERE uuid= ? AND permission= ?" ) ) {
                statement.setString( 1, uuid.toString() );
                statement.setString( 2, permission.toLowerCase() );
                statement.executeUpdate();
                if ( this.playerPermissionList.containsKey( uuid ) ) {
                    this.playerPermissionList.get( uuid ).remove( permission.toLowerCase() );
                }
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        } );
    }

    public Map<UUID, Set<String>> getPlayerPermissionList() {
        return this.playerPermissionList;
    }
}
