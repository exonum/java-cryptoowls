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
}
