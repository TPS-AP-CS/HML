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
public class HumanMachine {
    
    private Cards c;
    private Program p;
    private ArrayList<LinkedHashMap<String,String>> t; // parsed tokens
    private boolean error;
    
    private int leftHand;
    private int rightHand;
    final int MAXSTEP = 8885;
    
    
    // constructor
    public HumanMachine () {
        c = new Cards();
        p = new Program();
        t = new ArrayList<>(); //LinkedHashMap<String,String>();
        error = false;
        leftHand = 0;
        rightHand = 1;
        
        if (p.getStatus()) {
            System.out.println("Program loaded with error status");
        } else {
            System.out.println("Program loaded ok");
        }
        
    }
    public HumanMachine (ArrayList<LinkedHashMap<String,String>> t, Program p) {
        c = new Cards();
        this.p = p;
        this.t = t;  //LinkedHashMap<String,String>();
        error = false;
        leftHand = 0;
        rightHand = 1;
        
    }
    
    public boolean getErrorState() {
        return error;
    }
    
    public Cards getCards() {
        return c;
    }
    
    public Program getProgram() {
        return p;
    }
    
    public int getLeftHandPosition() {
        return leftHand;
    }
    public int getRightHandPosition() {
        return rightHand;
    }
    public ArrayList<LinkedHashMap<String,String>> getParsedTokens () {
        return t;
    }
    /*
    HML in U3L2:
        SHIFT hand TO THE dir
        MOVE hand TO POSITION num
        JUMP TO LINE num
        JUMP TO LINE num IF num comp num
        STOP
    hands: LH RH
    dir: L R
    values: LHPos RHPos LHCard RHCard
    
    HML in U3L3:
        SWAP
    */
    public void checkSyntaxAndParse() {
        ArrayList<String> lines = new ArrayList<>(p.getLines());
        
        int l = 0;
        
        for (String line : lines ) {
            l++;
            LinkedHashMap<String,String> m = parseLine(line,l);
            String s = m.get("OP");
            if (!s.equalsIgnoreCase("INVALID")) {
                t.add(m);
            } else {
                // Errors, 
                error = true;
                //return;
            }
        }
        
    }
    
    public LinkedHashMap<String,String> parseLine(String in, int l) {

            String[] sa = in.split(" ");
            int pos = -1;
            if (sa.length < 1) {
                System.out.println("Problem parsing program line: "+l);
                System.out.println(in);
            }
            
            LinkedHashMap<String,String> lm;
            lm = new LinkedHashMap<>();
            switch(sa[0].toUpperCase()) {
                case "SHIFT":  // SHIFT hand TO THE dir
                    if (sa.length < 5) {
                        lm.put("OP","INVALID");    
                    } else {
                        lm.put("OP","SHIFT");

                        if (sa[4].equalsIgnoreCase("L")) {
                            lm.put("DIR","L");
                        } else if (sa[4].equalsIgnoreCase("R")) {
                            lm.put("DIR","R");                            
                        } else {
                            lm.put("OP","INVALID"); // invalid DIR

                        }
                        if (sa[1].equalsIgnoreCase("LH")) {
                            lm.put("HAND", "LH");
                        } else if (sa[1].equalsIgnoreCase("RH")) {
                            lm.put("HAND", "RH");                       
                        } else {
                            lm.put("OP","INVALID"); // invalid HAND

                        }
                    }  
                    break;
                case "MOVE":  // MOVE hand TO POSITION num
                    lm.put("OP","MOVE");

                    if (sa.length < 5) {
                        lm.put("OP","INVALID");    
                    } else {
                        try {
                            pos = Integer.parseInt(sa[4]);
                            if (pos < 0) {
                                pos = -2;  // invalid Position
                                lm.put("OP","INVALID"); // invalid POS    

                            }
                        } catch (Exception e) {
                            pos = -1;  // not a number, but LHPos | RHPos

                            // determine if LHPos or RHPos
                            if (sa[4].equalsIgnoreCase("LHPos")) {
                                lm.put("POSCODE","HAND");
                                lm.put("POS", "LHPos");
                            } else if (sa[4].equalsIgnoreCase("RHPos")) {
                                lm.put("POSCODE","HAND");
                                lm.put("POS", "RHPos");
                            } else {
                                pos = -2;  // invalid Position
                                lm.put("OP","INVALID"); // invalid POS

                            } 

                        }
                        if (pos > -1) {
                            lm.put("POS",sa[4]);
                            lm.put("POSCODE","POSNUM");
                        }
                        if (pos > -2) {

                            if (sa[1].equalsIgnoreCase("LH")) {
                                lm.put("HAND", "LH");

                            } else if (sa[1].equalsIgnoreCase("RH")) {
                                lm.put("HAND", "RH");

                            } else {
                                lm.put("OP","INVALID"); // invalid HAND

                            }
                        }
                    }
                    break;
                case "JUMP":
                    //  JUMP TO LINE num
                    //  JUMP TO LINE num IF num comp num
                    
                    if (sa.length < 4) {
                        lm.put("OP","INVALID");    
                    } else {
                        if ((sa.length > 7) && (sa[4].equalsIgnoreCase("IF")) ) {
                            if (sa.length < 8) {
                                lm.put("OP","INVALID");
                            } else {
                                lm.put("OP","JUMPIF");
                                // check comp first
                                String s = sa[6].toUpperCase();
                                if (((s.equals("EQ") || s.equals("NE")) || 
                                        s.equals("LT")) || s.equals("GT")) {
                                    // valid comp codes
                                    lm.put("COMP",s.toLowerCase());

                                    // pos #5 code1, pos #7 code2
                                    s = sa[5].toUpperCase();
                                    if (s.equals("LHCARD") || s.equals("LHPOS") ||
                                            s.equals("RHCARD") || s.equals("RHPOS")) {
                                        lm.put("LS",s);
                                    } else {
                                        lm.put("OP","INVALID"); //invalid left comp
                                    }

                                    s = sa[7].toUpperCase();
                                    try {
                                        pos = Integer.parseInt(sa[7]);
                                        if (pos < 0) {
                                            pos = -2;  // invalid Position
                                            lm.put("OP","INVALID"); // invalid POS    

                                        }
                                        lm.put("RSCODE","POSNUM");
                                        lm.put("RS",s);
                                    } catch (Exception e) {
                                        if (s.equals("LHCARD") || s.equals("LHPOS") ||
                                            s.equals("RHCARD") || s.equals("RHPOS")) {
                                            lm.put("RS",s);
                                            lm.put("RSCODE","HAND");
                                        } else {
                                            lm.put("OP","INVALID"); //invalid left comp
                                        }
                                    }


                                } else {
                                    // invalid comp codes
                                    lm.put("OP","INVALID"); // invalid comp code

                                }
                            }    
                        } else {
                            lm.put("OP","JUMP");

                        }
                    }    
                    
                    if (sa.length < 4) {
                        lm.put("OP","INVALID");
                    } else {
                        // parse out the line to jump to
                        int line = -1;
                        try {
                            line = Integer.parseInt(sa[3]);
                            lm.put("LINE",sa[3]);
                        } catch (Exception e) {
                            lm.put("OP","INVALID"); // invalid line num

                        }
                    }
                    
                    break;
                case "STOP":
                    lm.put("OP","STOP");
                    break;
                case "SWAP":
                    lm.put("OP","SWAP");
                    break;
                default:
                    lm.put("OP","INVALID"); // invalid line
                    break;
                    
            }

            
            if (lm.get("OP").equalsIgnoreCase("INVALID")) {
                System.out.println("Line "+l+" contains invalid OP: "+in);
            }
            
            return lm;
    }

    public void printTokens() {
        int i = 0;
        // ArrayList<LinkedHashMap<String,String>> t
        for (LinkedHashMap<String,String> m : t) {
            i++;
            printTokenLine(m,i);
        }
    }
    
    public void printTokenLine(LinkedHashMap<String,String> tl, int l) {
        String dir;
        String hand;
        String pos;
        String poscode;
        String lpos;
        String rpos;
        
        String op = tl.get("OP");
        System.out.print(l+" ");
        if (op.equalsIgnoreCase("invalid")) {
            System.out.println("INVALID");
            return;
        } else if (op.equalsIgnoreCase("JUMPIF")) {
            System.out.print("JUMP ");
        } else {
            System.out.print(op+" ");
        }
        switch (op) {
            case "SHIFT":
                dir = tl.get("DIR");
                hand = tl.get("HAND");
                System.out.println(hand+" TO THE "+dir);
                break;
            case "MOVE":
                hand = tl.get("HAND");
                poscode = tl.get("POSCODE");
                pos = tl.get("POS");
                if (poscode.equals("HAND")) {
                    System.out.println(hand+" TO POSITION "+pos);
                } else {
                    System.out.println(hand+" TO POSITION "+pos);
                }
                break;
            case "JUMP":
                pos = tl.get("LINE");
                System.out.println("TO LINE "+pos);
                break;
            case "JUMPIF":
                // COMP: eq,ne,lt,gt LS:LHCARD,LHPOS RSCODE:POSNUM,HAND RS: 
                
                pos = tl.get("LINE");
                lpos = tl.get("LS");
                rpos = tl.get("RS");
                poscode = tl.get("RSCODE");
                
                System.out.println("TO LINE "+pos+" IF "+lpos+" "+tl.get("COMP")+" "+rpos);
                 
                break;
            case "STOP":
                System.out.println("");
                break;
            case "SWAP":
                System.out.println("");
                break;
        }
        
    }
    
    public void reset_machine() {
        leftHand = 0;
        rightHand = 1;
        // tell cards to reset
        c.resetCards();
    }
    
    public void execute () {
        int step = 0;
        int line = 1;
        LinkedHashMap<String,String> m = t.get(1);
        
        while (step < MAXSTEP) {
            step++;

            try {
                m = t.get(line-1);
            } catch (java.lang.IndexOutOfBoundsException e) {
                System.out.println("Machine Execution Stopped at Step: "+step+" Line: "+line);
                System.out.println("Instruction line "+line+ " is out of range");
                error = true;
                return;
            }
            if (m.get("OP").equals("STOP")) {
                System.out.println("Machine Execution Stopped at Step: "+step+" Line: "+line);
                return;
            }
            System.out.println("Step: "+step+" Line: "+line);
            line = execute_line(m,line);
            if (line == -1) {
                System.out.println("Machine Execution resulted in ERROR at Step: "+step+" Line: "+line);
                error = true;
                return;
            }
            
            //
        }
        System.out.println("Exceeded Max Steps allowed: "+MAXSTEP);
        error = true;
        return;
        
    }
    
    public int execute_line (LinkedHashMap<String,String> tl, int l) {
        // OP: SHIFT MOVE JUMP JUMPIF SWAP STOP
        String op;
        String dir;
        String hand;
        String pos;
        String poscode;
        String lpos;
        String rpos;
        int posnum;
        int lposnum;
        int rposnum;
        String comp;
        boolean res = false;
        
        lposnum = c.getCard(leftHand);
        if (lposnum == -1) {
            lpos = "INVALID";
        } else {
            lpos = Integer.toString(lposnum);
        }
        rposnum = c.getCard(rightHand);
                if (rposnum == -1) {
            rpos = "INVALID";
        } else {
            rpos = Integer.toString(rposnum);
        }        
        String curr = "LH:"+leftHand+" RH:"+rightHand+" LHCard:"+lpos+" RHCard: "+rpos;

        op = tl.get("OP");
        System.out.print(l+" ");
        if (op.equalsIgnoreCase("invalid")) {
            System.out.println("INVALID");
            return -1;
        } else if (op.equalsIgnoreCase("JUMPIF")) {
            System.out.print("JUMP ");
        } else {
            System.out.print(op+" ");
        }        
        
        switch (op) {
            case "SHIFT":
                dir = tl.get("DIR");
                hand = tl.get("HAND");
                System.out.println(hand+" TO THE "+dir+" ("+curr+") ");
                if (hand.equals("LH")) {
                    if (dir.equals("L")) {
                        leftHand--;
                    } else if (dir.equals("R")) {
                        leftHand++;
                    } else {
                        System.out.println("DIR is INVALID");
                        return -1;
                    }
                } else if (hand.equals("RH")) {
                    if (dir.equals("L")) {
                        rightHand--;
                    } else if (dir.equals("R")) {
                        rightHand++;
                    } else {
                        System.out.println("DIR is INVALID");
                        return -1;
                    }                    
                } else {
                    System.out.println("HAND is INVALID");
                    return -1;
                }
                break;
            case "MOVE":
                hand = tl.get("HAND");
                poscode = tl.get("POSCODE");
                pos = tl.get("POS");
                if (poscode.equals("HAND")) {
                    System.out.println(hand+" TO POSITION "+pos+" ("+curr+") ");
                } else {
                    System.out.println(hand+" TO POSITION "+pos+" ("+curr+") ");
                }
                // resolve RHpos and LHpos to actual position first
                if (poscode.equals("HAND")) {
                    if (pos.equalsIgnoreCase("LHPOS")) {
                        posnum = leftHand;
                    } else if (pos.equalsIgnoreCase("RHPOS")) {
                        posnum = rightHand;
                    } else {
                        System.out.println("TARGET HAND is INVALID");
                        return -1;
                    }
                } else {
                    // it's a int position, transform to int
                    try {
                        posnum = Integer.parseInt(pos);
                        
                    } catch (Exception e) {
                        System.out.println("TARGET POSITION is INVALID");
                        return -1;

                    }                    
                }
                if (hand.equals("LH")) {
                    leftHand = posnum;
                } else if (hand.equals("RH")) {
                    rightHand = posnum;
                } else {
                    System.out.println("HAND is INVALID");
                    return -1;
                }
                break;
            case "JUMP":
                pos = tl.get("LINE");
                System.out.println("TO LINE "+pos+" ("+curr+") ");
                
                try {
                    posnum = Integer.parseInt(pos);

                } catch (Exception e) {
                    System.out.println("TARGET POSITION is INVALID");
                    return -1;

                } 
                l = posnum - 1;
                
                break;
            case "JUMPIF":
                
                // COMP: eq,ne,lt,gt LS:LHCARD,LHPOS RSCODE:POSNUM,HAND RS: 
                
                pos = tl.get("LINE");
                lpos = tl.get("LS");
                rpos = tl.get("RS");
                poscode = tl.get("RSCODE");
                comp = tl.get("COMP");
                
                System.out.println("TO LINE "+pos+" IF "+lpos+" "+tl.get("COMP")+" "+rpos+" ("+curr+") ");  
                
                // resolve line to jump to first
                try {
                    posnum = Integer.parseInt(pos);

                } catch (Exception e) {
                    System.out.println("TARGET POSITION is INVALID");
                    return -1;

                }         

                // now resolve FIRST COMP to actual position

                if (lpos.equalsIgnoreCase("LHPOS")) {
                    lposnum = leftHand;
                } else if (lpos.equalsIgnoreCase("RHPOS")) {
                    lposnum = rightHand;
                } else if (lpos.equalsIgnoreCase("LHCARD")) {
                    lposnum = c.getCard(leftHand);
                    if (lposnum == -1) {
                        System.out.println("FIRST COMP ARG LEFT HAND POSITION "+leftHand+" is INVALID");
                        return -1;
                    }
                } else if (lpos.equalsIgnoreCase("RHCARD")) {
                    lposnum = c.getCard(rightHand);
                    if (lposnum == -1) {
                        System.out.println("FIRST COMP ARG RIGHT HAND POSITION "+rightHand+" is INVALID");
                        return -1;
                    }                        
                } else {
                    System.out.println("FIRST COMP ARG SECOND COMP HAND is INVALID");
                    return -1;
                }
                
                
                
                // now resolve SECOND COMP to actual position
                if (poscode.equals("HAND")) {
                    if (rpos.equalsIgnoreCase("LHPOS")) {
                        rposnum = leftHand;
                    } else if (rpos.equalsIgnoreCase("RHPOS")) {
                        rposnum = rightHand;
                    } else if (rpos.equalsIgnoreCase("LHCARD")) {
                        rposnum = c.getCard(leftHand);
                        if (rposnum == -1) {
                            System.out.println("SECOND COMP ARG LEFT HAND POSITION "+leftHand+" is INVALID");
                            return -1;
                        }
                    } else if (rpos.equalsIgnoreCase("RHCARD")) {
                        rposnum = c.getCard(rightHand);
                        if (rposnum == -1) {
                            System.out.println("SECOND COMP ARG RIGHT HAND POSITION "+rightHand+" is INVALID");
                            return -1;
                        }                        
                    } else {
                        System.out.println("SECOND COMP ARG SECOND COMP HAND is INVALID");
                        return -1;
                    }
                } else {
                    // it's a int position, transform to int
                    try {
                        rposnum = Integer.parseInt(rpos);
                        
                    } catch (Exception e) {
                        System.out.println("SECOND COMP ARG VALUE is INVALID");
                        return -1;

                    }                    
                }
                
                // COMP: eq,ne,lt,gt
                
                // now do the compare
                if (comp.equals("eq")) {
                    if (lposnum == rposnum) {
                        System.out.println("Jumping to Line "+posnum);
                        l = posnum - 1;
                    } else {
                        System.out.println("continuing on next line "+ (l+1));
                        
                    }
                } else if (comp.equals("ne")) {
                    if (lposnum != rposnum) {
                        System.out.println("Jumping to Line "+posnum);
                        l = posnum - 1;
                    } else {
                        System.out.println("continuing on next line "+ (l+1));
                        
                    }                    
                } else if (comp.equals("lt")) {
                    if (lposnum < rposnum) {
                        System.out.println("Jumping to Line "+posnum);
                        l = posnum - 1;
                    } else {
                        System.out.println("continuing on next line "+ (l+1));
                        
                    }                    
                } else if (comp.equals("gt")) {
                    if (lposnum > rposnum) {
                        System.out.println("Jumping to Line "+posnum);
                        l = posnum - 1;
                    } else {
                        System.out.println("continuing on next line "+ (l+1));
                        
                    }                    
                } else {
                    System.out.println("COMP ARG is INVALID");
                    return -1;
                }
                
                break;
            case "SWAP":
                res = c.swapCards(leftHand, rightHand);
                if (!res) {
                    System.out.println("ERROR from SWAP");
                    return -1;
                }
                break;
            default:
                break;
                
        }
        
        l++;
        return l;
    }
    
    public static void main (String[] args) {
        System.out.println("Starting Human Machine");
        HumanMachine hm = new HumanMachine();
                
        hm.c.printOriginalCards();
        hm.p.printProgram();
        
        hm.checkSyntaxAndParse();
        if (hm.getErrorState()) {
            System.out.println("Human Machine has errored");
        }
        
        System.out.println("Tokenized program");
        hm.printTokens();
        
        if (hm.error) {
            System.out.println("Error Parsing and Tokenizing");
            System.out.println("Execution will not start");
            return;
        }
        System.out.println("Execute program");
        hm.execute();
       
        hm.c.printOriginalCards();
        hm.c.printResultCards();
        System.out.println("Left Hand is at index  "+hm.leftHand);
        System.out.println("Right Hand is at index "+hm.rightHand);
        if (hm.c.getCard(hm.leftHand) == hm.c.getMinCardVal()) {
            System.out.println("Find Min Card successful");
            
        } else {
            System.out.println("Find Min Card FAILED");
        }
        
        if (hm.error) {
            System.out.println("Execution resulted in error");
        }
    }
}
