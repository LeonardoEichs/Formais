package RegularLanguages;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

public class State implements Comparable<State> {
	String name;
	UUID id;
	boolean isFinal;
	int n;
	
	public State(String _name, boolean _isFinal, int _n) {
		name = _name;
		isFinal = _isFinal;
		id = UUID.randomUUID();
		n = _n;
	}
	
	public void setFinal() {
		isFinal = true;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !Node.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		State state = (State)obj;
		if (state.name == this.name) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.id);
	}
	@Override
	public int compareTo(State o) {
		return comparator.compare(this, o);
	}
	
	private static final Comparator<State> comparator = Comparator
			.comparingInt((State s) -> s.n);
}
