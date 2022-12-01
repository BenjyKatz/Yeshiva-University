// package edu.yu.cs.com1320.project.stage5.impl;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import edu.yu.cs.com1320.project.stage5.PersistenceManager;
// import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
// import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
// import edu.yu.cs.com1320.project.stage5.Document;
// import edu.yu.cs.com1320.project.stage5.DocumentStore;
// import java.io.ByteArrayInputStream;

// import java.net.URI;
// import java.io.IOException;
// import java.io.File;
// import java.util.Arrays;

// import static org.junit.jupiter.api.Assertions.*;

// import edu.yu.cs.com1320.project.Utils;
// public class PmTest{
// 	@Test
// 	public void fileTest(){
// 		PersistenceManager pm = new DocumentPersistenceManager(null);
		
// 		try{
// 		DocumentImpl doc2 = new DocumentImpl(new URI("http://www.yu.edu/documents/doc2"), "This is the second doc");
// 		DocumentImpl doc1 = new DocumentImpl(new URI("http://www.yu.edu/documents/doc1"), "Hello there world".getBytes());
// 		pm.serialize(new URI("http://www.yu.edu/documents/doc1"), doc1);

// 		pm.serialize(new URI("http://www.yu.edu/documents/doc2"), doc2);
// 		//pm.deserialize(new URI("http://www.yu.edu/documents/doc2"));
//         System.out.println("call deserialize");
// 		//DocumentImpl doc1Ds = (DocumentImpl) pm.deserialize(new URI("http://www.yu.edu/documents/doc1"));
		
// 		//assertFalse(doc2Ds);

// 		//System.out.println(pm.deserialize(new URI("http://www.yu.edu/documents/doc1")));
// 		// System.out.println("Hello there world".getBytes());
// 		// System.out.println("binary bytes "+doc1Ds.getDocumentBinaryData() );
// 		// System.out.println(doc1Ds.getWords());
// 		// System.out.println(doc1Ds.getKey());
// 		// System.out.println("Word Map "+doc1Ds.getWordMap());

// 		// System.out.println(doc1.getDocumentBinaryData() );
// 		// System.out.println(doc1.getWords());
// 		// System.out.println(doc1.getKey());
// 		// System.out.println(doc1.getWordMap());
// 		}
// 		catch(Exception e){
// 			fail(e +  " Problem");
// 		}
// 	}
// 	private URI uri1;
// 	private URI uri2;
// 	private URI uri3;
// 	private URI uri4;
// 	private String txt1;
// 	private String txt2;
// 	private String txt3;
// 	private String txt4;
// 	@Test
// 	public void DsStoreTestWithText()throws Exception{
// 		System.out.println("New test!!!");
		
//         uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//         txt1 = "This doc1 plain text string Computer Headphones";

//         //init possible values for doc2
//         uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//      	txt2 = "Text doc2 plain String";

//         //init possible values for doc3
//         uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//         txt3 = "This is the text of doc3";

//         //init possible values for doc4
//         uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
//         txt4 = "This is the text of doc4";
// 		DocumentStore store = new DocumentStoreImpl();
// 		System.out.println("Begin the test");
// 		store.setMaxDocumentCount(3);
//         store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//         store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//         store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
//         System.out.println("About to put the last doc in with no room");

//         store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
//         System.out.println("About to search for doc1");
//         store.searchByPrefix("Compu");
//         System.out.println("About to search for doc3");
//         store.search("doc3");
//         //store.setMaxDocumentCount(1);
//        // assertNotNull(store.getDocument(this.uri1));
//         System.out.println("About to delete for doc3");
      
//        store.deleteAllWithPrefix("doc3");
//        assert(store.search("doc3").isEmpty());
//        //assertNull(store.getDocument(this.uri3));
// //       store.undo();
//      //  assertNotNull(store.getDocument(this.uri3));
//       // store.undo(this.uri2);

//        // assertNull(store.getDocument(this.uri2));
//       //  assertNull(store.getDocument(this.uri3));
//         //store.getDocument(this.uri1);
//         System.out.println("end of test");
        
// 	}
// 	@Test
// 	public void DsStoreTestWithBinary()throws Exception{
		
//         uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//         txt1 = "This doc1 plain text string Computer Headphones";

//         //init possible values for doc2
//         uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//      	txt2 = "Text doc2 plain String";

//         //init possible values for doc3
//         uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//         txt3 = "This is the text of doc3";

//         //init possible values for doc4
//         uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
//         txt4 = "This is the text of doc4";
// 		DocumentStore store = new DocumentStoreImpl(new File("Binary Test"));
// 		//System.out.println("Begin the test");
// 		store.setMaxDocumentCount(3);
//         store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.BINARY);
//         store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.BINARY);
//         store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.BINARY);
//         //System.out.println("About to put the last doc in with no room");
//         store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.BINARY);
//         Document doc = store.getDocument(new URI("http://edu.yu.cs/com1320/project/doc1"));
//         assertTrue(Arrays.equals(this.txt1.getBytes(),doc.getDocumentBinaryData()));

        
// 	}
// }