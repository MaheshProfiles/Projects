package com.snapbizz.snaptoolkit.domains;

import java.io.InputStream;

public class StreamResponseContainer extends ResponseContainer {

	private InputStream inputStream;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
	
	
}
