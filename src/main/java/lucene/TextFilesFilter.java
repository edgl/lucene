package lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

public class TextFilesFilter implements FileFilter {

	public boolean accept(File pathname) {
		try {
			return pathname.getName().toLowerCase().endsWith(".txt") &&
					!pathname.getCanonicalPath().contains("cuttlefish/");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		
	}

}
