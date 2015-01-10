package cz.projectsurvive.limeth.hitboxbind.util;

/**
 * @author Limeth
 */
public final class BindingImpl<T> implements Binding<T>
{
	private T value;

	BindingImpl(T value)
	{
		this.value = value;
	}

	BindingImpl() {}

	@Override
	public T get()
	{
		return value;
	}

	@Override
	public void set(T value)
	{
		this.value = value;
	}

	@Override
	public ReadOnlyBinding<T> readOnly()
	{
		return Binding.readOnly(this);
	}
}
