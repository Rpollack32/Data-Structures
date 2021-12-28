package app;

import java.io.*;

import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	String varOrArray = "";

		for(int i =0; i < expr.length(); i++){
			char c = expr.charAt(i);
			if (c == ' ' || Character.isDigit(c) || c == '(' || c == ')' ||
					c == '+' || c == '-' || c == '*' || c == '\\' || c == '\t' || c == '['){
				if(i == expr.length()-1){	 
					break;
				}
				if(c == '['){	
					if(varOrArray != ""){	
						Array arr = new Array(varOrArray);
						boolean duplicateArray = false;
						
						for(int j = 0; j < arrays.size(); j++){
							if(arrays.get(j).name.equals(arr.name)){
								duplicateArray = true;
							}
						}
						if(!duplicateArray){
							arrays.add(arr);
						}
					}
					varOrArray = "";
				}else{
					if(varOrArray != ""){	
						Variable var = new Variable(varOrArray);
						boolean duplicateVar = false;
						
						for(int j = 0; j < vars.size(); j++){
							if(vars.get(j).name.equals(var.name)){
								duplicateVar = true;
							}
						}
						if (!duplicateVar){
							vars.add(var);
						}
					}
					varOrArray = "";
				}
			}else{
				if(Character.isLetter(c)){
					varOrArray = varOrArray + c;
				}
			}
		}
		if(varOrArray != ""){
			vars.add(new Variable(varOrArray));
		}
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays){
    	/** COMPLETE THIS METHOD **/
    	expr = expr.replaceAll(" ", "");
    	expr = expr.replaceAll("\t", "");
    	expr = getVariable(expr, vars, arrays);
    	expr = evaluateBracket(expr, vars, arrays);
    	return evaluate(expr);
    }

    	private static String getVariable(String expr, ArrayList <Variable> vars, ArrayList<Array> arrays){
    		StringTokenizer tokenizer = new StringTokenizer(expr, delims);
    		String string = "";
    		while(tokenizer.hasMoreTokens()){
    			try{
    				string = tokenizer.nextToken();
    				Float.parseFloat(string); 			
    			}catch (NumberFormatException e){	
    				if(vars.contains(new Variable(string)) && !arrays.contains(new Array(string))){
    					expr = expr.substring(0, expr.indexOf(string)) + vars.get(vars.indexOf(new Variable(string))).value 
    						+ expr.substring(expr.indexOf(string) + string.length(),expr.length());	
    				}
    			}
    		}
    	return expr;
        }
                
        private static String evaluateBracket(String expr, ArrayList <Variable> vars, ArrayList<Array> arrays){
    		StringTokenizer tokenizer;
    		
    		while(expr.indexOf('[') != -1){
    			tokenizer = new StringTokenizer(expr, "[]");
    			while(tokenizer.hasMoreTokens()){
    				String string = tokenizer.nextToken();		
    				try{
    					float x = evaluate(string);
    					string = "["+string+"]";
    					expr = expr.substring(0, expr.indexOf(string))+(int)x +
    							expr.substring(expr.indexOf(string)+string.length());
    				}catch(Exception e){			
    				}
    			}
    			
    			tokenizer = new StringTokenizer(expr, delims);
    			while(tokenizer.hasMoreTokens()){
    				String string = tokenizer.nextToken();
    				if(arrays.contains(new Array(string))){
    					continue;
    				}else{
    					try{
    						Float.parseFloat(string);  						
    					}catch(Exception e){
    						String string2 = string;
    						String[] splitDigPos = string.split("(?<=\\D)(?=\\d)");   						
    						float x = Float.parseFloat(splitDigPos[1]);
    						expr = expr.substring(0,expr.indexOf(string2)) + arrays.get(arrays.indexOf(new Array(splitDigPos[0]))).values[(int)x]
    								+ expr.substring(expr.indexOf(string2) + string2.length());   						
    					}   					
    				}
    			}   			
    		}
    		return expr;
        }
        
        private static ArrayList<Character> operators = new ArrayList<Character>();
    	private static ArrayList<Float> numeric = new ArrayList<Float>();

    	private static void reorder(String input,int index) throws IllegalArgumentException{
    		if((index <= input.length()-2) && (input.charAt(index+1) != '-')
    				&& (input.charAt(index) == '+'|| input.charAt(index) == '*' || input.charAt(index) == '/'|| input.charAt(index) == '-')){
    			
    			if(index == 0 && input.charAt(index) == '-'){
    				index++;
    				while(index != input.length() && !(input.charAt(index) == '+' || input.charAt(index) == '*' || input.charAt(index) == '/' || input.charAt(index) == '-') ){
    					index++;
    				}   				
    				try{
    					numeric.add(Float.parseFloat(input.substring(0,index)));
    					reorder(input, index);
    				}catch (Exception e){   					
    					throw new IllegalArgumentException("wrong syntax");
    				}
    				
    			}else{
    				operators.add(input.charAt(index));
    				reorder(input, index+1);
    			}
    		}else if((index <= input.length()-2) && (input.charAt(index) == '+' || input.charAt(index) == '*' || input.charAt(index) == '/' || input.charAt(index) == '-') 
    				&& input.charAt(index+1)== '-'){  			
    			operators.add(input.charAt(index));
    			index += 1;
    			int start = index;
    			index++;
    			while(index != input.length()&&!(input.charAt(index)=='+'|| input.charAt(index)=='*' || input.charAt(index)=='/' || input.charAt(index)=='-')){
    				index++;
    			}
    			
    			try{
    			numeric.add(Float.parseFloat(input.substring(start,index)));
    			reorder(input,index);
    			} catch (Exception e){  				
    				throw new IllegalArgumentException("wrong syntax");
    			}
    		}else if (index<input.length()){
    			int start = index;
    			while(index!=input.length()&&!(input.charAt(index)=='+' || input.charAt(index)=='*' || input.charAt(index)=='/' || input.charAt(index)=='-')){
    				index++;
    			}
    			try{
    				numeric.add(Float.parseFloat(input.substring(start, index)));
    				reorder(input, index);
    			} catch (Exception e){   				
    				throw new IllegalArgumentException("wrong syntax");
    			}  			
    		}   	
    	}
    	
    	private static float priority(ArrayList<Float> opands, ArrayList<Character> oper) throws IllegalArgumentException{
    		int index = 0;
    		
    		if(oper.contains('*') && (oper.indexOf('/') == -1 || oper.indexOf('*') < oper.indexOf('/'))){
    			index = oper.indexOf('*');
    			opands.set(index, opands.get(index)*opands.get(index+1));
    			opands.remove(index+1);
    			oper.remove(index);
    			
    			return priority(opands, oper);		
    		}
    		else if(oper.contains('/') && (oper.indexOf('*') == -1 || oper.indexOf('*') > oper.indexOf('/'))){
    			index = oper.indexOf('/');
    			if(opands.get(index+1)!=0){
    				opands.set(index, opands.get(index)/opands.get(index+1));
    				opands.remove(index+1);
    				oper.remove(index);
    			
    				return priority(opands,oper);
    			}else{
    				throw new IllegalArgumentException("divide by 0");
    			}
    				
    		}else if((oper.contains('+')) && (oper.indexOf('-') == -1||oper.indexOf('+') < oper.indexOf('-'))){
    			index = oper.indexOf('+');
    			opands.set(index, opands.get(index) + opands.get(index+1));
    			opands.remove(index+1);
    			oper.remove(index);
    			
    		return priority(opands, oper);		
    		}else if((oper.contains('-'))&&(oper.indexOf('+') == -1||oper.indexOf('+') > oper.indexOf('-'))){
    			index = oper.indexOf('-');
    			opands.set(index, opands.get(index) - opands.get(index+1));
    			opands.remove(index+1);
    			oper.remove(index);
    			
    			return priority(opands,oper);		
    		}else if(oper.size() == 0||opands.size() == 1){   			
    			return opands.get(0);
    		}else{ 			
    			throw new IllegalArgumentException("wrong syntax");
    		}
    	}
        
    	private static String evaluateParenthesis(String input){
    		if(input.lastIndexOf('(') != -1){
    			String a = input.substring(input.lastIndexOf('(')+1, input.indexOf(')', input.lastIndexOf('(')));
    			reorder(a, 0);
    			float b = priority(numeric,operators);
    			numeric.removeAll(numeric);
    			operators.removeAll(operators);
    			input = input.substring(0,input.lastIndexOf('('))+b+input.substring(input.indexOf(')', input.lastIndexOf('('))+1,input.length());			
    				return evaluateParenthesis(input);
    		}else{
    			return input;
    		}		
    	}
    	
    	private static float evaluate(String input){    		
    		input = evaluateParenthesis(input);		
    		numeric.removeAll(numeric);
    		operators.removeAll(operators);
    		reorder(input,0);
    		float b = priority(numeric, operators);
    		numeric.removeAll(numeric);
    		operators.removeAll(operators);
    		return b;	 			
    	}    
}

