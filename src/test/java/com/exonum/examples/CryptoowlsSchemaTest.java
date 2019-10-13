package com.exonum.examples;

import com.exonum.binding.core.storage.indices.ListIndex;
import com.exonum.examples.cryptoowls.model.ModelProtos.*;
import com.exonum.binding.testkit.TestKit;
import com.exonum.binding.testkit.TestKitExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class CryptoowlsSchemaTest {
  @RegisterExtension
  TestKitExtension testKitExtension = new TestKitExtension(
      TestKit.builder().withService(ServiceModule.class));

  @Test
  void returnsEmptyOwlsListOnCreation(TestKit testKit) {
    CryptoowlsSchema schema = new CryptoowlsSchema(testKit.getSnapshot());
    ListIndex<CryptoOwl> owls = schema.getOwls();
    assert(owls.isEmpty());
  }

  @Test
  void returnsEmptyUsersListOnCreation(TestKit testKit) {
    CryptoowlsSchema schema = new CryptoowlsSchema(testKit.getSnapshot());
    ListIndex<User> users = schema.getUsers();
    assert(users.isEmpty());
  }

  @Test
  void returnsEmptyAuctionsListOnCreation(TestKit testKit) {
    CryptoowlsSchema schema = new CryptoowlsSchema(testKit.getSnapshot());
    ListIndex<Auction> auctions = schema.getAuctions();
    assert(auctions.isEmpty());
  }
}
