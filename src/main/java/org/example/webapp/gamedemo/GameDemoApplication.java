package org.example.webapp.gamedemo;

import org.example.webapp.models.InnerBody;
import org.example.webapp.models.Line;
import org.example.webapp.models.Point;
import org.example.webapp.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.ErrorController;
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
	private static Map<Point, List<Point>> availableAdjuscentPoints;

	@Autowired
	private org.example.webapp.service.GetAdjuscentNodeService getAdjuscentNodeService;

	static {
		state = new HashMap<>();
		pointsAlreadyMet = new HashMap<>();
	}

	/**
	 * This is rest endpoint to initialize the game with some settings.
	 *
	 * @return
	 */
	@RequestMapping("/initialize")
	public String initialize() {
		// state = new HashMap<>();
		availableAdjuscentPoints = getAdjuscentNodeService.getPoints();
		adjuscentPoints = getAdjuscentNodeService.getPoints();
		resetMap();
		state.put(Constants.PLAYER, 1);

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

	/**
	 * Node clicked endpoint
	 *
	 * @param point : cordinates clicked by the user with x and y
	 * @return returns the response body
	 */
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
			msg = "Select a second node to complete the line.";
			return getResponse(Constants.VALID_START_NODE, getPlayer(), null, null, msg);
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
		/*
		 * // If no node available from start and end node : game over if
		 * (isNodeAvailable() && isNotDiagonalNode(point)) { return
		 * gameOverResponse(point); }
		 */
		// To Identify first/second click
		if (clickCount % 2 == 0) {
			state.put(Constants.IS_FIRST_MOVE, false);
			state.put(Constants.STATE_END, point);
			updateFirstLastNode(state, point);
		} else {
			state.put(Constants.IS_FIRST_MOVE, true);
			state.put(Constants.STATE_START, point);
		}

		Boolean isFirstMove = (Boolean) state.get(Constants.IS_FIRST_MOVE);
		// if user selected the start point but it's not an adjuscent then throw error
		// if user selected node is not adjuscent then should be INVALID_NODE
		Point tempStartPoint = (Point) state.get(Constants.STATE_START);
		List<Point> adjuscentPoint = (List<Point>) availableAdjuscentPoints.get(tempStartPoint);
		if (!adjuscentPoint.contains(point) && !isFirstMove) {
			clickCount = (Integer) state.get(Constants.STATE_CLICK_COUNT) - 1;
			state.put(Constants.STATE_CLICK_COUNT, clickCount);
			state.remove(Constants.STATE_START);
			msg = "Invalid move! Try again.";
			return getResponse(Constants.INVALID_END_NODE, getPlayer(), null, null, msg);
		}

		if (!pointsAlreadyMet.isEmpty() && (first.equals(point) || last.equals(point))) {
			// If selected proper First/Last node to line
			updateFirstLastNode(state, point);
			// if user is selected proper adjuscent node
			if (isFirstMove) {
				pointsAlreadyMet.put((Point) state.get(Constants.STATE_START), point);
				Point start = (Point) state.get(Constants.STATE_START);
				msg = "Select a second node to complete the line.";
				return getResponse(Constants.VALID_START_NODE, getPlayer(), start, point, msg);
			}
		} else if (!pointsAlreadyMet.isEmpty() && (!first.equals(point) || !last.equals(point)) && isFirstMove) {
			// If selected node other than start or end of line Invalid start node
			// invalid move!!
			clickCount = (Integer) state.get(Constants.STATE_CLICK_COUNT) - 1;
			state.put(Constants.STATE_CLICK_COUNT, clickCount);
			msg = "Not a valid starting position.";
			return getResponse(Constants.INVALID_START_NODE, getPlayer(), null, null, msg);
		}

		// If no node available from start and end node : game over
		if (isNodeAvailable() ) {
			return gameOverResponse(point);
		}

		// if user is selected proper adjuscent node
		if (adjuscentPoint.contains(point) && !isFirstMove) {
			updatePlayer();
			pointsAlreadyMet.put((Point) state.get(Constants.STATE_START), point);
			findNotVisitedNode();
			Point start = (Point) state.get(Constants.STATE_START);
			return getResponse(Constants.VALID_END_NODE, getPlayer(), start, point, null);
		}

		// if node-clicked event is already started but not the end point
		if (state.containsKey(Constants.STATE_START) && !state.containsKey(Constants.STATE_END)) {
			state.put(Constants.STATE_END, point);
			Point start = (Point) state.get(Constants.STATE_START);
			Point end = (Point) state.get(Constants.STATE_END);
			msg = "Select a second node to complete the line.";
			return getResponse(Constants.VALID_START_NODE, getPlayer(), start, end, msg);
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
		} else {
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

	/**
	 * This method helps to update the first and last node based on the user clicked.
	 * @param state : state map
	 * @param point : points or co-ordinates clicked by player
	 */
	private void updateFirstLastNode(Map<String, Object> state, Point point) {
		Point first = (Point) state.get(Constants.STATE_NODE_FIRST);
		Point last = (Point) state.get(Constants.STATE_NODE_LAST);

		if (point.equals(first)) {
			state.put(Constants.IS_FIRST_NODE_CLICKED, true);
		} else if (point.equals(last)) {
			state.put(Constants.IS_FIRST_NODE_CLICKED, false);
		}
		// If the move sencod move then only update First/last node
		if ((Boolean) state.get(Constants.IS_FIRST_MOVE) == false) {
			if ((Boolean) state.get(Constants.IS_FIRST_NODE_CLICKED) == true) {
				state.put(Constants.STATE_NODE_FIRST, point);
			} else {
				state.put(Constants.STATE_NODE_LAST, point);
			}
		}

	}

	/**
	 * This method helps to find the node which is not visited
	 *
	 * @param node : Node to be checked from the not visited list
	 * @return : returns the list of not visited nodes.
	 */
	private void findNotVisitedNode() {
		availableAdjuscentPoints.put((Point) state.get(Constants.STATE_NODE_FIRST),
				findNotVisitedNode((Point) state.get(Constants.STATE_NODE_FIRST)));
		availableAdjuscentPoints.put((Point) state.get(Constants.STATE_NODE_LAST),
				findNotVisitedNode((Point) state.get(Constants.STATE_NODE_LAST)));
		findAndUpdateAdjuscentNode((Point) state.get(Constants.STATE_NODE_LAST));
	}

	private List<Point> findNotVisitedNode(Point node) {
		List<Point> adjuscentPoint = (List<Point>) availableAdjuscentPoints.get(node);
		List<Point> availableNodes = new ArrayList<Point>();
		for (Point p : adjuscentPoint) {
			if (pointsAlreadyMet.containsKey(p) || pointsAlreadyMet.containsValue(p)) {

			} else {
				if (!node.equals(p))
					availableNodes.add(p);
			}
		}
		
		return availableNodes;
	}

	private boolean findAndUpdateAdjuscentNode(Point node) {
		List<Point> adjuscentPoint = (List<Point>) adjuscentPoints.get(node);
		List<Point> availablePoint = (List<Point>) availableAdjuscentPoints.get(node);

		List<Point> availableNodes = new ArrayList<Point>();
		for (Point p : availablePoint) {
			availableAdjuscentPoints.put(p,findNotVisitedNode(p));

		}
		return availableNodes.isEmpty();
	}

	/**
	 * This method checks the node is available of not
	 * @return : returns the true if node is not visited else false.
	 */
	private boolean isNodeAvailable() {
		return findNotVisitedNode((Point) state.get(Constants.STATE_END)).isEmpty() ? true : false;
	}

	/**
	 * This method update the player turn
	 * @return : returns void.
	 */
	private void updatePlayer() {
		Integer player = (Integer) state.get(Constants.PLAYER);
		if (player == 1) {
			++player;
			state.put(Constants.PLAYER, player);
		} else {
			--player;
			state.put(Constants.PLAYER, player);
		}
	}

	private String getPlayer() {
		Integer player = (Integer) state.get(Constants.PLAYER);
		return Constants.PLAYER_NAME.toString() + player;
	}

	private boolean isNotDiagonalNode(Point node) {
		List<Point> adjuscentPoint = (List<Point>) adjuscentPoints.get(node);
		List<Point> availablePoint = (List<Point>) availableAdjuscentPoints.get(node);
		List<Point> chkAdj = (List<Point>) availableAdjuscentPoints.get(availablePoint.get(0));

		Point diagonalStart = new Point(node.getX(), node.getY());
		Point diagonalEnd = new Point(node.getX(), node.getY());

		if (node.getX() != 0)
			diagonalStart = new Point(node.getX() - 1, node.getY());
		diagonalEnd = new Point(node.getX(), node.getY() + 1);
		List<Point> availableNodes = new ArrayList<Point>();
		for (Point p : availablePoint) {
			if ((pointsAlreadyMet.containsKey(diagonalStart) && pointsAlreadyMet.containsValue(diagonalStart))
					|| (pointsAlreadyMet.containsKey(diagonalEnd) && pointsAlreadyMet.containsValue(diagonalEnd))) {

			} else {
				if (!node.equals(p))
					availableNodes.add(p);
			}
		}
		return availableNodes.isEmpty();
	}

	/**
	 * This method construct game over response
	 * @return : returns Response
	 */
	private Response gameOverResponse(Point point) {
		pointsAlreadyMet.put((Point) state.get(Constants.STATE_START), point);
		findNotVisitedNode();
		Point start = (Point) state.get(Constants.STATE_START);
		String msg = "Player " + state.get(Constants.PLAYER) + " Wins!";
		return getResponse(Constants.GAME_OVER, Constants.MSG_GAME_OVER, start, point, msg);

	}
}
