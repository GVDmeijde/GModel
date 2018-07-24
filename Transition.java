package convertion.model;

public class Transition extends Model{
	//public int from, to;
	public String input, output;
	public String orgFrom, orgTo;
	public String label;
	
	public boolean isUsed = false; //Used for adding trace information.
	public int calls = 0;	//Used for adding trace information.
	
	public Transition(String from, String to, String input, String output){
		this(from, to, input+" / "+output);
		this.input = input;
		this.output = output;
	}
	
	public Transition(int from, int to, String input, String output){
		this(from, to, input+" / "+output);
		this.input = input;
		this.output = output;
	}
	
	public Transition(int from, int to, String label){
		//this.from = from;
		//this.to = to;
		this.label = label;
		this.orgFrom = from+"";
		this.orgTo = to+"";
		setInOutput(label);
		//generateName();
	}
	
	public Transition(String from, String to, String label){
		this.orgFrom = from;
		this.orgTo = to;
		this.label = label;
		setInOutput(label);
		//this.from = Integer.parseInt(from.replaceAll("\\D+",""));
		//this.to = Integer.parseInt(to.replaceAll("\\D+",""));
		//generateName();
	}
	
	public Transition(Transition t){
		this(t.orgFrom, t.orgTo, t.input, t.output);
		this.label = t.label;
	}
	
	public String toString(){
		return label;
	}
	
	private void setInOutput(String label){
		if(label != null && label.contains("/")){
			this.input = label.split("/")[0].trim();
			this.output = label.split("/")[1].trim();
		}
	}
}
