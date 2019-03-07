package com.frt.fhir.filter;

import javax.servlet.*;
import java.io.*;

public class FilterServletOutputStream extends ServletOutputStream {
	  private DataOutputStream stream; 
	 
	  public FilterServletOutputStream(OutputStream output) { 
	    stream = new DataOutputStream(output); 
	  }
	 
	  public void write(int b) throws IOException  { 
	    stream.write(b); 
	  }
	 
	  public void write(byte[] b) throws IOException  { 
	    stream.write(b); 
	  }
	 
	  public void write(byte[] b, int off, int len) throws IOException  { 
	    stream.write(b,off,len); 
	  } 
	 
}
