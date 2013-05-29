package org.bbplus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

public class DocumentFactory{
	
	@Autowired
	Utilities bbUtilities;
		
	public String fromId(long id){
		return bbUtilities.getXmlRaw(id);
	}
}

