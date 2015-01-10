package cz.projectsurvive.limeth.hitboxbind.actions;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;

/**
 * @author Limeth
 */
public interface FrameAction
{
	void onRightClick(Player player, ItemFrame itemFrame);
}
