package com.datastax.oss.driver.internal.core.metadata;

public class Nodes {
    private final Iterable<NodeInfo> nodeInfos;
    private final Iterable<NodeInfo> graphNodeInfos;

    public Nodes(Iterable<NodeInfo> nodeInfos, Iterable<NodeInfo> graphNodeInfos) {
        this.nodeInfos = nodeInfos;
        this.graphNodeInfos = graphNodeInfos;
    }

    public Iterable<NodeInfo> getNodeInfos() {
        return nodeInfos;
    }

    public Iterable<NodeInfo> getGraphNodeInfos() {
        return graphNodeInfos;
    }
}
