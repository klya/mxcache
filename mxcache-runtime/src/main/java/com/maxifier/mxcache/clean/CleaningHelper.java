/*
 * Copyright (c) 2008-2014 Maxifier Ltd. All Rights Reserved.
 */
package com.maxifier.mxcache.clean;

import com.maxifier.mxcache.caches.CleaningNode;
import com.maxifier.mxcache.impl.resource.DependencyNode;
import com.maxifier.mxcache.impl.resource.DependencyTracker;
import com.maxifier.mxcache.util.TIdentityHashSet;

import javax.annotation.Nonnull;

import java.util.*;
import java.util.concurrent.locks.Lock;

/**
 * CleaningHelper
 *
 * @author Andrey Yakoushin (andrey.yakoushin@maxifier.com)
 * @author Alexander Kochurov (alexander.kochurov@maxifier.com)
 */
public final class CleaningHelper {

    private CleaningHelper() {
    }

    public static void lock(List<Lock> locks) {
        int i = 0;
        int size = locks.size();
        int firstLockedIndex = 0;
        int locked = 0;
        while (locked < size) {
            Lock lock = locks.get(i);
            if (!lock.tryLock()) {
                for (; locked > 0; locked--) {
                    locks.get(firstLockedIndex++).unlock();
                    if (firstLockedIndex == size) {
                        firstLockedIndex = 0;
                    }
                }
                lock.lock();
            }
            locked++;
            i++;
            if (i == size) {
                i = 0;
            }
        }
    }

    public static void unlock(List<Lock> locks) {
        for (Lock lock : locks) {
            lock.unlock();
        }
    }

    public static List<Lock> getLocks(Collection<? extends CleaningNode> caches) {
        List<Lock> locks = new ArrayList<Lock>(caches.size());
        for (CleaningNode cache : caches) {
            Lock lock = cache.getLock();
            if (lock != null) {
                locks.add(lock);
            }
        }
        return locks;
    }

    private static int lockLists(List<WeakList<?>> lists) {
        int version = 0;
        for (WeakList<?> list : lists) {
            version += list.lock();
        }
        return version;
    }

    private static void unlockLists(List<WeakList<?>> lists) {
        for (WeakList<?> list : lists) {
            list.unlock();
        }
    }

    private static void clear(Collection<? extends CleaningNode> caches) {
        for (CleaningNode cache : caches) {
            cache.clear();
        }
    }

    public static void clear(@Nonnull CleanableInstanceList list) {
        List<WeakList<?>> lists = new ArrayList<WeakList<?>>();
        List<CleaningNode> elements = new ArrayList<CleaningNode>();

        Iterable<DependencyNode> nodes = nodeMapping(elements);

        outer: while (true) {
            int subtreeVersion = list.deepLock();
            try {
                lists.clear();
                list.getLists(lists);
            } finally {
                list.deepUnlock();
            }

            int listsVersion = lockLists(lists);
            try {
                elements.clear();
                list.getCaches(elements);
            } finally {
                unlockLists(lists);
            }
            TIdentityHashSet<CleaningNode> elementsAndDependent = DependencyTracker.getAllDependentNodes(nodes, elements);
            // цикл проверки модификации зависимостей
            while (true) {
                List<Lock> locks = getLocks(elementsAndDependent);
                lock(locks);
                try {
                    TIdentityHashSet<CleaningNode> newElements = DependencyTracker.getAllDependentNodes(nodes, elements);
                    if (!newElements.equals(elementsAndDependent)) {
                        // набор зависимых кэшей изменился, придется еще раз все блокировать заново
                        elementsAndDependent = newElements;
                        continue;
                    }
                    int newSubtreeVersion = list.deepLock();
                    try {
                        if (newSubtreeVersion != subtreeVersion) {
                            continue outer;
                        }
                        int newListsVersion = lockLists(lists);
                        try {
                            if (newListsVersion != listsVersion) {
                                continue outer;
                            }
                            clear(elementsAndDependent);
                            return;
                        } finally {
                            unlockLists(lists);
                        }
                    } finally {
                        list.deepUnlock();
                    }
                } finally {
                    unlock(locks);
                }
            }
        }
    }

    public static TIdentityHashSet<CleaningNode> lockRecursive(DependencyNode initial) {
        TIdentityHashSet<CleaningNode> elements = DependencyTracker.getAllDependentNodes(Collections.singleton(initial));
        Iterable<DependencyNode> nodes = nodeMapping(elements);
        while (true) {
            List<Lock> locks = getLocks(elements);
            lock(locks);
            TIdentityHashSet<CleaningNode> newElements = DependencyTracker.getAllDependentNodes(nodes, elements);
            if (!newElements.equals(elements)) {
                // we have to unlock all locks and lock them again among with new ones as advanced locking algorithm
                // locks guarantees the absence of deadlocks only if all locks are locked at once.
                unlock(locks);
                elements.addAll(newElements);
                continue;
            }
            return elements;
        }
    }

    public static void lockAndClear(Collection<? extends CleaningNode> elements) {
        Iterable<DependencyNode> nodes = nodeMapping(elements);
        TIdentityHashSet<CleaningNode> elementsAndDependent = DependencyTracker.getAllDependentNodes(nodes, elements);
        while (true) {
            List<Lock> locks = getLocks(elementsAndDependent);
            lock(locks);
            try {
                TIdentityHashSet<CleaningNode> newElements = DependencyTracker.getAllDependentNodes(nodes, elements);
                if (!newElements.equals(elementsAndDependent)) {
                    // набор зависимых кэшей изменился, придется еще раз все блокировать заново
                    elementsAndDependent = newElements;
                    continue;
                }
                for (CleaningNode element : elementsAndDependent) {
                    element.clear();
                }
                return;
            } finally {
                unlock(locks);
            }
        }
    }

    private static Iterable<DependencyNode> nodeMapping(final Collection<? extends CleaningNode> elements) {
        return new MappingIterable<CleaningNode, DependencyNode>(elements) {
            @Override
            public DependencyNode map(CleaningNode cleaningNode) {
                return cleaningNode.getDependencyNode();
            }
        };
    }
}