package cz.projectsurvive.limeth.hitboxbind.util;

/**
 * @author Limeth
 */
public final class ReadOnlyBindingImpl<T> implements ReadOnlyBinding<T>
{
	private T value;

	ReadOnlyBindingImpl(T value)
	{
		this.value = value;
	}

	@Override
	public T get()
	{
		return value;
	}
}
