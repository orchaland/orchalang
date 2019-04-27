package orcha.lang.compiler.referenceimpl;

import org.jdom2.input.SAXBuilder;
import orcha.lang.compiler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class TranspileToSpringIntegration implements Transpiler {

    private static Logger log = LoggerFactory.getLogger(TranspileToSpringIntegration.class);

    File sourceCodeDirectory;
    File binaryCodeDirectory;

    public TranspileToSpringIntegration(String pathToBinaryCode) {

        try{
            String[] directories = pathToBinaryCode.split("/");

            String pathToBinCode = ".";
            for(String directory: directories){
                pathToBinCode = pathToBinCode + File.separator + directory;
            }

            binaryCodeDirectory = new File(pathToBinCode);

            String pathToBinary =  binaryCodeDirectory.getCanonicalPath() + File.separator + "orcha" + File.separator + "lang" + File.separator + "generated";

            File[] files = new File(pathToBinary).listFiles();
            if(files != null){
                for(File file: files){
                    log.info("Delete existing generated file: " + file.getCanonicalPath());
                    file.delete();
                }
            }

            String pathToSouresCode = "." + File.separator + "src" + File.separator + "main";
            sourceCodeDirectory = new File(pathToSouresCode);


            String pathToSource =  sourceCodeDirectory.getCanonicalPath() + File.separator + "groovy" + File.separator + "orcha" + File.separator + "lang" + File.separator + "generated";

            files = new File(pathToSource).listFiles();
            if(files != null){
                for(File file: files){
                    log.info("Delete existing generated file: " + file.getCanonicalPath());
                    file.delete();
                }
            }

            pathToSource =  sourceCodeDirectory.getCanonicalPath() + File.separator + "java" + File.separator + "orcha" + File.separator + "lang" + File.separator + "generated";

            files = new File(pathToSource).listFiles();
            if(files != null){
                for(File file: files){
                    log.info("Delete existing generated file: " + file.getCanonicalPath());
                    file.delete();
                }
            }

        } catch(IOException e){
            log.error(e.getMessage());
        }


    }

    @Override
    public void transpile(OrchaProgram orchaProgram){

        OrchaMetadata orchaMetadata = orchaProgram.getOrchaMetadata();
        List<IntegrationNode> graphOfInstructions = orchaProgram.getIntegrationGraph();

        log.info("Transpilatation of the orcha program \"" + orchaMetadata + "\" into the directory " + sourceCodeDirectory.getAbsolutePath() + File.separator + "resources");

        String xmlSpringContextFileName = orchaMetadata.getTitle() + ".xml";
        String xmlSpringContent = sourceCodeDirectory.getAbsolutePath() + File.separator + "resources" + File.separator + xmlSpringContextFileName;
        File xmlSpringContextFile = new File(xmlSpringContent);

        String xmlSpringContextQoSFileName = orchaMetadata.getTitle() + "QoS.xml";
        String xmlQoSSpringContent = sourceCodeDirectory.getAbsolutePath() + File.separator + "resources" + File.separator + xmlSpringContextQoSFileName;
        File xmlQoSSpringContextFile = new File(xmlQoSSpringContent);

        StringWriter stringWriter = new StringWriter();
        stringWriter.write("<beans xmlns=\"http://www.springframework.org/schema/beans\">");
        stringWriter.write("</beans>");
        stringWriter.flush();

        String s = stringWriter.toString();

        InputStream inputStream = new ByteArrayInputStream(s.getBytes());

        SAXBuilder builder = new SAXBuilder();

        //Document xmlSpringIntegration = builder.build(inputStream);
    }

    private void transpile(OrchaMetadata orchaMetadata, List<IntegrationNode> graphOfInstructions) {

        for (IntegrationNode node: graphOfInstructions) {

            switch (node.getInstruction().getCommand()){
                case RECEIVE:
                    break;
                case COMPUTE:
                    break;
                case WHEN:
                    break;
                case SEND:
                    break;
                default:

            }

            List<IntegrationNode> adjacentNodes = node.getNextIntegrationNodes();

            this.transpile(orchaMetadata, adjacentNodes);
        }

    }
}
