/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Program;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wing Choi
 * @date Nov 11, 2016
 */
public class Program {
    
    ArrayList <String> lines;
    String filename = "program";
    Boolean error = false;
    
    // constructor
    public Program () {
        // initialize
        lines = new ArrayList<String>();
        readInFile();
    }
    public Program (String fn) {
        filename = fn;
        lines = new ArrayList<String>();
        readInFile();
    }
    
    public void readInFile() {
        Path path;
        try {
            path = Paths.get(".",filename);
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
            return;
        }
        try {
            lines = new ArrayList<String>(Files.readAllLines(path));
        } catch (Exception e) {
            e.printStackTrace();
            error = true;
            return;
        }
        
    }
    
    public void printProgram () {
        lines.forEach(line->System.out.println(line));
    }
    
    public Boolean getStatus() {
        return error;
    }
    
    public List getLines() {
        return lines;
    }
    
    public static void main (String[] args) {
        Program p = new Program();
        
        if (p.getStatus()) {
            System.out.println("Program load resulted in error status");
        } else {
            System.out.println("Program loaded ok");
        }
        
        p.printProgram();
    }
    
    
}
