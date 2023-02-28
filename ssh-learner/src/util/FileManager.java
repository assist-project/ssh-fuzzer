package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

public class FileManager {

	/**
	 * Copies a file at path to a different path, also creating the necessary directory.
	 * Doesn't do anything if the from path doesn't exist. 
	 */
	public static void copy(String from, String to) throws IOException{
		Path fromPath = FileSystems.getDefault().getPath(from);
		Path toPath = FileSystems.getDefault().getPath(to);
		if(fromPath.toFile().exists()) {
			copyFromTo(fromPath, toPath);
		}
	}
	
	public static void copyFromTo(Path fromPath, Path toPath) throws IOException{
		File fromFile = fromPath.toFile();
		Files.createDirectories(toPath.getParent());
		if(fromFile.exists() && fromFile.isFile()) {
			Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
		}
		else {
			if(!Files.exists(toPath)) {
				Files.createDirectories(toPath);
			}
			for(String subFileName : fromFile.list()) {
				copyFromTo(fromPath.resolve(subFileName), toPath.resolve(subFileName));
			}
		}
	}
	
	public static void move(String from, String to) throws IOException {
		Path fromPath = FileSystems.getDefault().getPath(from);
		Path toPath = FileSystems.getDefault().getPath(to);
		Iterator<Path> pathIter = Files.walk(fromPath).iterator();
		while (pathIter.hasNext()) {
			Path path = pathIter.next();
			copyFromTo(path, toPath);
			Files.delete(path);
		}
	}
}
