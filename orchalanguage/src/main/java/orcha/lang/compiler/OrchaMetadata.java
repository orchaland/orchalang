package orcha.lang.compiler;

import orcha.lang.compiler.syntax.Instruction;
import orcha.lang.compiler.syntax.TitleInstruction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrchaMetadata {

    List<Instruction> metadata = new ArrayList<Instruction>();

    String title;
    String description;
    String domain;
    String author;
    String version;

    public void add(Instruction instruction) {
        metadata.add(instruction);
    }

    public List<Instruction> getMetadata() {
        return metadata;
    }

    public String getTitle() {
        TitleInstruction titleInstruction = (TitleInstruction) metadata.stream().filter(instruction -> instruction instanceof TitleInstruction).findAny().orElse(null);
        if(titleInstruction != null){
            return titleInstruction.getTitle();
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "OrchaMetadata{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", domain='" + domain + '\'' +
                ", author='" + author + '\'' +
                ", version='" + version + '\'' +
                '}';
    }

}
