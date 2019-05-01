package orcha.lang.compiler.referenceimpl;

import orcha.lang.compiler.*;
import orcha.lang.compiler.syntax.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class LexicalAnalysisImpl implements LexicalAnalysis {

    private static Logger log = LoggerFactory.getLogger(LexicalAnalysisImpl.class);

    @Resource(name = "&whenInstruction")
    AbstractFactoryBean whenInstructionFactory;

    @Resource(name = "&sendInstruction")
    AbstractFactoryBean sendInstructionFactory;

    @Override
    public OrchaProgram analysis(List<String> linesOfCode) throws OrchaCompilationException {

        log.info("Lexical analysis of the orcha program");

        List<IntegrationNode> graphOfInstructions = new ArrayList<IntegrationNode>();
        OrchaMetadata orchaMetadata = new OrchaMetadata();
        int lineNumber = 1;

        for(String lineOfCode: linesOfCode){

            lineOfCode = lineOfCode.trim();

            if(lineOfCode.equals("") == true) {}
            else {

                log.info("Lexical analysis of the instruction: " + lineOfCode);

                if (lineOfCode.toLowerCase().startsWith("receive")) {

                    Instruction orchaInstruction = new ReceiveInstruction(lineOfCode, lineNumber);
                    graphOfInstructions.add(new IntegrationNode(orchaInstruction));

                } else if (lineOfCode.toLowerCase().startsWith("compute")) {

                    Instruction orchaInstruction = new ComputeInstruction(lineOfCode, lineNumber);
                    graphOfInstructions.add(new IntegrationNode(orchaInstruction));

                } else if (lineOfCode.toLowerCase().startsWith("when")) {

                    try{
                        Instruction orchaInstruction = (Instruction)whenInstructionFactory.getObject();
                        orchaInstruction.setLineNumber(lineNumber);
                        orchaInstruction.setInstruction(lineOfCode);
                        graphOfInstructions.add(new IntegrationNode(orchaInstruction));

                    } catch (Exception e){
                        throw new OrchaCompilationException(e.getMessage());
                    }

                } else if (lineOfCode.toLowerCase().startsWith("send")) {

                    try{
                        Instruction orchaInstruction = (Instruction)sendInstructionFactory.getObject();
                        orchaInstruction.setLineNumber(lineNumber);
                        orchaInstruction.setInstruction(lineOfCode);
                        graphOfInstructions.add(new IntegrationNode(orchaInstruction));
                    } catch (Exception e){
                        throw new OrchaCompilationException(e.getMessage());
                    }


                } else if (lineOfCode.toLowerCase().startsWith("title")) {

                    Instruction titleInstruction = new TitleInstruction(lineOfCode, lineNumber);
                    orchaMetadata.add(titleInstruction);

                } else if (lineOfCode.toLowerCase().startsWith("domain")) {

                    Instruction domainInstruction = new DomainInstruction(lineOfCode, lineNumber);
                    orchaMetadata.add(domainInstruction);

                } else if (lineOfCode.toLowerCase().startsWith("description")) {

                    Instruction descriptionInstruction = new DescriptionInstruction(lineOfCode, lineNumber);
                    orchaMetadata.add(descriptionInstruction);

                } else if (lineOfCode.toLowerCase().startsWith("authors")) {

                    Instruction authorsInstruction = new AuthorsInstruction(lineOfCode, lineNumber);
                    orchaMetadata.add(authorsInstruction);


                } else if (lineOfCode.toLowerCase().startsWith("version")) {

                    Instruction versionInstruction = new VersionInstruction(lineOfCode, lineNumber);
                    orchaMetadata.add(versionInstruction);


                } else {
                    throw new OrchaCompilationException("Syntax error", lineNumber, lineOfCode);
                }

            }
            lineNumber++;

        }

        log.info("Lexical analysis of the orcha program complete successfully");

        return new OrchaProgram(graphOfInstructions, orchaMetadata);
    }


/*    @Override
    public OrchaProgram analysis(String orchaFileName) throws OrchaCompilationException {

        //String pathToCode = "." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source" + File.separator + orchaFileName;
        String pathToCode = "." + File.separator + "src" + File.separator + "main" + File.separator + "orcha" + File.separator + "source";

        log.info("Lexical analysis of the orcha program: " + pathToCode  + File.separator + orchaFileName);

        File orchaFile = new File(pathToCode + File.separator + orchaFileName);

        if(orchaFile.exists() == false){
            try {
                String path = orchaFile.getCanonicalPath();
                throw new FileNotFoundException(path);
            } catch (IOException e) {
            }
        }

        List<IntegrationNode> graphOfInstructions = new ArrayList<IntegrationNode>();
        OrchaMetadata orchaMetadata = new OrchaMetadata();
        int lineNumber = 1;
        int instructionID = 1;

        Path path = FileSystems.getDefault().getPath(new File(pathToCode).getAbsolutePath(), orchaFileName);

        List<String> liste;
        try {
            liste = Files.readAllLines(path);
            for(String line: liste) {

                log.info("Lexical analysis of the instruction: " + line);

                String instruction = line;

                instruction = instruction.trim();

                String[] tokens = instruction.split("\\s+");

                System.out.println("--" + instruction + "--" + tokens.length);

                if(instruction.equals("")==true || tokens.length==0 || tokens[0].equals("package")) {}
                else {
                    if (tokens.length < 2) {
                        throw new OrchaCompilationException("Syntax error", lineNumber, instruction, orchaFile.getCanonicalPath());
                    }

                    if (tokens[0].toLowerCase().equals("receive")) {

                        Instruction orchaInstruction = new ReceiveInstruction(instruction, instructionID, lineNumber);
                        graphOfInstructions.add(new IntegrationNode(orchaInstruction));
                        instructionID++;

                    } else if (tokens[0].toLowerCase().equals("compute")) {

                        Instruction orchaInstruction = new ComputeInstruction(instruction, instructionID, lineNumber);
                        graphOfInstructions.add(new IntegrationNode(orchaInstruction));
                        instructionID++;

                    } else if (tokens[0].toLowerCase().equals("when")) {

                        try{
                            Instruction orchaInstruction = whenInstructionFactory.getObject();
                            graphOfInstructions.add(new IntegrationNode(orchaInstruction));
                            instructionID++;
                        } catch (Exception e){
                            throw new OrchaCompilationException(e.getMessage());
                        }


                    } else if (tokens[0].toLowerCase().equals("send")) {

                        Instruction orchaInstruction = new SendInstruction(instruction, instructionID, lineNumber);
                        graphOfInstructions.add(new IntegrationNode(orchaInstruction));
                        instructionID++;

                    } else if (tokens[0].toLowerCase().equals("title")) {

                        orchaMetadata.setTitle(instruction.substring("title".length()).trim());

                    } else if (tokens[0].toLowerCase().equals("domain")) {

                        orchaMetadata.setDomain(instruction.substring("domain".length()).trim());

                    } else if (tokens[0].toLowerCase().equals("description")) {

                        orchaMetadata.setDescription(instruction.substring("description".length()).trim());

                    } else if (tokens[0].toLowerCase().equals("author")) {

                        orchaMetadata.setAuthor(instruction.substring("author".length()).trim());

                    } else if (tokens[0].toLowerCase().equals("version")) {

                        orchaMetadata.setVersion(instruction.substring("version".length()).trim());

                    } else {
                        throw new OrchaCompilationException("Syntax error", lineNumber, instruction, pathToCode);
                    }

                    if (orchaMetadata.getTitle() == null) {
                        throw new OrchaCompilationException("title metadata is missing", orchaFile.getCanonicalPath());
                    }
                }
                lineNumber++;

            }
        } catch (IOException e) {
            OrchaCompilationException exception = new OrchaCompilationException(e.getMessage(), orchaFile.getAbsolutePath());
            log.error("Error while reading the file " + orchaFile.getAbsolutePath(), exception);
            throw exception;
        }

        try{
            log.info("Lexical analysis of the orcha program: " + orchaFile.getCanonicalPath() + " complete successfully.");
        }catch(IOException e) {
        }

        return new OrchaProgram(graphOfInstructions, orchaMetadata);

    }
*/
}

