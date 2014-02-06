package uk.ac.cam.db538.dexter.aux.struct;

import java.io.Serializable;
import java.util.HashSet;

import uk.ac.cam.db538.dexter.aux.TaintConstants;

public class TaintInternal implements Taint, Serializable {

	private InternalDataStructure obj;
	private TaintExternal t_super; 
	
	TaintInternal(InternalDataStructure obj, TaintExternal t_super) {
		define(obj, t_super);
	}
	
	void define(InternalDataStructure obj, TaintExternal t_super) {
		assert this.obj == null;
		assert this.t_super == null;
		assert obj != null;
		assert t_super != null;
		
		this.obj = obj;
		this.t_super = t_super;
	}
	
	public int get() {
		HashSet<TaintInternal> visited = visitedSet.get();
		if (visited.contains(this))
			return TaintConstants.EMPTY.value;
		else
			visited.add(this);

		int result = TaintConstants.EMPTY.value;
		if (this.obj != null)
			result |= this.obj.getTaint();
		if (this.t_super != null)
			result |= this.t_super.get();
		return result;
	}
	
	public int getExternal() {
		if (this.t_super != null)
			return this.t_super.get();
		else
			return TaintConstants.EMPTY.value;
	}

	public void set(int taint) {
		HashSet<TaintInternal> visited = visitedSet.get();
		if (!visited.contains(this)) {
			visited.add(this);
			
			if (this.obj != null)
				this.obj.setTaint(taint);
			if (this.t_super != null)
				this.t_super.set(taint);
		}
	}
	
	public void setExternal(int taint) {
		if (this.t_super != null)
			this.t_super.set(taint);
	}
	
	public boolean belongsTo(Object other) {
		return obj == other;
	}
	
	// THREAD-LOCAL SET OF VISITED NODES
	// prevents infinite looping
	
	private static class VisitedSet extends ThreadLocal<HashSet<TaintInternal>> {
		@Override
		protected HashSet<TaintInternal> initialValue() {
			return new HashSet<TaintInternal>();
		}

		@Override
		public void set(HashSet<TaintInternal> value) {
			throw new UnsupportedOperationException();
		}
	}

	private static final VisitedSet visitedSet;
	static { visitedSet = new VisitedSet(); }
	
	// MUST BE CALLED BEFORE A TRAVERSAL IS INITIATED !!!
	// traversal is every call of Taint.get or Taint.set
	// but that can be initiated even from Assigner.lookup* methods! 
	
	public static void clearVisited() {
		visitedSet.get().clear();
	}
}
