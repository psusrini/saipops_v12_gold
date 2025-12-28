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
public   class SaiPOPS_Heuristic  extends Sai_BASE_Heuristic{
    //
       
    public SaiPOPS_Heuristic (  Set<Attributes> attributes ,     
            TreeMap<String, Double>  objectiveFunctionMap  ){
         super ( attributes ,     objectiveFunctionMap);
    } 
    
    @Override
    protected TreeSet<String>  selectBranchingVariable  (){
        TreeSet<String>  candidates = new  TreeSet<String> ();
        
        candidates.addAll( this.fractionalPrimaryVariablesWithFrequency_AtLowestDim.keySet());
        
        //largest objective 
        candidates = MathUtils.getMaxObjMagn(candidates, this.objectiveFunctionMap )  ;  
   
        TreeSet<String>  apex= this.getApexVariables(candidates) ;                
        if (!apex.isEmpty())candidates=apex;
        
        //tie break on highest frequency.
        candidates = MathUtils.getMaxObjMagn(candidates, this.fractionalPrimaryVariablesWithFrequency_AtLowestDim )  ;  
        
        return candidates;
    }   
  
   
}
