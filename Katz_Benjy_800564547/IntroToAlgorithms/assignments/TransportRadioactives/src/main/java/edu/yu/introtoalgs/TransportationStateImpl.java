package edu.yu.introtoalgs;

/** Implements the TransportationState interface.  
 *
 *
 * Students may ONLY use the specified constructor, and may (perhaps even
 * encouraged to) add as many other methods as they choose.
 *
 * @author Avraham Leff
 */

import static edu.yu.introtoalgs.TransportationState.Location.*;

public class TransportationStateImpl implements TransportationState { 

  /** Constructor:
   *
   * @param mithiumAtSrc amount of mithium at the src location, must be >= 0
   * @param cathiumAtSrc amount of cathium at the src location, must be >= 0
   * @param truckLocation location of the truck, must not be null
   * @param totalMithium sum of mithium amounts at src + dest, must be > 0
   * @param totalCathium sum of cathium amounts at src + dest, must be > 0
   *
   * @Students: you may NOT USE ANY OTHER CONSTRUCTOR SIG
   */
  int mAtSrc;
  int cAtSrc;
  int totalM;
  int totalC;
  TransportationState.Location truckLoc;
  public TransportationStateImpl(final int mithiumAtSrc,
                                 final int cathiumAtSrc,
                                 final Location truckLocation,
                                 final int totalMithium,
                                 final int totalCathium)
  {
      this.mAtSrc = mithiumAtSrc;
      this.cAtSrc = cathiumAtSrc;
      truckLoc = truckLocation;
      this.totalM = totalMithium;
      this.totalC = totalCathium;
  } // constructor


  @Override
  public int getMithiumSrc() {
    
      return mAtSrc; 
    }

  @Override
  public int getCathiumSrc() { return this.cAtSrc; }
    
  @Override
  public int getMithiumDest() { return this.totalM-this.mAtSrc; }
    
  @Override
  public int getCathiumDest() { return this.totalC-this.cAtSrc; }
    
  @Override
  public Location truckLocation() { return this.truckLoc; }

  @Override
  public int getTotalMithium() { return this.totalM; }

  @Override
  public int getTotalCathium() { return this.totalC; }
  @Override
  public String toString(){
      return "At  "+ this.truckLocation()+ " there are C: "+ getCathiumSrc() +" and M: "+  getMithiumSrc() +" at the source. At Dest, C: "+getCathiumDest()+" M: "+getMithiumDest()+"\n";
  }
  @Override
  public int hashCode(){
    int loc;
    if (truckLocation() == TransportationState.Location.SRC){ loc = 19;}
    else loc = 7;
    return 31*getCathiumSrc() + 17*getMithiumSrc() + loc;
  }
  @Override
  public boolean equals(Object o){
    if(o instanceof TransportationStateImpl && o.hashCode() == this.hashCode()){
        return true;
    }
    return false;
  }

}   // class