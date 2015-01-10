package cz.projectsurvive.limeth.hitboxbind;

import com.google.common.collect.Maps;
import cz.projectsurvive.limeth.hitboxbind.util.Binding;
import cz.projectsurvive.limeth.hitboxbind.util.ReadOnlyBinding;
import de.howaner.FramePicture.FrameManager;
import de.howaner.FramePicture.FramePicturePlugin;
import de.howaner.FramePicture.util.Frame;
import net.minecraft.util.org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Limeth
 */
public class HitboxService
{
	private static final int                            LOOP_PERIOD_TICKS = 20 * 60;
	private final HashMap<String, Binding<HitboxMedia>> dataMap           = Maps.newHashMap();
	private final Object                                LOCK              = new Object();

	public ReadOnlyBinding<HitboxMedia> registerMedia(String name)
	{
		ReadOnlyBinding<HitboxMedia> media = getMedia(name);

		if(media != null)
			return media;

		Binding<HitboxMedia> binding = Binding.of(new HitboxMedia(name));
		MediaUpdateRunnable updateRunnable = new MediaUpdateRunnable(name, binding);

		synchronized(LOCK)
		{
			dataMap.put(name, binding);
		}

		updateRunnable.runTaskAsynchronously(HitboxBind.getInstance());

		return binding;
	}

	public ReadOnlyBinding<HitboxMedia> getMedia(String name)
	{
		synchronized(LOCK)
		{
			return dataMap.containsKey(name) ? dataMap.get(name).readOnly() : null;
		}
	}

	public HitboxService startLoop()
	{
		BukkitScheduler scheduler = Bukkit.getScheduler();

		scheduler.scheduleSyncRepeatingTask(HitboxBind.getInstance(), this::loopTickRunnable, 0, LOOP_PERIOD_TICKS);

		return this;
	}

	private void loopTickRunnable()
	{
		new AsyncLoopTickRunnable().runTaskAsynchronously(HitboxBind.getInstance());
	}

	private class AsyncLoopTickRunnable extends BukkitRunnable
	{
		@Override
		public void run()
		{
			synchronized(LOCK)
			{
				dataMap.entrySet().forEach(HitboxService::reloadMedia);
			}
		}
	}

	private class MediaUpdateRunnable extends BukkitRunnable
	{
		private final String               mediaName;
		private final Binding<HitboxMedia> mediaBinding;

		public MediaUpdateRunnable(String mediaName, Binding<HitboxMedia> mediaBinding)
		{
			this.mediaName = mediaName;
			this.mediaBinding = mediaBinding;
		}

		@Override
		public void run()
		{
			reloadMedia(Pair.of(mediaName, mediaBinding));
		}
	}

	private static void reloadMedia(Map.Entry<String, Binding<HitboxMedia>> entry)
	{
		try
		{
			entry.getValue().set(HitboxMedia.load(entry.getKey()));
			Bukkit.getScheduler().runTask(HitboxBind.getInstance(), () -> refreshItemFrames(entry.getKey()));
		}
		catch(IOException e)
		{
			HitboxBind.warn("An error occurred while updating media '" + entry.getKey() + "'.");
			e.printStackTrace();
		}
	}

	private static void refreshItemFrames(String mediaName)
	{
		HitboxBind.getFramesWithMedia(mediaName).stream().map(Frame.class::cast).forEach(HitboxService::refreshFrame);
	}

	private static void refreshFrame(Frame frame)
	{
		FrameManager manager = FramePicturePlugin.getManager();

		frame.clearCache();
		manager.sendFrame(frame);
	}
}
