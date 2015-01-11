package cz.projectsurvive.limeth.hitboxbind.commands;

import com.google.common.base.Optional;
import cz.projectsurvive.limeth.hitboxbind.HitboxBind;
import cz.projectsurvive.limeth.hitboxbind.Name;
import cz.projectsurvive.limeth.hitboxbind.actions.CreateAction;
import cz.projectsurvive.limeth.hitboxbind.actions.FrameAction;
import cz.projectsurvive.limeth.hitboxbind.frames.HitboxFrame;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Limeth
 */
public class HitboxBindCommandExecutor implements CommandExecutor
{
	public static final String PERMISSION_TYPES = "HitboxBind.command.types";
	public static final String PERMISSION_NEW_PREFIX = "HitboxBind.command.new.";

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandAlias, String[] args)
	{
		if(args.length <= 0)
		{
			sendCommandInfo(sender, ChatColor.GREEN, commandAlias, "types", "Lists all available map types.");
			sendCommandInfo(sender, ChatColor.GREEN, commandAlias, "new [Type] [Media]", "Creates a new map view.");
		}
		else if(args[0].equalsIgnoreCase("types"))
		{
			if(permissionBlock(sender, PERMISSION_TYPES))
				return true;

			StringBuilder message = new StringBuilder().append(ChatColor.GRAY).append("Available map types:")
			                                           .append('\n').append(ChatColor.BOLD);
			boolean first = true;

			for(Name name : HitboxBind.getFrameTypes().keySet())
			{
				if(first)
					first = false;
				else
					message.append(", ");

				message.append(name);
			}

			sender.sendMessage(message.toString());
		}
		else if(args[0].equalsIgnoreCase("new"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "Sorry, console. Players only.");
				return true;
			}

			if(args.length < 3)
			{
				sendCommandInfo(sender, ChatColor.RED, commandAlias, "new [Type] [Media]", "Creates a new map view.");
				return true;
			}

			String rawType = args[1];
			Optional<Class<? extends HitboxFrame>> frameClass = HitboxBind.getFrameType(new Name(rawType));

			if(!frameClass.isPresent())
			{
				sender.sendMessage(ChatColor.RED + "Unknown map type '" + ChatColor.YELLOW + rawType + ChatColor.RED + "'.");
				return true;
			}

			if(permissionBlock(sender, PERMISSION_NEW_PREFIX + rawType.toLowerCase()))
				return true;

			Name media = new Name(args[2]);
			Player player = (Player) sender;
			FrameAction action = new CreateAction(media, frameClass.get());

			HitboxBind.setFrameAction(player, action);
			sender.sendMessage(ChatColor.GREEN + "Right-click an item frame to create a new map view.");
		}

		return true;
	}

	private boolean permissionBlock(CommandSender sender, String permission)
	{
		if(!sender.hasPermission(permission))
		{
			sender.sendMessage(ChatColor.RED + "Insufficient permission. (" + permission + ")");
			return true;
		}

		return false;
	}

	private String getCommandInfo(ChatColor color, String commandAlias, String arguments, String description)
	{
		return color.toString() + '/' + commandAlias + " " + arguments + ChatColor.GRAY + " - " + description;
	}

	private void sendCommandInfo(CommandSender sender, ChatColor color, String commandAlias, String arguments, String description)
	{
		sender.sendMessage(getCommandInfo(color, commandAlias, arguments, description));
	}
}
