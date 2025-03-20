package src;

public class CarroEcono extends Carro {
    
    public CarroEcono(String modelo, int capacidadeTanque) {
        super(modelo, TipoCombustivel.GASOLINA, 20, capacidadeTanque);
        this.motor = new MotorEcono(TipoCombustivel.GASOLINA);
    }

    @Override
    public String toString() {
        return "CarroEcono:\n  Modelo=" + modelo + "\n  Motor=" + motor + "\n  Tanque=" + tanque;
    }
}
