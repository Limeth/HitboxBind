package cz.projectsurvive.limeth.hitboxbind;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Limeth
 */
public class HitboxMedia
{
	private static final String MEDIA_USER_NAME = "media_user_name";
	private static final String MEDIA_IS_LIVE = "media_is_live";
	private static final String MEDIA_DISPLAY_NAME = "media_display_name";
	private static final String MEDIA_STATUS = "media_status";
	private static final String MEDIA_VIEWS = "media_views";
	private static final String MEDIA_THUMBNAIL_SMALL = "media_thumbnail";
	private static final String MEDIA_THUMBNAIL_LARGE = "media_thumbnail_large";
	private static final String CHANNEL = "channel";
	private static final String CHANNEL_FOLLOWERS = "followers";
	private static final String CHANNEL_LOGO_LARGE = "user_logo";
	private static final String CHANNEL_LOGO_SMALL = "user_logo_small";
	private static final String CHANNEL_COVER = "user_cover";
	private static final String CHANNEL_LINK = "channel_link";
	private static final String HOSTNAME_STATIC = "http://edge.sf.hitbox.tv/";
	private JsonObject root;
	private Name username;
	private boolean exists;

	private HitboxMedia(JsonObject root)
	{
		this.root = root;
		this.exists = true;
	}

	private HitboxMedia(Name username, boolean exists)
	{
		this.username = username;
		this.exists = exists;
	}

	HitboxMedia(Name username)
	{
		this(username, true);
	}

	public static HitboxMedia load(Name username)
	{
		try
		{
			URL url = new URL("http://api.hitbox.tv/media/live/" + username);
			URLConnection connection = url.openConnection();
			JsonParser parser = new JsonParser();
			JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream()));
			JsonObject root = parser.parse(reader).getAsJsonObject();

			return new HitboxMedia(root.get("livestream").getAsJsonArray().iterator().next().getAsJsonObject());
		}
		catch(Exception e)
		{
			HitboxBind.warn("Could not load media '" + username + "': " + e.getLocalizedMessage());
			return new HitboxMedia(username, false);
		}
	}

	public Name getUsername()
	{
		return root != null ? new Name(root.get(MEDIA_USER_NAME).getAsString()) : username;
	}

	public boolean exists()
	{
		return exists;
	}

	public String getDisplayName()
	{
		return root.get(MEDIA_DISPLAY_NAME).getAsString();
	}

	public boolean isLive()
	{
		return root.get(MEDIA_IS_LIVE).getAsString().equals("1");
	}

	public String getStatus()
	{
		return root.get(MEDIA_STATUS).getAsString();
	}

	public int getViews()
	{
		return root.get(MEDIA_VIEWS).getAsInt();
	}

	public int getFollowerCount()
	{
		return root.get(CHANNEL).getAsJsonObject().get(CHANNEL_FOLLOWERS).getAsInt();
	}

	public URL getThumbnailSmall()
	{
		try
		{
			return new URL(HOSTNAME_STATIC + root.get(MEDIA_THUMBNAIL_SMALL).getAsString());
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public URL getThumbnailLarge()
	{
		try
		{
			return new URL(HOSTNAME_STATIC + root.get(MEDIA_THUMBNAIL_LARGE).getAsString());
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public URL getLogoLarge()
	{
		try
		{
			return new URL(HOSTNAME_STATIC + root.get(CHANNEL).getAsJsonObject().get(CHANNEL_LOGO_LARGE).getAsString());
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public URL getLogoSmall()
	{
		try
		{
			return new URL(HOSTNAME_STATIC + root.get(CHANNEL).getAsJsonObject().get(CHANNEL_LOGO_SMALL).getAsString());
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public URL getCover()
	{
		try
		{
			return new URL(HOSTNAME_STATIC + root.get(CHANNEL).getAsJsonObject().get(CHANNEL_COVER).getAsString());
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public URL getLink()
	{
		try
		{
			return new URL(root.get(CHANNEL).getAsJsonObject().get(CHANNEL_LINK).getAsString());
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public boolean isLoaded()
	{
		return root != null && exists;
	}
}
