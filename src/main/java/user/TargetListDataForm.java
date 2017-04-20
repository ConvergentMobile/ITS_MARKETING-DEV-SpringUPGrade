package user;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.apache.struts.upload.FormFile;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.ListUtils;

public class TargetListDataForm extends ActionForm {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(TargetListDataForm.class);
	private int MAX_ITEMS = 5;

	private List<TargetListData> targetListData;
	private Object[] targetListNumbers;
	private String listId;
	
	public TargetListDataForm() {
	}

	public List<TargetListData> getTargetListData() {
		return targetListData;
	}

	public void setTargetListData(List<TargetListData> targetListData) {
		this.targetListData = targetListData;
	}

	public Object[] getTargetListNumbers() {
		return targetListNumbers;
	}

	public void setTargetListNumbers(Object[] targetListNumbers) {
		this.targetListNumbers = targetListNumbers;
	}

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public void populateForm(HttpServletRequest request, Object[] flist, String mode) {
		if (flist != null && flist.length > 0)
			MAX_ITEMS = flist.length;
		
		if (mode.equals("add")) //increase the size for Add
			MAX_ITEMS++;
		
		if (flist != null) {
			setTargetListNumbers(flist);
		} else {
			targetListNumbers = new Object[MAX_ITEMS];
		}
	}
	
	public void populateForm(HttpServletRequest request, List<TargetListData>flist, String mode) {
		if (flist != null && flist.size() > 0)
			MAX_ITEMS = flist.size();
		
		if (mode.equals("add")) //increase the size for Add
			MAX_ITEMS++;
		
		if (flist != null) {
			setTargetListData(flist);
		} else {
			targetListData = new ArrayList<TargetListData>();
		}

		for (int i = targetListData.size(); i < MAX_ITEMS; i++) {
			TargetListData c = new TargetListData();
			//c.setListId(listId);
			targetListData.add(c);
		}
	}
	
    public void reset(ActionMapping actionMapping, HttpServletRequest httpServletRequest) {   	
    	targetListData = ListUtils.lazyList(new ArrayList(),
        new Factory() {
            public Object create() {
                return new TargetListData();
            }
        });
    }
}
