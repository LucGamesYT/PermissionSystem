package org.jukeboxmc.permissionsystem.command;

import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;
import org.jukeboxmc.JukeboxMC;
import org.jukeboxmc.command.Command;
import org.jukeboxmc.command.CommandData;
import org.jukeboxmc.command.CommandParameter;
import org.jukeboxmc.command.CommandSender;
import org.jukeboxmc.command.annotation.Description;
import org.jukeboxmc.command.annotation.Name;
import org.jukeboxmc.command.annotation.Permission;
import org.jukeboxmc.permissionsystem.PermissionSystem;
import org.jukeboxmc.permissionsystem.service.GroupService;
import org.jukeboxmc.permissionsystem.service.PlayerGroupService;
import org.jukeboxmc.permissionsystem.service.PlayerPermissionService;
import org.jukeboxmc.player.Player;

import java.util.List;
import java.util.UUID;

/**
 * @author LucGamesYT
 * @version 1.0
 */
@Name ( "permissionsystem" )
@Description ( "This is a command to handle permissions and groups." )
@Permission ( "permissionsystem.command.execute" )
public class PermissionCommand extends Command {

    private final PermissionSystem plugin;
    private final GroupService groupService;
    private final PlayerGroupService playerGroupService;
    private final PlayerPermissionService playerPermissionService;

    public PermissionCommand( PermissionSystem plugin ) {
        super( CommandData.builder()
                .setAliases( "perms" )
                .setParameters( new CommandParameter[]{
                                new CommandParameter( "group", List.of( "group" ), false ),
                                new CommandParameter( "create", List.of( "create" ), false ),
                                new CommandParameter( "name", CommandParamType.TEXT, false )
                        },
                        new CommandParameter[]{
                                new CommandParameter( "group", List.of( "group" ), false ),
                                new CommandParameter( "delete", List.of( "delete" ), false ),
                                new CommandParameter( "name", CommandParamType.TEXT, false )
                        },
                        new CommandParameter[]{
                                new CommandParameter( "group", List.of( "group" ), false ),
                                new CommandParameter( "user", List.of( "user" ), false ),
                                new CommandParameter( "add", List.of( "add" ), false ),
                                new CommandParameter( "player", CommandParamType.TARGET, false ),
                                new CommandParameter( "group", CommandParamType.STRING, false ),
                        },
                        new CommandParameter[]{
                                new CommandParameter( "group", List.of( "group" ), false ),
                                new CommandParameter( "user", List.of( "user" ), false ),
                                new CommandParameter( "remove", List.of( "remove" ), false ),
                                new CommandParameter( "player", CommandParamType.TARGET, false ),
                                new CommandParameter( "group", CommandParamType.STRING, false ),
                        },
                        new CommandParameter[]{
                                new CommandParameter( "group", List.of( "group" ), false ),
                                new CommandParameter( "add", List.of( "add" ), false ),
                                new CommandParameter( "group", CommandParamType.TEXT, false ),
                                new CommandParameter( "permission", CommandParamType.TEXT, false ),
                        },
                        new CommandParameter[]{
                                new CommandParameter( "group", List.of( "group" ), false ),
                                new CommandParameter( "delete", List.of( "delete" ), false ),
                                new CommandParameter( "group", CommandParamType.TEXT, false ),
                                new CommandParameter( "permission", CommandParamType.TEXT, false ),
                        },
                        new CommandParameter[]{
                                new CommandParameter( "permission", List.of( "permission" ), false ),
                                new CommandParameter( "add", List.of( "add" ), false ),
                                new CommandParameter( "player", CommandParamType.TARGET, false ),
                                new CommandParameter( "permission", CommandParamType.TEXT, false ),
                        },
                        new CommandParameter[]{
                                new CommandParameter( "permission", List.of( "permission" ), false ),
                                new CommandParameter( "remove", List.of( "remove" ), false ),
                                new CommandParameter( "player", CommandParamType.TARGET, false ),
                                new CommandParameter( "permission", CommandParamType.TEXT, false ),
                        }
                ).build() );
        this.plugin = plugin;
        this.groupService = plugin.getGroupService();
        this.playerGroupService = plugin.getPlayerGroupService();
        this.playerPermissionService = plugin.getPlayerPermissionService();
    }

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
                                commandSender.sendMessage( "§aThe group §e" + groupName + " §awas created." );
                            } else {
                                commandSender.sendMessage( "§cThe group §e" + groupName + " §calready exists." );
                            }
                        } );
                    } );
                } else if ( args[1].equalsIgnoreCase( "delete" ) ) {
                    String groupName = args[2];
                    this.groupService.groupExists( groupName ).whenComplete( ( exists, throwable ) -> {
                        JukeboxMC.getScheduler().execute( () -> {
                            if ( exists ) {
                                this.groupService.deleteGroup( groupName );
                                commandSender.sendMessage( "§aThe group §e" + groupName + " §awas deleted." );
                            } else {
                                commandSender.sendMessage( "§cThe group §e" + groupName + " §cdoes not exist." );
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
                                commandSender.sendMessage( "§aThe permission §e" + permission + " §ahas been added to the group §e" + groupName );
                            } else {
                                commandSender.sendMessage( "§cThe group §e" + groupName + " §cdoes not exist." );
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
                                commandSender.sendMessage( "§aThe permission §e" + permission + " §awas removed from the group §e" + groupName );
                            } else {
                                commandSender.sendMessage( "§cThe group §e" + groupName + " §cdoes not exist." );
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
                            commandSender.sendMessage( "§cNo UUID could be found from the player §e" + player );
                            return;
                        }
                    }
                    this.playerPermissionService.playerHasPermission( uuid, permission ).whenComplete( ( hasPermission, throwable ) -> {
                        JukeboxMC.getScheduler().execute( () -> {
                            if ( !hasPermission ) {
                                this.playerPermissionService.addPlayerPermission( uuid, permission );
                                commandSender.sendMessage( "§aThe permission §e" + permission + " §has been added to the player §e" + player );
                            } else {
                                commandSender.sendMessage( "§cThe player §e" + player + " §chas already the permission §e" + permission );
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
                            commandSender.sendMessage( "§cNo UUID could be found from the player §e" + player );
                            return;
                        }
                    }
                    this.playerPermissionService.playerHasPermission( uuid, permission ).whenComplete( ( hasPermission, throwable ) -> {
                        JukeboxMC.getScheduler().execute( () -> {
                            if ( hasPermission ) {
                                this.playerPermissionService.removePlayerPermission( uuid, permission );
                                commandSender.sendMessage( "§aThe permission §e" + permission + " §has been added to the player §e" + player );
                            } else {
                                commandSender.sendMessage( "§cThe player §e" + player + " §chas not the permission §e" + permission );
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
                                        commandSender.sendMessage( "§cNo UUID could be found from the player §e" + player );
                                        return;
                                    }
                                }
                                boolean playerHasGroup = this.playerGroupService.playerHasGroup( uuid, groupName ).join();
                                if ( !playerHasGroup ) {
                                    this.playerGroupService.addPlayerGroup( uuid, groupName );
                                    commandSender.sendMessage( "§aThe player §e" + player + " §has been added to the group §e" + groupName );
                                } else {
                                    commandSender.sendMessage( "§cThe player §e" + player + " §cis already in the group §e" + groupName );
                                }
                            } else {
                                commandSender.sendMessage( "§cThe group §e" + groupName + " §cdoes not exist." );
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
                                        commandSender.sendMessage( "§cNo UUID could be found from the player §e" + player );
                                        return;
                                    }
                                }
                                boolean playerHasGroup = this.playerGroupService.playerHasGroup( uuid, groupName ).join();
                                if ( playerHasGroup ) {
                                    this.playerGroupService.removePlayerGroup( uuid, groupName );
                                    commandSender.sendMessage( "§aThe player §e" + player + " §has been removed from group §e" + groupName );
                                } else {
                                    commandSender.sendMessage( "§cThe player §e" + player + " §cis not in the group §e" + groupName );
                                }
                            } else {
                                commandSender.sendMessage( "§cThe group §e" + groupName + " §calready exists." );
                            }
                        } );
                    }
                }
            }
        }
    }
}
