package com.bcopstein.ex4introducaotesteunitario;

public class Encomenda {
    public static int[] qtdadeBarras(int dispB1,int dispB5,int peso){
        int[] resp = {-1,-1};

        int maxB5 = peso/5;
        int qtdadeB5 = Math.min(maxB5,dispB5);
        int qtdadeB1 = peso - (qtdadeB5 * 5);
        if (dispB1 <= qtdadeB1){ 
            return null;
        }else{ 
            resp[0] = qtdadeB1;
            resp[1] = qtdadeB5;
        }
        return resp;
    }      
}
