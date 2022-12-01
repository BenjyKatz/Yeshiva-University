package edu.yu.introtoalgs;
import java.util.*;

public class IntersectRectangles {

  /** This constant represents the fact that two rectangles don't intersect.
   *
   * @see #intersectRectangle
   * @warn you may not modify this constant in any way
   */
  public final static Rectangle NO_INTERSECTION =
    new Rectangle(0, 0, -1, -1);

  /** An immutable class that represents a 2D Rectangle.
   *
   * @warn you may not modify the instance variables in any way, you are
   * encouraged to add to the current set of variables and methods as you feel
   * necesssary.
   */
  public static class Rectangle {
    // safe to make instance variables public because they are final, now no
    // need to make getters
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    /** Constructor: see the requirements doc for the precise semantics.
     *
     * @warn you may not modify the currently defined semantics in any way, you
     * may add more code if you so choose.
     */
    public Rectangle
      (final int x, final int y, final int width, final int height)
    {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }
      @Override
      public boolean equals(Object o){
        if (!(o instanceof Rectangle)){
          return false;
        }
        Rectangle other = (Rectangle)o;
        if(this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height){
          return true;
        }
        else return false;
      }
      @Override
      public int hashCode(){
        return ((this.x)*100000000+(this.y)*100000+(this.width)*1000+(this.height));
      }
    }
  

  /** If the two rectangles intersect, returns the rectangle formed by their
   * intersection; otherwise, returns the NO_INTERSECTION Rectangle constant.
   *
   * @param r1 one rectangle
   * @param r2 the other rectangle
   * @param a rectangle representing the intersection of the input parameters
   * if they intersect, NO_INTERSECTION otherwise.  See the requirements doc
   * for precise definition of "rectangle intersection"
   * @throws IllegalArgumentException if either parameter is null.
   */
  public static Rectangle intersect (final Rectangle r1, final Rectangle r2)
  {
    if(r1==null || r2==null){
      throw new IllegalArgumentException();
    }
    //no intersect
    if(r2.y>(r1.y+r1.height)){
      return NO_INTERSECTION;
    }
    if(r2.x>(r1.x+r1.width)){
      return NO_INTERSECTION;
    }
    if((r2.y+r2.height)<r1.y){
      return NO_INTERSECTION;
    }
    if((r2.x+r2.width)<r1.x){
      return NO_INTERSECTION;
    }
    //otherwise lets do the four line method
    LinkedList<Integer> relevantXs = new LinkedList<Integer>();
    LinkedList<Integer> relevantYs = new LinkedList<Integer>();
    relevantXs.add(r1.x);
    relevantXs.add(r2.x);
    relevantXs.add(r1.x+r1.width);
    relevantXs.add(r2.x+r2.width);

    relevantYs.add(r1.y);
    relevantYs.add(r2.y);
    relevantYs.add(r1.y+r1.height);
    relevantYs.add(r2.y+r2.height);
    Collections.sort(relevantXs);
    Collections.sort(relevantYs);

    int intersectX = relevantXs.get(1);//the second X is the x value of the origin of the intercept rectangle
    int intersectY = relevantYs.get(1);//the second X is the x value of the origin of the intercept rectangle
    int intersectWidth = relevantXs.get(2)-intersectX;//finds the x value at the other side of the intersect and subtracts to find width
    int intersectHeight = relevantYs.get(2)- intersectY;
    return new Rectangle(intersectX, intersectY, intersectWidth, intersectHeight);
    // supply a more useful implementation!
  }

} // class

