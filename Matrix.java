package convertion.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * This class is a wrapper class for a List of Lists that makes it easier to define matrices.
 * @author Gijs van der Meijde
 * @param <T> the type of the inner values.
 */
public class Matrix<T> {
	private List<List<T>> matrix = new ArrayList<List<T>>();
	private int rows = 0, columns = 0;
	
	/**
	 * Default constructor for Matrix.
	 */
	public Matrix(){
		
	}
	
	/**
	 * Constructor that initializes a matrix of given sizes with null;
	 * @param rows, amount of rows.
	 * @param columns, amount of columns.
	 */
	public Matrix(int columns, int rows){
		this();
		this.growX(columns);
		this.growY(rows);
	}
	
	/**
	 * Constructor that initializes a square matrix of given size with null;
	 * @param size, amount of rows and columns of matrix.
	 */
	public Matrix(int size){
		this(size,size);
	}
	
	/**
	 * This function sets all fields in the matrix to the given value.
	 * @param value, the value to initialize the matrix with.
	 * @return the current matrix.
	 */
	public Matrix<T> initialize(T value){
		for(int i=0; i<columns; i++)
			for(int j=0; j<rows; j++)
				this.set(value, i,j);
		return this;
	}
	
	/**
	 * This function retrieves the value on the given index.
	 * @param row, the row index.
	 * @param column, the column index.
	 * @return
	 */
	public T get(int row, int column){
		if(row >= rows || column >= columns)
			throw new IndexOutOfBoundsException(String.format("Index %dx%d out of bounds. Matrix size is %dx%d.", row, column, rows, columns));
		return matrix.get(column).get(row);
	}
	
	public T get(Coord<Integer,Integer> coordinate){
		return this.get(coordinate.fst(), coordinate.snd());
	}
	
	/**
	 * This function sets the given value on the given index.
	 * @param value, the value to set.
	 * @param row, the row index.
	 * @param column, the column index.
	 * @return
	 */
	public Matrix<T> set(T value, int row, int column){
		if(row >= rows)
			growY(row-(rows-1));
		if(column >= columns)
			growX(column-(columns-1));
		matrix.get(column).set(row, value);
		return this;
	}
	
	public Matrix<T> set(T value, Coord<Integer,Integer> coordinate){
		return this.set(value, coordinate.fst(), coordinate.snd());
	}
	
	public int getRowAmount(){
		return rows;
	}
	
	public int getColumnAmount(){
		return columns;
	}
	
	public List<T> getRow(int row){
		List<T> toReturn = new ArrayList<T>();
		for(List<T> column : this.matrix)
			toReturn.add(column.get(row));
		return toReturn;
	}
	
	public List<T> getColumn(int column){
		return this.matrix.get(column);
	}
	
	/**
	 * This function flattens the matrix and returns all values in one list.
	 * @return a list of all values in the matrix.
	 */
	public List<T> asList(){
		List<T> toReturn = new ArrayList<T>();
		for(List<T> row : this.matrix)
			toReturn.addAll(row);
		return toReturn;
	}
	
	/**
	 * This function increases the matrix size and initializes the new fields with null;
	 * @param rows, the amount of rows to increase (>=0).
	 * @param columns, the amount of columns to increase (>=0).
	 * @return the current matrix.
	 */
	public Matrix<T> increase(int rows, int columns){
		if(rows >= 0 && columns >= 0){
			this.growX(columns);
			this.growY(rows);
		}
		return this;
	}
	
	/**
	 * This function increases the matrix size on both axises by the given size.
	 * @param size, the amount of rows and columns to increase.
	 * @return the current matrix.
	 */
	public Matrix<T> increase(int size){
		return this.increase(size, size);
	}
	
	/**
	 * This function returns the inner list that is used for the matrix.
	 * @return, the list of lists used by this class.
	 */
	public List<List<T>> getInnerList(){
		return matrix;
	}
	
	/**
	 * This function copies a matrix.
	 * Note that while the matrix is copied, the values are referenced!
	 * @param m, the matrix to copy.
	 * @return a copy of m.
	 */
	public <E> Matrix<E> copy(Matrix<E> m){
		Matrix<E> copy = new Matrix<E>();
		for(int i=0; i<m.getColumnAmount()-1; i++)
			for(int j=0; j<m.getRowAmount()-1; j++)
				copy.set(m.get(i, j), i,j);
		return copy;
	}
	
	/**
	 * This function returns a copy of the current matrix.
	 * Note that while the matrix is copied, the values are referenced!
	 * @return a copy of the current matrix.
	 */
	public Matrix<T> copy(){
		return copy(this);
	}
	
	/**
	 * Pretty printer for matrix.
	 * @return pretty printed matrix.
	 */
	public String toString(){
		String s = "";
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < columns; j++){
				s += (this.get(i, j) == null ? "-" : this.get(i, j).toString()) + " ";
			}
			s += "\n"; 
		}
		return s;		
	}
	
	/**
	 * This function filters the matrix by checking the given predicate.
	 * @param p, the predicate to check.
	 * @param copy, weather the matrix needs to be copied or modified. If not given the current model will be modified.
	 * @return the 'new' matrix.
	 */
	public Matrix<T> filter(Predicate<T> p, boolean copy){
		Matrix<T> m = null;
		if(copy){
			m = this.copy();
		}else{
			m = this;
		}
		for(int i = 0; i<columns; i++){
			//matrix.set(i, matrix.get(i).stream().filter(p).collect(Collectors.<T>toList()) ); //Will cause uneven columns.
			for(int j=0; j<rows; j++){
				if(!p.test(m.get(i, j)))
					m.set(null, i,j);
			}
		}
		return m;
	}
	
	/**
	 * This function filters the matrix by checking the given predicate.
	 * @param p, the predicate to check.
	 * @return the 'new' matrix.
	 */
	public Matrix<T> filter(Predicate<T> p){
		return filter(p, false);
	}
	
	/*--------------------|
	|  MATRIX OPERATIONS  |
	|--------------------*/
	
	/**
	 * Basic matrix multiplication (only works for Number subtypes).
	 * @param m, the matrix to multiply with this one.
	 * @return the new matrix.
	 * @throws Exception, an exception is thrown when the matrices could not be multiplied.
	 */
	public Matrix<T> multiply(Matrix<T> m) throws Exception{
		if(!this.isNumber())
			throw new Exception("Cannot multiply non-numbers!");
		
		int newSize = Integer.max(rows, columns);
		Matrix<Double> s = new Matrix<Double>(newSize, newSize);
		
		//Row * Column.
		if(!(rows == m.getColumnAmount() && columns == m.getRowAmount()))
			throw new Exception("Incompatible matrix sizes!");
		
		for(int i = 0; i<newSize; i++){
			for(int j = 0; j<newSize; j++){
				Double newVal = .0;
				for(int tRow = 0; tRow < rows; tRow++){
					for(int mCol = 0; mCol < m.getColumnAmount(); mCol++){
						newVal += ((Double) this.get(tRow, j)) * ((Double) m.get(i, mCol)); 
					}
				}
				s.set(newVal, i, j);
			}
		}
		//return s;
		throw new Exception("Not implemented yet...");
	}
	
	/**
	 * This function will apply the Floyd-Warshall algorithm to the matrix and return a new matrix containing all 'shortest distances'.
	 * For this to work the matrix needs to be casted to a matrix of doubles.
	 * Source: https://www.geeksforgeeks.org/dynamic-programming-set-16-floyd-warshall-algorithm/
	 * @return a matrix of all shortest distances as doubles.
	 * @throws Exception, when the matrix could not be casted to a double.
	 */
	public Matrix<Double> floydWarshall() throws Exception{
		if(!this.isNumber())
			throw new Exception("Cannot apply Floyd-Warshall on non-numbers!");
		
		Matrix<Double> dist = (Matrix<Double>) this.copy();
		for(int k = 0; k < columns; k++){
			for(int i = 0; i < columns; i++){
				for(int j = 0; j < rows; j++){
					if (dist.get(i, k) + dist.get(k,j) < dist.get(i,j))
                        dist.set(dist.get(i,k) + dist.get(k,j), i,j);
				}
			}
		}
		
		return dist;
	}
	
	/**
	 * This function returns the shortest route trough the matrix (if one exists)
	 * @param goal, the index to reach.
	 * @param origin, the index to start at.
	 * @return a list of coordinates representing the shortest path.
	 */
	public List<Coord<Integer,Integer>> getRoute(Coord<Integer,Integer> from, Coord<Integer,Integer> to){
		Matrix<Boolean> visited = new Matrix<Boolean>(columns,rows);
		for(int i = 0; i<columns; i++)
			for(int j = 0; j<rows; j++)
				visited.set(this.get(i, j)==null, i, j);
		return getRouteRecursively(from, to, visited);
	}
	
	/**
	 * This function returns the shortest route trough the matrix (if one exists).
	 * @param goal, the index to reach.
	 * @param origin, the index to start at.
	 * @param visited, a boolean matrix to mark the paths that are visited (can be used to block routes).
	 * @return a list of coordinates representing the shortest path.
	 */
	private ArrayList<Coord<Integer,Integer>> getRouteRecursively(Coord<Integer, Integer> goal, Coord<Integer, Integer> origin, Matrix<Boolean> visited){
		ArrayList<Coord<Integer,Integer>> left=null,right=null,up=null,down=null,route = new ArrayList<Coord<Integer,Integer>>();
		visited.set(true,origin.fst(),origin.snd());
		route.add(origin);
		
		//Check if goal is reached, 
		if(goal.equals(origin)){
			return route;
		}
		
		//Check all directions.
		if(origin.fst() > 0 && !visited.get(origin.fst()-1,origin.snd())){
			left = getRouteRecursively(goal
										, new Coord<Integer,Integer>(origin.fst()-1, origin.snd())
										, copy(visited));
		}
		if(origin.fst() > columns-1 && !visited.get(origin.fst()+1, origin.snd())){
			right = getRouteRecursively(goal
										, new Coord<Integer,Integer>(origin.fst()+1, origin.snd())
										, copy(visited));
		}
		if(origin.snd() > 0 && !visited.get(origin.fst(), origin.snd()-1)){
			up = getRouteRecursively(goal
										, new Coord<Integer,Integer>(origin.fst(), origin.snd()-1)
										, copy(visited));
		}
		if(origin.snd() > rows-1 && !visited.get(origin.fst(), origin.snd()+1)){
			down = getRouteRecursively(goal
										, new Coord<Integer,Integer>(origin.fst(), origin.snd()+1)
										, copy(visited));
		}
		
		if(up == null && down == null && left == null && right == null)
			return null;
		if(up.size() < down.size() && up.size() < left.size() && up.size() < right.size()){
			route.addAll(up);
		}else if(down.size() < up.size() && down.size() < left.size() && down.size() < right.size()){
			route.addAll(down);
		}else if(left.size() < down.size() && left.size() < up.size() && left.size() < right.size()){
			route.addAll(left);
		}else if(right.size() < down.size() && right.size() < left.size() && right.size() < up.size()){
			route.addAll(right);
		}
		return route;
	}
	
	/**
	 * This function returns the shortest route through a weighted graph.
	 * It uses dijkstra's algorithm to find the shortest route.
	 * @param goal, the index to reach.
	 * @param origin, the index to start at.
	 * @return a list of coordinates representing the shortest path.
	 * @throws Exception if route could not be calculated.
	 */
	public List<Coord<Integer,Integer>> getWeightedRoute(Coord<Integer,Integer> from, Coord<Integer,Integer> to) throws Exception{
		throw new Exception("Not implemented yet...");
	}
	
	/**
	 * This function returns the shortest route trough the matrix using the values as weights (if one exists).
	 * It uses the floyd warshall algorithm.
	 * @return a matrix containing all shortest routes through the current matrix.
	 * @throws Exception if routes could not be calculated.
	 */
	public Matrix<List<Coord<Integer,Integer>>> getAllShortestRoutes() throws Exception{
		if(rows != columns)
			throw new Exception("Square matrix is needed for this operation");
		Matrix<List<Coord<Integer,Integer>>> m = new Matrix<List<Coord<Integer,Integer>>>(rows,rows);
		m.initialize(new ArrayList<Coord<Integer,Integer>>());
		for(int k = 1; k<=rows; k++)
			for(int i = 1; i<=rows; i++)
				for(int j = 1; j<=rows; j++){
					
				}
		throw new Exception("Not implemented yet!");
		//return m;
	}
	
	/*--------------------|
	|  PRIVATE FUNCTIONS  |
	|--------------------*/
	
	/**
	 * Function to add X columns.
	 * @param amount the amount of columns to add.
	 */
	private void growX(int amount){
		for(int i = 0; i<amount; i++){
			columns += 1;
			matrix.add(new ArrayList<T>());
			for(int j=0; j<rows; j++){
				matrix.get(columns-1).add(null);
			}
		}
	}
	
	/**
	 * Function to add X rows.
	 * @param amount, the amount of rows to add.
	 */
	private void growY(int amount){
		rows += amount;
		for(int i = 0; i<amount; i++){
			for(int j = 0; j<columns; j++){
				matrix.get(j).add(null);
			}
		}
	}
	
	/**
	 * Checks if the inner type is of the Number supertype.
	 * Returns false if no value is set yet!
	 * @return true if the inner type is of the Number supertype, false otherwise.
	 */
	private boolean isNumber(){
		if(rows <= 0 && columns <= 0)
			return false;
		return matrix.get(0).get(0) instanceof Number;
	}
	
	/**
	 * Helper class since java doesn't support tupples out of the box;
	 * @author Gijs van der Meijde.
	 * @param <X> type of fst.
	 * @param <Y> type of snd.
	 */
	public static class Coord<X,Y> {
		private X x;
		private Y y;
		
		public Coord(X x, Y y) {
			this.x = x;
			this.y = y;
		}
		
		public Coord() {
			x = null;
			y = null;
		}
		
		public X fst() {
			return x;
		}
		public void fst(X x) {
			this.x = x;
		}
		public Y snd() {
			return y;
		}
		public void snd(Y y) {
			this.y = y;
		}
		@Override
		public boolean equals(Object o){
			if(!(o instanceof Coord))
				return false;
			return ((Coord<X,Y>) o).fst().equals(x) && ((Coord<X,Y>) o).snd().equals(y);
		}
	}
}
