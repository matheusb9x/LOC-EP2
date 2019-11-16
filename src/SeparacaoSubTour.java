import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.LazyConstraintCallback;


public class SeparacaoSubTour extends LazyConstraintCallback {
	IloCplex cplex;
	Grafo grafo;
	double EPSILON = 0.000001;
	
	public SeparacaoSubTour(IloCplex cplex, Grafo grafo) {
		this.grafo = grafo;
		this.cplex = cplex;
	}
	
	@Override
	protected void main() throws IloException {
		//Aresta[] arestas = grafo.getArestas();
		int nv = grafo.getNumeroVertices();
		Aresta[][] graph = grafo.getMapaArestas();

		// Create a residual graph and fill the residual
		// graph with given capacities in the original
		// graph as residual capacities in residual graph
		// rGraph[i][j] indicates residual capacity of edge i-j
		double[][] rGraph = new double[nv][nv];
		for (int i = 0; i < nv; i++) {
			for (int j = 0; j < nv; j++) {
				Aresta g = graph[i][j];

				if (g != null && g.x != null)
					rGraph[i][j] = getValue(g.x);
			}
		}

		List<Aresta> corte = corteMinimo(rGraph);

		if (corte != null && corte.size() > 0) {
			IloLinearNumExpr lhs = cplex.linearNumExpr();
			for(Aresta e: corte) {
				lhs.addTerm(1.0, e.x);
			}
			add(cplex.ge(lhs, 2));
		}
	}

	private List<Aresta> corteMinimo(double[][] rGraph) {
		int nV = grafo.getNumeroVertices();

		for(int s = 0; s < nV; s++) {
			for(int t = s + 1; t < nV; t++) {
				List<Aresta> s_t_corte  = s_t_corteMinimo(rGraph, s, t);

				if(s_t_corte.size() > 0 && estaViolada(s_t_corte, rGraph)) {
					return s_t_corte;
				}
			}
		}
		
		return null;
	}

	private List<Aresta> s_t_corteMinimo(double[][] graph, int s, int t) {
		// ford-fulkerson
		int u,v;
		int nv = grafo.getNumeroVertices();
		Aresta[][] mapaArestas = grafo.getMapaArestas();

		// This array is filled by BFS and to store path
		int[] parent = new int[graph.length];

		// Augment the flow while there is path from source to sink
		while (bfs(graph, s, t, parent)) {

			// Find minimum residual capacity of the edges
			// along the path filled by BFS. Or we can say
			// find the maximum flow through the path found.
			double pathFlow = Double.MAX_VALUE;
			for (v = t; v != s; v = parent[v]) {
				u = parent[v];
				pathFlow = Math.min(pathFlow, graph[u][v]);
			}

			// update residual capacities of the edges and
			// reverse edges along the path
			for (v = t; v != s; v = parent[v]) {
				u = parent[v];
				graph[u][v] = graph[u][v] - pathFlow;
				graph[v][u] = graph[v][u] + pathFlow;
			}
		}

		// Flow is maximum now, find vertices reachable from s
		boolean[] isVisited = new boolean[graph.length];
		dfs(graph, s, isVisited);

		ArrayList<Aresta> result = new ArrayList<>();
		// Print all edges that are from a reachable vertex to
		// non-reachable vertex in the original graph
		for (int i = 0; i < mapaArestas.length; i++) {
			for (int j = 0; j < mapaArestas.length; j++) {
				if (mapaArestas[i][j] != null && isVisited[i] && !isVisited[j]) {
					result.add(mapaArestas[i][j]);
					//System.out.println(i + " - " + j);
				}
			}
		}

		return result;
	}

	// Returns true if there is a path
	// from source 's' to sink 't' in residual
	// graph. Also fills parent[] to store the path
	private static boolean bfs(double[][] rGraph, int s,
							   int t, int[] parent) {

		// Create a visited array and mark
		// all vertices as not visited
		boolean[] visited = new boolean[rGraph.length];

		// Create a queue, enqueue source vertex
		// and mark source vertex as visited
		Queue<Integer> q = new LinkedList<Integer>();
		q.add(s);
		visited[s] = true;
		parent[s] = -1;

		// Standard BFS Loop
		while (!q.isEmpty()) {
			int v = q.poll();
			for (int i = 0; i < rGraph.length; i++) {
				if (rGraph[v][i] > 0 && !visited[i]) {
					q.offer(i);
					visited[i] = true;
					parent[i] = v;
				}
			}
		}

		// If we reached sink in BFS starting
		// from source, then return true, else false
		return (visited[t] == true);
	}

	// A DFS based function to find all reachable
	// vertices from s. The function marks visited[i]
	// as true if i is reachable from s. The initial
	// values in visited[] must be false. We can also
	// use BFS to find reachable vertices
	private static void dfs(double[][] rGraph, int s,
							boolean[] visited) {
		visited[s] = true;
		for (int i = 0; i < rGraph.length; i++) {
			if ((rGraph[s][i] > 0 || rGraph[i][s] > 0 && rGraph[s][i] != rGraph[i][s]) && !visited[i]) {
				dfs(rGraph, i, visited);
			}
		}
	}

	boolean estaViolada(List<Aresta> corte, double[][] capacidades) {
		double capCorte = 0;

		for(Aresta e : corte) {
			capCorte += capacidades[e.v1][e.v2];
		}

		return capCorte < 2 - EPSILON;
	}

}
