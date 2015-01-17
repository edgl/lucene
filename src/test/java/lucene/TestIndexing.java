package lucene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;

public class TestIndexing {

	@Test
	public void testMe() {
		assertTrue("This is true", true);
	}
	
	
	protected String[] uids = {"1", "2"};
	protected String[] unindexed = {"Netherlands", "Italy"};
	protected String[] unstored = {"Amsterdam has lots of bridges", "Venice has lots of canals"};
	
	protected String[] text = {"Amsterdam", "Venice"};
	
	private Directory directory;
	
	@Before
	public void setup() throws Exception {
		directory = new RAMDirectory();
		
		IndexWriter writer = getWriter();
		
		for (int i = 0; i < uids.length; i++) {
			Document doc = new Document();
			doc.add(new Field("id", uids[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("country", unindexed[i], Field.Store.YES, Field.Index.NO));
			doc.add(new Field("contents", unstored[i], Field.Store.NO, Field.Index.ANALYZED));
			doc.add(new Field("city", text[i], Field.Store.YES, Field.Index.ANALYZED));
			writer.addDocument(doc);
		}
		
		writer.close();
	}

	private IndexWriter getWriter() throws IOException {
		return new IndexWriter(directory, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
	}
	
	protected int getHitCount(String field, String searchTerm) throws IOException {
		IndexSearcher searcher = new IndexSearcher(directory);
		Query query = new TermQuery(new Term(field, searchTerm));
		int hitCount = TestUtil.hitCount(searcher, query);
		searcher.close();
		
		return hitCount;		
	}
	
	@Test
	public void testIndexWriter() throws IOException {
		IndexWriter writer = getWriter();
		assertEquals(uids.length, writer.numDocs());
		writer.close();
	}
	
	@Test
	public void testIndexReader() throws IOException {
		IndexReader reader = IndexReader.open(directory);
		assertEquals(uids.length, reader.maxDoc());
		assertEquals(uids.length, reader.numDocs());
	}
	
}
