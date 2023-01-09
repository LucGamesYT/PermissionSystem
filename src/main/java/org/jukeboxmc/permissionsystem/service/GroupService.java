package org.jukeboxmc.permissionsystem.service;

import org.jukeboxmc.permissionsystem.PermissionSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class GroupService {

    private final Connection connection;

    public GroupService( PermissionSystem plugin ) {
        this.connection = plugin.getConnection();
    }

    public CompletableFuture<Boolean> groupExists( String group ) {
        return CompletableFuture.supplyAsync( () -> {
            try ( PreparedStatement statement = this.connection.prepareStatement( "SELECT * FROM groups WHERE groupName= ?" ) ) {
                statement.setString( 1, group.toLowerCase() );
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

    public void createGroup( String group ) {
        CompletableFuture.runAsync( () -> {
            try ( PreparedStatement statement = this.connection.prepareStatement( "INSERT INTO groups (groupName) VALUES(?)" ) ) {
                statement.setString( 1, group.toLowerCase() );
                statement.executeUpdate();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        } );
    }

    public void deleteGroup( String group ) {
        CompletableFuture.runAsync( () -> {
            try ( PreparedStatement statement = this.connection.prepareStatement( "DELETE FROM groups WHERE groupName= ?" ) ) {
                statement.setString( 1, group.toLowerCase() );
                statement.executeUpdate();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        } );
    }

    public CompletableFuture<Set<String>> getPermissionsByGroup( String group ) {
        return CompletableFuture.supplyAsync( () -> {
            Set<String> permissions = new HashSet<>();
            try ( PreparedStatement statement = this.connection.prepareStatement( "SELECT * FROM groups_permission WHERE groupName= ?" ) ) {
                statement.setString( 1, group.toLowerCase() );
                ResultSet resultSet = statement.executeQuery();
                while ( resultSet.next() ) {
                    permissions.add( resultSet.getString( "permission" ).toLowerCase() );
                }
                resultSet.close();
                return permissions;
            } catch ( SQLException e ) {
                return new HashSet<>();
            }
        } );
    }

    public void addPermissionToGroup( String group, String permission ) {
        this.groupExists( group ).whenComplete( ( groupExists, throwable ) -> {
            if ( groupExists ) {
                try ( PreparedStatement checkStatement = this.connection.prepareStatement( "SELECT * FROM groups_permission WHERE groupName= ? AND permission= ?" ) ) {
                    checkStatement.setString( 1, group.toLowerCase() );
                    checkStatement.setString( 2, permission );
                    ResultSet resultSet = checkStatement.executeQuery();
                    boolean groupPermissionExists = resultSet.next();
                    resultSet.close();
                    if ( !groupPermissionExists ) {
                        try ( PreparedStatement statement = this.connection.prepareStatement( "INSERT INTO groups_permission (groupName, permission) VALUES(?,?)" ) ) {
                            statement.setString( 1, group.toLowerCase() );
                            statement.setString( 2, permission );
                            statement.executeUpdate();
                        } catch ( SQLException e ) {
                            e.printStackTrace();
                        }
                    }
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    public void removePermissionFromGroup( String group, String permission ) {
        this.groupExists( group ).whenComplete( ( groupExists, throwable ) -> {
            if ( groupExists ) {
                try ( PreparedStatement checkStatement = this.connection.prepareStatement( "SELECT * FROM groups_permission WHERE groupName= ? AND permission= ?" ) ) {
                    checkStatement.setString( 1, group.toLowerCase() );
                    checkStatement.setString( 2, permission );
                    ResultSet resultSet = checkStatement.executeQuery();
                    boolean groupPermissionExists = resultSet.next();
                    resultSet.close();
                    if ( groupPermissionExists ) {
                        try ( PreparedStatement statement = this.connection.prepareStatement( "DELETE FROM groups_permission WHERE groupName= ? AND permission= ?" ) ) {
                            statement.setString( 1, group.toLowerCase() );
                            statement.setString( 2, permission );
                            statement.executeUpdate();
                        } catch ( SQLException e ) {
                            e.printStackTrace();
                        }
                    }
                } catch ( SQLException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }
}
