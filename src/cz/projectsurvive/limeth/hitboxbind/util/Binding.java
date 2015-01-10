package cz.projectsurvive.limeth.hitboxbind.util;

import com.google.common.base.Preconditions;

/**
 * @author Limeth
 */
public interface Binding<T> extends ReadOnlyBinding<T>
{
	static <T> Binding<T> of(T value)
	{
		return new BindingImpl<>(value);
	}

	static <T> Binding<T> empty()
	{
		return of(null);
	}

	static <T> ReadOnlyBinding<T> readOnlyOf(T value)
	{
		return new ReadOnlyBindingImpl<>(value);
	}

	static <T> ReadOnlyBinding<T> readOnlyEmpty()
	{
		return readOnlyOf(null);
	}

	static <T> ReadOnlyBinding<T> readOnly(Binding<T> binding)
	{
		Preconditions.checkNotNull(binding);

		return new ReferencedReadOnlyBinding<>(binding);
	}

	void set(T value);
	ReadOnlyBinding<T> readOnly();
}
