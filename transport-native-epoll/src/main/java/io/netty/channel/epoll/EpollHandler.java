/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel.epoll;

<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.EventLoopTaskQueueFactory;
=======
import io.netty.channel.Channel;
import io.netty.channel.DefaultSelectStrategyFactory;
import io.netty.channel.IoExecutionContext;
import io.netty.channel.IoHandler;
import io.netty.channel.IoHandlerFactory;
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
import io.netty.channel.SelectStrategy;

import io.netty.channel.SelectStrategyFactory;
import io.netty.channel.SingleThreadEventLoop;

import io.netty.channel.epoll.AbstractEpollChannel.AbstractEpollUnsafe;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.util.IntSupplier;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.io.IOException;
<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
=======
import java.util.concurrent.atomic.AtomicInteger;

>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java

import static io.netty.util.internal.ObjectUtil.checkPositiveOrZero;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;

/**
 * {@link IoHandler} which uses epoll under the covers. Only works on Linux!
 */
<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
class EpollEventLoop extends SingleThreadEventLoop {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
=======
public class EpollHandler implements IoHandler {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollHandler.class);
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java

    static {
        // Ensure JNI is initialized by the time this class is loaded by this time!
        // We use unix-common methods in this class which are backed by JNI methods.
        Epoll.ensureAvailability();
    }

<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
=======
    // Pick a number that no task could have previously used.
    private long prevDeadlineNanos = SingleThreadEventLoop.nanoTime() - 1;
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
    private final FileDescriptor epollFd;
    private final FileDescriptor eventFd;
    private final FileDescriptor timerFd;
    private final IntObjectMap<AbstractEpollChannel> channels = new IntObjectHashMap<>(4096);
    private final boolean allowGrowing;
    private final EpollEventArray events;

    // These are initialized on first use
    private IovArray iovArray;
    private NativeDatagramPacketArray datagramPacketArray;

    private final SelectStrategy selectStrategy;
<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
    private final IntSupplier selectNowSupplier = new IntSupplier() {
        @Override
        public int get() throws Exception {
            return epollWaitNow();
        }
    };

    private static final long AWAKE = -1L;
    private static final long NONE = Long.MAX_VALUE;

    // nextWakeupNanos is:
    //    AWAKE            when EL is awake
    //    NONE             when EL is waiting with no wakeup scheduled
    //    other value T    when EL is waiting with wakeup scheduled at time T
    private final AtomicLong nextWakeupNanos = new AtomicLong(AWAKE);
    private boolean pendingWakeup;
    private volatile int ioRatio = 50;
=======
    private final IntSupplier selectNowSupplier = this::epollWaitNow;
    private final AtomicInteger wakenUp = new AtomicInteger(1);
    private boolean pendingWakeup;
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java

    // See https://man7.org/linux/man-pages/man2/timerfd_create.2.html.
    private static final long MAX_SCHEDULED_TIMERFD_NS = 999999999;

<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
    EpollEventLoop(EventLoopGroup parent, Executor executor, int maxEvents,
                   SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler,
                   EventLoopTaskQueueFactory queueFactory) {
        super(parent, executor, false, newTaskQueue(queueFactory), newTaskQueue(queueFactory),
                rejectedExecutionHandler);
        selectStrategy = ObjectUtil.checkNotNull(strategy, "strategy");
=======
    private static AbstractEpollChannel cast(Channel channel) {
        if (channel instanceof AbstractEpollChannel) {
            return (AbstractEpollChannel) channel;
        }
        throw new IllegalArgumentException("Channel of type " + StringUtil.simpleClassName(channel) + " not supported");
    }

    private EpollHandler() {
        this(0, DefaultSelectStrategyFactory.INSTANCE.newSelectStrategy());
    }

    // Package-private for tests.
    EpollHandler(int maxEvents, SelectStrategy strategy) {
        selectStrategy = strategy;
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
        if (maxEvents == 0) {
            allowGrowing = true;
            events = new EpollEventArray(4096);
        } else {
            allowGrowing = false;
            events = new EpollEventArray(maxEvents);
        }
        boolean success = false;
        FileDescriptor epollFd = null;
        FileDescriptor eventFd = null;
        FileDescriptor timerFd = null;
        try {
            this.epollFd = epollFd = Native.newEpollCreate();
            this.eventFd = eventFd = Native.newEventFd();
            try {
                // It is important to use EPOLLET here as we only want to get the notification once per
                // wakeup and don't call eventfd_read(...).
                Native.epollCtlAdd(epollFd.intValue(), eventFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to add eventFd filedescriptor to epoll", e);
            }
            this.timerFd = timerFd = Native.newTimerFd();
            try {
                // It is important to use EPOLLET here as we only want to get the notification once per
                // wakeup and don't call read(...).
                Native.epollCtlAdd(epollFd.intValue(), timerFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to add timerFd filedescriptor to epoll", e);
            }
            success = true;
        } finally {
            if (!success) {
                if (epollFd != null) {
                    try {
                        epollFd.close();
                    } catch (Exception e) {
                        // ignore
                    }
                }
                if (eventFd != null) {
                    try {
                        eventFd.close();
                    } catch (Exception e) {
                        // ignore
                    }
                }
                if (timerFd != null) {
                    try {
                        timerFd.close();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
    }

    private static Queue<Runnable> newTaskQueue(
            EventLoopTaskQueueFactory queueFactory) {
        if (queueFactory == null) {
            return newTaskQueue0(DEFAULT_MAX_PENDING_TASKS);
        }
        return queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
    }

    /**
     * Returns a new {@link IoHandlerFactory} that creates {@link EpollHandler} instances.
     */
    public static IoHandlerFactory newFactory() {
        return EpollHandler::new;
    }

    /**
     * Returns a new {@link IoHandlerFactory} that creates {@link EpollHandler} instances.
     */
    public static IoHandlerFactory newFactory(final int maxEvents,
                                              final SelectStrategyFactory selectStrategyFactory) {
        checkPositiveOrZero(maxEvents, "maxEvents");
        requireNonNull(selectStrategyFactory, "selectStrategyFactory");
        return () -> new EpollHandler(maxEvents, selectStrategyFactory.newSelectStrategy());
    }

    private IovArray cleanIovArray() {
        if (iovArray == null) {
            iovArray = new IovArray();
        } else {
            iovArray.clear();
        }
        return iovArray;
    }

    private NativeDatagramPacketArray cleanDatagramPacketArray() {
        if (datagramPacketArray == null) {
            datagramPacketArray = new NativeDatagramPacketArray();
        } else {
            datagramPacketArray.clear();
        }
        return datagramPacketArray;
    }

    @Override
<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop && nextWakeupNanos.getAndSet(AWAKE) != AWAKE) {
=======
    public final void register(Channel channel) throws Exception {
        final AbstractEpollChannel epollChannel = cast(channel);
        epollChannel.register0(new EpollRegistration() {
            @Override
            public void update() throws IOException {
                EpollHandler.this.modify(epollChannel);
            }

            @Override
            public void remove() throws IOException {
                EpollHandler.this.remove(epollChannel);
            }

            @Override
            public IovArray cleanIovArray() {
                return EpollHandler.this.cleanIovArray();
            }

            @Override
            public NativeDatagramPacketArray cleanDatagramPacketArray() {
                return EpollHandler.this.cleanDatagramPacketArray();
            }
        });
        add(epollChannel);
    }

    @Override
    public final void deregister(Channel channel) throws Exception {
        cast(channel).deregister0();
    }

    @Override
    public final void wakeup(boolean inEventLoop) {
        if (!inEventLoop && wakenUp.getAndSet(1) == 0) {
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
            // write to the evfd which will then wake-up epoll_wait(...)
            Native.eventFdWrite(eventFd.intValue(), 1L);
        }
    }

    @Override
    protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
        // Note this is also correct for the nextWakeupNanos == -1 (AWAKE) case
        return deadlineNanos < nextWakeupNanos.get();
    }

    @Override
    protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
        // Note this is also correct for the nextWakeupNanos == -1 (AWAKE) case
        return deadlineNanos < nextWakeupNanos.get();
    }

    /**
     * Register the given channel with this {@link EpollHandler}.
     */
    private void add(AbstractEpollChannel ch) throws IOException {
        int fd = ch.socket.intValue();
        Native.epollCtlAdd(epollFd.intValue(), fd, ch.flags);
        AbstractEpollChannel old = channels.put(fd, ch);

        // We either expect to have no Channel in the map with the same FD or that the FD of the old Channel is already
        // closed.
        assert old == null || !old.isOpen();
    }

    /**
     * The flags of the given epoll was modified so update the registration
     */
    private void modify(AbstractEpollChannel ch) throws IOException {
        Native.epollCtlMod(epollFd.intValue(), ch.socket.intValue(), ch.flags);
    }

    /**
     * Deregister the given channel from this {@link EpollHandler}.
     */
<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
    void remove(AbstractEpollChannel ch) throws IOException {
        assert inEventLoop();
        int fd = ch.socket.intValue();

        AbstractEpollChannel old = channels.remove(fd);
        if (old != null && old != ch) {
            // The Channel mapping was already replaced due FD reuse, put back the stored Channel.
            channels.put(fd, old);

            // If we found another Channel in the map that is mapped to the same FD the given Channel MUST be closed.
            assert !ch.isOpen();
        } else if (ch.isOpen()) {
            // Remove the epoll. This is only needed if it's still open as otherwise it will be automatically
            // removed once the file-descriptor is closed.
            Native.epollCtlDel(epollFd.intValue(), fd);
        }
    }

    @Override
    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return newTaskQueue0(maxPendingTasks);
    }

    private static Queue<Runnable> newTaskQueue0(int maxPendingTasks) {
        // This event loop never calls takeTask()
        return maxPendingTasks == Integer.MAX_VALUE ? PlatformDependent.<Runnable>newMpscQueue()
                : PlatformDependent.<Runnable>newMpscQueue(maxPendingTasks);
    }

    /**
     * Returns the percentage of the desired amount of time spent for I/O in the event loop.
     */
    public int getIoRatio() {
        return ioRatio;
    }
=======
    private void remove(AbstractEpollChannel ch) throws IOException {
        int fd = ch.socket.intValue();
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java

        AbstractEpollChannel old = channels.remove(fd);
        if (old != null && old != ch) {
            // The Channel mapping was already replaced due FD reuse, put back the stored Channel.
            channels.put(fd, old);

            // If we found another Channel in the map that is mapped to the same FD the given Channel MUST be closed.
            assert !ch.isOpen();
        } else if (ch.isOpen()) {
            // Remove the epoll. This is only needed if it's still open as otherwise it will be automatically
            // removed once the file-descriptor is closed.
            Native.epollCtlDel(epollFd.intValue(), fd);
        }
    }

<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
    @Override
    public int registeredChannels() {
        return channels.size();
    }

    private int epollWait(long deadlineNanos) throws IOException {
        if (deadlineNanos == NONE) {
            return Native.epollWait(epollFd, events, timerFd, Integer.MAX_VALUE, 0); // disarm timer
=======
    private int epollWait(IoExecutionContext context) throws IOException {
        int delaySeconds;
        int delayNanos;
        long curDeadlineNanos = context.deadlineNanos();
        if (curDeadlineNanos == prevDeadlineNanos) {
            delaySeconds = -1;
            delayNanos = -1;
        } else {
            long totalDelay = context.delayNanos(System.nanoTime());
            prevDeadlineNanos = curDeadlineNanos;
            delaySeconds = (int) min(totalDelay / 1000000000L, Integer.MAX_VALUE);
            delayNanos = (int) min(totalDelay - delaySeconds * 1000000000L, MAX_SCHEDULED_TIMERFD_NS);
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
        }
        long totalDelay = deadlineToDelayNanos(deadlineNanos);
        int delaySeconds = (int) min(totalDelay / 1000000000L, Integer.MAX_VALUE);
        int delayNanos = (int) min(totalDelay - delaySeconds * 1000000000L, MAX_SCHEDULED_TIMERFD_NS);
        return Native.epollWait(epollFd, events, timerFd, delaySeconds, delayNanos);
    }

    private int epollWaitNoTimerChange() throws IOException {
        return Native.epollWait(epollFd, events, false);
    }

    private int epollWaitNow() throws IOException {
        return Native.epollWait(epollFd, events, true);
    }

    private int epollBusyWait() throws IOException {
        return Native.epollBusyWait(epollFd, events);
    }

    private int epollWaitTimeboxed() throws IOException {
        // Wait with 1 second "safeguard" timeout
        return Native.epollWait(epollFd, events, 1000);
    }
<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java

    @Override
    protected void run() {
        long prevDeadlineNanos = NONE;
        for (;;) {
            try {
                int strategy = selectStrategy.calculateStrategy(selectNowSupplier, hasTasks());
                switch (strategy) {
                    case SelectStrategy.CONTINUE:
                        continue;

                    case SelectStrategy.BUSY_WAIT:
                        strategy = epollBusyWait();
                        break;

                    case SelectStrategy.SELECT:
                        if (pendingWakeup) {
                            // We are going to be immediately woken so no need to reset wakenUp
                            // or check for timerfd adjustment.
                            strategy = epollWaitTimeboxed();
                            if (strategy != 0) {
                                break;
                            }
                            // We timed out so assume that we missed the write event due to an
                            // abnormally failed syscall (the write itself or a prior epoll_wait)
                            logger.warn("Missed eventfd write (not seen after > 1 second)");
                            pendingWakeup = false;
                            if (hasTasks()) {
                                break;
                            }
                            // fall-through
                        }

                        long curDeadlineNanos = nextScheduledTaskDeadlineNanos();
                        if (curDeadlineNanos == -1L) {
                            curDeadlineNanos = NONE; // nothing on the calendar
                        }
                        nextWakeupNanos.set(curDeadlineNanos);
                        try {
                            if (!hasTasks()) {
                                if (curDeadlineNanos == prevDeadlineNanos) {
                                    // No timer activity needed
                                    strategy = epollWaitNoTimerChange();
                                } else {
                                    // Timerfd needs to be re-armed or disarmed
                                    prevDeadlineNanos = curDeadlineNanos;
                                    strategy = epollWait(curDeadlineNanos);
                                }
                            }
                        } finally {
                            // Try get() first to avoid much more expensive CAS in the case we
                            // were woken via the wakeup() method (submitted task)
                            if (nextWakeupNanos.get() == AWAKE || nextWakeupNanos.getAndSet(AWAKE) == AWAKE) {
                                pendingWakeup = true;
                            }
                        }
                        // fallthrough
                    default:
                }

                final int ioRatio = this.ioRatio;
                if (ioRatio == 100) {
                    try {
                        if (strategy > 0 && processReady(events, strategy)) {
                            prevDeadlineNanos = NONE;
=======

    @Override
    public final int run(IoExecutionContext context) {
        int handled = 0;
        try {
            int strategy = selectStrategy.calculateStrategy(selectNowSupplier, !context.canBlock());
            switch (strategy) {
                case SelectStrategy.CONTINUE:
                    return 0;

                case SelectStrategy.BUSY_WAIT:
                    strategy = epollBusyWait();
                    break;

                case SelectStrategy.SELECT:
                    if (pendingWakeup) {
                        // We are going to be immediately woken so no need to reset wakenUp
                        // or check for timerfd adjustment.
                        strategy = epollWaitTimeboxed();
                        if (strategy != 0) {
                            break;
                        }
                        // We timed out so assume that we missed the write event due to an
                        // abnormally failed syscall (the write itself or a prior epoll_wait)
                        logger.warn("Missed eventfd write (not seen after > 1 second)");
                        pendingWakeup = false;
                        if (!context.canBlock()) {
                            break;
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
                        }
                        // fall-through
                    }
<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
                } else if (strategy > 0) {
                    final long ioStartTime = System.nanoTime();
                    try {
                        if (processReady(events, strategy)) {
                            prevDeadlineNanos = NONE;
=======

                    wakenUp.set(0);
                    try {
                        if (context.canBlock()) {
                            strategy = epollWait(context);
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
                        }
                    } finally {
                        // Try get() first to avoid much more expensive CAS in the case we
                        // were woken via the wakeup() method (submitted task)
                        if (wakenUp.get() == 1 || wakenUp.getAndSet(1) == 1) {
                            pendingWakeup = true;
                        }
                    }
<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
                } else {
                    runAllTasks(0); // This will run the minimum number of tasks
                }
                if (allowGrowing && strategy == events.length()) {
                    //increase the size of the array as we needed the whole space for the events
                    events.increase();
                }
            } catch (Error e) {
                throw (Error) e;
            } catch (Throwable t) {
                handleLoopException(t);
            } finally {
                // Always handle shutdown even if the loop processing threw an exception.
                try {
                    if (isShuttingDown()) {
                        closeAll();
                        if (confirmShutdown()) {
                            break;
                        }
                    }
                } catch (Error e) {
                    throw (Error) e;
                } catch (Throwable t) {
                    handleLoopException(t);
                }
=======
                    // fall-through
                default:
            }
            if (strategy > 0) {
                handled = strategy;
                processReady(events, strategy);
            }
            if (allowGrowing && strategy == events.length()) {
                //increase the size of the array as we needed the whole space for the events
                events.increase();
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
            }
        } catch (Error error) {
            throw error;
        } catch (Throwable t) {
            handleLoopException(t);
        }
        return handled;
    }

    /**
     * Visible only for testing!
     */
    void handleLoopException(Throwable t) {
        logger.warn("Unexpected exception in the selector loop.", t);

        // Prevent possible consecutive immediate failures that lead to
        // excessive CPU consumption.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Ignore.
        }
    }

<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
    private void closeAll() {
=======
    @Override
    public void prepareToDestroy() {
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
        // Using the intermediate collection to prevent ConcurrentModificationException.
        // In the `close()` method, the channel is deleted from `channels` map.
        AbstractEpollChannel[] localChannels = channels.values().toArray(new AbstractEpollChannel[0]);

        for (AbstractEpollChannel ch: localChannels) {
            ch.unsafe().close(ch.unsafe().voidPromise());
        }
    }

    // Returns true if a timerFd event was encountered
    private boolean processReady(EpollEventArray events, int ready) {
        boolean timerFired = false;
        for (int i = 0; i < ready; i ++) {
            final int fd = events.fd(i);
            if (fd == eventFd.intValue()) {
                pendingWakeup = false;
            } else if (fd == timerFd.intValue()) {
<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java
                timerFired = true;
=======
                // Just ignore as we use ET mode for the eventfd and timerfd.
                //
                // See also https://stackoverflow.com/a/12492308/1074097
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
            } else {
                final long ev = events.events(i);

                AbstractEpollChannel ch = channels.get(fd);
                if (ch != null) {
                    // Don't change the ordering of processing EPOLLOUT | EPOLLRDHUP / EPOLLIN if you're not 100%
                    // sure about it!
                    // Re-ordering can easily introduce bugs and bad side-effects, as we found out painfully in the
                    // past.
                    AbstractEpollUnsafe unsafe = (AbstractEpollUnsafe) ch.unsafe();

                    // First check for EPOLLOUT as we may need to fail the connect ChannelPromise before try
                    // to read from the file descriptor.
                    // See https://github.com/netty/netty/issues/3785
                    //
                    // It is possible for an EPOLLOUT or EPOLLERR to be generated when a connection is refused.
                    // In either case epollOutReady() will do the correct thing (finish connecting, or fail
                    // the connection).
                    // See https://github.com/netty/netty/issues/3848
                    if ((ev & (Native.EPOLLERR | Native.EPOLLOUT)) != 0) {
                        // Force flush of data as the epoll is writable again
                        unsafe.epollOutReady();
                    }

                    // Check EPOLLIN before EPOLLRDHUP to ensure all data is read before shutting down the input.
                    // See https://github.com/netty/netty/issues/4317.
                    //
                    // If EPOLLIN or EPOLLERR was received and the channel is still open call epollInReady(). This will
                    // try to read from the underlying file descriptor and so notify the user about the error.
                    if ((ev & (Native.EPOLLERR | Native.EPOLLIN)) != 0) {
                        // The Channel is still open and there is something to read. Do it now.
                        unsafe.epollInReady();
                    }

                    // Check if EPOLLRDHUP was set, this will notify us for connection-reset in which case
                    // we may close the channel directly or try to read more data depending on the state of the
                    // Channel and als depending on the AbstractEpollChannel subtype.
                    if ((ev & Native.EPOLLRDHUP) != 0) {
                        unsafe.epollRdHupReady();
                    }
                } else {
                    // We received an event for an fd which we not use anymore. Remove it from the epoll_event set.
                    try {
                        Native.epollCtlDel(epollFd.intValue(), fd);
                    } catch (IOException ignore) {
                        // This can happen but is nothing we need to worry about as we only try to delete
                        // the fd from the epoll set as we not found it in our mappings. So this call to
                        // epollCtlDel(...) is just to ensure we cleanup stuff and so may fail if it was
                        // deleted before or the file descriptor was closed before.
                    }
                }
            }
        }
        return timerFired;
    }

    @Override
    public final void destroy() {
        try {
            // Ensure any in-flight wakeup writes have been performed prior to closing eventFd.
            while (pendingWakeup) {
                try {
                    int count = epollWaitTimeboxed();
                    if (count == 0) {
                        // We timed-out so assume that the write we're expecting isn't coming
                        break;
                    }
                    for (int i = 0; i < count; i++) {
                        if (events.fd(i) == eventFd.intValue()) {
                            pendingWakeup = false;
                            break;
                        }
                    }
                } catch (IOException ignore) {
                    // ignore
                }
            }
            try {
                eventFd.close();
            } catch (IOException e) {
                logger.warn("Failed to close the event fd.", e);
            }
            try {
                timerFd.close();
            } catch (IOException e) {
                logger.warn("Failed to close the timer fd.", e);
            }
<<<<<<< HEAD:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollEventLoop.java

=======
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:transport-native-epoll/src/main/java/io/netty/channel/epoll/EpollHandler.java
            try {
                epollFd.close();
            } catch (IOException e) {
                logger.warn("Failed to close the epoll fd.", e);
            }
        } finally {
            // release native memory
            if (iovArray != null) {
                iovArray.release();
                iovArray = null;
            }
            if (datagramPacketArray != null) {
                datagramPacketArray.release();
                datagramPacketArray = null;
            }
            events.free();
        }
    }
}
