package cz.projectsurvive.limeth.hitboxbind;

import com.google.common.base.Preconditions;

import java.io.Serializable;

/**
 * @author Limeth
 */
public class Name implements Serializable, Comparable<Name>, CharSequence
{
	private final String value;

	public Name(String value)
	{
		Preconditions.checkNotNull(value);

		this.value = value;
	}

	public boolean isEmpty()
	{
		return value.isEmpty();
	}

	@Override
	public int length()
	{
		return value.length();
	}

	@Override
	public char charAt(int index)
	{
		return value.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end)
	{
		return value.subSequence(start, end);
	}

	@Override
	public int compareTo(Name o)
	{
		return value.compareTo(o.value);
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		Name name = (Name) o;

		return value.equalsIgnoreCase(name.value);

	}

	@Override
	public int hashCode()
	{
		return value.toLowerCase().hashCode();
	}

	@Override
	public String toString()
	{
		return value;
	}
}
