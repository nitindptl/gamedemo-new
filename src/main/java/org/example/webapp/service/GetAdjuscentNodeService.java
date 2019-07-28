package org.example.webapp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.webapp.models.Point;
import org.springframework.stereotype.Service;

@Service
public class GetAdjuscentNodeService {

    // This is being used to store index of matrix
    private static int MATRIX[][]  = {{11, 12, 13, 14},
            {22, 23, 24, 25},
            {33, 34, 35, 36},
            {44, 45, 46, 47}
    };

    public Map<Point, List<Point>> getPoints() {
        Map<Point, List<Point>> pointsWithKey = new HashMap<>();
        for(int i = 0; i<4; i++) {
            for(int j = 0; j<4; j++) {
            	System.out.println(i+","+j+"=="+getAdjuscentPoints(i, j));
                pointsWithKey.put(new Point(i,j), getAdjuscentPoints(i, j));
            }
        }
        return pointsWithKey;
    }

    private List<Point> getAdjuscentPoints(int x, int y) {
        List<Point> points = new ArrayList<>(16);
        for(int i = x-1; i<= x+1; i++) {
            for(int j = y-1; j <= y+1; j++) {
                if(i<4 && i>= 0 && j<4 && j>= 0) {
                    points.add(new Point(i,j));
                }
             }
        }
        return points;
    }

}
