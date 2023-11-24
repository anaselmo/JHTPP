/**------------------------------------------------------------------------
 * ?                        Java Hypertext Preproccesor
 * ?                               (Main Class)
 * @author         :  anaselmo & YarasAtomic
 * @repo           :  github.com/anaselmo/JHTPP
 * @createdOn      :  15/11/2023
 * @description    :  Really basic Main Class as demo of the JHTPP Class
 *                    Read 'README.md' for further information
 *------------------------------------------------------------------------**/

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        // TODO: This definitions need to be changed ASAP

        VarTree mondayExerciseTree = new VarTree();
        mondayExerciseTree.put("1","Chest");
        mondayExerciseTree.put("2","Back");

        VarTree tuesdayExerciseTree = new VarTree();
        tuesdayExerciseTree.put("1","Biceps");
        tuesdayExerciseTree.put("2","Triceps");
        tuesdayExerciseTree.put("3","Shoulder");
        
        VarTree wednesdayExerciseTree = new VarTree();
        wednesdayExerciseTree.put("1","Leg");

        VarTree thursdayExerciseTree = new VarTree();
        thursdayExerciseTree.put("1","Chest");
        thursdayExerciseTree.put("2","Back");
        
        VarTree fridayExerciseTree = new VarTree();
        fridayExerciseTree.put("1","Biceps");
        fridayExerciseTree.put("2","Triceps");
        fridayExerciseTree.put("3","Shoulder");

        VarTree saturdayExerciseTree = new VarTree();
        saturdayExerciseTree.put("1","Leg");

        VarTree sundayExerciseTree = new VarTree();
        sundayExerciseTree.put("1","Rest!");

        //-----------------------------------------------------------------//
        //-----------------------------------------------------------------//

        VarTree mondayTree = new VarTree();
        mondayTree.put("name","Monday");
        mondayTree.put("exercises",mondayExerciseTree);
        
        VarTree tuesdayTree = new VarTree();
        tuesdayTree.put("name","Tuesday");
        tuesdayTree.put("exercises",tuesdayExerciseTree);
        
        VarTree wednesdayTree = new VarTree();
        wednesdayTree.put("name","Wednesday");
        wednesdayTree.put("exercises",wednesdayExerciseTree);
        
        VarTree thursdayTree = new VarTree();
        thursdayTree.put("name","Thursday");
        thursdayTree.put("exercises",thursdayExerciseTree);
        
        VarTree fridayTree = new VarTree();
        fridayTree.put("name","Friday");
        fridayTree.put("exercises",fridayExerciseTree);
        
        VarTree saturdayTree = new VarTree();
        saturdayTree.put("name","Saturday");
        saturdayTree.put("exercises",saturdayExerciseTree);
        
        VarTree sundayTree = new VarTree();
        sundayTree.put("name","Sunday");
        sundayTree.put("exercises",sundayExerciseTree);

        //-----------------------------------------------------------------//
        //-----------------------------------------------------------------//
        
        VarTree daysTree = new VarTree();
        daysTree.put("1",mondayTree);
        daysTree.put("2",tuesdayTree);
        daysTree.put("3",wednesdayTree);
        daysTree.put("4",thursdayTree);
        daysTree.put("5",fridayTree);
        daysTree.put("6",saturdayTree);
        daysTree.put("7",sundayTree);
        
        //-----------------------------------------------------------------//
        //-----------------------------------------------------------------//

        VarTree listTree = new VarTree();
        listTree.put("days", daysTree);
        listTree.put("name", "Guillermo");
        listTree.put("otherName", "Juan");

        //-----------------------------------------------------------------//
        //-----------------------------------------------------------------//
        
        JHTPP tp = new JHTPP(InputType.PATH,"./index.html", listTree);
        System.out.println(tp.processText());
    }
}
