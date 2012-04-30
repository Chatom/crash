/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.crsh.vfs;

import org.crsh.util.IO;
import org.crsh.vfs.spi.FSDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
class Handle<H> {

  /** . */
  private final FSDriver<H> driver;

  /** . */
  final Key key;

  /** . */
  final H handle;

  Handle(FSDriver<H> driver, H handle) throws IOException {
    String name = driver.name(handle);
    boolean dir = driver.isDir(handle);

    //
    this.driver = driver;
    this.handle = handle;
    this.key = new Key(name, dir);
  }

  Iterable<Handle<H>> children() throws IOException {
    List<Handle<H>> children = new ArrayList<Handle<H>>();
    for (H h : driver.children(handle)) {
      children.add(new Handle<H>(driver, h));
    }
    return children;
  }

  Resource getResource() throws IOException {
    InputStream in = open();
    String content = IO.readAsUTF8(in);
    long lastModified = getLastModified();
    return new Resource(content, lastModified);
  }

  InputStream open() throws IOException {
    return driver.open(handle);
  }

  long getLastModified() throws IOException {
    return driver.getLastModified(handle);
  }

  @Override
  public String toString() {
    return "Handle[driver=" + driver +",key=" + key + ",handle=" + handle + "]";
  }
}
