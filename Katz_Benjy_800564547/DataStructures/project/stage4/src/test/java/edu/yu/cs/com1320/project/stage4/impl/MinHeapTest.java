// package edu.yu.cs.com1320.project.impl.test;
// import edu.yu.cs.com1320.project.*;
// import edu.yu.cs.com1320.project.impl.*;
// import static org.junit.Assert.*;
// import org.junit.Test;
// import java.net.URI;
// import java.net.URI.*;
// import java.io.*;
// import java.net.URISyntaxException;
// import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;
// import edu.yu.cs.com1320.project.stage4.DocumentStore;

// public class MinHeapTest{
// 	//variables to hold possible values for doc1
//         private URI uri1;
//         private String txt1;
    
//         //variables to hold possible values for doc2
//         private URI uri2;
//         String txt2;

//         private URI uri3;
//         String txt3;
// 	@Test
// 	public void SimpleHeapTest(){
// 		MinHeap<Integer> myMinHeap = new MinHeapImpl<Integer>();
// 		for(int i =0; i<300; i++){
// 			myMinHeap.insert((Integer)i+5);
// 		}
// 		myMinHeap.insert((Integer)2);
// 		assertEquals((Integer)2, myMinHeap.remove());

// 	}
// 	class Entry implements Comparable<Entry>{
// 				private int data;
// 				public Entry(int i){
// 					this.data = i;
// 				}
// 				public int getData(){
// 					return this.data;
// 				}
// 				public void setData(int i){
// 					this.data = i;
// 				}
// 				@Override 
// 				public int compareTo(Entry o){
// 					return this.data - ((Entry)o).getData();
// 				}
// 				@Override 
// 				public boolean equals(Object o){
// 					if(!(o instanceof Entry)) return false;
// 					return (this.data == ((Entry)o).getData());
// 				}
// 			}
// 	@Test
// 	public void SimpleReHeapTest(){
			 
// 		MinHeap<Entry> myMinHeap = new MinHeapImpl<Entry>();
// 			Entry change = new Entry(75);
// 			for(int i =70; i<91; i++){		
// 				if(i != 85|| i != 75)myMinHeap.insert(new Entry(i));
// 			}
// 			System.out.println(myMinHeap.remove().getData());
// 			//assertEquals((Integer)5, myMinHeap.remove());
// 			myMinHeap.insert(change);
// 			System.out.println(myMinHeap.remove());
// 			//assertEquals((Integer)3, myMinHeap.remove());
// 			myMinHeap.insert(change);
// 			change.setData(85);
// 			//System.out.println(myMinHeap.remove().getData());
// 			myMinHeap.reHeapify(change);
// 			System.out.println(myMinHeap.remove().getData());
// 			//assertEquals((Integer)6, myMinHeap.remove());

// 	}
// 	@Test
// 	public void DocStoreTest()throws Exception{

// 		//init possible values for doc1
//             this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
//             this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
//             //init possible values for doc2
//             this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
//             this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

//             //init possible values for doc3
//             this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
//             this.txt3 = "Penguin Park Piccalo Pants Pain Possum";

//             DocumentStore store = new DocumentStoreImpl();
//             store.setMaxDocumentCount(3);
//             store.setMaxDocumentBytes(70);
//             System.out.println("putDocument being called");
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
//             store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
            
//             System.out.println("Start now");
//             System.out.println("Amount of bytes in 2 and 3: "+(this.txt3.getBytes().length + this.txt2.getBytes().length));
//             assertEquals(0, store.search("PiE").size());
//             //System.out.println("prefix p: "+store.searchByPrefix("p"));
//             assertEquals(2, store.searchByPrefix("p").size());
//             store.setMaxDocumentCount(3);
//             store.setMaxDocumentBytes(400);
//             store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
//             assertEquals(3, store.searchByPrefix("p").size());
//             //System.out.println("Lets find pizza before deleteing" + store.search("pizza"));
//             store.deleteAllWithPrefix("piz");
//             assertEquals(1, store.searchByPrefix("p").size());
//             System.out.println("Let the undoing begin");
//             System.out.println("Lets find park before undoing" + store.search("park"));
//             store.undo();
//             System.out.println("Lets find park" + store.search("park"));
//             assertEquals(3, store.searchByPrefix("p").size());
//             //store.setMaxDocumentBytes(50);
//             store.setMaxDocumentBytes(50);
//             assertEquals(1, store.searchByPrefix("p").size());
			       




        	
// 	}
	
// }

// package edu.yu.cs.com1320.project.stage4.impl;

// import org.junit.jupiter.api.Test;

// import java.lang.reflect.Field;
// import java.lang.reflect.Method;
// import java.lang.reflect.Modifier;
// import java.net.URI;
// import java.net.URISyntaxException;
// import java.util.HashSet;

// import static org.junit.jupiter.api.Assertions.assertTrue;

// public class MinHeapTest {


//     @Test
//     public void interfaceCount() {//tests that the class only implements the required interfaces
//         @SuppressWarnings("rawtypes")
//         Class[] classes = DocumentImpl.class.getInterfaces();
//         assertTrue(classes.length == 1, "length of classes is " + classes.length);
//         assertTrue(classes[0].getName().equals("edu.yu.cs.com1320.project.stage4.Document"));
//     }

//     @Test
//     public void methodCount() {//need only test for non constructors
//         Method[] methods = DocumentImpl.class.getDeclaredMethods();
//         int publicMethodCount = 0;
//         String names = "";
//         for (Method method : methods) {
//             if (Modifier.isPublic(method.getModifiers())) {
//                 names += method.getName() + "\n";
//                 if(!method.getName().equals("equals") && !method.getName().equals("hashCode") && !method.getName().equals("compareTo")) {
//                     publicMethodCount++;
//                 }
//             }
//         }
//         assertTrue(publicMethodCount == 7, "method count is " + publicMethodCount + " names are " + names);
//     }

//     //STAGE 4 tests
//     @Test
//     public void stage4GetLastUseTimeExists()throws URISyntaxException {
//         URI uri = new URI("https://this.com");
//         try {
//             new DocumentImpl(uri, "hi").getLastUseTime();
//         } catch (RuntimeException e) {}
//     }
//     @Test
//     public void stage4SetLastUseTimeExists()throws URISyntaxException {
//         URI uri = new URI("https://this.com");
//         try {
//             new DocumentImpl(uri, "hi").setLastUseTime(100);
//         } catch (RuntimeException e) {}
//     }

//     //STAGE 3 tests
//     @Test
//     public void stage3WordCountExists() throws URISyntaxException {
//         URI uri = new URI("https://this.com");
//         try {
//             new DocumentImpl(uri, "hi").wordCount("hi");
//         } catch (RuntimeException e) {}
//     }


//     //stage 1 tests
//         @Test
//     public void fieldCount() {
//         Field[] fields = DocumentImpl.class.getFields();
//         int publicFieldCount = 0;
//         for (Field field : fields) {
//             if (Modifier.isPublic(field.getModifiers())) {
//                 publicFieldCount++;
//             }
//         }
//         assertTrue(publicFieldCount == 0);
//     }

//     @Test
//     public void subClassCount() {
//         @SuppressWarnings("rawtypes")
//         Class[] classes = DocumentImpl.class.getClasses();
//         assertTrue(classes.length == 0);
//     }

//     @Test
//     public void constructor1Exists() throws URISyntaxException {
//         URI uri = new URI("https://this.com");
//         try {
//             new DocumentImpl(uri, "hi");
//         } catch (RuntimeException e) {}
//     }

//     @Test
//     public void constructor2Exists() throws URISyntaxException {
//         URI uri = new URI("https://this.com");
//         byte[] ary = {0,0,0};
//         try {
//             new DocumentImpl(uri, ary );
//         } catch (RuntimeException e) {}
//     }

//     @Test
//     public void getDocumentBinaryDataExists() throws URISyntaxException{
//         URI uri = new URI("https://this.com");
//         try {
//             new DocumentImpl(uri, "hi".getBytes()).getDocumentBinaryData();
//         } catch (RuntimeException e) {}
//     }

//     @Test
//     public void getDocumentTxtExists() throws URISyntaxException{
//         URI uri = new URI("https://this.com");
//         try {
//             new DocumentImpl(uri, "hi").getDocumentTxt();
//         } catch (RuntimeException e) {}
//     }

//     @Test
//     public void getKeyExists() throws URISyntaxException {
//         URI uri = new URI("https://this.com");
//         try {
//             new DocumentImpl(uri, "hi").getKey();
//         } catch (RuntimeException e) {}
//     }

// }