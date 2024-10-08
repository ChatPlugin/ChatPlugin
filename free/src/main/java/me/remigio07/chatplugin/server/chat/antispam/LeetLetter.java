/*
 * 	ChatPlugin - A complete yet lightweight plugin which handles just too many features!
 * 	Copyright 2024  Remigio07
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

package me.remigio07.chatplugin.server.chat.antispam;

public enum LeetLetter {
	
	A("4", "@", "α", "ἄ", "/\\", "/-\\", "^", "д", "à", "á", "â", "ã", "ä", "å", "ȧ", "ǎ", "ă", "ā", "ą", "ⱥ", "ấ", "ầ", "ắ", "ằ", "ǡ", "ǻ", "ǟ", "ẫ", "ẵ", "ả", "ȁ", "ȃ", "ẩ", "ẳ", "ạ", "ḁ", "ậ", "ặ", "𐌀", "ª", "ᵃ", "ₐ", "ₔ", "⁴", "₄", "ə", "ⓐ", "🅐", "𝖆", "ᴀ", "𝐚", "𝐀", "𝘢", "𝘈", "𝓪", "𝒜"),
	B("8", "θ", "|3", "β", "ß", "ḃ", "ƀ", "ɓ", "ḅ", "ḇ", "ƃ", "ᵇ", "⁸", "₈", "฿", "ⓑ", "🅑", "𝖇", "𝕭", "𝕻", "ʙ", "𝐛", "𝐁", "𝘣", "𝘉", "𝓫", "ℬ"),
	D("|)", "[)", "ḋ", "ď", "ḑ", "đ", "ƌ", "ɗ", "ḍ", "ḓ", "ḏ", "ð", "ᵈ", "ⓓ", "🅓", "𝖉", "𝕯", "ᴅ", "𝐝", "𝐃", "𝘥", "𝘋", "𝓭", "𝒟"),
	E("ξ", "3", "&", "£", "ε", "è", "é", "ê", "ë", "ė", "ě", "ĕ", "ē", "ẽ", "ę", "ȩ", "ɇ", "ế", "ề", "ḗ", "ḕ", "ễ", "ḝ", "ẻ", "ȅ", "ȇ", "ể", "ẹ", "ḙ", "ḛ", "ệ", "ᵉ", "ₑ", "³", "₃", "ⓔ", "🅔", "𝖊", "ᴇ", "𝐞", "𝐄", "𝘦", "𝘌", "𝓮", "ℰ"),
	F("ḟ", "ƒ", "ᶠ", "ⓕ", "🅕", "ꜰ", "𝐟", "𝐅", "𝘧", "𝘍", "𝓯", "ℱ"),
	G("6", "9", "ǵ", "ġ", "ĝ", "ǧ", "ğ", "ḡ", "ģ", "ǥ", "ɠ", "ᵍ", "⁶", "⁹", "₆", "₉", "ⓖ", "🅖", "𝖌", "ᴳ", "ɢ", "𝐠", "𝐆", "𝘨", "𝘎", "𝓰", "𝒢"),
	H("#", "]-[", "|-|", "ḣ", "ĥ", "ḧ", "ȟ", "ḩ", "ħ", "ḥ", "ḫ", "ⱨ", "ʰ", "ₕ", "ⓗ", "🅗", "𝖍", "ʜ", "𝐡", "𝐇", "𝘩", "𝘏", "𝓱", "ℋ"),
	J("ĵ", "ǰ", "ɉ", "ʲ", "ⱼ", "ⓙ", "🅙", "𝖏", "𝕵", "ᴊ", "𝐣", "𝐉", "𝘫", "𝘑", "𝓳", "𝒥"),
	K("|<", "|{", "κ", "ᵏ", "ₖ", "ḱ", "ǩ", "ķ", "ƙ", "ḳ", "ḵ", "ⱪ", "ĸ", "|‹", "ⓚ", "🅚", "ᴋ", "𝐤", "𝐊", "𝘬", "𝘒", "𝓴", "𝒦"),
	M("/\\/\\", "/v\\", "|\\/|", "|v|", "ᵐ", "ₘ", "ḿ", "ṁ", "ṃ", "₥", "ⓜ", "🅜", "𝖒", "𝕸", "ᴍ", "𝐦", "𝐌", "𝘮", "𝘔", "𝓶", "ℳ"),
	O("0", "()", "[]", "ø", "ó", "ò", "ȯ", "ô", "ö", "ǒ", "ŏ", "ō", "õ", "ǫ", "ő", "ố", "ồ", "σ", "º", "ᵒ", "ₒ", "⁰", "₀", "°", "⁽⁾", "∅", "¤", "ⓞ", "🅞", "𝖔", "𝕺", "ᴏ", "𝐨", "𝐎", "𝘰", "𝘖", "𝓸", "𝒪"),
	P("⁋", "ρ", "℗", "|*", "|°", "ᵖ", "ₚ", "þ", "₱", "ⓟ", "🅟", "𝖕", "ᴘ", "𝐩", "𝐏", "𝘱", "𝘗", "𝓹", "𝒫"),
	Q("2", "¶", "ⓠ", "🅠", "ᑫ", "𝖖", "𝕼", "q", "𝐪", "𝐐", "𝘲", "𝘘", "𝓺", "𝒬"),
	R("я", "®", "|2", "ԇ", "ʳ", "ᵣ", "ŗ", "ř", "ⓡ", "🅡", "𝕽", "ʀ", "𝐫", "𝐑", "𝘳", "𝘙", "𝓻", "ℛ"),
	S("5", "ş", "$", "ˢ", "ₛ", "⁵", "₅", "š", "ⓢ", "🅢", "ꜱ", "𝐬", "𝐒", "𝘴", "𝘚", "𝓼", "𝒮"),
	T("7", "τ", "+", "†", "ţ", "ᵗ", "ₜ", "⁷", "₇", "ŧ", "ť", "ẗ", "ⓣ", "🅣", "𝖙", "ᴛ", "𝐭", "𝐓", "𝘵", "𝘛", "𝓽", "𝒯"),
	W("\\/\\/", "vv", "\\^/", "\\v/", "\\x/", "\\|/", "\\\\//", "₩", "ш", "щ", "ω", "𝓌", "ʷ", "ẘ", "ẅ", "ⓦ", "🅦", "ᴡ", "𝐰", "𝐖", "𝘸", "𝘞", "𝔀", "𝒲"),
	X("χ", "×", "><", "}{", ")(", "*", "ˣ", "ₓ", "ẍ", "›‹", "ⓧ", "🅧", "𝐱", "𝐗", "𝘹", "𝘟", "𝔁", "𝒳"),
	Y("γ", "ч", "`/", "¥", "j", "ý", "ỳ", "ẏ", "ŷ", "ÿ", "ȳ", "ỹ", "ẙ", "ɏ", "ỷ", "ỵ", "ʸ", "ᵧ", "ⓨ", "🅨", "ʏ", "𝐲", "𝐘", "𝘺", "𝘠", "𝔂", "𝒴"),
	Z("ζ", "2", "%", "ᶻ", "²", "₂", "ž", "𝓏", "ⓩ", "🅩", "ᴢ", "𝐳", "𝐙", "𝘻", "𝘡", "𝔃", "𝒵"),
	C("(", "<", "[", "©", "¢", "ç", "ć", "ċ", "ĉ", "č", "ȼ", "ḉ", "ƈ", "⁽", "‹", "₡", "ᶜ", "ⓒ", "🄯", "🄫", "🅒", "𝖈", "ᴄ", "𝐜", "𝐂", "𝘤", "𝘊", "𝓬", "𝒞"),
	N("/\\/", "и", "ท ", "ǹ", "ń", "ñ", "ň", "ṅ", "ņ", "ɲ", "ƞ", "ṇ", "ṋ", "ṉ", "ŋ", "ⁿ", "ₙ", "ⓝ", "🅝", "𝖓", "ɴ", "𝐧", "𝐍", "𝘯", "𝘕", "𝓷", "𝒩"),
	V("\\/", "√", "ᵛ", "ᵥ", "ⓥ", "🅥", "ᴠ", "𝐯", "𝐕", "𝘷", "𝘝", "𝓿", "𝒱"),
	U("v", "υ", "ù", "ú", "û", "ü", "|_|", "(_)", "µ", "ᵘ", "ᵤ", "บ ", "ű", "ǔ", "ų", "ŭ", "ū", "ů", "ǖ", "ǘ", "ǚ", "ǜ", "ⓤ", "🅤", "𝖚", "𝖀", "ᴜ", "𝐮", "𝐔", "𝘶", "𝘜", "𝓾", "𝒰"),
	L("|_", "ĺ", "ŀ", "ľ", "ⱡ", "ļ", "ƚ", "ł", "ḷ", "ḽ", "ḻ", "ḹ", "ˡ", "ₗ", "ⓛ", "🅛", "𝖑", "ʟ", "𝐥", "𝐋", "𝘭", "𝘓", "𝓵", "ℒ"),
	I("ι", "1", "!", "|", "┃", "╏", "╎", "┇", "︱", "┊", "︳", "┋", "┆", "í", "ì", "ı", "î", "ï", "ǐ", "ĭ", "ī", "ĩ", "į", "ɨ", "ḯ", "ỉ", "ȉ", "ȋ", "ị", "ḭ", "ⁱ", "ᵢ", "¹", "₁", "¡", "ⓘ", "🅘", "𝖎", "ɪ", "𝐢", "𝐈", "𝘪", "𝘐", "𝓲", "ℐ");
	
	private String[] replacements;
	
	private LeetLetter(String... replacements) {
		this.replacements = replacements;
	}
	
	public String[] getReplacements() {
		return replacements;
	}
	
	public String replace(String input) {
		for (String replacement : replacements)
			input = input.replace(replacement, name());
		return input;
	}
	
}
