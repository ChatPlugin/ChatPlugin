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
 * 	<https://github.com/ChatPlugin/ChatPlugin>
 */

package me.remigio07.chatplugin.common.util;

import me.remigio07.chatplugin.api.common.util.Library;

public class LibraryException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private Library library;
	
	public LibraryException(Throwable throwable, Library library) {
		super(getFirstCause(throwable));
		this.library = library;
	}
	
	private static Throwable getFirstCause(Throwable throwable) {
		while (throwable.getCause() != null)
			throwable = throwable.getCause();
		return throwable;
	}
	
	public Library getLibrary() {
		return library;
	}
	
	@Override
	public String getMessage() {
		String message = getCause().getMessage();
		return (message.contains("org.objectweb") || message.contains("me.remigio07.jarrelocator")
				? "a relocation library is not working properly. Stop the server, delete the ChatPlugin/libraries folder and try again"
				: ("failed to load " + library.getName() + " library")) + " - " + getCause().getClass().getSimpleName()
				+ ": " + message;
	}
	
}
