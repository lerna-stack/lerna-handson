# Lerna ハンズオン

## 概要

Lerna ハンズオン (*lerna-handson*) に必要なサンプルアプリや演習問題が含まれています。  
ハンズオン資料は次の2つです。
- [Lernaハンズオン事前準備](docs/hands-on-preparation.pdf)
- [Lernaハンズオン](docs/hands-on.pdf)

## クイックスタート

サンプルアプリを動かしてみましょう。  
サンプルアプリは REST API を提供するサーバです。  
コンサートのチケット購入サービス を想定したサンプルとなります。

ターミナル1
```sh
cd docker
docker-compose up
```

ターミナル2
```sh
./scripts/runServer1.sh
```

ターミナル3
```sh
# コンサート作成 (コンサート名=RHCP, チケット枚数=3)
curl --silent --noproxy '*' -X POST -H "Content-Type: application/json" -d '{"tickets":3}' localhost:9001/concerts/RHCP
# コンサート取得 (コンサート名=RHCP)
curl --silent --noproxy '*' localhost:9001/concerts/RHCP
# コンサートチケット購入 (コンサート名=RHCP, チケット購入枚数=1)
curl --silent --noproxy '*' -X POST -H "Content-Type: application/json" -d '{"tickets":1}' localhost:9001/concerts/RHCP/tickets
# コンサートキャンセル (コンサート名=RHCP)
curl --silent --noproxy '*' -X POST localhost:9001/concerts/RHCP/cancel
# コンサート一覧  
# 一覧に反映されるまでは遅延時間があります。
curl --silent --noproxy '*' localhost:9001/concerts
```

## フォルダ構成

- docker  
サンプルアプリの動作に必要な Cassandra と MariaDB の Dockerfileがあります。
- exercise-accord-basic  
Accord のサンプルコードがあります。
- exercise-akka-basic  
Akka のサンプルコードがあります。  
演習問題も含まれています。
- exercise-akka-persistence-basic  
Akka Persistence のサンプルコードがあります。  
演習問題も含まれています。
- exercise-akka-http-basic  
Akka HTTP のサンプルコードがあります。  
演習問題も含まれています。
- exercise-scala-basic  
Scala のサンプルコードがあります。  
演習問題も含まれています。
- exercise-slick-basic  
Slick のサンプルコードがあります。
- sample-app  
サンプルアプリです。
- scripts  
サンプルアプリを起動するためのスクリプトです。

## exercise-*** について

技術トピックごとにディレクトリがあり、  
ハンズオン説明資料で使用したサンプルコードが含まれています。  
また、演習問題が含まれているものもあります。  
各ディレクトリには、example, exercise, answer というパッケージがあります。  
- example  
サンプルコード(スライドなどに載せているものになります)
- exercise  
演習問題に必要なファイルがあります。
- answer  
演習問題の答えがあります。

## サンプルアプリ

サンプルアプリは REST API を提供するサーバです。  
コンサートのチケット購入サービス を想定したサンプルとなります。

### 実行方法

まず、次の2つのサービスを起動します。
- Cassandra
- MariaDB

```sh
cd docker
docker-compose up
# (停止するには Ctrl+C)
```

サンプルアプリ(APIサーバ)を起動します。
クラスタで起動できます。
最低1台のノードを起動する必要があります。
```sh
./scripts/runServer1.sh
# 次の２つはオプション
./scripts/runServer2.sh
./scripts/runServer3.sh
```

### テストの実行方法

演習向けテストを除外してテストする方法を準備しています。  
```sh
sbt testAll
```

演習ごとに特別なテスト用sbtコマンドを準備しています。
```sh
sbt testMyConcertActorBinding
sbt testMyConcertActor
sbt testMyBoxOfficeResourceBinding
sbt testMyBoxOfficeResource
sbt testMyConcertProjectionRepositoryBinding
sbt testMyConcertProjectionRepository
```

### API リクエスト例

ポート 9001 の HTTPサーバ (runServer1.shで起動する) にリクエストしています。  
(runServer2.shやrunServer3.shで複数台起動している場合は)、  
9002,9003 のサーバにリクエストしても問題ありません。  

```sh
# コンサート作成 (コンサート名=RHCP, チケット枚数=3)
curl --silent --noproxy '*' -X POST -H "Content-Type: application/json" -d '{"tickets":3}' localhost:9001/concerts/RHCP

# コンサート取得 (コンサート名=RHCP)
curl --silent --noproxy '*' localhost:9001/concerts/RHCP

# コンサートチケット購入 (コンサート名=RHCP, チケット購入枚数=1)
curl --silent --noproxy '*' -X POST -H "Content-Type: application/json" -d '{"tickets":1}' localhost:9001/concerts/RHCP/tickets

# コンサートキャンセル (コンサート名=RHCP)
curl --silent --noproxy '*' -X POST localhost:9001/concerts/RHCP/cancel

# コンサート一覧  
# 一覧に反映されるまでは遅延時間があります。
curl --silent --noproxy '*' localhost:9001/concerts
```

### データの削除方法
次の２つのサービスのデータを削除します。
- Cassandra
- MariaDB

```sh
docker-compose down --volumes
```

### パッケージ構成

パッケージは次の通りです。  
[プロジェクト構成詳解](https://github.com/lerna-stack/lerna-sample-payment-app/blob/v1.0.0/docs/プロジェクト構成詳解.md) と同様の構成になっています。

- `example.adapter`  
  Application の インターフェース (trait) を定義します。
- `example.application`  
  業務ロジックを記述します。
  - `example.application.command`  
    書き込みとインメモリからの読み込みを実装します。  
  - `example.application.projection`  
    リードモデル更新を実装します。
  - `example.application.query`  
    リードモデルからの読み込みを実装します。
- `example.entrypoint`  
  Main クラスを実装します。
- `example.presentation`  
  HTTP API を実装します。
- `exaple.readmodel`  
  RDBMS にアクセスするコードを配置します。


### テストに失敗する場合には?

コンピュータのスペックによってはタイムアウト値が短すぎてテストに失敗する場合があります。
[sample-app/src/test/resources/application.conf](sample-app/src/test/resources/application.conf)
にある次の設定値をすることで、タイムアウト値を変更することができます。

```config
akka.test.default-timeout = 5s
akka.test.timefactor = 1.0
```

### 永続化されたデータ確認方法
- Cassandra に永続化されたデータを確認する方法は、[Cassandra の基本操作](docs/cassandra-ops.md) に記載されています。
- MariaDB に永続化されたデータを確認する方法は、[MariaDB の基本操作](docs/mariadb-ops.md) に記載されています。

## 変更履歴
*lerna-handson* に関する注目すべき変更は、[CHANGELOG.md](CHANGELOG.md) で確認できます。

## License
*lerna-handson*, except for files in the [docs](docs) directory, is released under the terms of the [Apache License Version 2.0](LICENSE).
The files in the [docs](docs) is released under the [Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)](https://creativecommons.org/licenses/by-sa/4.0/).

© 2021 TIS Inc.
