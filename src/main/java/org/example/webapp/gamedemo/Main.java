package org.example.webapp.gamedemo;

public class Main {
    public static void main(String[] args) {
        int arr[][] = {{11, 12, 13, 14},
                       {22, 23, 24, 25},
                       {33, 34, 35, 36},
                       {44, 45, 46, 47}
                        };
        for(int i = 0; i<4; i++) {
            for(int j =0; j<4; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }

        findDiagonalElements(arr, 4);
    }
    public static String findDiagonalElements(int[][] arr, int sizeOfMatrix) {
      for(int i = 0; i<4; i++) {
            int tempI = i;

            for(int j =0; j<4; j++) {
                // finds the adjuscent and diagonal elements
                int tempJ = j;
                tempI = i;
                System.out.print(" Adjuscent of node at : " + arr[i][j] + ":=> \n ");
               if(tempI < 4 && tempI != 3) {
                 System.out.print(arr[++tempI][tempJ]+ " ");
                }
                if(tempJ < 4 && tempJ != 3) {
                    System.out.print(" " +arr[tempI][++tempJ]);
                }
                if(tempI >= 0) {
                    if (--tempI != i && tempJ != j) {
                        System.out.print(" " + arr[--tempI][tempJ]);
                    }
                }
                System.out.println("  Done!!");
            }

            System.out.println();
        }
      /*  for(int i = 0; i<4; i++) {
            int tempI = i;

            for(int j =0; j<4; j++) {
                // finds the adjuscent and diagonal elements
                int tempJ = j;
                tempI = i;
                System.out.print(" Adjuscent of node at : " + arr[i][j] + ":=> \n ");
                if(tempJ > 0 && tempJ != 0) {
                    System.out.print(arr[tempI][--tempJ]+ " ");
                }
                if(tempI >= 0 && tempJ>=0) {
                   // System.out.print(" " +arr[--tempI][tempJ]);
                    System.out.print(" " +arr[++tempI][tempJ]);
                }
                System.out.println("  Done!!");

            System.out.println();
        }            } */

        return "";
    }
}
