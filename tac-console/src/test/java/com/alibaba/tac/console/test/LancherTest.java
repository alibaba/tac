package com.alibaba.tac.console.test;

import com.alibaba.tac.engine.bootlaucher.BootJarLaucherUtils;
import org.junit.Test;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.data.RandomAccessData;
import org.springframework.boot.loader.jar.JarFile;

import java.io.*;
import java.net.URI;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;

/**
 * @author jinshuan.li 08/03/2018 17:58
 */
public class LancherTest {

    private static final int BUFFER_SIZE = 32 * 1024;

    private JarFileArchive archive;

    private JarFile jarFile;

    static final String BOOT_INF_CLASSES = "BOOT-INF/classes/";

    static final String BOOT_INF_LIB = "BOOT-INF/lib/";

    private File tempUnpackFolder;

    String file="/Users/jinshuan.li/Source/open-tac/tac-console/target/tac-console-0.0.1-SNAPSHOT.jar";

    @Test
    public void test() throws IOException {


        JarFileArchive jarFileArchive=new JarFileArchive(new File(file));
        this.jarFile = new JarFile(new File(file));
        this.archive=jarFileArchive;
        List<Archive> archives = new ArrayList<Archive>(
            this.archive.getNestedArchives(new Archive.EntryFilter() {

                @Override
                public boolean matches(Archive.Entry entry) {
                    return isNestedArchive(entry);
                }

            }));

        for (Archive entries : archives) {
            JarFileArchive archive=(JarFileArchive)entries;

        }


    }

    @Test
    public void test1() throws IOException {



        this.jarFile=new JarFile(new File(file));

        Enumeration<JarEntry> entries = this.jarFile.entries();
        while (entries.hasMoreElements()){

            JarEntry jarEntry = entries.nextElement();
            if (jarEntry.getName().contains(".jar")){
                getUnpackedNestedArchive(jarEntry);
            }
            System.out.println(jarEntry);
        }
    }

    @Test
    public void test3() throws Exception {

        BootJarLaucherUtils.unpackBootLibs(new JarFile(new File(file)));
    }
    protected final Archive createArchive() throws Exception {
        ProtectionDomain protectionDomain = getClass().getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
        String path = (location == null ? null : location.getSchemeSpecificPart());
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }
        File root = new File(path);
        if (!root.exists()) {
            throw new IllegalStateException(
                "Unable to determine code source archive from " + root);
        }
        return (root.isDirectory() ? new ExplodedArchive(root)
            : new JarFileArchive(root));
    }

    protected boolean isNestedArchive(Archive.Entry entry) {
        System.out.println(entry.getName());
        if (entry.isDirectory()) {
            return entry.getName().equals(BOOT_INF_CLASSES);
        }
        return entry.getName().startsWith(BOOT_INF_LIB);
    }

    private Archive getUnpackedNestedArchive(JarEntry jarEntry) throws IOException {
        String name = jarEntry.getName();
        if (name.lastIndexOf("/") != -1) {
            name = name.substring(name.lastIndexOf("/") + 1);
        }
        File file = new File(getTempUnpackFolder(), name);
        if (!file.exists() || file.length() != jarEntry.getSize()) {
            unpack(jarEntry, file);
        }
        return new JarFileArchive(file, file.toURI().toURL());
    }
    private File getTempUnpackFolder() {
        if (this.tempUnpackFolder == null) {
            File tempFolder = new File(System.getProperty("java.io.tmpdir"));
            this.tempUnpackFolder = createUnpackFolder(tempFolder);
        }
        return this.tempUnpackFolder;
    }

    private File createUnpackFolder(File parent) {
        int attempts = 0;
        while (attempts++ < 1000) {
            String fileName = new File(this.jarFile.getName()).getName();
            File unpackFolder = new File(parent,
                fileName + "-spring-boot-libs-" + UUID.randomUUID());
            if (unpackFolder.mkdirs()) {
                return unpackFolder;
            }
        }
        throw new IllegalStateException(
            "Failed to create unpack folder in directory '" + parent + "'");
    }

    private void unpack(JarEntry entry, File file) throws IOException {
        InputStream inputStream = this.jarFile.getInputStream(entry, RandomAccessData.ResourceAccess.ONCE);
        try {
            OutputStream outputStream = new FileOutputStream(file);
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
            }
            finally {
                outputStream.close();
            }
        }
        finally {
            inputStream.close();
        }
    }

}
