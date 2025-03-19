package src;

public class MotorEcono extends Motor {

    public MotorEcono(TipoCombustivel tipoMotor) {
        super(tipoMotor, 20);
    }

    @Override
    public void percorre(int distancia) {
        super.percorre(distancia);
        int novaEficiencia = 20 - (getQuilometragem() / 5000);
        if (novaEficiencia < 10) {
            novaEficiencia = 10;
        }
        setConsumo(novaEficiencia);
    }
}
