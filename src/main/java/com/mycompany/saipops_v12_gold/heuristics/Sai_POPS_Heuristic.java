/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saipops_v12_gold.heuristics;
    
 
import static com.mycompany.saipops_v12_gold.Constants.*; 
import com.mycompany.saipops_v12_gold.constraints.*;
import com.mycompany.saipops_v12_gold.utils.MathUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author sst119
 *  
 * 
 *  
 */
public   class Sai_POPS_Heuristic  extends Sai_BASE_Heuristic{
    //
       
    public Sai_POPS_Heuristic (  Set<Attributes> attributes ,     
            TreeMap<String, Double>  objectiveFunctionMap  ){
         super ( attributes ,     objectiveFunctionMap);
    } 
    
    @Override
    protected TreeSet<String>  selectBranchingVariable  (){
        TreeSet<String>  candidates = new  TreeSet<String> ();
         
        candidates.addAll( this.fractionalPrimaryVariablesWithFrequency_AtLowestDim.keySet());
        
        TreeSet<String>  apex= this.getApexVariables(candidates) ;                
        if (!apex.isEmpty())candidates=apex;
           
        //largest objective 
        candidates = MathUtils.getMaxObjMagn(candidates, getBCP_ObjMagnMap(candidates) )  ;  
        
        //tie break on highest frequency.
        candidates = MathUtils.getMaxObjMagn(candidates, this.fractionalPrimaryVariablesWithFrequency_AtLowestDim )  ;  
        
        return candidates;
    }   
  
  
       
    private TreeMap<String, Double>   getBCP_ObjMagnMap ( TreeSet<String>  candidates ){
       
        TreeMap<String, Double> modifiedObjFuncMap = new TreeMap<String, Double> ();
        
        //first fill up the map with the original objective magnitude
        for (String var: candidates ){
            modifiedObjFuncMap.put (var, Math.abs ( this.objectiveFunctionMap.get(var))) ;
        }
        
        TreeSet<String>  exists = new TreeSet<String>  ();
        exists.addAll(secondaryVariables_At_DimensionOne.keySet());
        exists.retainAll(candidates);      
        
        TreeSet<String> dominatedVariables = new TreeSet<String>   ();
        if (!exists.isEmpty()){
            // run BCP at level 2 and identify dominated triggers    

            for (;;){
                int  numAdditionsInThisIteration = ZERO;
                for (String key : secondaryVariables_At_DimensionOne.keySet()){

                    if (dominatedVariables.contains(key))  continue;
                    //
                    TreeSet<String> currentImplications  = secondaryVariables_At_DimensionOne.get(key);
                    TreeSet<String> copyOfcurrentImplications = new  TreeSet<String>  ();
                    copyOfcurrentImplications.addAll(currentImplications);
                    if (candidates.contains(key)){
                        dominatedVariables.addAll( currentImplications);
                    }
                    for (String impl: currentImplications){
                        
                        TreeSet<String> implicationsToAdd =   secondaryVariables_At_DimensionOne.get(impl);
                        if (implicationsToAdd !=null) {
                            copyOfcurrentImplications.addAll( implicationsToAdd);

                        }                    
                    }        
                    numAdditionsInThisIteration += (copyOfcurrentImplications.size() - currentImplications.size()) ;
                    secondaryVariables_At_DimensionOne.put(key,copyOfcurrentImplications );                
                }            
                if (numAdditionsInThisIteration==ZERO) break;
            }
        }
        
        
        for ( String dom:  dominatedVariables){
            modifiedObjFuncMap.put (dom, DOUBLE_ZERO );
        }
        for (String  cand: candidates){
            if (dominatedVariables.contains(cand))continue;
            double current = modifiedObjFuncMap.get (cand );
            
            //if this candidate var is also a secondary var at dim 1
            if (null!= secondaryVariables_At_DimensionOne.get(cand)){
                modifiedObjFuncMap.put (cand, current + MathUtils.getObjMagnSum(secondaryVariables_At_DimensionOne.get(cand) , objectiveFunctionMap) );            
            }
            
        } 
         
        return modifiedObjFuncMap;
    }
   
}
