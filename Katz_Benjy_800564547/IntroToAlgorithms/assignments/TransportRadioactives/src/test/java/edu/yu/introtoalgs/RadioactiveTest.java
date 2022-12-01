package edu.yu.introtoalgs;
import org.junit.Test;


import java.util.*;
import edu.yu.introtoalgs.TransportRadioactives;
import edu.yu.introtoalgs.TransportRadioactives.*;
//import edu.yu.introtoalgs.TransportRadioactives.TransportationStateImpl;

public class RadioactiveTest{
    @Test
    public void basicTest(){
        System.out.println("basicTest");
        TransportRadioactives TR = new TransportRadioactives();
        System.out.println(TR.transportIt(6, 5));

        /*
        Set<Interval> ogSet = new HashSet<Interval>();
        MergeAnInterval.Interval newInterval= new Interval(5, 15);
        ogSet.add(new Interval(1, 3));
        ogSet.add(new Interval(4, 7));
        ogSet.add(new Interval(8, 10));
        ogSet.add(new Interval(11, 13));
        Set<Interval> newSet = MAI.merge(ogSet, newInterval);
        for(Interval i: newSet){
            System.out.println(i.getLeft()+" "+i.getRight());
        }
        System.out.println(newSet.contains(new Interval(4, 15)));
        assert(newSet.contains(new Interval(4, 15)));*/
    }
    @Test
    public void twotwo(){
        System.out.println("twotwo test");
        TransportRadioactives TR = new TransportRadioactives();
        System.out.println(TR.transportIt(2, 2));
    }
    @Test
    public void oneone(){
        System.out.println("oneone test");
        TransportRadioactives TR = new TransportRadioactives();
        System.out.println(TR.transportIt(1, 1));
    }
    @Test
    public void threethree(){
        System.out.println("threethree test");
        TransportRadioactives TR = new TransportRadioactives();
        System.out.println(TR.transportIt(3, 3));
    }
    @Test
    public void illegal(){
        System.out.println("illegal test");
        TransportRadioactives TR = new TransportRadioactives();
        System.out.println(TR.transportIt(5, 7));
    }
   


}
