package com.vlas.mr_random;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import db_class.DBConnector;

import mr_random_classes.ElemList;
import mr_random_classes.IElemList;
import mr_random_classes.IList;
import mr_random_classes.MyList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final int CM_DELETE_ID = 3001;
	
	TextView 				tvCountList;
	Spinner 				spLists;
	ListView 				lvElemList;
	EditText 				etAdd;

	List<IList> 			lists;
	IList 					list;
	
	DBConnector 			connector;
	Random 					random;
	AlertDialog.Builder 	ad;
	Context 				context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        connector 		= new DBConnector(this);
        
        tvCountList 	= (TextView)findViewById(R.id.tvCountList);
        spLists 		= (Spinner)findViewById(R.id.spLists);
        lvElemList 		= (ListView)findViewById(R.id.lvElemList);
        etAdd 			= (EditText)findViewById(R.id.etAdd);
        
        lists			= connector.getLists();          
        random 			= new Random(Calendar.getInstance().getTimeInMillis());        
        context 		= MainActivity.this;
        
        if (lists.size() == 0) {
        	connector.setList(new MyList("Main List", new Date(Calendar.getInstance().getTimeInMillis())));
        	lists = connector.getLists();
        }
        
        list = lists.get(setSpinnerLists(lists));
        
        /*if (i >= 0) {        	
        	list = lists.get(i);
        } else {        	
        	list = new MyList("Main List", new Date(Calendar.getInstance().getTimeInMillis()));
        	list.setID(connector.setList(list));
        }*/
        
        lvElemList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lvElemList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				list.getElemsList().get(arg2).setUsed(!list.getElemsList().get(arg2).getUsed());
				connector.setList(list);
			}
		});
        
        setListElemList(list);
        
        registerForContextMenu(lvElemList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private int setSpinnerLists(List<IList> list) {
    	
    	ArrayList<String> stringElemList = new ArrayList<String>();
    	
    	for(int i = 0; i < list.size(); i++)
    		stringElemList.add(list.get(i).getName());
    	
    	ArrayAdapter<String> adapter = 
    			new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringElemList);
    	
    	
    	spLists.setAdapter(adapter);
    	
    	return spLists.getSelectedItemPosition();
    }
    private void setListElemList(IList list) {
    	
    	ArrayList<String> stringElemList = new ArrayList<String>();
    	
    	for(int i = 0; i < list.getElemsList().size(); i++)    		
    		stringElemList.add(list.getElemsList().get(i).getName() + " : " + String.valueOf(list.getElemsList().get(i).getPriority()));
    	
    	ArrayAdapter<String> adapter = 
    			new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, stringElemList);
    	
    	lvElemList.setAdapter(adapter);
    	
    	for(int i = 0; i < adapter.getCount(); i++)
    		lvElemList.setItemChecked(i, list.getElemsList().get(i).getUsed());
    }

    public void btnAddElemList_onClick(View v){
    	
    	ElemList elemList = new ElemList(etAdd.getText().toString(), true, list.getID());
		
		list.getElemsList().add(elemList);
		
		elemList.setID(connector.setElemList(elemList));
		
		setListElemList(list);
		
		etAdd.setText("");
	}
    public void btnClear_onClick(View v) {
    	
    	etAdd.setText("");
    }    
    public void btnGetRandomElem_onClick(View v) {
    	    	
    	List<IElemList> arrayList = new ArrayList<IElemList>();
		SparseBooleanArray checkedItems = lvElemList.getCheckedItemPositions();
		
		float sumPriority = 0;
    	
    	for (int i = 0; i < checkedItems.size(); i++) {
    		
    		if (checkedItems.get(i)) {
    			
    			arrayList.add(list.getElemsList().get(i));
    			sumPriority += list.getElemsList().get(i).getPriority();    			
    		}
    	}
    	    	
    	float priority = random.nextFloat();
    	
    	float curPriority = 0;
    	
    	int curElem = 0;
    	
    	for (int i = 0; i < arrayList.size(); i++) {
    		
    		curPriority += arrayList.get(i).getPriority() / sumPriority;
    		
    		if (priority <= curPriority && priority > curPriority - arrayList.get(i).getPriority() / sumPriority) {
    			
    			curElem = i;
    			
    			arrayList.get(i).setPriority(arrayList.get(i).getPriority() / 2);
    		} else {
    			
    			arrayList.get(i).setPriority(arrayList.get(i).getPriority() + arrayList.get(i).getPriority() / arrayList.size());
    		}
    	}
    	
    	connector.setList(list);
    	
    	ad = new AlertDialog.Builder(this);
    	
    	String button1String = "Оке";
        String button2String = "Не не не";
    	
    	ad.setTitle("Mr. Random выберает: ");
        ad.setMessage(arrayList.get(curElem).getName()); 
        ad.setPositiveButton(button1String, new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Оке", Toast.LENGTH_LONG).show();
            }
        });
        ad.setNegativeButton(button2String, new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(context, "Не не не", Toast.LENGTH_LONG) .show();
            }
        });   	
        
        ad.show();
        
        setListElemList(list);
    }
    
    public void btnClearPriority_onClick(View v) {
    	
    	for (IElemList elem : list.getElemsList()) {
    		
    		elem.setPriority(100);
    	}
    	
    	connector.setList(list);
    	
    	setListElemList(list);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);
      menu.add(0, CM_DELETE_ID, 0, "Удалить запись");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	if (item.getItemId() == CM_DELETE_ID) {
    		AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();
        
    		connector.removeElemList(list.getElemsList().get(acmi.position));
    		list.getElemsList().remove(acmi.position);
        
    		setListElemList(list);
        
    		return true;
    	}
    	return super.onContextItemSelected(item);
    }
}
