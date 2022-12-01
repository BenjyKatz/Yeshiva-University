package edu.yu.introtoalgs;
import edu.yu.introtoalgs.IntersectRectangles;

import org.junit.Test;
import static org.junit.Assert.*;
import  org.junit.Assert;

//import org.junit.Assert.*;
public class IntersectTest{
	@Test
	public void NonIntersectTest(){
		IntersectRectangles.Rectangle middleRec = new IntersectRectangles.Rectangle(3,7, 10, 15);
		IntersectRectangles.Rectangle sideRec = new IntersectRectangles.Rectangle(14, 10, 5, 5);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).width, -1);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).height, -1);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).x, 0);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).y, 0);

		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).width, -1);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).height, -1);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).x, 0);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).y, 0);

		middleRec = new IntersectRectangles.Rectangle(3,7, 10, 15);
		sideRec = new IntersectRectangles.Rectangle(14, 23, 5, 50);

		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).width, -1);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).height, -1);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).x, 0);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).y, 0);

		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).width, -1);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).height, -1);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).x, 0);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).y, 0);
	}
	@Test
	public void IntersectTest(){
		//right corner case
		IntersectRectangles.Rectangle middleRec = new IntersectRectangles.Rectangle(1,1, 5, 5);
		IntersectRectangles.Rectangle sideRec = new IntersectRectangles.Rectangle(3, 3, 10, 10);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).width, 3);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).height, 3);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).x, 3);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).y, 3);

		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).width, 3);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).height, 3);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).x, 3);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).y, 3);

		assertEquals(IntersectRectangles.intersect(sideRec, middleRec), new IntersectRectangles.Rectangle(3,3,3,3));

		//left corner case
		 middleRec = new IntersectRectangles.Rectangle(10,1, 5, 5);
		 sideRec = new IntersectRectangles.Rectangle(3, 3, 10, 10);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).width, 3);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).height, 3);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).x, 10);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).y, 3);

		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).width, 3);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).height, 3);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).x, 10);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).y, 3);

		//overlap side
		 middleRec = new IntersectRectangles.Rectangle(10,10, 5, 5);
		 sideRec = new IntersectRectangles.Rectangle(13, 8, 10, 10);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).width, 2);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).height, 5);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).x, 13);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).y, 10);

		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).width, 2);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).height, 5);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).x, 13);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).y, 10);

		//left corner case
		 middleRec = new IntersectRectangles.Rectangle(10,10, 20, 20);
		 sideRec = new IntersectRectangles.Rectangle(12, 12, 5, 5);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).width, 5);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).height, 5);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).x, 12);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).y, 12);

		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).width, 5);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).height, 5);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).x, 12);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).y, 12);



	}
	@Test
	public void BoarderTest(){
		IntersectRectangles.Rectangle middleRec = new IntersectRectangles.Rectangle(1,1, 5, 5);
		IntersectRectangles.Rectangle sideRec = new IntersectRectangles.Rectangle(6, 3, 10, 10);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).width, 0);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).height, 3);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).x, 6);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).y, 3);

		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).width, 0);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).height, 3);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).x, 6);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).y, 3);

	}
	@Test
	public void NegativeTest(){
		IntersectRectangles.Rectangle middleRec = new IntersectRectangles.Rectangle(-1,-1, 5, 5);
		IntersectRectangles.Rectangle sideRec = new IntersectRectangles.Rectangle(3, 3, 10, 10);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).width, 1);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).height, 1);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).x, 3);
		assertEquals(IntersectRectangles.intersect(middleRec, sideRec).y, 3);

		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).width, 1);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).height, 1);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).x, 3);
		assertEquals(IntersectRectangles.intersect(sideRec, middleRec).y, 3);


	}
}