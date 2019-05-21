/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.nps.moves.opendis.reference.jargenerator;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Main.java created on May 21, 2019 MOVES Institute Naval Postgraduate School, Monterey, CA, USA www.nps.edu
 *
 * @author Mike Bailey, jmbailey@nps.edu
 * @version $Id$
 */
public class Main
{
    private JarOutputStream jarstream;
    private File basedir;
    private Path basedirpath;
    private String[] paths;

    public Main(JarOutputStream jarstream, File basedir, String... paths)
    {
        this.jarstream = jarstream;
        this.basedir = basedir;
        this.basedirpath = basedir.toPath();
        this.paths = paths;
    }

    public void run() throws Exception
    {
        for (String path : paths) {
            File source = new File(basedir,path);
 
            if(!source.exists())
                throw new RuntimeException("No source at "+source.getAbsolutePath());
            add(source);
        }
        jarstream.close();
    }

    private void add(File source) throws Exception
    {
        String relativePath = basedirpath.relativize(source.toPath()).toString().replace("\\", "/");
        if (source.isDirectory()) {
            
            String name = relativePath;
            if (!name.isEmpty()) {
                if (!name.endsWith("/"))
                    name += "/";
                JarEntry entry = new JarEntry(name);
                entry.setTime(source.lastModified());
                jarstream.putNextEntry(entry);
                jarstream.closeEntry();
                System.out.println(name);
            }
            for (File nestedFile : source.listFiles()) {
                add(nestedFile);
            }
            return;
        }

        JarEntry entry = new JarEntry(relativePath);
        entry.setTime(source.lastModified());
        jarstream.putNextEntry(entry);
        
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));

        byte[] buffer = new byte[1024];
        while (true) {
            int count = in.read(buffer);
            if (count == -1)
                break;
            jarstream.write(buffer, 0, count);
        }
        jarstream.closeEntry();

        if (in != null)
            in.close();
        System.out.println(relativePath);
    }

    // 0 jarstream
    // 1 base directory path
    // 2... individual paths or "-f filelist"
    public static void main(String[] args)
    {
        Manifest manifest = new Manifest();
        Attributes mainAtts = manifest.getMainAttributes();
        mainAtts.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        mainAtts.putValue("Created-By", "Unified Open DIS, on github.com");

        //mainAtts.put(Attributes.Name.SPECIFICATION_TITLE, "todo");
        //mainAtts.put(Attributes.Name.SPECIFICATION_VENDOR, "todo");
        //mainAtts.put(Attributes.Name.SPECIFICATION_VERSION, "todo");
        JarOutputStream target;
        File basedir;
        try {
            target = new JarOutputStream(new FileOutputStream(args[0]), manifest);
            basedir = new File(args[1]).getAbsoluteFile();
            if (!basedir.exists() || !basedir.isDirectory())
                throw new RuntimeException("Missing base directory");
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        try {
            Main main;
            if(args[2].equals("-f"))
                main = new Main(target, basedir, buildPathList(args[3]));
            else
                main = new Main(target, basedir, Arrays.copyOfRange(args, 2, args.length));
            main.run();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static String[] buildPathList(String filepath) throws IOException
    {
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line = null;
        ArrayList<String> arLis = new ArrayList<>();
        while((line = br.readLine()) != null)
            arLis.add(line);
        
        return arLis.toArray(new String[]{});
    }
}
