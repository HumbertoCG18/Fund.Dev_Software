package src;

public class MotorFlex extends Motor {

    private int consumoFlex; // Consumo alternativo para, por exemplo, álcool

    public MotorFlex(TipoCombustivel tipoMotor, int consumoPadrao, int consumoFlex) {
        // Usa o construtor da classe mãe para o consumo padrão (gasolina)
        super(tipoMotor, consumoPadrao);
        this.consumoFlex = consumoFlex;
    }

    public int getConsumoFlex() {
        return consumoFlex;
    }

    // Calcula o combustível necessário considerando o combustível utilizado
    public int combustivelNecessario(int distancia, TipoCombustivel combustivelUtilizado) {
        int consumoUtilizado = getConsumo(); // consumo padrão
        if (combustivelUtilizado == TipoCombustivel.ALCOOL) {
            consumoUtilizado = consumoFlex;
        }
        return distancia / consumoUtilizado;
    }

    @Override
    public String toString() {
        return "MotorFlex [consumo (gasolina)=" + getConsumo() +
               ", consumo (álcool)=" + consumoFlex +
               ", quilometragem=" + getQuilometragem() +
               ", tipoMotor=" + getTipoMotor() + "]";
    }
}
