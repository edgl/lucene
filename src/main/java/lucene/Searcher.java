package lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Searcher {

	public static void main(String[] args) throws IOException, ParseException {
		usage(args);
		
		String indexDir = args[0];
		String q = args[1];
		
		search(indexDir, q);
	}

	private static void search(String indexDir, String q) throws IOException, ParseException {
		Directory dir = FSDirectory.open(new File(indexDir));
		IndexSearcher searcher = new IndexSearcher(dir);
		
		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", 
				new StandardAnalyzer(Version.LUCENE_30));
		
		Query query = parser.parse(q);
		
		long start = System.currentTimeMillis();
		
		TopDocs hits = searcher.search(query, 10);
		
		long end = System.currentTimeMillis();
		
		System.err.println("Found " + hits.totalHits + " document(s) (in " + 
		  (end - start) + " millseconds) that matched query '" + q + "':");
		
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(doc.get("fullpath"));
		}
		
		searcher.close();
		
	}

	private static void usage(String[] args) {
		if(args.length != 2) {
			throw new IllegalArgumentException("Usage: " + 
					"java " + Searcher.class.getName() + 
					" <index_dir> <data_dir>");
		}
		
		
	}
}
