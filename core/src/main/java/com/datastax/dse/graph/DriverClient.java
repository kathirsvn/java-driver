package com.datastax.dse.graph;

import com.datastax.dse.driver.api.core.graph.*;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.net.InetSocketAddress;
import java.util.List;

import static com.datastax.dse.driver.api.core.graph.DseGraph.g;

public class DriverClient {
    public static void main(String[] args) {
        CqlSession cqlSession = new CqlSessionBuilder()
                .addGraphContactPoint(new InetSocketAddress("192.168.1.12", 28182))
                .build();
        //regular cql
        ResultSet resultSet = cqlSession.execute("select * from system_schema.tables");
        for(Row r : resultSet){
            System.out.println("Table : " + r.getString("table_name"));
        }
        //graph queries
        testScriptAPI(cqlSession);
        testExplicitFluentAPI(cqlSession);
        System.out.println("Complete");
    }

    private static void testImplicitFluentAPI(CqlSession session) {
        //G
        //submit along the construction of the traversal as required
        GraphTraversalSource g = DseGraph.g
                .withRemote(DseGraph.remoteConnectionBuilder(session).build());
        List<Vertex> vertices = g.V().toList();
        System.out.println(vertices);
    }

    private static void testExplicitFluentAPI(CqlSession session) {
        //Graph
        //Graph traversal + submit
        GraphTraversal<Vertex, Vertex> traversal = g.V();
        FluentGraphStatement statement = FluentGraphStatement.newInstance(traversal).setGraphName("remote_test1");
        GraphResultSet result = session.execute(statement);
        for (GraphNode node : result) {
            System.out.println(node.asVertex());
        }
    }

    private static void testScriptAPI(CqlSession session) {
        String script = "system.graphs()";
        ScriptGraphStatement statement =
                ScriptGraphStatement.builder(script)
//                        .setQueryParam("name", "marko")
                        .build();
        GraphResultSet result = session.execute(statement);
        for (GraphNode node : result) {
            System.out.println(node.asString());
        }
    }
}
