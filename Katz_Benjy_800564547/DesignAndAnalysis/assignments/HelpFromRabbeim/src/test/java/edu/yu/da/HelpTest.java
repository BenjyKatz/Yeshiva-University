package edu.yu.da;
import org.junit.Test;
import edu.yu.da.HelpFromRabbeim.*;
import edu.yu.da.HelpFromRabbeimI.*;

import java.util.*;

import static org.junit.Assert.*;
import  org.junit.Assert;

public class HelpTest {

    @Test
    public void basicTest(){
        
        List<Rebbe> rebbeim = new LinkedList<Rebbe>();
        Map<HelpTopics, Integer> requestedHelp = new HashMap<HelpTopics, Integer>();
        List<HelpTopics> skills = new LinkedList<HelpTopics>();
        skills.add(HelpTopics.BAVA_KAMMA);
        rebbeim.add(new Rebbe(20, skills));

        List<HelpTopics> skills1 = new LinkedList<HelpTopics>();
        
        skills1.add(HelpTopics.SANHEDRIN);
        skills1.add(HelpTopics.MISHNAYOS);
        rebbeim.add(new Rebbe(25, skills1));

        requestedHelp.put(HelpTopics.SANHEDRIN, 1);
        requestedHelp.put(HelpTopics.BAVA_KAMMA, 1);
        HelpFromRabbeim hfr = new HelpFromRabbeim();
       System.out.println( hfr.scheduleIt(rebbeim, requestedHelp));
        
    }

    @Test
    public void realTest(){
        
        List<Rebbe> rebbeim = new LinkedList<Rebbe>();
        Map<HelpTopics, Integer> requestedHelp = new HashMap<HelpTopics, Integer>();
        List<HelpTopics> skills = new LinkedList<HelpTopics>();
        skills.add(HelpTopics.BAVA_KAMMA);
        rebbeim.add(new Rebbe(20, skills));

        List<HelpTopics> skills1 = new LinkedList<HelpTopics>();
        skills1.add(HelpTopics.BAVA_KAMMA);
        rebbeim.add(new Rebbe(201, skills1));

        List<HelpTopics> skills2 = new LinkedList<HelpTopics>();
        skills2.add(HelpTopics.BAVA_KAMMA);
        skills2.add(HelpTopics.SANHEDRIN);
        rebbeim.add(new Rebbe(202, skills2));

        List<HelpTopics> skills3 = new LinkedList<HelpTopics>();
        skills3.add(HelpTopics.MISHNAYOS);
        skills3.add(HelpTopics.SANHEDRIN);
        rebbeim.add(new Rebbe(203, skills3));

        List<HelpTopics> skills4 = new LinkedList<HelpTopics>();
        skills4.add(HelpTopics.MISHNAYOS);
        skills4.add(HelpTopics.SANHEDRIN);
        rebbeim.add(new Rebbe(204, skills4));

        List<HelpTopics> skills5 = new LinkedList<HelpTopics>();
        skills5.add(HelpTopics.MISHNAYOS);
        skills5.add(HelpTopics.NACH);
        rebbeim.add(new Rebbe(205, skills5));

        List<HelpTopics> skills6 = new LinkedList<HelpTopics>();
        skills6.add(HelpTopics.NACH);

        rebbeim.add(new Rebbe(206, skills6));

        rebbeim.add(new Rebbe(207, skills6));

        List<HelpTopics> skills7 = new LinkedList<HelpTopics>();
        skills6.add(HelpTopics.BEITZA);
        rebbeim.add(new Rebbe(208, skills7));





        

        requestedHelp.put(HelpTopics.SANHEDRIN, 2);
        requestedHelp.put(HelpTopics.BAVA_KAMMA, 3);
        requestedHelp.put(HelpTopics.MISHNAYOS, 1);
        requestedHelp.put(HelpTopics.NACH, 2);
        HelpFromRabbeim hfr = new HelpFromRabbeim();
       System.out.println( hfr.scheduleIt(rebbeim, requestedHelp));
        
    }



    @Test
    public void cantDoTest(){
        System.out.println("cantDoTest");
        List<Rebbe> rebbeim = new LinkedList<Rebbe>();
        Map<HelpTopics, Integer> requestedHelp = new HashMap<HelpTopics, Integer>();
        List<HelpTopics> skills = new LinkedList<HelpTopics>();
        skills.add(HelpTopics.BAVA_KAMMA);
        rebbeim.add(new Rebbe(20, skills));

        List<HelpTopics> skills1 = new LinkedList<HelpTopics>();
        skills1.add(HelpTopics.BAVA_KAMMA);
        rebbeim.add(new Rebbe(201, skills1));

        List<HelpTopics> skills2 = new LinkedList<HelpTopics>();
        skills2.add(HelpTopics.BAVA_KAMMA);
        skills2.add(HelpTopics.SANHEDRIN);
        rebbeim.add(new Rebbe(202, skills2));

        List<HelpTopics> skills3 = new LinkedList<HelpTopics>();
        skills3.add(HelpTopics.MISHNAYOS);
        skills3.add(HelpTopics.SANHEDRIN);
        rebbeim.add(new Rebbe(203, skills3));

        List<HelpTopics> skills4 = new LinkedList<HelpTopics>();
        skills4.add(HelpTopics.MISHNAYOS);
        skills4.add(HelpTopics.SANHEDRIN);
        rebbeim.add(new Rebbe(204, skills4));

        List<HelpTopics> skills5 = new LinkedList<HelpTopics>();
        skills5.add(HelpTopics.MISHNAYOS);
        skills5.add(HelpTopics.NACH);
        rebbeim.add(new Rebbe(205, skills5));

        List<HelpTopics> skills6 = new LinkedList<HelpTopics>();
        skills6.add(HelpTopics.NACH);

        rebbeim.add(new Rebbe(206, skills6));

        rebbeim.add(new Rebbe(207, skills6));

        List<HelpTopics> skills7 = new LinkedList<HelpTopics>();
        skills6.add(HelpTopics.BEITZA);
        rebbeim.add(new Rebbe(208, skills7));





        

        requestedHelp.put(HelpTopics.SANHEDRIN, 2);
        requestedHelp.put(HelpTopics.BAVA_KAMMA, 4);
        requestedHelp.put(HelpTopics.MISHNAYOS, 1);
        requestedHelp.put(HelpTopics.NACH, 2);
        HelpFromRabbeim hfr = new HelpFromRabbeim();
       System.out.println( hfr.scheduleIt(rebbeim, requestedHelp));
        
    }
   
    
        
}