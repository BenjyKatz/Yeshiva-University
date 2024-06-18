
package edu.yu.cs.com3800.stage5;





import org.junit.Test;



public class Stage5Test{

    @Test
    public void DemoTest() throws Exception {
        System.out.println("running test");
        Stage5Demo stage5 = new Stage5Demo();
        try{
            stage5.runDemo();
        }
        catch(Exception e){
            System.out.println(e);
        }
        
    }

}