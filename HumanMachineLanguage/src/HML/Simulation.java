/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package HML;
import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
import java.util.LinkedHashMap;
import Program.Program;

/**
 *
 * @author Wing Choi
 * @date Nov 11, 2016
 */
public class Simulation {
    
    final static int NUMSIMS = 500;
    
    public static void main (String[] args) {
        HumanMachine hms[] = new HumanMachine[NUMSIMS];
        Cards c = new Cards();
        Program p = new Program();
        HumanMachine hm = new HumanMachine();
        ArrayList<LinkedHashMap<String,String>> t = hm.getParsedTokens();
        boolean status[] = new boolean[NUMSIMS];
        boolean minCardStatus[] = new boolean[NUMSIMS];
        boolean minToFrontStatus[] = new boolean[NUMSIMS];
        boolean error = false;
        
        for (int i=0;i<500; i++) {
            //if (error) break;
            System.out.println("\n\n==================================\n\n");
            
            status[i] = true;
            
            if (i == 0) {
                
                hms[i] = new HumanMachine();
                hm = hms[i];
                
                c = hm.getCards();
                p = hm.getProgram();

                c.printOriginalCards();
                p.printProgram();

                hm.checkSyntaxAndParse();
                if (hm.getErrorState()) {
                    System.out.println("Human Machine has errored");
                }

                System.out.println("Tokenized program");
                hm.printTokens();

                if (hm.getErrorState()) {
                    System.out.println("Human Machine has errored");
                    status[i] = false;
                    break;
                }
                
                t = hm.getParsedTokens();
                
                System.out.println("\n\n==================================\n\n");
                
            } else {
                hms[i] = new HumanMachine(t,p);
                hm = hms[i];
                c = hm.getCards();
            }

            System.out.println("Execute program");
            hm.execute();

            c.printOriginalCards();
            c.printResultCards();
            System.out.println("Left Hand is at index  "+hm.getLeftHandPosition());
            System.out.println("Right Hand is at index "+hm.getRightHandPosition());
            if (c.getCard(hm.getLeftHandPosition()) == c.getMinCardVal()) {
                System.out.println("Find Min Card successful for sim# "+i);
                minCardStatus[i] = true;
            } else {
                System.out.println("Find Min Card FAILED for sim# "+i);
                error = true;
                minCardStatus[i] = false;
            }
            
            if (c.getCard(0) == c.getMinCardVal()) {
                System.out.println("Find Min to Front successful for sim# "+i);
                minToFrontStatus[i] = true;
            } else {
                System.out.println("Find Min to Front FAILED for sim# "+i);
                minToFrontStatus[i] = false;
            }
            
            if (hm.getErrorState()) {
                System.out.println("Execution of sim# "+i+" resulted in error");
                status[i] = false;
                error = true;
            }
        }
        
        System.out.println("\n\n==================================\n\n");
        System.out.println("\n\n==================================\n\n");
        
        int num=0;
        for (int i=0; i<status.length; i++ ) {
            if (!status[i]) {
                num++;
            }
        }
        int minCardErrNum=0;
        for (int i=0; i<minCardStatus.length; i++ ) {
            if (!minCardStatus[i]) {
                minCardErrNum++;
            }
        }
        System.out.println(minCardErrNum+" out of "+status.length+" executions did not find Min Card");
        
        int minToFrontErrNum=0;
        for (int i=0; i<minToFrontStatus.length; i++ ) {
            if (!minToFrontStatus[i]) {
                minToFrontErrNum++;
            }
        }   
        System.out.println(minToFrontErrNum+" out of "+status.length+" executions did not find Min To Front");
        
        if (error) {
            System.out.println("There were some failures");
            System.out.println(num+" out of "+status.length+" executions resulted in error");
            
        } else {
            System.out.println("Simulation was completed successfully");
        }
        
    }
}
