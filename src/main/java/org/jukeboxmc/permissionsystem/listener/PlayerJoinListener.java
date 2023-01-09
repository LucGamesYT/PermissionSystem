package org.jukeboxmc.permissionsystem.listener;

import org.jukeboxmc.JukeboxMC;
import org.jukeboxmc.event.EventHandler;
import org.jukeboxmc.event.EventPriority;
import org.jukeboxmc.event.Listener;
import org.jukeboxmc.event.player.PlayerJoinEvent;
import org.jukeboxmc.permissionsystem.PermissionSystem;
import org.jukeboxmc.permissionsystem.service.GroupService;
import org.jukeboxmc.permissionsystem.service.PlayerGroupService;
import org.jukeboxmc.permissionsystem.service.PlayerPermissionService;
import org.jukeboxmc.player.Player;

import java.util.Set;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class PlayerJoinListener implements Listener {

    private final GroupService groupService;
    private final PlayerGroupService playerGroupService;
    private final PlayerPermissionService playerPermissionService;

    public PlayerJoinListener( PermissionSystem plugin ) {
        this.groupService = plugin.getGroupService();
        this.playerGroupService = plugin.getPlayerGroupService();
        this.playerPermissionService = plugin.getPlayerPermissionService();
    }

    @EventHandler ( priority = EventPriority.MONITOR )
    public void onPlayerJoin( PlayerJoinEvent event ) {
        Player player = event.getPlayer();

        this.playerGroupService.getGroupsByUUID( player.getUUID() ).whenComplete( ( groups, throwable ) -> {
            if ( !groups.isEmpty() ) {
                for ( String group : groups ) {
                    Set<String> permissions = this.groupService.getPermissionsByGroup( group ).join();
                    JukeboxMC.getScheduler().execute( () -> {
                        player.addPermissions( permissions );
                    } );
                }
            }
            Set<String> permissions = this.playerPermissionService.getPermissionsByUUID( player.getUUID() ).join();
            if ( !permissions.isEmpty() ) {
                JukeboxMC.getScheduler().execute( () -> {
                    player.addPermissions( permissions );
                } );
            }
        } );
    }
}
