/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.forth.ics.isl.lattice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author micha
 */
public class Lattices_Algorithms {

    TreeMap<Integer, Set<String>> ranking = new TreeMap<>();
    int maxLevel = 10;
    private Map<Integer, TreeSet<String>> subsets;
    int dcID = 0;
    int nodes = 0, loops, checks;
    private HashMap<String, Integer> directCountID;
    private Map<Integer, HashMap<String, HashSet<Integer>>> subsets2;
    BufferedWriter bw;
    Map<String, Map<String, Integer>> subse;


    public Map<String, Map<String, Integer>> getSubse() {
        return subse;
    }

    public void setSubse(String fName, String node) throws IOException {
        this.subse = this.setDirectCountsUnion(fName, node);
    }

    public HashMap<Integer, Integer> setDirectCountsBottomUp(String fName) throws FileNotFoundException, IOException {
        BufferedReader br = null;
        maxLevel = 0;
        String s;
        br = new BufferedReader(new FileReader(fName));
        int count = 1, zeros = 0;
        Map<Integer, HashMap<String, HashSet<Integer>>> subsets = new HashMap<Integer, HashMap<String, HashSet<Integer>>>();
        int splitCount = 1;
        File file;// = new File("split"+splitCount+".txt");
        FileWriter fw;// = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw;// = new BufferedWriter(fw);
        HashMap<Integer, Integer> ret = new HashMap<Integer, Integer>();
        HashMap<String, Integer> directCountID = new HashMap<String, Integer>();
        while ((s = br.readLine()) != null) {
            String[] p = s.split("\t");
            String[] line = p[0].split(",");

            String b = p[0]; //b.substring(0, b.length() - 1);
            if (!subsets.containsKey(line.length)) {
                subsets.put(line.length, new HashMap<String, HashSet<Integer>>());
            }
            if (!subsets.get(line.length).containsKey(b)) {
                subsets.get(line.length).put(b, new HashSet<Integer>());
            }
            if (!directCountID.containsKey(b)) {
                splitCount++;
                directCountID.put(b, dcID);
                ret.put(dcID, Integer.parseInt(p[1]));
                dcID++;
            } else {
                ret.put(directCountID.get(b), ret.get(directCountID.get(b)) + Integer.parseInt(p[1]));
                //directCountID.put(b, ret.get(b) + 1);
            }
            if (line.length > maxLevel) {
                maxLevel = line.length;
                //  System.out.println(s);
            }
            //}
        }
        // System.out.println("Split Count: " + splitCount);
        this.setDirectCountID(directCountID);
        this.setSubsets2(subsets);
        return ret;
    }

    public void topDownMethod(String fName) throws IOException {
        HashMap<Integer, Integer> array = this.setDirectCountsBottomUp(fName);
        Map<Integer, HashMap<String, HashSet<Integer>>> subsetsD = this.subsets2;
        int level = maxLevel;
        // System.out.println("Level:"+level);
        int subsetsNum = 0, allDescs = 0;
        Map<String, Set<Integer>> descendants = new HashMap<>();
        FileWriter fw = null;
        BufferedWriter bw = null;
        int nodesNum = 0;

        while (level > 1) {
            //System.out.println(level);
            HashMap<String, HashSet<Integer>> levelB = subsetsD.get(level);
            if (!subsetsD.containsKey(level - 1)) {
                subsetsD.put(level - 1, new HashMap<String, HashSet<Integer>>());
            }

            int aa = 0;
            int descs = 0;
            for (Iterator<String> it = levelB.keySet().iterator(); it.hasNext();) {
                String node = it.next();
                subsetsNum++;
                if (this.directCountID.containsKey(node)) {
                    checks++;
                    levelB.get(node).add(this.directCountID.get(node));
                }

                descs += levelB.get(node).size();
                int score = 0;
                for (int subset : levelB.get(node)) {
                    score += array.get(subset);
                }
                //System.out.println(node + " " + score);
                nodesNum++;
                String[] substs = node.split(",");
                HashMap<String, HashSet<Integer>> levelB1 = subsetsD.get(level - 1);
                for (int j = 0; j < substs.length; j++) {
                    String sbs = "";
                    for (int i = 0; i < substs.length; i++) {
                        if (i != j) {
                            sbs += substs[i] + ",";
                        }

                    }
                    sbs = sbs.substring(0, sbs.length() - 1);
                    if (!levelB1.containsKey(sbs)) {
                        levelB1.put(sbs, new HashSet<Integer>());
                    }
                    checks+=levelB.get(node).size();
                   // System.out.println(checks);
                    levelB1.get(sbs).addAll(levelB.get(node));

                }
                it.remove();

            }
            subsetsD.get(level).clear();
            subsetsD.remove(level);
            allDescs += descs;
            //System.out.println("======" + level + " " + nodesNum + " Descs:" + descs);
            String value = "";
            String maxs = " ";
            level--;

        }
        //  System.out.println("All Subsets: " + subsetsNum + "All Desks:" + allDescs);

    }

    private void setDirectCountID(HashMap<String, Integer> directCountID) {
        this.directCountID = directCountID; //To change body of generated methods, choose Tools | Templates.
    }

    private void setSubsets2(Map<Integer, HashMap<String, HashSet<Integer>>> subsets) {
        this.subsets2 = subsets;
    }

    public Map<Integer, Map<String, Map<String, Integer>>> setDirectCountsTopDown(String fName) throws FileNotFoundException, IOException {
        BufferedReader br = null;
        String sCurrentLine;
        br = new BufferedReader(new FileReader(fName));
        int count = 1, zeros = 0;
        Map<String, Map<String, Integer>> descendants = new HashMap<>();
        Map<Integer, Map<String, Map<String, Integer>>> subsets = new TreeMap<Integer, Map<String, Map<String, Integer>>>();
        Map<String, Integer> scanIndex = new TreeMap<String, Integer>();
        int splitCount = 1;
        File file;// = new File("split"+splitCount+".txt");
        FileWriter fw;// = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw;// = new BufferedWriter(fw);
        HashMap<String, Integer> ret = new HashMap<String, Integer>();
        subsets.put(2, new TreeMap<String, Map<String, Integer>>());
        Map<Integer, Integer> dcNum = new HashMap<Integer, Integer>();
        int countNumberOfUnique = 0;
        int loops = 0;
        while ((sCurrentLine = br.readLine()) != null) {
            //System.out.println(u);
            String[] line = sCurrentLine.split("\t")[0].split(",");
            for (int i = 0; i < line.length - 1; i++) {
                String word = line[i];
                String sub = "";
                int cc = 0;

                for (int j = i + 1; j < line.length; j++) {
                    word = line[i] + "," + line[j];
                    if (!subsets.get(2).containsKey(word)) {
                        subsets.get(2).put(word, new HashMap<String, Integer>());
                        // descendants.put(word, new HashMap<String, Integer>());
                    }
                    sub = word;
                    String rest = "";
                    //System.out.println(sub);
                    for (int k = j + 1; k < line.length; k++) {
                        rest += line[k];
                        if (k + 1 != line.length) {
                            rest += ",";
                        }
                    }
                    if (subsets.get(2).get(word).containsKey(rest)) {
                        subsets.get(2).get(word).put(rest, subsets.get(2).get(word).get(rest) + Integer.parseInt(sCurrentLine.split("\t")[1]));
                    } else {
                        subsets.get(2).get(word).put(rest, Integer.parseInt(sCurrentLine.split("\t")[1]));
                    }

                    cc++;
                    loops++;
                }

            }

        }
        System.out.println(loops);
        System.out.println(maxLevel);
        //this.setDescendantsOf2(descendants);
        //this.setSubsetsBup(subsets);
        return subsets;
    }

    public void print() {
        System.out.println(loops);
    }

    public Map<String, Map<String, Integer>> setDirectCountsUnion(String fName, String node) throws FileNotFoundException, IOException {

        BufferedReader br = null;
        String sCurrentLine;
        HashSet<String> triads = new HashSet<String>();
        br = new BufferedReader(new FileReader(fName));
        int count = 1, zeros = 0;
        Map<String, Map<String, Integer>> descendants = new HashMap<>();
        Map<String, Map<String, Integer>> subsets = new TreeMap<String, Map<String, Integer>>();
        Map<String, Integer> scanIndex = new TreeMap<String, Integer>();
        int splitCount = 1;
        File file;// = new File("split"+splitCount+".txt");
        FileWriter fw;// = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw;// = new BufferedWriter(fw);
        int countNumberOfUnique = 0;
        int loops = 0;
        while ((sCurrentLine = br.readLine()) != null) {
            //System.out.println(u);
            String[] line = sCurrentLine.split("\t")[0].split(",");
            for (int i = 0; i < line.length; i++) {
                String word = line[i];
                String sub = "";
                int cc = 0;
                sub = word;
                String rest = "";
                if (line[i].equals(node)) {
                    if (subsets.containsKey(word)) {
                        subsets.get(word).put(sCurrentLine.split("\t")[0], Integer.parseInt(sCurrentLine.split("\t")[1]));
                    } else {
                        TreeMap<String, Integer> tr = new TreeMap<>();
                        tr.put(sCurrentLine.split("\t")[0], Integer.parseInt(sCurrentLine.split("\t")[1]));
                        subsets.put(word, tr);

                    }
                } else if (!Arrays.asList(line).contains(node)) {
                    if (subsets.containsKey(word)) {
                        subsets.get(word).put(sCurrentLine.split("\t")[0], Integer.parseInt(sCurrentLine.split("\t")[1]));
                    } else {
                        TreeMap<String, Integer> tr = new TreeMap<>();
                        tr.put(sCurrentLine.split("\t")[0], Integer.parseInt(sCurrentLine.split("\t")[1]));
                        subsets.put(word, tr);

                    }
                }

            }

        }
        for (String k : subsets.keySet()) {
            System.out.println(k + "\t" + subsets.get(k).size());
        }
        System.out.println(triads.size());
        //subsets.remove("23");
        return subsets;
    }

    public Map<String, Map<String, Map<String, Integer>>> setDirectCountsUnionNew(String fName) throws FileNotFoundException, IOException {

        BufferedReader br = null;
        String sCurrentLine;
        br = new BufferedReader(new FileReader(fName));
        int count = 1, zeros = 0;
        Map<String, Map<String, Integer>> descendants = new HashMap<>();
        Map<String, Map<String, Map<String, Integer>>> subsets = new HashMap<String, Map<String, Map<String, Integer>>>();
        Map<String, Integer> scanIndex = new TreeMap<String, Integer>();
        int splitCount = 1;
        File file;// = new File("split"+splitCount+".txt");
        FileWriter fw;// = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw;// = new BufferedWriter(fw);
        int countNumberOfUnique = 0;
        int loops = 0;
        while ((sCurrentLine = br.readLine()) != null) {
            //System.out.println(u);
            String[] line = sCurrentLine.split("\t")[0].split(",");
            for (int i = 0; i < line.length; i++) {
                String word = line[i];
                String sub = "";
                int cc = 0;
                sub = word;
                String rest = "";

                if (subsets.containsKey(word)) {
                    if (subsets.get(word).containsKey(line[0])) {
                        subsets.get(word).get(line[0]).put(sCurrentLine.split("\t")[0], Integer.parseInt(sCurrentLine.split("\t")[1]));
                    } else {
                        TreeMap<String, Integer> tr = new TreeMap<>();
                        tr.put(sCurrentLine.split("\t")[0], Integer.parseInt(sCurrentLine.split("\t")[1]));

                        subsets.get(word).put(line[0], tr);

                    }
                    //subsets.get(word).put();
                } else {
                    TreeMap<String, Map<String, Integer>> newTr = new TreeMap<>();
                    TreeMap<String, Integer> tr = new TreeMap<>();
                    tr.put(sCurrentLine.split("\t")[0], Integer.parseInt(sCurrentLine.split("\t")[1]));
                    String startsWith = line[0];
                    newTr.put(startsWith, tr);
                    subsets.put(word, newTr);

                }
            }

        }
        for (String k : subsets.keySet()) {

            for (String k1 : subsets.get(k).keySet()) {
                // System.out.println("===="+k1);
                System.out.println(subsets.get(k).get(k1));
            }
            System.out.println(k + "\t" + subsets.get(k).size());
        }
        //subsets.remove("23");
        return subsets;
    }

   
   
    public HashMap<ArrayList<String>, Integer> setUnionDC(String fname, int i, int max) {

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fname));
            String s;

            HashMap<ArrayList<String>, Integer> ret = new HashMap<>();
            int sc = 0;
            HashMap<Integer, Integer> sum = new HashMap<>();
            int count = 0;
            while ((s = br.readLine()) != null) {
                // if(s.contains("6"))
                if (s.split("\t").length != 2) {
                    continue;
                }
                ArrayList<String> dtsets = new ArrayList<String>();
                for (String k : s.split("\t")[0].split(",")) {
                    if (Integer.parseInt(k) >= i && Integer.parseInt(k) <= max) {
                        dtsets.add(k);
                    }
                }

                if (dtsets.size() > 0 && Integer.parseInt(dtsets.get(dtsets.size() - 1)) >= i) {
                    if (ret.containsKey(dtsets)) {
                        ret.put(dtsets, ret.get(dtsets) + Integer.parseInt(s.split("\t")[1]));
                    } else {
                        ret.put(dtsets, Integer.parseInt(s.split("\t")[1]));
                    }
                }
                // dtsets.addAll(Arrays.asList(s.split("\t")[0].split(",")));
                // int last = Integer.parseInt(s.split("\t")[0].split(",")[s.split("\t")[0].split(",").length - 1]);
                //System.out.println(last+" "+i);
                //if (last >= i) {
                //    ret.put(dtsets, Integer.parseInt(s.split("\t")[1]));
                // }

            }
            count += ret.size();
            //   System.out.println(count);
            return ret;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public HashMap<Integer, HashMap<ArrayList<String>, Integer>> setUnionDCPruning(String fname, int max) {

        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(fname));
            String s;
            HashMap<Integer, HashMap<ArrayList<String>, Integer>> map = new HashMap<>();
            for (int t = 1; t <= max; t++) {
                map.put(t, new HashMap<>());
            }
            HashMap<ArrayList<String>, Integer> ret = new HashMap<>();
            int sc = 0;
            HashMap<Integer, Integer> sum = new HashMap<>();
            int count = 0;
            while ((s = br.readLine()) != null) {
                // if(s.contains("6"))
                if (s.split("\t").length != 2) {
                    continue;
                }
                String[] ids = s.split("\t")[0].split(",");
                ArrayList<String> dtsets = new ArrayList<String>();
                HashSet<Integer> already = new HashSet<>();
                int cmin = 0, cmax = max;
                for (int p = ids.length - 1; p >= 0; p--) {

                    if (Integer.parseInt(ids[p]) <= max) {
                        dtsets.add(ids[p]);
                        cmin = Integer.parseInt(ids[p]);
                        ArrayList<String> toAdd = new ArrayList<String>(dtsets);

                        if (p == 0) {
                            cmin = 1;
                        }

                        for (int i = cmin; i <= cmax; i++) {

                            ArrayList<String> toAdd2 = new ArrayList<String>(toAdd);
                            if (i > Integer.parseInt(ids[p])) {
                                toAdd2.remove(ids[p]);
                            }
                            if (toAdd2.size() > 0) {
                                if (map.get(i).containsKey(toAdd2)) {

                                    map.get(i).put(toAdd2, map.get(i).get(toAdd2) + Integer.parseInt(s.split("\t")[1]));
                                } else {
                                    map.get(i).put(toAdd2, Integer.parseInt(s.split("\t")[1]));
                                }

                            }

                        }

                        cmax = cmin - 1;
                    }

                    // dtsets.addAll(Arrays.asList(s.split("\t")[0].split(",")));
                    // int last = Integer.parseInt(s.split("\t")[0].split(",")[s.split("\t")[0].split(",").length - 1]);
                    //System.out.println(last+" "+i);
                    //if (last >= i) {
                    //    ret.put(dtsets, Integer.parseInt(s.split("\t")[1]));
                    // }
                }
            }
            count += ret.size();
            //   System.out.println(count);
            return map;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public HashMap<Integer, HashMap<ArrayList<String>, Integer>> setIntersectionDC(String fname, int max) {
        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(fname));
            String s;
            HashMap<Integer, HashMap<ArrayList<String>, Integer>> map = new HashMap<>();

            for (int t = 0; t <= max; t++) {
                map.put(t, new HashMap<>());
            }
            HashMap<ArrayList<String>, Integer> ret = new HashMap<>();
            int sc = 0;
            HashMap<Integer, Integer> sum = new HashMap<>();
            int count = 0;
            while ((s = br.readLine()) != null) {
                // if(s.contains("6"))
                ArrayList<String> dtsets = new ArrayList<String>();
                boolean flag = false;
                String[] ids = s.split("\t")[0].split(",");
                for (int p = ids.length - 1; p >= 0; p--) {
                    if (Integer.parseInt(ids[p]) <= max) {
                        dtsets.add(ids[p]);
                        ArrayList<String> toAdd = new ArrayList<String>(dtsets);

                        if (toAdd.size() > 0) {
                            if (map.get(Integer.parseInt(ids[p])).containsKey(toAdd)) {

                                map.get(Integer.parseInt(ids[p])).put(toAdd, map.get(Integer.parseInt(ids[p])).get(toAdd) + Integer.parseInt(s.split("\t")[1]));
                            } else {
                                map.get(Integer.parseInt(ids[p])).put(toAdd, Integer.parseInt(s.split("\t")[1]));
                            }

                        }
                    }

                }
                //if(flag==false)
                //  continue;

                // dtsets.addAll(Arrays.asList(s.split("\t")[0].split(",")));
                // int last = Integer.parseInt(s.split("\t")[0].split(",")[s.split("\t")[0].split(",").length - 1]);
                //System.out.println(last+" "+i);
                //if (last >= i) {
                //    ret.put(dtsets, Integer.parseInt(s.split("\t")[1]));
                // }
            }
            count += ret.size();
            // System.out.println(count);
            return map;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    
    public void lattice_based_Intersection_without_Pruning(ArrayList<String> order, int level, String fName, String nod, int scoreAll, String newNode, String oldNode, Map<ArrayList<String>, Integer> ups,  int maxB, boolean print) throws IOException, CloneNotSupportedException {
        int max = maxB;
        if (level == 2) {
            HashMap<Integer, HashMap<ArrayList<String>, Integer>> ups1 = this.setIntersectionDC(fName, max);
            for (int j = 1; j <= max; j++) {
                String node = Integer.toString(j);
                int score = 0;

                //System.out.println(node + " " + ups1.get(j));
                for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups1.get(j).entrySet().iterator(); it.hasNext();) {

                    Map.Entry<ArrayList<String>, Integer> entry = it.next();
                    checks++;//= entry.getKey().size();
                    //System.out.println(entry.getKey());
                    if (!entry.getKey().contains(node)) {
                        int cScore = entry.getValue();
                        it.remove();
                    } else {
                        int cScore = entry.getValue();
                        score += cScore;

                    }
                }
                //Map<ArrayList<String>, Integer> upsN = new HashMap(ups1.get(j));

                //System.out.println(node + " " + score);
                for (int i = j + 1; i <= max; i++) {
                   // TrieInt tr1 = triesInt.get(Integer.toString(i - 1));
                    String newN = node + "," + i;
                    lattice_based_Intersection_without_Pruning(order, 3, null, newN, score, Integer.toString(i), node, new HashMap(ups1.get(j)), maxB, print);
                }
            }
        } else {
            nodes++;

            if (nodes % 10000 == 0) {
                //System.out.println(nodes);
            }
            int score = 0;// scoreAll;
            boolean gBreak = false;
            Set<ArrayList<String>> remove = new HashSet<>();

            for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups.entrySet().iterator(); it.hasNext();) {
                Map.Entry<ArrayList<String>, Integer> entry = it.next();
                checks++;//=entry.getKey().size();//++;
                if (!entry.getKey().contains(newNode)) {
                    int cScore = entry.getValue();
                    it.remove();
                } else {
                    int cScore = entry.getValue();
                    score += cScore;

                }

            }

            if (print == true) {
                System.out.println(nod + " " + score);
            }
         

            level++;
            for (int i = Integer.parseInt(newNode) + 1; i <= max; i++) {
                String node = nod + "," + i;
                lattice_based_Intersection_without_Pruning(order, level, null, node, score, Integer.toString(i), nod, new HashMap(ups), maxB, print);

            }

        }
    }


    public void lattice_based_Intersection_Pruning(ArrayList<String> order, int level, String fName, String nod, int scoreAll, String newNode, String oldNode, Map<ArrayList<String>, Integer> ups,  int maxB, boolean print) throws IOException, CloneNotSupportedException {
        int max = maxB;
        if (level == 2) {
            HashMap<Integer, HashMap<ArrayList<String>, Integer>> ups1 = this.setIntersectionDC(fName, max);
            for (int j = 1; j <= max; j++) {
                String node = Integer.toString(j);
                int score = 0;

                //System.out.println(node + " " + ups1.get(j));
                for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups1.get(j).entrySet().iterator(); it.hasNext();) {

                    Map.Entry<ArrayList<String>, Integer> entry = it.next();
                    checks++;//= entry.getKey().size();
                    //System.out.println(entry.getKey());
                    if (!entry.getKey().contains(node)) {
                        int cScore = entry.getValue();
                        it.remove();
                    } else {
                        int cScore = entry.getValue();
                        score += cScore;

                    }
                }
                //Map<ArrayList<String>, Integer> upsN = new HashMap(ups1.get(j));

                //System.out.println(node + " " + score);
                for (int i = j + 1; i <= max; i++) {
                   // TrieInt tr1 = triesInt.get(Integer.toString(i - 1));
                    String newN = node + "," + i;
                    lattice_based_Intersection_Pruning(order, 3, null, newN, score, Integer.toString(i), node, new HashMap(ups1.get(j)),  maxB, print);
                }
            }
        } else {
            nodes++;

            if (nodes % 10000 == 0) {
                //System.out.println(nodes);
            }
            int score = 0;// scoreAll;
            boolean gBreak = false;
            Set<ArrayList<String>> remove = new HashSet<>();
            Map<ArrayList<String>, Integer> upsX = new HashMap<>();

            for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups.entrySet().iterator(); it.hasNext();) {
                Map.Entry<ArrayList<String>, Integer> entry = it.next();
                checks++;//=entry.getKey().size();//++;
                if (!entry.getKey().contains(newNode)) {
                    int cScore = entry.getValue();
                    it.remove();
                } else {
                    int cScore = entry.getValue();
                    score += cScore;
                    ArrayList<String> array = new ArrayList<>(entry.getKey());
                    Iterator<String> x = array.iterator();
                        //System.out.println("Prin"+array.size());
                    while (x.hasNext()) {

                        if (Integer.parseInt(x.next()) <= Integer.parseInt(newNode)) {
                            x.remove();
                        }

                    }
                   // System.out.println("Meta"+array.size());
                    if (upsX.containsKey(array)) {
                        upsX.put(array, upsX.get(array) + entry.getValue());
                    } else {
                        upsX.put(array, entry.getValue());
                    }

                }

            }
            Map<ArrayList<String>, Integer> ups1;

            ups1 = new HashMap(upsX);

            if (print == true) {
                System.out.println(nod + " " + score);
            }
          
            level++;
            for (int i = Integer.parseInt(newNode) + 1; i <= max; i++) {
                String node = nod + "," + i;
                lattice_based_Intersection_Pruning(order, level, null, node, score, Integer.toString(i), nod, new HashMap(ups1), maxB, print);

            }

        }
    }

  
    public void lattice_based_Union_without_Pruning(ArrayList<String> order, int level, String fName, String nod, int scoreAll, String newNode, String oldNode, Map<ArrayList<String>, Integer> ups, int maxB, boolean print, int exclude) throws IOException, CloneNotSupportedException {
        int max = maxB;
        if (level == 2) {
            HashMap<Integer, HashMap<ArrayList<String>, Integer>> ups2 = this.setUnionDCPruning(fName, max);
            for (int j = 1; j <= max; j++) {
                if (exclude == j) {
                    continue;
                }
                String node = Integer.toString(j);
                int score = 0;

                Map<ArrayList<String>, Integer> ups1 = new HashMap(ups2.get(j));
                // System.out.println(j+" "+ups1);
                for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups1.entrySet().iterator(); it.hasNext();) {
                    this.checks++;
                    Map.Entry<ArrayList<String>, Integer> entry = it.next();

                    if (entry.getKey().contains(node)) {
                        int cScore = entry.getValue();
                        score += cScore;
                        it.remove();
                    }
                }

                if (print == true) {
                    // System.out.println(node + " " + score);
                }
                //  Map<ArrayList<String>, Integer> upsN = new HashMap(ups1);
                for (int i = j + 1; i <= max; i++) {
                    String newN = node + "," + i;

                    lattice_based_Union_without_Pruning(order, 3, null, newN, score, Integer.toString(i), node, new HashMap(ups1), maxB, print, exclude);
                }
            }
        } else {

            nodes++;
            level++;
            if (nodes % 10000 == 0) {
                //    System.out.println(nodes);
            }
            int score = scoreAll;
            boolean gBreak = false;
            Set<ArrayList<String>> remove = new HashSet<>();

            for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups.entrySet().iterator(); it.hasNext();) {
                Map.Entry<ArrayList<String>, Integer> entry = it.next();
                this.checks++;
                if (entry.getKey().contains(newNode)) {

                    int cScore = entry.getValue();
                    score += cScore;
                    it.remove();
                } else if (Integer.parseInt(entry.getKey().get(0)) < Integer.parseInt(newNode)) {

                    // System.out.println(Integer.parseInt(entry.getKey().get(entry.getKey().size()-1))+" "+newNode);
                    it.remove();

                }

            }
            Map<ArrayList<String>, Integer> ups1 = new HashMap(ups);
            if (print == true) {
                System.out.println(nod + " " + score);
            }

            for (int i = Integer.parseInt(newNode) + 1; i <= max; i++) {
                if (exclude == i) {
                    continue;
                }
                String node = nod + "," + i;
                lattice_based_Union_without_Pruning(order, level, null, node, score, Integer.toString(i), nod, new HashMap(ups), maxB, print, exclude);

            }

        }
    }

    public void lattice_based_Union_Pruning(ArrayList<String> order, int level, String fName, String nod, int scoreAll, String newNode, String oldNode, Map<ArrayList<String>, Integer> ups, int maxB, boolean print, int exclude) throws IOException, CloneNotSupportedException {
        int max = maxB;
        if (level == 2) {
            HashMap<Integer, HashMap<ArrayList<String>, Integer>> ups2 = this.setUnionDCPruning(fName, max);
            for (int j = 1; j <= max; j++) {

                String node = Integer.toString(j);
                int score = 0;

                Map<ArrayList<String>, Integer> ups1 = new HashMap(ups2.get(j));

                // System.out.println(j+" "+ups1);
                for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups1.entrySet().iterator(); it.hasNext();) {
                    this.checks++;
                    Map.Entry<ArrayList<String>, Integer> entry = it.next();

                    if (entry.getKey().contains(node)) {
                        int cScore = entry.getValue();
                        score += cScore;
                        it.remove();
                    }

                }

                if (print == true) {
                    // System.out.println(node + " " + score);
                }
                //  Map<ArrayList<String>, Integer> upsN = new HashMap(ups1);
                for (int i = j + 1; i <= max; i++) {
                    String newN = node + "," + i;

                    lattice_based_Union_Pruning(order, 3, null, newN, score, Integer.toString(i), node, new HashMap(ups1), maxB, print, exclude);
                }
            }
        } else {

            nodes++;

            if (nodes % 10000 == 0) {
                //    System.out.println(nodes);
            }
            int score = scoreAll;
            boolean gBreak = false;
            Set<ArrayList<String>> remove = new HashSet<>();
            Map<ArrayList<String>, Integer> upsX = new HashMap<>();
            for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups.entrySet().iterator(); it.hasNext();) {
                Map.Entry<ArrayList<String>, Integer> entry = it.next();
                this.checks++;
                if (entry.getKey().contains(newNode)) {

                    int cScore = entry.getValue();
                    score += cScore;
                    it.remove();
                } else if (Integer.parseInt(entry.getKey().get(0)) < Integer.parseInt(newNode)) {

                    // System.out.println(Integer.parseInt(entry.getKey().get(entry.getKey().size()-1))+" "+newNode);
                    it.remove();

                } else if (level < exclude) {

                    ArrayList<String> array = new ArrayList<>(entry.getKey());
                    Iterator<String> x = array.iterator();
                    //    System.out.println("Prin"+array.size());
                    while (x.hasNext()) {

                        if (Integer.parseInt(x.next()) < Integer.parseInt(newNode)) {
                            x.remove();
                        }

                    }
                    //System.out.println("Meta"+array.size());
                    if (upsX.containsKey(array)) {
                        upsX.put(array, upsX.get(array) + entry.getValue());
                    } else {
                        upsX.put(array, entry.getValue());
                    }
                }

            }
            Map<ArrayList<String>, Integer> ups1;
            if (level < exclude) {
                ups1 = new HashMap(upsX);
            } else {
                ups1 = new HashMap(ups);
            }
            if (print == true) {
                System.out.println(nod + " " + score);
            }
            level++;
            for (int i = Integer.parseInt(newNode) + 1; i <= max; i++) {

                String node = nod + "," + i;
                lattice_based_Union_Pruning(order, level, null, node, score, Integer.toString(i), nod, new HashMap(ups1), maxB, print, exclude);

            }

        }
    }

    public void recursiveSFUnionNoPruning(ArrayList<String> order, int level, String fName, String nod, int scoreAll, String newNode, String oldNode, Map<ArrayList<String>, Integer> ups, int maxB, boolean print, int exclude) throws IOException, CloneNotSupportedException {
        int max = maxB;

        if (level == 2) {
            this.checks = 0;
            Map<ArrayList<String>, Integer> ups2 = this.setUnionDC(fName, 1, max);
            for (int j = 1; j <= max; j++) {
                if (j == exclude) {
                    continue;
                }
                String node = Integer.toString(j);
                int score = 0;
                Map<ArrayList<String>, Integer> ups1 = new HashMap<>(ups2);
                //System.out.println(j+" "+ups1.size());

                for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups1.entrySet().iterator(); it.hasNext();) {
                    this.checks++;
                    Map.Entry<ArrayList<String>, Integer> entry = it.next();
                    if (entry.getKey().contains(node)) {
                        int cScore = entry.getValue();
                        score += cScore;
                        it.remove();
                    } else if (Integer.parseInt(entry.getKey().get(entry.getKey().size() - 1)) < j) {
                        // System.out.println(Integer.parseInt(entry.getKey().get(entry.getKey().size()-1))+" "+newNode);
                        it.remove();

                    }

                }
                Map<ArrayList<String>, Integer> upsN = new HashMap(ups1);
                //
                for (int i = j + 1; i <= max; i++) {
                    if (i == exclude) {
                        continue;
                    }
                    String newN = node + "," + i;
                    recursiveSFUnionNoPruning(order, 3, null, newN, score, Integer.toString(i), node, new HashMap(upsN), maxB, print, exclude);
                }
            }
        } else {

            nodes++;
            level++;
            if (nodes % 10000 == 0) {
                //  System.out.println(nodes);
            }
            int score = scoreAll;
            boolean gBreak = false;
            Set<ArrayList<String>> remove = new HashSet<>();

            for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups.entrySet().iterator(); it.hasNext();) {
                Map.Entry<ArrayList<String>, Integer> entry = it.next();
                this.checks++;
                if (entry.getKey().contains(newNode)) {
                    int cScore = entry.getValue();
                    score += cScore;
                    it.remove();
                } else if (Integer.parseInt(entry.getKey().get(entry.getKey().size() - 1)) < Integer.parseInt(newNode)) {
                    // System.out.println(Integer.parseInt(entry.getKey().get(entry.getKey().size()-1))+" "+newNode);
                    it.remove();

                }

                // 
            }
            if (print == true) {
                System.out.println(nod + " " + score);
            }

            Map<ArrayList<String>, Integer> ups1 = new HashMap(ups);
            for (int i = Integer.parseInt(newNode) + 1; i <= max; i++) {
                String node = nod + "," + i;
                if (i == exclude) {
                    continue;
                }
                recursiveSFUnionNoPruning(order, level, null, node, score, Integer.toString(i), nod, new HashMap(ups), maxB, print, exclude);
            }

        }
    }

    public void DCSUnion(ArrayList<String> order, int level, String fName, String nod, int scoreAll, String newNode, String oldNode, Map<ArrayList<String>, Integer> ups, int maxB, boolean print, HashMap<String, Integer> list, int exclude) throws IOException, CloneNotSupportedException {
        int max = maxB;
        if (level == 2) {
            BufferedReader br = new BufferedReader(new FileReader(fName));
            String sCurrentLine = "";
            HashMap<String, Integer> dcList = new HashMap<>();
            while ((sCurrentLine = br.readLine()) != null) {
                String[] split = sCurrentLine.split("\t");
                // String[] split2=split[1].split(",");
                //if(dcList.containsKey(split[0]))
                //  System.out.println("hi");
                dcList.put(split[0], Integer.parseInt(split[1]));
            }
            Map<ArrayList<String>, Integer> ups2 = this.setUnionDC(fName, 1, max);
            for (int j = 1; j <= max; j++) {
                if (j == exclude) {
                    continue;
                }
                String node = Integer.toString(j);
                int score = 0;
                Map<ArrayList<String>, Integer> ups1 = new HashMap<>(ups2);
                for (Iterator<Map.Entry<ArrayList<String>, Integer>> it = ups1.entrySet().iterator(); it.hasNext();) {
                    Map.Entry<ArrayList<String>, Integer> entry = it.next();
                    if (entry.getKey().contains(node)) {
                        int cScore = entry.getValue();
                        score += cScore;
                        it.remove();
                    }
                }
                Map<ArrayList<String>, Integer> upsN = new HashMap(ups1);
                //
                for (int i = j + 1; i <= max; i++) {
                    if (i == exclude) {
                        continue;
                    }
                    String newN = node + "," + i;
                    DCSUnion(order, 3, fName, newN, score, Integer.toString(i), node, new HashMap(upsN), maxB, print, dcList, exclude);
                }
            }
        } else {

            nodes++;
            level++;
            if (nodes % 10000 == 0) {
                //  System.out.println(nodes);
            }
            int score = 0;
            boolean gBreak = false;
            Set<ArrayList<String>> remove = new HashSet<>();

            BufferedReader br = new BufferedReader(new FileReader(fName));
            String sCurrentLine = "";
            // nod="5,6";
            for (String ls : list.keySet()) {
                HashSet<String> set = new HashSet<String>();
                for (String k : ls.split(",")) {
                    set.add(k);
                }
                boolean flag = true;
                for (String p : nod.split(",")) {
                    // System.out.println(nod+" "+p);
                    if (set.contains(p)) {
                        score += list.get(ls);
                        break;
                    }

                }

                //score+=list.get(ls);
            }
            if (print == true) {
                System.out.println(nod + " " + score);
            }

            Map<ArrayList<String>, Integer> ups1 = new HashMap(ups);
            for (int i = Integer.parseInt(newNode) + 1; i <= max; i++) {
                String node = nod + "," + i;
                if (i == exclude) {
                    continue;
                }
                DCSUnion(order, level, fName, node, score, Integer.toString(i), nod, new HashMap(ups), maxB, print, list, exclude);
            }

        }
    }

    public void DCSIntersection(ArrayList<String> order, int level, String fName, String nod, int scoreAll, String newNode, String oldNode, Map<ArrayList<String>, Integer> ups, int maxB, boolean print, HashMap<String, Integer> list) throws IOException, CloneNotSupportedException {
        int max = maxB;
        if (level == 2) {
            BufferedReader br = new BufferedReader(new FileReader(fName));
            String sCurrentLine = "";
            HashMap<String, Integer> dcList = new HashMap<>();
            while ((sCurrentLine = br.readLine()) != null) {
                String[] split = sCurrentLine.split("\t");
                // String[] split2=split[1].split(",");
                //if(dcList.containsKey(split[0]))
                //  System.out.println("hi");
                dcList.put(split[0], Integer.parseInt(split[1]));
            }
            //Map<ArrayList<String>, Integer> ups2 = this.setUnionDC(fName, 1,max);
            for (int j = 1; j <= max; j++) {
                String node = Integer.toString(j);
                int score = 0;
                for (int i = j + 1; i <= max; i++) {
                    String newN = node + "," + i;
                    DCSIntersection(order, 3, fName, newN, score, Integer.toString(i), node, null, maxB, print, dcList);
                }
            }
        } else {

            nodes++;
            level++;
            if (nodes % 10000 == 0) {
                //  System.out.println(nodes);
            }
            int score = 0;
            boolean gBreak = false;
            Set<ArrayList<String>> remove = new HashSet<>();

            BufferedReader br = new BufferedReader(new FileReader(fName));
            String sCurrentLine = "";
            // nod="5,6";
            for (String ls : list.keySet()) {
                HashSet<String> set = new HashSet<String>();
                for (String k : ls.split(",")) {
                    set.add(k);
                }
                boolean flag = true;
                for (String p : nod.split(",")) {
                    // System.out.println(nod+" "+p);
                    if (!set.contains(p)) {
                        flag = false;
                        // score+=list.get(ls);
                        break;
                    }

                }
                if (flag == true) {
                    score += list.get(ls);
                }
                //score+=list.get(ls);
            }
            if (print == true) {
                System.out.println(nod + " " + score);
            }

            //Map<ArrayList<String>, Integer> ups1 = new HashMap(ups);
            for (int i = Integer.parseInt(newNode) + 1; i <= max; i++) {
                String node = nod + "," + i;
                DCSIntersection(order, level, fName, node, score, Integer.toString(i), nod, null, maxB, print, list);
            }

        }
    }

    public HashMap<ArrayList<String>, Integer> setIntersectionDCNoPruning(String fname, int i, int max) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fname));
            String s;

            HashMap<ArrayList<String>, Integer> ret = new HashMap<>();
            int sc = 0;
            HashMap<Integer, Integer> sum = new HashMap<>();
            int count = 0;
            while ((s = br.readLine()) != null) {
                // if(s.contains("6"))
                ArrayList<String> dtsets = new ArrayList<String>();
                boolean flag = false;
                for (String k : s.split("\t")[0].split(",")) {
                    if (Integer.parseInt(k) <= max) {
                        dtsets.add(k);
                    }
                    if (Integer.parseInt(k) == i) {
                        flag = true;
                    }
                    if (Integer.parseInt(k) > i && flag == false) {
                        break;
                    }
                }
                if (flag == false) {
                    continue;
                }
                if (dtsets.size() > 0 && Integer.parseInt(dtsets.get(dtsets.size() - 1)) >= i) {
                    if (ret.containsKey(dtsets)) {
                        ret.put(dtsets, ret.get(dtsets) + Integer.parseInt(s.split("\t")[1]));
                    } else {
                        ret.put(dtsets, Integer.parseInt(s.split("\t")[1]));
                    }
                }
                // dtsets.addAll(Arrays.asList(s.split("\t")[0].split(",")));
                // int last = Integer.parseInt(s.split("\t")[0].split(",")[s.split("\t")[0].split(",").length - 1]);
                //System.out.println(last+" "+i);
                //if (last >= i) {
                //    ret.put(dtsets, Integer.parseInt(s.split("\t")[1]));
                // }

            }
            count += ret.size();
            // System.out.println(count);
            return ret;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Lattices_Algorithms.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

   
}
