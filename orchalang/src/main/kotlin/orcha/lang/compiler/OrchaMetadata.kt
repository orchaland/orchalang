package orcha.lang.compiler

import orcha.lang.compiler.syntax.*
import java.util.*

data class OrchaMetadata(var metadata: MutableList<Instruction?> = ArrayList()) {

    var authors: MutableList<String>? = null
        get() {
            val authorInstruction = metadata.stream().filter { instruction: Instruction? -> instruction is AuthorsInstruction }.findAny().orElse(null) as AuthorsInstruction?
            return authorInstruction?.authors
        }

    var description: String? = null
        get() {
            val descriptionInstruction = metadata.stream().filter { instruction: Instruction? -> instruction is DescriptionInstruction }.findAny().orElse(null) as DescriptionInstruction?
            return descriptionInstruction?.description
        }

    var domain: String? = null
        get() {
            val domainInstruction = metadata.stream().filter { instruction: Instruction? -> instruction is DomainInstruction }.findAny().orElse(null) as DomainInstruction?
            return domainInstruction?.domain
        }

    var domainAsCapitalizedConcatainedString: String? = null
        get() {
            val domainInstruction = metadata.stream().filter { instruction: Instruction? -> instruction is DomainInstruction }.findAny().orElse(null) as DomainInstruction?
            return domainInstruction!!.domain.split(" ").joinToString(""){ it.toString().capitalize() }
            return domainInstruction?.domain
        }

    var title: String? = null
        get() {
            val titleInstruction = metadata.stream().filter { instruction: Instruction? -> instruction is TitleInstruction }.findAny().orElse(null) as TitleInstruction?
            return titleInstruction?.title
        }

    var titleAsCapitalizedConcatainedString: String? = null
        get() {
            val titleInstruction = metadata.stream().filter { instruction: Instruction? -> instruction is TitleInstruction }.findAny().orElse(null) as TitleInstruction?
            return titleInstruction!!.title.split(" ").joinToString(""){ it.toString().capitalize() }
        }

    var version: String? = null
        get() {
            val versionInstruction = metadata.stream().filter { instruction: Instruction? -> instruction is VersionInstruction }.findAny().orElse(null) as VersionInstruction?
            return versionInstruction?.version
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
