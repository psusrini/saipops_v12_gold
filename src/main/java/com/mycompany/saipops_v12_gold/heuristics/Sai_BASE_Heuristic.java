/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saipops_v12_gold.heuristics;

import static com.mycompany.saipops_v12_gold.Constants.*;
import static com.mycompany.saipops_v12_gold.Parameters.PERF_VARIABILITY_RANDOM_GENERATOR;
import com.mycompany.saipops_v12_gold.constraints.Attributes;
import com.mycompany.saipops_v12_gold.utils.MathUtils;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 *
 * @author sst119
 */
public abstract class Sai_BASE_Heuristic {
     
    protected  TreeMap<String, Double>  objectiveFunctionMap;
   
    protected  TreeMap<String , Double>  fractionalNeutralVariables_WithScore   = new  TreeMap<String , Double>   ();
    
    //secondary variable and the primary variables it will fix if the lowestKnownSecondaryDimension is 1 
    protected  TreeMap<String , TreeSet<String>  >  secondaryVariables_At_DimensionOne   = new  TreeMap<String , TreeSet<String>  >   ();
   
    protected int lowestKnownFractionalPrimaryDimension = BILLION;
    protected  TreeMap<String , Double>  fractionalPrimaryVariablesWithFrequency_AtLowestDim   = new  TreeMap<String , Double>   ();
    //primary variable and the secondary variables it will fix if the lowestKnownFractionalPrimaryDimension is 1 
    protected  TreeMap<String , TreeSet<String>  >  primaryVariables_At_DimensionOne   = new  TreeMap<String , TreeSet<String>  >   ();
     
    
    public Sai_BASE_Heuristic (  Set<Attributes> attributes ,     
            TreeMap<String, Double>  objectiveFunctionMap  ){
                      
        this. objectiveFunctionMap = objectiveFunctionMap;
                
        // populate the maps  
        for (Attributes attr: attributes){
            
            if (attr.hasFractionalNeutralVariables()){
                double score = Math.pow( TWO, TWO - attr.constraintSize);
                for (String neutralVar : attr.fractionalNeutralVariables){
                    //
                    Double currentScore =  fractionalNeutralVariables_WithScore.get (neutralVar)  ;
                    if (null == currentScore)currentScore= DOUBLE_ZERO;
                    fractionalNeutralVariables_WithScore.put (neutralVar,score+ currentScore)  ;
                }
            }
            
            if (attr.allSecondaryVariables.size() > ZERO){ 
                                
                    if (ONE == attr.secondaryDimension){
                        for (String sVar : attr.allSecondaryVariables){
                            //for every secondary var, append the list of primary variables in this constraint
                            TreeSet<String> current = secondaryVariables_At_DimensionOne.get (sVar) ;
                            if (current ==null)   current=     new TreeSet<String> ();
                            current.addAll( attr.allPrimaryVariables);
                            secondaryVariables_At_DimensionOne.put (sVar, current) ; 
                        }                        
                    }       
                           
            }
            
                             
            if (attr.hasFractionalPrimaryVariables()){ 
                               
                if (lowestKnownFractionalPrimaryDimension > attr.primaryDimension){                    
                    lowestKnownFractionalPrimaryDimension = attr.primaryDimension;                   
                    this.fractionalPrimaryVariablesWithFrequency_AtLowestDim.clear();     
                     
                }
                
                if (lowestKnownFractionalPrimaryDimension == attr.primaryDimension){
                    for (String var: attr.fractionalPrimaryVariables  ){
                        //
                        Double currentFreq= fractionalPrimaryVariablesWithFrequency_AtLowestDim .get ( var);
                        if (null==currentFreq)currentFreq=DOUBLE_ZERO;
                        fractionalPrimaryVariablesWithFrequency_AtLowestDim .put ( var, currentFreq + DOUBLE_ONE); 
                    }
                }                                                                               
            } 
            
            if (ONE == attr.primaryDimension){
                for (String var : attr.allPrimaryVariables){
                    TreeSet<String>  currentSet =  primaryVariables_At_DimensionOne .get(var);
                    if (null == currentSet)currentSet= new  TreeSet<String>();
                    currentSet.addAll (attr.allSecondaryVariables) ;
                    primaryVariables_At_DimensionOne .put(var,currentSet); 
                }                       
            } 
            
        }//for all attrs
           
    }//end constructor method
        
    public String getBranchingVariable() {
        TreeSet<String>  candidates  ;
        
        if ( fractionalPrimaryVariablesWithFrequency_AtLowestDim.isEmpty()){            
            //Only neutral vars (no primary). Use MOM_S on neutral vars         
            candidates = getMOMS ( ) ;
        } else  candidates =   selectBranchingVariable()  ;
        
        //random tiebreak        
        String[] candidateArray = candidates.toArray(new String[ZERO]);        
        return candidateArray[ PERF_VARIABILITY_RANDOM_GENERATOR.nextInt(candidates.size())];
    }
    
    protected abstract TreeSet<String>  selectBranchingVariable  ();
    
    protected TreeSet<String>  getApexVariables  ( TreeSet<String>  candidates ){
       
        TreeSet<String> dominatedVariables = new TreeSet<String>   ();
         
        if (this.lowestKnownFractionalPrimaryDimension==ONE){
            //run BCP at level 1 and identify dominated triggers        
            
            for (;;){
                int  numAdditionsInThisIteration = ZERO;
                for (String key : this.primaryVariables_At_DimensionOne.keySet() ){

                    if (dominatedVariables.contains(key))  continue;
                    //
                    TreeSet<String> currentImplications  = primaryVariables_At_DimensionOne.get(key);
                    TreeSet<String> copyOfcurrentImplications = new  TreeSet<String>  ();
                    copyOfcurrentImplications.addAll(currentImplications);
                    if (candidates.contains(key)){
                        dominatedVariables.addAll(currentImplications);
                    }
                    for (String impl: currentImplications){
                        
                        TreeSet<String> implicationsToAdd =   primaryVariables_At_DimensionOne.get(impl);
                        if (implicationsToAdd !=null) {
                            copyOfcurrentImplications.addAll( implicationsToAdd);                            
                        }                    
                    }        
                    numAdditionsInThisIteration += (copyOfcurrentImplications.size() - currentImplications.size()) ;
                    primaryVariables_At_DimensionOne.put(key,copyOfcurrentImplications );                
                }            
                if (numAdditionsInThisIteration==ZERO) break;
            }
        }
        
        TreeSet<String>  apex = new  TreeSet<String> ();
        apex.addAll( candidates);
        apex.removeAll(dominatedVariables );
                                
        return apex;        
    }
    
    // 
    private TreeSet<String>  getMOMS  (){
        TreeSet<String>  candidates   = new TreeSet<String> ();
        candidates.addAll(fractionalNeutralVariables_WithScore.keySet());
        //get candidates with highest score
        return     MathUtils.getMaxObjMagn(candidates, fractionalNeutralVariables_WithScore  ) ;        
    }
     
    
}
