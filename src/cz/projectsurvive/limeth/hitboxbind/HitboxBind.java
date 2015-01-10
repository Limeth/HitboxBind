package cz.projectsurvive.limeth.hitboxbind;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cz.projectsurvive.limeth.hitboxbind.actions.FrameAction;
import cz.projectsurvive.limeth.hitboxbind.commands.HitboxBindCommandExecutor;
import cz.projectsurvive.limeth.hitboxbind.frames.AvatarFrame;
import cz.projectsurvive.limeth.hitboxbind.frames.HitboxFrame;
import cz.projectsurvive.limeth.hitboxbind.frames.StateFrame;
import cz.projectsurvive.limeth.hitboxbind.listeners.InteractionListener;
import cz.projectsurvive.limeth.hitboxbind.listeners.PluginListener;
import de.howaner.FramePicture.FrameManager;
import de.howaner.FramePicture.FramePicturePlugin;
import de.howaner.FramePicture.util.Frame;
import de.howaner.FramePicture.util.Utils;
import org.bukkit.*;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Limeth
 */
public class HitboxBind extends JavaPlugin
{
	private static final String FRAMEPICTURE_NAME = "FramePicture-1.8";
	private static final String COMMAND_NAME      = "hitboxbind";
	private static HitboxBind                              instance;
	private static FramePicturePlugin                      framePicturePlugin;
	private static HitboxService                           hitboxService;
	private static Map<Name, Class<? extends HitboxFrame>> frameTypes;
	private static Map<String, FrameAction> frameActions = Maps.newHashMap();

	@Override
	public void onEnable()
	{
		instance = this;
		setupFrameTypes();
		setupHitboxService();
		registerListeners();
		registerCommands();
		hookFramePicture();
		info("Enabled.");
	}

	@Override
	public void onDisable()
	{
		info("Disabled.");
		resetStaticValues();
	}

	private static void setupFrameTypes()
	{
		frameTypes = Maps.newHashMap();

		frameTypes.put(new Name("State"), StateFrame.class);
		frameTypes.put(new Name("Avatar"), AvatarFrame.class);
	}

	public static Class<? extends HitboxFrame> getFrameType(Name name)
	{
		return frameTypes.get(name);
	}

	public static Class<? extends HitboxFrame> registerFrameType(Name name, Class<? extends HitboxFrame> frameClass)
	{
		Preconditions.checkNotNull(frameClass);

		return frameTypes.put(name, frameClass);
	}

	public static Class<? extends HitboxFrame> unregisterFrameType(Name name)
	{
		return frameTypes.remove(name);
	}

	public static FrameAction getFrameAction(OfflinePlayer player)
	{
		return frameActions.get(player.getName());
	}

	public static FrameAction setFrameAction(OfflinePlayer player, FrameAction frameClass)
	{
		Preconditions.checkNotNull(frameClass);

		return frameActions.put(player.getName(), frameClass);
	}

	public static FrameAction removeFrameAction(OfflinePlayer player)
	{
		return frameActions.remove(player.getName());
	}

	private static void setupHitboxService()
	{
		hitboxService = new HitboxService().startLoop();
	}

	private void registerListeners()
	{
		PluginManager pm = Bukkit.getPluginManager();

		pm.registerEvents(new PluginListener(), this);
		pm.registerEvents(new InteractionListener(), this);
	}

	private void registerCommands()
	{
		Bukkit.getPluginCommand(COMMAND_NAME).setExecutor(new HitboxBindCommandExecutor());
	}

	@SuppressWarnings("unchecked")
	public static void hookFramePicture()
	{
		info("Hooking into FramePicture...");

		PluginManager pm = Bukkit.getPluginManager();

		try
		{
			framePicturePlugin = (FramePicturePlugin) pm.getPlugin(FRAMEPICTURE_NAME);

			if(framePicturePlugin == null)
				throw new NullPointerException();
		}
		catch(ClassCastException e)
		{
			warn("Could not find plugin FramePicture.");
			pm.disablePlugin(instance);
			return;
		}
		catch(NullPointerException e)
		{
			warn("Could not hook into FramePicture, plugin not found.");
			pm.disablePlugin(instance);
			return;
		}

		FrameManager frameManager = FramePicturePlugin.getManager();
		Map<String, List<Frame>> frames;

		try
		{
			Field framesField = FrameManager.class.getDeclaredField("frames");

			if(!framesField.isAccessible())
				framesField.setAccessible(true);

			frames = (Map<String, List<Frame>>) framesField.get(frameManager);
		}
		catch(ClassCastException | NoSuchFieldException | IllegalAccessException e)
		{
			warn("Could not inject FrameManager for the 'frames' field.");
			e.printStackTrace();
			pm.disablePlugin(instance);
			return;
		}

		for(List<Frame> worldChunkFrames : frames.values())
		{
			Iterator<Frame> worldChunkFrameIterator = worldChunkFrames.iterator();
			List<HitboxFrame> replacementWorldChunkFrames = Lists.newArrayList();

			while(worldChunkFrameIterator.hasNext())
			{
				Frame frame = worldChunkFrameIterator.next();

				if(HitboxFrame.isReplaceable(frame))
				{
					HitboxFrame hitboxFrame = HitboxFrame.instanceOf(frame, hitboxService::registerMedia);

					hitboxFrame.setEntity(frame.getEntity());
					frameManager.sendFrame(frame);
					replacementWorldChunkFrames.add(hitboxFrame);
					worldChunkFrameIterator.remove();
				}
			}

			worldChunkFrames.addAll(replacementWorldChunkFrames);
		}

		info("Hooked into FramePicture.");
	}

	public static void addFrame(HitboxFrame frame)
	{
		ItemFrame entity = frame.getEntity();

		Preconditions.checkNotNull(entity, "The HitboxFrame's item frame entity must not be null!");

		FrameManager manager = FramePicturePlugin.getManager();
		Chunk chunk = entity.getLocation().getChunk();
		List<Frame> frameList = manager.getFramesInChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());

		frameList.add(frame);
		manager.setFramesInChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ(), frameList);
		Utils.setFrameItemWithoutSending(entity, new ItemStack(Material.AIR));
		manager.sendFrame(frame);
		manager.saveFrames();
	}

	public static List<? super HitboxFrame> getFramesWithMedia(String mediaName)
	{
		return FramePicturePlugin.getManager().getFrames().stream().filter(
				frame -> frame instanceof HitboxFrame && ((HitboxFrame) frame).getMedia().getUsername().equals(mediaName)
		       ).map(HitboxFrame.class::cast).collect(Collectors.toList());
	}

	private static void resetStaticValues()
	{
		instance = null;
		framePicturePlugin = null;
		hitboxService = null;
		frameTypes = null;
	}

	private static void sendConsoleMessage(String string)
	{
		Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "[HitboxBind] " + string);
	}

	public static void info(String string)
	{
		sendConsoleMessage(ChatColor.WHITE + "[INFO] " + string);
	}

	public static void warn(String string)
	{
		sendConsoleMessage(ChatColor.RED + "[WARN] " + string);
	}

	public static HitboxBind getInstance()
	{
		return instance;
	}

	public static FramePicturePlugin getFramePicturePlugin()
	{
		return framePicturePlugin;
	}

	public static HitboxService getHitboxService()
	{
		return hitboxService;
	}

	public static Map<Name, Class<? extends HitboxFrame>> getFrameTypes()
	{
		return ImmutableSortedMap.copyOf(frameTypes);
	}

	public static Map<String, FrameAction> getFrameActions()
	{
		return ImmutableSortedMap.copyOf(frameActions);
	}
}
