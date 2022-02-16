/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *//*

package com.datastax.oss.driver.internal.core.pool;

import com.datastax.oss.driver.shaded.guava.common.base.Preconditions;
import com.datastax.oss.driver.shaded.guava.common.collect.Iterators;
import edu.umd.cs.findbugs.annotations.NonNull;
import net.jcip.annotations.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

*/
/**
 * Concurrent structure used to store the channels of a pool.
 *
 * <p>Its write semantics are similar to "copy-on-write" JDK collections, selection operations are
 * expected to vastly outnumber mutations.
 *//*

@ThreadSafe
class GremlinClientSet implements Iterable<GremlinClient> {

  private static final Logger LOG = LoggerFactory.getLogger(GremlinClientSet.class);
  */
/**
   * The maximum number of iterations in the busy wait loop in {@link #next()} when there are
   * multiple channels. This is a backstop to protect against thread starvation, in practice we've
   * never observed more than 3 iterations in tests.
   *//*

  private static final int MAX_ITERATIONS = 50;

  private volatile GremlinClient[] channels;
  private final ReentrantLock lock = new ReentrantLock(); // must be held when mutating the array

  GremlinClientSet() {
    this.channels = new GremlinClient[] {};
  }

  void add(GremlinClient toAdd) {
    Preconditions.checkNotNull(toAdd);
    lock.lock();
    try {
      assert indexOf(channels, toAdd) < 0;
      GremlinClient[] newChannels = Arrays.copyOf(channels, channels.length + 1);
      newChannels[newChannels.length - 1] = toAdd;
      channels = newChannels;
    } finally {
      lock.unlock();
    }
  }

  boolean remove(GremlinClient toRemove) {
    Preconditions.checkNotNull(toRemove);
    lock.lock();
    try {
      int index = indexOf(channels, toRemove);
      if (index < 0) {
        return false;
      } else {
        GremlinClient[] newChannels = new GremlinClient[channels.length - 1];
        int newI = 0;
        for (int i = 0; i < channels.length; i++) {
          if (i != index) {
            newChannels[newI] = channels[i];
            newI += 1;
          }
        }
        channels = newChannels;
        return true;
      }
    } finally {
      lock.unlock();
    }
  }

  */
/** @return null if the set is empty or all are full *//*

  GremlinClient next() {
    GremlinClient[] snapshot = this.channels;
    switch (snapshot.length) {
      case 0:
        return null;
      case 1:
        GremlinClient onlyChannel = snapshot[0];
        return onlyChannel.preAcquireId() ? onlyChannel : null;
      default:
        for (int i = 0; i < MAX_ITERATIONS; i++) {
          GremlinClient best = null;
          int bestScore = 0;
          for (GremlinClient channel : snapshot) {
//            int score = channel.getAvailableIds();
//            if (score > bestScore) {
//              bestScore = score;
//              best = channel;
//            }
            best = channel;
          }
          if (best == null) {
            return null;
          } else if (best.preAcquireId()) {
            return best;
          }
        }
        LOG.trace("Could not select a channel after {} iterations", MAX_ITERATIONS);
        return null;
    }
  }

  */
/** @return the number of available stream ids on all channels in this channel set. *//*

  int getAvailableIds() {
    int availableIds = 0;
    GremlinClient[] snapshot = this.channels;
    for (GremlinClient channel : snapshot) {
      availableIds ++;
    }
    return availableIds;
  }

  */
/**
   * @return the number of requests currently executing on all channels in this channel set
   *     (including {@link #getOrphanedIds() orphaned ids}).
   *//*

  int getInFlight() {
    int inFlight = 0;
    GremlinClient[] snapshot = this.channels;
    for (GremlinClient channel : snapshot) {
      inFlight ++;
    }
    return inFlight;
  }

  */
/**
   * @return the number of stream ids for requests in all channels in this channel set that have
   *     either timed out or been cancelled, but for which we can't release the stream id because a
   *     request might still come from the server.
   *//*

  int getOrphanedIds() {
    int orphanedIds = 0;
    */
/*Client[] snapshot = this.channels;
    for (Client channel : snapshot) {
      orphanedIds += channel.getOrphanedIds();
    }*//*

    return orphanedIds;
  }

  int size() {
    return this.channels.length;
  }

  @NonNull
  @Override
  public Iterator<GremlinClient> iterator() {
    return Iterators.forArray(this.channels);
  }

  private static int indexOf(GremlinClient[] channels, GremlinClient key) {
    for (int i = 0; i < channels.length; i++) {
      if (channels[i] == key) {
        return i;
      }
    }
    return -1;
  }
}
*/
