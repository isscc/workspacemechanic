package com.google.eclipse.mechanic.internal;

import static org.easymock.EasyMock.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.Status;

import junit.framework.TestCase;

import com.google.eclipse.mechanic.IResourceTaskReference;
import com.google.eclipse.mechanic.tests.internal.RunAsJUnitTest;

@RunAsJUnitTest
public class UriTaskProviderTest extends TestCase {
  private static final String CONTENT =
    "{ type : 'com.google.eclipse.mechanic.UriTaskProviderModel', " +
    "metadata : {" +
    "  name: 'green hornet'," +
    "  description: 'i wear green and i fight crime'," +
    "  contact: 'greenhornet.com'" +
    "}, " + 
    "tasks: [" +
    "  'http://www.google.com/foo/bar/baz', " +
    "  'path'," +
    "  'path2'" +
    "]" +
    "} ";

  public void testRelativize() throws Exception {
    URI uri = URI.create("http://www.testuri.com/mechanic/tasks/schema");

    IUriContentProvider cache = createMock(IUriContentProvider.class);
    expect(cache.get(uri)).andReturn(asInputStream(CONTENT));
    replay(cache);

    UriTaskProvider provider = new UriTaskProvider(uri, cache, cache);
    assertEquals(Status.OK_STATUS, provider.initialize());
    List<IResourceTaskReference> taskReferences = provider.getTaskReferences("");

    assertEquals(3, taskReferences.size());
    assertEquals("http://www.google.com/foo/bar/baz", taskReferences.get(0).getPath());
    assertEquals("http://www.testuri.com/mechanic/tasks/path", taskReferences.get(1).getPath());
    assertEquals("http://www.testuri.com/mechanic/tasks/path2", taskReferences.get(2).getPath());

    verify(cache);
  }

	private InputStream asInputStream(String content) {
    return new ByteArrayInputStream(content.getBytes());
  }
}