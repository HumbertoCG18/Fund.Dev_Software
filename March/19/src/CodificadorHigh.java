package src;
import java.time.LocalDate;

public class CodificadorHigh implements Codificador {
    private static final int DESLOCAMENTO = 3; // Deslocamento de 3 posições

    public String getNome() {
        return "CodificadorHigh";
    }

    public LocalDate getDataCriacao() {
        return LocalDate.of(2025, 03, 19);
    }

    public int getNivelSeguranca() {
        return 3;
    }

    public String codifica(String str) {
        StringBuilder encoded = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                encoded.append((char) ((c - base + DESLOCAMENTO) % 26 + base));
            } else {
                encoded.append(c);
            }
        }

        return encoded.toString();
    }

    public String decodifica(String str) {
        StringBuilder decoded = new StringBuilder();

        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                decoded.append((char) ((c - base - DESLOCAMENTO + 26) % 26 + base));
            } else {
                decoded.append(c);
            }
        }

        return decoded.toString();
    }
}