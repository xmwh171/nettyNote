/*
 * Copyright 2019 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
<<<<<<< HEAD:transport-udt/src/main/java/io/netty/channel/udt/UdtServerChannel.java
 * https://www.apache.org/licenses/LICENSE-2.0
=======
 *   https://www.apache.org/licenses/LICENSE-2.0
>>>>>>> 8eb8ad8e9d716530c51f0558a41625bb483333f7:testsuite/src/main/java/io/netty/testsuite/transport/LocalSingleThreadEventLoopTest.java
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.testsuite.transport;

import io.netty.channel.IoHandlerFactory;
import io.netty.channel.ServerChannel;
import io.netty.channel.local.LocalHandler;
import io.netty.channel.local.LocalServerChannel;

public class LocalSingleThreadEventLoopTest extends AbstractSingleThreadEventLoopTest {
    @Override
    protected IoHandlerFactory newIoHandlerFactory() {
        return LocalHandler.newFactory();
    }

    @Override
    protected Class<? extends ServerChannel> serverChannelClass() {
        return LocalServerChannel.class;
    }
}
