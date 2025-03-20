package src;
public class Carro {

    protected String modelo;
    protected Motor motor;
    protected TanqueCombustivel tanque;

    // Construtor para carros não-flex
    public Carro(String modelo, TipoCombustivel tipoMotor, int consumoMotor, int capacidadeTanque) {
        this.modelo = modelo;
        motor = new Motor(tipoMotor, consumoMotor);
        tanque = new TanqueCombustivel(tipoMotor, capacidadeTanque);
    }
    
    // Novo construtor para carros flex, que utiliza MotorFlex
    public Carro(String modelo, TipoCombustivel tipoMotor, int consumoPadrao, int consumoFlex, int capacidadeTanque) {
        this.modelo = modelo;
        if (tipoMotor == TipoCombustivel.FLEX) {
            motor = new MotorFlex(tipoMotor, consumoPadrao, consumoFlex);
        } else {
            motor = new Motor(tipoMotor, consumoPadrao);
        }
        tanque = new TanqueCombustivel(tipoMotor, capacidadeTanque);
    }
    
 public String getModelo() {
        return modelo;
    }

    public int getCombustivelDisponivel() {
        return tanque.getCombustivelDisponivel();
    }

    public int abastece(TipoCombustivel tipoCombustivel, int quantidade) {
        int capacidadeLivre = tanque.getCapacidade() - tanque.getCombustivelDisponivel();
        int quantidadeAbastecida = 0;
        if (capacidadeLivre < quantidade) {
            quantidadeAbastecida = capacidadeLivre;
            tanque.abastece(tipoCombustivel, capacidadeLivre);
        } else {
            quantidadeAbastecida = quantidade;
            tanque.abastece(tipoCombustivel, quantidade);
        }
        return quantidadeAbastecida;
    }

    public int verificaSePodeViajar(int distancia) {
        int combustivelNecessario = motor.combustivelNecessario(distancia);
        if (tanque.getCombustivelDisponivel() >= combustivelNecessario) {
            return distancia;
        } else {
            return tanque.getCombustivelDisponivel() * motor.getConsumo();
        }
    }

    public boolean viaja(int distancia) {
        if (verificaSePodeViajar(distancia) >= distancia) {
            motor.percorre(distancia);
            tanque.gasta(motor.combustivelNecessario(distancia));
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Carro:\n  Modelo=" + modelo + "\n  Motor=" + motor + "\n  Tanque=" + tanque;
    }
}