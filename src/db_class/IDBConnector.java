package db_class;

import java.util.List;

import mr_random_classes.IElemList;
import mr_random_classes.IList;

public interface IDBConnector {
	
	public List<IList> getLists();
	public IList getList(long id);
	public List<IElemList> getElemsList(long idList);
	public IElemList getElemList(long idElemList);	
	
	public long setList(IList list);
	public long setElemList(IElemList elemList);
	
	public int removeList(IList list);
	public int removeElemList(IElemList elemList);
}
