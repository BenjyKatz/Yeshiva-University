package edu.yu.introtoalgs;
import org.junit.Test;


import java.util.*;
import edu.yu.introtoalgs.MergeAnInterval;
import edu.yu.introtoalgs.MergeAnInterval.Interval;
public class MergeTest{
    @Test
    public void basicTest(){
        System.out.println("basicTest");
        MergeAnInterval MAI = new MergeAnInterval();
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
        assert(newSet.contains(new Interval(4, 15)));
    }
    @Test
    public void outOfScope(){
        System.out.println("outOfScopeTest");
        MergeAnInterval MAI = new MergeAnInterval();
        Set<Interval> ogSet = new HashSet<Interval>();
        MergeAnInterval.Interval newInterval= new Interval(17, 25);
        ogSet.add(new Interval(1, 3));
        ogSet.add(new Interval(4, 7));
        ogSet.add(new Interval(8, 10));
        ogSet.add(new Interval(11, 13));
        Set<Interval> newSet = MAI.merge(ogSet, newInterval);
        for(Interval i: newSet){
            System.out.println(i.getLeft()+" "+i.getRight());
        }
        assert(newSet.contains(new Interval(17, 25)));
    }

    @Test
    public void entireSpan(){
        System.out.println("entireSpan");
        MergeAnInterval MAI = new MergeAnInterval();
        Set<Interval> ogSet = new HashSet<Interval>();
        MergeAnInterval.Interval newInterval= new Interval(-50, 100);
        ogSet.add(new Interval(1, 3));
        ogSet.add(new Interval(4, 7));
        ogSet.add(new Interval(8, 10));
        ogSet.add(new Interval(11, 13));
        Set<Interval> newSet = MAI.merge(ogSet, newInterval);
        for(Interval i: newSet){
            System.out.println(i.getLeft()+" "+i.getRight());
        }
        assert(newSet.contains(new Interval(-50, 100)));
    }
    @Test
    public void sameSet(){
        System.out.println("samSet");
        MergeAnInterval MAI = new MergeAnInterval();
        Set<Interval> ogSet = new HashSet<Interval>();
        MergeAnInterval.Interval newInterval= new Interval(50, 100);
        ogSet.add(new Interval(1, 3));
        ogSet.add(new Interval(4, 7));
        ogSet.add(new Interval(8, 10));
        ogSet.add(new Interval(11, 117));
        Set<Interval> newSet = MAI.merge(ogSet, newInterval);
        for(Interval i: newSet){
            System.out.println(i.getLeft()+" "+i.getRight());
        }
        assert(newSet.contains(new Interval(11, 117)));
    }


}
