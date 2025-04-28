package com.bcopstein.ex4introducaotesteunitario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controller {
    private NumeralRomano numeralRomano;
    private NumFinder numFinder;

    @Autowired
    public Controller(NumeralRomano numeralRomano,NumFinder numFinder){
        this.numeralRomano = numeralRomano;
    }

    @GetMapping("")
    @CrossOrigin(origins = "*")
    public String welcomeMessage(){
        return("Introdução ao teste unitário");
    }

    @GetMapping("converte/{numeral}")
    @CrossOrigin(origins = "*")
    public String converteNumeralRomano(@PathVariable(value="numeral")String numeral){
        int resp = numeralRomano.convert(numeral);
        return ""+resp;
    }    

    @GetMapping("calcula/{expressao}")
    @CrossOrigin(origins = "*")
    public String calcula(@PathVariable(value="expressao")String expressao){
        int resp = Calculadora.evaluate(expressao);
        return ""+resp;
    }    

    @GetMapping("encontraMaior/{listaNros}")
    @CrossOrigin(origins = "*")
    public String encontraMaior(@PathVariable(value="listaNros")String lista){
        String numeros[] = lista.split(",");
        int[] valores = new int[numeros.length];
        for(int i=0;i<numeros.length;i++){
            valores[i] = Integer.parseInt(numeros[i]);
        }
        numFinder.find(valores);
        int resp = numFinder.getLargest();
        return ""+resp;
    }    

    @GetMapping("encontraMenor/{listaNros}")
    @CrossOrigin(origins = "*")
    public String encontraMenor(@PathVariable(value="listaNros")String lista){
        String numeros[] = lista.split(",");
        int[] valores = new int[numeros.length];
        for(int i=0;i<numeros.length;i++){
            valores[i] = Integer.parseInt(numeros[i]);
        }
        numFinder.find(valores);
        int resp = numFinder.getSmallest();
        return ""+resp;
    }    
    
}
