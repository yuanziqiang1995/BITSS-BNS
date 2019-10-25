package com.gbdpcloud.bayes.entity;

import java.util.List;

public class StaticDiscreteNet {
    private List<StaticDiscreteNode> variables;
    private List<String> edges;

    public List<StaticDiscreteNode> getVariables() {
        return variables;
    }

    public void setVariables(List<StaticDiscreteNode> variables) {
        this.variables = variables;
    }

    public List<String> getEdges() {
        return edges;
    }

    public void setEdges(List<String> edges) {
        this.edges = edges;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        StringBuilder edgeStr = new StringBuilder("[");
        if (edges == null || edges.size() == 0 || edges.size() % 2 != 0) {
            System.out.println("error");
            return null;
        }
        for (int i = 0; i < edges.size(); i += 2) {
            edgeStr.append(String.format("(\'%s\',\'%s\')", edges.get(i), edges.get(i + 1)));
            if (i != edges.size() - 2)
                edgeStr.append(",");
            else edgeStr.append("]");
        }
        result.append(edgeStr);
        for (StaticDiscreteNode s : variables) {
            result.append("\n").append(s.toString());
        }
        return result.toString();
    }
}
