package orcha.lang.compiler

import orcha.lang.compiler.syntax.Instruction
import orcha.lang.compiler.syntax.TitleInstruction
import java.util.*

data class OrchaMetadata(var description: String? = null,
                         var domain: String? = null,
                         var author: String? = null,
                         var version: String? = null,
                         var metadata: MutableList<Instruction?> = ArrayList()) {

    var title: String? = null
        get() {
            val titleInstruction = metadata.stream().filter { instruction: Instruction? -> instruction is TitleInstruction }.findAny().orElse(null) as TitleInstruction?
            return titleInstruction?.title
        }

    fun add(instruction: Instruction?) {
        metadata.add(instruction)
    }

    /*override fun getMetadata(): List<Instruction?> {
        return metadata
    }*/

    /*fun getTitle(): String? {
        val titleInstruction = metadata.stream().filter { instruction: Instruction? -> instruction is TitleInstruction }.findAny().orElse(null) as TitleInstruction?
        return titleInstruction?.title
    }*/

    /*override fun toString(): String {
        return "OrchaMetadata{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", domain='" + domain + '\'' +
                ", author='" + author + '\'' +
                ", version='" + version + '\'' +
                '}'
    }*/
}
