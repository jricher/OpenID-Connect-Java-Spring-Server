package org.bbplus;

import org.springframework.beans.factory.annotation.Autowired;

public class DocumentFactory{
	
	@Autowired
	Utilities bbUtilities;
		
	public String fromId(long id){
		return bbUtilities.getXmlRaw(id);
	}

}

