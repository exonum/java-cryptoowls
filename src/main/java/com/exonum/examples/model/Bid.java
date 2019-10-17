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

package com.exonum.examples.model;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.examples.Helpers;
import com.exonum.examples.cryptoowls.model.ModelProtos;

public class Bid {
  private PublicKey bidder;
  private long value;

  public Bid(PublicKey bidder, long value) {
    this.bidder = bidder;
    this.value = value;
  }

  public Bid(ModelProtos.Bid proto) {
    this.bidder = Helpers.publicKeyFromProtobuf(proto.getPublicKey());
    this.value = proto.getValue();
  }

  public ModelProtos.Bid toProtobuf() {
    return ModelProtos.Bid.newBuilder()
        .setPublicKey(Helpers.publicKeyToProtobuf(bidder))
        .setValue(value)
        .build();
  }

  public PublicKey getBidder() {
    return bidder;
  }

  public long getValue() {
    return value;
  }
}
