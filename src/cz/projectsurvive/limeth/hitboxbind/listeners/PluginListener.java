package cz.projectsurvive.limeth.hitboxbind.listeners;

import cz.projectsurvive.limeth.hitboxbind.HitboxBind;
import de.howaner.FramePicture.FramePicturePlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

/**
 * @author Limeth
 */
public class PluginListener implements Listener
{
	@EventHandler
	public void onPluginEnable(PluginEnableEvent event)
	{
		Plugin plugin = event.getPlugin();

		if(!(plugin instanceof FramePicturePlugin))
			return;

		HitboxBind.hookFramePicture();
	}
}
