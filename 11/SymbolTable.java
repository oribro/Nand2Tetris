
import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
	
	public static enum VarKind{STATIC, FIELD, ARG, VAR, NONE};
	private final static int TYPE_COLUMN = 0;
	private final static int KIND_COLUMN = 1;
	private final static int INDEX_COLUMN = 2;
	private int staticRunningIndex, fieldRunningIndex, argRunningIndex, varRunningIndex;
	private HashMap<String, ArrayList<String>> methodScopeTable;
	private HashMap<String, ArrayList<String>> classScopeTable;
	private HashMap<String, String> methodTypesTable;
	
	SymbolTable()
	{
		classScopeTable = new HashMap<String, ArrayList<String>>();
		methodScopeTable = new HashMap<String, ArrayList<String>>();
		methodTypesTable = new HashMap<String, String>();
		staticRunningIndex = 0; 
		argRunningIndex = 0;
		fieldRunningIndex = 0;
		varRunningIndex = 0;
	}
	
	// TODO: List of hash tables for nested calls, recursions.
	
	public void startSubroutine()
	{
		methodScopeTable.clear();
		argRunningIndex = 0;
		varRunningIndex = 0;
	}
	
	public void define(String name, String type, VarKind kind)
	{
		ArrayList<String> methodVarDetails = null;
		ArrayList<String> classVarDetails = null;
		switch(kind){
		case ARG:   methodVarDetails = new ArrayList<String>();
					methodVarDetails.add(TYPE_COLUMN, type);
					methodVarDetails.add(KIND_COLUMN, "argument");
					methodVarDetails.add(INDEX_COLUMN,
							Integer.toString(argRunningIndex));
					methodScopeTable.put(name, methodVarDetails);
					argRunningIndex++;
					break;
	
		
	     case VAR: methodVarDetails = new ArrayList<String>();
	   				methodVarDetails.add(TYPE_COLUMN, type);
	   				methodVarDetails.add(KIND_COLUMN, "variable");
	   				methodVarDetails.add(INDEX_COLUMN,
	   						Integer.toString(varRunningIndex));
	   				methodScopeTable.put(name, methodVarDetails);
	   				varRunningIndex++;
	   				break;
	   				
	     case STATIC: classVarDetails = new ArrayList<String>();
		 			classVarDetails.add(TYPE_COLUMN, type);
		 			classVarDetails.add(KIND_COLUMN, "static");
		 			classVarDetails.add(INDEX_COLUMN,
				Integer.toString(staticRunningIndex));
		 			classScopeTable.put(name, classVarDetails);
		 			staticRunningIndex++;
		 			break;
		
		
	     case FIELD: classVarDetails = new ArrayList<String>();
	     			classVarDetails.add(TYPE_COLUMN, type);
	     			classVarDetails.add(KIND_COLUMN, "field");
	     			classVarDetails.add(INDEX_COLUMN,
	     					Integer.toString(fieldRunningIndex));
	     			classScopeTable.put(name, classVarDetails);
	     			fieldRunningIndex++;
	     			break;
	   	default: break;
		}
	}
	public int varCount(VarKind kind)
	{
		int varCount = 0;
		switch(kind){
		case STATIC: varCount = staticRunningIndex;
					break;
					
		case ARG:   varCount = argRunningIndex;
					break;
					
		case FIELD: varCount = fieldRunningIndex;
					break;
					
		case VAR:  varCount = varRunningIndex;
				   break;
		default:
			break;
		}
		return varCount;
	}
	
	public String kindOf(String name)
	{
		if (classScopeTable.containsKey(name))
			return classScopeTable.get(name).get(KIND_COLUMN);
		if (methodScopeTable.containsKey(name))
			return methodScopeTable.get(name).get(KIND_COLUMN);
		return "none";
	}
	
	public String typeOf(String name)
	{
		if (classScopeTable.containsKey(name))
			return classScopeTable.get(name).get(TYPE_COLUMN);
		if (methodScopeTable.containsKey(name))
			return methodScopeTable.get(name).get(TYPE_COLUMN);
		return "none";
	}
	
	public String indexOf(String name)
	{
		if (classScopeTable.containsKey(name))
			return classScopeTable.get(name).get(INDEX_COLUMN);
		if (methodScopeTable.containsKey(name))
			return methodScopeTable.get(name).get(INDEX_COLUMN);
		return "none";
	}
	
	public void addMethodType(String name, String type){
		methodTypesTable.put(name, type);
	}
	
	public String getMethodType(String name){
		return methodTypesTable.get(name);
	}
}
