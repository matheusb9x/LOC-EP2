import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.BooleanParam;


public class Modelo {

	void resolve(int[][] custos) throws IloException {
		Grafo grafo = new Grafo(custos);

		IloCplex cplex = new IloCplex();
		int indice_e = 0;

		Aresta[] arestas = grafo.getArestas();
		for(Aresta e : arestas) {
			e.x = cplex.boolVar();
		}

		// monta funcao obj
		IloLinearNumExpr obj = cplex.linearNumExpr();
		for(Aresta e: arestas){
			obj.addTerm(e.custo, e.x);
		}
		cplex.addMinimize(obj);

		// inclui restricao grau
		int nV = grafo.getNumeroVertices();
		for(int s = 0; s < nV; s++) {
			IloLinearNumExpr lhs = cplex.linearNumExpr();

			for(Aresta e: grafo.getIncidentes(s)){
				if (e.x != null) {
					lhs.addTerm(1, e.x);
				}
			}
			cplex.addEq(lhs,2);
		}

		cplex.use(new SeparacaoSubTour(cplex, grafo));

		// tem que desligar o presolve
		cplex.setParam(BooleanParam.PreInd, false);

		cplex.solve();

		printResults(grafo, cplex);
	}

	private void printResults(Grafo grafo, IloCplex cplex) throws IloException {
		int nV = grafo.getNumeroVertices();

		System.out.println("Status: " + cplex.getStatus());
		System.out.println("Custo de uma atribuicao otima: " + cplex.getObjValue());

		double tolerance = cplex.getParam(IloCplex.DoubleParam.EpInt);
		Aresta[][] mapa = grafo.getMapaArestas();

		int[][] checker = new int[nV][nV];

		for (int i = 0; i < nV; i++) {
			for (int j = 0; j < nV; j++) {
				if (i <= j && mapa[i][j] != null) {
					IloIntVar x = mapa[i][j].x;
					if (x != null && cplex.getValue(x) >= 1 - tolerance) {

						checker[i][j] = 1;
						checker[j][i] = 1;

						//System.out.println("De " + i
						//		+ " Para " + j);
					}
				}
			}
		}

		int v = 0;
		boolean[] visited = new boolean[nV];
		do {
			visited[v] = true;

			for (int i = 0; i < nV; i++) {
				int aresta = checker[v][i];

				if (aresta == 1) {
					if (!visited[i]) {
						System.out.println(v + " -> " + i);

						v = i;
						break;
					}
					else if (i == 0) {
						// O vertice 0 deve ser marcado como não visitado para que a última aresta seja detectada.
						visited[i] = false;
					}
				}
			}
		} while(v != 0);

		checkIfIsValidAnswer(checker);
	}

	private void checkIfIsValidAnswer(int[][] checker) {
		int current = 0;
		boolean first = true;
		int count = 0;

		while (current != 0 || first) {
			if (count > checker.length) {
				System.out.println("INVALID ANSWER");
				break;
			}

			first = false;

			for (int i = 0; i < checker.length; i++) {
				if (checker[current][i] == 1) {
					current = i;
				}

				break;
			}

			count++;
		}

		System.out.println("VALID ANSWER");
	}
}
