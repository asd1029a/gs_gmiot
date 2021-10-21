package com.danusys.guardian.socket;

import org.vertx.java.core.Vertx;

public abstract interface EmbeddableVerticle {
	public abstract void start(Vertx paramVertx);
	  
	public abstract String host();
	  
	public abstract int port();
}
