package src;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Escolha o nível de segurança desejado (1-3):");
        System.out.println("1 - Codificador Simples");
        System.out.println("2 - Codificador Mid");
        System.out.println("3 - Codificador High");
        
        int nivelSegurancaDesejado = scanner.nextInt();
        
        Factory factory = Factory.getInstance();
        Codificador cod = factory.getCodificador(nivelSegurancaDesejado);

        System.out.println("Codificador selecionado: " + cod.getNome());
        System.out.println("Versão: " + cod.getDataCriacao());
        System.out.println("Nível de segurança: " + cod.getNivelSeguranca());
        
        String texto = "Este é o string a ser codificado";
        String codificado = cod.codifica(texto);
        String decodificado = cod.decodifica(codificado);

        System.out.println("Texto original: " + texto);
        System.out.println("Texto codificado: " + codificado);
        System.out.println("Texto decodificado: " + decodificado);
        
        scanner.close();
    }
}