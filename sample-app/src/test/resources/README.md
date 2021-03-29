基本的には `typesafe config` のフォールバック機能を活用して、  
`main/resources/application.conf` や `main/resources/reference.conf` から読み込む。

テスト全般で使用する項目は、`test/resources/application.conf` で上書き/追加する。  
特別なテストの設定を必要とする場合は、`test/resources/test-***.conf`のように設定ファイルを特別に準備する。  
ただし、設定ミスをなくすため `test/resources/application.conf` からの設定は必ず読み込むこと。
