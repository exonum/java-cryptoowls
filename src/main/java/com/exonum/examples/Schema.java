/*
 * Copyright 2018 The Exonum Team
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
import com.exonum.binding.common.serialization.StandardSerializers;
import com.exonum.binding.core.storage.database.View;
import com.exonum.binding.core.storage.indices.*;
import com.exonum.examples.cryptoowls.model.ModelProtos;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@code MySchema} provides access to the tables of {@link Service},
 * given a database state: a {@link View}.
 *
 * @see <a href="https://exonum.com/doc/version/0.12/architecture/storage/#table-types">Exonum table types.</a>
 */
public final class Schema implements com.exonum.binding.core.service.Schema {

  private final View view;

  public Schema(View view) {
    this.view = checkNotNull(view);
  }

  @Override
  public List<HashCode> getStateHashes() {
    return ImmutableList.of(getOwls().getIndexHash());
  }

  public ProofMapIndexProxy<HashCode, ModelProtos.CryptoOwl> getOwls() {
    String indexName = fullIndexName("owls");
    return ProofMapIndexProxy.newInstance(indexName, view,
        StandardSerializers.hash(),
        StandardSerializers.protobuf(ModelProtos.CryptoOwl.class));
  }

  public ValueSetIndexProxy<HashCode> getUserOwls(PublicKey user) {
    String indexName = fullIndexName("userOwls");
    return ValueSetIndexProxy.newInGroupUnsafe(indexName, user.toBytes(),
        view, StandardSerializers.hash());
  }

  public MapIndex<PublicKey, ModelProtos.User> getUsers() {
    String indexName = fullIndexName("users");
    return MapIndexProxy.newInstance(indexName, view,
        StandardSerializers.publicKey(), StandardSerializers.protobuf(ModelProtos.User.class));
  }

  public ProofListIndexProxy<ModelProtos.Auction> getAuctions() {
    String indexName = fullIndexName("auctions");
    return ProofListIndexProxy.newInstance(indexName, view,
        StandardSerializers.protobuf(ModelProtos.Auction.class));
  }

  public ProofListIndexProxy<ModelProtos.Bid> getAuctionBids(int auctionId) {
    String indexName = fullIndexName("auctionBids");
    return ProofListIndexProxy.newInGroupUnsafe(indexName, Ints.toByteArray(auctionId), view,
        StandardSerializers.protobuf(ModelProtos.Bid.class));
  }

  public MapIndex<HashCode, Integer> getOwlAuctions() {
    String indexName = fullIndexName("owlAuctions");
    return MapIndexProxy.newInstance(indexName, view,
        StandardSerializers.hash(), StandardSerializers.fixed32());
  }

  private String fullIndexName(String name) {
    return Service.NAME.replace('-', '_') + "_" + name;
  }
}
