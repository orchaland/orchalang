package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.OrchaCompilationException;
import orcha.lang.compiler.OrchaMetadata;
import orcha.lang.compiler.syntax.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetadataSyntaxAnalysisTest {

    @Test
    public void title() {

        String expression = null;

        try {

            expression = "title: un titre";
            TitleInstruction titleInstruction = new TitleInstruction(expression);
            titleInstruction.analysis();

            String titre = titleInstruction.getTitle();
            Assert.assertNotNull(titre);
            Assert.assertEquals(titre, "un titre");

        } catch (OrchaCompilationException e) {
            Assert.fail("Syntax error in: " + expression);
        }

        try {

            expression = "title 	 :	  un titre	 ";
            TitleInstruction titleInstruction = new TitleInstruction(expression);
            titleInstruction.analysis();

            String titre = titleInstruction.getTitle();
            Assert.assertNotNull(titre);
            Assert.assertEquals(titre, "un titre");

        } catch (OrchaCompilationException e) {
            Assert.fail("Syntax error in: " + expression);
        }

    }

    @Test
    public void metadata(){
        try{

            OrchaMetadata orchaMetadata = new OrchaMetadata();
            Instruction titleInstruction = new TitleInstruction("title:   this is    a    title   ");
            titleInstruction.setLineNumber(2);
            titleInstruction.analysis();
            orchaMetadata.add(titleInstruction);

            Instruction domainInstruction = new DomainInstruction("domain:   travel   agency ");
            domainInstruction.setLineNumber(2);
            domainInstruction.analysis();
            orchaMetadata.add(domainInstruction);

            Instruction authors = new AuthorsInstruction("authors:   A.H. Alen,    Ben Orcha   ");
            authors.setLineNumber(2);
            authors.analysis();
            orchaMetadata.add(authors);

            Instruction descriptionInstruction = new DescriptionInstruction("description:   organize travel   ");
            descriptionInstruction.setLineNumber(2);
            descriptionInstruction.analysis();
            orchaMetadata.add(descriptionInstruction);

            Instruction versionInstruction = new VersionInstruction("version:   1.0   ");
            versionInstruction.setLineNumber(2);
            versionInstruction.analysis();
            orchaMetadata.add(versionInstruction);

            Assert.assertEquals(orchaMetadata.getTitle(), "this is a title");
            Assert.assertEquals(orchaMetadata.getTitleAsCapitalizedConcatainedString(), "ThisIsATitle");

            List<Instruction> metadata = orchaMetadata.getMetadata();
            DomainInstruction domain = (DomainInstruction) metadata.stream().filter(instruction -> instruction instanceof DomainInstruction).findAny().orElse(null);
            Assert.assertNotNull(domain);
            Assert.assertEquals(domain.getDomain(), "travel agency");
            Assert.assertEquals(orchaMetadata.getDomain(), "travel agency");
            Assert.assertEquals(orchaMetadata.getDomainAsCapitalizedConcatainedString(), "TravelAgency");

            AuthorsInstruction authorsInstruction = (AuthorsInstruction) metadata.stream().filter(instruction -> instruction instanceof AuthorsInstruction).findAny().orElse(null);
            Assert.assertNotNull(authorsInstruction);
            List<String> authorList = authorsInstruction.getAuthors();
            Assert.assertNotNull(authorList);
            Assert.assertEquals(authorList.size(), 2);
            String author = authorList.get(0);
            Assert.assertEquals(author, "A.H. Alen");
            author = authorList.get(1);
            Assert.assertEquals(author, "Ben Orcha");

            DescriptionInstruction description = (DescriptionInstruction) metadata.stream().filter(instruction -> instruction instanceof DescriptionInstruction).findAny().orElse(null);
            Assert.assertNotNull(description);
            Assert.assertEquals(description.getDescription(), "organize travel");

            VersionInstruction version = (VersionInstruction) metadata.stream().filter(instruction -> instruction instanceof VersionInstruction).findAny().orElse(null);
            Assert.assertNotNull(version);
            Assert.assertEquals(version.getVersion(), "1.0");

        } catch(OrchaCompilationException e){
            Assert.fail(e.getMessage());
        }

    }

}
