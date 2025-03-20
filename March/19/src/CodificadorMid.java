package src;
import java.time.LocalDate;
import java.util.Base64;
public class CodificadorMid implements Codificador {
    public String getNome() {
        return "CodificadorMid";
    }

    public LocalDate getDataCriacao() {
        return LocalDate.of(2025, 03, 19);
    }

    public int getNivelSeguranca() {
        return 2;
    }

    public String codifica(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public String decodifica(String str) {
        return new String(Base64.getDecoder().decode(str));
    }
}