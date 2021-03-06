/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.tests.unit.core.server.impl.fakes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.activemq.api.core.SimpleString;
import org.apache.activemq.core.filter.Filter;
import org.apache.activemq.core.paging.cursor.PageSubscription;
import org.apache.activemq.core.postoffice.PostOffice;
import org.apache.activemq.core.server.Queue;
import org.apache.activemq.core.server.QueueFactory;
import org.apache.activemq.core.server.impl.QueueImpl;

public class FakeQueueFactory implements QueueFactory
{
   private final ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

   private final ExecutorService executor = Executors.newSingleThreadExecutor();

   private PostOffice postOffice;

   public Queue createQueue(final long persistenceID,
                            final SimpleString address,
                            final SimpleString name,
                            final Filter filter,
                            final PageSubscription subscription,
                            final boolean durable,
                            final boolean temporary,
                            final boolean autoCreated)
   {
      return new QueueImpl(persistenceID,
                           address,
                           name,
                           filter,
                           subscription,
                           durable,
                           temporary,
                           autoCreated,
                           scheduledExecutor,
                           postOffice,
                           null,
                           null,
                           executor);
   }

   public void setPostOffice(final PostOffice postOffice)
   {
      this.postOffice = postOffice;

   }

   public void stop() throws Exception
   {
      scheduledExecutor.shutdown();

      executor.shutdown();
   }

}
