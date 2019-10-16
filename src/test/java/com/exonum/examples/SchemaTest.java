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
