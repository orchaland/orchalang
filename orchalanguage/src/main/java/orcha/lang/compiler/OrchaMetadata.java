package orcha.lang.compiler;

public class OrchaMetadata {

    String title;
    String description;
    String domain;
    String author;
    String version;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
