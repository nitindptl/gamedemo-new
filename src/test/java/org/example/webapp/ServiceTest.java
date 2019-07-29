package org.example.webapp;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.example.webapp.models.Point;
import org.example.webapp.service.GetAdjuscentNodeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=ServiceTest.class)
public class ServiceTest {

	@MockBean 
	GetAdjuscentNodeService getAdjuscentNodeService;
	
	@Before
	public void init(){
		getAdjuscentNodeService =  new GetAdjuscentNodeService();
	}
	
	@Test
	public void getAdjuscentNodes_NotEmptyTest() {
		Map<Point, List<Point>> map = getAdjuscentNodeService.getPoints();
		assertTrue(map.size()>0);
		System.out.println(map);
		
	}
	
	/*@Test
	public void getAdjuscentNodes_EmptyTest() {
		Map<Point, List<Point>> map = getAdjuscentNodeService.getPoints();
		assertTrue(map.size()>0);
		System.out.println(map);
		
	}*/

}
