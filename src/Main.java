import ilog.concert.IloException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        /*int[][] custos = new int[4][4];
        custos[0][0] = 0;
        custos[0][1] = 2;
        custos[0][2] = 5;
        custos[0][3] = 7;

        custos[1][0] = 2;
        custos[1][1] = 0;
        custos[1][2] = 3;
        custos[1][3] = 5;

        custos[2][0] = 5;
        custos[2][1] = 3;
        custos[2][2] = 0;
        custos[2][3] = 1;

        custos[3][0] = 7;
        custos[3][1] = 5;
        custos[3][2] = 1;
        custos[3][3] = 0;*/

        /*Aresta[] a = new Aresta[9];
        a[0] = new Aresta(0, 1, 2);
        a[1] = new Aresta(1, 2, 1);
        a[2] = new Aresta(2, 3, 1);
        a[3] = new Aresta(1, 4, 3);
        a[4] = new Aresta(3, 4, 2);
        a[5] = new Aresta(0, 4, 2);
        a[6] = new Aresta(4, 5, 3);
        a[7] = new Aresta(5, 6, 2);
        a[8] = new Aresta(0, 6, 4);
        int[][] custos = new int[7][7];
        for (Aresta x : a) {
            custos[x.v1][x.v2] = x.custo;
            custos[x.v2][x.v1] = x.custo;
        }*/

       /*int[][] custos = readFromFile("Fri Nov 15 22-56-22 BRST 2019759.txt");

        Modelo mod = new Modelo();
        try {
            mod.resolve(custos);
        } catch (IloException e) {
            e.printStackTrace();
        }*/

        /*for (int i = 0; i < 10; i++) {
            generateRandom();
        }*/

        List<int[][]> problemas = readFromAllFiles();
        int i = 1;
        for (int[][] custos : problemas) {
            System.out.println("----------------------------");
            System.out.println("Executando problema " + i);

            Modelo mod = new Modelo();
            try {
                mod.resolve(custos);
            } catch (IloException e) {
                e.printStackTrace();
            }

            i++;
        }
    }

    private static List<int[][]> readFromAllFiles() {
        File root = new File(".");

        List<int[][]> result = new ArrayList<>();
        for (File f : root.listFiles()) {
            String name = f.getName();
            if (name.length() > 4 && name.substring(name.length() - 3).equals("txt")) {
                result.add(readFromFile(name));
            }
        }

        return result;
    }

    private static int[][] readFromFile(String fileName) {
        int[][] custos = null;

        try {
            File fr = new File(fileName);
            Scanner s = new Scanner(fr);

            boolean first = true;
            int nv = 0;
            int l = 0;
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] split = line.split(" ");

                if (first) {
                    nv = split.length;
                    custos = new int[nv][nv];
                    first = false;
                }

                for (int i = 0; i < nv; i++) {
                    custos[l][i] = Integer.parseInt(split[i]);
                }

                l++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return custos;
    }

    private static int[][] generateRandom() throws FileNotFoundException {
        int tamanho = 50;
        Random random = new Random();
        int[][] custos = new int[tamanho][tamanho];

        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                if (i == j) {
                    custos[i][j] = 0;
                    custos[j][i] = 0;
                }
                else {
                    int valor = random.nextInt(49) + 1;

                    if (i <= j)
                        custos[i][j] = valor;
                    else
                        custos[i][j] = custos[j][i];
                }
            }
        }

        PrintWriter writer = new PrintWriter(
                new File(new Date().toString().replace(':', '-') + random.nextInt(1000) + ".txt"));
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                writer.print(custos[i][j] + " ");
            }
            writer.println();
        }
        writer.close();

        return custos;
    }
}
