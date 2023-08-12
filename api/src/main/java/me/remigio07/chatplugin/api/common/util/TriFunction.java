/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2023  Remigio07
 * 	
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 * 	
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 	
 * 	<https://github.com/Remigio07/ChatPlugin>
 */

package me.remigio07.chatplugin.api.common.util;

import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is a {@link FunctionalInterface} whose functional method is {@link #apply(Object, Object, Object)}.
 * 
 * @param <T> The type of the first argument to the function
 * @param <U> The type of the second argument to the function
 * @param <V> The type of the third argument to the function
 * @param <R> The type of the result of the function
 */
@FunctionalInterface
public interface TriFunction<T, U, V, R> {
	
	/**
	 * Applies this function to the given arguments.
	 * 
	 * @param t The first function argument
	 * @param u The second function argument
	 * @param v The third function argument
	 * @return The function result
	 */
	public R apply(T t, U u, V v);
	
	/**
	 * Returns a composed function that first applies this function to
	 * its input, and then applies the <code>after</code> function to the result.
	 * If evaluation of either function throws an exception, it is relayed to
	 * the caller of the composed function.
	 * 
	 * @param <W> The type of output of the <code>after</code> function, and of the composed function
	 * @param after The function to apply after this function is applied
	 * @return A composed function that first applies this function and then applies the <code>after</code> function
	 * @throws NullPointerException If <code>after</code> is <code>null</code>
	 */
	public default <W> TriFunction<T, U, V, W> andThen(Function<? super R, ? extends W> after) {
		Objects.requireNonNull(after);
		return (T t, U u, V v) -> after.apply(apply(t, u, v));
	}
	
}
