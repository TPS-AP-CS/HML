/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package counter;

/**
 *
 * @author digimo
 */

/**
   This class models a tally counter.
*/
public class Counter
{
   private int value;

   public Counter () {
       value = 0;
   }
   public Counter (int v) {
       value = v;
   }
   /**
      Gets the current value of this counter.
      @return the current value
   */
   public int getValue()
   {
      return value;
   }

   /**
      Advances the value of this counter by 1.
   */
   public void click() 
   {
      value = value + 1;
   }

   public void undo()
   {
       value = value - 1;
       value = Math.max(value,0);
   }
   /**
      Resets the value of this counter to 0.
   */
   public void reset()
   {
      value = 0;
   }
   
    public static void main(String[] args) {
        // TODO code application logic here
        Counter c = new Counter();
        Counter c2 = new Counter(20);
        c2.click();
        c.click();
        
        System.out.println("c is "+c.getValue());
        System.out.println("c2 is "+c2.getValue());
        
        c.undo();
        c.undo();
        c.undo();
        c.undo();
        
        System.out.println("c is "+c.getValue());
        
        c2.undo();
        c2.undo();
        c2.undo();
        c2.undo();
        
        System.out.println("c2 is "+c2.getValue());
        
    }
}