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
import com.exonum.binding.core.service.Schema;
import com.exonum.binding.core.storage.database.View;
import com.exonum.binding.core.storage.indices.ListIndex;
import com.exonum.binding.core.storage.indices.ListIndexProxy;
import com.exonum.binding.core.storage.indices.MapIndex;
import com.exonum.binding.core.storage.indices.MapIndexProxy;
import com.exonum.examples.cryptoowls.model.ModelProtos.*;

import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@code MySchema} provides access to the tables of {@link CryptoowlsService},
 * given a database state: a {@link View}.
 *
 * @see <a href="https://exonum.com/doc/version/0.12/architecture/storage/#table-types">Exonum table types.</a>
 */
public final class CryptoowlsSchema implements Schema {

  private final View view;

  public CryptoowlsSchema(View view) {
    this.view = checkNotNull(view);
  }

  @Override
  public List<HashCode> getStateHashes() {
    return Collections.emptyList();
  }

  public ListIndex<CryptoOwl> getOwls() {
    String indexName = fullIndexName("owls");
    return ListIndexProxy.newInstance(indexName, view,
        StandardSerializers.protobuf(CryptoOwl.class));
  }

  public MapIndex<PublicKey, User> getUsers() {
    String indexName = fullIndexName("users");
    return MapIndexProxy.newInstance(indexName, view,
        StandardSerializers.publicKey(), StandardSerializers.protobuf(User.class));
  }

  public ListIndex<Auction> getAuctions() {
    String indexName = fullIndexName("auctions");
    return ListIndexProxy.newInstance(indexName, view,
        StandardSerializers.protobuf(Auction.class));
  }

  private String fullIndexName(String name) {
    return CryptoowlsService.NAME.replace('-', '_') + "_" + name;
  }
}
