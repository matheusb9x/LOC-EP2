import ilog.concert.IloException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        RunInstances(50);
    }

    private static void RunInstances(int tamanhoInstancia) {
        List<SampleItem> problemas = readFromAllFiles(tamanhoInstancia);
        int i = 1;
        for (SampleItem sample : problemas) {
            System.out.println("----------------------------");
            System.out.println("Executando " + sample.fileName);

            int[][] custos = sample.custos;
            Modelo mod = new Modelo();
            try {
                mod.resolve(custos);
            } catch (IloException e) {
                e.printStackTrace();
            }

            i++;
        }
    }

    private static List<SampleItem> readFromAllFiles(int tamanhoInstancia) {
        File root = new File("samples/" + tamanhoInstancia);

        List<SampleItem> result = new ArrayList<>();
        for (File f : root.listFiles()) {
            String name = f.getName();
            if (name.length() > 4 && name.substring(name.length() - 3).equals("txt")) {
                result.add(new SampleItem(readFromFile(f.getAbsolutePath()), f.getName()));
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
}
