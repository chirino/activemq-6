/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.boot;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * <p>
 * A main class which setups up a classpath and then passes
 * execution off to the ActiveMQ cli main.
 * </p>
 */
public class ActiveMQ
{

   public static void main(String[] args) throws Throwable
   {
      ArrayList<File> dirs = new ArrayList<File>();
      String instance = System.getProperty("activemq.instance");
      if (instance != null)
      {
         dirs.add(new File(new File(instance), "lib"));
      }

      String home = System.getProperty("activemq.home");
      if (home != null)
      {
         dirs.add(new File(new File(home), "lib"));
      }

      ArrayList<URL> urls = new ArrayList<URL>();
      for (File bootdir : dirs)
      {
         if (bootdir.exists() && bootdir.isDirectory())
         {

            // Find the jar files in the directory..
            ArrayList<File> files = new ArrayList<File>();
            for (File f : bootdir.listFiles())
            {
               if (f.getName().endsWith(".jar") || f.getName().endsWith(".zip"))
               {
                  files.add(f);
               }
            }

            // Sort the list by file name..
            Collections.sort(files, new Comparator<File>()
            {
               public int compare(File file, File file1)
               {
                  return file.getName().compareTo(file1.getName());
               }
            });

            for (File f : files)
            {
               add(urls, f);
            }

         }
      }

      if (instance != null)
      {
         System.setProperty("java.io.tmpdir", new File(new File(instance), "tmp").getCanonicalPath());
      }

      // Now setup our classloader..
      URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[urls.size()]));
      Class<?> clazz = loader.loadClass("org.apache.activemq.cli.ActiveMQ");
      Method method = clazz.getMethod("main", args.getClass());
      try
      {
         method.invoke(null, (Object) args);
      }
      catch (InvocationTargetException e)
      {
         throw e.getTargetException();
      }

   }

   private static void add(ArrayList<URL> urls, File file)
   {
      try
      {
         urls.add(file.toURI().toURL());
      }
      catch (MalformedURLException e)
      {
         e.printStackTrace();
      }
   }

}
