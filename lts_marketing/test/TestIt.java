import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.LTUserDAOManager;
import data.ValueObject;


public class TestIt {
	public static void main(String[] args) throws Exception {
		new TestIt().test1();
	}
	
	public void test1() throws Exception {
		Map<String, Long> pList = new HashMap<String, Long>();
		String[] listIds = {"186a41f3-e630-49dd-a07a-59191af9e718", "ac0a1db3-7f63-48f5-b404-2af5216a8533"};
		
		ValueObject vo1 = new ValueObject();
		vo1.setField1("12702269137");

		ValueObject vo2 = new ValueObject();
		vo2.setField1("12702269137");
		
		if (! pList.containsValue(vo1.getField1())) 
				pList.put((String)vo1.getField1(), null);

		if (! pList.containsValue(vo2.getField1())) 
			pList.put((String)vo2.getField1(), null);
		
		
        for (final String listId : listIds) {
            List<ValueObject> listData = new LTUserDAOManager().getListData(listId);
            for (ValueObject vo : listData) {
                    if (! pList.containsValue(vo.getField1())) //add the number only if does not exist
                            pList.put((String)vo.getField1(), (Long)vo.getField2());

            }
    }

        
		for (Map.Entry<String, Long> entry : pList.entrySet())
			System.out.println("num: " + entry.getKey());
	}
}
