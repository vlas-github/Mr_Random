package mr_random_classes;

import java.util.Date;
import java.util.List;

public interface IList {

	public long getID();
	public void setID(long id);
	public String getName();
	public void setName(String name);
	public Date getDate();
	public void setDate(Date date);
	public List<IElemList> getElemsList();
	public void setElemList(List<IElemList> elemList);
}
