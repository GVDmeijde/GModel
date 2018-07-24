package convertion.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import application.Main;
import convertion.model.Matrix.Coord;

/**
 * This class is developed for Model Checking, it uses a matrix of transitions to represent a model.
 * @author Gijs van der Meijde
 */
public class Model {
	public Set<String> functions = new HashSet<String>();
	private Matrix<List<Transition>> matrix; /* Rows = from state, Columns = to state, value = list of transitions between those states */
	private Matrix<List<Transition>> shortestRoutes = null;
	private Map<String,Integer> stateMap = new HashMap<String,Integer>(); /* Map that links a state name to a index in the matrix */
	private int startStateIndex = 0;

	/**
	 * Constructor that copies a list of transitions and a list of states.
	 * @param transitions, the list of transitions.
	 * @param states, the list of states.
	 */
	public Model(List<Transition> transitions, List<String> states){
		this.initializeStates(states);
		this.addAll((Transition[]) transitions.toArray());
	}
	
	/**
	 * Constructor that copies a list of transitions and extracts all states from the transitions.
	 * @param transitions, the list of transitions.
	 */
	public Model(List<Transition> transitions){
		Set<String> states = new HashSet<String>();
		for(Transition t : transitions){
			states.add(t.orgFrom);
			states.add(t.orgTo);
		}
		this.initializeStates(new ArrayList<String>(states));
		this.addAll((Transition[]) transitions.toArray());
	}
	
	/**
	 * Default constructor.
	 */
	public Model(){
		this.matrix = new Matrix<List<Transition>>();
		this.stateMap = new HashMap<String,Integer>();
	}
	
	/**
	 * Copy constructor.
	 * @param m, the model to copy.
	 */
	public Model(Model m){
		this.matrix = m.matrix.copy();
		this.stateMap = new HashMap<String,Integer>();
		for(String s : m.getStates())
			this.stateMap.put(s, stateMap.size());
		for(String s : m.functions)
			this.functions.add(s);
	}
	
	/**
	 * This function returns the start state if it exists, returns null otherwise.
	 * @return the start state if it exists, returns null otherwise.
	 */
	public String startState(){
		for (Entry<String, Integer> entry : stateMap.entrySet()) {
	        if (entry.getValue() == startStateIndex) {
	            return entry.getKey();
	        }
	    }
	    return null;
	}
	
	/**
	 * This function sets the startState if the state exists in the model.
	 * @param startState, the name of the startState.
	 * @return the current Model.
	 */
	public Model setStartState(String startState){
		if(stateMap.containsKey(startState))
			this.startStateIndex = stateMap.get(startState);
		return this;
	}
	
	public List<String> getStates(){
		List<String> toReturn = new ArrayList<String>();
		for (Entry<String, Integer> entry : stateMap.entrySet())
			toReturn.add(entry.getKey());
		return toReturn;
	}
	
	/**
	 * This function is used to check if the model is set (is not empty).
	 * @return false if no states exist yet, true otherwise.
	 */
	public boolean isSet(){
		if(stateMap == null || stateMap.size() == 0)
			return false;
		return true;
	}
	
	/**
	 * This function adds a transition (and its states if needed) to the model.
	 * @param t, the transition to add to the model.
	 * @return the current model.
	 */
	public Model add(Transition t){
		this.add(t.orgFrom);
		this.add(t.orgTo);
		this.functions.add(t.input != null ? t.input : (t.label.split("/")[0].trim()));
		if(matrix.get(stateMap.get(t.orgFrom), stateMap.get(t.orgTo)) == null)
			matrix.set(new ArrayList<Transition>(), stateMap.get(t.orgFrom), stateMap.get(t.orgTo));
		matrix.get(stateMap.get(t.orgFrom), stateMap.get(t.orgTo)).add(t);
		return this;
	}
	
	/**
	 * This function is used to add multiple transitions at once.
	 * @param t, the transitions.
	 * @return the current model.
	 */
	public Model addAll(Transition... t){
		for(Transition tt : t)
			this.add(tt);
		return this;
	}
	
	/**
	 * This function adds a state if it doesn't exist already.
	 * @param s, the state to add.
	 * @return the current model.
	 */
	public Model add(String state){
		if(!stateMap.containsKey(state)){
			this.stateMap.put(state, stateMap.size());
			this.matrix.increase(1);
		}
		return this;
	}
	
	/**
	 * This function is used to add multiple states at once.
	 * @param s, the state to add.
	 * @return the current matrix.
	 */
	public Model addAll(String... s){
		for(String ss : s)
			this.add(ss);
		return this;
	}
	
	/**
	 * This function is used to check if a state with the given name exists.
	 * @param name, the name of the state.
	 * @return true if a state with the given name exists, false otherwise.
	 */
	public boolean hasState(String name){
		return stateMap.get(name) != null;
	}
	
	/**
	 * This function returns a list of all transitions with the given state's.
	 * @param from, the name of the origin state.
	 * @param to, the name of the goal state.
	 * @return a list of all transitions between the 2 states.
	 */
	public List<Transition> getTransitions(String from, String to){
		return matrix.get(stateMap.get(from), stateMap.get(to));
	}
	
	/**
	 * This function gets the resulting state after taking a given transition from a given state.
	 * @param from, the state we take the transition from.
	 * @param transition, the transition to take.
	 * @return the resulting state after taking the given transition from the given state.
	 */
	public String getState(String from, String transition){
		Transition t = getTransition(from, transition); 
		return t == null ? null : t.orgTo;
	}
	
	public List<Transition> getTransitions(){
		
		List<Transition> transitions = new ArrayList<Transition>();
		for(List<Transition> list : matrix.asList())
			if(list != null && list.size() > 0)
				transitions.addAll(list);
		return transitions;
	}
	
	/**
	 * This function returns the transition with the given name that starts at the given state.
	 * @param from, the state to start at.
	 * @param name, the name of the transition.
	 * @return the transition if it exists, null otherwise.
	 */
	public Transition getTransition(String from, String name){
		for(List<Transition> ts : matrix.getRow(stateMap.get(from))){
			if(ts != null)
				for(Transition t : ts)
					if(t.label.equals(name))
						return t;
		}
		return null;
	}
	
	/**
	 * This function returns all transitions starting from a given state.
	 * @param s, the state to start.
	 * @return a list of all transitions starting from state s.
	 */
	public List<Transition> getTransitionsFrom(String state){
		List<Transition> toReturn = new ArrayList<Transition>();
		for(List<Transition> ts : matrix.getRow(stateMap.get(state)))
			if(ts != null)
				toReturn.addAll(ts);
		return toReturn.size() == 0 ? null : toReturn;
	}
	
	/**
	 * @TODO TEST THIS FUNCTION
	 * @param from
	 * @param to
	 * @return
	 */
	public List<Transition> getRoute(String from, String to){
		//generateMatrix();
		Coord<Integer,Integer> origin = new Coord<Integer, Integer>(stateMap.get(from), stateMap.get(from));
		Coord<Integer, Integer> goal   = new Coord<Integer, Integer>(stateMap.get(to), stateMap.get(to));
		ArrayList<Transition> route = new ArrayList<Transition>();
		for(Coord<Integer,Integer> c : matrix.getRoute(goal, origin))
			route.add(matrix.get(c).get(0)); //Always pick first transition in list (easy).
		return route;
	}
	
	public Matrix<List<Transition>> floydWarshall(){
		Matrix<List<Transition>> dist = new Matrix<List<Transition>>(matrix.getColumnAmount(),matrix.getRowAmount());
		for(int i = 0; i < dist.getColumnAmount(); i++)
			for(int j = 0; j < dist.getRowAmount(); j++)
				if(matrix.get(j, i) != null && matrix.get(j, i).size()>0){
					dist.set(new ArrayList<Transition>(), j,i);
					dist.get(j,i).add(matrix.get(j, i).get(0));//Only add first transition in list (for convenience).
				}
		
		for(int k = 0; k < dist.getRowAmount(); k++){
			for(int i = 0; i < dist.getColumnAmount(); i++){
				for(int j = 0; j < dist.getRowAmount(); j++){
					if (dist.get(i,j) == null 
							|| (!(dist.get(i, k) == null || dist.get(k, j) == null) 
									&& dist.get(i, k).size() + dist.get(k,j).size() < dist.get(i,j).size())){
						ArrayList<Transition> n = new ArrayList<Transition>();
						n.addAll(dist.get(i,k) == null ? new ArrayList<Transition>() : dist.get(i, k));
						n.addAll(dist.get(k,j) == null ? new ArrayList<Transition>() : dist.get(k, j));
						dist.set(n, i,j);
					}
				}
			}
		}
		this.shortestRoutes = dist;
		return dist;
	}
	
	/**
	 * Temp test function (for the floyd-warshall algorithm above).
	 */
	public void printAllShortestRoutes(){
		System.out.println(floydWarshall());
	}
	
	public List<Transition> getPathFromCalls(List<String> transitions){
		if(this.startState() == null){
			Main.Logger.ERR("Start state not set!");
			return null;
		}
		
		String state = this.startState();
		List<Transition> toReturn = new ArrayList<Transition>();
		
		for(String trans : transitions){
			//Transition t = this.getTransition(state, trans);
			
			for(List<Transition> ts : matrix.getRow(stateMap.get(state))){
				if(ts != null)
					for(Transition t : ts)
						if(t.input.equals(trans)){
							toReturn.add(t);
							state = t.orgTo;
							break;
						}
			}
			
		}
		return toReturn;
	}
	
	/**
	 * This function returns the shortest route between 2 states using the floyd warshall function.
	 * To make sure you have the latest version of the shortest routes, call the floyd warshall function first.
	 * @param from, the name of the state to start.
	 * @param to, the name of the state to reach.
	 * @return a list of transitions that forms the shortest route between the states.
	 */
	public List<Transition> getShortestRoute(String from, String to){
		if(this.shortestRoutes == null)
			this.floydWarshall();
		return shortestRoutes.get(this.stateMap.get(from), this.stateMap.get(to));
	}
	
	public ArrayList<Transition> getDistinguishingSequence(String state){
		System.err.println("getDistinguishingSequence NOT IMPLEMENTED YET!");
		return new ArrayList<Transition>();
	}
	
	public String toString(){
		return this.matrix.toString();
	}
	
	/**
	 * This model initializes the Matrix and StateMap.
	 * @param states a list of all states (names).
	 */
	private void initializeStates(List<String> states){
		for(String s : states){
			stateMap.put(s,stateMap.size());
		}
		matrix = new Matrix<List<Transition>>(stateMap.size());
	}
	
	/**
	 * This function fills the model using the input file.
	 * Currently only input files of the .DOT format as generated by LearnLib are accepted.
	 * @param reader, the reader containing the .DOT file.
	 */
	public Model fromDotFile(Reader reader) throws IOException{
		BufferedReader br = new BufferedReader(reader);
		String startState = null;
		/* read input and extract transitions */
		    for(String line; (line = br.readLine()) != null; ) {
		        if(line.contains("->") && !line.contains("__start")){
		        	Transition t = this.parseTransitionLine(line);
		        	if(t != null){
		        		this.add(t);
		        	}else{
		        		return null;
		        	}
		        }else if(line.contains("->") && line.contains("__start")){
		        	startState = line.split("->")[1].trim();
		        	this.setStartState(startState);
		        }
		    }
		return this;
	}
	
	@Override
	/**
	 * This writes the model to a .DOT file.
	 * @param writer, the Writer for the .DOT file.
	 */
	public void toDotFile(Writer writer) throws Exception{
		writer.write("digraph g {\n__start0 [label=\"\" shape=\"none\"]\n");
		for(String s : this.getStates()){
			writer.write(String.format("    %s [shape=\"circle\" label=\"%s\"];\n", s, s));
		}
		for(Transition t : this.getTransitions()){
			writer.write(String.format("    s%s -> s%s [label=\"%s\"];", t.orgFrom, t.orgTo, (t.label != null ? t.label : (t.input+" / "+t.output))));
		}
		writer.write(String.format("__start0 -> s%s;\n}", this.startState()));
	}
	
	/**
	 * This function parses a .dot file transition line into a Transition.
	 * @param line, the line in the .dot file containing the transition.
	 * @return the parsed Transition.
	 */
	private Transition parseTransitionLine(String line){
		try{
			String[] temp = line.split("->");
			int from = Integer.parseInt(temp[0].trim().substring(1));
			temp = temp[1].split("\\[");
			int to = Integer.parseInt(temp[0].split(";")[0].trim().substring(1));
			String input = temp[1].split("\\/")[0].split("\"")[1].trim();
			String output = temp[1].split("\\/")[1].split("\"")[0].trim();
			return new Transition(from, to, input, output);
			}catch(Exception e){
				System.err.println("Could not extract transition from: \""+line+"\"");
			}
		return null;
	}
	
	/*-------------------------------|
	|			  Filters			 | 
	|-------------------------------*/
	
	/**
	 * This function filters the model's transitions.
	 * @param p, the predicate to check.
	 * @param copy, weather the model needs to be copied or modified. If not given the current model will be modified.
	 * @return the 'new' model.
	 */
	public Model filterTransitions(Predicate<Transition> p, boolean copy){
		Model m = this;
		if(copy)
			m = new Model(this);
		int x=-1,y=-1;
		for(List<List<Transition>> l : m.matrix.getInnerList()){
			x++;
			for(List<Transition> ts : l){
				if(ts != null)
					m.matrix.set(ts.stream().filter(p).collect(Collectors.<Transition>toList()), y, x);
			}
		}
		return m;
	}
	
	/**
	 * This function filters the model's transitions.
	 * @param p, the predicate to check.
	 * @return the 'new' model.
	 */
	public Model filterTransitions(Predicate<Transition> p){
		return filterTransitions(p, false);
	}
	
}
