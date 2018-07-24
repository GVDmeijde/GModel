package convertion.model;

/**
 * Helper class since java doesnt support tupples out of the box;
 * @author Gijs van der Meijde.
 * @param <X> type of fst.
 * @param <Y> type of snd.
 */
public class Tuple<X,Y> {
	private X x;
	private Y y;
	
	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	public Tuple() {
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
		if(!(o instanceof Tuple))
			return false;
		return ((Tuple<X,Y>) o).fst().equals(x) && ((Tuple<X,Y>) o).snd().equals(y);
	}
}