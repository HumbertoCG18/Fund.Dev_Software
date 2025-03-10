package src;

public class Motor {

    private TipoCombustivel tipoMotor;
    private int consumo; // Consumo padrão (ex: gasolina)
    private int quilometragem;

    public Motor(TipoCombustivel tipoMotor, int consumo) {
        this.tipoMotor = tipoMotor;
        this.consumo = consumo;
        this.quilometragem = 0;
    }

    public int getConsumo() {
        return this.consumo;
    }
    
    protected void setConsumo(int novoConsumo) {
        this.consumo = novoConsumo;
    }

    public TipoCombustivel getTipoMotor() {
        return this.tipoMotor;
    }

    public int getQuilometragem() {
        return this.quilometragem;
    }

    public int combustivelNecessario(int distancia) {
        return distancia / consumo;
    }

    public void percorre(int distancia) {
        quilometragem += distancia;
    }

    @Override
    public String toString() {
        return "Motor [consumo=" + consumo +
               ", quilometragem=" + quilometragem +
               ", tipoMotor=" + tipoMotor + "]";
    }
}
