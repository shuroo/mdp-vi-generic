package org.jgrapht.graph;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Graph<V, E> {

    private org.jgrapht.Graph<Vertex, Edge> graph = new DefaultDirectedGraph<>(Edge.class);

    public Vertex getHead() {
        return head;
    }

    private Vertex head;

    public Vertex getTail() {
        return tail;
    }

    private Vertex tail;

    public HashMap<String, Vertex> getVertices() {
        return vertices;
    }


    public HashMap<String, Edge> getEdges() {
        return edges;
    }

    private HashMap<String, Vertex> vertices = new HashMap<java.lang.String, Vertex>();

    private HashMap<String, Edge> edges = new HashMap<String, Edge>();

    // The REAL constructor
    public Graph(java.lang.String jsonFileName) {
        readFromJson(jsonFileName);
    }

    private void init() {
        this.setHeadTail();
        this.setNextEdges();
    }

    private void setHeadTail() {
        for (Vertex v : vertices.values()) {
            if (v.isInitial()) {
                this.head = v;
            } else if (v.isFinal()) {
                this.tail = v;
            }
        }
    }

    private void setNextEdges() {

        // todo: switch the edges back to an array?

        this.edges.values().stream().map(e -> e.getSource().vertexEdges.add(e)
        ).collect(Collectors.toList());
    }

    public Boolean addVertex(Vertex vi) {
        if (graph.addVertex(vi)) {
            vertices.put(vi.toString(),vi);
            return true;
        }
        return false;
    }

    public boolean addEdge(Edge ei) {
        try {
            Vertex sourceV = vertices.get(ei.source.toString());
            Vertex destV = vertices.get(ei.target.toString());
            Edge e = graph.addEdge(sourceV, destV);
            e.setBlockingAndReward(ei.getBlockingProbability(), ei.getReward());
            edges.put(e.getId(), e);
            this.addEdge(e);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
//
//

    private void loadGraph(HashSet<Vertex> vertices, HashSet<Edge> edges) {

        vertices.stream().map(vi -> addVertex(vi)).collect(Collectors.toList());
        edges.stream().map(ei -> addEdge(ei)).collect(Collectors.toList());
        // Data structure for graph search..
        init();

    }

    private Graph readFromJson(java.lang.String filename) {
        JSONParser parser = new JSONParser();
        try {

            Object obj = parser.parse(new FileReader(filename));

            // A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
            JSONObject jsonObject = (JSONObject) obj;

            // A JSON array. JSONObject supports java.util.List interface.
            JSONArray vertices_json = (JSONArray) jsonObject.get("vertices");
            JSONArray edges_json = (JSONArray) jsonObject.get("edges");

            Gson g = new Gson();
            HashSet vertices = (HashSet) vertices_json.stream().map(vert ->
                    g.fromJson(vert.toString(), Vertex.class)).collect(Collectors.toSet());


            HashSet edges = (HashSet) edges_json.stream().map(edg ->
                    g.fromJson(edg.toString(), Edge.class)).collect(Collectors.toSet());

            loadGraph(vertices, edges);
        } catch (FileNotFoundException e) {
            System.out.println(e.getStackTrace());
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return this;
    }


}
