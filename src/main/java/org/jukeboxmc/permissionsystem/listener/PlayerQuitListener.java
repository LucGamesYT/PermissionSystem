package org.jukeboxmc.permissionsystem.listener;

import org.jukeboxmc.event.EventHandler;
import org.jukeboxmc.event.Listener;
import org.jukeboxmc.event.player.PlayerQuitEvent;
import org.jukeboxmc.permissionsystem.PermissionSystem;
import org.jukeboxmc.player.Player;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class PlayerQuitListener implements Listener {

    private final PermissionSystem plugin;

    public PlayerQuitListener( PermissionSystem plugin ) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();

        this.plugin.getPlayerGroupService().getPlayerGroupList().remove( player.getUUID() );
        this.plugin.getPlayerPermissionService().getPlayerPermissionList().remove( player.getUUID() );
    }

}
