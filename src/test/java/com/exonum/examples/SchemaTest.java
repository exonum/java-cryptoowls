/*
 * Copyright 2019 The Exonum Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.exonum.examples;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.hash.HashCode;
import com.exonum.binding.core.storage.indices.ListIndex;
import com.exonum.binding.core.storage.indices.MapIndex;
import com.exonum.examples.cryptoowls.model.ModelProtos;
import com.exonum.examples.cryptoowls.model.ModelProtos.*;
import com.exonum.binding.testkit.TestKit;
import com.exonum.binding.testkit.TestKitExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class SchemaTest {
  @RegisterExtension
  TestKitExtension testKitExtension = new TestKitExtension(
      TestKit.builder().withService(ServiceModule.class));

  @Test
  void returnsEmptyOwlsListOnCreation(TestKit testKit) {
    Schema schema = new Schema(testKit.getSnapshot());
    MapIndex<HashCode, ModelProtos.CryptoOwl> owls = schema.getOwls();
    assert(owls.isEmpty());
  }

  @Test
  void returnsEmptyUsersMapOnCreation(TestKit testKit) {
    Schema schema = new Schema(testKit.getSnapshot());
    MapIndex<PublicKey, ModelProtos.User> users = schema.getUsers();
    assert(users.isEmpty());
  }

  @Test
  void returnsEmptyAuctionsListOnCreation(TestKit testKit) {
    Schema schema = new Schema(testKit.getSnapshot());
    ListIndex<Auction> auctions = schema.getAuctions();
    assert(auctions.isEmpty());
  }
}
