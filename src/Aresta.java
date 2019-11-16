import ilog.concert.IloIntVar;


public class Aresta {
	IloIntVar x;
	double capacidade;
	int custo;
	int v1;
	int v2;

	public Aresta(int v1, int v2, int custo) {
		this.v1 = v1;
		this.v2 = v2;
		this.custo = custo;
	}
}
