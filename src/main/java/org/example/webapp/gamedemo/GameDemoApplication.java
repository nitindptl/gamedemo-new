package org.example.webapp.gamedemo;

import org.example.webapp.models.InnerBody;
import org.example.webapp.models.Line;
import org.example.webapp.models.Point;
import org.example.webapp.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.*;
import org.example.webapp.constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication(scanBasePackages = { "org.example.webapp" })
@EnableAutoConfiguration
@RestController
@CrossOrigin({ "*" })
public class GameDemoApplication implements ErrorController {

	private static Map<String, Object> state;
	private static Map<Point, Point> pointsAlreadyMet;
	private static Map<Point, List<Point>> adjuscentPoints;

	@Autowired
	private org.example.webapp.service.GetAdjuscentNodeService getAdjuscentNodeService;

	static {
		state = new HashMap<>();
		pointsAlreadyMet = new HashMap<>();
	}

	@RequestMapping("/initialize")
	public String initialize() {
		// state = new HashMap<>();
		adjuscentPoints = getAdjuscentNodeService.getPoints();
		resetMap();
		state.put(Constants.PLAYER, Constants.PLAYER_1);

		System.out.println("{\\n\" +\n" + "\t\t\t\t\"    \\\"msg\\\": \\\"INITIALIZE\\\",\\n\" +\n"
				+ "\t\t\t\t\"    \\\"body\\\": {\\n\" +\n" + "\t\t\t\t\"\\\"newLine\\\": null,\\n\" +\n"
				+ "\t\t\t\t\"\\\"heading\\\": \\\"Player 1\\\",\\n\" +\n"
				+ "\t\t\t\t\"\\\"message\\\": \\\"Awaiting Player 1's Move\\\"\\n\" +\n" + "\t\t\t\t\"} }");

		return "{\n" + "    \"msg\": \"INITIALIZE\",\n" + "    \"body\": {\n" + "\"newLine\": null,\n"
				+ "\"heading\": \"Player 1\",\n" + "\"message\": \"Awaiting Player 1's Move\"\n" + "} }";
	}

	private void resetMap() {
		state.clear();
		pointsAlreadyMet.clear();
	}

	@PostMapping("/node-clicked")
	Response nodeClicked(@RequestBody Point point) {

		System.out.println(
				"NODE_CLICKED : " + " {\n" + "\"x\": " + point.getX() + ",\n" + "\"y\": " + point.getY() + " } ");

		String msg = null;
		// If node-clicked is not started then let's start it
		if (!state.containsKey(Constants.STATE_START)) {
			state.put(Constants.STATE_CLICK_COUNT, 1);
			state.put(Constants.STATE_START, point);
			state.put(Constants.STATE_NODE_FIRST, point);
			//state.put(Constants.PLAYER, Constants.PLAYER_1);
			msg = "Select a second node to complete the line.";
			return getResponse(Constants.VALID_START_NODE, state.get(Constants.PLAYER).toString(), null, null, msg);
		} else {
			if (!state.containsKey(Constants.STATE_END)) {
				state.put(Constants.STATE_END, point);
				state.put(Constants.STATE_NODE_LAST, point);
			}
		}
		
		Point first = (Point) state.get(Constants.STATE_NODE_FIRST);
		Point last = (Point) state.get(Constants.STATE_NODE_LAST);
		
		Integer clickCount = (Integer) state.get(Constants.STATE_CLICK_COUNT) + 1;
		state.put(Constants.STATE_CLICK_COUNT, clickCount);

		if (clickCount % 2 == 0) {
			state.put(Constants.IS_FIRST_MOVE, false);
			state.put(Constants.STATE_END, point);
			updateFirstLastNode(state,point);
			//update player if he played second move
			//String up = state.get(Constants.PLAYER).equals(Constants.PLAYER_1) ? Constants.PLAYER_2 : Constants.PLAYER_1;
			state.put(Constants.PLAYER, Constants.PLAYER_1);
		} else {
			state.put(Constants.PLAYER, Constants.PLAYER_2);
			state.put(Constants.IS_FIRST_MOVE, true);
			// if user already selected 1 line and tries to select another point then it
			// should be either START or END should be clicked first
			if (!pointsAlreadyMet.isEmpty() && (first.equals(point) || last.equals(point))) {
				updateFirstLastNode(state,point);
			} else if (!pointsAlreadyMet.isEmpty() && (!first.equals(point) || !last.equals(point))) {
					clickCount = (Integer) state.get(Constants.STATE_CLICK_COUNT) - 1;
					state.put(Constants.STATE_CLICK_COUNT, clickCount);
					// invalid move!!
					msg = "Not a valid starting position.";
					//String up = state.get(Constants.PLAYER).equals(Constants.PLAYER_1) ? Constants.PLAYER_1 : Constants.PLAYER_2;
					//state.put(Constants.PLAYER, up);
					return getResponse(Constants.INVALID_START_NODE, state.get(Constants.PLAYER).toString(), null, null, msg);
				}
			state.put(Constants.STATE_START, point);
			}
			
			

		// if user selected the start point but it's not an adjuscent then throw error
		// if user selected node is not adjuscent then should be INVALID_NODE
		List<Point> adjuscentPoint = (List<Point>) adjuscentPoints.get((Point) state.get(Constants.STATE_START));
		if (!adjuscentPoint.contains(point)) {
			clickCount = (Integer) state.get(Constants.STATE_CLICK_COUNT) - 1;
			state.put(Constants.STATE_CLICK_COUNT, clickCount);
			state.remove(Constants.STATE_START);
			msg = "Invalid move! Try again.";
			//String up = state.get(Constants.PLAYER).equals(Constants.PLAYER_1) ? Constants.PLAYER_2 : Constants.PLAYER_1;
			//state.put(Constants.PLAYER, up);
			return getResponse(Constants.INVALID_END_NODE, state.get(Constants.PLAYER).toString(), null, null, msg);
		}
		
		//If not available game over
		if(isNodeAvailable()){
				pointsAlreadyMet.put((Point) state.get(Constants.STATE_START), point);
				findNotVisitedNode();
				Point start = (Point) state.get(Constants.STATE_START);
				// end = (Point) state.get(Constants.STATE_END);
				msg = "Player wins.";
				return getResponse(Constants.GAME_OVER, state.get(Constants.PLAYER).toString(), start, point, msg);
		}
		
		// if user is selected proper adjuscent node
		if (adjuscentPoint.contains(point)) {
			pointsAlreadyMet.put((Point) state.get(Constants.STATE_START), point);
			findNotVisitedNode();
			Point start = (Point) state.get(Constants.STATE_START);
			// end = (Point) state.get(Constants.STATE_END);
			msg = "Select a second node to complete the line.";
			return getResponse(Constants.VALID_START_NODE, state.get(Constants.PLAYER).toString(), start, point, msg);
		}

		// if node-clicked event is already started but not the end point
		if (state.containsKey(Constants.STATE_START) && !state.containsKey(Constants.STATE_END)) {
			state.put(Constants.STATE_END, point);
			Point start = (Point) state.get(Constants.STATE_START);
			Point end = (Point) state.get(Constants.STATE_END);
			msg = "Select a second node to complete the line.";
			return getResponse(Constants.VALID_START_NODE, state.get(Constants.PLAYER_2).toString(), start, end, msg);
		}

		
		// user selected already from the start or end node
		// then we need to update start and end in the current state.
		return null;

	}

	@RequestMapping("/error")
	String gameError() {
		return "{\n" + "    \"error\": \"Invalid type for `id`: Expected INT but got a STRING\"\n" + "}";
	}

	private Response getResponse(String msg, String heading, Point start, Point end, String bodyMsg) {
		Response resp = new Response();
		resp.setMsg(msg);
		InnerBody body = new InnerBody();
		if (start != null && end != null) {
			Line line = new Line();
			line.setStart(new Point(start.getX(), start.getY()));
			line.setEnd(new Point(end.getX(), end.getY()));
			body.setNewLine(line);
		}else {
			body.setNewLine(null);
		}
		body.setHeading(heading);
		body.setMessage(bodyMsg);
		resp.setBody(body);
		return resp;

	}

	public static void main(String[] args) {
		SpringApplication.run(GameDemoApplication.class, args);
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

	private void updateFirstLastNode(Map<String, Object> state,Point point) {
		Point first = (Point) state.get(Constants.STATE_NODE_FIRST);
		Point last = (Point) state.get(Constants.STATE_NODE_LAST);
		
		if(point.equals(first)) {
			state.put(Constants.IS_FIRST_NODE_CLICKED, true);
		}else if(point.equals(last)) {
			state.put(Constants.IS_FIRST_NODE_CLICKED, false);
		}
		//If the move sencod move then only update First/last node
		if((Boolean)state.get(Constants.IS_FIRST_MOVE) == false) {
			if((Boolean)state.get(Constants.IS_FIRST_NODE_CLICKED) == true) {
				state.put(Constants.STATE_NODE_FIRST, point);
			} else {
				state.put(Constants.STATE_NODE_LAST, point);
			}
		}
		
	}
	
	private void findNotVisitedNode() {
		adjuscentPoints.put((Point) state.get(Constants.STATE_NODE_FIRST),findNotVisitedNode((Point) state.get(Constants.STATE_NODE_FIRST)));
		adjuscentPoints.put((Point) state.get(Constants.STATE_NODE_FIRST),findNotVisitedNode((Point) state.get(Constants.STATE_NODE_LAST)));
	}
	
	private List<Point> findNotVisitedNode(Point node) {
		List<Point> adjuscentPoint = (List<Point>) adjuscentPoints.get(node);
		List<Point> availableNodes =  new ArrayList<Point>();
		for(Point p : adjuscentPoint) {
			if(pointsAlreadyMet.containsKey(p) || pointsAlreadyMet.containsValue(p)){
				
			}else {
				if(!node.equals(p))
					availableNodes.add(p);
			}
		}
		return availableNodes;
	}
	
	private boolean isNodeAvailable() {
		return findNotVisitedNode((Point) state.get(Constants.STATE_END)).isEmpty()?true:false;
	}
}
