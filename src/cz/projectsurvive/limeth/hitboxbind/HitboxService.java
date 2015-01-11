package cz.projectsurvive.limeth.hitboxbind;

import com.google.common.base.Preconditions;
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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Limeth
 */
public class HitboxService
{
	private static final int                            LOOP_PERIOD_TICKS = 20 * 60;
	private final HashMap<Name, Binding<HitboxMedia>>   dataMap           = Maps.newHashMap();
	private final Object                                LOCK              = new Object();

	public ReadOnlyBinding<HitboxMedia> registerMedia(Name name, boolean asyncUpdate)
	{
		Preconditions.checkNotNull(name, "The media name must not be null!");
		ReadOnlyBinding<HitboxMedia> media = getMedia(name);

		if(media != null)
			return media;

		Binding<HitboxMedia> binding = Binding.of(new HitboxMedia(name));

		synchronized(LOCK)
		{
			dataMap.put(name, binding);
		}

		if(asyncUpdate)
			updateMedia(name);

		return binding;
	}

	public ReadOnlyBinding<HitboxMedia> registerMediaAndUpdate(Name name)
	{
		Preconditions.checkNotNull(name, "The media name must not be null!");
		return registerMedia(name, true);
	}

	public ReadOnlyBinding<HitboxMedia> getMedia(Name name)
	{
		Preconditions.checkNotNull(name, "The media name must not be null!");
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

	public void updateMedia(Name name)
	{
		Preconditions.checkNotNull(name, "The media name must not be null!");
		Binding<HitboxMedia> binding;

		synchronized(LOCK)
		{
			binding = dataMap.get(name);
		}

		new MediaUpdateRunnable(name, binding).runTaskAsynchronously(HitboxBind.getInstance());
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
		private final Name                 mediaName;
		private final Binding<HitboxMedia> mediaBinding;

		public MediaUpdateRunnable(Name mediaName, Binding<HitboxMedia> mediaBinding)
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

	private static void reloadMedia(Map.Entry<Name, Binding<HitboxMedia>> entry)
	{
		entry.getValue().set(HitboxMedia.load(entry.getKey()));
		Bukkit.getScheduler().runTask(HitboxBind.getInstance(), () -> refreshItemFrames(entry.getKey()));
	}

	private static void refreshItemFrames(Name mediaName)
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
