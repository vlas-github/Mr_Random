package mr_random_classes;

public class ElemList implements IElemList {
	
	long id;
	String name;
	Boolean uses;
	Long idList;
	float priority;
	
	public ElemList(String name, boolean uses, long idList) {
		this(-1, name, uses, 100 ,idList);
	}
	
	public ElemList(String name, boolean uses, float priority, long idList) {
		this(-1, name, uses, priority, idList);
	}
	
	public ElemList(long id, String name, boolean uses, float priority, long idList) {
		
		this.id = id;
		this.name = name;
		this.uses = uses;
		this.priority = priority;
		this.idList = idList;
	}

	@Override
	public long getID() {

		return id;
	}

	@Override
	public void setID(long id) {
		
		this.id = id;		
	}

	@Override
	public String getName() {
		
		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public boolean getUsed() {

		return  uses;
	}

	@Override
	public void setUsed(boolean used) {
		
		this.uses = used;
	}

	@Override
	public long getIDList() {

		return idList;
	}

	@Override
	public void setIDList(long idList) {
		
		this.idList = idList;
	}

	@Override
	public float getPriority() {
		
		return priority;
	}

	@Override
	public void setPriority(float priority) {
		
		this.priority = priority;
	}
}
