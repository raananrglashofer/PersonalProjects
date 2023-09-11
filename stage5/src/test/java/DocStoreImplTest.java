package edu.yu.cs.com1320.project.stage5.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.Comparator;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class DocStoreImplTest {

    @Test
    public void setAndGetLastUseTimeTest() throws IOException {
        URI uri1 = URI.create("raanan.com");
        String txt1 = "raanan went down the road";
        Map<String, Integer> map = new HashMap<>();
        DocumentImpl doc = new DocumentImpl(uri1, txt1, map);
        assertEquals(doc.getLastUseTime(), 0);
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(txt1.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = URI.create("glashofer.com");
        String txt2 = "raanan did not go down the road. Instead, he went up the road";
        store.put(new ByteArrayInputStream(txt2.getBytes()), uri2, DocumentStore.DocumentFormat.TXT);
        assertTrue(store.get(uri1).getLastUseTime() > 0);
    }

    @Test
    public void checkIfHeapIsWorkingTest() throws IOException {
        URI uri1 = URI.create("raanan.com");
        String txt1 = "raanan went down the road";
        Map<String, Integer> map = new HashMap<>();
        DocumentImpl doc1 = new DocumentImpl(uri1, txt1, map);
        assertEquals(doc1.getLastUseTime(), 0);
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(txt1.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = URI.create("glashofer.com");
        String txt2 = "raanan did not go down the road. Instead, he went up the road";
        store.put(new ByteArrayInputStream(txt2.getBytes()), uri2, DocumentStore.DocumentFormat.TXT);
        assertTrue(store.get(uri1).getLastUseTime() > 0);
        store.get(uri2);
        assertTrue(store.get(uri1).getLastUseTime() < store.get(uri2).getLastUseTime());
    }

    @Test
    public void changeNanoTimeofDoc1Test() throws IOException {
        URI uri1 = URI.create("raanan.com");
        String txt1 = "raanan went down the road";
        Map<String, Integer> map = new HashMap<>();
        DocumentImpl doc = new DocumentImpl(uri1, txt1, map);
        assertEquals(doc.getLastUseTime(), 0);
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(txt1.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = URI.create("glashofer.com");
        String txt2 = "raanan did not go down the road. Instead, he went up the road";
        store.put(new ByteArrayInputStream(txt2.getBytes()), uri2, DocumentStore.DocumentFormat.TXT);
        long uri1Time = store.get(uri1).getLastUseTime();
        long uri2Time = store.get(uri2).getLastUseTime();
        assertTrue(uri1Time > 0);
        assertTrue(uri2Time > 0);
        assertTrue(uri1Time < uri2Time);
        long updatedUri2 = store.get(uri2).getLastUseTime();
        long updatedUri1 = store.get(uri1).getLastUseTime();
        assertTrue(uri1Time < updatedUri1);
        assertTrue(uri2Time < updatedUri2);
        assertFalse(updatedUri1 < updatedUri2);
    }

    @Test
    public void setDocCountLimitAndPassItWithPut() throws IOException {
        URI uri1 = URI.create("raanan.com");
        String txt1 = "raanan went down the road.";
        DocumentStore store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(txt1.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = URI.create("glashofer.com");
        String txt2 = "he did not go down the road. Instead, he went up the road";
        store.put(new ByteArrayInputStream(txt2.getBytes()), uri2, DocumentStore.DocumentFormat.TXT);
        assertNotNull(store.get(uri1));
        assertNotNull(store.get(uri2));
        store.setMaxDocumentCount(2);
        URI uri3 = URI.create("rachamim.com");
        String txt3 = "it's lit!";
        store.put(new ByteArrayInputStream(txt3.getBytes()), uri3, DocumentStore.DocumentFormat.TXT);
        assertNull(store.get(uri1));
        assertNotNull(store.get(uri3));
    }
    @Test
    public void setDocCountLimitAndPassItWithUndo() throws IOException {
        URI uri1 = URI.create("raanan.com");
        String txt1 = "raanan went down the road.";
        DocumentStoreImpl store = new DocumentStoreImpl();
        store.put(new ByteArrayInputStream(txt1.getBytes()), uri1, DocumentStore.DocumentFormat.TXT);
        URI uri2 = URI.create("glashofer.com");
        String txt2 = "he did not go down the road. Instead, he went up the road";
        store.put(new ByteArrayInputStream(txt2.getBytes()), uri2, DocumentStore.DocumentFormat.TXT);
        URI uri3 = URI.create("rachamim.com");
        String txt3 = "it's lit!";
        store.put(new ByteArrayInputStream(txt3.getBytes()), uri3, DocumentStore.DocumentFormat.TXT);
        assertNotNull(store.get(uri3));
        store.delete(uri3);
        assertNull(store.get(uri3));
        store.setMaxDocumentCount(2);
        store.undo();
        assertNull(store.get(uri1));
        assertNotNull(store.get(uri3));
    }
}