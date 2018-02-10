package com.stericson.RootToolsTests;

import com.stericson.RootTools.containers.RootClass.Candidate;
import com.stericson.RootTools.containers.RootClass.RootArgs;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Candidate
public class NativeJavaClass {
    public NativeJavaClass(RootArgs args) {
        System.out.println("NativeJavaClass says: oh hi there.");
        String p = "/data/data/com.android.browser/cache";
        String[] fl = new File(p).list();
        if (fl != null) {
            System.out.println("Look at all the stuff in your browser's cache:");
            for (String af : fl) {
                System.out.println("-" + af);
            }
            System.out.println("Leaving my mark for posterity...");
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(new File(p + "/roottools_was_here")));
                out.write("This is just a file created using RootTool's Sanity check tools..\n");
                out.close();
                System.out.println("Done!");
            } catch (IOException e) {
                System.out.println("...and I failed miserably.");
                e.printStackTrace();
            }
        }
    }
}
