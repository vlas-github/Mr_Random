package mr_random_classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyList implements IList {

	long id;
	String name;
	Date date;
	List<IElemList> elemList;
	
	public MyList(long id, String name, Date date, List<IElemList> elemList) {
		this.id = id;
		this.name = name;
		this.date = date;
		this.elemList = elemList;
	}
	public MyList(long id, String name, Date date) {
		this(id, name, date, new ArrayList<IElemList>());
	}
	public MyList(String name, Date date) {
		this(-1, name, date);
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
	public Date getDate() {

		return date;
	}

	@Override
	public void setDate(Date date) {
		
		this.date = date;
	}

	@Override
	public List<IElemList> getElemsList() {

		return elemList;
	}
	@Override
	public void setElemList(List<IElemList> elemList) {

		this.elemList = elemList;
	}

	@Override
	public String toString() {
		
		return name;
	}


}
