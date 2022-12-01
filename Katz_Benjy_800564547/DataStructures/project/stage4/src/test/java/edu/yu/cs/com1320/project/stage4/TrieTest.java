// package edu.yu.cs.com1320.project;

// //import org.junit.jupiter.api.*;
// //import static org.junit.jupiter.api.Assertions.*;
// import static org.junit.Assert.*;
// //import  org.junit.Assert;

// import java.util.Comparator;
// import java.io.*;
// import java.util.Set;
// import java.util.List;
// import org.junit.Test;
// import java.net.URI;
// import java.net.URI.*;
// import java.io.*;
// import java.net.URISyntaxException;
// import edu.yu.cs.com1320.project.Trie;
// import edu.yu.cs.com1320.project.impl.TrieImpl;
// import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
// import edu.yu.cs.com1320.project.stage4.DocumentStore;
// import edu.yu.cs.com1320.project.stage4.Document;
// import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
// import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;
// public class TrieTest{
// 	@Test
// 	public void simpleTriePutandGetTest(){
// 		TrieImpl<Integer> trie = new TrieImpl<Integer>();
// 		trie.put("ThAt", 7);
// 		List<Integer> results = trie.getAllSorted("that", (int1, int2) -> {
						  
// 						   return int1-int2;});
// 		for(Integer i: results){
// 			assertEquals(i, (Integer)7);
// 		}
// 	}
// 	@Test
// 	public void multipleValuesTest(){
// 		TrieImpl<Integer> trie = new TrieImpl<Integer>();
// 		trie.put("ThAt", 7);
// 		trie.put("th", 10);
// 		trie.put("thAts", 11);
// 		trie.put("that", 100);
// 		trie.put("that", 55);
// 		List<Integer> results = trie.getAllSorted("that", (int1, int2) -> {
// 						   if ((int) int1 < (int) int2) {
// 						    return -1;
// 						   } else if ((int) int2 < (int) int1) {
// 						    return 1;
// 						   }
// 						   return 0;});
// 		//for(Integer i: results){
// 		//	System.out.println(i);
// 		//}
// 		results = trie.getAllWithPrefixSorted("tha", (int1, int2)->{ return int1-int2;});
// 		for(Integer i: results){
// 			System.out.println(i);
// 		}
// 	}
// 	@Test
//  public void wordCountAndGetWordsTest() throws URISyntaxException {
//   DocumentImpl txtDoc = new DocumentImpl(new URI("placeholder"), " The!se ARE? sOme   W@o%$rds with^ s**ymbols (m)ixed [in]. Hope   this test test test test test passes!");
//   assertEquals(0, txtDoc.wordCount("bundle"));
//   assertEquals(1, txtDoc.wordCount("these"));
//   assertEquals(1, txtDoc.wordCount("WORDS"));
//   //assertEquals(1, txtDoc.wordCount("S-Y-M-B-O-??-LS"));
//  // assertEquals(1, txtDoc.wordCount("p@A$$sse$s"));
//   assertEquals(5, txtDoc.wordCount("tEst"));
//   Set<String> words = txtDoc.getWords();
//   assertEquals(12, words.size());
//   assertTrue(words.contains("some"));

//   DocumentImpl binaryDoc = new DocumentImpl(new URI("0110"), new byte[] {0,1,1,0});
//   assertEquals(0, binaryDoc.wordCount("anythingYouPutHereShouldBeZero"));
//   Set<String> words2 = binaryDoc.getWords();
//   assertEquals(0, words2.size());
//  }


//  //variables to hold possible values for doc1
//         private URI uri1;
//         private String txt1;
    
//         //variables to hold possible values for doc2
//         private URI uri2;
//         String txt2;

//         private URI uri3;
//         String txt3;
    
//   //      @BeforeEach
//         public void init() throws Exception {
//             //init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";
//         }
    
//         @Test
//         public void basicSearchAndOrganizationTest() throws Exception {
//         	//init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";

//             DocumentStore store = new DocumentStoreImpl();
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
//             assertEquals(1, store.search("PiE").size());
//             assertEquals(3, store.searchByPrefix("p").size());
//             assertEquals(0, store.searchByPrefix("x").size());
//             assertEquals(3, store.searchByPrefix("pi").size());
//             assertEquals(5, store.search("PiZzA").get(0).wordCount("pizza"));
//             assertEquals(6, store.searchByPrefix("p").get(0).getWords().size());
//         }
//         @Test
//         public void basicSearchDeleteTest() throws Exception {
//         	//init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";

//             DocumentStore store = new DocumentStoreImpl();
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
//             System.out.println("About to search");
//             System.out.println("here :"+ store.search("PiE"));
//             //assertEquals(1, store.search("PiE").size());
//             System.out.println("prefix p: "+store.searchByPrefix("p"));
//             assertEquals(3, store.searchByPrefix("p").size());
//             assertEquals(1, store.search("possum").size());
//             store.deleteDocument(this.uri3);
//             DocumentImpl doc1 = new DocumentImpl(this.uri1, this.txt1);
//             DocumentImpl doc2 = new DocumentImpl(this.uri2, this.txt2);
//             DocumentImpl doc3 = new DocumentImpl(this.uri3, this.txt3);
//          /*   for (char c = 'a'; c<='z'; c++) {
//                 List<Document> list = store.searchByPrefix(Character.toString(c));
//                 if (list.size()!=0) {
//                 	System.out.println(c);
//                     assertNotEquals(doc3, list.get(0));
//                     if ((!list.get(0).equals(doc1))&&(!list.get(0).equals(doc2))) {
//                         fail();
//                     }
//                 }
//             }
//             for (char c = '0'; c<='9'; c++) {
//                 List<Document> list = store.searchByPrefix(Character.toString(c));
//                 if (list.size()!=0) {
//                     assertNotEquals(doc3, list.get(0));
//                     if ((!list.get(0).equals(doc1))&&(!list.get(0).equals(doc2))) {
//                         fail();
//                     }
//                 }
//             }*/
//             System.out.println("Start basicSearchDel");
//             assertEquals(0, store.search("possum").size());
//             assertEquals(2, store.search("pizza").size());
//             store.deleteDocument(this.uri2);
//             assertEquals(1, store.search("pizza").size());
//         }
//         @Test
//         public void basicPutOverwriteTest() throws Exception {
//         	//init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";
//             DocumentStore store = new DocumentStoreImpl();
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             assertEquals(2, store.search("pizza").size());
//             store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             assertEquals(1, store.search("pizza").size());
//         }
//         @Test
//         public void testDeleteAndDeleteAll() throws Exception {
//         	//init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";
//             DocumentStore store = new DocumentStoreImpl();
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
//             assertEquals(2, store.search("pizza").size());
//             store.deleteAll("PiZZa");
//             assertEquals(0, store.search("pizza").size());
//             assertNull(store.getDocument(this.uri1));
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             assertEquals(2, store.search("pizza").size());
//             assertNotNull(store.getDocument(this.uri1));assertNotNull(store.getDocument(this.uri2));assertNotNull(store.getDocument(this.uri3));
//             store.deleteAllWithPrefix("p");
//             assertNull(store.getDocument(this.uri1));assertNull(store.getDocument(this.uri2));assertNull(store.getDocument(this.uri3));
//         }
//         @Test
//         public void testUndoNoArgs() throws Exception {
//         	//init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";
//             DocumentStore store = new DocumentStoreImpl();
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
//             store.undo();
//             assertEquals(null, store.getDocument(this.uri3));
//             assertEquals(0, store.search("penguin").size());
//             store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
//             store.deleteAll("pizza");
//             assertEquals(0, store.search("pizza").size());
//             assertNull(store.getDocument(this.uri1));
//             store.undo();
//             assertEquals(2, store.search("pizza").size());
//         }
//         @Test
//         public void testUndoWithArgs() throws Exception {
//         	//init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";
//             DocumentStore store = new DocumentStoreImpl();
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
//             assertEquals(1, store.search("apple").size());
//             assertEquals(1, store.searchByPrefix("a").size());
//             store.undo(this.uri1);
//             assertEquals(0, store.search("apple").size());
//             assertEquals(0, store.searchByPrefix("a").size());
//         }
//         @Test
//         public void testUndoCommandSet() throws Exception {
//         	//init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";
//             DocumentStore store = new DocumentStoreImpl();
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             assertEquals(2, store.deleteAll("pizza").size());
//             store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
//             assertNotNull(store.getDocument(this.uri3));
//             assertEquals(0, store.search("pizza").size());
//             store.undo(uri1);
//             assertEquals(1, store.search("pizza").size());
//             assertEquals(4, store.search("pizza").get(0).getWords().size());
//             store.undo(uri2);
//             assertEquals(2, store.search("pizza").size());
//             assertEquals(1, store.search("pizza").get(0).getWords().size());
//             store.undo();
//             assertNull(store.getDocument(this.uri3));
//             assertEquals(0, store.search("penguin").size());
//         }
//         @Test
//         public void testUndoCommandSet2() throws Exception {
//         	//init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";
//             System.out.println("Start of the test");
//             DocumentStore store = new DocumentStoreImpl();
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             store.deleteAll("pizza");
//             assertEquals(0, store.search("pizza").size());
//             store.undo(uri2);
//             assertEquals(1, store.search("pizza").size());
//             System.out.println("big call to undo");
//             store.undo(uri2);
//             assertNull(store.getDocument(uri2));
//             assertEquals(0, store.search("pizza").size()); //problem
//             boolean test = false;
//             try {
//                 store.undo(uri2);
//             } catch (IllegalStateException e) {
//                 test = true;
//             }
//             assertTrue(test);
//             assertEquals(0, store.search("pizza").size());
//             store.undo(uri1);
//             assertEquals(1, store.searchByPrefix("app").size());
//             assertEquals(1, store.search("pizza").size());
//         }
//     @Test
//         public void removeCommandSet() throws Exception {
//         	//init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";
//             DocumentStore store = new DocumentStoreImpl();
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             store.deleteAll("pizza");
//             assertEquals(0, store.search("pizza").size());
//             store.undo(uri2);
//             assertEquals(1, store.search("pizza").size());
//             store.undo(uri1);
//             assertEquals(2, store.search("pizza").size());
//             store.undo();
//             assertNull(store.getDocument(uri2)); //problem
//             assertNotNull(store.getDocument(uri1));
//             assertEquals(1, store.search("pizza").size());
//         }
    
	
// }