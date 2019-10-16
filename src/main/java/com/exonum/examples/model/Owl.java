package com.exonum.examples.model;

import com.exonum.binding.common.crypto.PublicKey;
import com.exonum.binding.common.hash.HashCode;
import com.exonum.binding.common.hash.Hashing;
import com.exonum.examples.Helpers;
import com.exonum.examples.cryptoowls.model.ModelProtos;
import com.google.common.primitives.Ints;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

public class Owl {
  private static final int BREEDING_TIMEOUT = 30;
  private final String name;
  private final PublicKey owner;
  private final int DNA;
  private ZonedDateTime lastBreeding;

  public Owl(String name, PublicKey owner, HashCode uniqueHash, ZonedDateTime lastBreeding) {
    Random random = new Random(uniqueHash.asInt());

    int fatherDNA = random.nextInt();
    int motherDNA = random.nextInt();
    int childDNA = generateChildDNA(fatherDNA, motherDNA, uniqueHash);

    this.name = name;
    this.DNA = childDNA;
    this.owner = owner;
    this.lastBreeding = lastBreeding;
  }

  public Owl(String name, PublicKey owner, int fatherDNA, int motherDNA, HashCode uniqueHash, ZonedDateTime lastBreeding) {
    int childDNA = generateChildDNA(fatherDNA, motherDNA, uniqueHash);

    this.name = name;
    this.DNA = childDNA;
    this.owner = owner;
    this.lastBreeding = lastBreeding;
  }

  private int generateChildDNA(int fatherDNA, int motherDNA, HashCode uniqueHash) {
    Random random = new Random(uniqueHash.asInt());

    BitSet fatherDNABits = BitSet.valueOf(Ints.toByteArray(fatherDNA));
    BitSet motherDNABits = BitSet.valueOf(Ints.toByteArray(motherDNA));
    BitSet childDNABits = new BitSet(32);

    for (int i = 0; i < fatherDNABits.size(); i++) {
      boolean fatherBit = fatherDNABits.get(i);
      boolean motherBit = motherDNABits.get(i);
      if (fatherBit == motherBit) {
        if (random.nextFloat() <= 0.8) {
          childDNABits.set(i, fatherBit);
        } else {
          childDNABits.set(i, !fatherBit);
        }
      } else {
        childDNABits.set(i, random.nextFloat() > 0.5);
      }
    }
    return Ints.fromByteArray(Arrays.copyOf(childDNABits.toByteArray(), 4));
  }

  public Owl(ModelProtos.CryptoOwl protobufOwl) {
    this.name = protobufOwl.getName();
    this.DNA = protobufOwl.getDna();
    this.owner = Helpers.publicKeyFromProtobuf(protobufOwl.getOwner());
    this.lastBreeding = Helpers.timestampToZdt(protobufOwl.getLastBreeding());
  }

  public int getDNA() {
    return DNA;
  }

  public PublicKey getOwner() {
    return owner;
  }

  public ModelProtos.CryptoOwl toProtobuf() {
    return ModelProtos.CryptoOwl.newBuilder()
        .setName(name)
        .setDna(DNA)
        .setOwner(Helpers.publicKeyToProtobuf(owner))
        .build();
  }

  public HashCode getHash() {
    return Hashing.defaultHashFunction().newHasher()
        .putUnencodedChars(name)
        .putInt(DNA)
        .hash();
  }

  public boolean isBreedingPossible(ZonedDateTime currentTime) {
    return (currentTime.toEpochSecond() - lastBreeding.toEpochSecond()) > BREEDING_TIMEOUT;
  }
}