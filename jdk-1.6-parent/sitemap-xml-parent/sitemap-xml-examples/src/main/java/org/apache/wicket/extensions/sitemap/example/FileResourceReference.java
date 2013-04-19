package org.apache.wicket.extensions.sitemap.example;

import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;

public class FileResourceReference extends ResourceReference {

	private static final long serialVersionUID = 1L;

	public static FileResourceReference INSTANCE = new FileResourceReference();

	private FileResourceReference() {
		super(FileResourceReference.class, "files");
	}

	@Override
	public IResource getResource() {
		return new ExampleSiteMap();
	}

}
