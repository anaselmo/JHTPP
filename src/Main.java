/**------------------------------------------------------------------------
 * ?                        Java Hypertext Preproccesor
 * ?                               (Main Class)
 * @author         :  anaselmo & YarasAtomic
 * @repo           :  
 * @createdOn      :  15/11/2023
 * @description    :  Really basic Java Hypertext Preproccesor (JHTPP)
 *------------------------------------------------------------------------**/

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        VarTree dias = new VarTree();
        VarTree lunes = new VarTree(); 
        VarTree martes = new VarTree(); 
        VarTree miercoles = new VarTree(); 
        VarTree jueves = new VarTree(); 
        VarTree viernes = new VarTree(); 
        VarTree sabado = new VarTree(); 
        VarTree domingo = new VarTree(); 

        VarTree ejer_lunes = new VarTree();
        VarTree ejer_martes = new VarTree(); 
        VarTree ejer_miercoles = new VarTree(); 
        VarTree ejer_jueves = new VarTree(); 
        VarTree ejer_viernes = new VarTree(); 
        VarTree ejer_sabado = new VarTree(); 
        VarTree ejer_domingo = new VarTree(); 

        VarTree lista = new VarTree();

        lista.put("dias", dias);
        lista.put("nombre", "Guillermo");
        lista.put("nombre3", "Juan");

        dias.put("0",lunes);
        dias.put("1",martes);
        dias.put("2",miercoles);
        dias.put("3",jueves);
        dias.put("4",viernes);
        dias.put("5",sabado);
        dias.put("6",domingo);

        //dias.add(lunes,martes,miercoles,jueves,viernes,sabado,domingo);

        lunes.put("ejercicios",ejer_lunes);
        lunes.put("nombre","Lunes");

        martes.put("ejercicios",ejer_martes);
        martes.put("nombre","Martes");

        miercoles.put("ejercicios",ejer_miercoles);
        miercoles.put("nombre","Miercoles");

        jueves.put("ejercicios",ejer_jueves);
        jueves.put("nombre","Jueves");

        viernes.put("ejercicios",ejer_viernes);
        viernes.put("nombre","Viernes");

        sabado.put("ejercicios",ejer_sabado);
        sabado.put("nombre","Sabado");

        domingo.put("ejercicios",ejer_domingo);
        domingo.put("nombre","Domingo");


        ejer_lunes.put("1","Pecho");
        ejer_lunes.put("2","Espalda");

        ejer_martes.put("1","Biceps");
        ejer_martes.put("2","Triceps");
        ejer_martes.put("3","Hombro");

        ejer_miercoles.put("1","Pierna");

        ejer_jueves.put("1","Pecho");
        ejer_jueves.put("2","Espalda");

        ejer_viernes.put("1","Biceps");
        ejer_viernes.put("2", "Triceps");
        ejer_viernes.put("3","Hombro");

        ejer_sabado.put("1","Pierna");

        ejer_domingo.put("1","Descanso!");

        
        JHTPP tp = new JHTPP(InputType.PATH,"./index.html", lista);
        System.out.println(tp.processText());
    }
}
