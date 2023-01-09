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
public class PlayerGroupService {

    private final Connection connection;

    private final Map<UUID, Set<String>> playerGroupList = new HashMap<>();

    public PlayerGroupService( PermissionSystem plugin ) {
        this.connection = plugin.getConnection();
    }

    public CompletableFuture<Set<String>> getGroupsByUUID( UUID uuid ) {
        return CompletableFuture.supplyAsync( () -> {
            if ( this.playerGroupList.containsKey( uuid ) ) {
                return this.playerGroupList.get( uuid );
            }
            Set<String> groups = new HashSet<>();
            try ( PreparedStatement statement = this.connection.prepareStatement( "SELECT * FROM player_group WHERE uuid= ?" ) ) {
                statement.setString( 1, uuid.toString() );
                ResultSet resultSet = statement.executeQuery();
                while ( resultSet.next() ) {
                    groups.add( resultSet.getString( "groupName" ).toLowerCase() );
                }
                resultSet.close();
                this.playerGroupList.put( uuid, groups );
                return groups;
            } catch ( SQLException e ) {
                return new HashSet<>();
            }
        } );
    }

    public CompletableFuture<Boolean> playerHasGroup( UUID uuid, String group ) {
        return CompletableFuture.supplyAsync( () -> {
            try ( PreparedStatement statement = this.connection.prepareStatement( "SELECT * FROM player_group WHERE uuid= ? AND groupName= ?" ) ) {
                statement.setString( 1, uuid.toString() );
                statement.setString( 2, group.toLowerCase() );
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

    public void addPlayerGroup( UUID uuid, String group ) {
        CompletableFuture.runAsync( () -> {
            try ( PreparedStatement statement = this.connection.prepareStatement( "INSERT INTO player_group (uuid, groupName) VALUES(?, ?)" ) ) {
                statement.setString( 1, uuid.toString() );
                statement.setString( 2, group.toLowerCase() );
                statement.executeUpdate();

                if ( this.playerGroupList.containsKey( uuid ) ) {
                    this.playerGroupList.get( uuid ).add( group.toLowerCase() );
                } else {
                    this.playerGroupList.put( uuid, Collections.singleton( group.toLowerCase() ) );
                }
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        } );
    }

    public void removePlayerGroup( UUID uuid, String group ) {
        CompletableFuture.runAsync( () -> {
            try ( PreparedStatement statement = this.connection.prepareStatement( "DELETE FROM player_group WHERE uuid= ? AND groupName= ?" ) ) {
                statement.setString( 1, uuid.toString() );
                statement.setString( 2, group.toLowerCase() );
                statement.executeUpdate();

                if ( this.playerGroupList.containsKey( uuid ) ) {
                    this.playerGroupList.get( uuid ).remove( group.toLowerCase() );
                }
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        } );
    }

    public Map<UUID, Set<String>> getPlayerGroupList() {
        return this.playerGroupList;
    }
}
