package com.feather.net.decoders;

import com.feather.io.InputStream;
import com.feather.net.Session;

public abstract class Decoder {

	protected Session session;

	public Decoder(Session session) {
		this.session = session;
	}

	public abstract void decode(InputStream stream);

}
