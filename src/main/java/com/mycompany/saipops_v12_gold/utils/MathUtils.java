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
    
    public static TreeSet<String> getSmallestScore (TreeSet<String> candidates, TreeMap<String, Double>  scoreMap) {
        double knownSmallest = BILLION;
        TreeSet<String> winners = new TreeSet<String>();
        
        for (String cand: candidates){
            
            Double thisScore = scoreMap.get(cand);
            if (null==thisScore) thisScore = DOUBLE_ZERO+BILLION;
            
            if (thisScore < knownSmallest){
                knownSmallest =thisScore;
                winners.clear();
            }
            if (isEqual (thisScore , knownSmallest)){
                winners.add (cand);
            }
        }
        
        return winners;
    }
    
    //find var whose smaller score is as large as possible
    public static   TreeSet<String> getMaxiMinFrequency(Set<String>  candidates,  TreeMap<String, Double>  mapOne,  TreeMap<String, Double>  mapTwo ) {
        TreeSet<String >  winners = new  TreeSet<String>();
        
        double smallestKnownFreq = DOUBLE_ZERO; 
        double largestKnownFreq = DOUBLE_ZERO;
        
        for (String candidate:  candidates){
            Double pFreq = mapOne.get (candidate );
            if (null == pFreq) pFreq = DOUBLE_ZERO;
            Double nFreq = mapTwo.get (candidate );
            if (null == nFreq) nFreq = DOUBLE_ZERO;
            
            pFreq= Math.abs (pFreq );
            nFreq= Math.abs (nFreq);
            
            double smaller = Math.min (pFreq,  nFreq);
            double larger = Math.max (pFreq,  nFreq);
            
            if ( (smaller >smallestKnownFreq ) || (isEqual (smaller , smallestKnownFreq) && largestKnownFreq<larger  )) {
                winners.clear();
                smallestKnownFreq =smaller;
                largestKnownFreq= larger;
                winners.add (candidate );
            }  else if (isEqual (smaller , smallestKnownFreq) && isEqual (largestKnownFreq,larger)  ) {
                winners.add (candidate );
            }              
        }
               
        return winners;
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
    
    public static double getMaxObjMagnFromList  (TreeSet<String> varSet,  TreeMap<String, Double>  objectiveFunctionMap){
        double maxKnown = DOUBLE_ZERO;
        for (String var : varSet){
            double thisObjmagn = Math.abs (objectiveFunctionMap.get(var));
            if (maxKnown < thisObjmagn)  maxKnown = thisObjmagn;
        }
        return maxKnown;
    }
    
    public static TreeSet<String > getCandidatesWithMaxFixes (TreeSet<String > candidates, TreeMap<String , TreeSet<String>  >  primaryVariables_At_DimensionOne) {
        TreeSet<String >  winners = new  TreeSet<String>();
        int knownMax = -ONE;
        
        for (String cand : candidates){
            int thisCount = primaryVariables_At_DimensionOne.get(cand).size();
            if (thisCount >knownMax ){
               knownMax=  thisCount;
               winners.clear();
            }
            if (thisCount==knownMax){
                winners.add (cand);
            }
        }
        
        return winners;
    }
    
}
