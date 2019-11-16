import java.util.ArrayList;

public class Grafo {
    private Aresta[] arestas;
    private Aresta[][] mapaArestas;
    private int numeroVertices;

    public Grafo(int[][] custos) {
        if (custos.length != custos[0].length)
            throw new IllegalArgumentException("A matriz deve ser quadrada");

        int nv = custos.length;
        numeroVertices = nv;
        mapaArestas = new Aresta[nv][nv];
        ArrayList<Aresta> arestasList = new ArrayList<>();

        for (int i = 0; i < nv; i++) {
            for (int j = 0; j < nv; j++) {
                int custo = custos[i][j];

                if (custo > 0) {
                    Aresta aresta = null;

                    if (i <= j) {
                        aresta = new Aresta(i, j, custo);
                        arestasList.add(aresta);
                    }
                    else
                        aresta = mapaArestas[j][i];

                    mapaArestas[i][j] = aresta;
                }
            }
        }

        arestas = arestasList.toArray(new Aresta[arestasList.size()]);
    }

    public Aresta[] getIncidentes(int vertice) {
        ArrayList<Aresta> result = new ArrayList<>();

        for (int i = 0; i < numeroVertices; i++) {
            Aresta ar = mapaArestas[vertice][i];
            if (ar != null)
                result.add(ar);
        }

        return result.toArray(new Aresta[result.size()]);
    }

    public Aresta[] getArestas() {
        return arestas;
    }

    public int getNumeroVertices() {
        return numeroVertices;
    }

    public Aresta[][] getMapaArestas() {
        return mapaArestas;
    }
}
