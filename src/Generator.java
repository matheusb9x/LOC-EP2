import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;

public class Generator {
    public static void main(String[] args) throws FileNotFoundException {
        gerarInstancias(50, 90);
    }

    private static void gerarInstancias(int tamanho, int quantidade) throws FileNotFoundException {
        Random random = new Random();

        for (int c = 0; c < quantidade; c++) {
            int[][] custos = new int[tamanho][tamanho];

            for (int i = 0; i < tamanho; i++) {
                for (int j = 0; j < tamanho; j++) {
                    if (i == j) {
                        custos[i][j] = 0;
                        custos[j][i] = 0;
                    } else {
                        int valor = random.nextInt(49) + 1;

                        if (i <= j)
                            custos[i][j] = valor;
                        else
                            custos[i][j] = custos[j][i];
                    }
                }
            }

            String folderPath= "samples/" + tamanho;
            CreateDirIfNotExists(folderPath);
            PrintWriter writer = new PrintWriter(
                    new File(folderPath + "/" + new Date().toString().replace(':', '-') + c + ".txt"));

            for (int i = 0; i < tamanho; i++) {
                for (int j = 0; j < tamanho; j++) {
                    writer.print(custos[i][j] + " ");
                }
                writer.println();
            }
            writer.close();
        }
    }

    private static void CreateDirIfNotExists(String folderPath) {
        File dir = new File(folderPath);
        if (!dir.exists())
            dir.mkdir();
    }
}
