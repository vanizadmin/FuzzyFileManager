/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm.entitymanager.logic.file;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pm.entitymanager.logic.EntityNotFolderishException;
/**
 *
 * @author grinias
 */
public class TestFileLogic  {    
    private static AbstractFile fileSystemsRoot;
    private static AbstractFile currentDir;
    private static String currentPath;
    private static String fullPath;
    private static List<String> cFullPath; //Storing the full path that the user is currebtly in including the root folder
    public static void initialize() throws IOException {
        fileSystemsRoot = new FileSystemsRoot();
        currentDir = fileSystemsRoot;
        currentPath = Paths.get(currentDir.getName()).toString();
        fullPath = new String();
        System.out.println("CurrentPath: " + currentPath);
        System.out.println("CurrentPath alias: " + currentDir.getAlias());
        cFullPath = new ArrayList<>();        
    }
    private static void clearConsole() {
        try {
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException ex) {

        }
    }
    public static void displayDirContents(List<AbstractFile> dirContents) throws EntityNotFolderishException {
        
        dirContents.stream().forEach((file) -> {
            String fileName = file.getFullName();
            String fileType = (file.isFolderish() ? "D" : "F");
            String permissions = (file.isReadable() ? "R" : "-");
            permissions += (file.isWritable() ? "W" : "-");
            Date date = file.getLastModified();
            System.out.printf("\t %s\t\t\t\t %-2s %s %12.1f \t%-27s \t%s%n", fileName, fileType, permissions, file.getSize() /1024.0, date.toString() , file.getAlias());
        }); 
    }
    public static void displayPathAndContents() throws EntityNotFolderishException {
        clearConsole();
        System.out.println("Path:" + currentPath); 
        System.out.println("FullPath: " + fullPath);
        System.out.println("FullPath auto print List: " + cFullPath.toString());
        cFullPath.stream().forEach((file) -> {
            System.out.printf("%s", file);
        });
        System.out.printf("\n");
        List<AbstractFile> dirContents = currentDir.getAllChildren();   
        displayDirContents(dirContents);
    }
    public static boolean moveToParent() {        
        if (currentDir.getParent() != null) {         
            currentDir = currentDir.getParent();
           if((Paths.get(currentPath).getParent())==null && (currentPath.equals(fileSystemsRoot.getAlias())))  {
               currentPath = "";
               fullPath="";               
               cFullPath.remove(cFullPath.size() - 1);                
           }
            System.out.println("currPath: " + currentPath);
            if((Paths.get(currentPath).getParent())==null) {
                currentPath=currentDir.getName();
                cFullPath.remove(cFullPath.size() - 1);
                fullPath = fullPath.replace(currentPath, "");                        
            }

        }
        if(currentDir.getAlias().equals(fileSystemsRoot.getAlias())) return(true);
            else return(false);
    }
public static String dm(String md5) {
   try {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] array = md.digest(md5.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
       }
        return sb.toString();
    } catch (java.security.NoSuchAlgorithmException e) {
    }
    return null;
}
    public static void moveToChild(String selection, Boolean isRoot) throws EntityNotFolderishException {        
        List<AbstractFile> children = currentDir.getAllChildren();
        AbstractFile selectedDir = null;
        System.out.println("selection: " + selection);
        if(isRoot) {
            
            for (AbstractFile anEntity : children) {
                selection= new PathNameSanitization(selection).getOnlyLetter();            
                if (anEntity.getCharNameOnly().equals(selection= new PathNameSanitization(selection).getOnlyLetter()) && anEntity.isFolderish()) {
                    selectedDir =  anEntity;
                    break;                    
            }
        }
            String drive;
            drive= new PathNameSanitization(selection).getDrive();
            System.out.println("Drive: " + drive);
            PathNameSanitization n;
            n = new PathNameSanitization(selection);
            drive= n.getOnlyLetter();
            System.out.println("Letter: " + drive);
            fullPath=drive;        
            cFullPath.add(drive);
    }
        if(!isRoot) { for (AbstractFile anEntity : children) {
            if (anEntity.getFullName().equals(selection= new PathNameSanitization(selection).getDirectoryName()) && anEntity.isFolderish()) {
                selectedDir =  anEntity;
                break;
            }
        }            
            fullPath+=  new PathNameSanitization(selection).getDirectoryName();
            String directory = "\\" + new PathNameSanitization(selection).getDirectoryName();            
            cFullPath.add(directory);
        }
        if(selectedDir==null) System.out.println("selected dir null");
       
        if (selectedDir != null) {
            currentDir = selectedDir;
            if(!(currentPath.equals(currentDir.getName())))    currentPath = Paths.get( currentDir.getFullName()).toString();
            else Paths.get(currentDir.getFullName(),currentDir.getFullName()).toString();
        }        
    }
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {

        try {
            Scanner consoleInput = new Scanner(System.in);
            String selection;
            initialize();            
            displayPathAndContents();
            System.out.print("Select a directory ('..' to go up one level,'.' for exit):");
            selection = consoleInput.nextLine();            
            System.out.println("Selection " + selection);
            Boolean isRoot=true; // is inside a root child ?
            do {
                if (selection.equals("..")) {
                    isRoot = moveToParent();                   
                } else {
                    if(!isRoot) moveToChild(selection= new PathNameSanitization(selection).getDirectoryName() , false);
                    if(isRoot) {
                        moveToChild(selection= new PathNameSanitization(selection).getDrive(), true);
                        isRoot=false; 
                    }
                }
                displayPathAndContents();
                System.out.print("Select a directory ('..' to go up one level,'.' for exit):");
                selection = consoleInput.nextLine();                
            }
            while (!selection.equals("."));
        } catch (EntityNotFolderishException ex) {
            Logger.getLogger(TestFileLogic.class.getName()).log(Level.SEVERE, null, ex);
        }
        PathNameSanitization snt= new PathNameSanitization("nssss324232");
        String l = snt.getOnlyLetter();
        
        System.out.println(l);
        System.out.println(l.substring(0, 1).toUpperCase());
    }
}
