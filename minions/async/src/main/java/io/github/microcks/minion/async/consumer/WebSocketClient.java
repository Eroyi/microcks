/*
 * Licensed to Laurent Broudoux (the "Author") under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Author licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.github.microcks.minion.async.consumer;

import javax.websocket.ClientEndpoint;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ClientEndpoint
/**
 * A simple WebSocket client that stores messages as <code>ConsumedMessage</code>s. It supports
 * both the {@class javax.websocket.ClientEndpoint} annotation and the generic {@class javax.websocket.Endpoint}
 * interface, registering itself as a new message handler.
 * @author laurent
 */
public class WebSocketClient extends Endpoint {

   private List<ConsumedMessage> messages = new ArrayList<>();

   /**
    * Create a new WebSocket Client endpoint.
    */
   public WebSocketClient() {
   }

   @Override
   public void onOpen(Session session, EndpointConfig endpointConfig) {
      session.addMessageHandler(new MessageHandler.Whole<String>() {
         public void onMessage(String messagePayload) {
            storeMessage(messagePayload);
         }
      });
   }

   @OnOpen
   public void onOpen(Session session) {
      // Nothing to do on session opening.
   }

   @OnMessage
   public void onMessage(String messagePayload) {
      storeMessage(messagePayload);
   }

   /**
    * Get the list of consumed messages.
    * @return The consumed message during client connection time.
    */
   public List<ConsumedMessage> getMessages() {
      return messages;
   }

   /** Store after having transformed into a ConsumedMessage. */
   protected void storeMessage(String messagePayload) {
      // Build a ConsumedMessage from Kafka record.
      ConsumedMessage message = new ConsumedMessage();
      message.setReceivedAt(System.currentTimeMillis());
      message.setPayload(messagePayload.getBytes(StandardCharsets.UTF_8));
      messages.add(message);
   }
}