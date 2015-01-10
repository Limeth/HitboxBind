package cz.projectsurvive.limeth.hitboxbind.util;

import com.google.common.base.Preconditions;

/**
 * @author Limeth
 */
public final class ReferencedReadOnlyBinding<T> implements ReadOnlyBinding<T>
{
	private final Binding<T> binding;

	ReferencedReadOnlyBinding(Binding<T> binding)
	{
		this.binding = binding;
	}

	@Override
	public T get()
	{
		return binding.get();
	}
}
