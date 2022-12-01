package edu.yu.introtoalgs;

/** Specifies the interface for generating a sequence of transportation states
 * that moves the radioactives from src to dest per the requirements doc.
 *
 * @author Avraham Leff
 */

import java.util.*;

public class TransportRadioactives {

  /** Computes a sequence of "transport radioactives" movements between the src
   * and the dest such that all of the initial methium and initial cathium are
   * transported safely from the src to the dest.  Each movement must respect
   * the constraints specified in the requirements doc.
   *
   * @param initialMithium initial amount of mithium (in kg) at the src
   * @param initialCathium initial amount of cathium (in kg) at the src
   * @return List of "transport radioactives" movements between the src and the
   * dest (if such a sequence can be computed), or an empty List if no such
   * sequence can be computed under the specified constraints.
   */
  
  public static List<TransportationState> transportIt(final int initialMithium, final int initialCathium) {
    if((!(initialCathium == 2 && initialMithium == 2))&&(!(initialCathium==1 && initialMithium==1))&&initialCathium>=initialMithium)return new LinkedList<TransportationState>();
    int mAtSrc = initialMithium;
    int cAtSrc = initialCathium;
    
    List<TransportationState> path = new LinkedList<TransportationState>();
    if(mAtSrc == 2 && cAtSrc == 2){
        path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.SRC, initialMithium, initialCathium));
        cAtSrc = cAtSrc -2;
        path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.DEST, initialMithium, initialCathium));
        cAtSrc++;
        path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.SRC, initialMithium, initialCathium));
        mAtSrc = mAtSrc-2;
        path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.DEST, initialMithium, initialCathium));
        mAtSrc++;
        path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.SRC, initialMithium, initialCathium));
        mAtSrc--;
        cAtSrc--;
        path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.DEST, initialMithium, initialCathium));
        return path;
    }
    while(mAtSrc >= cAtSrc+1){
        //load the truck up with 1kg of c and 1kg of m
        path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.SRC, initialMithium, initialCathium));
        mAtSrc = mAtSrc - 1;
        cAtSrc = cAtSrc - 1;
        path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.DEST, initialMithium, initialCathium));
        //load the truck wit the c to bring it back
        cAtSrc++;
    }
    while(mAtSrc != 0 && cAtSrc !=0){
        if(cAtSrc == mAtSrc){
            path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.SRC, initialMithium, initialCathium));
            //load up a c and an m
            mAtSrc--;
            cAtSrc--;
            path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.DEST, initialMithium, initialCathium));
            if(mAtSrc == 0 && cAtSrc==0){
                return path;
            }
            //bring back the m
            mAtSrc++;
            
        }
        if(cAtSrc< mAtSrc){
            path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.SRC, initialMithium, initialCathium));
            //load up c and m
            mAtSrc--;
            cAtSrc--;
            path.add(new TransportationStateImpl(mAtSrc, cAtSrc, TransportationState.Location.DEST, initialMithium, initialCathium));
            //bring back the c
            cAtSrc++;
        }
    }

  

    return null;
  } // transportIt

} // TransportRadioactives