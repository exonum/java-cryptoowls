#!/usr/bin/env bash

export RUST_LOG="${RUST_LOG-warn,exonum=info,exonum-java=info,java_bindings=info,jni=error}"

exonum-java generate-template \
    --validators-count=1 \
    testnet/common.toml

exonum-java generate-config \
    testnet/common.toml \
    testnet \
    --no-password \
    --peer-address 127.0.0.1:5400

exonum-java finalize \
    testnet/sec.toml \
    testnet/node.toml \
    --public-configs testnet/pub.toml

exonum-java run \
    --node-config testnet/node.toml \
    --db-path testnet/db \
    --consensus-key-pass pass \
    --service-key-pass pass \
    --public-api-address 127.0.0.1:3000 \
    --ejb-port 7000 \
