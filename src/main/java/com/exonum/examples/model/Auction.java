package com.exonum.examples.model;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.hash.HashCode;
import com.exonum.examples.Helpers;
import com.exonum.examples.cryptoowls.model.ModelProtos;

import java.time.Duration;
import java.time.ZonedDateTime;

public class Auction {
  private PublicKey owner;
  private HashCode owlHash;
  private long startPrice;
  // In seconds
  private long duration;
  private ZonedDateTime startedAt;
  private HashCode bidsIndexHash;
  private boolean closed;

  public Auction(ModelProtos.Auction proto) {
    this.owner = Helpers.publicKeyFromProtobuf(proto.getPublicKey());
    this.owlHash = Helpers.hashFromProtobuf(proto.getOwlId());
    this.startPrice = proto.getStartPrice();
    this.duration = proto.getDuration();
    this.startedAt = Helpers.timestampToZdt(proto.getStartedAt());
    this.bidsIndexHash = Helpers.hashFromProtobuf(proto.getBiddingMerkleRoot());
    this.closed = proto.getClosed();
  }

  public Auction(PublicKey owner, HashCode owlHash, long startPrice, long duration, ZonedDateTime startedAt, HashCode bidsIndexHash, boolean closed) {
    this.owner = owner;
    this.owlHash = owlHash;
    this.startPrice = startPrice;
    this.duration = duration;
    this.startedAt = startedAt;
    this.bidsIndexHash = bidsIndexHash;
    this.closed = closed;
  }

  public ModelProtos.Auction toProtobuf() {
    return ModelProtos.Auction.newBuilder()
        .setPublicKey(Helpers.publicKeyToProtobuf(owner))
        .setOwlId(Helpers.hashToProtobuf(owlHash))
        .setStartPrice(startPrice)
        .setDuration(duration)
        .setStartedAt(Helpers.zdtToTimestamp(startedAt))
        .setBiddingMerkleRoot(Helpers.hashToProtobuf(bidsIndexHash))
        .setClosed(closed)
        .build();
  }

  public void setBidsIndexHash(HashCode bidsIndexHash) {
    this.bidsIndexHash = bidsIndexHash;
  }

  public ZonedDateTime endsAt() {
    return startedAt.plus(Duration.ofSeconds(duration));
  }

  public boolean isClosed() {
    return closed;
  }

  public PublicKey getOwner() {
    return owner;
  }

  public long getStartPrice() {
    return startPrice;
  }

  public HashCode getOwlHash() {
    return owlHash;
  }

  public void close() {
    closed = true;
  }
}
