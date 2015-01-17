package lucene;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class Indexer {

	private IndexWriter writer;
	
	public Indexer(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
		writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30),
				true, IndexWriter.MaxFieldLength.UNLIMITED);
		
	}

	public static void main(String[] args) throws Exception {
		
		if(args.length != 2) {
			throw new IllegalArgumentException("Usage: " + 
					"java " + Indexer.class.getName() + 
					" <index_dir> <data_dir>");
			
		}
		
		String indexDir = args[0];
		String dataDir = args[1];
		
		long start = System.currentTimeMillis();
		Indexer indexer = new Indexer(indexDir);
		int numIndexed;
		try {
			numIndexed = indexer.index(dataDir, new TextFilesFilter());
		} finally {
			indexer.close();
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds" );
		
		
	}

	private int index(String dataDir, FileFilter filter) throws Exception{
		
		File[] files = new File(dataDir).listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory() && !f.isHidden()) {
					index(f.getCanonicalPath(), filter);
				} else if (!f.isHidden() && f.exists() && f.canRead()
						&& (filter == null || filter.accept(f))) {
					indexFile(f);
				}
			}
		}
		
		return writer.numDocs();
	}

	private void indexFile(File f) throws IOException {
		System.out.println("Indexing " + f.getCanonicalPath());
		Document doc = getDocument(f);
		writer.addDocument(doc);
		
	}

	private Document getDocument(File f) throws IOException {
		Document doc = new Document();
		doc.add(new Field("contents", new FileReader(f)));
		doc.add(new Field("filename", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("fullpath", f.getCanonicalPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		
		return doc;
	}

	private void close() {
		try {
			writer.close();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
