/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.lattice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 *
 * @author micha
 */
public class LatticeMeasurements {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, CloneNotSupportedException {
        Lattices_Algorithms td = new Lattices_Algorithms();

        LatticeMeasurements lm = new LatticeMeasurements();
        ArrayList<String> order = new ArrayList<String>();
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Select the Measurement Type: Commonalities or Coverage");
        String type = keyboard.next();

        if (type.equals("Commonalities")) {

            System.out.println("Select the Dataset: Entities, Literals, Triples");
            String dataset = keyboard.next();
            String intDataset = "datasets/commonalities/" + dataset + ".txt";

            System.out.println("Select the Desired Approach for computing Commonalities:"
                    + "STRAIGHTFORWARD,LATTICE_BASED_PRUNING,LATTICE_BASED_NO_PRUNING,TOP-DOWN");
            String choice = keyboard.next();

            System.out.println("Select the minimum Number of Subsets:");
            int min = keyboard.nextInt();
            System.out.println("Select the maximum Number of Subsets:");
            int max = keyboard.nextInt();
            System.out.println("Print the commonalities of all the subsets? true or false");
            boolean print2 = keyboard.nextBoolean();
            for (int i = min; i <= max; i++) {
                long startTime = System.currentTimeMillis();
                td.checks = 0;
                //SF DirectCount
                // 
                if (choice.equals("STRAIGHTFORWARD")) {
                    td.DCSIntersection(order, 2, intDataset, null, 0, "0", "", null, i, print2, null);
                } else if (choice.equals("LATTICE_BASED_NO_PRUNING")) {
                    td.lattice_based_Intersection_without_Pruning(order, 2, intDataset, null, 0, "0", "", null,  i, print2);
                } else if (choice.equals("LATTICE_BASED_PRUNING")) {
                    td.lattice_based_Intersection_Pruning(order, 2, intDataset, null, 0, "0", "", null,  i, print2);
                } else {
                    td.topDownMethod(intDataset.replace(".txt", i + ".txt"));

                }
                long estimatedTime = System.currentTimeMillis() - startTime;
                if (!choice.equals("")) {
                    // System.out.println(+i + " Subsets\tApproach:" + choice + "\tTime (Seconds):" + (double) estimatedTime / (1000));
                    System.out.println(+i + " Subsets\tApproach:" + choice + "\tTime (Seconds):\t" + (double) estimatedTime / (1000) + "\t" + (double) td.checks / (Math.pow(2, i)));

                }

            }
        } else if (type.equals("Coverage")) {
//Union Case        

            System.out.println("Select the Dataset: EntitiesDesc, LiteralsDesc, TriplesDesc, EntitiesSemiDesc,EntitiesMedium,EntitiesSemiAsc,EntitiesAsc");
            String dataset = keyboard.next();
            String unionDataset = "datasets/coverage/" + dataset + ".txt";

            System.out.println("Select the Desired Approach for computing Coverage:"
                    + "STRAIGHTFORWARD,LATTICE_BASED_PRUNING,LATTICE_BASED_NO_PRUNING");
            String choice2 = keyboard.next();
            int exclude = 2;
            System.out.println("Select the minimum Number of Subsets:");
            int min = keyboard.nextInt();
            System.out.println("Select the maximum Number of Subsets:");
            int max = keyboard.nextInt();
            System.out.println("Print the coverage of all the subsets? true or false");
            boolean print = keyboard.nextBoolean();
            for (int x = 10; x <= 10; x++) {

                for (int i = min; i <= max; i++) {
                    exclude = 1000;//(int) ((int) i*x*0.1);
                    if (i == min) {
                        System.out.println("====" + x);
                    }
                    long startTime = System.currentTimeMillis();
                    //  td.setTrieUnion(unionDataset,i);
                    td.checks = 0;
                    if (choice2.equals("STRAIGHTFORWARD")) {
                        //SF DirectCount
                        td.DCSUnion(order, 2, unionDataset, null, 0, "0", "", null, i, print, null, exclude);
                    }       
                    else if (choice2.equals("LATTICE_BASED_NO_PRUNING")) {
                        td.lattice_based_Union_without_Pruning(order, 2, unionDataset, null, 0, "0", "", null, i, print, exclude);

                    } else if (choice2.equals("LATTICE_BASED_PRUNING")) {

                        td.lattice_based_Union_Pruning(order, 2, unionDataset, null, 0, "0", "", null, i, print, exclude);

                    }    else {
                        continue;
                    }

                    long estimatedTime = System.currentTimeMillis() - startTime;
                    if (!choice2.equals("")) {
                        //System.out.println(+i + " Subsets\tApproach:" + choice2 + "\tTime (Seconds):\t" + (double) estimatedTime / (1000) + "\t" + (double) td.checks / (Math.pow(2, i)));
                        //   System.out.println((double) estimatedTime / (1000)+"\t"+(double) td.checks/(Math.pow(2, i)));
                        System.out.println(+i + " Subsets\tApproach:" + choice2 + "\tTime (Seconds):\t" + (double) estimatedTime / (1000) + "\t" + (double) td.checks / (Math.pow(2, i)));

                    }

                }
            }
            //}
        }
    }

   
}
