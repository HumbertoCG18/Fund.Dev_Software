package src;

public class App {
    public static void main(String[] args) throws Exception {

        System.out.println("Modelo Esportivo");
        Carro esportivo = new Carro("Esportivo", TipoCombustivel.GASOLINA, 6, 45);
        System.out.println("Antes de abastecer: " + esportivo);
        esportivo.abastece(TipoCombustivel.GASOLINA, 45);
        esportivo.viaja(100);
        System.out.println("Após viagem: " + esportivo);

        System.out.println("\nModelo Utilitario");
        Carro utilitario = new Carro("Utilitario", TipoCombustivel.DISEL, 5, 70);
        System.out.println("Antes de abastecer: " + utilitario);
        utilitario.abastece(TipoCombustivel.DISEL, 70);
        utilitario.viaja(200);
        System.out.println("Após viagem: " + utilitario);

        System.out.println("\nModelo SUV (Tanque FLEX com motor a gasolina fixo)");
        Carro suv = new Carro("SUV", TipoCombustivel.GASOLINA, 8, 55);
        System.out.println("Antes de abastecer: " + suv);
        suv.abastece(TipoCombustivel.GASOLINA, 55);
        suv.viaja(120);
        System.out.println("Após viagem: " + suv);

        System.out.println("\nModelo SUVFlex (Tanque FLEX dinâmico e consumo variável)");
        Carro suvFlex = new Carro("SUVFlex", TipoCombustivel.FLEX, 8, 6, 65);
        System.out.println("Antes de abastecer: " + suvFlex);
        // Abastecendo com gasolina
        suvFlex.abastece(TipoCombustivel.GASOLINA, 65);
        suvFlex.viaja(100);
        System.out.println("Após viagem com gasolina: " + suvFlex);
        // Abastecendo com álcool agora
        suvFlex.abastece(TipoCombustivel.ALCOOL, 65);
        suvFlex.viaja(100);
        System.out.println("Após viagem com álcool: " + suvFlex);

        System.out.println("\nModelo Econo (Motor com consumo dinâmico)");
        CarroEcono econo = new CarroEcono("Econo", 55);
        System.out.println("Antes de abastecer: " + econo);
        econo.abastece(TipoCombustivel.GASOLINA, 55);
        econo.viaja(5000);  // 5000 km para alterar a eficiência
        System.out.println("Após viagem 5000 km: " + econo);
        econo.viaja(3000);
        System.out.println("Após viagem 3000 km: " + econo);
    }
}
