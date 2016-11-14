/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package HML;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Wing Choi
 * @date Nov 11, 2016
 */
public class Cards {
    
    final int NUMCARDS = 8;
    final int NUMINDECK = 52;
    final int NUMINRUN = 13; // 13
    final boolean noConsecutiveMatchingCards = false;
    
    private int [] input;
    private int [] output;
    private int minCardPos;
    private int minCardVal;
    
    
    // constructor
    public Cards () {
        input = new int[NUMCARDS];
        output = new int[NUMCARDS];
        
        this.populateCards();
        this.verifyCards();
    }
    
    public void populateCards () {
        for (int i=0; i < NUMCARDS; i++) {
            input[i] = (int)(Math.random() * NUMINRUN)+ 1;
            if (i>0 && noConsecutiveMatchingCards) {
                if (input[i] == input[i-1]) {
                    input[i] = (int)(Math.random() * NUMINRUN)+ 1;
                    //continue;
                }
            }
            output[i] = input[i];
            
        }
        calculateMinCard();
    }
    
    public void verifyCards () {
        for (int i=0; i < NUMCARDS; i++) {
            if (input[i] < 1 || input[i] > NUMINRUN) {
                System.out.println("card at index "+i+" is out of range");
            }
        }
    }
    
    public void printOriginalCards () {
        printCards(input);
    }
    public void printResultCards () {
        printCards(output);
    }
    
    public void printCards (int[] in) {
        String o = "";
        for (int i=0; i < NUMCARDS; i++) {
            
            if (in[i] < 11 && in[i] > 1) {
                o = Integer.toString(in[i]);
            } else {
                switch (in[i]) {
                    case 1:  o = "A"; break;
                    case 11: o = "J"; break;
                    case 12: o = "Q"; break;
                    case 13: o = "K"; break;
                }
            }
            System.out.print(o+" ");
        }
        System.out.println("");
    }
    
    public void resetCards () {
        
    }
    
    public int getCard (int pos) {
        if ((pos < NUMCARDS) && (pos >= 0)) {
            return output[pos];
        } else {
            return -1;
        }
    }
    
    public boolean putCard (int pos, int val) {
        if ((pos < NUMCARDS) && (pos >= 0)) {
            output[pos] = val;
            return true;
        } else {
            return false;
        }
    }
    
    public boolean swapCards (int pos1, int pos2) {
        if ((pos1 < NUMCARDS) && (pos1 >= 0) &&
            (pos2 < NUMCARDS) && (pos2 >= 0)) {
            int temp = output[pos1];
            output[pos1] = output[pos2];
            output[pos2] = temp;
            return true;
        } else {
            return false;
        }
    }
    
    public int calculateMinCard () {
        ArrayList<Integer> a;
        a = new ArrayList<Integer>(output.length);
        for (int i = 0; i < output.length; i++) {
            a.add(output[i]);
        }
        minCardVal = Collections.min(a);
        minCardPos = a.indexOf(minCardVal);
        
        return minCardVal;
    }
    public int getMinCardPos () {
        return minCardPos;
    }
    public int getMinCardVal () {
        return minCardVal;
    }
    
    public static void main (String[] args) {
        System.out.println("Initiating Cards");
        Cards c = new Cards();
        
        c.printOriginalCards();
        
    }    
}
