package edu.yu.cs.com1320.project.stage5.impl;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import com.google.gson.JsonSerializer;
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
import java.io.File;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;
public class FailedTests {
    private File baseDir = new File(System.getProperty("user.dir"));
    private String URItoFile(URI uri){
        String toFile = uri.toString().replace("http://", "");
        String path = toFile + ".json";
        return path;
    }
    @Test
    public void deleteAllWithPrefixTest(){

    }

    @Test
    public void stage3UndoByURIThatImpactsEarlierThanLast(){

    }
}
