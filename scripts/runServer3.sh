#!/usr/bin/env bash

# クラスタノード 3台目
sbt \
-DHOST=127.0.0.1 \
-DPORT=25523 \
-DAPP_HOST=127.0.0.1 \
-DAPP_PORT=9003 \
"SampleApp/run"
