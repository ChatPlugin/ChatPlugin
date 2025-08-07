/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2025  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://remigio07.me/chatplugin>
 */

package me.remigio07.chatplugin.api.common.util;

/**
 * Represents a pseudo-enum.
 * 
 * <p>Classes from non-loaded implementations and generics cannot be used
 * in method declarations of ordinary {@link Enum}s. This class is used
 * by adapters and other modules to bypass this restriction.</p>
 * 
 * <p>The <code>valueOf({@link String})</code> and <code>values()</code> methods
 * must be declared by the subclass and behave like methods of an ordinary enum.
 * A helper <code>valueOf({@link String}, V[])</code> method is provided.</p>
 * 
 * @param <E> Enum's type
 */
public abstract class PseudoEnum<E extends PseudoEnum<E>> implements Comparable<E> {
	
	protected String name;
	protected int ordinal;
	
	protected PseudoEnum(String name, int ordinal) {
		this.name = name;
		this.ordinal = ordinal;
	}
	
	/**
	 * Equivalent of {@link Enum#compareTo(Enum)}.
	 */
	@Override
	public final int compareTo(E o) {
		PseudoEnum<?> other = (PseudoEnum<?>) o;
		
		if (getClass() != other.getClass() && getDeclaringClass() != other.getDeclaringClass())
			throw new ClassCastException();
		return ordinal - other.ordinal;
	}
	
	/**
	 * Equivalent of {@link Enum#hashCode()}.
	 * 
	 * @return Constant's hash code
	 */
	@Override
	public final int hashCode() {
		return super.hashCode();
	}
	
	/**
	 * Equivalent of {@link Enum#equals(Object)}.
	 * 
	 * @return Whether the two constants are equal
	 */
	@Override
	public final boolean equals(Object obj) {
		return this == obj;
	}
	
	/**
	 * Equivalent of {@link Enum#toString()}.
	 * 
	 * @return Constant's name
	 */
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Throws {@link CloneNotSupportedException}.
	 * 
	 * <p>This guarantees that pseudo-enums are never cloned,
	 * which is necessary to preserve their "singleton" status.</p>
	 * 
	 * @return Nothing (never returns)
	 */
	@Override
	protected final Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	/**
	 * Equivalent of {@link Enum#name()}.
	 * 
	 * @return Constant's name
	 */
	public final String name() {
		return name;
	}
	
	/**
	 * Equivalent of {@link Enum#ordinal()}.
	 * 
	 * @return Constant's ordinal
	 */
	public final int ordinal() {
		return ordinal;
	}
	
	/**
	 * Equivalent of {@link Enum#getDeclaringClass()}.
	 * 
	 * @return Constant's declaring class
	 */
	@SuppressWarnings("unchecked")
	public final Class<E> getDeclaringClass() {
		Class<?> clazz = getClass();
		Class<?> zuper = clazz.getSuperclass();
		return (zuper == PseudoEnum.class) ? (Class<E>) clazz : (Class<E>) zuper;
	}
	
	protected static <V extends PseudoEnum<?>> V valueOf(String name, V[] values) {
		for (V value : values)
			if (name.equals(value.name()))
				return value;
		String simpleName = values.getClass().getSimpleName();
		throw new IllegalArgumentException("No pseudo-enum constant " + simpleName.substring(0, simpleName.length() - 2) + "." + name);
	}
	
}
