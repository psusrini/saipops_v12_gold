/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saipops_v12_gold.utils;
  
import static com.mycompany.saipops_v12_gold.Constants.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author sst119
 */
public class MathUtils {
    
    public static double getObjMagnSum (TreeSet<String> variables , TreeMap<String, Double>  objectiveFunctionMap) {
        double sum = DOUBLE_ZERO;
        
        for (String var: variables ){
                        
            sum += Math.abs ( objectiveFunctionMap.get( var));
        }
        
        return sum;
    }
   
    public static    TreeSet<String>  getMaxObjMagn ( Set<String> candidates  ,  TreeMap<String, Double>  objectiveFunctionMap){
        TreeSet<String >  winners = new  TreeSet<String>();
        double bestKnownObjMagn = -ONE;
        
        for (String var : candidates ){
            Double objval =   objectiveFunctionMap.get( var);
            
            double thisObjMagn = Math.abs ( objval);
            if (thisObjMagn > bestKnownObjMagn){
                bestKnownObjMagn = thisObjMagn;
                winners.clear();
            }
            if (Math.abs(bestKnownObjMagn -thisObjMagn) < EPSILON_SMALL){
                winners.add (var );
            }
        }
        
        return winners ;
    }
    
    public static  boolean isEqual (double x1, double x2) {
        return Math.abs(x1 -x2) < EPSILON_SMALL ;
    }
    
}
