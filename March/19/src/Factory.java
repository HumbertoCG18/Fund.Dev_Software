package src;
import java.util.HashMap;
import java.util.Map;

public class Factory {
    private static Factory instance;
    private Map<Integer, Codificador> codificadores;

    private Factory() {
        codificadores = new HashMap<>();
        codificadores.put(1, new CodificadorSimples());
        codificadores.put(2, new CodificadorMid());
        codificadores.put(3, new CodificadorHigh());
    }

    public static Factory getInstance() {
        if (instance == null) {
            instance = new Factory();
        }
        return instance;
    }

    public Codificador getCodificador(int nivelSegurancaDesejado) {
        int closestLevel = codificadores.keySet().stream()
            .min((a, b) -> Math.abs(a - nivelSegurancaDesejado) - Math.abs(b - nivelSegurancaDesejado))
            .orElse(1); // Default to the lowest security level if no codificador is found

        return codificadores.get(closestLevel);
    }
}