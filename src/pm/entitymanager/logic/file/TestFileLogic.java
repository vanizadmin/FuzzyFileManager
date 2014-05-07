/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pm.entitymanager.logic.file;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private PathNameSanitization path;
    public static void initialize() throws IOException {
        fileSystemsRoot = new FileSystemsRoot();
        currentDir = fileSystemsRoot;
        currentPath = Paths.get(currentDir.getName()).toString();
        
        System.out.println("CurrentPath: " + currentPath);
        System.out.println("CurrentPath alias: " + currentDir.getAlias());
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
        
        for (AbstractFile file : dirContents) {
            String fileName = file.getFullName();
            String fileType = (file.isFolderish() ? "D" : "F");
            String permissions = (file.isReadable() ? "R" : "-");
            permissions += (file.isWritable() ? "W" : "-");
            Date date = file.getLastModified();
            
            String fmpath = ""; //new PathNameSanitization(null).getFullPath(fullPath)
        //    System.out.println("Full Path: " + path.getFullPath());
            System.out.printf("\t %s\n%s\t\t\t\t %-2s %s %12.1f \t%-27s \t%s%n", fmpath, fileName, fileType, permissions, file.getSize() /1024.0, date.toString() , file.getAlias());        
        } 
    }
    public static void displayPathAndContents() throws EntityNotFolderishException {
        clearConsole();
        System.out.println("Path:" + currentPath);        
        List<AbstractFile> dirContents = currentDir.getAllChildren();   
        displayDirContents(dirContents);
    }
    public static void moveToParent() {
        if (currentDir.getParent() != null) {
         
            currentDir = currentDir.getParent();
            System.out.println("currentDir: "+ currentDir.getName());
            
            Path parentPath=Paths.get(currentPath).getParent();

           if((Paths.get(currentPath).getParent())==null && (currentPath.equals(fileSystemsRoot.getAlias())))  {
               System.out.println("Entered ");
               currentPath = "";
           
           }
            System.out.println("currPath: " + currentPath);
            if((Paths.get(currentPath).getParent())==null){ 
                System.out.println("Entered ");
                currentPath=currentDir.getName();
                System.out.println("currPath: " + currentPath);
                        }



        }
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
        if(isRoot) for (AbstractFile anEntity : children) {
            selection= new PathNameSanitization(selection).getOnlyLetter();
             System.out.println("selection San: " + selection);
            if (anEntity.getCharNameOnly().equals(selection= new PathNameSanitization(selection).getOnlyLetter()) && anEntity.isFolderish()) {
                selectedDir =  anEntity;
                break;
            }
            String drive;
           //TODO: get root drive
       // --->   path.getFullPath(drive= new PathNameSanitization(selection).getDrive());
        System.out.println("cu Path: " + selection);
        }
        if(!isRoot) for (AbstractFile anEntity : children) {
            if (anEntity.getFullName().equals(selection= new PathNameSanitization(selection).getDirectoryName()) && anEntity.isFolderish()) {
                selectedDir =  anEntity;
                break;
            }
            String folder;
            //TODO: get current folder
     // --->       path.getFullPath(folder= new PathNameSanitization(selection).getDirectoryName());
        }
        if(selectedDir==null) System.out.println("selected dir null");
        System.out.println("currentPath: " + currentPath);
        if (selectedDir != null) {
            currentDir = selectedDir;
          //  currentDir
            System.out.println("currentDir name: " + currentDir.getFullName().toString());
            System.out.println("currentPath: " + currentPath);
            if(!(currentPath.equals(currentDir.getName())))    currentPath = Paths.get( currentDir.getFullName()).toString();
            else Paths.get(currentDir.getFullName(),currentDir.getFullName()).toString();
            System.out.println("currentPath: " + currentPath);
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
            Boolean isRoot=true;
            do {
                if (selection.equals("..")) {
                    moveToParent();
                    isRoot=true;
                } else {
                    if(!isRoot) moveToChild(selection= new PathNameSanitization(selection).getDirectoryName() , false);
                    if(isRoot) moveToChild(selection= new PathNameSanitization(selection).getDrive(), true);
                    isRoot=false;
                }
                displayPathAndContents();
                System.out.print("Select a directory ('..' to go up one level,'.' for exit):");
                selection = consoleInput.nextLine();
                
            }
            while (!selection.equals("."));
        } catch (EntityNotFolderishException ex) {
            Logger.getLogger(TestFileLogic.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
