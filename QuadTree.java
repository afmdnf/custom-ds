import java.util.LinkedList;
import java.util.Collections;
import java.util.HashSet;
import java.util.HashMap;

/**
 * Created by Mohammed Shaikh
 */
public class QuadTree {
    private Node root;

    private LinkedList<Node> trees;
    private HashSet<Double> uniqueX;
    private HashSet<Double> uniqueY;
    
    QuadTree() {
        root = new Node(-122.2998046875, 37.892195547244356,
                -122.2119140625, 37.82280243352756, ""); // default Berkeley coordinates
    }

    QuadTree(double ullon, double ullat, double lrlon, double lrlat, String name) {
        root = new Node(ullon, ullat, lrlon, lrlat, name);
    }

    // Returns the matching image files for the requested coordinates
    public HashMap<String, Object> match(double ullon, double ullat, double lrlon, double lrlat, double w, String src) {
        HashMap<String, Object> results = new HashMap<>();
        trees = new LinkedList<>();
        uniqueX = new HashSet<>();
        uniqueY = new HashSet<>();
        double lonDPP = (lrlon - ullon) / w;
        if (lonDPP < 0) {
            return null;
        }
        matcher(ullon, ullat, lrlon, lrlat, root, lonDPP);
        if (trees.size() == 0) {
            return null;
        }
        Collections.sort(trees);

        int k = 0;
        String[][] files = new String[uniqueY.size()][uniqueX.size()];
        for (int i = 0; i < uniqueY.size() && k < trees.size(); i++) {
            for (int j = 0; j < uniqueX.size() && k < trees.size(); j++) {
                files[i][j] = src + trees.get(k++).name + ".png";
            }
        }
        results.put("render_grid", files);
        results.put("raster_ul_lon", trees.get(0).ullon);
        results.put("raster_ul_lat", trees.get(0).ullat);
        results.put("raster_lr_lon", trees.get(trees.size() - 1).lrlon);
        results.put("raster_lr_lat", trees.get(trees.size() - 1).lrlat);
        results.put("depth", trees.get(0).name.length());
        results.put("query_success", true);
        return results;
    }

    // Fills trees with Nodes matching the queried coordinates and lonDPP
    public void matcher(double ullon, double ullat, double lrlon, double lrlat, Node k, double query) {
        if (k != null) {
            if (intersect(ullon, ullat, lrlon, lrlat, k.ullon, k.ullat, k.lrlon, k.lrlat)) {
                if (checklonDPP(query, k.lrlon, k.ullon, k.name)) {
                    trees.add(k);
                    uniqueX.add(k.ullon);
                    uniqueY.add(k.ullat);
                } else {
                    matcher(ullon, ullat, lrlon, lrlat, k.UL, query);
                    matcher(ullon, ullat, lrlon, lrlat, k.UR, query);
                    matcher(ullon, ullat, lrlon, lrlat, k.BL, query);
                    matcher(ullon, ullat, lrlon, lrlat, k.BR, query);
                }
            }
        }
    }

    // Checks if the longitudinal distance per pixel (lonDPP) is within the queried lonDPP
    private boolean checklonDPP(double query, double lrlon, double ullon, String name) {
        if (name.length() == 7) { // most zoomed in file is 7 tiers down
            return true;
        }
        return ((lrlon - ullon) / 256) <= query;
    }

    // Checks whether (a, b, c, d) is contained by (ulon, ulat, llon, llat)
    private boolean intersect(double ulon, double ulat, double llon, double llat, double a, double b, double c, double d) {
        return !((llat > b) || (d > ulat) || (llon < a) || (c < ulon));
    }

    private class Node implements Comparable<Node> {
        double ullon, ullat, lrlon, lrlat; // characteristics
        Node UL, UR, BL, BR;   // four children
        String name;           // file name

        Node(double ullon, double ullat, double lrlon, double lrlat, String name) {
            this.ullon = ullon;
            this.ullat = ullat;
            this.lrlon = lrlon;
            this.lrlat = lrlat;
            this.name = name;
            setChildren();
        }

        private void setChildren() {
            if (this.name.length() == 7) {
                UL = UR = BL = BR = null;
            } else {
                UL = new Node(this.ullon, this.ullat, centerlon(), centerlat(), this.name + "1");
                UR = new Node(centerlon(), this.ullat, this.lrlon, centerlat(), this.name + "2");
                BL = new Node(this.ullon, centerlat(), centerlon(), this.lrlat, this.name + "3");
                BR = new Node(centerlon(), centerlat(), this.lrlon, this.lrlat, this.name + "4");
            }
        }

        private double centerlon() {
            return (this.ullon + this.lrlon) / 2;
        }

        private double centerlat() {
            return (this.ullat + this.lrlat) / 2;
        }

        @Override
        public int compareTo(Node n1) {
            int ans = Double.compare(this.ullat, n1.ullat);
            if (ans == 0) {
                return Double.compare(this.ullon, n1.ullon);
            }
            return -ans;
        }
    }
}
