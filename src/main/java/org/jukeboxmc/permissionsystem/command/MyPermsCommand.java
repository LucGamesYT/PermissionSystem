package org.jukeboxmc.permissionsystem.command;

import org.jukeboxmc.JukeboxMC;
import org.jukeboxmc.command.Command;
import org.jukeboxmc.command.CommandData;
import org.jukeboxmc.command.CommandSender;
import org.jukeboxmc.command.annotation.Description;
import org.jukeboxmc.command.annotation.Name;
import org.jukeboxmc.command.annotation.Permission;
import org.jukeboxmc.permissionsystem.PermissionSystem;
import org.jukeboxmc.permissionsystem.service.GroupService;
import org.jukeboxmc.permissionsystem.service.PlayerGroupService;
import org.jukeboxmc.permissionsystem.service.PlayerPermissionService;
import org.jukeboxmc.player.Player;

import java.util.UUID;

/**
 * @author LucGamesYT
 * @version 1.0
 */
@Name ( "permissionsystem" )
@Description ( "This is a command to handle permissions and groups." )
@Permission ( "permissionsystem.command.execute" )
public class MyPermsCommand extends Command {

    private final PermissionSystem plugin;
    private final GroupService groupService;
    private final PlayerGroupService playerGroupService;
    private final PlayerPermissionService playerPermissionService;

    public MyPermsCommand( PermissionSystem plugin ) {
        super( CommandData.builder()
                .setAliases( "perms" )
                .build());
        this.plugin = plugin;
        this.groupService = plugin.getGroupService();
        this.playerGroupService = plugin.getPlayerGroupService();
        this.playerPermissionService = plugin.getPlayerPermissionService();
    }

    /*
    /perms group create <name>
    /perms group delete <name>
    /perms group user add <uuid|name> <group>
    /perms group user remove <uuid|name> <group>
    /perms group add <group> <permission>
    /perms group delete <group> <permission>

    /perms permission add <uuid|name> <permission>
    /perms permission remove <uuid|name> <permission>
     */

    @Override
    public void execute( CommandSender commandSender, String s, String[] args ) {
        if ( args.length == 3 ) {
            if ( args[0].equalsIgnoreCase( "group" ) ) {
                if ( args[1].equalsIgnoreCase( "create" ) ) {
                    String groupName = args[2];
                    this.groupService.groupExists( groupName ).whenComplete( ( exists, throwable ) -> {
                        JukeboxMC.getScheduler().execute( () -> {
                            if ( !exists ) {
                                this.groupService.createGroup( groupName );
                                commandSender.sendMessage( "§aDie Gruppe §e" + groupName + " §awurde erstellt." );
                            } else {
                                commandSender.sendMessage( "§cDie Gruppe §e" + groupName + " exestiert bereits." );
                            }
                        } );
                    } );
                } else if ( args[1].equalsIgnoreCase( "delete" ) ) {
                    String groupName = args[2];
                    this.groupService.groupExists( groupName ).whenComplete( ( exists, throwable ) -> {
                        JukeboxMC.getScheduler().execute( () -> {
                            if ( exists ) {
                                this.groupService.deleteGroup( groupName );
                                commandSender.sendMessage( "§aDie Gruppe §e" + groupName + " §awurde gelöscht." );
                            } else {
                                commandSender.sendMessage( "§cDie Gruppe §e" + groupName + " exestiert nicht." );
                            }
                        } );
                    } );
                }
            }
        } else if ( args.length == 4 ) {
            if ( args[0].equalsIgnoreCase( "group" ) ) {
                if ( args[1].equalsIgnoreCase( "add" ) ) {
                    String groupName = args[2];
                    String permission = args[3];

                    this.groupService.groupExists( groupName ).whenComplete( ( exists, throwable ) -> {
                        JukeboxMC.getScheduler().execute( () -> {
                            if ( exists ) {
                                this.groupService.addPermissionToGroup( groupName, permission );
                                commandSender.sendMessage( "§aDie Permission §e" + permission + " §awurde der Gruppe §e" + groupName + " §ahinzugefügt." );
                            } else {
                                commandSender.sendMessage( "§cDie Gruppe §e" + groupName + " exestiert nicht." );
                            }
                        } );
                    } );
                } else if ( args[1].equalsIgnoreCase( "remove" ) ) {
                    String groupName = args[2];
                    String permission = args[3];

                    this.groupService.groupExists( groupName ).whenComplete( ( exists, throwable ) -> {
                        JukeboxMC.getScheduler().execute( () -> {
                            if ( exists ) {
                                this.groupService.removePermissionFromGroup( groupName, permission );
                                commandSender.sendMessage( "§aDie Permission §e" + permission + " §awurde der Gruppe §e" + groupName + " §aentfernt." );
                            } else {
                                commandSender.sendMessage( "§cDie Gruppe §e" + groupName + " exestiert nicht." );
                            }
                        } );
                    } );
                }
            } else if ( args[0].equalsIgnoreCase( "permission" ) ) {
                if ( args[1].equalsIgnoreCase( "add" ) ) {
                    String player = args[2];
                    String permission = args[3];
                    UUID uuid;
                    if ( this.plugin.isValidUUID( player ) ) {
                        uuid = UUID.fromString( player );
                    } else {
                        if ( JukeboxMC.getPlayer( player ) != null ) {
                            uuid = JukeboxMC.getPlayer( player ).getUUID();
                        } else {
                            commandSender.sendMessage( "§cEs konnte keine UUID vom Spieler §e" + player + " §cgefunden werden." );
                            return;
                        }
                    }
                    this.playerPermissionService.playerHasPermission( uuid, permission ).whenComplete( ( hasPermission, throwable ) -> {
                        JukeboxMC.getScheduler().execute( () -> {
                            if ( !hasPermission ) {
                                this.playerPermissionService.addPlayerPermission( uuid, permission );
                                commandSender.sendMessage( "§aDie Permission §e" + permission + " §awurde dem Spieler §e" + player + " §ahinzugefügt." );
                            } else {
                                commandSender.sendMessage( "§cDer Spieler §e" + player + " §chat bereits die Permission §e" + permission + "§c." );
                            }
                        } );
                    } );
                } else if ( args[1].equalsIgnoreCase( "delete" ) ) {
                    String player = args[2];
                    String permission = args[3];
                    UUID uuid;
                    if ( this.plugin.isValidUUID( player ) ) {
                        uuid = UUID.fromString( player );
                    } else {
                        Player target = JukeboxMC.getPlayer( player );
                        if ( target != null ) {
                            uuid = target.getUUID();
                        } else {
                            commandSender.sendMessage( "§cEs konnte keine UUID vom Spieler §e" + player + " §cgefunden werden." );
                            return;
                        }
                    }
                    this.playerPermissionService.playerHasPermission( uuid, permission ).whenComplete( ( hasPermission, throwable ) -> {
                        JukeboxMC.getScheduler().execute( () -> {
                            if ( hasPermission ) {
                                this.playerPermissionService.removePlayerPermission( uuid, permission );
                                commandSender.sendMessage( "§aDie Permission §e" + permission + " §awurde dem Spieler §e" + player + " §ahinzugefügt." );
                            } else {
                                commandSender.sendMessage( "§cDer Spieler §e" + player + " §cbesitzt die Permission §e" + permission + "§cnicht." );
                            }
                        } );
                    } );
                }
            }
        } else if ( args.length == 5 ) {
            if ( args[0].equalsIgnoreCase( "group" ) ) {
                if ( args[1].equalsIgnoreCase( "user" ) ) {
                    if ( args[2].equalsIgnoreCase( "add" ) ) {
                        String player = args[3];
                        String groupName = args[4];

                        this.groupService.groupExists( groupName ).whenComplete( ( exists, throwable ) -> {
                            if ( exists ) {
                                UUID uuid;
                                if ( this.plugin.isValidUUID( player ) ) {
                                    uuid = UUID.fromString( player );
                                } else {
                                    Player target = JukeboxMC.getPlayer( player );
                                    if ( target != null ) {
                                        uuid = target.getUUID();
                                    } else {
                                        commandSender.sendMessage( "§cEs konnte keine UUID vom Spieler §e" + player + " §cgefunden werden." );
                                        return;
                                    }
                                }
                                boolean playerHasGroup = this.playerGroupService.playerHasGroup( uuid, groupName ).join();
                                if ( !playerHasGroup ) {
                                    this.playerGroupService.addPlayerGroup( uuid, groupName );
                                    commandSender.sendMessage( "§aDer Spieler §e" + player + " §awurde der Gruppe §e" + groupName + " §ahinzugefügt." );
                                } else {
                                    commandSender.sendMessage( "§cDer Spieler §e" + player + " §cist bereits in der Gruppe §e" + groupName + "§c." );
                                }
                            } else {
                                commandSender.sendMessage( "§cDie Gruppe §e" + groupName + " exestiert nicht." );
                            }
                        } );
                    } else if ( args[2].equalsIgnoreCase( "remove" ) ) {
                        String player = args[3];
                        String groupName = args[4];

                        this.groupService.groupExists( groupName ).whenComplete( ( exists, throwable ) -> {
                            if ( exists ) {
                                UUID uuid;
                                if ( this.plugin.isValidUUID( player ) ) {
                                    uuid = UUID.fromString( player );
                                } else {
                                    Player target = JukeboxMC.getPlayer( player );
                                    if ( target != null ) {
                                        uuid = target.getUUID();
                                    } else {
                                        commandSender.sendMessage( "§cEs konnte keine UUID vom Spieler §e" + player + " §cgefunden werden." );
                                        return;
                                    }
                                }
                                boolean playerHasGroup = this.playerGroupService.playerHasGroup( uuid, groupName ).join();
                                if ( playerHasGroup ) {
                                    this.playerGroupService.removePlayerGroup( uuid, groupName );
                                    commandSender.sendMessage( "§aDer Spieler §e" + player + " §awurde aus Gruppe §e" + groupName + " §aentfernt." );
                                } else {
                                    commandSender.sendMessage( "§cDer Spieler §e" + player + " §cist nicht in der Gruppe §e" + groupName + "§c." );
                                }
                            } else {
                                commandSender.sendMessage( "§cDie Gruppe §e" + groupName + " exestiert nicht." );
                            }
                        } );
                    }
                }
            }
        }
    }
}
