package orcha.lang.compiler.referenceimpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import orcha.lang.compiler.OrchaCompilationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import orcha.lang.compiler.Preprocessing;

public class PreprocessingImpl implements Preprocessing{
	
	private static Logger log = LoggerFactory.getLogger(PreprocessingImpl.class);

	@Value("${orcha.pathToOrchaSourceFile:src/main/orcha/source}")
	String pathToOrchaSourceFile;

	@Override
	public List<String> process(String orchaFileName) throws OrchaCompilationException {

		log.info("Filename of the orcha program: " + orchaFileName);

		String pathToSourceFile = ".";
				
		for(String s: pathToOrchaSourceFile.split("/")) {
			pathToSourceFile = pathToSourceFile + File.separator + s;
		}
				
		File file = new File(pathToSourceFile + File.separator + orchaFileName);
		if(file.exists() == false){
			throw  new OrchaCompilationException("File not found: " + file.getAbsolutePath(), file.getAbsolutePath());
		}
		
		log.info("Preprocessing of the orcha program: " + file.getAbsolutePath());
		
		Path path = FileSystems.getDefault().getPath(new File(pathToSourceFile).getAbsolutePath(), orchaFileName);

		List<String> instructions = new ArrayList<String>();
		
		List<String> liste;
		try {
			liste = Files.readAllLines(path);
			for(String line: liste) {
				instructions.add(line.trim());
			}
		} catch (IOException e) {
			OrchaCompilationException exception = new OrchaCompilationException(e.getMessage(), file.getAbsolutePath());
			log.error("Error while reading the file " + file.getAbsolutePath(), exception);
			throw exception;
		}

		IntStream.range(0, instructions.size()).filter(
			i ->
				instructions.get(i).startsWith("receive ")==false &&
				instructions.get(i).startsWith("compute ")==false &&
				instructions.get(i).startsWith("when ")==false &&
				instructions.get(i).startsWith("send ")==false &&
				instructions.get(i).startsWith("//")==false &&
				instructions.get(i).startsWith("/*")==false &&
				instructions.get(i).startsWith("/**")==false &&
				instructions.get(i).startsWith("*")==false &&
				instructions.get(i).startsWith("*/")==false &&
				instructions.get(i).startsWith("title:")==false &&
				instructions.get(i).startsWith("domain:")==false &&
				instructions.get(i).startsWith("description:")==false &&
				instructions.get(i).startsWith("authors:")==false &&
				instructions.get(i).startsWith("version:")==false &&
				instructions.get(i).equals("")==false
		).forEach(
			i -> instructions.set(i, "compute " + instructions.get(i))
		);

		log.info("Preprocessing of the Orcha file complete successfully: " + file.getAbsolutePath());

		return instructions;
	}

}
